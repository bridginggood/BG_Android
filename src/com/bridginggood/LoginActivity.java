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
import android.widget.Toast;

import com.bridginggood.DB.AuthJSON;
import com.bridginggood.Facebook.FacebookAPI;
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
		
		//Facebook login button
		Button btnLoginFB = (Button) findViewById(R.id.login_btnLoginFacebook);
		btnLoginFB.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Log.d("BG", "FB Login button Clicked");
				UserInfo.setUserType(CONST.USER_SESSION_TYPE_FACEBOOK);
				startFacebookLogin();
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
				mIsLoginSuccess = AuthJSON.loginUser(UserInfo.getUserType());
				
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
				//Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
				UserInfoStore.saveUserSession(getApplicationContext());
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
