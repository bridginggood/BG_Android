package com.bridginggood.Biz;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

public class BizMyLocation {
	//private Timer timer1;
	private LocationManager locMgr;
	private LocationResult locResult;
	private boolean isGpsEnabled=false;
	private boolean isNetworkEnabled=false;
	private Context context;
	
	public BizMyLocation(Context context){
		this.context = context;
	}

	public boolean getLocation(Context context, LocationResult result)
	{
		//Use LocationResult callback class to pass location value from this class to BizMapController.
		locResult=result;
		if(locMgr==null)
			locMgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		//exceptions will be thrown if provider is not permitted.
		try{isGpsEnabled=locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
		try{isNetworkEnabled=locMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}

		//don't start listeners if no provider is enabled
		if(!isGpsEnabled && !isNetworkEnabled)
			return false;

		if(isGpsEnabled)
			locMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
		if(isNetworkEnabled)
			locMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
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
			Toast.makeText(context, "GPS is disabled", Toast.LENGTH_SHORT);
		}
		public void onProviderEnabled(String provider) {
			Toast.makeText(context, "GPS is enabled", Toast.LENGTH_SHORT);
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
	
	public void cancelGetLocation(){
		locMgr.removeUpdates(locationListenerGps);
		locMgr.removeUpdates(locationListenerNetwork);
	}
	
/*
	class GetLastLocation extends TimerTask {
		@Override
		public void run() {
			locMgr.removeUpdates(locationListenerGps);
			locMgr.removeUpdates(locationListenerNetwork);

			Location net_loc=null, gps_loc=null;
			if(isGpsEnabled)
				gps_loc=locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if(isNetworkEnabled)
				net_loc=locMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

			//if there are both values use the latest one
			if(gps_loc!=null && net_loc!=null){
				if(gps_loc.getTime()>net_loc.getTime())
					locResult.gotLocation(gps_loc);
				else
					locResult.gotLocation(net_loc);
				return;
			}

			if(gps_loc!=null){
				locResult.gotLocation(gps_loc);
				return;
			}
			if(net_loc!=null){
				locResult.gotLocation(net_loc);
				return;
			}
			locResult.gotLocation(null);
		}
	}
*/
	public static abstract class LocationResult{
		public abstract void gotLocation(Location location);
	}
}
