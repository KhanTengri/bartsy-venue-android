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

import static com.vendsy.bartsy.venue.utils.Utilities.SENDER_ID;

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

		super(SENDER_ID);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.v(TAG, "senderid :::" + SENDER_ID);
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
		String messageTypeMSG = "";
		try {

			// JSONObject json = new JSONObject(message);
			if (json.has("messageType")) {
				// To check push notification message type
				if (json.getString("messageType").equals("placeOrder")) {
					Order order = new Order(json);
					// Add order to order list
					app.addOrder(order);
					// To display message in PN
					messageTypeMSG = "User placed an Order";
				} else if (json.getString("messageType").equals("userCheckIn")) {
					Profile profile = new Profile(json);
					// Add user profile to people list
					app.addPerson(profile);
					// To display message in PN
					messageTypeMSG = "User checkIn";
				} else if (json.getString("messageType").equals("orderTimeout")) {
					
					JSONArray expiredOrders = json.has("ordersCancelled") ? json.getJSONArray("ordersCancelled") : null;
					
					// When user checkout from venue, it is required to remove user open orders
					if (expiredOrders != null && expiredOrders.length() > 0) {
						// For removing the cancelled orders
						app.expireOrders(expiredOrders);

					}
					// To display message in PN
					messageTypeMSG = "Order time out";
				}

				else if (json.getString("messageType").equals("userCheckOut")) {
					// Remove user profile from people list
					app.removePerson(json.getString("bartsyId"));
					JSONArray cancelledOrders = json.has("cancelledOrders") ? json
							.getJSONArray("cancelledOrders") : null;
					// When user checkout from venue, it is required to remove user open orders
					if (cancelledOrders != null && cancelledOrders.length() > 0) {
						// For removing the cancelled orders
						app.expireOrders(cancelledOrders);

					}
					// To display message in PN
					messageTypeMSG = "User check out";

				}else if(json.getString("messageType").equals("userTimeout")){
					
					JSONArray usersCheckedOut = json.has("usersCheckedOut") ? json
							.getJSONArray("usersCheckedOut") : null;
//					JSONArray ordersCancelled = json.has("ordersCancelled") ? json
//							.getJSONArray("ordersCancelled") : null;
//					// When userTimeout push notification came, it is required to remove user open orders
//					if (ordersCancelled != null && ordersCancelled.length() > 0) {
//						// For removing the cancelled orders
//						app.removeOrders(ordersCancelled);
//					}
							
					// When userTimeout push notification received, it is required to remove user profiles form people list 
					if (usersCheckedOut != null && usersCheckedOut.length() > 0) {
						// For removing the time out users
						if(usersCheckedOut!=null&&usersCheckedOut.length()>0)
							for(int i=0;i<usersCheckedOut.length();i++)
								app.removePerson(usersCheckedOut.getString(i));

					}
					// To display message in PN
					messageTypeMSG = "User time out";
				}
					
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return messageTypeMSG;
	}

	

	@Override
	protected void onMessage(Context context, Intent intent) {

//		Log.v(TAG, "Received message");

		String message = (String) intent.getExtras().get(Utilities.EXTRA_MESSAGE);
		String count = (String) intent.getExtras().get("badgeCount");
		
//		Log.v(TAG, "message: " + message);
		
		Log.i(TAG, "<=== pushNotification(" + message + ")");

		String notifyMSG = "Received..";
		
		if (message != null) {
			try {
				JSONObject json = new JSONObject(message);
				if (json.has("messageType")) {
					if (!json.getString("messageType").equalsIgnoreCase("heartBeat"))
						generateNotification(context, notifyMSG, count);
				}
				notifyMSG = processPushNotification(json);
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
