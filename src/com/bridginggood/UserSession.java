/*
 * Created By: Junsung Lim
 * 
 * Description:
 * 	This class is used to create User Session on user's login page.
 * 	The class is called to store login credentials to SharedPreference.
 * 
 */
package com.bridginggood;

public class UserSession {
	private String mLoginToken;
	private String mType;

	public UserSession(){
		setLoginToken(null);
		setType(null);
	}

	/*
	 * Constructor
	 */
	public UserSession(String loginToken, String type){
		setLoginToken(loginToken);
		setType(type);
	}
	
	public boolean isEmptySession(){
		return (getLoginToken()==null);
	}

	public String getType() {
		return mType;
	}

	public void setType(String mType) {
		this.mType = mType;
	}

	public String getLoginToken() {
		return mLoginToken;
	}

	public void setLoginToken(String mLoginToken) {
		this.mLoginToken = mLoginToken;
	}
}
