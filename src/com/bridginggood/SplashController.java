/**
 * Created By: Junsung Lim
 * 
 * Description:
 * 	Starting point of the BridgingGood application.
 * 	
 * 	Checks whether saved user token exists or not. If not, directs user to the login page.
 * 	Otherwise, directs the user to the MainController
 */
package com.bridginggood;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bridginggood.Facebook.BaseRequestListener;
import com.bridginggood.Facebook.FacebookSessionStore;

public class SplashController extends Activity {
	protected final static long SPLASH_DELAY = 1500;	//Minimum time to show Splash in milliseconds
	private UserSession mUserSession;
	private boolean mLockThread = false;				//Created to put a lock on asynchronous thread

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_layout);

		Thread splashTread = new Thread() {
			@Override
			public void run() {
				long startTime = android.os.SystemClock.uptimeMillis();

				/*
				 *===============START Loading Job==================== 
				 */
				UserInfo.init();	//Initialize UserInfo

				//Check if saved user token exists or not
				boolean userWithValidUserToken = hasValidUserToken();

				/*
				 * Decide where to redirect user depending on userWithValidUserToken
				 */
				Log.d("BG", "userWithValidUserToken "+userWithValidUserToken);

				Class<?> targetClass = null;
				if(userWithValidUserToken){
					//Go to main tabhost view
					targetClass = MainController.class;
				}
				else{
					//Go to login view
					targetClass = LoginController.class;
				}

				long durationTime = android.os.SystemClock.uptimeMillis() - startTime;
				Log.d("BG", "Should redirect soon "+durationTime);
				if (durationTime <=SPLASH_DELAY) {
					try {
						synchronized(this){
							wait(SPLASH_DELAY-durationTime);			//Pause the application for remaining time to meet SPLASH_DELAY
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
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

	/*
	 * Returns whether the stored login was successful or not.
	 * 
	 * Assumption: If FacebookSessionStore exists, then so will UserSessionStore.
	 * (Since Server will generate token on every login no matter what)
	 */
	private boolean hasValidUserToken(){
		mUserSession = UserSessionStore.loadUserSession(getApplicationContext());
		FacebookSessionStore.restore(UserInfo.mFacebook, getApplicationContext());

		//If session token is not empty, there must have been a login history in the past. 
		if(!mUserSession.isSessionTokenEmpty()){
			//If Facebook session is valid, then the user must have logged in using facebook account.
			if(UserInfo.mFacebook.isSessionValid()){ 
				updateUserInfoUsingFacebookToken();	//Call Facebook API
				mUserSession.createUserSessionForFacebook(UserInfo.getUserEmail(), UserInfo.getUserFirstName(),
						UserInfo.getUserLastName(), CONST.USER_SESSION_TYPE_FACEBOOK);	//Create UserSession for the login
				Log.d("BG", "Name: "+UserInfo.getUserFirstName()+" "+UserInfo.getUserLastName()+" . "+UserInfo.getUserEmail());
			}
			
			return mUserSession.loginUserSession();
		}
		else{	//No token exists
			return false;
		}
	}

	private void updateUserInfoUsingFacebookToken(){
		mLockThread = true;
		Bundle params = new Bundle();
		params.putString("fields", "first_name, last_name, email");
		UserInfo.mAsyncRunner.request("me", params,new UserInfoRequestListener());

		while(mLockThread);
	}
	/*
	 * Callback for fetching current user's firstname, lastname, email
	 */
	protected class UserInfoRequestListener extends BaseRequestListener {

		@Override
		public void onComplete(final String response, final Object state) {
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(response);

				//String firstname = jsonObject.getString("first_name");
				//String lastname = jsonObject.getString("last_name");
				//String email = jsonObject.getString("email");
				UserInfo.setUserFirstName(jsonObject.getString("first_name"));
				UserInfo.setUserLastName(jsonObject.getString("last_name"));
				UserInfo.setUserEmail(jsonObject.getString("email"));
				//Log.d("BG", "SSS Name: "+UserInfo.getUserFirstName()+" "+UserInfo.getUserLastName()+" . "+UserInfo.getUserEmail());
				mLockThread = false;


			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * Called to extend mFacebook token, if necessary
	 */
	public void onResume() {    
		super.onResume();
		if(UserInfo.mFacebook != null && UserInfo.mFacebook.isSessionValid())
			UserInfo.mFacebook.extendAccessTokenIfNeeded(this, null);
	}
}
