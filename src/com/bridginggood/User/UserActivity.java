package com.bridginggood.User;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.bridginggood.R;
import com.bridginggood.DB.StatsJSON;

public class UserActivity extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_main_layout);
		
		LoadUserActivity loadUserActivity = new LoadUserActivity();
		loadUserActivity.execute();
	}
	
	private class LoadUserActivity extends AsyncTask<Context, Boolean, Boolean>{
		//Display progress dialog
		protected void onPreExecute()
		{
			toggleLayout(true);
		}

		protected Boolean doInBackground(Context... contexts)
		{
			loadData();
			initButtons();
			return null;
		}
		protected void onPostExecute(final Boolean b)
		{

			toggleLayout(false);
		}
	}
	
	private void loadData(){
		//Load total donation amount
		String total = StatsJSON.getTotalDonationAmount();
		if(total != null){
			Float totalf = Float.parseFloat(total);
			DecimalFormat dFormat = new DecimalFormat("#0.00");
			total = dFormat.format(totalf);
		}
		TextView txtTotal = (TextView)findViewById(R.id.profile_main_header_textview);
		txtTotal.setText(txtTotal.getText()+total);	//Appened total at the back of the existing string
		
	}
	
	private void toggleLayout(boolean isLoading){
		if(isLoading){
			findViewById(R.id.profile_main_loaded_layout).setVisibility(View.GONE);
			findViewById(R.id.profile_main_loading_layout).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.profile_main_loading_layout).setVisibility(View.GONE);
			findViewById(R.id.profile_main_loaded_layout).setVisibility(View.VISIBLE);
		}
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
