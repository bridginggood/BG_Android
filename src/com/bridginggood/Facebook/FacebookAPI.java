package com.bridginggood.Facebook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.bridginggood.CONST;
import com.bridginggood.UserInfo;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.FacebookError;

public class FacebookAPI {
	private static final long TIMEOUT = 10000; //TIMEOUT 10 seconds
	private static boolean mLock = false, mUnlockedByTimeout=false;
	private static long mStartTime;
	
	/**
	 *  * Requests user to logout from Facebook.
	 * 
	 * @param context
	 * 
	 * @return 	True if request was successful.
	 * 			False if mLock is timed out.
	 * 
	 */
	public static boolean requestUserLogout(Context context){
		startLock();
		
		UserInfo.mAsyncRunner.logout(context, new RequestListener() {
			@Override
			public void onComplete(String response, Object state) {
				Log.d("BgFacebook", "FacebookAPI: User successfully logged out from Facebook");
				stopLock();
			}

			@Override
			public void onIOException(IOException e, Object state) {}

			@Override
			public void onFileNotFoundException(FileNotFoundException e,
					Object state) {}

			@Override
			public void onMalformedURLException(MalformedURLException e,
					Object state) {}

			@Override
			public void onFacebookError(FacebookError e, Object state) {
				Log.d("BgFacebook", "FacebookAPI: Failed to logout user");
				stopLock();
			}
		});
		
		return noTimeoutOnLock();
	}
	
	/**
	 * Requests user's first name, last name and email to Facebook.
	 * 
	 * @return 	True if request was successful.
	 * 			False if mLock is timed out.
	 */
	public static boolean requestUserInfo(){
		startLock();
		
		String fbRequestDetail = "id, first_name, last_name, email";	//Request name, email to Facebook
		Bundle params = new Bundle();
		params.putString("fields", fbRequestDetail);
		UserInfo.mAsyncRunner.request("me", params,new UserInfoRequestListener());
		
		return noTimeoutOnLock();
	}
	
	/**
	 * Callback called from updateUserInfoUsingFacebookToken.
	 * 
	 * User's first name, last name and email are available for store.
	 */
	protected static class UserInfoRequestListener extends BaseRequestListener {
		@Override
		public void onComplete(final String response, final Object state) {
			try {
				JSONObject jsonObject = new JSONObject(response);
				UserInfo.createUserSessionForFacebook(jsonObject.getString("id"), jsonObject.getString("email"), jsonObject.getString("first_name"), jsonObject.getString("last_name"),CONST.USER_SESSION_TYPE_FACEBOOK);
				mLock = false;
			} catch (Exception e) {
				Log.d("BgFacebook", "UserInfoRequestListener Exception: "+e.getLocalizedMessage());
			}
		}
	}
	
	/**
	 * Extend Facebook Token if necessary.
	 * @param context Application context for Facebook's SharedPreference
	 */
	public static void extendFacebookToken(Context context){
		if(UserInfo.mFacebook != null && UserInfo.mFacebook.isSessionValid())
			UserInfo.mFacebook.extendAccessTokenIfNeeded(context, null);
	}
	
	private static void startLock(){
		mLock = true;
		mStartTime = android.os.SystemClock.uptimeMillis();
	}
	
	/**
	 * 
	 * @return 	True if timeout did not occur
	 * 			False if timeout occurred
	 */
	private static boolean noTimeoutOnLock(){
		mUnlockedByTimeout = false;
		while(mLock){
			long durationTime = android.os.SystemClock.uptimeMillis() - mStartTime;
			if(durationTime > TIMEOUT){
				mUnlockedByTimeout = true;
				mLock = false;
			}
		}
		return !mUnlockedByTimeout;
	}
	
	private static void stopLock(){
		mLock = false;
	}
}
