package com.bridginggood;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LoginController extends Activity{
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
			}
		});
		
		Button btnLoginFB = (Button) findViewById(R.id.btnLoginFacebook);
		btnLoginFB.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Log.d("BG", "FB Login button Clicked");
			}
		});
	}
}
