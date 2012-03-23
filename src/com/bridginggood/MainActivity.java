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
import com.bridginggood.QR.QRActivityGroup;
import com.bridginggood.User.UserActivityGroup;

public class MainActivity extends TabActivity {
	private TabHost mTabHost;
	public static MainActivity _this;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabhost_layout);

		_this = this;

		//Apply customed tab layout
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		setupTab("Profile", R.drawable.selector_tab_profile, new Intent().setClass(this, UserActivityGroup.class));
		setupTab("Explore", R.drawable.selector_tab_explore, new Intent().setClass(this, BizActivityGroup.class));
		setupTab("Donate", R.drawable.selector_tab_donate, new Intent().setClass(this, QRActivityGroup.class));
		setupTab("Causes", R.drawable.selector_tab_causes, new Intent().setClass(this, CharityActivityGroup.class));

		mTabHost.setCurrentTab(2);	//Donate tab is default

		initC2DMRegistration();
	}

	private void setupTab(final String tag, final int drawableImg, final Intent intent) {
		View tabview = createTabView(mTabHost.getContext(), tag, drawableImg);

		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview);
		setContent.setContent(intent);

		mTabHost.addTab(setContent);
	}

	private static View createTabView(final Context context, final String text, final int drawableImg) {
		View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
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

	public void onDestory(){
		super.onDestroy();
		Log.d("BG", "onDestroy called");
		System.exit(0);
	}

	@Override
	public void onResume(){
		super.onResume();
		//C2DM Handle
		if(UserInfo.getLoadThankyouActivity()){
			Intent newIntent = new Intent().setClass(MainActivity.this, ThankyouActivity.class);
			//newIntent.putExtra(CONST.BUNDLE_C2DM_KEY, tmp);
			//newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(newIntent);
		}
	}
}