package com.bridginggood;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class LoginController extends Activity{
	 Facebook facebook = new Facebook("176588365777862");
	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		
		initButtonViews();
	}
	
	private void initButtonViews(){
		Button btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Log.d("BG", "Login button Clicked");
				EditText edtEmail = (EditText) findViewById(R.id.edtEmail);
				EditText edtPassword = (EditText) findViewById(R.id.edtPassword);
				
				//Call UserSession
				UserSession userSession = new UserSession();
				userSession.createUserSessionForBG(edtEmail.getText().toString(), edtPassword.getText().toString(), CONSTANT.USER_SESSION_TYPE_BRIDGINGGOOD);
			}
		});
		
		Button btnLoginFB = (Button) findViewById(R.id.btnLoginFacebook);
		btnLoginFB.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Log.d("BG", "FB Login button Clicked");
				displayFacebookLogin();
			}
		});
	}
	
	private void displayFacebookLogin(){
		facebook.authorize(this, new DialogListener() {
            @Override
            public void onComplete(Bundle values) {}

            @Override
            public void onFacebookError(FacebookError error) {}

            @Override
            public void onError(DialogError e) {}

            @Override
            public void onCancel() {}
        });
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebook.authorizeCallback(requestCode, resultCode, data);
    }
}
