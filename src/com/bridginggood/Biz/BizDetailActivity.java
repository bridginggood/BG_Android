package com.bridginggood.Biz;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bridginggood.CONST;
import com.bridginggood.ImageManager;
import com.bridginggood.ImageManager.ImageManagerResult;
import com.bridginggood.R;
import com.bridginggood.UserInfo;

public class BizDetailActivity extends Activity{
	private ImageManager mImageManager;
	private ArrayList<String> mImageViewURLArrayList;
	private final int TOTAL_IMAGEVIEWS = 3;		//Number of imageviews to load in this activity
	private int mImageViewCounter;
	private Business mBusiness;		//Business object
	private View mPopupview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.bizdetail_layout);

		//Show loading
		toggleLayout(true);

		//Init business name and address
		initTextViews();
		mImageViewURLArrayList = new ArrayList<String>();
		mImageViewCounter = 0;
		mImageManager = new ImageManager(getApplicationContext(), false, mImageDownloaded);
		initImageViews();

		initButtons();
	}

	private void initButtons(){
		mPopupview = View.inflate(this,R.layout.popup_qrcode,null);
		Button btnDonate = (Button) findViewById(R.id.business_detail_donate_button);
		btnDonate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ImageView imgQrcode = (ImageView)mPopupview.findViewById(R.id.popup_qrcode_imageview);
				mImageManager.displayImage(UserInfo.getQRCodeURL(), BizDetailActivity.this, imgQrcode);
				mImageViewURLArrayList.add(UserInfo.getQRCodeURL());
				PopupWindow popup = new PopupWindow(mPopupview, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
				popup.setOutsideTouchable(true);
				popup.setBackgroundDrawable(new BitmapDrawable());
				popup.showAtLocation(v, Gravity.CENTER, 0, 0);
			}
		});
	}

	private void initTextViews(){
		Bundle bundle = getIntent().getExtras();
		mBusiness = (Business) bundle.getSerializable("biz");

		TextView txtBusinessName = (TextView)findViewById(R.id.business_detail_name_textview);
		txtBusinessName.setText(mBusiness.getBizName());
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
		//BridgedWith
		String url = CONST.IMAGES_PREFIX_CHARITY+"charity_bridgedwith.png";

		ImageView imgBridged = (ImageView) findViewById(R.id.business_detail_bridgedwith_img);
		mImageManager.displayImage(url, this, imgBridged);
		mImageViewURLArrayList.add(url);

		//Detail
		url = CONST.IMAGES_PREFIX_BUSINESS+mBusiness.getBizId()+"_detail.png";
		ImageView imgDetail = (ImageView) findViewById(R.id.business_detail_img);
		mImageManager.displayImage(url, this, imgDetail);
		mImageViewURLArrayList.add(url);

		//Rest type
		url = CONST.IMAGES_PREFIX_COMMON+"category/REST.png";
		ImageView imgType = (ImageView) findViewById(R.id.business_detail_type_img);
		mImageManager.displayImage(url, this, imgType);
		mImageViewURLArrayList.add(url);
	}

	public ImageManagerResult mImageDownloaded = new ImageManagerResult()
	{
		@Override
		public void gotImage(final boolean isLoaded, String url)
		{
			if(mImageViewURLArrayList.contains(url)){
				if(!isLoaded){
					//TODO: Error occured while loading certain image
				}
				mImageViewCounter++;

				//Handle popup imageview load seperately
				if(url.equals(UserInfo.getQRCodeURL())){
					if (isLoaded){
						mPopupview.findViewById(R.id.popup_loading_progressbar).setVisibility(View.GONE);
						mPopupview.findViewById(R.id.popup_qrcode_imageview).setVisibility(View.VISIBLE);
					}
					return;
				}
			}
			Log.d("BG", "GotImage called: "+isLoaded+" , "+url+", counter:"+mImageViewCounter);

			//Display content when all loaded
			if(mImageViewCounter >= TOTAL_IMAGEVIEWS)
				toggleLayout(false);
		}
	};
}
