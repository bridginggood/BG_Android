/*
 * Created By: Junsung Lim
 * 
 * Description:
 * 	This class is used to create User Session on user's login page.
 * 	The class creates user session accordingly and perform login too.
 * 
 */
package com.bridginggood;

import com.bridginggood.DB.LoginJSON;

public class UserSession {
	private String mLoginToken, mType, mEmail, mPassword, mFirstName, mLastName;

	public UserSession(){
		setLoginToken(null);
		setType(null);
		setEmail(null);
		setPassword(null);
		setFirstName(null);
		setLastName(null);
	}

	public void createUserSessionForFacebook(String email, String firstname, String lastname, String type){
		setEmail(email);
		setFirstName(firstname);
		setLastName(lastname);
		setType(type);
	}
	
	public void createUserSessionForToken(String loginToken, String type){
		setLoginToken(loginToken);
		setType(type);
	}
	
	public void createUserSessionForBG(String email, String password, String type){
		setEmail(email);
		setPassword(password);
		setType(type);
	}
	
	/**
	 * Depending on the data available on UserSession, 
	 * call appropriate methods to perform login.
	 * 
	 * If type is FACEBOOK, directs user to facebook login method no matter what.
	 * Else, if Login Token exists, directs to token login method.
	 * Otherwise, go to BG login method.
	 * 
	 *@return true if login was successful.  
	 */
	public boolean loginUserSession(){
		if (getType().equals(CONST.USER_SESSION_TYPE_FACEBOOK))
		{
			//Go to Facebook Login
			return LoginJSON.loginUserSession(this, CONST.LOGIN_TYPE_FACEBOOK);
		}
		else if (getLoginToken() != null){
			//Go to token login
			return LoginJSON.loginUserSession(this, CONST.LOGIN_TYPE_TOKEN);
		}
		else{
			//Go to BG login
			return LoginJSON.loginUserSession(this, CONST.LOGIN_TYPE_BG);
		}
	}
	
	//Returns whether the login token is empty or not
	public boolean isSessionTokenEmpty(){
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

	public String getLastName() {
		return mLastName;
	}

	public void setLastName(String mLastName) {
		this.mLastName = mLastName;
	}

	public String getFirstName() {
		return mFirstName;
	}

	public void setFirstName(String mFirstName) {
		this.mFirstName = mFirstName;
	}
}
