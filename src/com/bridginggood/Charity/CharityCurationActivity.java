package com.bridginggood.Charity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bridginggood.CONST;
import com.bridginggood.ImageManager;
import com.bridginggood.ImageManager.ImageManagerResult;
import com.bridginggood.R;

public class CharityCurationActivity extends Activity{
	private ImageManager mImageManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.charity_layout);
		
		//Show loading message
		toggleLayout(true);	
		
		mImageManager = new ImageManager(getApplicationContext(), false, mImageDownloaded);
		
		initButtons();
		initImageViews();
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
		Log.d("BG", "Loading:"+CONST.IMAGES_PREFIX_CHARITY+"charity_main.png");
		ImageView imgMain = (ImageView) findViewById(R.id.charity_img);
		mImageManager.displayImage(CONST.IMAGES_PREFIX_CHARITY+"charity_main.png", this, imgMain);
	}
	
	public ImageManagerResult mImageDownloaded = new ImageManagerResult()
	{
		@Override
		public void gotImage(final boolean isLoaded)
		{
			Log.d("BG", "Done!");
			//Done. Show content
			toggleLayout(false);
		}
	};
}