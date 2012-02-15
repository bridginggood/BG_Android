package com.bridginggood.Biz;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bridginggood.R;
import com.bridginggood.Biz.BizMyLocation.LocationResult;
import com.bridginggood.DB.BusinessJSON;

public class BizListController extends Activity implements OnScrollListener{
	private static final float MAX_DIST = 10.0f;	//Maximum search radius
	private static final int MAX_TIME_TO_WAIT_LOCATION_SEARCH = 15000;

	private ArrayList<Business> mBizArrayList;		//Stores Business objects in array
	private BizMyLocation mBizMyLocation;
	private Location mCurrentLocation;
	private float mMyLat, mMyLng, mDistanceRadius;	//For search

	private boolean mIsListLoadingMore, mStopLoadingMore;					//To lock the list while handling onScroll event

	private BizListAdapter mBizListAdapter;			//ListView adapter
	private ListView mBizListView;					//ListView
	private View mBizListViewFooter;				//ListView footer - Loading message
	
	private boolean mIsLocationAvailable = false;
	private LocationControl mLocationControlTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bizlist_view);

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
		mCurrentLocation = new Location("CurrentLocation");
		mCurrentLocation.setLatitude(40.714353);
		mCurrentLocation.setLongitude(-74.005973);
		mDistanceRadius = 1.0f;	//miles

		//Initialize myLocation to get current loc
		mBizMyLocation = new BizMyLocation();
		mBizMyLocation.getLocation(this, locationResult);
		mLocationControlTask = new LocationControl();
		mLocationControlTask.execute(this);
		
		//Initialize listview
		this.initListView();

		//Initialize button
		initButtonViews();
	}
	
	private class LocationControl extends AsyncTask<Context, Void, Void>
    {
        private final ProgressDialog dialog = new ProgressDialog(BizListController.this);
        protected void onPreExecute()
        {
            //this.dialog.setMessage("Searching");
            //this.dialog.show();
        }
        protected Void doInBackground(Context... params)
        {
            //Wait x seconds to see if we can get a location from either network or GPS, otherwise stop
            Long t = Calendar.getInstance().getTimeInMillis();
            while (!mIsLocationAvailable && Calendar.getInstance().getTimeInMillis() - t < MAX_TIME_TO_WAIT_LOCATION_SEARCH) {
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
            if(this.dialog.isShowing())
            {
                this.dialog.dismiss();
            }

            if (mCurrentLocation != null)
            {
            	Log.d("BgBiz", "Location found!: "+mCurrentLocation.getLatitude()+" | "+mCurrentLocation.getLongitude());
                //useLocation();
            	
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
            }
        }
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
		mStopLoadingMore = false;
	}
	
	public LocationResult locationResult = new LocationResult()
    {
        @Override
        public void gotLocation(final Location location)
        {
            mCurrentLocation = new Location(location);
            mIsLocationAvailable = true;
        }
    };

	private Runnable loadMoreListItems = new Runnable(){
		@Override
		public void run() {
			//set flag so items are not loaded twice at the same time
			mIsListLoadingMore = true;
			
			//Get new items
			float myLat = (float) mCurrentLocation.getLatitude();
			float myLng = (float) mCurrentLocation.getLongitude();
			
			BusinessJSON bizDB = new BusinessJSON(myLat, myLng, mDistanceRadius);
			mBizArrayList = bizDB.getBizListJSON();

			//Increase mDistanceRadius to search for more result
			//Increase exponentially
			mDistanceRadius += 1.0f; 

			Log.d("BgBiz", "ListLoaded with size: "+mBizArrayList.size()+" . Search paramter: ("+mMyLat+", "+mMyLng+
					" ) within "+mDistanceRadius);

			runOnUiThread(updateListView);
		}
	};

	private Runnable updateListView = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub

			//Clear mBizListAdapter
			mBizListAdapter.clear();

			//Loop through the new items and add them to the adapter
			if(mBizArrayList != null && mBizArrayList.size()>0){
				for(Business biz : mBizArrayList){
					mBizListAdapter.add(biz);
				}
			}

			//Stop search for more item in the future once it reaches max_dist.
			if(mDistanceRadius == MAX_DIST){
				mStopLoadingMore = true;

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

			//Tell to the adapter that changes have been made, this will cause the list to refresh
			mBizListAdapter.notifyDataSetChanged();
			Log.d("BgBiz", "mBizListAdpater notified!");

			//Done loading more
			mIsListLoadingMore = false;	
		}
	};

	//Load button to BizMap.java
	public void initButtonViews(){

		//Search in Map button
		Button btnGoBizMap = (Button) findViewById(R.id.btnGoToBizMap);
		//Button Handler
		final Intent intent = new Intent(this, BizMapController.class);
		btnGoBizMap.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Log.d("BgBiz", "Button Clicked");
				BizActivityGroup bizActivityGroup = ((BizActivityGroup)getParent());
				View newView = bizActivityGroup.getBizActivityGroup().getLocalActivityManager()
						.startActivity("BizMapController", intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
						.getDecorView();

				bizActivityGroup.getBizActivityGroup().replaceView(newView);	//Replace View
			}
		});

		//Refresh my location button
		Button btnRefreshMyLoc = (Button) findViewById(R.id.btnRefreshMyLoc);
		btnRefreshMyLoc.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				refreshList();
			}
		});
	}

	private void refreshList(){
		Log.d("BgBiz", "Refresh clicked");
		stopLocationLoading();
		mBizArrayList.clear();
		mBizListAdapter.clear();

		mDistanceRadius = 1.0f;	//miles
		mIsListLoadingMore = true;
		mStopLoadingMore = false;

		//If footer does not exist, add it
		if(mBizListView.getFooterViewsCount() == 0)
			mBizListView.addFooterView(mBizListViewFooter);

		//Initialize views
		TextView txtBizListNotFound = (TextView)findViewById(R.id.txtBizListNoResult);
		txtBizListNotFound.setVisibility(View.INVISIBLE);
		TextView txtBizListLoading = (TextView)findViewById(R.id.txtBizListLoading);
		txtBizListLoading.setVisibility(View.VISIBLE);
		mBizListView.setVisibility(View.INVISIBLE);

		//Initialize myLocation to get current loc
		//mBizMyLocation.getLocation();
		//mCurrentLocation = mBizMyLocation.getCurrentLocation();
		mBizMyLocation.getLocation(this, locationResult);
		mLocationControlTask = new LocationControl();
		mLocationControlTask.execute(this);
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
			Intent intent = new Intent(BizListController.this, BizDetailController.class);

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
		if(lastInScreen == totalItemCount && totalItemCount != 0 && mIsListLoadingMore == false && mStopLoadingMore == false)
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
		if (mLocationControlTask != null || !mLocationControlTask.isCancelled())
			mLocationControlTask.cancel(true);
	}
	
	@Override
	public void onStop(){
		Log.d("BgBiz", "onStop called from BizListController");
		stopLocationLoading();
		super.onStop();
	}
}