package com.bridginggood;

import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.thankyou_layout);
		mPostOnFacebook = true;

		//Load stacked c2dm messages
		String pushMessage = UserInfoStore.loadPushMessage(getApplicationContext());
		createPushMessageArrayListFromString(pushMessage);

		initViews();
		initButtons();
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

	private void initViews(){
		TextView txtHeader = (TextView)findViewById(R.id.thankyou_header_textview);
		txtHeader.setText("Welcome "+UserInfo.getUserFirstName()+" ! Size:"+mPushMessageArrayList.size());
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

				//Calculate sum, round to 2 decimal places
				float sumTotalf = 0.0f;
				for(PushMessage pm : mPushMessageArrayList){
					sumTotalf += pm.getDonationAmount();
				}
				String sumTotal = sumTotalf+"";

				//Select random business
				Random rnd = new Random();
				int ind = rnd.nextInt(mPushMessageArrayList.size());
				String businessId = mPushMessageArrayList.get(ind).getBusinessId();

				String[] dataArray = StatsJSON.getThankyouDetail(businessId);
				if(dataArray != null){
					//Initialize thank you page note
					TextView txtThankyou = (TextView)findViewById(R.id.thankyou_header_textview);
					txtThankyou.setText(getThankyouHeaderText(sumTotal, dataArray[0], dataArray[2], dataArray[3]));
					
				}
				/*Business b = BusinessJSON.getBusinessDetail(businessId);
				if (b== null){
					return false;	//How is this possible?
				}

				//FACEBOOK post
				if(mPostOnFacebook && UserInfo.mFacebook.isSessionValid()){
					Bundle bundle = new Bundle();
					bundle.putString("message", comment);
					bundle.putString("caption", "BridgingGood");
					bundle.putString("description", b.getBizDescription());
					bundle.putString("link", "http://www.bridginggood.com/");
					bundle.putString("picture", CONST.FACEBOOK_POST_ICON);
					
					if(mPushMessageArrayList.size()>1){
						bundle.putString("name", "$"+sumTotal+" @"+b.getBizName()+" +"+(mPushMessageArrayList.size()-1)+"places");
					}else{
						bundle.putString("name", "$"+sumTotal+" @"+b.getBizName());
					}

					//Send facebook request
					String response = UserInfo.mFacebook.request("me/feed", bundle, "POST");
					if(response==null || response.equals("") || response.equals("false"))	//Error
						return false;
				}
				*/
				//Log
				String posted = (mPostOnFacebook)?"Y":"N";
				LogJSON.createSNSLog(businessId, posted);
				
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
				Toast.makeText(mContext, "Error has occured", Toast.LENGTH_SHORT).show();
			} else {
				//Success!
				_this.finish();	//Finish ThankyouActivity
			}
		}
	}
	
	private String getThankyouHeaderText(String sumTotal, String businessName, String charityName, String totalRaised){
		ArrayList<PushMessage> uniqueList = new ArrayList<PushMessage>();
		/*
		String content = "$"+sumTotal;
		" of your purchase at ";
		for(PushMessage pm : mPushMessageArrayList){
			thankyouContent += pm.getBusinessName()+",";
		}
		
		thankyouContent += "has successfully been converted into a donation."+
		dataArray[2]+" has now raised $" +
		dataArray[3]+" with your support and other do gooders like you. Keep on BridgingGood!";*/
		return null;
	}
}
