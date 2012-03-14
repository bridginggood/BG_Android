package com.bridginggood.QR;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bridginggood.ImageManager;
import com.bridginggood.ImageManager.ImageManagerResult;
import com.bridginggood.R;
import com.bridginggood.UserInfo;

public class QRMainActivity extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qrcode_layout);
		
		ImageView imgView = (ImageView)findViewById(R.id.qrcode_imgview);
		initQRCode ("https://s3.amazonaws.com/BG_DEV_S3/media/qrcode/1203031822146048.png", imgView);
	}

	private void initQRCode(String qrcodeURL, ImageView imgView){
		Log.d("BG_USER", "initQRCode called:"+qrcodeURL);
		findViewById(R.id.qrcode_imgview).setVisibility(View.INVISIBLE);
		imgView.setVisibility(View.INVISIBLE);
		ImageManager imageManager = new ImageManager(this, false, mImageDownloaded);
		imageManager.displayImage(qrcodeURL, this, imgView);
	}
	
	public ImageManagerResult mImageDownloaded = new ImageManagerResult()
	{
		@Override
		public void gotImage(final boolean isLoaded)
		{
			if(isLoaded){
				findViewById(R.id.qrcode_loading).setVisibility(View.GONE);
				findViewById(R.id.qrcode_imgview).setVisibility(View.VISIBLE);
			}
		}
	};
}