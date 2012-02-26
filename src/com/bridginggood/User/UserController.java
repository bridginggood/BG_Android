package com.bridginggood.User;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bridginggood.CONST;
import com.bridginggood.ImageManager;
import com.bridginggood.R;
import com.bridginggood.UserInfo;

public class UserController extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_qrcode_layout);
		
		ImageView imgView = (ImageView)findViewById(R.id.user_qrcode_imgview);
		String qrcodeURL = CONST.QRCODE_URL+UserInfo.getQRCodeFileName();
		initQRCode (CONST.QRCODE_URL+"test_method.png", imgView);
	}

	private void initQRCode(String qrcodeURL, ImageView imgView){
		Log.d("BG_USER", "initQRCode called:"+qrcodeURL);
		ImageManager imageManager = new ImageManager(this, false);
		imageManager.displayImage(qrcodeURL, this, imgView);
	}
}
