/**
 * Created By: Junsung Lim
 * 
 * UserInfo is a static class that holds current user info
 */
package com.bridginggood;

import android.app.Application;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;

public class UserInfo extends Application{
	private static String mFbUid, mUserEmail, mUserPassword, mUserFirstName, mUserLastName;
	private static String mTokenString, mDeviceId, mC2DMRegistrationId, mQRCodeURL;
	private static Long mUserId;
	private static int mUserType;
	public static Facebook mFacebook;
	public static AsyncFacebookRunner mAsyncRunner;
	
	public static void init(){
		setUserId(null);
		setUserEmail(null);
		setUserPassword(null);
		setUserFirstName(null);
		setUserLastName(null);
		setDeviceId(null);
		setC2DMRegistrationId(null);
		setQRCodeURL(null);
		setFbUid(null);
		setUserType(0);
		mFacebook = new Facebook(CONST.FACEBOOK_APP_ID);
		mAsyncRunner= new AsyncFacebookRunner(mFacebook);
	}
	
	public static void createUserSessionForFacebook(String fbuid, String email, String firstname, String lastname, int type){
		setFbUid(fbuid);
		setUserEmail(email);
		setUserFirstName(firstname);
		setUserLastName(lastname);
		setUserType(type);
	}

	public static void createUserSessionForToken(String loginToken, int type){
		setTokenString(loginToken);
		setUserType(type);
	}

	public static void createUserInfoForBG(String email, String password, int type){
		setUserEmail(email);
		setUserPassword(password);
		setUserType(type);
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
	public static int getUserType() {
		return mUserType;
	}
	public static void setUserType(int userType) {
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

	public static Long getUserId() {
		return mUserId;
	}

	public static void setUserId(Long mUserId) {
		UserInfo.mUserId = mUserId;
	}

	public static String getQRCodeURL() {
		return mQRCodeURL;
	}

	public static void setQRCodeURL(String mQRCodeURL) {
		//Update to full URL only if filename exists
		if(mQRCodeURL != null && mQRCodeURL.length() > 0)
			UserInfo.mQRCodeURL = CONST.QRCODE_S3_URL_PREFIX + mQRCodeURL + ".png";
	}

	public static String getFbUid() {
		return mFbUid;
	}

	public static void setFbUid(String mFbUid) {
		UserInfo.mFbUid = mFbUid;
	}
}
