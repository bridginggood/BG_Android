package com.bridginggood.Biz;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
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

public class BizMapController extends MapActivity{ 
	private static final int MAX_TIME_TO_WAIT_LOCATION_SEARCH = 15000;

	private BizExtendedMapView mMapView;			//MapView
	private ArrayList<Business> mBizArrayList;		//Business list

	private ProgressDialog mProgressDialog;			
	private BizMyLocation mBizMyLocation;
	private LoadBizThread mLoadBizThread;

	private Location mCurrentLocation;
	private float mDistanceRadius = 1.0f;

	private boolean mIsLoadingBizLocation = false; 		//Lock

	private final int STATUS_LOADED_BIZ_LOC = 1;

	private boolean mIsLocationAvailable = false;
	private LocationControl mLocationControlTask;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bizmap);

		Log.d("BgBiz", "Welcome to BizMapController");

		//Initialize ArrayLists
		mBizArrayList = new ArrayList<Business>();
		//Initialize trigger
		mIsLoadingBizLocation = false;
		//Initialize mapview
		initMapView();

		retrieveCurrentLocation();	
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
					Log.d("BgBiz", "mLoadMapThread killed by the user");				
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
			mCurrentLocation = new Location(location);
			mIsLocationAvailable = true;
		}
	};

	private void retrieveBizLocation(){
		Log.d("BgBiz", "retrieveBizLocation called.");
		mIsLoadingBizLocation = true;
		mLoadBizThread = new LoadBizThread(handlerLoadBizThread);
		mLoadBizThread.start();
	}

	private void initMapView(){
		mMapView = (BizExtendedMapView) findViewById(R.id.bizMapView);
		mMapView.setBuiltInZoomControls(true);
		mMapView.setClickable(true);

		//Initial map point. Start from NYC!
		float mapCenterLat = 40.714353f;
		float mapCenterLng = -74.005973f;

		mCurrentLocation = new Location("CurrentLocation");
		mCurrentLocation.setLatitude(mapCenterLat);
		mCurrentLocation.setLongitude(mapCenterLng);

		GeoPoint tmpGeoPoint = new GeoPoint(convFloatToIntE6(mapCenterLat), convFloatToIntE6(mapCenterLng));
		MapController mapController = mMapView.getController();
		mapController.setZoom(15);
		mapController.setCenter(tmpGeoPoint);

		Log.d("BgBiz", "Adding listener to the map");
		//Handle map change - zoom and span
		mMapView.setOnChangeListener(new MapViewChangeListener());
	}

	/**
	 * Get Arraylist of GeoPoints to be marked on the map
	 */
	private void updateBizArrayList(){
		float lat = convIntE6ToFloat(mMapView.getMapCenter().getLatitudeE6());
		float lng = convIntE6ToFloat(mMapView.getMapCenter().getLongitudeE6());
		BusinessJSON bizDB = new BusinessJSON(lat, lng, mDistanceRadius);
		mBizArrayList = bizDB.getBizListJSON();
		Log.d("BgBiz", "mBizArrayList:"+mBizArrayList.toString());
	}

	private void createMyLocationOverlayOnMapView(){
		float myLat = (float)mCurrentLocation.getLatitude();
		float myLng = (float)mCurrentLocation.getLongitude();

		GeoPoint myPoint = new GeoPoint (convFloatToIntE6(myLat), convFloatToIntE6(myLng));
		MapController mapController = mMapView.getController();
		mapController.animateTo(myPoint);
		Log.d("BgBiz", "createUserPositionOverlayOnMap :"+myLat+" , "+myLng);

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
	private void createBusinessOverlayOnMapView(){
		Log.d("BgBiz", "CreateOverLay called");
		List<Overlay> mapOverlays = mMapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.charity_icon_default);
		BizMapOverlay itemizedOverlay = new BizMapOverlay(drawable, mMapView, mBizArrayList, getParent());	//getParent() since alert only works on the upper-most context.

		Log.d("BgBiz", "Ready to make GeoPoint");
		GeoPoint point = null;
		OverlayItem overlayItem = null;
		for(Business biz : mBizArrayList){
			if (biz==null) break;
			point = new GeoPoint( convFloatToIntE6(biz.getBizLat()) , convFloatToIntE6(biz.getBizLng()));	//Create GeoPoint
			overlayItem = new OverlayItem(point, biz.getBizName(), biz.getBizAddress());					//Store name and address as descriptions of GeoPoint

			itemizedOverlay.addOverlay(overlayItem);
			mapOverlays.add(itemizedOverlay);
		}
		mIsLoadingBizLocation = false;
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
		Log.d("BgBiz", "Back Pressed from BizMap");
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

		Log.d("BgBiz", "LatSpan, LngSpan, Center: "+mMapView.getLatitudeSpan()+" , "+mMapView.getLongitudeSpan()+", "+mapCenter);

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
	 * 
	 * @author wns349
	 *
	 * Listener called when there is a map change by the user.
	 */
	private class MapViewChangeListener implements BizExtendedMapView.OnChangeListener
	{

		@Override
		public void onChange(MapView view, GeoPoint newCenter, GeoPoint oldCenter, int newZoom, int oldZoom)
		{
			// Check values
			if ((!newCenter.equals(oldCenter)) || (newZoom != oldZoom))
			{
				if(mIsLoadingBizLocation)
					return;
				mIsLoadingBizLocation = true;
				// Map Zoom and Pan Detected
				Log.d("BgBiz", "Map changed from "+oldCenter+" to "+newCenter+" zoom:"+oldZoom+" -> "+newZoom);

				if(newZoom != oldZoom){
					//If zoom level changed, mDistanceRadius must be updated.
					updateDistanceRadius();
				}
				retrieveBizLocation();
			}
		}
	}

	private class LoadBizThread extends Thread{
		Handler mHandler;

		public LoadBizThread(Handler h){
			this.mHandler = h;
		}

		public void run(){
			Log.d("BgBiz", "LoadBizThread called");
			updateBizArrayList();
			createBusinessOverlayOnMapView();

			Message msg = mHandler.obtainMessage();
			Bundle b = new Bundle();
			b.putInt("status", STATUS_LOADED_BIZ_LOC);
			mHandler.sendMessage(msg);
			mIsLoadingBizLocation = false;
			Log.d("BgBiz", "Changed mIsLoadingBizLocation to "+mIsLoadingBizLocation);
		}
	}

	final Handler handlerLoadBizThread = new Handler() {
		public void handleMessage(Message msg) {
			int status = msg.getData().getInt("status");
			switch(status){
			case STATUS_LOADED_BIZ_LOC:
				Log.d("BgBiz", "Status_loaded_biz_loc");
				createBusinessOverlayOnMapView();
				break;
			}
		}
	};

	private class LocationControl extends AsyncTask<Context, Void, Void>
	{
		private final ProgressDialog dialog = new ProgressDialog(BizMapController.this);
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
				Log.d("BgBiz", "Location found!: "+mCurrentLocation.getLatitude()+" | "+mCurrentLocation.getLongitude());
				//useLocation();

				/*
				 * Do when current location is found
				 */
				createMyLocationOverlayOnMapView();
				if(!mIsLoadingBizLocation){
					Log.d("BgBiz", "retrieveBizLocation already called");
					mIsLoadingBizLocation = true;
					retrieveBizLocation();
				}
			}
			else
			{
				Log.d("BgBiz", "Location not found!");
				//Couldn't find location, do something like show an alert dialog
				if(!mIsLoadingBizLocation){
					Log.d("BgBiz", "retrieveBizLocation already called");
					mIsLoadingBizLocation = true;
					retrieveBizLocation();
				}
			}
		}
	}
	@Override
	public void onStop(){
		Log.d("BgBiz", "onStop called from BizMapController");
		stopLocationLoading();
		super.onStop();
	}
	private void stopLocationLoading(){
		mBizMyLocation.stopLocationUpdates();
		mLocationControlTask.cancel(true);
	}
}