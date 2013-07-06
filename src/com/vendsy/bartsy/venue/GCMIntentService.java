/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vendsy.bartsy.venue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.vendsy.bartsy.venue.model.Order;
import com.vendsy.bartsy.venue.model.Profile;
import com.vendsy.bartsy.venue.utils.Constants;
import com.vendsy.bartsy.venue.utils.Utilities;
import com.vendsy.bartsy.venue.utils.WebServices;

//import static com.vendsy.bartsy.venue.utils.Utilities.displayMessage;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {
	public static final String REG_ID = "RegId";

	@SuppressWarnings("hiding")
	private static final String TAG = "GCMIntentService";
	// Setup application pointer

	public GCMIntentService() {

		super(WebServices.SENDER_ID);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.v(TAG, "senderid :::" + WebServices.SENDER_ID);
		Log.v(TAG, "in on registered method");
		Log.v(TAG, "Device registered: regId = " + registrationId);

		SharedPreferences settings = getSharedPreferences(REG_ID, 0);
		// String uname = settings.getString("user", "").toString();
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("RegId", registrationId);

		editor.commit();
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.v(TAG, "Device unregistered");
		// displayMessage(context, getString(R.string.gcm_unregistered));
		if (GCMRegistrar.isRegisteredOnServer(getApplicationContext())) {
			// ServerUtilities.unregister(context, registrationId);
		} else {
			// This callback results from the call to unregister made on ServerUtilities when the registration to the server failed.
			Log.v(TAG, "Ignoring unregister callback");
		}
	}

	/**
	 * To process push notification message
	 * 
	 * @param message
	 * @return
	 */
	private String processPushNotification(JSONObject json) {
		
		BartsyApplication app = (BartsyApplication) getApplication();
		
		String notificationMessage = null;
		try {

			// JSONObject json = new JSONObject(message);
			if (json.has("messageType")) {

				if (json.getString("messageType").equals("placeOrder")) {

					// <=== placeOrder - **** new order placed ****
					
					// Add order to order list
//					Order order = new Order(json);
//					app.addOrder(order);
//					notificationMessage = "New order: " + order.title + " for " + order.receiverId + " from " + order.senderId;
	
					app.update();
					
				} else if (json.getString("messageType").equals("userCheckIn")) {
					
					// <=== userCheckIn - **** check the user in ****
					
					Profile profile = new Profile(json);
					app.addPerson(profile);
					notificationMessage = "User check in: " + profile.getName() + " ( " + profile.userID + ")";

				} else if (json.getString("messageType").equals("orderTimeout")) {
					
					// <=== orderTimeout - **** Change the state and leave it in the order list until user acknowledges the time out ****
					
//					JSONArray expiredOrders = json.has("ordersCancelled") ? json.getJSONArray("ordersCancelled") : null;
					
					// When user checkout from venue, it is required to remove user open orders
//					if (expiredOrders != null && expiredOrders.length() > 0) {
						// For removing the cancelled orders
//						notificationMessage = app.cancelOrders(expiredOrders, "This order took too long and it timed out. Please accept orders promptly.");
//					}

					// Make sure we successfully removed some orders and set up notification message accordingly
//					if (notificationMessage == null) {
//						notificationMessage = "System error: Received a timeout, but no orders were attached";
//						Log.e(TAG, notificationMessage);
//					}
					
					app.update();
				}

				else if (json.getString("messageType").equals("userCheckOut")) {
					
					// <=== userCheckOut - **** Remove user profile from people list. Notice that we keep a pointer to the profile from any open orders for display purposes. ****

					// Remove the person
					notificationMessage = app.removePerson(json.getString("bartsyId"), true);

							
					// Flag the orders as cancelled
					JSONArray cancelledOrders = json.has("cancelledOrders") ? json.getJSONArray("cancelledOrders") : null;
					if (cancelledOrders != null && cancelledOrders.length() > 0) {
						String orderMessage = app.cancelOrders(cancelledOrders, "User checked out and the order was charged.You may dispose of it or wait.");

						if (notificationMessage == null) notificationMessage = ""; else notificationMessage += "\n";
						notificationMessage += "Orders cancelled: " + orderMessage;
					}

					// Make sure we successfully removed some orders and set up notification message accordingly
					if (notificationMessage == null) {
						notificationMessage = "System error: User check out received with no attached user!";
						Log.e(TAG, notificationMessage);
					}

				}else if(json.getString("messageType").equals("userTimeout")) {
					
					// <=== userTimeout - **** Check out time-out users (this means their connection is not working).  ****

					JSONArray usersCheckedOut = json.has("usersCheckedOut") ? json.getJSONArray("usersCheckedOut") : null;
							
					// When userTimeout push notification received, it is required to remove user profiles from people list 
					if(usersCheckedOut != null && usersCheckedOut.length() > 0) {
						notificationMessage = "Removing users: ";
						String removeMessage = app.removePeople(usersCheckedOut);
						if (removeMessage == null)
							notificationMessage = "System error: Trying to remove peole failed for user: " + usersCheckedOut;
						else
							notificationMessage = "Removed users: " + removeMessage;					
					}
				}
					
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return notificationMessage;
	}

	

	@Override
	protected void onMessage(Context context, Intent intent) {

		String message = (String) intent.getExtras().get(Utilities.EXTRA_MESSAGE);
		String count = (String) intent.getExtras().get("badgeCount");
		
		Log.i(TAG, "<=== pushNotification(" + message + ")");

		// Process the notification and if successful post a user-level notification
		if (message != null) {
			try {
				JSONObject json = new JSONObject(message);
				String notifyMSG = processPushNotification(json);
				if (notifyMSG != null) {
					generateNotification(context, notifyMSG, count);
					Log.v(TAG, "<=== pushNotification result: " + notifyMSG);
				}
			} catch (JSONException e) {
				e.printStackTrace(); 
			}
		}
	}

	// @Override
	// protected void onDeletedMessages(Context context, int total) {
	// Log.v(TAG, "Received deleted messages notification");
	// String message = getString(R.string.gcm_deleted, total);
	// displayMessage(context, message);
	// // notifies user
	// generateNotification(context, message);
	// }

	@Override
	public void onError(Context context, String errorId) {
		Log.v(TAG, "Received error: " + errorId);
		// displayMessage(context, getString(R.string.gcm_error, errorId));
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		Log.v(TAG, "Received recoverable error: " + errorId);
		// displayMessage(context, getString(R.string.gcm_recoverable_error,
		// errorId));
		return super.onRecoverableError(context, errorId);
	}

	/**
	 * To generate a notification to inform the user that server has sent a message.
	 * 
	 * @param count
	 * @param count 
	 */
	private static void generateNotification(Context context, String message, String count) {
		int icon = R.drawable.ic_launcher;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);
		String title = context.getString(R.string.app_name);

		Intent notificationIntent = new Intent(context, MainActivity.class);
		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, title, message, intent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		try {
			int countValue = Integer.parseInt(count);
			notification.number = countValue;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		// // Play default notification sound
		notification.defaults = Notification.DEFAULT_SOUND;
		notificationManager.notify(0, notification);
	}

}
