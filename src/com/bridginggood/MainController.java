package com.bridginggood;


import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

import com.bridginggood.Biz.BizActivityGroup;
import com.bridginggood.Charity.CharityActivityGroup;
import com.bridginggood.Setting.SettingActivityGroup;
import com.bridginggood.User.UserActivityGroup;

public class MainController extends TabActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainlayout_view);

		Resources res = getResources(); 	// Resource object to get Drawables
		TabHost tabHost = getTabHost();  	// The activity TabHost
		TabHost.TabSpec spec = null; 		// Resusable TabSpec for each tab
		Intent intent = null;  				// Reusable Intent for each tab

		//User
		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, UserActivityGroup.class);
		spec = tabHost.newTabSpec("user").setIndicator("Account",
				res.getDrawable(android.R.drawable.ic_dialog_info))
				.setContent(intent);
		tabHost.addTab(spec);

		// Charity
		intent = new Intent().setClass(this, CharityActivityGroup.class);
		spec = tabHost.newTabSpec("charity").setIndicator("Charity",
				res.getDrawable(android.R.drawable.ic_dialog_info))
				.setContent(intent);
		tabHost.addTab(spec);

		//Biz
		intent = new Intent().setClass(this, BizActivityGroup.class);
		spec = tabHost.newTabSpec("biz").setIndicator("Search",
				res.getDrawable(android.R.drawable.ic_dialog_info))
				.setContent(intent);
		tabHost.addTab(spec);

		//Setting
		intent = new Intent().setClass(this, SettingActivityGroup.class);
		spec = tabHost.newTabSpec("bg").setIndicator("About",
				res.getDrawable(android.R.drawable.ic_dialog_info))
				.setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(2);

		initC2DMRegistration();
	}

	private void initC2DMRegistration(){
		if(UserInfo.getC2DMRegistrationId()==null){
			Log.d("BG", "Need to register for C2DM!");
			Context context = getApplicationContext();
			Intent registrationIntent = new Intent("com.google.android.c2dm.intent.REGISTER");
			registrationIntent.putExtra("app", PendingIntent.getBroadcast(context, 0, new Intent(), 0));
			registrationIntent.putExtra("sender", CONST.C2DM_SENDER);
			context.startService(registrationIntent);
		}
	}

	/*
	 * Called to extend mFacebook token, if necessary
	 */
	public void onResume() {    
		super.onResume();
		if(UserInfo.mFacebook != null && UserInfo.mFacebook.isSessionValid())
			UserInfo.mFacebook.extendAccessTokenIfNeeded(this, null);
	}
	public void onDestory(){
		super.onDestroy();
		Log.d("BG", "onDestroy called");
		System.exit(0);
	}
}