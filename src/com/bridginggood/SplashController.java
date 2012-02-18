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

import java.util.UUID;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.bridginggood.Facebook.FacebookAPI;
import com.bridginggood.Facebook.FacebookSessionStore;

public class SplashController extends Activity {
	private ProgressDialog mProgressDialog;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_layout);
		
		mProgressDialog = ProgressDialog.show(this, "", "Loading... Please wait", true, false);
		UserInfo.init();	//Initialize UserInfo
		
		Thread splashTread = new Thread() {
			@Override
			public void run() {
				long startTime = android.os.SystemClock.uptimeMillis();

				/*
				 *===============START Loading Job==================== 
				 */
				
				//Get device ID
				UserInfo.setDeviceId(getDeviceId());
				
				//Check if saved user token exists or not
				boolean isLoginSuccess = isUserLoginSuccess();

				
				Log.d("BG", "isUserLoginSuccess "+isLoginSuccess);

				// Decide where to redirect user depending on isLoginSuccess
				Class<?> targetClass = isLoginSuccess? MainController.class:LoginController.class;

				long durationTime = android.os.SystemClock.uptimeMillis() - startTime;
				Log.d("BG", "Login time taken: "+durationTime);
				if (durationTime <=CONST.SPLASH_DELAY) {
					try {
						synchronized(this){
							//Pause the application for remaining time to meet SPLASH_DELAY
							wait(CONST.SPLASH_DELAY-durationTime);
						}
					} catch (Exception e) {
						Log.d("BG", "spalashThread Exception: "+e.getLocalizedMessage());
					}
				}

				/*
				 *===============END Loading Job==================== 
				 */

				mProgressDialog.dismiss();
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
				FacebookAPI.requestUserInfo();
				Log.d("BG", "UserInfo by Facebook: "+UserInfo.getUserFirstName()+" "+UserInfo.getUserLastName()+" . "+UserInfo.getUserEmail());
			}
			return UserInfo.loginUserInfo(getApplicationContext());
		}
		else{	//No token exists
			return false;
		}
	}

	/**
	 * Called to extend mFacebook token, if necessary
	 */
	public void onResume() {    
		super.onResume();
		FacebookAPI.extendFacebookToken(getApplicationContext());
	}
	
	/**
	 * Combination of TelephonyManager and Settings.Secure to generate unique device id
	 * @return unique device id
	 */
	private String getDeviceId(){
		final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

	    final String tmDevice, tmSerial, androidId;
	    tmDevice = "" + tm.getDeviceId();
	    tmSerial = "" + tm.getSimSerialNumber();
	    androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

	    UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
	    String deviceId = deviceUuid.toString();
	    
	    return deviceId;
	}
}
