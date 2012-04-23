package com.bridginggood.DB;

import org.json.JSONObject;

import android.util.Log;

import com.bridginggood.CONST;
import com.bridginggood.UserInfo;

public class QRCodeJSON {
	private static final String PARAM_USER_ID = "UserId";
	private static final String PARAM_QR_ID = "QrId";
	private static final String PARAM_RESULT_CODE = "resultCode";
	private static final String PARAM_RESULT_MSG = "resultMsg";

	/**
	 * 
	 */
	public static boolean createQRCode(){
		try {
			String targetURL = CONST.API_CREATE_QRCODE_URL;
			String requestParam = "";

			String[][] param = {{PARAM_USER_ID, UserInfo.getUserId()+""}};
			requestParam = BgHttpHelper.generateParamData(param);

			String jsonStr = BgHttpHelper.requestHttpRequest(targetURL, requestParam, "POST");

			JSONObject jsonObject = new JSONObject(jsonStr);

			if(jsonObject.getString(PARAM_RESULT_CODE).charAt(0) == 'S'){
				//QRCode generated! 
				UserInfo.setQRCodeURL(jsonObject.getString(PARAM_QR_ID));
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
	
	public static String registerQRCode(String qrId){
		try {
			String targetURL = CONST.API_REGISTER_QRCODE_URL;
			String requestParam = "";

			String[][] param = {{PARAM_USER_ID, UserInfo.getUserId()+""}, {PARAM_QR_ID, qrId}};
			requestParam = BgHttpHelper.generateParamData(param);

			String jsonStr = BgHttpHelper.requestHttpRequest(targetURL, requestParam, "POST");

			JSONObject jsonObject = new JSONObject(jsonStr);

			if(jsonObject.getString(PARAM_RESULT_CODE).charAt(0) == 'S'){
				//QRCode generated! 
				UserInfo.setQRCodeURL(jsonObject.getString(PARAM_QR_ID));
				return "Success";
			} else if (jsonObject.getString(PARAM_RESULT_CODE).equals("E001")){
				return "Exist";
			} else{
				Log.d("BgDB", "Login failed: "+jsonObject.getString(PARAM_RESULT_MSG));
				return "Failed";
			}
		} catch (Exception e){
			// Handle all exception
			Log.d("BgDB", "Exception occured: "+e.getLocalizedMessage());
		}
		return "Failed";
	}
}
