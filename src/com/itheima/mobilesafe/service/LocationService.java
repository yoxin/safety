package com.itheima.mobilesafe.service;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class LocationService extends Service {
	private LocationManager locationManager;
	private String provider;
	private SharedPreferences sp;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		sp = getSharedPreferences("config", MODE_PRIVATE);
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		List<String> providers = locationManager.getProviders(true);
		if (providers.contains(LocationManager.GPS_PROVIDER)) {
			provider = LocationManager.GPS_PROVIDER;
		} else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
			provider = LocationManager.NETWORK_PROVIDER;
		} else {
			Editor editor = sp.edit();
			editor.putString("location", "没有打开定位服务");
			editor.commit();
			return;
		}
		Location location = locationManager.getLastKnownLocation(provider);
		if (location != null) {
			saveLocation(location);
		}
		locationManager.requestLocationUpdates(provider, 0, 0,
				locationListener);
	}

	private void saveLocation(Location location) {
		String longitude = "Longitude:" + location.getLongitude() + "\n";
		String latitude = "Latitude:" + location.getLatitude();
		Editor editor = sp.edit();

		editor.putString("location", longitude + latitude);
		editor.commit();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (locationListener != null) {
			locationManager.removeUpdates(locationListener);
		}
		locationListener = null;
	}

	LocationListener locationListener = new LocationListener() {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLocationChanged(Location location) {
			saveLocation(location);
		}
	};
}
