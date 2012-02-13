package com.bridginggood.DB;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.bridginggood.CONST;
import com.bridginggood.Biz.Business;


public class BusinessJSON {
	private static final String REQUEST_PARAM_LATITUDE = "lat";
	private static final String REQUEST_PARAM_LONGITUDE = "lng";
	private static final String REQUEST_PARAM_DISTANCE = "dist";

	private static final String REPLY_PARAM_BUSINESS_ID = "BusinessId";
	private static final String REPLY_PARAM_BUSINESS_NAME = "BusinessName";
	private static final String REPLY_PARAM_BUSINESS_ADDRESS = "BusinessAddress";
	private static final String REPLY_PARAM_BUSINESS_LATITUDE = "Latitude";
	private static final String REPLY_PARAM_BUSINESS_LONGITUDE = "Longitude";
	private static final String REPLY_PARAM_BUSINESS_CHARITYID = "CharityId";
	private static final String REPLY_PARAM_BUSINESS_DISTANCE = "distance";

	private float mMyLat, mMyLng, mDistanceRadius;

	public BusinessJSON (float myLat, float myLng, float distanceRadius){
		this.mMyLat = myLat;
		this.mMyLng = myLng;
		this.mDistanceRadius = distanceRadius;		//in miles
	}

	public ArrayList<Business> getBizListJSON(){
		ArrayList<Business> bizList = new ArrayList<Business>();
		try {
			String[][] paramData = {{REQUEST_PARAM_LATITUDE, mMyLat+""}, 
					{REQUEST_PARAM_LONGITUDE, mMyLng+""}, 
					{REQUEST_PARAM_DISTANCE, mDistanceRadius+""}};
			String paramStr = BgHttpHelper.generateParamData(paramData);
			String jsonStr = BgHttpHelper.requestHttpRequest(CONST.API_READ_BUSINESS_LIST_URL, paramStr, "GET");

			//If retreived empty string, return empty ArrayList
			if (jsonStr.equals("[]") || jsonStr.equals("{}"))
				return bizList;
			
			JSONArray jsonArray = new JSONArray(jsonStr);

			Business biz = null;
			for(int i=0;i<jsonArray.length();i++){
				JSONObject jsonObject = (JSONObject) jsonArray.get(i);

				String bid = jsonObject.getString(REPLY_PARAM_BUSINESS_ID);
				String name = jsonObject.getString(REPLY_PARAM_BUSINESS_NAME);
				String address = jsonObject.getString(REPLY_PARAM_BUSINESS_ADDRESS);
				float lat = Float.parseFloat(jsonObject.getString(REPLY_PARAM_BUSINESS_LATITUDE));
				float lng = Float.parseFloat(jsonObject.getString(REPLY_PARAM_BUSINESS_LONGITUDE));
				String cid = jsonObject.getString(REPLY_PARAM_BUSINESS_CHARITYID);
				float distanceAway = Float.parseFloat(jsonObject.getString(REPLY_PARAM_BUSINESS_DISTANCE));

				biz = new Business(bid, 0, name, address, lat, lng, cid, distanceAway);
			}
			bizList.add(biz); //add to ArrayList

		} catch (Exception e) {
			Log.d("BG", "getBizListJSON Exception:"+e.getLocalizedMessage());
		}
		return bizList;
	}
}
