package com.bridginggood.Charity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bridginggood.CONST;
import com.bridginggood.ImageManager;
import com.bridginggood.ImageManager.ImageManagerResult;
import com.bridginggood.R;
import com.bridginggood.DB.StatsJSON;

public class CharityCurationActivity extends Activity{
	private ImageManager mImageManager;
	private ArrayList<String> mImageViewURLArrayList;
	private final int TOTAL_IMAGEVIEWS = 1;		//Number of imageviews to load in this activity
	private int mImageViewCounter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.charity_layout);

		LoadPageAsyncTask loadAsyncTask = new LoadPageAsyncTask();
		loadAsyncTask.execute();
	}

	private class LoadPageAsyncTask extends AsyncTask<Context, Boolean, Boolean>{
		//Display progress dialog
		protected void onPreExecute()
		{
			//Show loading
			toggleLayout(true);
		}

		protected Boolean doInBackground(Context... contexts)
		{

			//Show loading message
			toggleLayout(true);	
			mImageViewURLArrayList = new ArrayList<String>();
			mImageViewCounter = 0;
			mImageManager = new ImageManager(getApplicationContext(), false, mImageDownloaded);

			initButtons();
			initImageViews();
			updateCharityData();

			//Wait until everything is loaded
			while(mImageViewCounter < TOTAL_IMAGEVIEWS){
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			return false;
		}
		protected void onPostExecute(final Boolean isSuccess)
		{
			toggleLayout(false);
		}
	}

	private void updateCharityData(){
		//TODO CHANGE CHARITY ID
		String[] data = StatsJSON.getCharityDonation("3000000002");
		if(data!=null){
			TextView txtAmount = (TextView)findViewById(R.id.charity_donated_textview);
			TextView txtPeople = (TextView)findViewById(R.id.charity_people_textview);
			TextView txtDays = (TextView)findViewById(R.id.charity_daystogo_textview);

			txtAmount.setText(data[0]);
			txtPeople.setText(data[1]);
			txtDays.setText(data[2]);
		}
	}

	private void toggleLayout(boolean isLoading){
		if(isLoading){
			findViewById(R.id.charity_loaded_layout).setVisibility(View.GONE);
			findViewById(R.id.charity_loading_layout).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.charity_loading_layout).setVisibility(View.GONE);
			findViewById(R.id.charity_loaded_layout).setVisibility(View.VISIBLE);
		}
	}

	private void initButtons(){
		LinearLayout statLayout = (LinearLayout)findViewById(R.id.charity_stat_layout);
		statLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent().setClass(getParent(), CharityDetailActivity.class));	
			}
		});
	}

	private void initImageViews(){
		String imgMainURL = CONST.IMAGES_PREFIX_CHARITY+"charity_main.png";
		ImageView imgMain = (ImageView) findViewById(R.id.charity_img);
		mImageViewURLArrayList.add(imgMainURL);
		mImageManager.displayImage(imgMainURL, this, imgMain);
	}

	public ImageManagerResult mImageDownloaded = new ImageManagerResult()
	{
		@Override
		public void gotImage(final boolean isLoaded, String url)
		{
			if(mImageViewURLArrayList.contains(url)){
				mImageViewCounter++;
			}
			Log.d("BG", "GotImage called: "+isLoaded+" , "+url+", counter:"+mImageViewCounter);
		}
	};
}