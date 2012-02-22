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

public class UserSessionStore {

	private static final String TOKEN = "loginToken";					//Token attribute
	private static final String USER_TYPE = "userType";					//Type of the user
	private static final String C2DM_REGISTRATION_ID = "c2dmRegId";		//C2DM Registration Id
	private static final String KEY = "BridgingGoodSession";			//SharedPreference Key Value

	/**
	 * Save user session object to SharedPreference
	 * 
	 * @param context 		Application context to call SharedPreference in
	 */
	public static boolean saveUserSession(Context context) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putString(TOKEN, UserInfo.getTokenString());
		editor.putString(USER_TYPE, UserInfo.getUserType());
		editor.putString(C2DM_REGISTRATION_ID, UserInfo.getC2DMRegistrationId());
		return editor.commit();
	}

	public static void loadUserSession(Context context){
		SharedPreferences savedSession = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		UserInfo.setTokenString(savedSession.getString(TOKEN, null));
		UserInfo.setUserType(savedSession.getString(USER_TYPE,null));
		UserInfo.setC2DMRegistrationId(savedSession.getString(C2DM_REGISTRATION_ID, null));
	}

	public static void clearSession(Context context) {
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.clear();
		editor.commit();
	}

	public static boolean saveUserSessionC2DMOnly(Context context){
		Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putString(C2DM_REGISTRATION_ID, UserInfo.getC2DMRegistrationId());
		return editor.commit();
	}
}
