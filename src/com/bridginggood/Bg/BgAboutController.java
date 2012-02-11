package com.bridginggood.Bg;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.bridginggood.CONST;
import com.bridginggood.R;
import com.bridginggood.UserInfo;
import com.bridginggood.UserSessionStore;
import com.bridginggood.Facebook.FacebookSessionStore;
import com.facebook.android.FacebookError;
import com.facebook.android.AsyncFacebookRunner.RequestListener;

public class BgAboutController extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		TextView txtHello = (TextView) findViewById(R.id.txtHello);
		txtHello.setText("Hello "+UserInfo.getUserFirstName()+" "+UserInfo.getUserLastName()+"\n"+
				UserInfo.getUserEmail()+" | Type: "+UserInfo.getUserType());

		initButton();
	}

	private void initButton(){
		Button btnGoBizMap = (Button) findViewById(R.id.btnTestButton);
		btnGoBizMap.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Log.d("BG", "Button Clicked");
				if(UserInfo.getUserType().equals(CONST.USER_SESSION_TYPE_FACEBOOK)){
					logoutFacebook();
				}
				UserSessionStore.clearSession(getApplicationContext());
			}
		});
	}
	
	private void logoutFacebook(){
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
}
