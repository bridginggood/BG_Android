package com.bridginggood.Charity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.bridginggood.R;
import com.bridginggood.User.UserPreferencesActivity;

public class CharityCurationActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.charity_layout);
		
		initButton();
	}
	
	private void initButton(){
		LinearLayout statLayout = (LinearLayout)findViewById(R.id.charity_stat_layout);
		statLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent().setClass(getParent(), CharityDetailActivity.class));	
			}
		});
	}
}