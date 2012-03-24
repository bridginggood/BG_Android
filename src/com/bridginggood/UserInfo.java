/**
 * Created By: Junsung Lim
 * 
 * UserInfo is a static class that holds current user info
 */
package com.bridginggood;

import java.util.UUID;

import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;

public class UserInfo extends Application{
	private static String mFbUid, mUserEmail, mUserPassword, mUserFirstName, mUserLastName;
	private static String mTokenString, mDeviceId, mC2DMRegistrationId, mQRCodeURL;
	private static Long mUserId;
	private static int mUserType;
	private static boolean mFbAutoPost;
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
		setFbAutoPost(true);
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

	public static void setQRCodeURL(String qrId) {
		//Update to full URL only if filename exists
		if(qrId != null && qrId.length() > 0)
			UserInfo.mQRCodeURL = CONST.QRCODE_S3_URL_PREFIX + qrId + ".png";
	}

	public static String getFbUid() {
		return mFbUid;
	}

	public static void setFbUid(String mFbUid) {
		UserInfo.mFbUid = mFbUid;
	}
	

	/**
	 * Combination of TelephonyManager and Settings.Secure to generate unique device id
	 * @return unique device id
	 */
	public static String calcDeviceId(Context context){
		final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String deviceId = deviceUuid.toString();

		return deviceId;
	}

	public static boolean isFbAutoPost() {
		return mFbAutoPost;
	}

	public static void setFbAutoPost(boolean mFbAutoPost) {
		UserInfo.mFbAutoPost = mFbAutoPost;
	}
}
