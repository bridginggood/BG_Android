/**
 * Created By: Junsung Lim
 * 
 * Description:
 * 	Starting point of the BridgingGood application.
 * 	
 * 	Checks whether saved user token exists or not. If not, directs user to the LoginController.
 * 	Otherwise, attempts login and directs user to MainController on success.
 */
package com.bridginggood;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bridginggood.Facebook.BaseRequestListener;
import com.bridginggood.Facebook.FacebookSessionStore;

public class SplashController extends Activity {
	protected final static long SPLASH_DELAY = 1500;	//Minimum time to show Splash in milliseconds
	private boolean mLockThread = false;				//Created to put a lock on asynchronous thread

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_layout);
		
		UserInfo.init();	//Initialize UserInfo

		Thread splashTread = new Thread() {
			@Override
			public void run() {
				long startTime = android.os.SystemClock.uptimeMillis();

				/*
				 *===============START Loading Job==================== 
				 */
				
				//Check if saved user token exists or not
				boolean isLoginSuccess = isUserLoginSuccess();

				
				Log.d("BG", "isUserLoginSuccess "+isLoginSuccess);

				// Decide where to redirect user depending on isLoginSuccess
				Class<?> targetClass = isLoginSuccess? MainController.class:LoginController.class;

				long durationTime = android.os.SystemClock.uptimeMillis() - startTime;
				Log.d("BG", "Login time taken: "+durationTime);
				if (durationTime <=SPLASH_DELAY) {
					try {
						synchronized(this){
							//Pause the application for remaining time to meet SPLASH_DELAY
							wait(SPLASH_DELAY-durationTime);
						}
					} catch (Exception e) {
						Log.d("BG", "spalashThread Exception: "+e.getLocalizedMessage());
					}
				}

				/*
				 *===============END Loading Job==================== 
				 */

				finish();
				startActivity(new Intent().setClass(SplashController.this, targetClass));
			}
		};
		splashTread.start();
	}

	/**
	 * Returns whether login attempt using stored credentials was successful or not.
	 * 
	 * Assumption: If FacebookSessionStore exists, then so will UserSessionStore.
	 * (Since Server will generate token on every login no matter what)
	 * 
	 * @return True if login is success
	 * 
	 */
	private boolean isUserLoginSuccess(){
		//Load saved session settings
		UserSessionStore.loadUserSession(getApplicationContext());
		FacebookSessionStore.restore(getApplicationContext());

		//If session token is not empty, there must have been a login history in the past. 
		if(!UserInfo.isTokenStringEmpty()){
			//If Facebook session is valid, then the user must have logged in using facebook account.
			if(UserInfo.mFacebook.isSessionValid()){ 
				updateUserInfoUsingFacebookToken();	//Call Facebook API
				Log.d("BG", "UserInfo by Facebook: "+UserInfo.getUserFirstName()+" "+UserInfo.getUserLastName()+" . "+UserInfo.getUserEmail());
			}
			
			return UserInfo.loginUserInfo(getApplicationContext());
		}
		else{	//No token exists
			return false;
		}
	}

	/**
	 * Send request user's first name, last name and email to Facebook
	 * 
	 * mLockThread is enabled to make program wait until the user's information has been retrieved. 
	 */
	private void updateUserInfoUsingFacebookToken(){
		mLockThread = true;
		String fbRequestDetail = "first_name, last_name, email";	//Request name, email to Facebook
		Bundle params = new Bundle();
		params.putString("fields", fbRequestDetail);
		UserInfo.mAsyncRunner.request("me", params,new UserInfoRequestListener());

		while(mLockThread);	//Wait until mLockThread is disabled.
	}
	
	/**
	 * Callback called from updateUserInfoUsingFacebookToken.
	 * 
	 * User's first name, last name and email are available for store.
	 */
	protected class UserInfoRequestListener extends BaseRequestListener {
		@Override
		public void onComplete(final String response, final Object state) {
			try {
				JSONObject jsonObject = new JSONObject(response);

				UserInfo.setUserFirstName(jsonObject.getString("first_name"));
				UserInfo.setUserLastName(jsonObject.getString("last_name"));
				UserInfo.setUserEmail(jsonObject.getString("email"));

				mLockThread = false;
			} catch (Exception e) {
				Log.d("BG", "UserInfoRequestListener Exception: "+e.getLocalizedMessage());
			}
		}
	}

	/**
	 * Called to extend mFacebook token, if necessary
	 */
	public void onResume() {    
		super.onResume();
		if(UserInfo.mFacebook != null && UserInfo.mFacebook.isSessionValid())
			UserInfo.mFacebook.extendAccessTokenIfNeeded(this, null);
	}
}
