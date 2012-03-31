package com.bridginggood;

import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bridginggood.DB.LogJSON;
import com.bridginggood.DB.StatsJSON;
import com.facebook.android.R;

public class ThankyouActivity extends Activity{

	private ArrayList<PushMessage> mPushMessageArrayList;
	private Activity _this = this;
	private boolean mPostOnFacebook;
	
	private String mBusinessId = null, mSumTotal=null, mBusinessName=null, mBusinessDetail=null, mCharityName=null, mTotalRaised=null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.thankyou_layout);
		mPostOnFacebook = true;

		LoadActivityAsyncTask loadAsyncTask = new LoadActivityAsyncTask(this);
		loadAsyncTask.execute();
	}
	
	//Loading async task
	private class LoadActivityAsyncTask extends AsyncTask<Context, Boolean, Boolean>{
		private ProgressDialog mProgressDialog = new ProgressDialog(ThankyouActivity.this);
		private Context mContext;

		public LoadActivityAsyncTask(Context context){
			mContext = context;
		}

		protected void onPreExecute(){
			mProgressDialog =  ProgressDialog.show(mContext, "", "Loading... Please wait", true, false);
		}
		protected Boolean doInBackground(Context... params){
			//Load stacked c2dm messages
			String pushMessage = UserInfoStore.loadPushMessage(getApplicationContext());
			createPushMessageArrayListFromString(pushMessage);
			
			//Calculate sum, round to 2 decimal places
			float sumTotalf = 0.0f;
			for(PushMessage pm : mPushMessageArrayList){
				sumTotalf += pm.getDonationAmount();
			}
			mSumTotal = sumTotalf+"";

			//Select random business
			Random rnd = new Random();
			int ind = rnd.nextInt(mPushMessageArrayList.size());
			mBusinessId = mPushMessageArrayList.get(ind).getBusinessId();

			//Get thank you details
			String[] dataArray = StatsJSON.getThankyouDetail(mBusinessId);
			mBusinessName = dataArray[0];
			mBusinessDetail = dataArray[1];
			mCharityName = dataArray[2];
			mTotalRaised = dataArray[3];
			return false;
		}
		protected void onPostExecute(final Boolean isSucc){
			initThankyouTextViews();
			initButtons();
			
			if (mProgressDialog.isShowing())
				mProgressDialog.dismiss();
		}
	}
	
	/**
	 * Creates mPushMessageArrayList
	 * @param pushMessage
	 */
	private void createPushMessageArrayListFromString(String pushMessage){
		mPushMessageArrayList = new ArrayList<PushMessage>();
		try{
			StringTokenizer st = new StringTokenizer(pushMessage, "|");
			StringTokenizer sub_st;
			while(st.hasMoreTokens()){
				String message = st.nextToken();
				//Parse each c2dm message
				sub_st = new StringTokenizer(message, ";");

				String businessId = sub_st.nextToken();
				Float donationAmount = Float.parseFloat(sub_st.nextToken());
				String businessName = sub_st.nextToken();

				PushMessage pm = new PushMessage(businessId, donationAmount, businessName);

				mPushMessageArrayList.add(pm);
			}
		} catch (Exception e){
			Log.d("BG_TU", "createPushMessageArrayListFromString exception:"+e.getLocalizedMessage());
		}
	}

	private void initThankyouTextViews(){
		TextView txtHeader = (TextView)findViewById(R.id.thankyou_header_textview);
		txtHeader.setText("Thank you "+UserInfo.getUserFirstName()+"!");
		TextView txtMessage = (TextView)findViewById(R.id.thankyou_message_textview);
		txtMessage.setText(Html.fromHtml(getThankyouHeaderText()));
	}

	private void initButtons(){
		//Facebook button
		final ImageView btnFacebook = (ImageView)findViewById(R.id.thankyou_facebook_button);
		btnFacebook.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(mPostOnFacebook){
					//Turn off posting
					mPostOnFacebook = false;

					btnFacebook.setImageResource(R.drawable.icon_facebook_disabled);
				}else{
					//Turn on posting
					mPostOnFacebook = true;
					btnFacebook.setImageResource(R.drawable.icon_facebook_enabled);
				}
			}
		});

		//Done button
		Button btnDone = (Button)findViewById(R.id.thankyou_done_button);
		btnDone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				writePostOnFacebook();
			}
		});
	}

	private void writePostOnFacebook(){
		WriteOnFacebookAsyncTask writeOnFacebookAsyncTask = new WriteOnFacebookAsyncTask(ThankyouActivity.this);
		writeOnFacebookAsyncTask.execute();
	}

	@Override
	public void onPause(){
		super.onPause();

		Log.d("BG_TU", "onPause called");
		UserInfoStore.clearPushMessage(getApplicationContext());
	}

	private class WriteOnFacebookAsyncTask extends AsyncTask<Context, Boolean, Boolean>{
		private ProgressDialog mProgressDialog = new ProgressDialog(ThankyouActivity.this);
		private Context mContext;

		public WriteOnFacebookAsyncTask(Context context){
			mContext = context;
		}

		protected void onPreExecute(){
			mProgressDialog =  ProgressDialog.show(mContext, "", "Loading... Please wait", true, false);
		}
		protected Boolean doInBackground(Context... params){
			try{
				//Retrieve the comment
				EditText edtComment = (EditText)findViewById(R.id.thankyou_writecomment_edittext);
				String comment = edtComment.getText().toString();

				if(comment == null || comment.trim().length() == 0){
					//If empty string, assign a default message 
					comment = "";
				}
				
				//FACEBOOK post
				if(mPostOnFacebook && UserInfo.mFacebook.isSessionValid()){
					Bundle bundle = new Bundle();
					bundle.putString("message", comment);
					bundle.putString("caption", "BridgingGood");
					bundle.putString("description", mBusinessDetail);
					bundle.putString("link", "http://www.bridginggood.com/");
					bundle.putString("picture", CONST.FACEBOOK_POST_ICON);

					if(mPushMessageArrayList.size()>1){
						bundle.putString("name", "$"+mSumTotal+" @"+mBusinessName+" +"+(mPushMessageArrayList.size()-1)+"places");
					}else{
						bundle.putString("name", "$"+mSumTotal+" @"+mBusinessName);
					}

					//Send facebook request
					String response = UserInfo.mFacebook.request("me/feed", bundle, "POST");
					if(response==null || response.equals("") || response.equals("false"))	//Error
						return false;
				}

				//Log
				String posted = (mPostOnFacebook)?"Y":"N";
				LogJSON.createSNSLog(mBusinessId, posted);

				//Clear notifications
				UserInfoStore.clearPushMessage(getApplicationContext());
				return true;
			}catch(Exception e){
				Log.d("BG_TU", "Error on FacebookAsyncTask:"+e.getLocalizedMessage());
				return false;
			}
		}
		protected void onPostExecute(final Boolean isSucc){
			if (mProgressDialog.isShowing())
				mProgressDialog.dismiss();
			if(!isSucc){
				Toast.makeText(mContext, "An error has occured", Toast.LENGTH_SHORT).show();
			} else {
				//Success!
				_this.finish();	//Finish ThankyouActivity
			}
		}
	}

	private String getThankyouHeaderText(){
		ArrayList<PushMessage> uniqueList = new ArrayList<PushMessage>();

		String content = "<B>$"+mSumTotal+"</B> of your purchase at " +
				"<B>"+mBusinessName+"</B> has successfully been converted into a donation. <BR/><BR/>" +
				"<B>"+mCharityName+"</B> has now raised " +
				"<B>$"+mTotalRaised+"</B> with your support and other do gooders like you. <BR/><BR/>" +
				"<I>Keep on BridgingGood!</I>";
		return content;
		/*
		String content = "$"+sumTotal;
		" of your purchase at ";
		for(PushMessage pm : mPushMessageArrayList){
			thankyouContent += pm.getBusinessName()+",";
		}

		thankyouContent += "has successfully been converted into a donation."+
		dataArray[2]+" has now raised $" +
		dataArray[3]+" with your support and other do gooders like you. Keep on BridgingGood!";*/
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true; //Ignore back key action
		}
		return super.onKeyDown(keyCode, event);
	}
}
