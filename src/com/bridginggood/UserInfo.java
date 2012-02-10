package com.bridginggood;

import android.app.Application;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;

public class UserInfo extends Application{
	private static String mUserEmail, mUserType, mUserFirstName, mUserLastName, mPhoneType;
	public static Facebook mFacebook;
	public static AsyncFacebookRunner mAsyncRunner;
	
	public static void init(){
		setUserEmail(null);
		setUserType(null);
		setUserFirstName(null);
		setUserLastName(null);
		setPhoneType(null);
		mFacebook = new Facebook(CONST.FACEBOOK_APP_ID);
		mAsyncRunner= new AsyncFacebookRunner(mFacebook);
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
}
