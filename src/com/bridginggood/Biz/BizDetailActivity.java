package com.bridginggood.Biz;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
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
import android.widget.Toast;

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

		LoadBizDetailAsyncTask loadbizAsyncTask = new LoadBizDetailAsyncTask();
		loadbizAsyncTask.execute();
	}

	private class LoadBizDetailAsyncTask extends AsyncTask<Context, Boolean, Boolean>{
		//Display progress dialog
		protected void onPreExecute()
		{
			//Show loading
			toggleLayout(true);
		}

		//Load current location
		protected Boolean doInBackground(Context... contexts)
		{
			//Init business name and address
			initTextViews();
			mImageViewURLArrayList = new ArrayList<String>();
			mImageViewCounter = 0;
			mImageManager = new ImageManager(getApplicationContext(), false, mImageDownloaded);
			mPopupview = View.inflate(BizDetailActivity.this,R.layout.popup_qrcode,null);
			initImageViews();
			initButtons();

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

	private void initButtons(){
		Button btnDonate = (Button) findViewById(R.id.business_detail_donate_button);
		btnDonate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(UserInfo.getQRCodeURL()!=null && UserInfo.getQRCodeURL().length()>0){
					PopupWindow popup = new PopupWindow(mPopupview, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
					popup.setOutsideTouchable(true);
					popup.setBackgroundDrawable(new BitmapDrawable());
					popup.showAtLocation(v, Gravity.CENTER, 0, 0);
				}else{
					Toast.makeText(BizDetailActivity.this, "QR Code not found. Please register or create new QR Code from Donate tab", Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	private void initTextViews(){
		Bundle bundle = getIntent().getExtras();
		mBusiness = (Business) bundle.getSerializable("biz");

		TextView txtBusinessName = (TextView)findViewById(R.id.business_detail_name_textview);
		txtBusinessName.setText(mBusiness.getBizName());
		
		TextView txtBusinessAddress = (TextView)findViewById(R.id.business_detail_address_textview);
		txtBusinessAddress.setText(mBusiness.getBizAddress());
	}

	private void toggleLayout(boolean isLoading){
		if(isLoading){
			findViewById(R.id.business_detail_loaded_layout).setVisibility(View.GONE);
			findViewById(R.id.business_detail_loading_layout).setVisibility(View.VISIBLE);
			findViewById(R.id.business_detail_footer_layout).setVisibility(View.GONE);
		} else {
			findViewById(R.id.business_detail_loading_layout).setVisibility(View.GONE);
			findViewById(R.id.business_detail_loaded_layout).setVisibility(View.VISIBLE);
			findViewById(R.id.business_detail_footer_layout).setVisibility(View.VISIBLE);
		}
	}
	private void initImageViews(){
		//BridgedWith
		String url = CONST.IMAGES_PREFIX_CHARITY+"charity_bridgedwith.png";

		/*ImageView imgBridged = (ImageView) findViewById(R.id.business_detail_bridgedwith_img);
		mImageViewURLArrayList.add(url);
		mImageManager.displayImage(url, this, imgBridged);*/


		//Detail
		url = CONST.IMAGES_PREFIX_BUSINESS+mBusiness.getBizId()+"_detail.png";
		ImageView imgDetail = (ImageView) findViewById(R.id.business_detail_img);
		mImageViewURLArrayList.add(url);
		mImageManager.displayImage(url, this, imgDetail);


		//Rest type
		url = CONST.IMAGES_PREFIX_COMMON+"category/REST.png";
		ImageView imgType = (ImageView) findViewById(R.id.business_detail_type_img);
		mImageViewURLArrayList.add(url);
		mImageManager.displayImage(url, this, imgType);


		//Load only if qr exists.
		if(UserInfo.getQRCodeURL()!=null && UserInfo.getQRCodeURL().length()>0){
			//QRCode
			ImageView imgQrcode = (ImageView)mPopupview.findViewById(R.id.popup_qrcode_imageview);
			mImageViewURLArrayList.add(UserInfo.getQRCodeURL());
			mImageManager.displayImage(UserInfo.getQRCodeURL(), BizDetailActivity.this, imgQrcode);
		} else {
			mImageViewCounter++;
		}

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
		}
	};
}