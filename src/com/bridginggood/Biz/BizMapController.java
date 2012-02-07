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
	private BizExtendedMapView mMapView;
	private BizMyLocation mMyLocation;
	private ArrayList<Business> mBizArrayList;

	private float mMyLat = 37.4848f;
	private float mMyLng = 126.895f;
	private float mDistanceRadius = 1.0f;
	
	private Location locTR, locBL;
	
	private boolean mIsLoadingOverlay = false; 
	
	private static final int TIME_TO_WAIT_IN_MS = 300;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bizmap);
		
		//Initialize ArrayLists
		mBizArrayList = new ArrayList<Business>();

		//Initialize mapview
		initMapView();
		
		//Initialize location TopRight, BottomLeft for onUserInteraction() - to handle mapview change
		locTR = new Location("LocationTopRight");
		locBL = new Location("LocationBottomLeft");
		
		//Initialize mapview Heading layout
		TextView txtHeaderLoc = (TextView) findViewById(R.id.txtBizMapHeader);
		txtHeaderLoc.setText("Getting your location...");
		
		//Initialize myLocation to get current loc
		mMyLocation = new BizMyLocation(getParent());
		mMyLocation.getLocation(this, locationResult);
		
		//Initialize trigger
		mIsLoadingOverlay = false;
		
		//Initial map point. Start from NYC!
		GeoPoint tmpGeoPoint = new GeoPoint(convFloatToIntE6(40.714353f), convFloatToIntE6(-74.005973f));
		MapController mapController = mMapView.getController();
		mapController.setZoom(16);
		mapController.setCenter(tmpGeoPoint);
	}
	
	private void initMapView(){
		mMapView = (BizExtendedMapView) findViewById(R.id.bizMapView);
		mMapView.setBuiltInZoomControls(true);
		mMapView.setClickable(true);
		
		Log.d("BG", "Adding listener to the map");
		//Handle map change - zoom and span
		mMapView.setOnTouchEventListener(new BizExtendedMapView.OnTouchEventListener() {
	        @Override
	        public void onTouchEvent(MapView view, int newZoom, int oldZoom, GeoPoint newMapCenter, GeoPoint oldMapCenter) {
	            Log.d("BG", "Zoom changed from " + oldZoom + " to " + newZoom + ". GeoPoint:"+newMapCenter+" | "+oldMapCenter);
	            //Load only when map is not loading any overlays
	            if(mIsLoadingOverlay == false)
	            	mMapView.postDelayed(calcMapSpan, TIME_TO_WAIT_IN_MS);
	        }
	    });
	}

	//Callback - once you got the location of the user
	private LocationResult locationResult = new LocationResult(){
		@Override
		public void gotLocation(final Location location){
			Log.d("BG", "LocationResult called with  "+location.getLatitude() + " , "+location.getLongitude());
			//Got the location!, store them as current location
			mMyLat = (float) location.getLatitude();
			mMyLng = (float) location.getLongitude();
			
			TextView txtHeaderLoc = (TextView) findViewById(R.id.txtBizMapHeader);
			txtHeaderLoc.setText("Your location: ("+mMyLat+" , "+mMyLng+" )");

			createMyOverlay();
		}
	};
	
    // Wait for mapview to become ready.
    private Runnable calcMapSpan = new Runnable() {
        public void run() {
        	mIsLoadingOverlay = true;
            
        	// If either is true we must wait.
        	if(mMapView.getLatitudeSpan() == 0 || mMapView.getLongitudeSpan() == 360000000)
            	mMapView.postDelayed(this, TIME_TO_WAIT_IN_MS);
            
            float latDiff = mMapView.getLatitudeSpan() / 2;
            float lngDiff = mMapView.getLongitudeSpan() / 2;
            GeoPoint mapCenter = mMapView.getMapCenter();
            
            Log.d("BG", "LatSpan, LngSpan, Center: "+mMapView.getLatitudeSpan()+" , "+mMapView.getLongitudeSpan()+", "+mapCenter);
            
            //Find distance using span
            float p1Lat = (float) ((mapCenter.getLatitudeE6()+latDiff)/1E6);
            float p1Lng = (float) ((mapCenter.getLongitudeE6()+lngDiff)/1E6);
            float p2Lat = (float) ((mapCenter.getLatitudeE6()-latDiff)/1E6);
            float p2Lng = (float) ((mapCenter.getLongitudeE6()-lngDiff)/1E6);
            
            setLocationTR(p1Lat, p1Lng);
            setLocationBL(p2Lat, p2Lng);
            
            mDistanceRadius = getLocationDistanceDifference();
            Log.d("BG", "Dist: "+ mDistanceRadius);
            
            //Get GeoPoints from the Server
    		updateBizArrayList((float)(mapCenter.getLatitudeE6()/1E6), (float)(mapCenter.getLongitudeE6()/1E6));
    		
    		//Mark on the map
    		createOverlay();
    		
    		mIsLoadingOverlay = false;
        }
    };
    
    //Store it in float
    private void setLocationTR(float lat, float lng){
    	locTR.setLatitude(lat);
    	locTR.setLongitude(lng);
    }
    
    private void setLocationBL(float lat, float lng){
    	locBL.setLatitude(lat);
    	locBL.setLongitude(lng);
    }
 
    private float getLocationDistanceDifference(){
        float dist = locTR.distanceTo(locBL);
		//Convert distanceAway from meter to miles
        dist = (float) ((float)(dist/1000)/1.6);
		return dist;
	}

	//Set arraylist of GeoPoints to be marked on the map
	private void updateBizArrayList(float lat, float lng){
		BizReadFromDB bizDB = new BizReadFromDB(lat, lng, mDistanceRadius);
		mBizArrayList = bizDB.getBizListFromXML();
	}
	
	private void createMyOverlay(){
		GeoPoint myPoint = new GeoPoint (convFloatToIntE6(mMyLat), convFloatToIntE6(mMyLng));
		MapController mapController = mMapView.getController();
		mapController.animateTo(myPoint);
		Log.d("BG", "createMyOverlay called. Should move to :"+mMyLat+" , "+mMyLng);
		
		// Marker
		MapView.LayoutParams mapMarkerParams = new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, myPoint,
				MapView.LayoutParams.TOP_LEFT);
		ImageView mapMarker = new ImageView(
				getApplicationContext());
		mapMarker.setImageResource(R.drawable.icon);
		mMapView.addView(mapMarker, mapMarkerParams);
		
		
		//Get Spans
		mMapView.postDelayed(calcMapSpan, TIME_TO_WAIT_IN_MS);
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