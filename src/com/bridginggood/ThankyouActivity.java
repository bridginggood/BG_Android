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
import android.widget.TextView;
import android.widget.Toast;

import com.bridginggood.Biz.Business;
import com.bridginggood.DB.BusinessJSON;
import com.facebook.android.R;

public class ThankyouActivity extends Activity{

	private ArrayList<VisitedBusiness> mPushMessageArrayList;
	private Activity _this = this;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.thankyou_layout);

		//Load stacked c2dm messages
		String pushMessage = UserInfoStore.loadPushMessage(getApplicationContext());
		createPushMessageArrayListFromString(pushMessage);

		initViews();
		initButtons();
	}

	private void createPushMessageArrayListFromString(String pushMessage){
		mPushMessageArrayList = new ArrayList<VisitedBusiness>();
		try{
			StringTokenizer st = new StringTokenizer(pushMessage, "|");
			StringTokenizer sub_st;
			while(st.hasMoreTokens()){
				String message = st.nextToken();
				//Parse each c2dm message
				sub_st = new StringTokenizer(message, ",");
				sub_st.nextToken();	//DonationSuccess message
				VisitedBusiness vb = new VisitedBusiness(sub_st.nextToken(), Float.parseFloat(sub_st.nextToken()));
				mPushMessageArrayList.add(vb);
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

				float sumTotal = 0.0f;
				for(VisitedBusiness vb : mPushMessageArrayList){
					sumTotal += vb.getDonationAmount();
				}

				Random rnd = new Random();
				int ind = rnd.nextInt(mPushMessageArrayList.size());
				String businessId = mPushMessageArrayList.get(ind).getBusinessId();

				Business b = BusinessJSON.getBusinessDetail(businessId);
				if (b== null){
					return false;
				}

				//FACEBOOK post
				if(UserInfo.isFbAutoPost() && UserInfo.mFacebook.isSessionValid()){
					Bundle bundle = new Bundle();
					bundle.putString("message", comment);
					bundle.putString("caption", "BridgingGood");
					bundle.putString("description", b.getBizDescription());
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
				//TEMP CODE
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

	private class VisitedBusiness{
		private String mBusinessId;
		private float mDonationAmount;

		public VisitedBusiness(String businessId, float donationAmount){
			setBusinessId(businessId);
			setDonationAmount(donationAmount);
		}

		public String getBusinessId() {
			return mBusinessId;
		}

		public void setBusinessId(String mBusinessId) {
			this.mBusinessId = mBusinessId;
		}

		public float getDonationAmount() {
			return mDonationAmount;
		}

		public void setDonationAmount(float mDonationAmount) {
			this.mDonationAmount = mDonationAmount;
		}
	}
}
