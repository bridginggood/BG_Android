package com.bridginggood.Biz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class BizMyLocation {
	//private Timer timer1;
	private LocationManager locMgr;
	private boolean isGpsEnabled=false;
	private boolean isNetworkEnabled=false;
	private Context mContext;
	private Location mCurrentLocation = null;
	private boolean isLocked = false;
	private ProgressDialog mProgressDialog;

	public BizMyLocation(Context context){
		mContext = context;
		isLocked = false;
	}

	public Location getLocation()
	{	

		/*mProgressDialog = ProgressDialog.show(mContext, "", "Identifying your location...", true, true);
		mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				//TODO: Implement cancel action
				Log.d("BgBiz", "mLoadMapThread killed by the user");				
			}
		});*/

		//Use LocationResult callback class to pass location value from this class to BizMapController.
		if(locMgr==null)
			locMgr = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

		//exceptions will be thrown if provider is not permitted.
		try{isGpsEnabled=locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
		try{isNetworkEnabled=locMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}

		//don't start listeners if no provider is enabled
		if(!isGpsEnabled && !isNetworkEnabled)
			return null;

		if(isGpsEnabled){
			isLocked = true;
			Log.d("BgBiz", "reuqestLocationUpdates by GPS");
			locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
			//while(isLocked);
		}
		if(isNetworkEnabled){
			isLocked = true;
			Log.d("BgBiz", "reuqestLocationUpdates by Network");
			locMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
			//while(isLocked);
		}

		Log.d("BgBiz", "Turning on lock from create");
		isLocked = true;
		while(isLocked);
		return mCurrentLocation;
	}

	LocationListener locationListenerGps = new LocationListener() {
		public void onLocationChanged(Location location) {
			Log.d("BgBiz", "locationListnerGPS Called");
			mCurrentLocation = location;
			locMgr.removeUpdates(this);
			locMgr.removeUpdates(locationListenerNetwork);

			mHandler.sendEmptyMessage(0);
		}
		public void onProviderDisabled(String provider) {
			Toast.makeText(mContext, "GPS is disabled", Toast.LENGTH_SHORT);
			isLocked = false;
		}
		public void onProviderEnabled(String provider) {
			Toast.makeText(mContext, "GPS is enabled", Toast.LENGTH_SHORT);
			isLocked = false;
		}
		public void onStatusChanged(String provider, int status, Bundle extras) {isLocked = false;}
	};

	LocationListener locationListenerNetwork = new LocationListener() {
		public void onLocationChanged(Location location) {
			Log.d("BgBiz", "locationListnerNetwork Called");
			mCurrentLocation = location;
			locMgr.removeUpdates(this);
			locMgr.removeUpdates(locationListenerGps);

			mHandler.sendEmptyMessage(0);
		}
		public void onProviderDisabled(String provider) {isLocked = false;}
		public void onProviderEnabled(String provider) {isLocked = false;}
		public void onStatusChanged(String provider, int status, Bundle extras) {isLocked = false;}
	};

	public void cancelGetLocation(){
		//TODO: Is this correct implementation?
		locMgr.removeUpdates(locationListenerGps);
		locMgr.removeUpdates(locationListenerNetwork);
	}

	public void setCurrentLocation(Location location){
		mCurrentLocation = location;
	}

	public Location getCurrentLocation(){
		return mCurrentLocation;
	}

	public boolean isCurrentLocationAvailable(){
		return (mCurrentLocation!=null);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			Log.d("BgBiz", "mHandler called");
			isLocked = false;
		}
	};
}
