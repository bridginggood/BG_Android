package com.bridginggood.User;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bridginggood.R;
import com.bridginggood.ValuePair;
import com.bridginggood.DB.StatsJSON;

public class UserActivity extends Activity{
	private static final int BY_CHARITY = 0;
	private static final int BY_PLACE = 1;
	private LoadUserDataAsyncTask mLoadUserDataAsyncTask;
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
			mLoadUserDataAsyncTask = new LoadUserDataAsyncTask(BY_CHARITY);
			mLoadUserDataAsyncTask.execute();
		}
	}
	
	private void initList(ArrayList<ValuePair<String, String>> dataArrayList){
		UserDataListAdapter listAdapter = new UserDataListAdapter(this, R.layout.profile_datalist_cell, dataArrayList);
		ListView listview = (ListView)findViewById(R.id.profile_data_listview);
		listview.setAdapter(listAdapter);
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

		final Button btnByCharity = (Button)findViewById(R.id.profile_btn_select_charity);
		final Button btnByPlace = (Button)findViewById(R.id.profile_btn_select_place);
		btnByCharity.setEnabled(false);	//Default
		
		//By Charity
		btnByCharity.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mLoadUserDataAsyncTask!= null 
						&& (mLoadUserDataAsyncTask.getStatus().equals(AsyncTask.Status.RUNNING)
						|| mLoadUserDataAsyncTask.getStatus().equals(AsyncTask.Status.PENDING))){
					mLoadUserDataAsyncTask.cancel(true);	//Cancel running thread
				}
				//Run thread
				mLoadUserDataAsyncTask = new LoadUserDataAsyncTask(BY_CHARITY);
				mLoadUserDataAsyncTask.execute();
				
				//Make me selected
				btnByCharity.setEnabled(false);
				btnByPlace.setEnabled(true);
			}
		});

		//By Places
		btnByPlace.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {				
				if(mLoadUserDataAsyncTask!= null 
						&& (mLoadUserDataAsyncTask.getStatus().equals(AsyncTask.Status.RUNNING)
						|| mLoadUserDataAsyncTask.getStatus().equals(AsyncTask.Status.PENDING))){
					mLoadUserDataAsyncTask.cancel(true);	//Cancel running thread
				}
				//Run thread
				mLoadUserDataAsyncTask = new LoadUserDataAsyncTask(BY_PLACE);
				mLoadUserDataAsyncTask.execute();
				
				//Make me selected
				btnByCharity.setEnabled(true);
				btnByPlace.setEnabled(false);
			}
		});

		//Preferences
		Button btnPreferences = (Button)findViewById(R.id.profile_btn_preferences);
		btnPreferences.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent().setClass(getParent(), UserPreferencesActivity.class));	
			}
		});
	}

	private class LoadUserDataAsyncTask extends AsyncTask<Context, ArrayList<ValuePair<String, String>>, ArrayList<ValuePair<String, String>>>
	{
		private int mType;	//Determine which data to load

		public LoadUserDataAsyncTask(int type){

			this.mType = type;
		}

		//Display progress dialog
		protected void onPreExecute()
		{
			findViewById(R.id.profile_data_loading_progressbar).setVisibility(View.VISIBLE);
		}

		//Load current location
		protected ArrayList<ValuePair<String, String>> doInBackground(Context... contexts)
		{
			return StatsJSON.getDonationAmount(this.mType);
		}
		protected void onPostExecute(final ArrayList<ValuePair<String, String>> dataList)
		{
			findViewById(R.id.profile_data_loading_progressbar).setVisibility(View.GONE);
			if (dataList != null){
				initList(dataList);
			}
		}

	}
}