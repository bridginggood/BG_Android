/**
 * Created By: Junsung Lim
 * 
 * UserInfo is a static class that holds current user info
 */
package com.bridginggood;

import android.app.Application;
import android.content.Context;

import com.bridginggood.DB.UserLoginJSON;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;

public class UserInfo extends Application{
	private static String mUserEmail, mUserPassword, mUserType, mUserFirstName, mUserLastName;
	private static String mTokenString, mDeviceId, mC2DMRegistrationId;
	private static boolean mIsFirstTimeOnThisDevice;
	public static Facebook mFacebook;
	public static AsyncFacebookRunner mAsyncRunner;
	
	public static void init(){
		setUserEmail(null);
		setUserPassword(null);
		setUserType(null);
		setUserFirstName(null);
		setUserLastName(null);
		setDeviceId(null);
		setFirstTimeOnThisDevice(true);
		setC2DMRegistrationId(null);
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

	public static void createUserInfoForBG(String email, String password, String type){
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
	public static boolean loginUserInfo(Context context){
		boolean isLoginSucc = false;
		
		if (getUserType().equals(CONST.USER_SESSION_TYPE_FACEBOOK))
		{
			//Go to Facebook Login
			if (UserLoginJSON.loginUser(CONST.LOGIN_TYPE_FACEBOOK))
				isLoginSucc = true;
			else
				return false;
		}
		else if (getTokenString() != null && getUserPassword() == null){
			//Go to token login
			if (UserLoginJSON.loginUser(CONST.LOGIN_TYPE_TOKEN))
				isLoginSucc = true;
			else
				return false;
		}
		else{
			//Go to BG login
			if (UserLoginJSON.loginUser(CONST.LOGIN_TYPE_BG))
				isLoginSucc = true;
			else
				return false;
		}
		
		//If login is successful, register the device
		if(isLoginSucc){
			if (UserInfo.getFirstTimeOnThisDevice())
				UserLoginJSON.sendThisDeviceDetail();
			return saveCurrentUserSessionToUserSessionStore(context);
		}
		else
			return false;
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

	public static boolean getFirstTimeOnThisDevice() {
		return mIsFirstTimeOnThisDevice;
	}

	public static void setFirstTimeOnThisDevice(boolean mIsFirst) {
		UserInfo.mIsFirstTimeOnThisDevice = mIsFirst;
	}

	public static String getDeviceId() {
		return mDeviceId;
	}

	public static void setDeviceId(String mDeviceId) {
		UserInfo.mDeviceId = mDeviceId;
	}

	public static String getC2DMRegistrationId() {
		return mC2DMRegistrationId;
	}

	public static void setC2DMRegistrationId(String mC2DMRegistrationId) {
		UserInfo.mC2DMRegistrationId = mC2DMRegistrationId;
	}
}
