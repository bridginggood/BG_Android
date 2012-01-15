package com.bridginggood;


import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

import com.bridginggood.Bg.BgActivityGroup;
import com.bridginggood.Biz.BizActivityGroup;
import com.bridginggood.Charity.CharityActivityGroup;
import com.bridginggood.Payment.PaymentActivityGroup;
import com.bridginggood.User.UserActivityGroup;

public class MainLayoutController extends TabActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainlayout_view);

		Resources res = getResources(); 	// Resource object to get Drawables
		TabHost tabHost = getTabHost();  	// The activity TabHost
		TabHost.TabSpec spec = null; 		// Resusable TabSpec for each tab
		Intent intent = null;  				// Reusable Intent for each tab

		// Payment
		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, PaymentActivityGroup.class);
		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec("payment").setIndicator("QR Code",
				res.getDrawable(android.R.drawable.ic_dialog_info))
				.setContent(intent);
		tabHost.addTab(spec);

		// Charity
		// Do the same for the other tabs
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
		
		//User
		intent = new Intent().setClass(this, UserActivityGroup.class);
		spec = tabHost.newTabSpec("user").setIndicator("Account",
				res.getDrawable(android.R.drawable.ic_dialog_info))
				.setContent(intent);
		tabHost.addTab(spec);

		//Bg
		intent = new Intent().setClass(this, BgActivityGroup.class);
		spec = tabHost.newTabSpec("bg").setIndicator("About",
				res.getDrawable(android.R.drawable.ic_dialog_info))
				.setContent(intent);
		tabHost.addTab(spec);
		
		tabHost.setCurrentTab(2);
	}
}