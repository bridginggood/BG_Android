package com.bridginggood.DB;

import org.json.JSONObject;

import android.util.Log;

import com.bridginggood.CONST;
import com.bridginggood.UserInfo;

public class LogJSON {
	private static final String PARAM_USER_ID = "UserId";
	private static final String PARAM_BUSINESS_ID = "BusinessId";
	private static final String PARAM_FACEBOOK = "Facebook";
	private static final String PARAM_RESULT_CODE = "resultCode";
	private static final String PARAM_RESULT_MSG = "resultMsg";

	public static boolean createSNSLog(String businessId, String facebookPosted){
		try {
			String targetURL = CONST.API_LOG_SNS_URL;
			String requestParam = "";

			String[][] param = {{PARAM_USER_ID, UserInfo.getUserId()+""},
					{PARAM_BUSINESS_ID, businessId},
					{PARAM_FACEBOOK, facebookPosted}};
			requestParam = BgHttpHelper.generateParamData(param);

			String jsonStr = BgHttpHelper.requestHttpRequest(targetURL, requestParam, "POST");

			JSONObject jsonObject = new JSONObject(jsonStr);

			if(jsonObject.getString(PARAM_RESULT_CODE).charAt(0) == 'S'){
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
}
