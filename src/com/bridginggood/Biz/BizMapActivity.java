package com.bridginggood.Biz;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;

import com.bridginggood.R;
import com.bridginggood.Biz.BizMyLocation.LocationResult;
import com.bridginggood.DB.BusinessJSON;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class BizMapActivity extends MapActivity{ 
	private static final int MAX_TIME_TO_WAIT_LOCATION_SEARCH = 15000;

	private BizExtendedMapView mMapView;			//MapView
	private ArrayList<GeoPoint> mGeoPointArrayList;		//GeoPoints that have already been added to the map for display

	private ProgressDialog mProgressDialog;			
	private BizMyLocation mBizMyLocation;
	private LoadBusinessAndDisplayAsyncTask mLoadBusinessAndDisplayAsyncTask;

	private Location mCurrentLocation;
	private float mDistanceRadius = 1.0f;

	private boolean mIsLoadingBizLocation = false; 		//Lock

	private boolean mIsLocationAvailable = false;
	private LocationControl mLocationControlTask;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bizmap_layout);

		Log.d("BgMap", "Welcome to BizMapController");

		//Initialize ArrayLists
		mGeoPointArrayList = new ArrayList<GeoPoint>();
		
		//Initialize lock
		mIsLoadingBizLocation = false;
		//Initialize mapview
		initMapView();
		initButtonViews();

		//retrieveCurrentLocation();	
	}

	/**
	 * Initilaize mapview
	 */
	private void initMapView(){
		mMapView = (BizExtendedMapView) findViewById(R.id.bizMapView);
		mMapView.setBuiltInZoomControls(true);
		mMapView.setClickable(true);

		//Initial map point. Start from NYC!
		float mapCenterLat = 40.714353f;
		float mapCenterLng = -74.005973f;	
		GeoPoint tmpGeoPoint = new GeoPoint(convFloatToIntE6(mapCenterLat), convFloatToIntE6(mapCenterLng));

		MapController mapController = mMapView.getController();
		mapController.setZoom(15);
		mapController.setCenter(tmpGeoPoint);

		//Handle map change - zoom and span
		mMapView.setOnChangeListener(new MapViewChangeListener());
	}

	//Load button to BizMap.java
	public void initButtonViews(){

		//Set MapView button as selected
		Button btnGoBizMap = (Button) findViewById(R.id.btnGoToBizMap);
		btnGoBizMap.setPressed(true);
		btnGoBizMap.setEnabled(false);


		//Action to ListView button
		Button btnGoListView = (Button) findViewById(R.id.btnGoToListView);
		//Button Handler
		final Intent intent = new Intent(this, BizListActivity.class);
		btnGoListView.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Log.d("BgMap", "Button Clicked");
				BizActivityGroup bizActivityGroup = ((BizActivityGroup)getParent());
				View newView = bizActivityGroup.getBizActivityGroup().getLocalActivityManager()
						.startActivity("BizListActivity", intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
						.getDecorView();

				bizActivityGroup.getBizActivityGroup().changeView(newView);	//Replace View
			}
		});
	}

	private void retrieveCurrentLocation(){
		//Start progress dialog
		mProgressDialog = ProgressDialog.show(getParent(), "", "Identifying your location...", true, true);
		mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				if(mBizMyLocation!=null){
					//TODO: Implement cancel action
					if(mLocationControlTask!=null)
						mLocationControlTask.cancel(true);
					Log.d("BgMap", "mLoadMapThread killed by the user");				
				}
			}
		});

		//Initialize myLocation to get current loc
		mBizMyLocation = new BizMyLocation();
		mBizMyLocation.getLocation(this, locationResult);

		mLocationControlTask = new LocationControl();
		mLocationControlTask.execute(this);
	}

	public LocationResult locationResult = new LocationResult()
	{
		@Override
		public void gotLocation(final Location location)
		{
			if(location != null)
				mCurrentLocation = new Location(location);

			mIsLocationAvailable = true;
		}
	};

	private void retrieveBizLocation(){
		mIsLoadingBizLocation = true;	//Lock
		mLoadBusinessAndDisplayAsyncTask = new LoadBusinessAndDisplayAsyncTask();
		mLoadBusinessAndDisplayAsyncTask.execute(this);
	}


	/**
	 * Get Arraylist of GeoPoints to be marked on the map
	 */
	private ArrayList<Business> getBusinessListToShow(){
		float lat = convIntE6ToFloat(mMapView.getMapCenter().getLatitudeE6());
		float lng = convIntE6ToFloat(mMapView.getMapCenter().getLongitudeE6());
		BusinessJSON bizDB = new BusinessJSON(lat, lng, mDistanceRadius);
		return bizDB.getBizMapJSON();
	}

	private void createMyLocationOverlayOnMapView(){
		float myLat = (float)mCurrentLocation.getLatitude();
		float myLng = (float)mCurrentLocation.getLongitude();

		GeoPoint myPoint = new GeoPoint (convFloatToIntE6(myLat), convFloatToIntE6(myLng));
		MapController mapController = mMapView.getController();
		mapController.animateTo(myPoint);
		Log.d("BgMap", "createUserPositionOverlayOnMap :"+myLat+" , "+myLng);

		// Marker
		MapView.LayoutParams mapMarkerParams = new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, myPoint,
				MapView.LayoutParams.TOP_LEFT);
		ImageView mapMarker = new ImageView(
				getApplicationContext());
		mapMarker.setImageResource(R.drawable.icon);
		mMapView.addView(mapMarker, mapMarkerParams);
	}

	//To create overlay on the map
	private void createBusinessOverlayOnMapView(ArrayList<Business> businesArrayList){
		Log.d("BgMap", "CreateOverLay called");
		List<Overlay> mapOverlays = mMapView.getOverlays();
		//TODO: change the drawable
		Drawable drawable = this.getResources().getDrawable(R.drawable.shop_default);
		BizMapOverlay itemizedOverlay = new BizMapOverlay(drawable, mMapView, businesArrayList, getParent());	//getParent() since alert only works on the upper-most context.

		//Create GeoPoints
		GeoPoint point = null;
		OverlayItem overlayItem = null;
		boolean isNewGeopoint = true;
		for(Business biz : businesArrayList){
			if (biz==null) break;
			isNewGeopoint = true;

			point = new GeoPoint( convFloatToIntE6(biz.getBizLat()) , convFloatToIntE6(biz.getBizLng()));	//Create GeoPoint
			
			//Check if the point already exists on the map.
			for(GeoPoint gp : mGeoPointArrayList){
				if (gp.getLatitudeE6() == point.getLatitudeE6() && gp.getLongitudeE6() == gp.getLongitudeE6()){
					isNewGeopoint = false;
					break;
				}
			}
			
			//Add only new points to the map
			if(isNewGeopoint){
				Log.d("BgMap", "Added new point:"+biz.getBizName());
				overlayItem = new OverlayItem(point, biz.getBizName(), biz.getBizAddress());					//Store name and address as descriptions of GeoPoint
				itemizedOverlay.addOverlay(overlayItem);
				mapOverlays.add(itemizedOverlay);
				
				//Add this geopoint to the arraylist to mark it as used
				mGeoPointArrayList.add(point);
			}
		}
		mMapView.postInvalidate();
	}


	//Convert float lat, lng to int lat lng for geocode
	private int convFloatToIntE6(float num){
		return (int)(num*1E6);
	}

	private float convIntE6ToFloat(int num){
		return (float)(num/1E6);
	}

	@Override 
	protected boolean isRouteDisplayed() {
		return false;
	}

	// To handle back button
	public void onBackPressed() { //on Back
		Log.d("BgMap", "Back Pressed from BizMap");
		stopLocationLoading();
		BizActivityGroup parent = ((BizActivityGroup)getParent());
		parent.back();
	}

	/**
	 * Update mDistanceRadius to the user's current screen radius for new search
	 * 
	 * Calculated by taking Top-Right and Bottom-Left corners of screen
	 */
	private void updateDistanceRadius(){
		float latDiff = mMapView.getLatitudeSpan() / 2;
		float lngDiff = mMapView.getLongitudeSpan() / 2;
		GeoPoint mapCenter = mMapView.getMapCenter();

		Log.d("BgMap", "LatSpan, LngSpan, Center: "+mMapView.getLatitudeSpan()+" , "+mMapView.getLongitudeSpan()+", "+mapCenter);

		//Find distance using span
		float p1Lat = (float) ((mapCenter.getLatitudeE6()+latDiff)/1E6);
		float p1Lng = (float) ((mapCenter.getLongitudeE6()+lngDiff)/1E6);
		float p2Lat = (float) ((mapCenter.getLatitudeE6()-latDiff)/1E6);
		float p2Lng = (float) ((mapCenter.getLongitudeE6()-lngDiff)/1E6);

		Location locTR = new Location("LocationTopRight");
		Location locBL = new Location("LocationBottomLeft");
		locTR.setLatitude(p1Lat);
		locTR.setLongitude(p1Lng);
		locBL.setLatitude(p2Lat);
		locBL.setLongitude(p2Lng);

		float dist = locTR.distanceTo(locBL);
		//Convert distanceAway from meter to miles
		mDistanceRadius = (float) ((float)(dist/1000)/1.6);
	}

	/**
	 * @author wns349
	 *
	 * Listener called when there is a map change by the user.
	 * 
	 * Implements BizExtendedMapView.OnChangeListner.
	 * 
	 * onChange() is called every 0.5 seconds. (Can be modified within OnChangeListener)
	 */
	private class MapViewChangeListener implements BizExtendedMapView.OnChangeListener
	{
		@Override
		public void onChange(MapView view, GeoPoint newCenter, GeoPoint oldCenter, int newZoom, int oldZoom)
		{
			// Check values
			if ((!newCenter.equals(oldCenter)) || (newZoom != oldZoom))
			{
				Log.d("BgMapOnChange", "onChanged called. center: "+oldCenter+" -> "+newCenter + ". zoom: "+oldZoom+" -> "+newZoom);

				//If locked, do not execute this
				if(mIsLoadingBizLocation)
					return;

				if(newZoom != oldZoom){
					//If zoom level changed, mDistanceRadius must be updated.
					updateDistanceRadius();
				}

				//Create new overlays
				retrieveBizLocation();
			}
		}
	}

	//Thread to load businesses to display on mapview
	private class LoadBusinessAndDisplayAsyncTask extends AsyncTask<Context, Void, Void>{
		protected Void doInBackground(Context... params){
			Log.d("BgMap", "Executing LoadBusinessAndDisplayAsyncTask");
			ArrayList<Business> tmpBusinessArrayList = getBusinessListToShow();

			//Create overlays for business only if business list is not empty
			if (tmpBusinessArrayList != null)
				createBusinessOverlayOnMapView(tmpBusinessArrayList);

			//Unlock
			mIsLoadingBizLocation = false;

			return null;
		}
	}


	private class LocationControl extends AsyncTask<Context, Void, Void>
	{
		private final ProgressDialog dialog = new ProgressDialog(BizMapActivity.this);
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

			if(mProgressDialog.isShowing())
				mProgressDialog.dismiss();

			if (mCurrentLocation != null)
			{
				Log.d("BgMap", "Location found!: "+mCurrentLocation.getLatitude()+" | "+mCurrentLocation.getLongitude());
				//useLocation();

				/*
				 * Do when current location is found
				 */
				createMyLocationOverlayOnMapView();
				if(!mIsLoadingBizLocation){
					Log.d("BgMap", "retrieveBizLocation already called");
					mIsLoadingBizLocation = true;
					retrieveBizLocation();
				}
			}
			else
			{
				Log.d("BgMap", "Location not found!");
				//Couldn't find location, do something like show an alert dialog
				if(!mIsLoadingBizLocation){
					Log.d("BgMap", "retrieveBizLocation already called");
					mIsLoadingBizLocation = true;
					retrieveBizLocation();
				}
			}
		}
	}
	@Override
	public void onStop(){
		Log.d("BgMap", "onStop called from BizMapController");
		stopLocationLoading();
		super.onStop();
	}
	private void stopLocationLoading(){
		if(mBizMyLocation!= null)
			mBizMyLocation.stopLocationUpdates();
		if(mLoadBusinessAndDisplayAsyncTask != null)
			mLoadBusinessAndDisplayAsyncTask.cancel(true);
		if(mLocationControlTask != null)
			mLocationControlTask.cancel(true);
	}
}