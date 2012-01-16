package com.bridginggood.Biz;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.bridginggood.R;
import com.bridginggood.Biz.BizMyLocation.LocationResult;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class BizMapController extends MapActivity{ 
	private MapView mMapView;
	private BizMyLocation mMyLocation;
	private ArrayList<Business> mBizArrayList;
	
	private TextView mTxtHeaderLoc;
	
	private float mMyLat = 37.4848f;
	private float mMyLng = 126.895f;
	private float mDistanceRadius = 1.0f;
	
	private GeoPoint mMapCenter;
	
	private static final int TIME_TO_WAIT_IN_MS = 500;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bizmap);
		
		//Initialize ArrayLists
		mBizArrayList = new ArrayList<Business>();

		//Initialize mapview
		mMapView = (MapView) findViewById(R.id.bizMapView);
		mMapView.setBuiltInZoomControls(true);
		
		//Initialize mapview Heading layout
		mTxtHeaderLoc = (TextView) findViewById(R.id.txtBizMapHeader);
		mTxtHeaderLoc.setText("Getting your location...");
		
		//Initialize myLocation to get current loc
		mMyLocation = new BizMyLocation(getParent());
		mMyLocation.getLocation(this, locationResult);
	}

	//Callback - once you got the location of the user
	public LocationResult locationResult = new LocationResult(){
		@Override
		public void gotLocation(final Location location){
			Log.d("BG", "LocationResult called with  "+location.getLatitude() + " , "+location.getLongitude());
			//Got the location!, store them as current location
			mMyLat = (float) location.getLatitude();
			mMyLng = (float) location.getLongitude();
			
			mTxtHeaderLoc.setText("Your location: ("+mMyLat+" , "+mMyLng+" )");
			
			//Get Spans
			mMapView.postDelayed(waitForMapTimeTask, TIME_TO_WAIT_IN_MS);
			
			createMyOverlay();
		}
	};
	
    // Wait for mapview to become ready.
    private Runnable waitForMapTimeTask = new Runnable() {
        public void run() {
            // If either is true we must wait.
            if(mMapView.getLatitudeSpan() == 0 || mMapView.getLongitudeSpan() == 360000000)
            	mMapView.postDelayed(this, TIME_TO_WAIT_IN_MS);
            
            float latDiff = mMapView.getLatitudeSpan() / 2;
            float lngDiff = mMapView.getLongitudeSpan() / 2;
            mMapCenter = mMapView.getMapCenter();
            
            Log.d("BG", "LatSpan, LngSpan, Center: "+mMapView.getLatitudeSpan()+" , "+mMapView.getLongitudeSpan()+", "+mMapCenter);
            
            //Find distance using span
            float p1Lat = (float) ((mMapCenter.getLatitudeE6()+latDiff)/1E6);
            float p1Lng = (float) ((mMapCenter.getLongitudeE6()+lngDiff)/1E6);
            float p2Lat = (float) ((mMapCenter.getLatitudeE6()-latDiff)/1E6);
            float p2Lng = (float) ((mMapCenter.getLongitudeE6()-lngDiff)/1E6);
            
            mDistanceRadius = getDistanceAway(p1Lat, p1Lng, p2Lat, p2Lng);
            Log.d("BG", "Dist: "+ mDistanceRadius);
            
            //Get GeoPoints from the Server
    		updateBizArrayList();
    		
    		//Mark on the map
    		createOverlay();
        }
    };
    
    public float getDistanceAway(float p1Lat, float p1Lng, float p2Lat, float p2Lng){
		//Android function. Returns in Meter 
    	Location loc1 = new Location("Loc1");
        loc1.setLatitude(p1Lat);
        loc1.setLongitude(p1Lng);
        
        Location loc2 = new Location("Loc2");
        loc2.setLatitude(p2Lat);
        loc2.setLongitude(p2Lng);
        
        float dist = loc1.distanceTo(loc2);
        
		//Convert distanceAway from meter to miles
        dist = (float) ((float)(dist/1000)/1.6);
		
		return dist;
	}

	//Set arraylist of GeoPoints to be marked on the map
	public void updateBizArrayList(){
		BizDBController bizDB = new BizDBController(mMyLat, mMyLng, mDistanceRadius);
		mBizArrayList = bizDB.getBizListFromXML();
	}
	
	private void createMyOverlay(){
		GeoPoint myPoint = new GeoPoint (convFloatToIntE6(mMyLat), convFloatToIntE6(mMyLng));
		MapController mapController = mMapView.getController();
		mapController.animateTo(myPoint);
		Log.d("BG", "createMyOverlay called. Should move to :"+mMyLat+" , "+mMyLng);
		
		// 마커표시
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
	private void createOverlay(){
		Log.d("BG", "CreateOverLay called");
		List<Overlay> mapOverlays = mMapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.charity_icon_default);
		BizMapOverlay itemizedOverlay = new BizMapOverlay(drawable, mMapView, mBizArrayList, getParent());	//getParent() since alert only works on the upper-most context.

		Log.d("BG", "Ready to make GeoPoint");
		GeoPoint point = null;
		OverlayItem overlayItem = null;
		for(Business biz : mBizArrayList){
			point = new GeoPoint( convFloatToIntE6(biz.getBizLat()) , convFloatToIntE6(biz.getBizLng()));	//Create GeoPoint
			overlayItem = new OverlayItem(point, biz.getBizName(), biz.getBizAddress());					//Store name and address as descriptions of GeoPoint
			
			itemizedOverlay.addOverlay(overlayItem);
			mapOverlays.add(itemizedOverlay);
		}

		MapController mapController = mMapView.getController();
		mapController.animateTo(point);
		mapController.setZoom(15);
	}
	

	//When there is user interaction, check if the user has changed mapview region or not.
	@Override
	public void onUserInteraction(){
		
		//Compare with previous mMapCenter position.
		GeoPoint newGeoPoint = mMapView.getMapCenter();
		if(mMapCenter != null && newGeoPoint != null 
				&& newGeoPoint.getLatitudeE6() != mMapCenter.getLatitudeE6() 
				&& newGeoPoint.getLongitudeE6() != mMapCenter.getLongitudeE6())
		{
			//If mapView region has changed, 
			mMapCenter = newGeoPoint;
			Log.d("BG", "onUserInteraction called!");
			
			//Get Spans
			mMapView.postDelayed(waitForMapTimeTask, TIME_TO_WAIT_IN_MS);
		}
	}

	//Convert float lat, lng to int lat lng for geocode
	private int convFloatToIntE6(float num){
		return (int)(num*1E6);
	}

	@Override 
	protected boolean isRouteDisplayed() {
		return false;
	}

	// To handle back button
	public void onBackPressed() { //on Back
		Log.d("BG", "Back Pressed from BizMap");
		BizActivityGroup parent = ((BizActivityGroup)getParent());
		parent.back();
	}
}