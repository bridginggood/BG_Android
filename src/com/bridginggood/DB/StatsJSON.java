package com.bridginggood.DB;

import java.text.DecimalFormat;
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
	
	private static final int BY_CHARITY = 0;

	public static String getTotalDonationAmount(){
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
	
	public static ArrayList<ValuePair<String, String>> getDonationAmount(int type){
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
					
					String amount = jsonObject.getString(PARAM_TOTAL);
					
					//Change amount format
					DecimalFormat dFormat = new DecimalFormat("#0.00");
					amount = "$" + dFormat.format(Float.parseFloat(amount));
					
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
	/*
	public static ArrayList<ValuePair<String, String>> getDonationAmountByPlace(){
		ArrayList<ValuePair<String, String>> dataArrayList = new ArrayList<ValuePair<String, String>>();
		try {
			String targetURL = CONST.API_STATS_DONATION_BY_PLACE;
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
					String businessName = jsonObject.getString(PARAM_BUSINESS_NAME);
					String amount = jsonObject.getString(PARAM_TOTAL);
					
					//Change amount format
					DecimalFormat dFormat = new DecimalFormat("#0.00");
					amount = "$" + dFormat.format(Float.parseFloat(amount));
					
					//Add to list
					dataArrayList.add(new ValuePair<String, String>(businessName, amount));
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
	}*/
}
