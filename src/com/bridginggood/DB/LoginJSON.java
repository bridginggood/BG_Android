/*
 * Static class to perform login
 */
package com.bridginggood.DB;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONObject;

import android.util.Log;

import com.bridginggood.CONST;
import com.bridginggood.UserInfo;

public class LoginJSON {

	private static final String PARAM_USER_EMAIL = "UserEmail";
	private static final String PARAM_USER_FIRSTNAME = "UserFirstName";
	private static final String PARAM_USER_LASTNAME = "UserLastName";
	private static final String PARAM_USER_PASSWORD = "UserPassword";
	private static final String PARAM_USER_TYPE = "UserType";
	private static final String PARAM_TOKEN_STRING = "TokenString";
	private static final String PARAM_PHONE_TYPE = "PhoneType";

	private static final String PARAM_RESULT_CODE = "resultCode";
	private static final String PARAM_RESULT_MSG = "resultMsg";

	/*
	 * returns true if login is successful
	 */
	public static boolean loginUserSession(final int LoginType){
		try {
			HttpURLConnection http = null;
			URL url = null;
			BufferedReader postRes = null;
			String postData = "";
			StringBuilder json = new StringBuilder();
			String line="";
			int responseCode=0;

			switch(LoginType)
			{
			case CONST.LOGIN_TYPE_BG:
				url = new URL(CONST.LOGIN_BY_BG_URL);
				postData += URLEncoder.encode(PARAM_USER_EMAIL)+"="+URLEncoder.encode(UserInfo.getUserEmail())+"&";
				postData += URLEncoder.encode(PARAM_USER_PASSWORD)+"="+URLEncoder.encode(UserInfo.getUserPassword())+"&";
				break;
			case CONST.LOGIN_TYPE_FACEBOOK:
				url = new URL(CONST.LOGIN_BY_FACEBOOK_URL);
				postData += URLEncoder.encode(PARAM_USER_EMAIL)+"="+URLEncoder.encode(UserInfo.getUserEmail())+"&";
				postData += URLEncoder.encode(PARAM_USER_FIRSTNAME)+"="+URLEncoder.encode(UserInfo.getUserFirstName())+"&";
				postData += URLEncoder.encode(PARAM_USER_LASTNAME)+"="+URLEncoder.encode(UserInfo.getUserLastName())+"&";
				break;
			case CONST.LOGIN_TYPE_TOKEN:
				url = new URL(CONST.LOGIN_BY_TOKEN_URL);
				postData += URLEncoder.encode(PARAM_TOKEN_STRING)+"="+URLEncoder.encode(UserInfo.getTokenString())+"&";
				break;
			}

			if (url.getProtocol().toLowerCase().equals("https")) {
				trustAllHosts();
				HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
				https.setHostnameVerifier(DO_NOT_VERIFY);
				http = https;
			} else {
				http = (HttpURLConnection) url.openConnection();
			}

			Log.d("BG", "POSTDATA: "+postData);

			byte[] bytes = postData.getBytes("UTF-8");
	        http.setRequestProperty("Content-Length", String.valueOf(bytes.length));
	        http.setRequestMethod("POST");
	        http.setDoOutput(true);
	        http.connect();
	        OutputStream outputStream = http.getOutputStream();
	        outputStream.write(bytes);
	        Log.d("BG", "Bytes: "+bytes.toString());
	        outputStream.close();
	        
			responseCode = http.getResponseCode();
			if(responseCode < 400){
				postRes = new BufferedReader(new InputStreamReader(http.getInputStream(), "UTF-8"));
				while ((line = postRes.readLine()) != null){
					json.append(line);
				}
				postRes.close();
			}
			else{
				Log.d("BG", "ResponseCode error: "+responseCode+" . "+http.getErrorStream().toString()+" . "+http.getResponseMessage());
			}
			

			Log.d("BG", "HTTPS received: "+json.toString());

			JSONObject jsonObject = new JSONObject(json.toString());

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
		UserInfo.setTokenString(jsonObject.getString(PARAM_TOKEN_STRING));
		UserInfo.setPhoneType(jsonObject.getString(PARAM_PHONE_TYPE));
		
		UserInfo.setUserPassword(null);	//Nullify for security reason?!
		
		Log.d("BG", "UserInfo updated:"+UserInfo.getUserEmail()+" ,"+UserInfo.getUserFirstName()+","+
				UserInfo.getUserLastName()+", "+UserInfo.getUserType());
	}

	// always verify the host - dont check for certificate
	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	/**
	 * Trust every server - dont check for any certificate
	 */
	private static void trustAllHosts() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}

			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
			.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
