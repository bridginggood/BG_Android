package com.bridginggood.Biz;

import java.io.Serializable;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
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
	private ArrayList<Business> mBizArrayList;		//Stores Business objects in array
	private BizMyLocation mBizMyLocation;			//To find out my current location
	private float mMyLat, mMyLng, mDistanceRadius;	//For search
	private static final float MAX_DIST = 10.0f;	//Maximum search radius

	private boolean mIsListLoadingMore, mStopLoadingMore;					//To lock the list while handling onScroll event

	private BizListAdapter mBizListAdapter;			//ListView adapter
	private ListView mBizListView;					//ListView
	private View mBizListViewFooter;				//ListView footer - Loading message

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bizlist_view);
		
		mBizArrayList = new ArrayList<Business>();

		//Initialize distnaceRadius to 5 miles
		mDistanceRadius = 1.0f;	//miles
		mMyLat = 37.507275f; mMyLng = 126.939812f; //sample random location

		//Initialize myLocation to get current loc
		mBizMyLocation = new BizMyLocation(getParent());
		mBizMyLocation.getLocation(this, locationResult);

		//Initialize listview
		this.initListView();

		//Attach scroll listener
		mBizListView.setOnScrollListener(this);
		mIsListLoadingMore = true;
		mStopLoadingMore = false;

		//Initialize button
		initButtonViews();
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
	}

	//Callback - once you got the location of the user
	public LocationResult locationResult = new LocationResult(){
		@Override
		public void gotLocation(final Location location){
			if(location == null)
				return;
			
			Log.d("BG", "LocationResult called with  "+location.getLatitude() + " , "+location.getLongitude());
			//Got the location!, store them as current location
			mMyLat = (float) location.getLatitude();
			mMyLng = (float) location.getLongitude();
			Log.d("BG", "myLat: "+mMyLat+" , myLng: "+mMyLng);

			//Change the header
			TextView txtBizListLoading = (TextView)findViewById(R.id.txtBizListLoading);
			txtBizListLoading.setVisibility(View.INVISIBLE);
			mBizListView.setVisibility(View.VISIBLE);

			//Load list around the user
			Log.d("BG", "Load new items");
			Thread thread = new Thread(null, loadMoreListItems);
			thread.start();
		}
	};

	private Runnable loadMoreListItems = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub

			//set flag so items are not loaded twice at the same time
			mIsListLoadingMore = true;

			//Simulate a delay, delete this on a production environment
			//try{ Thread.sleep(50);}catch(Exception e){}

			//Increase mDistanceRadius to search for more result
			//Increase exponentially
			mDistanceRadius += 1.0f; 

			//Get new items
			BusinessJSON bizDB = new BusinessJSON(mMyLat, mMyLng, mDistanceRadius);
			mBizArrayList = bizDB.getBizListJSON();

			Log.d("BG", "ListLoaded with size: "+mBizArrayList.size()+" . Search paramter: ("+mMyLat+", "+mMyLng+
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
				for(Business biz : mBizArrayList)
					mBizListAdapter.add(biz);
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
				Log.d("BG", "Removing footer result: "+isRemoved);
			}

			//Tell to the adapter that changes have been made, this will cause the list to refresh
			mBizListAdapter.notifyDataSetChanged();
			Log.d("BG", "mBizListAdpater notified!");

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
				Log.d("BG", "Button Clicked");
				BizActivityGroup bizActivityGroup = ((BizActivityGroup)getParent());
				View newView = bizActivityGroup.getBizActivityGroup().getLocalActivityManager()
						.startActivity("BizMap", intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
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
		Log.d("BG", "Refresh clicked");
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
		mBizMyLocation.getLocation(this, locationResult);
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
		Log.d("BG", "Back Pressed from BizList");
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
			Log.d("BG", "Load next items");
			Thread thread = new Thread(null, loadMoreListItems);
			thread.start();
		} 
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
	}
}