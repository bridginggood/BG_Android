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
	private MapView mapView;
	private BizMyLocation myLocation;
	private ArrayList<Business> bizList;
	
	private TextView txtHeaderLoc;
	
	private float myLat = 37.4848f;
	private float myLng = 126.895f;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bizmap);
		
		//Initialize ArrayLists
		bizList = new ArrayList<Business>();

		//Initialize mapview
		mapView = (MapView) findViewById(R.id.bizMapView);
		mapView.setBuiltInZoomControls(true);
		
		//Initialize mapview Heading layout
		txtHeaderLoc = (TextView) findViewById(R.id.txtBizMapHeader);
		txtHeaderLoc.setText("Getting your location...");
		
		//Initialize myLocation to get current loc
		myLocation = new BizMyLocation(getParent());
		myLocation.getLocation(this, locationResult);

		//Get GeoPoints
		this.setBizList(mapView.getZoomLevel());
		
		//Mark on the map
		this.createOverlay();
	}

	//Callback - once you got the location of the user
	public LocationResult locationResult = new LocationResult(){
		@Override
		public void gotLocation(final Location location){
			Log.d("BG", "LocationResult called with  "+location.getLatitude() + " , "+location.getLongitude());
			//Got the location!, store them as current location
			myLat = (float) location.getLatitude();
			myLng = (float) location.getLongitude();
			
			txtHeaderLoc.setText("Your location: ("+myLat+" , "+myLng+" )");
			
			createMyOverlay();
		}
	};
	
	//Set arraylist of GeoPoints to be marked on the map
	public void setBizList(int zoomLevel){
		BizDBController bizDB = new BizDBController(myLat, myLng, zoomLevel);
		bizList = bizDB.getBizListFromXML();
	}
	
	private void createMyOverlay(){
		GeoPoint myPoint = new GeoPoint (convFloatToIntE6(myLat), convFloatToIntE6(myLng));
		MapController mapController = mapView.getController();
		mapController.animateTo(myPoint);
		Log.d("BG", "createMyOverlay called. Should move to :"+myLat+" , "+myLng);
		
		// 마커표시
		MapView.LayoutParams mapMarkerParams = new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, myPoint,
				MapView.LayoutParams.TOP_LEFT);
		ImageView mapMarker = new ImageView(
				getApplicationContext());
		mapMarker.setImageResource(R.drawable.icon);
		mapView.addView(mapMarker, mapMarkerParams);
	}

	//To create overlay on the map
	private void createOverlay(){
		Log.d("BG", "CreateOverLay called");
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.charity_icon_default);
		BizMapOverlay itemizedOverlay = new BizMapOverlay(drawable, mapView, bizList, getParent());	//getParent() since alert only works on the upper-most context.

		Log.d("BG", "Ready to make GeoPoint");
		GeoPoint point = null;
		OverlayItem overlayItem = null;
		for(Business biz : bizList){
			point = new GeoPoint( convFloatToIntE6(biz.getBizLat()) , convFloatToIntE6(biz.getBizLng()));	//Create GeoPoint
			overlayItem = new OverlayItem(point, biz.getBizName(), biz.getBizAddress());					//Store name and address as descriptions of GeoPoint
			
			itemizedOverlay.addOverlay(overlayItem);
			mapOverlays.add(itemizedOverlay);
		}

		MapController mapController = mapView.getController();
		mapController.animateTo(point);
		mapController.setZoom(15);
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