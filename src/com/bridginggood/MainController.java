package com.bridginggood;


import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.bridginggood.Biz.BizActivityGroup;
import com.bridginggood.Charity.CharityActivityGroup;
import com.bridginggood.User.UserActivityGroup;

public class MainController extends TabActivity {
	private TabHost mTabHost;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabhost_layout);

		//Apply customed tab layout
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);

		setupTab("Causes", R.drawable.icon, new Intent().setClass(this, CharityActivityGroup.class));
		setupTab("Donate", R.drawable.icon, new Intent().setClass(this, UserActivityGroup.class));
		setupTab("Places", R.drawable.icon, new Intent().setClass(this, BizActivityGroup.class));

		mTabHost.setCurrentTab(1);
		/*		
		//User
		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, UserActivityGroup.class);
		spec = mTabHost.newTabSpec("user").setIndicator("Account",
				res.getDrawable(android.R.drawable.ic_dialog_info))
				.setContent(intent);
		mTabHost.addTab(spec);

		// Charity
		intent = new Intent().setClass(this, CharityActivityGroup.class);
		spec = mTabHost.newTabSpec("charity").setIndicator("Charity",
				res.getDrawable(android.R.drawable.ic_dialog_info))
				.setContent(intent);
		mTabHost.addTab(spec);

		//Biz
		intent = new Intent().setClass(this, BizActivityGroup.class);
		spec = mTabHost.newTabSpec("biz").setIndicator("Search",
				res.getDrawable(android.R.drawable.ic_dialog_info))
				.setContent(intent);
		mTabHost.addTab(spec);

		//Setting
		intent = new Intent().setClass(this, SettingActivityGroup.class);
		spec = mTabHost.newTabSpec("bg").setIndicator("About",
				res.getDrawable(android.R.drawable.ic_dialog_info))
				.setContent(intent);
		mTabHost.addTab(spec);

		mTabHost.setCurrentTab(2);
		 */
		initC2DMRegistration();
	}

	private void setupTab(final String tag, final int drawableImg, final Intent intent) {
		View tabview = createTabView(mTabHost.getContext(), tag, drawableImg);

		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview);
		setContent.setContent(intent);

		mTabHost.addTab(setContent);
	}

	private static View createTabView(final Context context, final String text, final int drawableImg) {
		View view = LayoutInflater.from(context).inflate(R.drawable.tabs_bg, null);
		ImageView img = (ImageView) view.findViewById(R.id.tabsImage);
		img.setImageResource(drawableImg);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
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