package com.bridginggood;

import android.app.Application;
import android.content.Context;

import com.bridginggood.DB.LoginJSON;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;

public class UserInfo extends Application{
	private static String mUserEmail, mUserPassword, mUserType, mUserFirstName, mUserLastName, mPhoneType, mTokenString;
	public static Facebook mFacebook;
	public static AsyncFacebookRunner mAsyncRunner;
	
	public static void init(){
		setUserEmail(null);
		setUserPassword(null);
		setUserType(null);
		setUserFirstName(null);
		setUserLastName(null);
		setPhoneType(null);
		mFacebook = new Facebook(CONST.FACEBOOK_APP_ID);
		mAsyncRunner= new AsyncFacebookRunner(mFacebook);
	}
	
	public static void createUserSessionForFacebook(String email, String firstname, String lastname, String type){
		setUserEmail(email);
		setUserFirstName(firstname);
		setUserLastName(lastname);
		setUserType(type);
	}

	public static void createUserSessionForToken(String loginToken, String type){
		setTokenString(loginToken);
		setUserType(type);
	}

	public static void createUserSessionForBG(String email, String password, String type){
		setUserEmail(email);
		setUserPassword(password);
		setUserType(type);
	}
	
	/**
	 * Depending on the data available on UserSession, 
	 * call appropriate methods to perform login.
	 * 
	 * If type is FACEBOOK, directs user to facebook login method no matter what.
	 * Else, if Login Token exists, directs to token login method.
	 * Otherwise, go to BG login method.
	 * 
	 *@return true if login was successful.  
	 */
	public static boolean loginUserSession(Context context){
		if (getUserType().equals(CONST.USER_SESSION_TYPE_FACEBOOK))
		{
			//Go to Facebook Login
			if (LoginJSON.loginUserSession(CONST.LOGIN_TYPE_FACEBOOK))
				return saveCurrentUserSessionToUserSessionStore(context);
			else
				return false;
		}
		else if (getTokenString() != null){
			//Go to token login
			if (LoginJSON.loginUserSession(CONST.LOGIN_TYPE_TOKEN))
				return saveCurrentUserSessionToUserSessionStore(context);
			else
				return false;
		}
		else{
			//Go to BG login
			if (LoginJSON.loginUserSession(CONST.LOGIN_TYPE_BG))
				return saveCurrentUserSessionToUserSessionStore(context);
			else
				return false;
		}
	}
	
	private static boolean saveCurrentUserSessionToUserSessionStore(Context context){
		return UserSessionStore.saveUserSession(context);
	}
	
	public static boolean isTokenStringEmpty(){
		return (getTokenString() == null);
	}
	
	public static String getUserFirstName() {
		return mUserFirstName;
	}
	public static void setUserFirstName(String userFirstName) {
		mUserFirstName = userFirstName;
	}
	public static String getUserType() {
		return mUserType;
	}
	public static void setUserType(String userType) {
		mUserType = userType;
	}
	public static String getUserLastName() {
		return mUserLastName;
	}
	public static void setUserLastName(String userLastName) {
		mUserLastName = userLastName;
	}
	public static String getUserEmail() {
		return mUserEmail;
	}
	public static void setUserEmail(String userEmail) {
		mUserEmail = userEmail;
	}
	public static String getPhoneType() {
		return mPhoneType;
	}
	public static void setPhoneType(String phoneType) {
		mPhoneType = phoneType;
	}

	public static String getTokenString() {
		return mTokenString;
	}

	public static void setTokenString(String tokenString) {
		mTokenString = tokenString;
	}

	public static String getUserPassword() {
		return mUserPassword;
	}

	public static void setUserPassword(String password) {
		mUserPassword = password;
	}
}
