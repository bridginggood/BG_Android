/**
 * Created by: Junsung Lim
 * 
 * Lists down the businesses around the user
 */
package com.bridginggood.Biz;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bridginggood.R;
import com.bridginggood.Biz.BizMyLocation.LocationResult;
import com.bridginggood.DB.BusinessJSON;

public class BizListActivity extends Activity implements OnScrollListener{
	private ArrayList<Business> mBizArrayList;		//Stores Business objects in array
	private BizMyLocation mBizMyLocation;
	private Location mUserLocation;
	private float mMyLat, mMyLng;	//For API search
	private int mPage;				//For API search

	private boolean mEndOfList;					//To lock the list while handling onScroll event
	private boolean mIsLoadUserLocationAsyncTaskRunning, mIsLoadBusinessToDisplayAsyncTaskRunning;

	private BizListAdapter mBizListAdapter;			//ListView adapter

	private ListView mBizListView;					//ListView
	private View mBizListViewFooter;				//ListView footer - Loading message

	private boolean mIsLocationAvailable = false;

	private LoadBusinessToDisplayAsyncTask mLoadBusinessToDisplayAsyncTask;
	private LoadUserLocationAsyncTask mLoadUserLocationAsyncTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bizlist_layout);

		//Check if GPS is on or not
		final LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

		if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
			displayAlertMessageToEnableGPS();
		}

		/*
		 * Initialize array list that stores business objects
		 */
		mBizArrayList = new ArrayList<Business>();
		mPage = 1;
		mEndOfList = false;

		mIsLoadUserLocationAsyncTaskRunning = false;
		mIsLoadBusinessToDisplayAsyncTaskRunning = false;

		//Initialize listview
		this.initListView();

		//Initialize button
		this.initButtonViews();

		//Initialize myLocation to get current loc
		retrieveUserLocation();
	}

	private void initListView(){
		mBizListAdapter = new BizListAdapter(this, R.layout.bizlist_cell, mBizArrayList);
		mBizListView = (ListView)findViewById(R.id.listBiz);

		//Add header
		View listBizHeader = (View)getLayoutInflater().inflate(R.layout.bizlist_cell_header, null);
		mBizListView.addHeaderView(listBizHeader);
		mBizListView.setAdapter(mBizListAdapter);

		//Add footer - "loading..." text
		mBizListViewFooter = (View)getLayoutInflater().inflate(R.layout.bizlist_cell_footer, null);
		mBizListView.addFooterView(mBizListViewFooter);

		//Click listener
		mBizListView.setOnItemClickListener(mItemClickListener);

		//Hide the list on init
		mBizListView.setVisibility(View.GONE);

		//Attach scroll listener
		mBizListView.setOnScrollListener(this);
		mEndOfList = false;
	}

	//Load button to BizMap.java
	public void initButtonViews(){

		//Search in Map button
		Button btnGoBizMap = (Button) findViewById(R.id.btnGoToBizMap);
		//Button Handler
		final Intent intent = new Intent(this, BizMapActivity.class);
		btnGoBizMap.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Log.d("BgBiz", "Button Clicked");
				BizActivityGroup bizActivityGroup = ((BizActivityGroup)getParent());
				View newView = bizActivityGroup.getBizActivityGroup().getLocalActivityManager()
						.startActivity("BizMapActivity", intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
						.getDecorView();

				bizActivityGroup.getBizActivityGroup().changeView(newView);	//Replace View
			}
		});

		ImageButton btnReloadLocation = (ImageButton) findViewById(R.id.actionbar_imgbtn_findlocation);
		btnReloadLocation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("BgBiz", "Reload location");
				retrieveUserLocation();
			}
		});

		//Set ListView button as selected
		Button btnGoListView = (Button) findViewById(R.id.btnGoToListView);
		btnGoListView.setEnabled(false);
		btnGoBizMap.setEnabled(true);
	}


	private void retrieveUserLocation(){
		mLoadUserLocationAsyncTask = new LoadUserLocationAsyncTask(this); 
		mLoadUserLocationAsyncTask.execute();
	}

	AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			position = position - 1; //To avoid list header
			if(position < 0 )	//Clicked header
				return;
			if(position == mBizListAdapter.getCount()) //Clicked footer
				return;

			Business biz = mBizArrayList.get(position);
			// TODO Auto-generated method stub
			Intent intent = new Intent(BizListActivity.this, BizDetailActivity.class);

			Bundle extra = new Bundle();
			extra.putSerializable("biz", (Serializable) biz);
			intent.putExtras(extra);

			startActivity(intent);
		}
	};

	// To handle back button
	public void onBackPressed() { //on Back
		Log.d("BgBiz", "Back Pressed from BizList");
		stopLocationLoading();
		BizActivityGroup parent = ((BizActivityGroup)getParent());
		parent.back();
	}

	/*
	 * Scroll Event related methods 
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

		//what is the bottom item that is visible
		int lastInScreen = firstVisibleItem+visibleItemCount;

		//is the bottom item visible & not loading more already? Load more!
		if(lastInScreen == totalItemCount && totalItemCount != 0 && mEndOfList == false)
		{
			Log.d("BgBiz", "Load next items");
			mLoadBusinessToDisplayAsyncTask = new LoadBusinessToDisplayAsyncTask();
			mLoadBusinessToDisplayAsyncTask.execute();
		} 
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
	}

	private void stopLocationLoading(){
		if(mBizMyLocation != null)
			mBizMyLocation.stopLocationUpdates();

		if(mLoadUserLocationAsyncTask != null && 
				!mLoadUserLocationAsyncTask.getStatus().equals(AsyncTask.Status.FINISHED))
			mLoadUserLocationAsyncTask.cancel(true);

		if(mLoadBusinessToDisplayAsyncTask != null && 
				!mLoadBusinessToDisplayAsyncTask.getStatus().equals(AsyncTask.Status.FINISHED))
			mLoadBusinessToDisplayAsyncTask.cancel(true);
	}

	@Override
	public void onStop(){
		Log.d("BgBiz", "onStop called from BizListController");
		stopLocationLoading();
		super.onStop();
	}

	@Override
	public void onPause(){
		Log.d("BgBiz", "onPause called from BizListController");
		stopLocationLoading();
		super.onPause();
	}

	/*
	 * Alerts the user to turn on the GPS
	 */
	private void displayAlertMessageToEnableGPS() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(getParent());
		builder.setMessage("Yout GPS seems to be disabled, do you want to enable it?")
		.setCancelable(false)
		.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(final DialogInterface dialog, final int id) {
				dialog.cancel();
			}
		});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	private void toggleLayout(boolean isLoading){
		if (isLoading){
			mBizListView.setVisibility(View.GONE);

			//Loading layout
			LinearLayout layoutBizListLoading = (LinearLayout)findViewById(R.id.layoutBizListLoading);
			layoutBizListLoading.setVisibility(View.VISIBLE);
			findViewById(R.id.progressbarBizListLoading).setVisibility(View.VISIBLE);
			((TextView)findViewById(R.id.txtBizListLoading)).setText(R.string.bizListLoading);

			//Change action bar state
			findViewById(R.id.actionbar_imgbtn_findlocation).setVisibility(View.GONE);
			findViewById(R.id.actionbar_loading).setVisibility(View.VISIBLE);
		}else{
			//Loading is done. 
			if (mBizListAdapter.isEmpty()){		//If empty, display no result found.
				//Update textview
				TextView txtBizListLoading = (TextView)findViewById(R.id.txtBizListLoading);
				txtBizListLoading.setText(R.string.bizNoResult);

				LinearLayout layoutBizListLoading = (LinearLayout)findViewById(R.id.layoutBizListLoading);
				layoutBizListLoading.setVisibility(View.VISIBLE);
				findViewById(R.id.progressbarBizListLoading).setVisibility(View.GONE);

				mBizListView.setVisibility(View.GONE);
			} else {

				LinearLayout layoutBizListLoading = (LinearLayout)findViewById(R.id.layoutBizListLoading);
				layoutBizListLoading.setVisibility(View.GONE);

				mBizListView.setVisibility(View.VISIBLE);
			}

			//Change action bar state
			findViewById(R.id.actionbar_loading).setVisibility(View.GONE);
			findViewById(R.id.actionbar_imgbtn_findlocation).setVisibility(View.VISIBLE);

			//Temp code: display location
			try{
				Geocoder gcd = new Geocoder(getParent(), Locale.getDefault());
				List<Address> addresses = gcd.getFromLocation(mUserLocation.getLatitude(), mUserLocation.getLongitude(), 1);
				if(addresses != null && addresses.size() > 0){
					((TextView) findViewById(R.id.actionbar_txtheader)).setText(addresses.get(0).getLocality());
				}
			} catch(Exception e){
				Log.d("BG", "Error occured while trying to get address of the user location"+e.getLocalizedMessage());
			}
		}
	}

	/**
	 * Class to find current user location
	 * @author wns349
	 *
	 */
	private class LoadUserLocationAsyncTask extends AsyncTask<Context, Void, Void>
	{
		private Context mContext;

		public LoadUserLocationAsyncTask(Context context){
			this.mContext = context;
		}

		protected void onPreExecute(){
			//Lock
			if(mIsLoadUserLocationAsyncTaskRunning)
				this.cancel(true);
			else
				mIsLoadUserLocationAsyncTaskRunning = true;

			toggleLayout(true);
			//Reset list
			mBizListAdapter.clear();
			mBizArrayList.clear();
			mPage = 1;
			mEndOfList = false;
			mIsLocationAvailable = false;

			mBizMyLocation = new BizMyLocation();
			mBizMyLocation.getLocation(this.mContext, mLocationResult);
		}

		protected Void doInBackground(Context... params)
		{
			//Wait x seconds to see if we can get a location from either network or GPS, otherwise stop
			while (!mIsLocationAvailable) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
			return null;
		}

		protected void onPostExecute(final Void unused)
		{
			if (mUserLocation != null)
			{
				Log.d("BgBiz", "Location found!: "+mUserLocation.getLatitude()+" | "+mUserLocation.getLongitude());
				Log.d("BgBiz", "Load new items");
			}
			else
			{
				Log.d("BgBiz", "Location not found!");
				Toast.makeText(getParent(), "Unable to identify your location.", Toast.LENGTH_SHORT).show();

				if(mUserLocation == null){
					mUserLocation = new Location("CurrentLocation");
					mUserLocation.setLatitude(40.714353);
					mUserLocation.setLongitude(-74.005973);
					mPage = 1;
				}
			}

			mLoadBusinessToDisplayAsyncTask = new LoadBusinessToDisplayAsyncTask();
			mLoadBusinessToDisplayAsyncTask.execute();

			mIsLoadUserLocationAsyncTaskRunning = false;
		}

		private LocationResult mLocationResult = new LocationResult()
		{
			@Override
			public void gotLocation(final Location location)
			{
				mUserLocation = location;
				mIsLocationAvailable = true;
			}
		};
	}

	private class LoadBusinessToDisplayAsyncTask extends AsyncTask<Context, Void, Void>
	{
		private ArrayList<Business> tmpBizListAdapter;

		protected void onPreExecute(){
			//Lock
			if(mIsLoadBusinessToDisplayAsyncTaskRunning)
				this.cancel(true);
			else
				mIsLoadBusinessToDisplayAsyncTaskRunning = true;

			tmpBizListAdapter = null;
		}
		protected Void doInBackground(Context... params){
			//Get new items
			float myLat = (float) mUserLocation.getLatitude();
			float myLng = (float) mUserLocation.getLongitude();

			BusinessJSON bizDB = new BusinessJSON(myLat, myLng, mPage);		
			tmpBizListAdapter = bizDB.getBizListJSON();

			if (tmpBizListAdapter == null){
				mEndOfList = true;
			}
			else{
				mPage++;
			}
			Log.d("BgBiz", "Search paramter: ("+mMyLat+", "+mMyLng+" ) with page: "+mPage);
			return null;
		}
		protected void onPostExecute(final Void unused){
			if (mEndOfList){
				//Remove Loading footer from the list
				boolean isRemoved = mBizListView.removeFooterView(mBizListViewFooter);
				Log.d("BgBiz", "Removing footer result: "+isRemoved);
			}
			else {
				for(Business biz : tmpBizListAdapter){
					mBizListAdapter.add(biz);
				}

				//Tell to the adapter that changes have been made, this will cause the list to refresh
				mBizListAdapter.notifyDataSetChanged();
				Log.d("BgBiz", "mBizListAdpater notified!");	
			}

			//Change the header
			toggleLayout(false);

			mIsLoadBusinessToDisplayAsyncTaskRunning = false;
		}
	}
}