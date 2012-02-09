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
	private String mEmail;
	private String mPassword;
	private boolean mIsRememberLogin;

	public UserSession(){
		setLoginToken(null);
		setEmail(null);
		setPassword(null);
		setIsRememberLogin(false);
	}

	/*
	 * Session by Login Token
	 */
	public UserSession(String loginToken, boolean isRememberLogin){
		setLoginToken(loginToken);
		setEmail(null);
		setPassword(null);
		setIsRememberLogin(isRememberLogin);
	}
	
	/*
	 * Session by email and apssword
	 */
	public UserSession(String email, String password, boolean isRememberLogin){
		setLoginToken(null);
		setEmail(email);
		setPassword(password);
		setIsRememberLogin(isRememberLogin);
	}
	
	/**
	 * 
	 * @return true if login token and email does not exist
	 */
	public boolean isEmptySession(){
		return !(getLoginToken()==null && getEmail()==null);
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

	public String getPassword() {
		return mPassword;
	}

	public void setPassword(String mPassword) {
		this.mPassword = mPassword;
	}

	public String getEmail() {
		return mEmail;
	}

	public void setEmail(String mEmail) {
		this.mEmail = mEmail;
	}
}
