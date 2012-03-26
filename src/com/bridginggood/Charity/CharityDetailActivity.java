package com.bridginggood.Charity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bridginggood.CONST;
import com.bridginggood.ImageManager;
import com.bridginggood.ImageManager.ImageManagerResult;
import com.bridginggood.R;

public class CharityDetailActivity extends Activity{
	private ImageManager mImageManager;
	private ArrayList<String> mImageViewURLArrayList;
	private final int TOTAL_IMAGEVIEWS = 1;		//Number of imageviews to load in this activity
	private int mImageViewCounter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.charity_detail_layout);
		
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

			mImageViewURLArrayList = new ArrayList<String>();
			mImageViewCounter = 0;
			mImageManager = new ImageManager(getApplicationContext(), false, mImageDownloaded);
			initImageViews();

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


	private void toggleLayout(boolean isLoading){
		if(isLoading){
			findViewById(R.id.charity_detail_loaded_layout).setVisibility(View.GONE);
			findViewById(R.id.charity_detail_loading_layout).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.charity_detail_loading_layout).setVisibility(View.GONE);
			findViewById(R.id.charity_detail_loaded_layout).setVisibility(View.VISIBLE);
		}
	}
	private void initImageViews(){
		String imgDetailURL = CONST.IMAGES_PREFIX_CHARITY+"charity_detail.png";
		ImageView imgDetail = (ImageView) findViewById(R.id.charity_detail_img);
		mImageViewURLArrayList.add(imgDetailURL);
		mImageManager.displayImage(imgDetailURL, this, imgDetail);
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
