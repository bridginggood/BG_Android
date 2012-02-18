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

public class UserLoginJSON {

	private static final String PARAM_USER_EMAIL = "UserEmail";
	private static final String PARAM_USER_FIRSTNAME = "UserFirstName";
	private static final String PARAM_USER_LASTNAME = "UserLastName";
	private static final String PARAM_USER_PASSWORD = "UserPassword";
	private static final String PARAM_USER_TYPE = "UserType";
	private static final String PARAM_TOKEN_STRING = "TokenString";
	private static final String PARAM_DEVICE_ID = "DeviceId";
	private static final String PARAM_DEVICE_TYPE = "DeviceType";

	private static final String PARAM_RESULT_CODE = "resultCode";
	private static final String PARAM_RESULT_MSG = "resultMsg";
	
	private static final String DATA_DEVICE_TYPE = "ANDRO";

	/**
	 * Interacts with server to do user login.
	 * 
	 * @param LoginType Type of the user. Refer to CONST.java
	 * 
	 * @return True upon successful login
	 */
	public static boolean loginUser(final int LoginType){
		try {
			String targetURL = "";
			String requestParam = "";
			// Make POST dataset
			switch(LoginType)
			{
			case CONST.LOGIN_TYPE_BG:
				targetURL = CONST.API_LOGIN_BY_BG_URL;
				String[][] paramBG = {	{PARAM_USER_EMAIL, UserInfo.getUserEmail()},
										{PARAM_USER_PASSWORD, UserInfo.getUserPassword()}};
				requestParam = BgHttpHelper.generateParamData(paramBG);
				break;
			case CONST.LOGIN_TYPE_FACEBOOK:
				targetURL = CONST.API_LOGIN_BY_FACEBOOK_URL;
				String[][] paramFacebook = {	{PARAM_USER_EMAIL, UserInfo.getUserEmail()},
												{PARAM_USER_FIRSTNAME, UserInfo.getUserFirstName()},
												{PARAM_USER_LASTNAME, UserInfo.getUserLastName()}};
				requestParam = BgHttpHelper.generateParamData(paramFacebook);
				break;
			case CONST.LOGIN_TYPE_TOKEN:
				targetURL = CONST.API_LOGIN_BY_TOKEN_URL;
				String[][] paramToken = {{PARAM_TOKEN_STRING, UserInfo.getTokenString()}};
				requestParam = BgHttpHelper.generateParamData(paramToken);
				break;
			}
			
			String jsonStr = BgHttpHelper.requestHttpRequest(targetURL, requestParam, "POST");

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
		UserInfo.setUserEmail(jsonObject.getString(PARAM_USER_EMAIL));
		UserInfo.setUserFirstName(jsonObject.getString(PARAM_USER_FIRSTNAME));
		UserInfo.setUserLastName(jsonObject.getString(PARAM_USER_LASTNAME));
		UserInfo.setUserType(jsonObject.getString(PARAM_USER_TYPE));
		UserInfo.setTokenString(jsonObject.getString(PARAM_TOKEN_STRING));

		UserInfo.setUserPassword(null);	//Nullify for security reason?!

		Log.d("BgDB", "UserInfo updated:"+UserInfo.getUserEmail()+" ,"+UserInfo.getUserFirstName()+","+
				UserInfo.getUserLastName()+", "+UserInfo.getUserType());
	}
	
	/**
	 * Sends device info to the server
	 */
	public static void sendThisDeviceDetail(){
		try{
		String targetURL = CONST.API_REGISTER_DEVICE_ID_URL;
		String[][] param = {	{PARAM_USER_EMAIL, UserInfo.getUserEmail()},
								{PARAM_DEVICE_ID, UserInfo.getDeviceId()},
								{PARAM_DEVICE_TYPE, DATA_DEVICE_TYPE}};
		String requestParam = BgHttpHelper.generateParamData(param);
		
		String jsonStr = BgHttpHelper.requestHttpRequest(targetURL, requestParam, "POST");

		JSONObject jsonObject = new JSONObject(jsonStr);

		//TODO: Maybe this needs to be changed later on.
		if(jsonObject.getString(PARAM_RESULT_CODE).charAt(0) == 'S'){
			//Login succeed!
			Log.d("BgDB", "Device registration completed: "+jsonObject.getString(PARAM_RESULT_MSG));

		} else {
			Log.d("BgDB", "Device registration failed: "+jsonObject.getString(PARAM_RESULT_MSG));
		}
		}
		catch(Exception e){
			Log.d("BgDB", "sendThisDeviceDetail Exception: "+e.getLocalizedMessage());
		}
	}
}
