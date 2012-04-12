package com.bridginggood.User;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.bridginggood.CONST;
import com.bridginggood.LoginActivity;
import com.bridginggood.MainActivity;
import com.bridginggood.R;
import com.bridginggood.UserInfo;
import com.bridginggood.UserInfoStore;
import com.bridginggood.DB.AuthJSON;

public class UserPreferencesActivity extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_preferences_layout);
		initMenu();
	}

	private void initMenu(){

		//Privacy policy
		findViewById(R.id.profile_preferences_menu_privacypolicy_textview).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//handlePrivacyPolicy();
				Toast.makeText(UserPreferencesActivity.this, "Coming soon...", Toast.LENGTH_SHORT).show();
			}
		});

		//Terms of service
		findViewById(R.id.profile_preferences_menu_termsofservice_textview).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//handleTermsOfService();
				Toast.makeText(UserPreferencesActivity.this, "Coming soon...", Toast.LENGTH_SHORT).show();
			}
		});

		//Logout
		findViewById(R.id.profile_preferences_menu_logout_textview).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleLogout();
			}
		});
		
		//Support
		findViewById(R.id.profile_preferences_support).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleSupport();
			}
		});
		
		//Send feedback
		findViewById(R.id.profile_preferences_sendfeedback).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleSendFeedback();
			}
		});
		
		//FAQ
		findViewById(R.id.profile_preferences_faq).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//handleFAQ();
				Toast.makeText(UserPreferencesActivity.this, "Coming soon...", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private void handleSupport(){
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(CONST.URL_SUPPORT));
		startActivity(browserIntent);
	}
	
	private void handleSendFeedback(){
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(CONST.URL_SENDFEEDBACK));
		startActivity(browserIntent);
	}
	
	private void handleFAQ(){
		
	}

	private void handlePrivacyPolicy(){
		
	}

	private void handleTermsOfService(){
		final AlertDialog.Builder builder = new AlertDialog.Builder(UserPreferencesActivity.this);
		builder.setMessage("Hello "+UserInfo.getUserFirstName()+" "+UserInfo.getUserLastName()+"\n"+
				UserInfo.getUserEmail()+" | Type: "+UserInfo.getUserType()+" | DeviceId: "+UserInfo.getDeviceId()
				+" | c2dm: "+UserInfo.getC2DMRegistrationId())
				.setTitle("handleClearImageCache")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int id) {
						dialog.cancel();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, final int id) {
						dialog.cancel();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	private void handleLogout(){
		final AlertDialog.Builder builder = new AlertDialog.Builder(UserPreferencesActivity.this);
		builder.setMessage("Are you sure?")
		.setTitle("Logout")
		.setCancelable(false)
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {

				boolean isSucc = AuthJSON.logoutUser();
				if(isSucc){
					//Unregister C2DM
					Intent unregIntent = new Intent("com.google.android.c2dm.intent.UNREGISTER");
					unregIntent.putExtra("app", PendingIntent.getBroadcast(UserPreferencesActivity.this, 0, new Intent(), 0));
					startService(unregIntent);

					//Clear remembered data
					UserInfoStore.clearSession(getApplicationContext());

					//Start login activity
					startActivity(new Intent().setClass(UserPreferencesActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
					finish();	//Finish this activity
					if(MainActivity._this!=null)	//Finish main tab, if running.
						MainActivity._this.finish();

				} else {
					//Logout failed
					Toast.makeText(UserPreferencesActivity.this, "An error has occured while logging out.", Toast.LENGTH_SHORT).show();
				}
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.cancel();
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
	}
}
