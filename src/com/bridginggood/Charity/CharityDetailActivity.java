package com.bridginggood.Charity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bridginggood.CONST;
import com.bridginggood.ImageManager;
import com.bridginggood.R;
import com.bridginggood.ImageManager.ImageManagerResult;

public class CharityDetailActivity extends Activity{
	private ImageManager mImageManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.charity_detail_layout);

		//Show loading
		toggleLayout(true);

		mImageManager = new ImageManager(getApplicationContext(), false, mImageDownloaded);
		initImageViews();
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
		Log.d("BG", "Loading:"+CONST.IMAGES_PREFIX_CHARITY+"charity_detail.png");
		ImageView imgDetail = (ImageView) findViewById(R.id.charity_detail_img);
		mImageManager.displayImage(CONST.IMAGES_PREFIX_CHARITY+"charity_detail.png", this, imgDetail);
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
