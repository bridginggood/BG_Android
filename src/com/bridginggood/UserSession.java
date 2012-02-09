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
	private boolean mIsRememberLogin;

	public UserSession(){
		setLoginToken(null);
		setIsRememberLogin(false);
	}

	public UserSession(String loginToken, boolean isRememberLogin){
		setLoginToken(loginToken);
		setIsRememberLogin(isRememberLogin);
	}

	public String getLoginToken() {
		return mLoginToken;
	}
	public void setLoginToken(String loginToken) {
		this.mLoginToken = loginToken;
	}
	public boolean getIsRememberLogin() {
		return mIsRememberLogin;
	}
	public void setIsRememberLogin(boolean isRememberLogin) {
		this.mIsRememberLogin = isRememberLogin;
	}
	
	/**
	 * 
	 * @return false if login token does not exist.
	 */
	public boolean isEmptySession(){
		return (getLoginToken()!=null);
	}
}
