package com.bridginggood.Biz;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
	private BizExtendedMapView mMapView;			//MapView
	private ArrayList<Business> mBizArrayList;		//Business list

	private ProgressDialog mProgressDialog;			
	private BizMyLocation mBizMyLocation;
	private LoadMapThread mLoadMapThread;
	private LoadBizThread mLoadBizThread;

	private float mMyLat = 40.714353f;
	private float mMyLng = -74.005973f;
	private float mDistanceRadius = 1.0f;

	private boolean mIsLoadingBizLocation = false; 		//Lock
	private boolean mIsLoadingMyLocation = false;

	private final int STATUS_LOADED_MY_LOC = 0;
	private final int STATUS_LOADED_BIZ_LOC = 1;

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
		mIsLoadingMyLocation = false;
		//Initialize mapview
		initMapView();

		//Get current location
		//retrieveCurrentLocation();
		if(!mIsLoadingBizLocation){
			Log.d("BgBiz", "retrieveBizLocation already called");
			mIsLoadingBizLocation = true;
			retrieveBizLocation();
		}
	}

	private void retrieveCurrentLocation(){
		mIsLoadingMyLocation = true;
		//Start progress dialog
		mProgressDialog = ProgressDialog.show(getParent(), "", "Identifying your location...", true, true);
		mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				if(mLoadMapThread!=null && mLoadMapThread.isAlive()){
					Log.d("BgBiz", "mLoadMapThread killed by the user");
					//mLoadMapThread.interrupt();
				}
			}
		});


		mBizMyLocation = new BizMyLocation(getParent());
		mBizMyLocation.getLocation(getApplicationContext(), locationResult);

		while(mIsLoadingMyLocation);

		createMyLocationOverlayOnMapView();
		mProgressDialog.dismiss();

		//mLoadMapThread= new LoadMapThread(handlerThread);
		//mLoadMapThread.start();
	}

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
		GeoPoint tmpGeoPoint = new GeoPoint(convFloatToIntE6(40.714353f), convFloatToIntE6(-74.005973f));
		MapController mapController = mMapView.getController();
		mapController.setZoom(16);
		mapController.setCenter(tmpGeoPoint);

		Log.d("BgBiz", "Adding listener to the map");
		//Handle map change - zoom and span
		mMapView.setOnChangeListener(new MapViewChangeListener());
	}

	//Callback - once you got the location of the user
	private LocationResult locationResult = new LocationResult(){
		@Override
		public void gotLocation(final Location location){
			Log.d("BgBiz", "LocationResult called with  "+location.getLatitude() + " , "+location.getLongitude());
			//Got the location!, store them as current location
			mMyLat = (float) location.getLatitude();
			mMyLng = (float) location.getLongitude();
			mIsLoadingMyLocation = false;
		}
	};

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
		GeoPoint myPoint = new GeoPoint (convFloatToIntE6(mMyLat), convFloatToIntE6(mMyLng));
		MapController mapController = mMapView.getController();
		mapController.animateTo(myPoint);
		Log.d("BgBiz", "createUserPositionOverlayOnMap :"+mMyLat+" , "+mMyLng);

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

	private class LoadMapThread extends Thread{
		Handler mHandler;

		LoadMapThread(Handler h){
			this.mHandler = h;
		}

		public void run(){
			Log.d("BgBiz", "LoadMapThread called");
			while(mIsLoadingMyLocation);
			Message msg = mHandler.obtainMessage();
			Bundle b = new Bundle();
			b.putInt("status", STATUS_LOADED_MY_LOC);
			mHandler.sendMessage(msg);
		}
	}

	final Handler handlerLoadMapThread = new Handler() {
		public void handleMessage(Message msg) {
			int status = msg.getData().getInt("status");
			switch(status){
			case STATUS_LOADED_MY_LOC:
				Log.d("BgBiz", "Status_loaded_my_loc");
				createMyLocationOverlayOnMapView();
				mProgressDialog.dismiss();
				break;
			}
		}
	};

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


}