/**
 * Created By: Junsung Lim
 * 
 * Calls server for user login. 
 * Calls to server are done in POST method.
 * Retrieves JSON from the server in return.
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

public class UserLoginJSON {

	private static final String PARAM_USER_EMAIL = "UserEmail";
	private static final String PARAM_USER_FIRSTNAME = "UserFirstName";
	private static final String PARAM_USER_LASTNAME = "UserLastName";
	private static final String PARAM_USER_PASSWORD = "UserPassword";
	private static final String PARAM_USER_TYPE = "UserType";
	private static final String PARAM_TOKEN_STRING = "TokenString";
	private static final String PARAM_PHONE_TYPE = "PhoneType";

	private static final String PARAM_RESULT_CODE = "resultCode";
	private static final String PARAM_RESULT_MSG = "resultMsg";

	private static final int RETRY_LIMIT = 3;

	/**
	 * Interacts with server to do user login.
	 * 
	 * @param LoginType Type of the user. Refer to CONST.java
	 * 
	 * @return True upon successful login
	 */
	public static boolean loginUser(final int LoginType){
		try {
			HttpURLConnection httpURLConnection = null;
			URL targetURL = null;
			BufferedReader postRes = null;
			String postData = "", line="";
			StringBuilder jsonStringBuilder = new StringBuilder();
			int responseCode=0, retryAttempt=0;

			// Make POST dataset
			switch(LoginType)
			{
			case CONST.LOGIN_TYPE_BG:
				targetURL = new URL(CONST.LOGIN_BY_BG_URL);
				postData += URLEncoder.encode(PARAM_USER_EMAIL)+"="+URLEncoder.encode(UserInfo.getUserEmail())+"&";
				postData += URLEncoder.encode(PARAM_USER_PASSWORD)+"="+URLEncoder.encode(UserInfo.getUserPassword())+"&";
				break;
			case CONST.LOGIN_TYPE_FACEBOOK:
				targetURL = new URL(CONST.LOGIN_BY_FACEBOOK_URL);
				postData += URLEncoder.encode(PARAM_USER_EMAIL)+"="+URLEncoder.encode(UserInfo.getUserEmail())+"&";
				postData += URLEncoder.encode(PARAM_USER_FIRSTNAME)+"="+URLEncoder.encode(UserInfo.getUserFirstName())+"&";
				postData += URLEncoder.encode(PARAM_USER_LASTNAME)+"="+URLEncoder.encode(UserInfo.getUserLastName())+"&";
				break;
			case CONST.LOGIN_TYPE_TOKEN:
				targetURL = new URL(CONST.LOGIN_BY_TOKEN_URL);
				postData += URLEncoder.encode(PARAM_TOKEN_STRING)+"="+URLEncoder.encode(UserInfo.getTokenString())+"&";
				break;
			}

			//Check if the server is HTTPS or HTTP
			if (targetURL.getProtocol().toLowerCase().equals("https")) {
				trustAllHosts();
				HttpsURLConnection https = (HttpsURLConnection) targetURL.openConnection();
				https.setHostnameVerifier(DO_NOT_VERIFY);
				httpURLConnection = https;
			} else {
				httpURLConnection = (HttpURLConnection) targetURL.openConnection();
			}

			while(retryAttempt < RETRY_LIMIT || responseCode<0){
				//Create httpURLConnection header
				byte[] bytes = postData.getBytes("UTF-8");
				httpURLConnection.setRequestProperty("Content-Length", String.valueOf(bytes.length));
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.connect();

				//Send POST data
				OutputStream outputStream = httpURLConnection.getOutputStream();
				outputStream.write(bytes);
				outputStream.close();

				//Check responseCode to determine if the call to server was successful.
				responseCode = httpURLConnection.getResponseCode();
				Log.d("BG", "ResponseCode: "+responseCode);
				if(responseCode > 0 && responseCode < 400){
					postRes = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
					while ((line = postRes.readLine()) != null){
						jsonStringBuilder.append(line);
					}
					postRes.close();
					Log.d("BG", "JSON Received: "+jsonStringBuilder.toString());
					break;
				}
				else{
					Log.d("BG", "ResponseCode error: "+responseCode+" . "+httpURLConnection.getErrorStream().toString()+" . "+httpURLConnection.getResponseMessage());
				}
				retryAttempt++;
				Log.d("BG","Connection Retry: "+retryAttempt);
			}

			JSONObject jsonObject = new JSONObject(jsonStringBuilder.toString());

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
		UserInfo.setPhoneType(jsonObject.getString(PARAM_PHONE_TYPE));

		UserInfo.setUserPassword(null);	//Nullify for security reason?!

		Log.d("BG", "UserInfo updated:"+UserInfo.getUserEmail()+" ,"+UserInfo.getUserFirstName()+","+
				UserInfo.getUserLastName()+", "+UserInfo.getUserType());
	}

	// always verify the host - Don't check for certificate
	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	/**
	 * Trust every server - Don't check for any SSL certificate
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
