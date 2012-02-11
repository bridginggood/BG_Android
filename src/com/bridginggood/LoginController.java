package com.bridginggood;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bridginggood.Facebook.FacebookSessionStore;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class LoginController extends Activity{
	private boolean mLockThread = false;				//Created to put a lock on asynchronous thread
	private boolean isFacebookLoginSuccess = false;		//True if login by facebook api succeeds
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);

		mLockThread = false;
		isFacebookLoginSuccess = false;
		
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

				//Call UserSession
				UserSession userSession = new UserSession();
				userSession.createUserSessionForBG(edtEmail.getText().toString(), edtPassword.getText().toString(), CONST.USER_SESSION_TYPE_BRIDGINGGOOD);
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
				
				
			}
		});
	}

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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		UserInfo.mFacebook.authorizeCallback(requestCode, resultCode, data);
	}
}
