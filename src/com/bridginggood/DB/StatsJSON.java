package com.bridginggood.DB;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.bridginggood.CONST;
import com.bridginggood.UserInfo;
import com.bridginggood.ValuePair;

public class StatsJSON {
	private static final String PARAM_USER_ID = "UserId";
	private static final String PARAM_TOTAL = "Total";
	private static final String PARAM_RESULT_CODE = "resultCode";
	private static final String PARAM_RESULT_MSG = "resultMsg";
	private static final String PARAM_BUSINESS_NAME = "BusinessName";
	private static final String PARAM_CHARITY_NAME = "CharityName";
	private static final String PARAM_CHARITY_ID = "CharityId";
	private static final String PARAM_BUSINESS_ID = "BusinessId";
	private static final String PARAM_CHARITY_TOTAL_DONATION = "DonationAmount";
	private static final String PARAM_CHARITY_PEOPLE = "People";
	private static final String PARAM_CHARITY_REMAINING_DAYS = "RemainingDays";
	private static final String PARAM_BUSINESS_DESCRIPTION = "BusinessDescription";


	private static final int BY_CHARITY = 0;

	public static String getUserTotalDonationAmount(){
		try {
			String targetURL = CONST.API_STATS_TOTAL_AMOUNT_DONATED_URL;
			String requestParam = "";

			String[][] param = {{PARAM_USER_ID, UserInfo.getUserId()+""}};
			requestParam = BgHttpHelper.generateParamData(param);

			String jsonStr = BgHttpHelper.requestHttpRequest(targetURL, requestParam, "POST");

			//If retreived empty string, return empty ArrayList
			if (jsonStr.equals("[]") || jsonStr.equals("{}"))
				return null;

			JSONArray jsonArray = new JSONArray(jsonStr);
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);

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

	public static ArrayList<ValuePair<String, String>> getUserDonationAmount(int type){
		ArrayList<ValuePair<String, String>> dataArrayList = new ArrayList<ValuePair<String, String>>();
		try {
			String targetURL;
			if (type == BY_CHARITY)
				targetURL = CONST.API_STATS_DONATION_BY_CHARITY_URL;
			else
				targetURL = CONST.API_STATS_DONATION_BY_PLACE_URL;

			String requestParam = "";

			String[][] param = {{PARAM_USER_ID, UserInfo.getUserId()+""}};
			requestParam = BgHttpHelper.generateParamData(param);

			String jsonStr = BgHttpHelper.requestHttpRequest(targetURL, requestParam, "POST");

			//If retreived empty string, return empty ArrayList
			if (jsonStr.equals("[]") || jsonStr.equals("{}"))
				return null;

			JSONArray jsonArray = new JSONArray(jsonStr);
			for(int i=0;i<jsonArray.length();i++){
				JSONObject jsonObject = (JSONObject) jsonArray.get(i);
				if(jsonObject.getString(PARAM_RESULT_CODE).charAt(0) == 'S'){
					//Retrieve data
					String name;
					if (type == BY_CHARITY )	//By Charity
						name = jsonObject.getString(PARAM_CHARITY_NAME);
					else //By Place
						name = jsonObject.getString(PARAM_BUSINESS_NAME);

					String amount = "$"+jsonObject.getString(PARAM_TOTAL);

					//Add to list
					dataArrayList.add(new ValuePair<String, String>(name, amount));
				} else {
					Log.d("BgDB", "Login failed: "+jsonObject.getString(PARAM_RESULT_MSG));
					return null;
				}	
			}
			return dataArrayList;
		} catch (Exception e){
			// Handle all exception
			Log.d("BgDB", "Exception occured: "+e.getLocalizedMessage());
		}
		return null;
	}

	public static String[] getCharityDonation(String charityId){
		String[] resultArray = new String[4];
		try {
			String targetURL = CONST.API_STATS_CHARITY_DONATION_DETAIL_URL;

			String requestParam = "";

			String[][] param = {{PARAM_CHARITY_ID, charityId}};
			requestParam = BgHttpHelper.generateParamData(param);

			String jsonStr = BgHttpHelper.requestHttpRequest(targetURL, requestParam, "POST");

			//If retreived empty string, return empty ArrayList
			if (jsonStr.equals("[]") || jsonStr.equals("{}"))
				return null;

			JSONArray jsonArray = new JSONArray(jsonStr);
			JSONObject jsonObject = (JSONObject) jsonArray.get(0);

			if(jsonObject.getString(PARAM_RESULT_CODE).charAt(0) == 'S'){
				resultArray[0] = "$"+jsonObject.getString(PARAM_CHARITY_TOTAL_DONATION);
				resultArray[1] = jsonObject.getString(PARAM_CHARITY_PEOPLE);
				resultArray[2] = jsonObject.getString(PARAM_CHARITY_REMAINING_DAYS);

				return resultArray;
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

	public static String[] getThankyouDetail(String businessId){
		String[] data = new String[4];
		try{
			String targetURL = CONST.API_STATS_THANKYOU_DETAIL_URL;
			String[][] param = {{PARAM_BUSINESS_ID, businessId}};
			String requestParam = BgHttpHelper.generateParamData(param);

			String jsonStr = BgHttpHelper.requestHttpRequest(targetURL, requestParam, "POST");

			JSONObject jsonObject = new JSONObject(jsonStr);

			if(jsonObject.getString(PARAM_RESULT_CODE).charAt(0) == 'S'){
				data[0] = jsonObject.getString(PARAM_BUSINESS_NAME);
				data[1] = jsonObject.getString(PARAM_BUSINESS_DESCRIPTION);
				data[2] = jsonObject.getString(PARAM_CHARITY_NAME);
				data[3] = jsonObject.getString(PARAM_TOTAL);
				return data;
			} else {
				Log.d("BgDB", "getThankyouDetail failed: "+jsonObject.getString(PARAM_RESULT_MSG));
				return null;
			}
		}
		catch(Exception e){
			Log.d("BgDB", "getThankyouDetail Exception: "+e.getLocalizedMessage());
		}
		return null;
	}
}
