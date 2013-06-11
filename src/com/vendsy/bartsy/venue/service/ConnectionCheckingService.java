package com.vendsy.bartsy.venue.service;

import com.vendsy.bartsy.venue.utils.WebServices;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class ConnectionCheckingService extends Service {

	private static final String TAG = "ConnectionCheckingService";

	private long wait = 12000; // 2 mins
	// check for thread is running or not
	private boolean isRunning = false;
	private Thread thr;
	private Handler handler = new Handler();

	@Override
	public void onCreate() {
		super.onCreate();

		Log.i(TAG, "ConnectionCheckingService Service created...");
		isRunning = true;
		// Intiate NetworkThread
		thr = new Thread(new NetworkThread());
		thr.start();
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
				thr = new Thread(new NetworkThread());
				// start thread
				thr.start();
			}
		}

		public void run() {
			while (isRunning) {
				try {
					// For get the network status
					boolean network = WebServices
							.isNetworkAvailable(ConnectionCheckingService.this);
					if (network) {
						// This handler to show the UI-related events
						handler.post(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(ConnectionCheckingService.this,
										"Network available...",
										Toast.LENGTH_LONG).show();

							}
						});

					} else {
						Log.w("connection checking service",
								" ******************************** Network is Not There ***********************************");
						// This handler to show the UI-related events
						handler.post(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(ConnectionCheckingService.this,
										"Network not available...",
										Toast.LENGTH_LONG).show();

							}
						});
					}

				} catch (Exception e) {
					Log.w("connection checking service",
							" ******************************** Exception Came ***********************************"
									+ e.getMessage());
					e.printStackTrace();
				}
				try {
					// Thread in sleep
					Thread.sleep(wait);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}

			}
		}

	}

}
