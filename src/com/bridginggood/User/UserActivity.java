package com.bridginggood.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.bridginggood.R;

public class UserActivity extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_main_layout);

		initButtons();
	}

	private void initButtons(){
		
		/** Footer buttons */
		//Today
		//Month
		//Week
		
		//Preferences
		Button btnPreferences = (Button)findViewById(R.id.profile_btn_preferences);
		btnPreferences.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent().setClass(getParent(), UserPreferencesActivity.class));	
			}
		});
	}
}
