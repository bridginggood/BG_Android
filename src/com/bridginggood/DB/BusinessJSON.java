package com.bridginggood.DB;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.bridginggood.CONST;
import com.bridginggood.Biz.Business;


public class BusinessJSON {
	private static final String PARAM_LATITUDE = "lat";
	private static final String PARAM_LONGITUDE = "lng";
	private static final String PARAM_PAGE = "page";
	private static final String PARAM_DISTANCE = "dist";

	private static final String PARAM_BUSINESS_ID = "BusinessId";
	private static final String PARAM_BUSINESS_NAME = "BusinessName";
	private static final String PARAM_BUSINESS_ADDRESS = "BusinessAddress";
	private static final String PARAM_BUSINESS_LATITUDE = "Latitude";
	private static final String PARAM_BUSINESS_LONGITUDE = "Longitude";
	private static final String PARAM_BUSINESS_CHARITYID = "CharityId";
	private static final String PARAM_BUSINESS_DISTANCE = "distance";
	private static final String PARAM_BUSINESS_DESCRIPTION =  "BusinessDescription";

	private float mMyLat, mMyLng, mDistance;
	private int mPage;

	public BusinessJSON (float myLat, float myLng, int page){
		this.mMyLat = myLat;
		this.mMyLng = myLng;
		this.mPage = page;
	}

	public BusinessJSON (float myLat, float myLng, float mDistance){
		this.mMyLat = myLat;
		this.mMyLng = myLng;
		this.mDistance = mDistance;
	}

	public ArrayList<Business> getBizListJSON(){
		ArrayList<Business> bizList = new ArrayList<Business>();
		try {
			String[][] paramData = {{PARAM_LATITUDE, mMyLat+""}, 
					{PARAM_LONGITUDE, mMyLng+""}, 
					{PARAM_PAGE, mPage+""}};
			String paramStr = BgHttpHelper.generateParamData(paramData);
			String jsonStr = BgHttpHelper.requestHttpRequest(CONST.API_READ_BUSINESS_LIST_URL, paramStr, "GET");

			//If retreived empty string, return empty ArrayList
			if (jsonStr.equals("[]") || jsonStr.equals("{}"))
				return null;

			JSONArray jsonArray = new JSONArray(jsonStr);

			Business biz = null;
			for(int i=0;i<jsonArray.length();i++){
				JSONObject jsonObject = (JSONObject) jsonArray.get(i);

				String bid = jsonObject.getString(PARAM_BUSINESS_ID);
				String name = jsonObject.getString(PARAM_BUSINESS_NAME);
				String address = jsonObject.getString(PARAM_BUSINESS_ADDRESS);
				String description = jsonObject.getString(PARAM_BUSINESS_DESCRIPTION);
				float lat = Float.parseFloat(jsonObject.getString(PARAM_BUSINESS_LATITUDE));
				float lng = Float.parseFloat(jsonObject.getString(PARAM_BUSINESS_LONGITUDE));
				String cid = jsonObject.getString(PARAM_BUSINESS_CHARITYID);
				float distanceAway = Float.parseFloat(jsonObject.getString(PARAM_BUSINESS_DISTANCE));

				biz = new Business(bid, 0, name, address, description, lat, lng, cid, distanceAway);
				bizList.add(biz); //add to ArrayList
				Log.d("BgDB", "Business object created. BID: "+bid);
			}
		} catch (Exception e) {
			Log.d("BgDB", "getBizListJSON Exception:"+e.getLocalizedMessage());
		}
		return bizList;
	}

	public ArrayList<Business> getBizMapJSON(){
		ArrayList<Business> bizList = new ArrayList<Business>();
		try {
			String[][] paramData = {{PARAM_LATITUDE, mMyLat+""}, 
					{PARAM_LONGITUDE, mMyLng+""}, 
					{PARAM_DISTANCE, mDistance+""}};
			String paramStr = BgHttpHelper.generateParamData(paramData);
			String jsonStr = BgHttpHelper.requestHttpRequest(CONST.API_READ_BUSINESS_MAP_URL, paramStr, "GET");

			//If retreived empty string, return empty ArrayList
			if (jsonStr.equals("[]") || jsonStr.equals("{}"))
				return null;

			JSONArray jsonArray = new JSONArray(jsonStr);

			Business biz = null;
			for(int i=0;i<jsonArray.length();i++){
				JSONObject jsonObject = (JSONObject) jsonArray.get(i);

				String bid = jsonObject.getString(PARAM_BUSINESS_ID);
				String name = jsonObject.getString(PARAM_BUSINESS_NAME);
				String address = jsonObject.getString(PARAM_BUSINESS_ADDRESS);
				String description = jsonObject.getString(PARAM_BUSINESS_DESCRIPTION);
				float lat = Float.parseFloat(jsonObject.getString(PARAM_BUSINESS_LATITUDE));
				float lng = Float.parseFloat(jsonObject.getString(PARAM_BUSINESS_LONGITUDE));
				String cid = jsonObject.getString(PARAM_BUSINESS_CHARITYID);
				float distanceAway = Float.parseFloat(jsonObject.getString(PARAM_BUSINESS_DISTANCE));

				biz = new Business(bid, 0, name, address, description, lat, lng, cid, distanceAway);
				bizList.add(biz); //add to ArrayList
				Log.d("BgDB", "Business object created. BID: "+bid);
			}
		} catch (Exception e) {
			Log.d("BgDB", "getBizMapJSON Exception:"+e.getLocalizedMessage());
		}
		return bizList;
	}
}