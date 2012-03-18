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

import com.bridginggood.DB.UserLoginJSON;
import com.bridginggood.Facebook.FacebookAPI;

public class SplashActivity extends Activity {
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
				
				//Load saved session settings
				UserInfo.init();
				UserSessionStore.loadUserSession(getApplicationContext());
				//FacebookSessionStore.restore(getApplicationContext());

				//Get device ID
				if(UserInfo.getDeviceId()==null){
					UserInfo.setDeviceId(getDeviceId());
					Log.d("BG", "Splash: device id is "+UserInfo.getDeviceId());
				}

				//Check facebook session
				FacebookAPI.extendFacebookToken(getApplicationContext());

				//Check if saved user token exists or not
				boolean isLoginSuccess = isUserAutoLoginSuccessful();


				Log.d("BG", "isUserLoginSuccess "+isLoginSuccess);

				// Decide where to redirect user depending on isLoginSuccess
				Class<?> targetClass = isLoginSuccess? MainActivity.class:LoginActivity.class;

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
				startActivity(new Intent().setClass(SplashActivity.this, targetClass));
			}
		};
		splashTread.start();
	}

	/**
	 * Returns whether login attempt using stored credentials was successful or not.
	 * @return True if login is success
	 */
	private boolean isUserAutoLoginSuccessful(){
		Log.d("BG", "Stored UserInfo getUserType:"+UserInfo.getUserType());
		Log.d("BG", "Stored UserName getUserFirstName: "+UserInfo.getUserFirstName());
		Log.d("BG", "Stored Facebook getFbUid: "+UserInfo.getFbUid());
		Log.d("BG", "Stored Facebook token: "+UserInfo.mFacebook.getAccessToken());
		Log.d("BG", "Stored Facebook expiry: "+UserInfo.mFacebook.getAccessExpires());
		return UserLoginJSON.loginUser(UserInfo.getUserType());
	}

	/**
	 * TODO: Delete this later
	 * Temporary method to skip login dialog
	 * @return
	 */
	private boolean skipLogin(){
		UserInfo.setUserEmail("skip@example.com");
		UserInfo.setUserFirstName("Michael");
		UserInfo.setUserLastName("Jackson");
		UserInfo.setUserId((long) 1000000111);

		return true;
	}

	/**
	 * Called to extend mFacebook token, if necessary
	 */
	public void onResume() {    
		Log.d("BG", "SplashActivity onResume called. FacebookAPI.extendFacebookToken will be called");
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
