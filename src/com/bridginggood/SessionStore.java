/**
 * Created By: Junsung Lim
 * 
 * Static class to make managing SharedPreferences easier for BridgingGood.
 * This class is called to verify if login token exists on the device.
 */
package com.bridginggood;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionStore {

    private static final String TOKEN = "loginToken";					//Token attribute
    private static final String ISREMEMBERLOGIN = "isRememberLogin";	//isRememberLogin attribute
    private static final String KEY = "BridgingGoodSession";			//SharedPreference Key Value

    /**
     * Save user session object to SharedPreference
     * 
     * @param context 		Application context to call SharedPreference in
     * @param userSession 	Session object to save
     */
    public static boolean saveUserSession(Context context, Session userSession) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putString(TOKEN, userSession.getLoginToken());
        editor.putBoolean(ISREMEMBERLOGIN, userSession.getIsRememberLogin());
        return editor.commit();
    }
    
    public static Session loadUserSession(Context context){
    	SharedPreferences savedSession = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
    	Session userSession = new Session();
    	userSession.setLoginToken(savedSession.getString(TOKEN, null));
    	userSession.setIsRememberLogin(savedSession.getBoolean(ISREMEMBERLOGIN, false));
    	return userSession;
    }

    public static void clearSession(Context context) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }

}
