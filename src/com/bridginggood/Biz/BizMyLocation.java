package com.bridginggood.Biz;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

public class BizMyLocation {
	private LocationManager locMgr;
	private LocationResult locResult;
	private boolean mIsGpsEnabled=false;
	private boolean mIsNetworkEnabled=false;
	private Context mContext;
	
	private static final int MIN_TIME_REFRESH_LOCATION = 1000*60*2;	//Min time in milliseconds
	private static final int MIN_DIST_REFRESH_LOCATION = 100;		//Min dist in meter
	
	public BizMyLocation(Context context){
		this.mContext = context;
	}

	public boolean getLocation(Context context, LocationResult result)
	{
		//Use LocationResult callback class to pass location value from this class to BizMapController.
		locResult=result;
		if(locMgr==null)
			locMgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		//exceptions will be thrown if provider is not permitted.
		try{mIsGpsEnabled=locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
		try{mIsNetworkEnabled=locMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}

		//don't start listeners if no provider is enabled
		if(!mIsGpsEnabled && !mIsNetworkEnabled)
			return false;

		if(mIsGpsEnabled)
			locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_REFRESH_LOCATION, MIN_DIST_REFRESH_LOCATION, locationListenerGps);
		if(mIsNetworkEnabled)
			locMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_REFRESH_LOCATION, MIN_DIST_REFRESH_LOCATION, locationListenerNetwork);
		//timer1=new Timer();
		//timer1.schedule(new GetLastLocation(), 20000);
		return true;
	}

	LocationListener locationListenerGps = new LocationListener() {
		public void onLocationChanged(Location location) {
			//timer1.cancel();
			locResult.gotLocation(location);
			locMgr.removeUpdates(this);
			locMgr.removeUpdates(locationListenerNetwork);
		}
		public void onProviderDisabled(String provider) {
			Toast.makeText(mContext, "GPS is disabled", Toast.LENGTH_SHORT);
		}
		public void onProviderEnabled(String provider) {
			Toast.makeText(mContext, "GPS is enabled", Toast.LENGTH_SHORT);
		}
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	};

	LocationListener locationListenerNetwork = new LocationListener() {
		public void onLocationChanged(Location location) {
			//timer1.cancel();
			locResult.gotLocation(location);
			locMgr.removeUpdates(this);
			locMgr.removeUpdates(locationListenerGps);
		}
		public void onProviderDisabled(String provider) {}
		public void onProviderEnabled(String provider) {}
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	};

	public static abstract class LocationResult{
		public abstract void gotLocation(Location location);
	}
}
