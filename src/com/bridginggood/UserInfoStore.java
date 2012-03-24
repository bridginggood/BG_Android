/**
 * Created By: Junsung Lim
 * 
 * Static class to make managing SharedPreferences easier for BridgingGood.
 * This class is called to verify if login token exists on the device.
 * 
 * NOTE: Do not store Device Id on the database.
 */
package com.bridginggood;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class UserInfoStore {

	private static final String TOKEN = "loginToken";					//BG Token attribute
	private static final String TYPE = "userType";						//User login type
	private static final String EMAIL = "userEmail";
	private static final String FIRSTNAME = "userFirstName";
	private static final String LASTNAME = "userLastName";
	private static final String FBUID = "fbUid";
	private static final String FBTOKEN = "fbToken";					//Facebook Token
	private static final String FBTOKEN_EXPIRES_IN = "fbTokenExpiresIn";//Facebook Token Expiry
	private static final String FBAUTO_POST = "fbAutoPost";				//Facebook auto-post
	private static final String DEVICE_ID = "deviceId";					//Device Id
	private static final String C2DM_REGISTRATION_ID = "c2dmRegId";		//C2DM Registration Id
	private static final String PUSH_MESSAGE = "pushMessage";			//For C2DM messages received
	private static final String KEY = "BridgingGoodSession";			//SharedPreference Key Value

	/**
	 * Save user session object to SharedPreference
	 * 
	 * @param context 		Application context to call SharedPreference in
	 */
	public static boolean saveUserSession(Context context) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putString(TOKEN, UserInfo.getTokenString());
		editor.putString(EMAIL, UserInfo.getUserEmail());
		editor.putString(FIRSTNAME, UserInfo.getUserFirstName());
		editor.putString(LASTNAME, UserInfo.getUserLastName());
		editor.putInt(TYPE, UserInfo.getUserType());
		editor.putString(FBUID, UserInfo.getFbUid());
		editor.putString(FBTOKEN, UserInfo.mFacebook.getAccessToken());
        editor.putLong(FBTOKEN_EXPIRES_IN, UserInfo.mFacebook.getAccessExpires());
        editor.putBoolean(FBAUTO_POST, UserInfo.isFbAutoPost());
		return editor.commit();
	}

	public static void loadUserSession(Context context){
		SharedPreferences savedSession = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		UserInfo.setTokenString(savedSession.getString(TOKEN, null));
		UserInfo.setUserEmail(savedSession.getString(EMAIL, null));
		UserInfo.setUserFirstName(savedSession.getString(FIRSTNAME, null));
		UserInfo.setUserLastName(savedSession.getString(LASTNAME, null));
		UserInfo.setUserType(savedSession.getInt(TYPE, 0));
		UserInfo.setFbUid(savedSession.getString(FBUID, null));
		UserInfo.mFacebook.setAccessToken(savedSession.getString(FBTOKEN, null));
        UserInfo.mFacebook.setAccessExpires(savedSession.getLong(FBTOKEN_EXPIRES_IN, 0));
        UserInfo.setFbAutoPost(savedSession.getBoolean(FBAUTO_POST, true));
		UserInfo.setDeviceId(savedSession.getString(DEVICE_ID, null));
		UserInfo.setC2DMRegistrationId(savedSession.getString(C2DM_REGISTRATION_ID, null));
	}

	public static void clearSession(Context context) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.clear();
		editor.commit();
	}

	public static boolean saveUserSessionC2DMOnly(Context context, final String regId){
		Log.d("BGUS", "saveUserSessionC2DMonly called");
		UserInfo.setC2DMRegistrationId(regId);
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putString(C2DM_REGISTRATION_ID, UserInfo.getC2DMRegistrationId());
		return editor.commit();
	}
	
	public static boolean saveFacebookSessionOnly(Context context){
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putString(FBTOKEN, UserInfo.mFacebook.getAccessToken());
		editor.putLong(FBTOKEN_EXPIRES_IN, UserInfo.mFacebook.getAccessExpires());
		return editor.commit();
	}
	
	public static boolean savePushMessage(Context context, String newPushMessage){
		SharedPreferences savedSession = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		Editor editor = savedSession.edit();
		String oldMessage = savedSession.getString(PUSH_MESSAGE, "");
		String newMessage = oldMessage + newPushMessage + "|";
		editor.putString(PUSH_MESSAGE, newMessage);
		return editor.commit();
	}
	
	public static String loadPushMessage(Context context){
		SharedPreferences savedSession = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		String oldMessage = savedSession.getString(PUSH_MESSAGE, "");
		return oldMessage;
	}
	
	public static boolean clearPushMessage(Context context){
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putString(PUSH_MESSAGE, "");
		return editor.commit();
	}
}
