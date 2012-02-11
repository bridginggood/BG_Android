/*
 * Static class to perform login
 */
package com.bridginggood.DB;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.util.Log;

import com.bridginggood.CONST;
import com.bridginggood.UserInfo;
import com.bridginggood.UserSession;

public class LoginJSON {

	private static final String PARAM_USER_EMAIL = "UserEmail";
	private static final String PARAM_USER_FIRSTNAME = "UserFirstName";
	private static final String PARAM_USER_LASTNAME = "UserLastName";
	private static final String PARAM_USER_PASSWORD = "UserPassword";
	private static final String PARAM_USER_TYPE = "UserType";
	private static final String PARAM_TOKEN_STRING = "TokenString";

	private static final String PARAM_RESULT_CODE = "resultCode";
	private static final String PARAM_RESULT_MSG = "resultMsg";

	/*
	 * returns true if login is successful
	 */
	public static boolean loginUserSession(final UserSession userSession, final int LoginType){
		try {
			
			// Create a new HttpClient and Post Header
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = null;

			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			
			switch(LoginType)
			{
			case CONST.LOGIN_TYPE_BG:
				httpPost = new HttpPost(CONST.LOGIN_BY_BG_URL);
				nameValuePairs.add(new BasicNameValuePair(PARAM_USER_EMAIL, userSession.getEmail()));
				nameValuePairs.add(new BasicNameValuePair(PARAM_USER_PASSWORD, userSession.getPassword()));
				break;
			case CONST.LOGIN_TYPE_FACEBOOK:
				httpPost = new HttpPost(CONST.LOGIN_BY_FACEBOOK_URL);
				nameValuePairs.add(new BasicNameValuePair(PARAM_USER_EMAIL, userSession.getEmail()));
				nameValuePairs.add(new BasicNameValuePair(PARAM_USER_FIRSTNAME, userSession.getFirstName()));
				nameValuePairs.add(new BasicNameValuePair(PARAM_USER_LASTNAME, userSession.getLastName()));
				break;
			case CONST.LOGIN_TYPE_TOKEN:
				httpPost = new HttpPost(CONST.LOGIN_BY_TOKEN_URL);
				nameValuePairs.add(new BasicNameValuePair(PARAM_TOKEN_STRING, userSession.getLoginToken()));
				break;
			}
			
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpClient.execute(httpPost);
			JSONObject jsonObject = new JSONObject(response.toString());

			//TODO: Maybe this needs to be changed later on.
			if(jsonObject.getString(PARAM_RESULT_CODE).charAt(0) == 'S'){
				//Login succeed!
				updateUserInfoWithJSON(jsonObject);
				return true;
			} else {
				Log.d("BG", "Login failed: "+jsonObject.getString(PARAM_RESULT_MSG));
				return false;
			}
		} catch (Exception e){
			// Handle all exception
			Log.d("BG", "Exception occured: "+e.getLocalizedMessage());
		}
		return false;
	}

	private static void updateUserInfoWithJSON(JSONObject jsonObject) throws Exception{
		UserInfo.setUserEmail(jsonObject.getString(PARAM_USER_EMAIL));
		UserInfo.setUserFirstName(jsonObject.getString(PARAM_USER_FIRSTNAME));
		UserInfo.setUserLastName(jsonObject.getString(PARAM_USER_LASTNAME));
		UserInfo.setUserType(jsonObject.getString(PARAM_USER_TYPE));
	}
}
