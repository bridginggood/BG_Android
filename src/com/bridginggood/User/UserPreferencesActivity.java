package com.bridginggood.User;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.bridginggood.CONST;
import com.bridginggood.LoginActivity;
import com.bridginggood.MainActivity;
import com.bridginggood.R;
import com.bridginggood.UserInfo;
import com.bridginggood.UserInfoStore;
import com.bridginggood.DB.AuthJSON;
import com.bridginggood.DB.QRCodeJSON;

public class UserPreferencesActivity extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_preferences_layout);
		initMenu();
	}

	private void initMenu(){

		//Register QR Code
		findViewById(R.id.profile_preferences_menu_registerqrcode_textview).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleRegisterQRCode();
			}
		});

		//Privacy policy
		findViewById(R.id.profile_preferences_menu_privacypolicy_textview).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handlePrivacyPolicy();
			}
		});

		//Terms of service
		findViewById(R.id.profile_preferences_menu_termsofservice_textview).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleTermsOfService();
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
				handleFAQ();
			}
		});
	}

	private void handleRegisterQRCode(){
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.qrcode_register_dialog_layout, null);
		AlertDialog.Builder registerDialog = new AlertDialog.Builder(UserPreferencesActivity.this);
		registerDialog.setTitle("Register QR Code").setView(textEntryView);
		registerDialog.setPositiveButton("Register", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//Register QR
				EditText edtQRCode = (EditText) textEntryView.findViewById(R.id.qrcode_register_dialog_qrcode_edittext);
				String qrcode = "";
				if (edtQRCode.getText() != null)
					qrcode = edtQRCode.getText().toString();

				registerQrcode(qrcode);
			}
		});
		registerDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		});
		registerDialog.show();
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

	private void registerQrcode(String qrId){
		RegisterQrcodeAsyncTask registerQrcodeAsyncTask =  new RegisterQrcodeAsyncTask(UserPreferencesActivity.this, qrId);
		registerQrcodeAsyncTask.execute(this);
	}

	private class RegisterQrcodeAsyncTask extends AsyncTask<Context, Void, String>
	{
		private Context mContext;
		private ProgressDialog mProgressDialog;
		private String mQrId;

		public RegisterQrcodeAsyncTask(Context context, String qrId){
			this.mContext = context;
			this.mQrId = qrId;
		}

		//Display progress dialog
		protected void onPreExecute()
		{
			mProgressDialog = ProgressDialog.show(this.mContext, "", "Please wait...", true, false);
		}

		//Load current location
		protected String doInBackground(Context... contexts)
		{
			return QRCodeJSON.registerQRCode(this.mQrId);
		}
		protected void onPostExecute(final String strSuccess)
		{
			if(mProgressDialog.isShowing())
			{
				mProgressDialog.dismiss();
			}

			if(strSuccess.equals("Success")){
				Toast.makeText(this.mContext, "QR Code Registered!", Toast.LENGTH_LONG).show();
			}else if (strSuccess.equals("Exist")){
				Toast.makeText(this.mContext, "You have already registered QR Code", Toast.LENGTH_LONG).show();
			}
			else{
				Toast.makeText(this.mContext, "Invalid QR Code. Please check again.", Toast.LENGTH_SHORT).show();
			}
		}
	}
}
