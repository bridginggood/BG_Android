package com.bridginggood.DB;

import org.json.JSONObject;

import android.util.Log;

import com.bridginggood.CONST;
import com.bridginggood.UserInfo;

public class StatsJSON {
	private static final String PARAM_USER_ID = "UserId";
	private static final String PARAM_TOTAL = "Total";
	private static final String PARAM_RESULT_CODE = "resultCode";
	private static final String PARAM_RESULT_MSG = "resultMsg";

	public static String getTotalDonationAmount(){
		try {
			String targetURL = CONST.API_STATS_TOTAL_AMOUNT_DONATED;
			String requestParam = "";

			String[][] param = {{PARAM_USER_ID, UserInfo.getUserId()+""}};
			requestParam = BgHttpHelper.generateParamData(param);

			String jsonStr = BgHttpHelper.requestHttpRequest(targetURL, requestParam, "POST");

			JSONObject jsonObject = new JSONObject(jsonStr);

			if(jsonObject.getString(PARAM_RESULT_CODE).charAt(0) == 'S'){
				return jsonObject.getString(PARAM_TOTAL);
			} else {
				Log.d("BgDB", "Login failed: "+jsonObject.getString(PARAM_RESULT_MSG));
				return null;
			}
		} catch (Exception e){
			// Handle all exception
			Log.d("BgDB", "Exception occured: "+e.getLocalizedMessage());
		}
		return null;
	}
}
