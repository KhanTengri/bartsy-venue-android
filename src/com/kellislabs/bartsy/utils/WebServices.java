package com.kellislabs.bartsy.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.kellislabs.bartsy.R;
import com.kellislabs.bartsy.model.Order;
import com.kellislabs.bartsy.model.Profile;
import com.kellislabs.bartsy.service.GCMIntentService;

public class WebServices {

	// checking internet connection
	public static boolean isNetworkAvailable(Context context) throws Exception {

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isFailover())
			return false;
		else if (cm.getActiveNetworkInfo() != null

		&& cm.getActiveNetworkInfo().isAvailable()
				&& cm.getActiveNetworkInfo().isConnected()) {

			return true;

		}

		else {

			return false;
		}

	}

	/**
	 * Create a new HttpClient and Post data
	 * 
	 * @param url
	 * @param postData
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static String postRequest(String url, JSONObject postData,
			Context context) throws Exception {

		String response = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);

		String data = postData.toString();
		System.out.println("*** " + data);
		try {
			boolean status = isNetworkAvailable(context);
			if (status == true) {
				try {
					httppost.setEntity(new StringEntity(data));

					// Execute HTTP Post Request

					httppost.setHeader("Accept", "application/json");
					httppost.setHeader("Content-type", "application/json");

					HttpResponse httpResponse = httpclient.execute(httppost);

					String responseofmain = EntityUtils.toString(httpResponse
							.getEntity());
					response = responseofmain.toString();
				} catch (Exception e) {
					Log.e("log_tag", "Error in http connection" + e.toString());
					System.out.println("::: " + e.getMessage());

				}
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		return response;

	}

	public static String userCheckInOrOut(final Context context, String venueId,
			String url) {
		String response = null;
		SharedPreferences sharedPref = context.getSharedPreferences(
				context.getResources().getString(
						R.string.config_shared_preferences_name),
				Context.MODE_PRIVATE);
		Resources r = context.getResources();
		int bartsyId = sharedPref.getInt(r.getString(R.string.bartsyUserId), 0);

		System.out.println("bartsyId ::: " + bartsyId);
		final JSONObject json = new JSONObject();
		try {
			json.put("bartsyId", bartsyId);
			json.put("venueId", venueId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {

			response = postRequest(url, json, context);
			System.out.println("response :: " + response);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}

	/*
	 * This Method i am using for each and every request which is going through
	 * get() method.
	 */
	public static String getRequest(String url, Context context) {
		System.out.println("web service calling ");
		BufferedReader bufferReader = null;
		StringBuffer stringBuffer = new StringBuffer("");
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpRequest = new HttpGet();
		String result = "";

		try {
			boolean status = isNetworkAvailable(context);
			if (status == true) {

				httpRequest.setURI(new URI(url));

				HttpResponse response = httpClient.execute(httpRequest);

				bufferReader = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));
				String line = "";
				String NL = System.getProperty("line.separator");
				while ((line = bufferReader.readLine()) != null) {
					stringBuffer.append(line + NL);
				}
				bufferReader.close();

			}
			result = stringBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public static void saveProfileData(Profile bartsyProfile, Context context) {
		try {
			// To get GCM reg ID from the Shared Preference
			SharedPreferences settings = context.getSharedPreferences(
					GCMIntentService.REG_ID, 0);
			String deviceToken = settings.getString("RegId", "");

			int deviceType = Constants.DEVICE_Type;
			JSONObject json = new JSONObject();
			json.put("userName", bartsyProfile.getUsername());
			json.put("name", bartsyProfile.getName());
			json.put("loginId", bartsyProfile.getSocialNetworkId());
			json.put("loginType", bartsyProfile.getType());
			json.put("gender", bartsyProfile.getGender());
			json.put("deviceType", deviceType);
			json.put("deviceToken", deviceToken);

			try {
				String responses = WebServices.postRequest(
						Constants.URL_POST_PROFILE_DATA, json,
						context.getApplicationContext());
				System.out.println("responses   " + responses);
				if (bartsyProfile != null) {
					int bartsyUserId = 0;
					JSONObject resultJson = new JSONObject(responses);
					String errorCode = resultJson.getString("errorCode");
					String errorMessage = resultJson.getString("errorMessage");
					if (resultJson.has("bartsyUserId"))
						bartsyUserId = resultJson.getInt("bartsyUserId");

					System.out.println("bartsyUserId " + bartsyUserId);

					if (bartsyUserId > 0) {
						SharedPreferences sharedPref = context
								.getSharedPreferences(
										context.getResources()
												.getString(
														R.string.config_shared_preferences_name),
										Context.MODE_PRIVATE);
						Resources r = context.getResources();

						SharedPreferences.Editor editor = sharedPref.edit();
						editor.putInt(r.getString(R.string.bartsyUserId),
								bartsyUserId);
						editor.commit();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
}
