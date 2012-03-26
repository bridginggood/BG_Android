/**
 * Created By: Junsung Lim
 * 
 * Calls server for user login. 
 * Calls to server are done in POST method.
 * Retrieves JSON from the server in return.
 */
package com.bridginggood.DB;

import org.json.JSONObject;

import android.util.Log;

import com.bridginggood.CONST;
import com.bridginggood.UserInfo;

public class AuthJSON {

	private static final String PARAM_USER_ID = "UserId";
	private static final String PARAM_USER_EMAIL = "Email";
	private static final String PARAM_USER_FIRSTNAME = "FirstName";
	private static final String PARAM_USER_LASTNAME = "LastName";
	private static final String PARAM_USER_PASSWORD = "UserPassword";
	private static final String PARAM_USER_TYPE = "UserType";
	private static final String PARAM_TOKEN_STRING = "TokenString";

	private static final String PARAM_FB_UID = "FacebookUid";

	private static final String PARAM_AUTO_POST = "AutoPost";

	private static final String PARAM_DEVICE_ID = "DeviceId";
	private static final String PARAM_DEVICE_TYPE = "DeviceType";
	private static final String PARAM_C2DM_REGISTRATION_ID = "C2DMRegId";
	private static final String PARAM_QRCODE_ID = "QrId";

	private static final String PARAM_RESULT_CODE = "resultCode";
	private static final String PARAM_RESULT_MSG = "resultMsg";

	/**
	 * Interacts with server to do user login.
	 * 
	 * @param UserType Type of the user. Refer to CONST.java
	 * 
	 * @return True upon successful login
	 */
	public static boolean loginUser(final int UserType){
		try {
			String targetURL = "";
			String requestParam = "";
			// Make POST dataset
			switch(UserType)
			{
			case CONST.USER_SESSION_TYPE_BG:
				/*
				targetURL = CONST.API_LOGIN_BY_BG_URL;
				String[][] paramBG = {	
						{PARAM_USER_EMAIL, UserInfo.getUserEmail()},
						{PARAM_USER_PASSWORD, UserInfo.getUserPassword()},
						{PARAM_DEVICE_ID, UserInfo.getDeviceId()},
						{PARAM_DEVICE_TYPE, DATA_DEVICE_TYPE}
				};
				requestParam = BgHttpHelper.generateParamData(paramBG);
				 */
				break;

			case CONST.USER_SESSION_TYPE_FACEBOOK:
				targetURL = CONST.API_LOGIN_BY_FACEBOOK_URL;
				String[][] paramFacebook = {	
						{PARAM_FB_UID, UserInfo.getFbUid()},
						{PARAM_USER_EMAIL, UserInfo.getUserEmail()},
						{PARAM_USER_FIRSTNAME, UserInfo.getUserFirstName()},
						{PARAM_USER_LASTNAME, UserInfo.getUserLastName()},
						{PARAM_DEVICE_ID, UserInfo.getDeviceId()},
						{PARAM_DEVICE_TYPE, CONST.DEVICE_TYPE}
				};
				requestParam = BgHttpHelper.generateParamData(paramFacebook);
				break;

			default:
				return false;
			}

			String jsonStr = BgHttpHelper.requestHttpRequest(targetURL, requestParam, "POST");
			Log.d("BgDB", "LoginJSON received:"+jsonStr);

			JSONObject jsonObject = new JSONObject(jsonStr);

			//TODO: Maybe this needs to be changed later on.
			if(jsonObject.getString(PARAM_RESULT_CODE).charAt(0) == 'S'){
				//Login succeed!
				updateUserInfoWithJSON(jsonObject);
				return true;
			} else {
				Log.d("BgDB", "Login failed: "+jsonObject.getString(PARAM_RESULT_MSG));
				return false;
			}
		} catch (Exception e){
			// Handle all exception
			Log.d("BgDB", "Exception occured: "+e.getLocalizedMessage());
		}
		return false;
	}

	/**
	 * Update UserInfo with JSON data
	 * @param jsonObject JSON from the server
	 * @throws Exception any exception that occurs while reading jsonObject
	 */
	private static void updateUserInfoWithJSON(JSONObject jsonObject) throws Exception{
		UserInfo.setUserId(jsonObject.getLong(PARAM_USER_ID));
		UserInfo.setQRCodeURL(jsonObject.getString(PARAM_QRCODE_ID));
		UserInfo.setUserPassword(null);	//Nullify for security reason?!
	}

	/**
	 * Sends C2DM registration info to the server
	 */
	public static boolean sendC2DMRegistrationId(final String devId, final String regId){
		try{
			String targetURL = CONST.API_CREATE_C2DM_DEVICE_URL;
			String[][] param = {	
					{PARAM_C2DM_REGISTRATION_ID, regId},
					{PARAM_DEVICE_ID, devId}};
			String requestParam = BgHttpHelper.generateParamData(param);

			String jsonStr = BgHttpHelper.requestHttpRequest(targetURL, requestParam, "POST");

			JSONObject jsonObject = new JSONObject(jsonStr);

			//TODO: Maybe this needs to be changed later on.
			if(jsonObject.getString(PARAM_RESULT_CODE).charAt(0) == 'S'){
				//Registration success! 
				Log.d("BgDB", "C2DM registration completed: "+jsonObject.getString(PARAM_RESULT_MSG));
				return true;

			} else {
				Log.d("BgDB", "C2DM registration failed: "+jsonObject.getString(PARAM_RESULT_MSG));
				return false;
			}
		}
		catch(Exception e){
			Log.d("BgDB", "sendC2DMRegistrationId Exception: "+e.getLocalizedMessage());
		}
		return false;
	}

	/**
	 * Sends QRCode generation request to server
	 */
	public static void createQRCode(){
		try{
			String targetURL = CONST.API_CREATE_QRCODE_URL;
			String[][] param = {	{PARAM_USER_ID, UserInfo.getUserId()+""},
					{PARAM_DEVICE_ID, UserInfo.getDeviceId()}};
			String requestParam = BgHttpHelper.generateParamData(param);

			String jsonStr = BgHttpHelper.requestHttpRequest(targetURL, requestParam, "POST");

			JSONObject jsonObject = new JSONObject(jsonStr);

			//TODO: Maybe this needs to be changed later on.
			if(jsonObject.getString(PARAM_RESULT_CODE).charAt(0) == 'S'){
				//Login succeed!
				Log.d("BgDB", "QRCode generated");

			} else {
				Log.d("BgDB", "QRCode NOT generated");
			}
		}
		catch(Exception e){
			Log.d("BgDB", "createQRCode Exception: "+e.getLocalizedMessage());
		}
	}

	public static boolean logoutUser(){
		try{
			String targetURL = CONST.API_LOGOUT;
			String[][] param = {	{PARAM_USER_ID, UserInfo.getUserId()+""},
					{PARAM_DEVICE_ID, UserInfo.getDeviceId()},
					{PARAM_DEVICE_TYPE, CONST.DEVICE_TYPE}};
			String requestParam = BgHttpHelper.generateParamData(param);

			String jsonStr = BgHttpHelper.requestHttpRequest(targetURL, requestParam, "POST");

			JSONObject jsonObject = new JSONObject(jsonStr);

			//TODO: Maybe this needs to be changed later on.
			if(jsonObject.getString(PARAM_RESULT_CODE).charAt(0) == 'S'){
				//Login succeed!
				Log.d("BgDB", "Logout success");
				return true;
			} else {
				Log.d("BgDB", "Logout failed");
				return false;
			}
		}
		catch(Exception e){
			Log.d("BgDB", "Logout Exception: "+e.getLocalizedMessage());
		}
		return false;
	}
}
