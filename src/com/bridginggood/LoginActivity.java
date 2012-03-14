/**
 * Created By: Junsung Lim
 * 
 * Description:
 * 	LoginController displays user login page.
 *  User can login using BridgingGood account or Facebook account. 
 *  
 *  Directs user to MainController on successful login.
 *  Displays an error message upon login failure.
 */
package com.bridginggood;

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
import android.widget.TextView;
import android.widget.Toast;

import com.bridginggood.DB.UserLoginJSON;
import com.bridginggood.Facebook.FacebookAPI;
import com.bridginggood.Facebook.FacebookSessionStore;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class LoginActivity extends Activity{
	private boolean mIsLoginSuccess = false;		//True if login is success
	private ProgressDialog mProgressDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		
		mIsLoginSuccess = false;

		initButtonViews();
	}

	private void initButtonViews(){
		//BridgingGood login button
		Button btnLogin = (Button) findViewById(R.id.login_btnLogin);
		btnLogin.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Log.d("BG", "Login button Clicked");
				EditText edtEmail = (EditText) findViewById(R.id.login_edtEmail);
				EditText edtPassword = (EditText) findViewById(R.id.login_edtPassword);

				UserInfo.createUserInfoForBG(edtEmail.getText().toString(), edtPassword.getText().toString(), CONST.USER_SESSION_TYPE_BG);
				startBridgingGoodLogin();
			}
		});

		//Facebook login button
		Button btnLoginFB = (Button) findViewById(R.id.login_btnLoginFacebook);
		btnLoginFB.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Log.d("BG", "FB Login button Clicked");
				UserInfo.setUserType(CONST.USER_SESSION_TYPE_FACEBOOK);
				startFacebookLogin();
			}
		});
		
		//Signup button
		TextView txtSignup = (TextView) findViewById(R.id.login_lblNeedAccount);
		txtSignup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("BG", "Need an account clicked");
				startActivity(new Intent().setClass(LoginActivity.this, SignupActivity.class));
			}
		});
	}

	/**
	 * Displays ProgressDialog to limit user's activity when logging in.
	 * 
	 * Actual login with server takes place here.
	 */
	private void startBridgingGoodLogin(){
		mProgressDialog = ProgressDialog.show(this, "", "Logging in, please wait...", true, false);
		Thread threadStartLogin = new Thread(new Runnable() {
			public void run() {
				mIsLoginSuccess = UserInfo.loginUserInfo(getApplicationContext());
				
				// If deviceId did not exist before, create QRCode
				if(!UserInfo.isDeviceIdExisted()){
					Log.d("BG", "Requesting createQRCode since isDeviceIdExisted is false");
					UserLoginJSON.createQRCode();
				}
				
				Log.d("BG", "mIsLoginSuccess: "+mIsLoginSuccess);
				handlerLogin.sendEmptyMessage(0);
			}
		});
		threadStartLogin.start();	
	}

	/**
	 * Handler called by threadStartLogin in startLoginProgressDialog()
	 * 
	 * Directs user to the MainController upon success login. 
	 * Otherwise, displays an error message
	 */
	private Handler handlerLogin = new Handler() {
		public void handleMessage(Message msg) {
			mProgressDialog.dismiss(); // Close dialog
			// Update view
			if(mIsLoginSuccess){
				Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
				finish();
				startActivity(new Intent().setClass(LoginActivity.this, MainActivity.class));
			}
			else {
				Toast.makeText(getApplicationContext(), "Login failed. Please verify your login information.", Toast.LENGTH_SHORT).show();
			}
		}
	};

	/**
	 * Displays Facebook login dialog.
	 * Also calls startBridgingGoodLogin() upon Facebook login success.
	 */
	private void startFacebookLogin(){
		if (!UserInfo.mFacebook.isSessionValid()){
			Log.d("BG", "Launching FB DialogListner in attempt to login using FB");
			UserInfo.mFacebook.authorize(this, CONST.FACEBOOK_PERMISSION, new DialogListener() {
				@Override
				public void onComplete(Bundle values) {
					Log.d("BG", "Facebook Login Success!");
					//Store Facebook session and request for user data
					FacebookSessionStore.save(getApplicationContext());
					FacebookAPI.requestUserInfo();
					
					//Start BridgingGood login for the Facebook user
					startBridgingGoodLogin();
				}
				@Override
				public void onFacebookError(FacebookError error) {
					Log.d("BG", "Facebook Login Error: "+error.getLocalizedMessage());
				}
				@Override
				public void onError(DialogError e) {
					Log.d("BG", "Facebook Login Error: "+e.getLocalizedMessage());
				}
				@Override
				public void onCancel() {
					Log.d("BG", "Facebook Login Cancelled");
				}
			});
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		UserInfo.mFacebook.authorizeCallback(requestCode, resultCode, data);
	}
}
