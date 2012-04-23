package com.bridginggood.QR;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bridginggood.ImageManager;
import com.bridginggood.ImageManager.ImageManagerResult;
import com.bridginggood.R;
import com.bridginggood.UserInfo;
import com.bridginggood.DB.QRCodeJSON;

public class QRMainActivity extends Activity{
	private EditText mEdtQrId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qrcode_layout);

		mEdtQrId = (EditText)findViewById(R.id.qrcode_edtQrcode);
		mEdtQrId.addTextChangedListener(mTextWatcher);

		toggleLayout();		
	}

	private void initButtons(){
		Button btnCreateNewQrcode = (Button)findViewById(R.id.qrcode_btnCreateNewQrcode);
		Button btnRegisterQrcode = (Button)findViewById(R.id.qrcode_btnRegisterQrcode);

		btnCreateNewQrcode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Create new qrcode
				AlertDialog.Builder builderCreateNewQrcode = new AlertDialog.Builder(getParent());
				builderCreateNewQrcode.setMessage("Are you sure?")
				.setCancelable(false)
				.setTitle("BridgingGood")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						//Create QRCode
						createNewQrcode();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
				
				AlertDialog ad = builderCreateNewQrcode.create();
				ad.show();
			}
		});

		
		btnRegisterQrcode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Register Qrcode button
				AlertDialog.Builder builderRegisterQrcode = new AlertDialog.Builder(getParent());
				builderRegisterQrcode.setMessage("Please confirm the entered QRcode: "+mEdtQrId.getText()+"")
				.setCancelable(false)
				.setTitle("BridgingGood")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						registerQrcode(mEdtQrId.getText()+"");
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
				
				AlertDialog ad = builderRegisterQrcode.create();
				ad.show();
			}
		});
	}

	private void toggleLayout(){
		RelativeLayout linearLayoutQRDefined = (RelativeLayout)findViewById(R.id.qrcode_defined_layout);
		LinearLayout linearLayoutQRUndefined = (LinearLayout)findViewById(R.id.qrcode_undefined_layout);

		//QR exists
		if (UserInfo.getQRCodeURL() == null){
			/*
			//If QrCode is blank (no url), hide QrCode image view
			linearLayoutQRDefined.setVisibility(View.GONE);
			linearLayoutQRUndefined.setVisibility(View.VISIBLE);

			initButtons();
			*/
			
			//Create new QR automatically
			QRCodeJSON.createQRCode();
		}
		
		//Show QR layout and load QR
		linearLayoutQRDefined.setVisibility(View.VISIBLE);
		linearLayoutQRUndefined.setVisibility(View.GONE);

		//Load image 
		ImageView imgView = (ImageView)findViewById(R.id.qrcode_imgview);
		initQRCode (UserInfo.getQRCodeURL(), imgView);
	}

	//Creates new qrcode
	private void createNewQrcode(){
		CreateNewQrcodeAsyncTask createNewQrcodeAsyncTask =  new CreateNewQrcodeAsyncTask(getParent());
		createNewQrcodeAsyncTask.execute(this);
	}

	//Registers qrcode
	private void registerQrcode(String qrId){
		RegisterQrcodeAsyncTask registerQrcodeAsyncTask =  new RegisterQrcodeAsyncTask(getParent(), qrId);
		registerQrcodeAsyncTask.execute(this);
	}

	//Loads image (Qrcode) asynchronously. 
	private void initQRCode(String qrcodeURL, ImageView imgView){
		Log.d("BG_USER", "initQRCode called:"+qrcodeURL);
		imgView.setVisibility(View.GONE);
		ImageManager imageManager = new ImageManager(this, false, mImageDownloaded);
		imageManager.displayImage(qrcodeURL, this, imgView);
	}

	public ImageManagerResult mImageDownloaded = new ImageManagerResult()
	{
		@Override
		public void gotImage(final boolean isLoaded, String url)
		{
			if(isLoaded && url.equals(UserInfo.getQRCodeURL())){
				findViewById(R.id.qrcode_loading).setVisibility(View.GONE);
				findViewById(R.id.qrcode_imgview).setVisibility(View.VISIBLE);
			}
		}
	};

	//TODO: Add - every 4 characters
	TextWatcher mTextWatcher = new TextWatcher() {  
		@Override  
		public void onTextChanged(CharSequence s, int start, int before, int count) {}  

		@Override  
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}  

		@Override  
		public void afterTextChanged(Editable s) {  
			/*	String tmp = s.toString();
			tmp = tmp.replace('-', '0');
			if(tmp.length() > 0 && tmp.length()%4 == 0)
				mEdtQrId.setText(s.toString()+"-");*/
		}  
	};    

	private class CreateNewQrcodeAsyncTask extends AsyncTask<Context, Void, Boolean>
	{
		private Context mContext;
		private ProgressDialog mProgressDialog;

		public CreateNewQrcodeAsyncTask(Context context){
			this.mContext = context;
		}

		//Display progress dialog
		protected void onPreExecute()
		{
			mProgressDialog = ProgressDialog.show(this.mContext, "", "Please wait...", true, false);
		}

		//Load current location
		protected Boolean doInBackground(Context... contexts)
		{
			return QRCodeJSON.createQRCode();
		}
		protected void onPostExecute(final Boolean isSuccess)
		{
			if(mProgressDialog.isShowing())
			{
				mProgressDialog.dismiss();
			}

			if(isSuccess){
				toggleLayout();
			}
			else{
				Toast.makeText(this.mContext, "An error has occurred.", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private class RegisterQrcodeAsyncTask extends AsyncTask<Context, Void, String>
	{
		private Context mContext;
		private ProgressDialog mProgressDialog;
		private String mQrId;

		public RegisterQrcodeAsyncTask(Context context, String qrId){
			this.mContext = context;
			this.mQrId = qrId;
		}

		//Display progress dialog
		protected void onPreExecute()
		{
			mProgressDialog = ProgressDialog.show(this.mContext, "", "Please wait...", true, false);
		}

		//Load current location
		protected String doInBackground(Context... contexts)
		{
			return QRCodeJSON.registerQRCode(this.mQrId);
		}
		protected void onPostExecute(final String strSuccess)
		{
			if(mProgressDialog.isShowing())
			{
				mProgressDialog.dismiss();
			}

			boolean isSuccess = (strSuccess.equals("Success"))?true:false;
			
			if(isSuccess){
				toggleLayout();
			}
			else{
				Toast.makeText(this.mContext, "An error has occurred.", Toast.LENGTH_SHORT).show();
			}
		}
	}
}