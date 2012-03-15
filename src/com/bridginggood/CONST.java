package com.bridginggood;

public class CONST {
	private static final String API_URL = "http://api.bridginggood.com:8080";
	
	//SplashController: Splash time
	public static final long SPLASH_DELAY = 1500;
	
	//Lazy Loading Image cache folder
	public static final String LAZY_LOAD_IMAGE_CACHE = "data/BridgingGood";
	
	//C2DM Registered account
	public static final String C2DM_SENDER = "admin@bridginggood.com";
	
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
	public static final String API_LOGIN_BY_TOKEN_URL = API_URL+"/auth/LoginByTokenFromMobile.json";
	public static final String API_LOGIN_BY_FACEBOOK_URL = API_URL+"/auth/LoginByFacebookFromMobile.json";
	public static final String API_LOGIN_BY_BG_URL = API_URL+"/auth/LoginByUserFromMobile.json";

	public static final String API_CREATE_C2DM_REGISTRATION_ID_URL = API_URL+"/auth/CreatePushNotificationAndroid.json";
	public static final String API_CREATE_QRCODE_URL = API_URL+"/auth/CreateQRCode.json";
	
	public static final String API_READ_BUSINESS_LIST_URL = API_URL+"/business_info/readlist.json";
	public static final String API_READ_BUSINESS_MAP_URL = API_URL+"/business_info/readmap.json";
}
