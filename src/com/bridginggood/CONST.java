package com.bridginggood;

public class CONST {
	//SplashController: Splash time
	public static final long SPLASH_DELAY = 1500;
	
	//Facebook APP ID
	public static final String FACEBOOK_APP_ID = "176588365777862";
	public static final String[] FACEBOOK_PERMISSION = {"email", "publish_checkins"};

	//For login: UserType element
	public static final String USER_SESSION_TYPE_FACEBOOK = "FBOOK";
	public static final String USER_SESSION_TYPE_BG = "BGOOD";
	public static final int LOGIN_TYPE_BG = 0;
	public static final int LOGIN_TYPE_FACEBOOK = 1;
	public static final int LOGIN_TYPE_TOKEN = 2;
	
	//For BridgingGood Database API
	public static final String LOGIN_BY_TOKEN_URL = "https://api.bridginggood.com:8080/auth/LoginByTokenFromMobile.json";
	public static final String LOGIN_BY_FACEBOOK_URL = "https://api.bridginggood.com:8080/auth/LoginByFacebookFromMobile.json";
	public static final String LOGIN_BY_BG_URL = "https://api.bridginggood.com:8080/auth/LoginByUserFromMobile.json";
	
	public static final String READ_BUSINESS_LIST_URL = "";
}
