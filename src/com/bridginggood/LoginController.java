package com.bridginggood;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bridginggood.Facebook.BaseRequestListener;
import com.bridginggood.Facebook.FacebookSessionStore;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class LoginController extends Activity{
	private boolean mLockThread = false;				//Created to put a lock on asynchronous thread
	private boolean mIsLoginSuccess = false;		//True if login is success
	private ProgressDialog mProgressDialog;
	private UserSession mUserSession;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);

		mLockThread = false;
		mIsLoginSuccess = false;
		mUserSession = new UserSession();

		initButtonViews();
	}

	private void initButtonViews(){
		//BridgingGood login button
		Button btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Log.d("BG", "Login button Clicked");
				EditText edtEmail = (EditText) findViewById(R.id.edtEmail);
				EditText edtPassword = (EditText) findViewById(R.id.edtPassword);

				mUserSession.createUserSessionForBG(edtEmail.getText().toString(), edtPassword.getText().toString(), CONST.USER_SESSION_TYPE_BG);
				startLoginProgressDialog();
			}
		});

		//Facebook login button
		Button btnLoginFB = (Button) findViewById(R.id.btnLoginFacebook);
		btnLoginFB.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Log.d("BG", "FB Login button Clicked");
				mLockThread = true;
				displayFacebookLogin();

				while(mLockThread);	//Wait until facebook login is done

				if(UserInfo.mFacebook.isSessionValid())
				{
					//If facebook signin was successful, 
					mLockThread = true;
					Bundle params = new Bundle();
					params.putString("fields", "first_name, last_name, email");
					UserInfo.mAsyncRunner.request("me", params,new UserInfoRequestListener());

					while(mLockThread);
					
					mUserSession.createUserSessionForFacebook(UserInfo.getUserEmail(), UserInfo.getUserFirstName(), 
							UserInfo.getUserLastName(), CONST.USER_SESSION_TYPE_FACEBOOK);
					startLoginProgressDialog();
				}
			}
		});
	}

	private void startLoginProgressDialog(){
		mProgressDialog = ProgressDialog.show(this, "Connecting", "Loading. Please wait...", true, false);
		Thread thread = new Thread(new Runnable() {
			public void run() {
				mIsLoginSuccess = mUserSession.loginUserSession();
				handlerLoading.sendEmptyMessage(0);
			}
		});
		thread.start();	
	}
	
	private Handler handlerLoading = new Handler() {
		public void handleMessage(Message msg) {
			mProgressDialog.dismiss(); // Close dialog
			// Update view
			if(mIsLoginSuccess){
				Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
				finish();
				startActivity(new Intent().setClass(LoginController.this, MainController.class));
			}
			else {
				Toast.makeText(getApplicationContext(), "Login Failed.", Toast.LENGTH_SHORT).show();
			}
		}
	};

	/*
	 * Display Facebook login dialog.
	 * 
	 */
	private void displayFacebookLogin(){

		if (UserInfo.mFacebook.isSessionValid()){
			//TEMP LOGOUT CODE
			Log.d("BG", "Session was valid! Logging out now!");
			UserInfo.mAsyncRunner.logout(getApplicationContext(), new RequestListener() {
				@Override
				public void onComplete(String response, Object state) {
					FacebookSessionStore.clear(getApplicationContext());
					Log.d("BG", "FacebookSessionStore also cleared");
				}

				@Override
				public void onIOException(IOException e, Object state) {}

				@Override
				public void onFileNotFoundException(FileNotFoundException e,
						Object state) {}

				@Override
				public void onMalformedURLException(MalformedURLException e,
						Object state) {}

				@Override
				public void onFacebookError(FacebookError e, Object state) {}
			});
			//TEMP LOGOUT END
		}
		else {
			UserInfo.mFacebook.authorize(this, CONST.FACEBOOK_PERMISSION, new DialogListener() {
				@Override
				public void onComplete(Bundle values) {
					Log.d("BG", "Facebook Login Success!");
					Toast.makeText(getApplicationContext(), "Facebook login success!",Toast.LENGTH_SHORT).show();
					FacebookSessionStore.save(UserInfo.mFacebook, getApplicationContext());

					mLockThread = false;
				}

				@Override
				public void onFacebookError(FacebookError error) {
					Log.d("BG", "Facebook Login Error.");
					Toast.makeText(getApplicationContext(), "Error occured: "+error,Toast.LENGTH_SHORT).show();

					mLockThread = false;
				}

				@Override
				public void onError(DialogError e) {
					mLockThread = false;
				}

				@Override
				public void onCancel() {
					mLockThread = false;
				}
			});
		}
	}
	/*
	 * Callback for fetching current user's firstname, lastname, email
	 */
	protected class UserInfoRequestListener extends BaseRequestListener {
		@Override
		public void onComplete(final String response, final Object state) {
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(response);

				UserInfo.setUserFirstName(jsonObject.getString("first_name"));
				UserInfo.setUserLastName(jsonObject.getString("last_name"));
				UserInfo.setUserEmail(jsonObject.getString("email"));

				mLockThread = false;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		UserInfo.mFacebook.authorizeCallback(requestCode, resultCode, data);
	}
}
