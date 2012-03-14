package com.bridginggood.Setting;

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
import com.bridginggood.Facebook.FacebookAPI;
import com.bridginggood.Facebook.FacebookSessionStore;

public class SettingActivity extends Activity{
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
					if (FacebookAPI.requestUserLogout(getApplicationContext()))
						FacebookSessionStore.clear(getApplicationContext());
				}
				UserSessionStore.clearSession(getApplicationContext());
			}
		});
	}
}
