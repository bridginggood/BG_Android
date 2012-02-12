/*
 * Similar to SessionStore.java from Facebook Hackbook
 * 
 * Modified slightly by Junsung
 */

package com.bridginggood.Facebook;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.bridginggood.UserInfo;

public class FacebookSessionStore {

    private static final String TOKEN = "access_token";
    private static final String EXPIRES = "expires_in";
    private static final String KEY = "facebook-session";

    /*
     * Save the access token and expiry date so you don't have to fetch it each
     * time
     */

    public static boolean save(Context context) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.putString(TOKEN, UserInfo.mFacebook.getAccessToken());
        editor.putLong(EXPIRES, UserInfo.mFacebook.getAccessExpires());
        return editor.commit();
    }

    /*
     * Restore the access token and the expiry date from the shared preferences.
     */
    public static boolean restore(Context context) {
        SharedPreferences savedSession = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        UserInfo.mFacebook.setAccessToken(savedSession.getString(TOKEN, null));
        UserInfo.mFacebook.setAccessExpires(savedSession.getLong(EXPIRES, 0));
        return UserInfo.mFacebook.isSessionValid();
    }

    public static void clear(Context context) {
        Editor editor = context.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }

}
