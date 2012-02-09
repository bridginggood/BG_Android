package com.bridginggood;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

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
				EditText edtEmail = (EditText) findViewById(R.id.edtEmail);
				EditText edtPassword = (EditText) findViewById(R.id.edtPassword);
				CheckBox chkRememberLogin = (CheckBox)findViewById(R.id.chkRememberLogin);
				
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
