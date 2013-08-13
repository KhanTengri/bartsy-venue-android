/*
 * Copyright 2011, Qualcomm Innovation Center, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.vendsy.bartsy.venue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gcm.GCMRegistrar;
import com.vendsy.bartsy.venue.db.DatabaseManager;
import com.vendsy.bartsy.venue.model.AppObservable;
import com.vendsy.bartsy.venue.model.Category;
import com.vendsy.bartsy.venue.model.Cocktail;
import com.vendsy.bartsy.venue.model.Order;
import com.vendsy.bartsy.venue.model.Profile;
import com.vendsy.bartsy.venue.service.ConnectionCheckingService;
import com.vendsy.bartsy.venue.service.ConnectivityService;
import com.vendsy.bartsy.venue.utils.Constants;
import com.vendsy.bartsy.venue.utils.Utilities;
import com.vendsy.bartsy.venue.utils.WebServices;
import com.vendsy.bartsy.venue.view.AppObserver;
 
/**
 * The ChatAppliation class serves as the Model (in the sense of the common user
 * interface design pattern known as Model-View-Controller) for the chat
 * application.
 * 
 * The ChatApplication inherits from the relatively little-known Android
 * application framework class Application. From the Android developers
 * reference on class Application:
 * 
 * Base class for those who need to maintain global application state. You can
 * provide your own implementation by specifying its name in your
 * AndroidManifest.xml's <application> tag, which will cause that class to be
 * instantiated for you when the process for your application/package is
 * created.
 * 
 * The important property of class Application is that its lifetime coincides
 * with the lifetime of the application, not its activities. Since we have
 * persistent state in our connections to the outside world via our AllJoyn
 * objects, and that state cannot be serialized, saved and restored; we need a
 * persistent object to ensure that state is held if transient objects like
 * Activities are destroyed and recreated by the Android application framework
 * during its normal operation.
 * 
 * This object holds the global state for our chat application, and starts the
 * Android Service that handles the background processing relating to our
 * AllJoyn connections.
 * 
 * Additionally, this class provides the Model for an MVC framework. It provides
 * a relatively abstract idea of what it is the application is doing. For
 * example, we provide methods oriented to conceptual actions (like our user has
 * typed a message) instead of methods oriented to the implementation (like,
 * create an AllJoyn bus object and register it). This allows the user interface
 * to be relatively independent of the channel implementation.
 * 
 * Android Activities can come and go in sometimes surprising ways during the
 * operation of an application. For example, when a phone is rotated from
 * portrait to landscape orientation, the displayed Activities are deleted and
 * recreated in the new orientation. This class holds the persistent state that
 * is required to correctly display Activities when they are recreated.
 */
public class BartsyApplication extends Application implements AppObservable {
	private static final String TAG = "BartsyApplication";
	public static String PACKAGE_NAME;

	/**
	 * When created, the application fires an intent to create the AllJoyn
	 * service. This acts as sort of a combined view/controller in the overall
	 * architecture.
	 */
	@Override
	public void onCreate() {
		Log.v(TAG, "onCreate()");
		PACKAGE_NAME = getApplicationContext().getPackageName();
		
		// Start background ConnectionCheckingService
		startService(new Intent(this,ConnectionCheckingService.class));
		
		// Start the background connectivity service if running on Alljoyn
		if (Constants.USE_ALLJOYN) {
			Intent intent = new Intent(this, ConnectivityService.class);
			mRunningService = startService(intent);
			if (mRunningService == null) {
				Log.v(TAG, "onCreate(): failed to startService()");
			}
		}

		// load venue profile if it exists. this is an application-wide
		// variable.
		loadVenueProfile();

		// GCM registration
		// --------------------------------------------------
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			GCMRegistrar.register(this, WebServices.SENDER_ID);
		} else {
			Log.v(TAG, "Already registered");
		}
		System.out.println("the registration id is:::::" + regId);

		// --------------------------------------------
		// DataBase initialization - First activity should call this method
		DatabaseManager.getNewInstance(this);
		
		List<Category> categories = DatabaseManager.getInstance().getCategories(Category.SPIRITS_TYPE);
		if(categories==null || categories.size()==0){
			loadCSVfilesAndSaveInDB();
		}
	}
	
	
	/**
	 * Convenience functions to generate notifications and Toasts 
	 */

	public Handler mHandler = new Handler();

	public void makeText(final String toast, final int length) {
		mHandler.post(new Runnable() {
			public void run() {
				Log.v(TAG, toast);
				Toast.makeText(BartsyApplication.this, toast, length).show();
			}
		});
	}
	
	private void generateNotification(final String title, final String body, final int count) {
		mHandler.post(new Runnable() {
			public void run() {
				
				Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
				
				NotificationCompat.Builder mBuilder =
				            new NotificationCompat.Builder(getApplicationContext())
				            .setLargeIcon(largeIcon)
				            .setContentTitle(title)
				            .setContentText(body);
				    // Creates an explicit intent for an Activity in your app
				    Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);

				    // The stack builder object will contain an artificial back stack for the
				    // started Activity.
				    // This ensures that navigating backward from the Activity leads out of
				    // your application to the Home screen.
				    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
				    // Adds the back stack for the Intent (but not the Intent itself)
				    stackBuilder.addParentStack(MainActivity.class);
				    // Adds the Intent that starts the Activity to the top of the stack
				    stackBuilder.addNextIntent(resultIntent);
				    PendingIntent resultPendingIntent =
				            stackBuilder.getPendingIntent(
				                0,
				                PendingIntent.FLAG_UPDATE_CURRENT
				            );
				    mBuilder.setContentIntent(resultPendingIntent);
				    mBuilder.setNumber(count);
				    NotificationManager mNotificationManager =
				        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				    
				    // Play default notification sound
					mBuilder.setDefaults(Notification.DEFAULT_SOUND);
				    // mId allows you to update the notification later on.
				    mNotificationManager.notify(0, mBuilder.build());
				
			}
		});
	}
	
	// Ingredients
	public boolean isIngredientsSaved;
	
	/**
	 *  To load csv file in background
	 */
	private void loadCSVfilesAndSaveInDB() {
		new Thread(){
			public void run() {
				// To read spirits and mixers data from CSV file and save in the DB 
				try {
					Utilities.saveIngredientsFromCSVFile(BartsyApplication.this, getAssets().open(Constants.INGREDIENTS_CSV_FILE));
				} catch (Exception e) {
					Log.e(TAG, "Utilities.saveIngredientsFromCSVFile ::"+e.getMessage());
				}
				
				// To read cocktails data from CSV file and save in the DB
				try {
					Utilities.saveCocktailsFromCSVFile(BartsyApplication.this, getAssets().open(Constants.COCKTAILS_CSV_FILE));
				} catch (Exception e) {
					Log.e(TAG, "Utilities.saveCocktailsFromCSVFile ::"+e.getMessage());
				}
				isIngredientsSaved = true;
				
				notifyObservers(INVENTORY_UPDATED);
				
				if(venueProfileID!=null){
					// Upload the data to server
					uploadDataToServerInBackground();
				}
				
			}
		}.start();
		
		
	}

	/**
	 * Upload Ingredients and cocktails to the server in background
	 */
	public synchronized void uploadDataToServerInBackground() {
		new Thread(){
			public void run() {
				
				uploadIngredientsDataToServer();
			 	
			 	uploadCocktailsDataToServer();
			}
		}.start();
	}
	
	public void uploadIngredientsDataToServer(){
		// Get spirits categories from the database and upload to server
		List<Category> categories = DatabaseManager.getInstance().getCategories(Category.SPIRITS_TYPE);
	    for(Category category:categories){
	    	WebServices.saveIngredients(category, DatabaseManager.getInstance().getIngredients(category), venueProfileID, BartsyApplication.this);
	    }
	    
	    // Get mixer categories from the database and upload to server
	 	List<Category> mixercategories = DatabaseManager.getInstance().getCategories(Category.MIXER_TYPE);
	 	for(Category category:mixercategories){
	 	    WebServices.saveIngredients(category, DatabaseManager.getInstance().getIngredients(category), venueProfileID, BartsyApplication.this);
	 	}
	}
	
	/**
	 *  Get cocktails from the db and upload to server
	 */
	public void uploadCocktailsDataToServer() {
		
	 	List<Cocktail> cocktails = DatabaseManager.getInstance().getCocktails();
	 	WebServices.saveCocktails(cocktails, venueProfileID, BartsyApplication.this);
	}

	/**
	 * TODO - Synchronize orders
	 * 
	 * This is the main synchronization function with the server. It's called if a discrepancy is found in our state versus the server state.
	 * It performs all necessary synchronizations between our state and the server state.
	 * @param message
	 * @param background  - run in the background or not
	 */
	
	
	synchronized public void performHeartbeat() {
		
		Log.v(TAG, "performHeartbeat()");
		
		if (venueProfileID == null) {
			return;
		}
		
		try {
			// Create json object
			JSONObject postData = new JSONObject();
			postData.put("venueId", venueProfileID);
			
			// Heart beat Webservice calling - this is useless for now in checking state so use the more complete syscall 
			WebServices.postRequest(WebServices.URL_HEART_BEAT_VENUE,	postData, this);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Accessors for the main update function. These decide if we should access in the current
	 * thread of spin up a new thread. They also can return a cloned version of the orders list
	 * instead of performing an update
	 */
	
	synchronized public ArrayList<Order> cloneOrders() {
		return accessOrders(ACCESS_ORDERS_VIEW);
	}
	
	/* 
	 * Simple scheduler for the update function 
	 */
	
	Runnable runUpdate = new Runnable() {
		@Override
		public void run() {
			update();
		}
	};

	synchronized public void update(long delay) {

		// Debug log
		Log.e(TAG, "Scheduling update in " + delay + " ms");
		
		// First reset any scheduled updates
        mHandler.removeCallbacks(runUpdate);
        
        // Schedule an update after the specified delay
		mHandler.postDelayed(runUpdate, delay);
	}
	
	synchronized private void update()  {
	
		if (Looper.myLooper() == Looper.getMainLooper()) {
			// We're in the main thread - execute the update in the background with a new asynchronous task
			Log.w(TAG, "Running updateOrders() in an async task");
//			mHandler.post(new Runnable() {
				
//				@Override
//				public void run() {
					new Thread () {
						@Override 
						public void run() {
							accessOrders(BartsyApplication.ACCESS_ORDERS_UPDATE);				
						};
					}.start();
//				};
//			});

//			new UpdateAsync().execute();
		} else {
			// We're not in the main thread - don't spin up a thread
			Log.w(TAG, "Running updateOrders()");
			accessOrders(ACCESS_ORDERS_UPDATE);
		}
		
		return;
	}
	
	private class UpdateAsync extends AsyncTask<Void, Void, Void>{
		@Override
		protected void onPreExecute(){
		}
		
		@Override
		protected Void doInBackground(Void... Voids) {
			accessOrders(ACCESS_ORDERS_UPDATE);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void params){
		}
	}
	
	/**
	 * 
	 * This is the main function of the bunch. It performs most of the work using helper functions. It looks at the server
	 * state and updates the current state by adding, removing and updated orders.
	 * 
	 */
	public static final int ACCESS_ORDERS_UPDATE	= 0;
	public static final int ACCESS_ORDERS_VIEW		= 1;
	
	@SuppressWarnings("unchecked")
	synchronized private ArrayList<Order> accessOrders(int options) {
		
		if (options == ACCESS_ORDERS_VIEW) {
			return (ArrayList<Order>) mOrders.clone();
		}

		
		// Print orders before update
		String ordersString = "\n";
		for (Order order : mOrders) {
			ordersString += order + "\n";
		}
		Log.w(TAG, ">>> Open orders before update:\n" + ordersString);

		
		boolean network = false;
		try {
			network = WebServices.isNetworkAvailable(BartsyApplication.this);
		} catch (Exception e) {
			e.printStackTrace();
		}	

		
		if (network) {	
			
			JSONObject json = WebServices.syncWithServer(venueProfileID, BartsyApplication.this);
			if(json == null)
				return null;
	
			// Synchronize people 
			updatePeople(json);
			
			// Get remote orders list
			ArrayList<Order> remoteOrders = extractOrders(json);
	
			// Find new orders, existing orders and missing orders.
			ArrayList<Order> addedOrders = processAddedOrders(mOrders, remoteOrders);
			processRemovedOrders(mOrders, remoteOrders);
			processExistingOrders(mOrders, remoteOrders);
			
			// Generate notifications
			if (addedOrders.size() > 0) {
				String message = "";
				int count = 0;
				if (addedOrders.size() > 0) {
					message += "Added orders:\n";
					for (Order order : addedOrders) {
						message += "New order for " + order.getRecipientName(mPeople) + "\n";
						count++;
					}
					message += "\n";
				}	
	
				// Print and generate modifications
				Log.w(TAG, message);
				generateNotification("New orders", message, count);
			}		
	
			// Print orders after update
			ordersString = "\n";
			for (Order order : mOrders) {
				ordersString += order + "\n";
			}
			Log.w(TAG, ">>> Open orders after update:\n" + ordersString);
			
			printOrders(addedOrders);
			
			
		}
		
		// Update timers and notify observers of status changes
		updateOrderTimers();
		notifyObservers(ORDERS_UPDATED);
		
		return null;
	}
	
	
	public void printOrders(ArrayList<Order> addedOrders) {
		
		String ip = Utilities.loadPref(this, R.string.config_printer_ip, null);
		
		if (ip == null) {
			Log.e(TAG, "Printer IP address not configured");
			return;
		}
		
		try 
	    {
		    Socket sock = new Socket(ip, 9100);
		    PrintWriter oStream = new PrintWriter(sock.getOutputStream());
			for (Order order : addedOrders) {
				order.println(oStream);
			}
	        oStream.print("\n\n\n");
	        oStream.close();
	        sock.close(); 
	    }
	    catch (UnknownHostException e) 
	    {
	        e.printStackTrace();
	        Log.e(TAG, "Unknown host");
	    } 
	    catch (IOException e) 
	    { 
	        e.printStackTrace();
	        Log.e(TAG, "I/O error");
	    } 
	}
	
	
	ArrayList<Order> extractOrders(JSONObject json) {
		
		ArrayList<Order> orders = new ArrayList<Order>();
		
		try {
			// To parse orders from JSON object
			if (json.has("orders")) {
				JSONArray ordersJson = json.getJSONArray("orders");
				
				for(int j=0; j<ordersJson.length();j++){
					
					JSONObject orderJSON = ordersJson.getJSONObject(j);
	
					// If the server is incorrectly sending the order timeout as a venue-wide variable, insert it in the order JSON
					if (!orderJSON.has("orderTimeout") && json.has("orderTimeout"))
						orderJSON.put("orderTimeout", json.getInt("orderTimeout"));
					
					Order order = new Order(orderJSON);
					orders.add(order);
				}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return orders;
	}
	
	Order findMatchingOrder(ArrayList<Order> orders, Order order) {
		for (Order found : orders) {
			if (found.orderId.equals(order.orderId))
				return found;
		}
		return null;
	}
	
	
	/**
	 * These are the processing helper functions of the main update function. They process orders that were in the server
	 * but not in the local state (new orders), orders that are in the local state but were not in the server state (removed 
	 * orders) and orders that are both in the server state and the local state (updated orders)
	 * 
	 */
	
	ArrayList<Order> processAddedOrders(ArrayList<Order> localOrders, ArrayList<Order> remoteOrders) {

		Log.w(TAG, "processAddedOrders()");

		// Find the orders to remove and store them in a separate list to avoid iterator issues
		ArrayList<Order> processedOrders = new ArrayList<Order>();
		for (Order order : remoteOrders) {
			if (findMatchingOrder(localOrders, order) == null) {

				switch(order.status) {

				// Legal state - add new orders to the list of work and notify the user
				case Order.ORDER_STATUS_NEW:
				case Order.ORDER_STATUS_IN_PROGRESS:
				case Order.ORDER_STATUS_READY:
					processedOrders.add(order);
					break;

				// This is not an illegal state but can happen right now because the phone may not be dismissing the order. Just don't add these locally (or even change their status - think about it more!) - for now
				case Order.ORDER_STATUS_CANCELLED:
					Log.e(TAG, "Skipping cancelled order: " + order.orderId + " with status: " + order.status);
					break;
					
				// Illegal state - print message and don't process the order
				case Order.ORDER_STATUS_COMPLETE:
				case Order.ORDER_STATUS_REJECTED:
				case Order.ORDER_STATUS_FAILED:
				case Order.ORDER_STATUS_INCOMPLETE:
				case Order.ORDER_STATUS_OFFER_REJECTED:
				case Order.ORDER_STATUS_OFFERED:
				case Order.ORDER_STATUS_REMOVED:
				default:
					Log.e(TAG, "Skipping illegal order: " + order.orderId + " with status: " + order.status);
					break;
				}
			}
		}
		
		// Add the orders found
		ArrayList<Order> addedOrders = new ArrayList<Order>();
		for (Order order : processedOrders) {
			if (addOrder(order)) {
				Log.e(TAG, "Adding order: " + order.orderId + " with status: " + order.status);
				addedOrders.add(order);
			} else {
				Log.e(TAG, "Could not add order: " + order.orderId + " with status: " + order.status);
			}
		}
		
		return addedOrders;
	}
	
	ArrayList<Order> processRemovedOrders(ArrayList<Order> localOrders, ArrayList<Order> remoteOrders) {
		
		Log.w(TAG, "processRemovedOrders()");

		// Find the orders to remove and store them in a separate list to avoid iterator issues
		ArrayList<Order> removedOrders = new ArrayList<Order>();
		for (Order order : localOrders) {
			Order remoteOrder = findMatchingOrder(remoteOrders, order);
			if ( remoteOrder == null) {

				switch(order.status) {
				
				// The orders are gone from the host's state, remove them from the local cache too
				case Order.ORDER_STATUS_COMPLETE:
				case Order.ORDER_STATUS_REJECTED:
				case Order.ORDER_STATUS_FAILED:
				case Order.ORDER_STATUS_INCOMPLETE:
					removedOrders.add(order);
					break;
					
				// Local state only, the host doesn't know about those orders any more. 
				case Order.ORDER_STATUS_CANCELLED:
				case Order.ORDER_STATUS_TIMEOUT:
					break;

				// These states should really not be appearing, but if they do change them to cancelled (server timeout) state
				case Order.ORDER_STATUS_NEW:
				case Order.ORDER_STATUS_IN_PROGRESS:
					order.setCancelledState("This order took too long and it timed out. Please process orders promptly.");
					break;
				case Order.ORDER_STATUS_READY:
					order.setCancelledState("This order was not picked up on time and the customer was charged. You may dispose of it or wait in case the customer comes to claim it.");
					break;
					
				// This local state should not exist without the host also being in the same state. Remove them
				case Order.ORDER_STATUS_OFFERED:
				case Order.ORDER_STATUS_OFFER_REJECTED:
				case Order.ORDER_STATUS_REMOVED:
					Log.e(TAG, "Illegal order: " + order.orderId + " with status: " + order.status + ". Removing it locally");
					removedOrders.add(order);
					break;
				default:
					order.setTimeoutState();
					break;
				}
			}
		}
		
		// Remove orders found
		for (Order order : removedOrders) {
			localOrders.remove(order);
		}
		
		return removedOrders;
	}		
	
	ArrayList<Order> processExistingOrders(ArrayList<Order> localOrders, ArrayList<Order> remoteOrders) {

		Log.w(TAG, "processExistingOrders()");
		ArrayList<Order> updatedOrders = new ArrayList<Order>();

		for (Order remoteOrder : remoteOrders) {
			
			Order localOrder = findMatchingOrder(localOrders, remoteOrder);
			
			// Handle the case where the timeout of the bartender has changed while we have open orders 
			if (localOrder != null && localOrder.timeOut != remoteOrder.timeOut) {
				Log.v(TAG, "Adjusting order timeout for order " + localOrder.orderId + " from " + localOrder.timeOut + " to " + remoteOrder.timeOut);
				localOrder.timeOut = remoteOrder.timeOut;
			}
				
			if ( localOrder != null && localOrder.status != remoteOrder.status) {
				
				switch (remoteOrder.status) {
				case Order.ORDER_STATUS_CANCELLED:
					// <=== orderTimeout - **** Change the state and leave it in the order list until user acknowledges the time out ****
					localOrder.setCancelledState("This order took too long and it timed out. Please accept orders promptly.");
					break;
				}
				
				switch (localOrder.status) {
				
				// These orders have a local status that the host doesn't know about and shoudln't know about.
				case Order.ORDER_STATUS_CANCELLED:
				case Order.ORDER_STATUS_TIMEOUT:
					break;
				
				// The status has been changed due to a local user action. Notify the host.
				case Order.ORDER_STATUS_IN_PROGRESS:
				case Order.ORDER_STATUS_READY:
				case Order.ORDER_STATUS_COMPLETE:
				case Order.ORDER_STATUS_REJECTED:
				case Order.ORDER_STATUS_FAILED:
				case Order.ORDER_STATUS_INCOMPLETE:
					updatedOrders.add(localOrder);
					break;

				// These states should not be possible locally if the host has a different status - flag them and move them to timeout
				case Order.ORDER_STATUS_OFFERED:
				case Order.ORDER_STATUS_OFFER_REJECTED:
				case Order.ORDER_STATUS_NEW:
				default:
					localOrder.setTimeoutState();
					Log.e(TAG, "Skipping illegal order: " + localOrder.orderId + " with status: " + localOrder.status);
					break;
				}
				
			}
		}

		// Send changes across if any
		if (updatedOrders.size() > 0) {
			WebServices.orderStatusChanged(updatedOrders, BartsyApplication.this);
		}
		
		return updatedOrders;
	}		
	
	

	/**
	 * 
	 * This functions updates the timers of the various orders and moves them to the expired state in case of a local timeout
	 * 
	 */
	
	private synchronized void updateOrderTimers() {

		Log.v(TAG, "updateOrderTimers()");
		
		for (Order order : mOrders) {

			// The additional timeout when we check for local timeouts gives the server the opportunity to always time out an order first. This 
			long duration  = Constants.timoutDelay + order.timeOut - ((System.currentTimeMillis() - (order.state_transitions[order.status]).getTime()))/60000;
			
			if (duration <= 0) {

				Log.v(TAG, "Order " + order.orderId + " timed out. Status " + order.status + " (" + order.state_transitions[order.status] + 
						"), last_status: " + order.last_status + " (" + order.state_transitions[order.last_status] + 
						"), placed (" + order.state_transitions[Order.ORDER_STATUS_NEW] + ")");

				// Order time out - set it to that state (this won't have an effect if already in that state as the called function guarantees that)
				order.setTimeoutState();
			}
		}
	}
	
	/**
	 * 
	 * TODO - Profile
	 * 
	 */
	
	public void saveVenueProfileImage(Bitmap bitmap) {
		// Save bitmap to file
		String file = getFilesDir()  + File.separator + getResources().getString(R.string.config_venue_profile_picture);
		Log.w(TAG, ">>> Saving venue profile image to " + file);

		try {
			FileOutputStream out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "Error saving venue profile image");
		}
	}
	
	Bitmap loadVenueProfileImage() {
		String file = getFilesDir()  + File.separator + getResources().getString(R.string.config_venue_profile_picture);
		Log.w(TAG, ">>> Loading venue profile from " + file);
		Bitmap image = null;
		try {
			image = BitmapFactory.decodeFile(file);
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "Could not load venue profile image");
		}
		return image;
	}
	
	void eraseVenueProfileImage() {
		
		Log.w(TAG, ">>> Erase venue profile image"); 
		
		File file = new File(getFilesDir()  + File.separator + getResources().getString(R.string.config_venue_profile_picture));
		file.delete();
	}
	
	
	/**
	 * 
	 * TODO - Synchronize people
	 * 
	 * 
	 * 
	 */
	
	synchronized private void updatePeople(JSONObject json) {
		// Verify checked in users match server list
		try {
			
			if(json.has("checkedInUsers")){
				
				JSONArray users;
					users = json.getJSONArray("checkedInUsers");
				
				// Check sizes match
				if (users.length() != mPeople.size()) {
					syncPeople(users);
					return;
				}
				
				// Check Id's match
				for(int i=0; i<users.length() ; i++){
					JSONObject userJson = users.getJSONObject(i);
					
					boolean found = false;
					for (Profile person : mPeople) {
						if (person.userID.equalsIgnoreCase(userJson.getString("bartsyId"))) {
							found = true;
						}
					}
	
					if (!found) {
						syncPeople(users);
						return;
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	synchronized private void syncPeople(JSONArray users) {
		Log.w(TAG, "syncPeople()");
		
		try {
			mPeople.clear();
			for(int i=0; i<users.length() ; i++) {
					mPeople.add(new Profile(users.getJSONObject(i)));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		notifyObservers(PEOPLE_UPDATED);

	}
	

	/**
	 * 
	 * TODO - Vennue profile
	 * 
	 * This venue ID represents the venue in which this tablet is setup. This is
	 * used only on the tablet.
	 * 
	 */

	public String venueProfileID = null;
	public String venueProfileName = null;

	void loadVenueProfile() {
		SharedPreferences sharedPref = getSharedPreferences(getResources()
				.getString(R.string.config_shared_preferences_name),
				Context.MODE_PRIVATE);
		venueProfileID = sharedPref.getString("RegisteredVenueId", null);
		venueProfileName = sharedPref.getString("RegisteredVenueName", null);
	}

	/*****
	 * 
	 * The list of people present (when checked in) is also saved here, in the
	 * global state. We have all handling code here because the application
	 * always runs in the background. If there is an activity that displays a
	 * view of that list listening, we will send an update message, but this
	 * code will always correctly change the model so that we never lose orders
	 * even if hte phone (or tablet) is in sleep mode, etc.
	 * 
	 */

	public ArrayList<Profile> mPeople = new ArrayList<Profile>();
	public static final String PEOPLE_UPDATED = "PEOPLE_UPDATED";


	/**
	 * To add profile to the existing checked in people list
	 * 
	 * @param profile
	 */
	synchronized public void addPerson(Profile profile) {

		// Don't add duplicates
		Profile found = null;
		for (Profile person : mPeople) {
			if (profile.userID.equals(person.userID)) {
				found = person;
				break;
			}
		}
		if (found == null) {
			mPeople.add(profile);
			notifyObservers(PEOPLE_UPDATED);
		}
	}

	/**
	 * Called when we have a person check out of a venue
	 */
	synchronized String removePerson(String profileId, boolean rebuildUI) {
		
		Log.v(TAG, "removePerson(" + profileId + ")");
		String response = null;
		
		for (Profile profile : mPeople) {
			if (profileId.equals(profile.userID)) {
				String message =  "(" + profile.getName() + ", " + profileId + ")";
				Log.v(TAG, "Removing " + message + " from the person list");
				mPeople.remove(profile);
				if (response == null) response = message; else response += ", " + message;
				break;
			}
		}

		if (rebuildUI) notifyObservers(PEOPLE_UPDATED);

		return response;
	}
	
	synchronized String removePeople (JSONArray usersCheckedOut) {
		String notificationMessage = "";
		for(int i = 0 ; i < usersCheckedOut.length() ; i++) {
			String userId;
			try {
				userId = usersCheckedOut.getString(i);
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
			String removeMessage = removePerson(userId, false);
			if (removeMessage == null)
				notificationMessage += "<" + userId + ", not found> ";
			else
				notificationMessage += userId + " ";
		}
		
		notifyObservers(PEOPLE_UPDATED);

		return notificationMessage;
	}
	

	/**********
	 * 
	 * TODO - Orders
	 * 
	 * The order list is saved in the global application state. This is done to
	 * avoid losing any orders while the other activities are swapped in and out
	 * as the user navigates in different screens.
	 * 
	 */

	private ArrayList<Order> mOrders = new ArrayList<Order>();
	public ReentrantLock mOrdersLock = new ReentrantLock();		// used to synchronize access to the orders list on any operation that changes its structure

	public static final String ORDERS_UPDATED = "ORDERS_UPDATED";

	/*
	 * This add a new order after verifying the person placing it is currently
	 * checked in this venue
	 */

	
	/**
	 * Called from the push notification when the order receives from the user
	 * 
	 * @param order
	 * @return 	true if all went well
	 * 			false if order was not added
	 */
	
	private synchronized boolean addOrder(Order order) {

		// Make sure the receiver of the order is present in our list of people
		Profile userFound = null;
		for (Profile p : mPeople) {
			if (p.userID.equals(order.recipientId)) {
				// User found
				userFound = p;
				order.orderRecipient = p;
				break;
			}
		}
		
		// Decline order if the recipient was not found
		if (userFound == null) {
			// User placing the order not in the list of users - decline order and send updated order status to the remote
			Log.d(TAG, "Error processing order " + order.orderId + ". User not checked in: " + order.profileId);
			order.nextNegativeState("User not checked in. Please check out and check back in the venue.");
			order.view = null;
			ArrayList<Order> orders = new ArrayList<Order>();
			orders.add(order);
			WebServices.orderStatusChanged(orders, this);
			return false;
		}
		
		// Add the order to the list 
		return mOrders.add(order);
	}
	
	
	/**
	 * Remove the orders based on the json array which is getting from the user check out PN
	 * 
	 * @param expiredOrders
	 */
	
	synchronized String cancelOrders(JSONArray expiredOrders, String cancelReason) {

		Log.v(TAG, "expireOrders(" + expiredOrders +", " + cancelReason + ")");
		
		// Lock the orders list
		mOrdersLock.lock();

		try {
			// If cancelled orders count greater than 0
			for (int i = 0; i < expiredOrders.length(); i++) {
	
				String orderId = null;
				try {
					// To get the cancelled orderId from the jsonArray response
					orderId = expiredOrders.getString(i);
					Log.v(TAG, "Trying to find order " + orderId);
	
					for (int j = 0; j < mOrders.size(); j++) {
						// To get the order object from the existing orders list
						Order order = mOrders.get(j);
	
						// Matching order - flag it as expired
						if (order.orderId.equalsIgnoreCase(orderId)) {
							order.setCancelledState(cancelReason);
							break;
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					return null;
				}
			}
		} finally {
			mOrdersLock.unlock();
		}
			
			
		// Notify views to display the updated order list
		notifyObservers(ORDERS_UPDATED);
		
		return expiredOrders.toString();
	}

	
	public synchronized void removeOrder(Order order) {
		// Add the order to the list of orders
		removeOrderWihtOutNotify(order);
		notifyObservers(ORDERS_UPDATED);
	}
	
	private synchronized void removeOrderWihtOutNotify(Order order) {
		mOrders.remove(order);
	}

	public int getOrderCount() {
		return mOrders.size();
	}

	
	
	/**
	 * Inventory
	 * 
	 */
	
	public static final String INVENTORY_UPDATED = "INVENTORY_UPDATED";
	
	/************************************************************************
	 * 
	 * 
	 * 
	 * TODO - This is the AllJoyn code
	 * 
	 * 
	 * 
	 */

	ComponentName mRunningService = null;

	/**
	 * Since our application is "rooted" in this class derived from Application
	 * and we have a long-running service, we can't just call finish in one of
	 * the Activities. We have to orchestrate it from here. We send an event
	 * notification out to all of our observers which tells them to exit.
	 * 
	 * Note that as a result of the notification, all of the observers will stop
	 * -- as they should. One of the things that will stop is the AllJoyn
	 * Service. Notice that it is started in the onCreate() method of the
	 * Application. As noted in the Android documentation, the Application class
	 * never gets torn down, nor does it provide a way to tear itself down.
	 * Thus, if the Chat application is ever run again, we need to have a way of
	 * detecting the case where it is "re-run" and then "re-start" the service.
	 */
	public void quit() {
		notifyObservers(APPLICATION_QUIT_EVENT);
		mRunningService = null;
	}

	/**
	 * Application components call this method to indicate that they are alive
	 * and may have need of the AllJoyn Service. This is required because the
	 * Android Application class doesn't have an end to its lifecycle other than
	 * through "kill -9". See quit().
	 */
	public void checkin() {
		Log.v(TAG, "checkin()");
		if (Constants.USE_ALLJOYN && mRunningService == null) {
			Log.v(TAG, "checkin():  Starting the AllJoynService");
			Intent intent = new Intent(this, ConnectivityService.class);
			mRunningService = startService(intent);
			if (mRunningService == null) {
				Log.v(TAG, "checkin(): failed to startService()");
			}
		}
	}

	public static final String APPLICATION_QUIT_EVENT = "APPLICATION_QUIT_EVENT";

	/**
	 * This is the method that AllJoyn Service calls to tell us that an error
	 * has happened. We are provided a module, which corresponds to the high-
	 * level "hunk" of code where the error happened, and a descriptive string
	 * that we do not interpret.
	 * 
	 * We expect the user interface code to sort out the best activity to tell
	 * the user about the error (by calling getErrorModule) and then to call in
	 * to get the string.
	 */
	public synchronized void alljoynError(Module m, String s) {
		mModule = m;
		mErrorString = s;
		notifyObservers(ALLJOYN_ERROR_EVENT);
	}

	/**
	 * Return the high-level module that caught the last AllJoyn error.
	 */
	public Module getErrorModule() {
		return mModule;
	}

	/**
	 * The high-level module that caught the last AllJoyn error.
	 */
	private Module mModule = Module.NONE;

	/**
	 * Enumeration of the high-level moudules in the system. There is one value
	 * per module.
	 */
	public static enum Module {
		NONE, GENERAL, USE, HOST
	}

	/**
	 * Return the error string stored when the last AllJoyn error happened.
	 */
	public String getErrorString() {
		return mErrorString;
	}

	/**
	 * The string representing the last AllJoyn error that happened in the
	 * AllJoyn Service.
	 */
	private String mErrorString = "ER_OK";

	/**
	 * The object we use in notifications to indicate that an AllJoyn error has
	 * happened.
	 */
	public static final String ALLJOYN_ERROR_EVENT = "ALLJOYN_ERROR_EVENT";

	/**
	 * Called from the AllJoyn Service when it gets a FoundAdvertisedName. We
	 * know by construction that the advertised name will correspond to a chat
	 * channel. Note that the channel here is the complete well-known name of
	 * the bus attachment advertising the channel. In most other places it is
	 * simply the channel name, which is the final segment of the well-known
	 * name.
	 */
	public synchronized void addFoundChannel(String channel) {
		Log.v(TAG, "addFoundChannel(" + channel + ")");
		removeFoundChannel(channel);
		mChannels.add(channel);
		Log.v(TAG, "addFoundChannel(): added " + channel);
		notifyObservers(NEW_CHANNEL_FOUND_EVENT);

	}

	/**
	 * The object we use in notifications to indicate that a channel has been
	 * found. By default Bartsy joins new channels automatically unless it's
	 * already connected to a channel. If it's already connected it adds the new
	 * channel to the list of channels in the main action bar UI and notifies
	 * the user with a notification that there are other services available
	 * nearby
	 */
	public static final String NEW_CHANNEL_FOUND_EVENT = "NEW_CHANNEL_FOUND_EVENT";

	/**
	 * Called from the AllJoyn Service when it gets a LostAdvertisedName. We
	 * know by construction that the advertised name will correspond to an chat
	 * channel.
	 */
	public synchronized void removeFoundChannel(String channel) {
		Log.v(TAG, "removeFoundChannel(" + channel + ")");

		for (Iterator<String> i = mChannels.iterator(); i.hasNext();) {
			String string = i.next();
			if (string.equals(channel)) {
				Log.v(TAG, "removeFoundChannel(): removed " + channel);
				i.remove();
			}
		}
	}

	/**
	 * Whenever the user is asked for a channel to join, it needs the list of
	 * channels found via FoundAdvertisedName. This method provides that list.
	 * Since we have no idea how or when the caller is going to access or change
	 * the list, and we are deeply paranoid, we provide a deep copy.
	 */
	public synchronized List<String> getFoundChannels() {
		Log.v(TAG, "getFoundChannels()");
		List<String> clone = new ArrayList<String>(mChannels.size());
		for (String string : mChannels) {
			Log.v(TAG, "getFoundChannels(): added " + string);
			clone.add(new String(string));
		}
		return clone;
	}

	/**
	 * The channels list is the list of all well-known names that correspond to
	 * channels we might conceivably be interested in. We expect that the "use"
	 * GUID will allow the local user to have this list displayed in a
	 * "join channel" dialog, whereupon she will choose one. This will
	 * eventually result in a joinSession call out from the AllJoyn Service
	 */
	private List<String> mChannels = new ArrayList<String>();

	/**
	 * The application has three ideas about the state of its channels. This is
	 * very detailed for a real application, but since this is an AllJoyn
	 * sample, we think it is important to convey the detailed state back to our
	 * user, whom we assume knows what it all means.
	 * 
	 * We have a basic bus attachment state, which reflects the fact that we
	 * can't do anything without a bus attachment. When the service comes up it
	 * automatically connects and starts discovering other instances of the
	 * application, so this isn't terribly interesting.
	 */
	public ConnectivityService.BusAttachmentState mBusAttachmentState = ConnectivityService.BusAttachmentState.DISCONNECTED;

	/**
	 * Set the status of the "host" channel. The AllJoyn Service part of the
	 * Application is expected to make this call to set the status to reflect
	 * the status of the underlying AllJoyn session.
	 */
	public synchronized void hostSetChannelState(
			ConnectivityService.HostChannelState state) {
		mHostChannelState = state;
		notifyObservers(HOST_CHANNEL_STATE_CHANGED_EVENT);
	}

	/**
	 * Get the state of the "use" channel.
	 */
	public synchronized ConnectivityService.HostChannelState hostGetChannelState() {
		return mHostChannelState;
	}

	/**
	 * The "host" state which reflects the state of the part of the system
	 * related to hosting an chat channel. In a "real" application this kind of
	 * detail probably isn't appropriate, but we want to do so for this sample.
	 */
	private ConnectivityService.HostChannelState mHostChannelState = ConnectivityService.HostChannelState.IDLE;

	/**
	 * Set the name part of the "host" channel. Since we are going to "use" a
	 * channel that is implemented remotely and discovered through an AllJoyn
	 * FoundAdvertisedName, this must come from a list of advertised names.
	 * These names are our channels, and so we expect the GUI to choose from
	 * among the list of channels it retrieves from getFoundChannels().
	 * 
	 * Since we are talking about user-level interactions here, we are talking
	 * about the final segment of a well-known name representing a channel at
	 * this point.
	 */
	public synchronized void hostSetChannelName(String name) {
		mHostChannelName = name;
		notifyObservers(HOST_CHANNEL_STATE_CHANGED_EVENT);
	}

	/**
	 * Get the name part of the "use" channel.
	 */
	public synchronized String hostGetChannelName() {
		return mHostChannelName;
	}

	/**
	 * The name of the "host" channel which the user has selected.
	 */
	private String mHostChannelName;

	/**
	 * The object we use in notifications to indicate that the state of the
	 * "host" channel or its name has changed.
	 */
	public static final String HOST_CHANNEL_STATE_CHANGED_EVENT = "HOST_CHANNEL_STATE_CHANGED_EVENT";

	/**
	 * Set the status of the "use" channel. The AllJoyn Service part of the
	 * appliciation is expected to make this call to set the status to reflect
	 * the status of the underlying AllJoyn session.
	 */
	public synchronized void useSetChannelState(
			ConnectivityService.UseChannelState state) {
		mUseChannelState = state;
		notifyObservers(USE_CHANNEL_STATE_CHANGED_EVENT);
	}

	/**
	 * Get the state of the "use" channel.
	 */
	public synchronized ConnectivityService.UseChannelState useGetChannelState() {
		return mUseChannelState;
	}

	/**
	 * The "use" state which reflects the state of the part of the system
	 * related to using a remotely hosted chat channel. In a "real" application
	 * this kind of detail probably isn't appropriate, but we want to do so for
	 * this sample.
	 */
	private ConnectivityService.UseChannelState mUseChannelState = ConnectivityService.UseChannelState.IDLE;

	/**
	 * The name of the "use" channel which the user has selected.
	 */
	private String mUseChannelName = null;

	/**
	 * Set the name part of the "use" channel. Since we are going to "use" a
	 * channel that is implemented remotely and discovered through an AllJoyn
	 * FoundAdvertisedName, this must come from a list of advertised names.
	 * These names are our channels, and so we expect the GUI to choose from
	 * among the list of channels it retrieves from getFoundChannels().
	 * 
	 * Since we are talking about user-level interactions here, we are talking
	 * about the final segment of a well-known name representing a channel at
	 * this point.
	 */
	public synchronized void useSetChannelName(String name) {
		mUseChannelName = name;
		notifyObservers(USE_CHANNEL_STATE_CHANGED_EVENT);
	}

	/**
	 * Get the name part of the "use" channel.
	 */
	public synchronized String useGetChannelName() {
		return mUseChannelName;
	}

	/**
	 * The object we use in notifications to indicate that the state of the
	 * "use" channel or its name has changed.
	 */
	public static final String USE_CHANNEL_STATE_CHANGED_EVENT = "USE_CHANNEL_STATE_CHANGED_EVENT";

	/**
	 * This is the method that the "use" tab user interface calls when the user
	 * indicates that she wants to join a channel. The channel name must have
	 * been previously set with a call to setUseChannelName(). The "use" channel
	 * is the channel that we talk about in the "Use" tab. Since it's a remote
	 * channel in a remote bus attachment, we need to tell the AllJoyn Service
	 * to go join the corresponding session.
	 */
	public synchronized void useJoinChannel() {
		clearHistory();
		notifyObservers(USE_CHANNEL_STATE_CHANGED_EVENT);
		notifyObservers(USE_JOIN_CHANNEL_EVENT);
	}

	/**
	 * The object we use in notifications to indicate that user has requested
	 * that we join a channel in the "use" tab.
	 */
	public static final String USE_JOIN_CHANNEL_EVENT = "USE_JOIN_CHANNEL_EVENT";

	/**
	 * This is the method that the "use" tab user interface calls when the user
	 * indicates that she wants to leave a channel. Since we're talking about a
	 * remote channel corresponding to a session with a remote bus attachment,
	 * we needto tell the AllJoyn Service to leave the corresponding session.
	 */
	public synchronized void useLeaveChannel() {
		notifyObservers(USE_CHANNEL_STATE_CHANGED_EVENT);
		notifyObservers(USE_LEAVE_CHANNEL_EVENT);
	}

	/**
	 * The object we use in notifications to indicate that user has requested
	 * that we leave a channel in the "use" tab.
	 */
	public static final String USE_LEAVE_CHANNEL_EVENT = "USE_LEAVE_CHANNEL_EVENT";

	/**
	 * This is the method that the "host" tab user interface calls when the user
	 * has completed providing her preferences for hosting a channel.
	 */
	public synchronized void hostInitChannel() {
		notifyObservers(HOST_CHANNEL_STATE_CHANGED_EVENT);
		notifyObservers(HOST_INIT_CHANNEL_EVENT);
	}

	/**
	 * The object we use in notifications to indicate that user has requested
	 * that we initialize the host channel parameters in the "use" tab.
	 */
	public static final String HOST_INIT_CHANNEL_EVENT = "HOST_INIT_CHANNEL_EVENT";

	/**
	 * This is the method that the "host" tab user interface calls when the user
	 * indicates that she wants to start hosting a channel.
	 */
	public synchronized void hostStartChannel() {
		notifyObservers(HOST_CHANNEL_STATE_CHANGED_EVENT);
		notifyObservers(HOST_START_CHANNEL_EVENT);
	}

	/**
	 * The object we use in notifications to indicate that user has requested
	 * that we initialize the host channel parameters in the "use" tab.
	 */
	public static final String HOST_START_CHANNEL_EVENT = "HOST_START_CHANNEL_EVENT";

	/**
	 * This is the method that the "host" tab user interface calls when the user
	 * indicates that she wants to stop hosting a channel.
	 */
	public synchronized void hostStopChannel() {
		notifyObservers(HOST_CHANNEL_STATE_CHANGED_EVENT);
		notifyObservers(HOST_STOP_CHANNEL_EVENT);
	}

	/**
	 * The object we use in notifications to indicate that user has requested
	 * that we initialize the host channel parameters in the "use" tab.
	 */
	public static final String HOST_STOP_CHANNEL_EVENT = "HOST_STOP_CHANNEL_EVENT";

	/**
	 * Whenever our local user types a message, we need to send it out on the
	 * channel, which we do by calling addOutboundItem. This will eventually
	 * result in an AllJoyn Bus Signal being sent to the other participants on
	 * the channel. Since the sessions that implement the channel don't "echo"
	 * back to the source, we need to echo the message into our history.
	 */
	public synchronized void newLocalUserMessage(String message) {
		addInboundItem("Me", message);
		if (useGetChannelState() == ConnectivityService.UseChannelState.JOINED) {
			addOutboundItem(message);
		}
	}

	/**
	 * Whenever a user types a message into the channel, we expect the AllJoyn
	 * Service local to that user to send the message to everyone participating
	 * on the channel. At each participant, the messages arrive in the AllJoyn
	 * Service as a Bus Signal. The Service handles the signals and passes the
	 * associated messages on to us here. We expect the nickname to be the
	 * unique ID of the sending bus attachment. This is not very user friendly,
	 * but is convenient and guaranteed to be unique.
	 */
	public synchronized void newRemoteUserMessage(String nickname,
			String message) {
		addInboundItem(nickname, message);
	}

	final int OUTBOUND_MAX = 5;

	/**
	 * The object we use in notifications to indicate that the the user has
	 * entered a message and it is queued to be sent to the outside world.
	 */
	public static final String OUTBOUND_CHANGED_EVENT = "OUTBOUND_CHANGED_EVENT";

	/**
	 * The outbound list is the list of all messages that have been originated
	 * by our local user and are designed for the outside world.
	 */
	private List<String> mOutbound = new ArrayList<String>();

	/**
	 * Whenever the local user types a message for distribution to the channel
	 * it calls newLocalMessage. We are called to queue up the message and send
	 * a notification to all of our observers indicating that the we have
	 * something ready to go out. We expect that the AllJoyn Service will
	 * eventually respond by calling back in here to get items off of the queue
	 * and send them down the session corresponding to the channel.
	 */
	private void addOutboundItem(String message) {
		if (mOutbound.size() == OUTBOUND_MAX) {
			mOutbound.remove(0);
		}
		mOutbound.add(message);
		notifyObservers(OUTBOUND_CHANGED_EVENT);
	}

	/**
	 * Whenever the local user types a message for distribution to the channel
	 * it is queued to a list of outbound messages. The AllJoyn Service is
	 * notified and calls in here to get the outbound messages that need to be
	 * sent.
	 */
	public synchronized String getOutboundItem() {
		if (mOutbound.isEmpty()) {
			return null;
		} else {
			return mOutbound.remove(0);
		}
	}

	/**
	 * The object we use in notifications to indicate that the history state of
	 * the model has changed and observers need to synchronize with it.
	 */
	public static final String HISTORY_CHANGED_EVENT = "HISTORY_CHANGED_EVENT";

	/**
	 * Whenever a message comes in from the AllJoyn Service over its channel
	 * session, it calls in here. We just add the message item to the history
	 * list, with the "nickname" provided by Service. This is currently expected
	 * to be the unique name of the bus attachment originating the message. Once
	 * the message is saved in the history, a change notification will be sent
	 * to all observers indicating that the history has changed. The user
	 * interface part of the application is then expected to wake up and
	 * syncrhonize itself to the new history.
	 */
	private void addInboundItem(String nickname, String message) {
		addHistoryItem(nickname, message);
	}

	/**
	 * Don't keep an infinite amount of history. Although we don't want to admit
	 * it, this is a toy application, so we just keep a little history.
	 */
	final int HISTORY_MAX = 20;

	/**
	 * The history list is the list of all messages that have been originated or
	 * recieved by the "use" channel.
	 */
	private List<String> mHistory = new ArrayList<String>();

	/**
	 * Whenever a user in the channel types a message, it needs to result in the
	 * history being updated with the nickname of the user originating the
	 * message and the message itself. We keep a history list of a given maximum
	 * size just for general principles. This history list contains the local
	 * time at which the message was recived, the nickname of the user who
	 * originated the message and the message itself. We send a change
	 * notification to all observers indicating that the history has changed
	 * when we modify it.
	 */
	private void addHistoryItem(String nickname, String message) {
		if (mHistory.size() == HISTORY_MAX) {
			mHistory.remove(0);
		}

		DateFormat dateFormat = new SimpleDateFormat("HH:mm");
		Date date = new Date();
		// mHistory.add("[" + dateFormat.format(date) + "] (" + nickname + ") "
		// + message);

		// Don't add local history messages for now - TODO
		if (nickname.equalsIgnoreCase("me"))
			return;

		mHistory.add(message);
		notifyObservers(HISTORY_CHANGED_EVENT);
	}

	/**
	 * Clear the history list. Whenever a user joins a new channel, we want to
	 * get rid of any existing history to avoid confusion.
	 */
	private void clearHistory() {
		mHistory.clear();
		notifyObservers(HISTORY_CHANGED_EVENT);
	}

	/**
	 * Whenever a new message is added to the history list, an update
	 * notification is sent to all of the observers registered to this object
	 * that indicates that the history list has changed. When the observer hears
	 * that the list has changed, it calls in here to get the new contents.
	 * Since we have no idea how or when the caller is going to access or change
	 * the list, and we are deeply paranoid, we provide a deep copy.
	 */
	public synchronized List<String> getHistory() {
		List<String> clone = new ArrayList<String>(mHistory.size());
		for (String string : mHistory) {
			clone.add(new String(string));
		}
		return clone;
	}

	public synchronized String getLastMessage() {
		if (mHistory.size() == 0)
			return null;
		else
			return mHistory.get(mHistory.size() - 1);
	}

	/**
	 * This object is really the model of a model-view-controller architecture.
	 * The observer/observed design pattern is used to notify view-controller
	 * objects when the model has changed. The observed object is this object,
	 * the model. Observers correspond to the view-controllers which in this
	 * case are the Android Activities (corresponding to the use tab and the
	 * hsot tab) and the Android Service that does all of the AllJoyn work. When
	 * an observer wants to register for change notifications, it calls here.
	 */
	@Override
	public synchronized void addObserver(AppObserver obs) {
		Log.v(TAG, "addObserver(" + obs + ")");
		if (mObservers.indexOf(obs) < 0) {
			mObservers.add(obs);
		}
	}

	/**
	 * When an observer wants to unregister to stop receiving change
	 * notifications, it calls here.
	 */
	@Override
	public synchronized void deleteObserver(AppObserver obs) {
		Log.v(TAG, "deleteObserver(" + obs + ")");
		mObservers.remove(obs);
	}

	/**
	 * This object is really the model of a model-view-controller architecture.
	 * The observer/observed design pattern is used to notify view-controller
	 * objects when the model has changed. The observed object is this object,
	 * the model. Observers correspond to the view-controllers which in this
	 * case are the Android Activities (corresponding to the use tab and the
	 * Host tab) and the Android Service that does all of the AllJoyn work. When
	 * the model (this object) wants to notify its observers that some
	 * interesting event has happened, it calls here and provides an object that
	 * identifies what has happened. To keep things obvious, we pass a
	 * descriptive string which is then sent to all observers. They can decide
	 * to act or not based on the content of the string.
	 */
	public void notifyObservers(Object arg) {
		Log.v(TAG, "notifyObservers(" + arg + ")");
		for (AppObserver obs : mObservers) {
			Log.v(TAG, "notify observer = " + obs);
			obs.update(this, arg);
		}
	}

	/**
	 * The observers list is the list of all objects that have registered with
	 * us as observers in order to get notifications of interesting events.
	 */
	private List<AppObserver> mObservers = new ArrayList<AppObserver>();
}
