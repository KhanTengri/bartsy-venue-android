package com.vendsy.bartsy.venue.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.vendsy.bartsy.venue.model.Order;
import com.vendsy.bartsy.venue.BartsyApplication;
import com.vendsy.bartsy.venue.utils.Constants;
import com.vendsy.bartsy.venue.utils.WebServices;

public class ConnectionCheckingService extends Service {

	private static final String TAG = "ConnectionCheckingService";
	
	// check for thread is running or not
	private boolean isRunning = false;
	private Thread thread;
	private Handler handler = new Handler();

	private BartsyApplication mApp;

	@Override
	public void onCreate() {
		super.onCreate();
		
		mApp = (BartsyApplication)getApplication();

		Log.i(TAG, "ConnectionCheckingService Service created...");
		isRunning = true;
		// Intiate NetworkThread
		thread = new Thread(new NetworkThread());
		thread.start();
	}

	/**
	 * Our onDestroy() is called by the Android appliation framework when it
	 * decides that our Service is no longer needed. Here we just kill the
	 * background thread
	 */

	@Override
	public void onDestroy() {
		super.onDestroy();
		isRunning = false;
		Toast.makeText(this, "Service destroyed...", Toast.LENGTH_LONG).show();
	}

	/**
	 * We are not use the binder service so we return null.
	 * 
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class NetworkThread implements Runnable {
		public void finalize() {

			if (isRunning) {
				// create the NetworkThread object in thread
				thread = new Thread(new NetworkThread());
				// start thread
				thread.start();
			}
		}

		public void run() {
			while (isRunning) {
				try {
					
					// The main synchronization function that runs periodically
					mApp.update();
					
					// The less interesting hearteat syscall
					if (WebServices .isNetworkAvailable(ConnectionCheckingService.this)) {	
						mApp.performHeartbeat();
					}
					
					// Handle WIFI failures
					
						/*		
						Log.w(TAG, "Network unavailable");
						
						// Attempt recovery by turning wifi off / on
						
						WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);

						if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
							 Log.v(TAG, "WiFi enabled - disabling it");
							 wifiManager.setWifiEnabled(false);
						} else if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
							 Log.v(TAG, "WiFi disabled - enabling it");
							 wifiManager.setWifiEnabled(true);
						} else {
							 Log.v(TAG, "WiFi status: " + wifiManager.getWifiState());							
						}
						
						mApp.makeText("Network not available. Trying to fix this issue... If you need to work with the WIFI please manually kill " +
								"the Bartsy Venue app in the tablet's preferences.", Toast.LENGTH_LONG);
					 */
					

				} catch (Exception e) {
					Log.w(TAG, " ******************************** Exception ***********************************");
					Log.e(TAG, e.getMessage());
					e.printStackTrace();
				}
				try {
					// Thread in sleep
					Thread.sleep(Constants.monitorFrequency);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
			}
		}
	}

}
