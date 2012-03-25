package com.bridginggood.Biz;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bridginggood.CONST;
import com.bridginggood.ImageManager;
import com.bridginggood.UserInfo;
import com.bridginggood.ImageManager.ImageManagerResult;
import com.bridginggood.R;

public class BizDetailActivity extends Activity{
	private ImageManager mImageManager;
	
	private static int mImageCounter;
	private static final int NUMBER_OF_IMAGES = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.bizdetail_layout);

		//Show loading
		toggleLayout(true);
		
		//Init business name and address
		initTextViews();
		
		mImageManager = new ImageManager(getApplicationContext(), false, mImageDownloaded);
		initImageViews();
	}
	
	private void initTextViews(){
		Bundle bundle = getIntent().getExtras();
		Business business = (Business) bundle.getSerializable("biz");
		
		TextView txtBusinessName = (TextView)findViewById(R.id.business_detail_name_textview);
		txtBusinessName.setText(business.getBizName());
	}

	private void toggleLayout(boolean isLoading){
		if(isLoading){
			findViewById(R.id.business_detail_loaded_layout).setVisibility(View.GONE);
			findViewById(R.id.business_detail_loading_layout).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.business_detail_loading_layout).setVisibility(View.GONE);
			findViewById(R.id.business_detail_loaded_layout).setVisibility(View.VISIBLE);
		}
	}
	private void initImageViews(){
		mImageCounter = 0;
		
		//BridgedWith
		ImageView imgBridged = (ImageView) findViewById(R.id.business_detail_bridgedwith_img);
		mImageManager.displayImage(CONST.IMAGES_PREFIX_CHARITY+"charity_bridgedwith.png", this, imgBridged);
		
		//Detail
		ImageView imgDetail = (ImageView) findViewById(R.id.business_detail_img);
		mImageManager.displayImage(CONST.IMAGES_PREFIX_BUSINESS+UserInfo.getUserId()+"_detail.png", this, imgDetail);
		
		//Rest type
		ImageView imgType = (ImageView) findViewById(R.id.business_detail_type_img);
		mImageManager.displayImage(CONST.IMAGES_PREFIX_BUSINESS+"category/REST.png", this, imgType);
	}

	public ImageManagerResult mImageDownloaded = new ImageManagerResult()
	{
		@Override
		public void gotImage(final boolean isLoaded)
		{
			Log.d("BG", "GotImage. Return:"+isLoaded);
			
			mImageCounter++;
			if (mImageCounter == NUMBER_OF_IMAGES){
				//All images are loaded
				//Done. Show content
				toggleLayout(false);
			}
		}
	};
}
