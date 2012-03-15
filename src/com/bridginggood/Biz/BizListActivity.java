/**
 * Created by: Junsung Lim
 * 
 * Lists down the businesses around the user
 */
package com.bridginggood.Biz;

import java.io.Serializable;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bridginggood.R;
import com.bridginggood.Biz.BizMyLocation.LocationResult;
import com.bridginggood.DB.BusinessJSON;

public class BizListActivity extends Activity implements OnScrollListener{
	private ArrayList<Business> mBizArrayList, mTmpBizListAdapter;		//Stores Business objects in array
	private BizMyLocation mBizMyLocation;
	private Location mUserLocation;
	private float mMyLat, mMyLng;	//For API search
	private int mPage;				//For API search

	private boolean mIsListLoadingMore, mEndOfList;					//To lock the list while handling onScroll event

	private BizListAdapter mBizListAdapter;			//ListView adapter

	private ListView mBizListView;					//ListView
	private View mBizListViewFooter;				//ListView footer - Loading message

	private boolean mIsLocationAvailable = false;
	private LoadUserLocationAndDisplayAsyncTask mLoadUserLocationAndDisplayAsyncTask;

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

		/*
		 * Initialize location variables to NYC City Center
		 * 
		 * Set default location to New York City
		 */
		//sample random location
		mUserLocation = new Location("CurrentLocation");
		mUserLocation.setLatitude(40.714353);
		mUserLocation.setLongitude(-74.005973);
		mPage = 1;
		
		mEndOfList = false;

		//Initialize myLocation to get current loc
		retrieveUserLocation();

		//Initialize listview
		this.initListView();

		//Initialize button
		this.initButtonViews();
	}
	
	private void retrieveUserLocation(){
		mBizMyLocation = new BizMyLocation();
		mBizMyLocation.getLocation(this, mLocationResult);

		mLoadUserLocationAndDisplayAsyncTask = new LoadUserLocationAndDisplayAsyncTask(getParent());
		mLoadUserLocationAndDisplayAsyncTask.execute(this);
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
		mBizListView.setVisibility(View.INVISIBLE);

		//Attach scroll listener
		mBizListView.setOnScrollListener(this);
		mIsListLoadingMore = true;
		mEndOfList = false;
	}

	public LocationResult mLocationResult = new LocationResult()
	{
		@Override
		public void gotLocation(final Location location)
		{
			mUserLocation = location;
			mIsLocationAvailable = true;
		}
	};

	private Runnable loadMoreListItems = new Runnable(){
		@Override
		public void run() {
			//set flag so items are not loaded twice at the same time
			mIsListLoadingMore = true;
			mTmpBizListAdapter = null;

			//Get new items
			float myLat = (float) mUserLocation.getLatitude();
			float myLng = (float) mUserLocation.getLongitude();

			BusinessJSON bizDB = new BusinessJSON(myLat, myLng, mPage);		
			mTmpBizListAdapter = bizDB.getBizListJSON();
			
			if (mTmpBizListAdapter == null){
				mEndOfList = true;
			}
			else{
				mPage++;
			}
			Log.d("BgBiz", "ListLoaded with size: "+mBizArrayList.size()+" . Search paramter: ("+mMyLat+", "+mMyLng+
					" ) with page: "+mPage);
			runOnUiThread(updateListView);
		}
	};

	private Runnable updateListView = new Runnable(){
		@Override
		public void run() {
			if (mEndOfList){
				//Display found none message if the list adapter is empty
				if(mBizListAdapter.isEmpty())
				{
					TextView txtBizListNotFound = (TextView)findViewById(R.id.txtBizListNoResult);
					txtBizListNotFound.setVisibility(View.VISIBLE);
				}

				//Remove Loading footer from the list
				boolean isRemoved = mBizListView.removeFooterView(mBizListViewFooter);
				Log.d("BgBiz", "Removing footer result: "+isRemoved);
			}
			else {
				for(Business biz : mTmpBizListAdapter){
					mBizListAdapter.add(biz);
				}
				
				//Tell to the adapter that changes have been made, this will cause the list to refresh
				mBizListAdapter.notifyDataSetChanged();
				Log.d("BgBiz", "mBizListAdpater notified!");		
			}
			//Done loading more
			mIsListLoadingMore = false;	
		}
	};

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

		//Refresh my location button
		Button btnRefreshMyLoc = (Button) findViewById(R.id.btnRefreshMyLoc);
		btnRefreshMyLoc.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				refreshList();
			}
		});

		//Set ListView button as selected
		Button btnGoListView = (Button) findViewById(R.id.btnGoToListView);
		btnGoListView.setPressed(true);
		btnGoListView.setEnabled(false);
	}

	private void refreshList(){
		/**/
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

			BizActivityGroup bizActivityGroup = ((BizActivityGroup)getParent());
			View newView = bizActivityGroup.getBizActivityGroup().getLocalActivityManager()
					.startActivity("BizDetail", intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
					.getDecorView();

			bizActivityGroup.getBizActivityGroup().replaceView(newView);	//Replace View

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
		if(lastInScreen == totalItemCount && totalItemCount != 0 && mIsListLoadingMore == false && mEndOfList == false)
		{
			Log.d("BgBiz", "Load next items");
			Thread thread = new Thread(null, loadMoreListItems);
			thread.start();
		} 
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
	}

	private void stopLocationLoading(){
		if(mBizMyLocation != null)
			mBizMyLocation.stopLocationUpdates();
		if (mLoadUserLocationAndDisplayAsyncTask != null || !mLoadUserLocationAndDisplayAsyncTask.isCancelled())
			mLoadUserLocationAndDisplayAsyncTask.cancel(true);
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

	/**
	 * Class to find current user location
	 * @author wns349
	 *
	 */
	private class LoadUserLocationAndDisplayAsyncTask extends AsyncTask<Context, Void, Void>
	{
		private Context mContext;

		public LoadUserLocationAndDisplayAsyncTask(Context context){
			this.mContext = context;
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

				/*
				 * Do when current location is found
				 */
				//Change the header
				TextView txtBizListLoading = (TextView)findViewById(R.id.txtBizListLoading);
				txtBizListLoading.setVisibility(View.INVISIBLE);
				mBizListView.setVisibility(View.VISIBLE);

				Log.d("BgBiz", "Load new items");
				new Thread(null, loadMoreListItems).start();
			}
			else
			{
				Log.d("BgBiz", "Location not found!");
				//Couldn't find location, do something like show an alert dialog
				Toast.makeText(this.mContext, "Unable to identify your location.", Toast.LENGTH_SHORT).show();
			}
		}
	}
}