package com.vendsy.bartsy.venue.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.vendsy.bartsy.venue.GCMIntentService;
import com.vendsy.bartsy.venue.R;
import com.vendsy.bartsy.venue.model.Order;
import com.vendsy.bartsy.venue.model.Profile;

public class WebServices {

	private static String TAG="WebServices";
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
		Log.i(TAG," *** Webservice postRequest " + data);
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
					Log.i("Exception Found","::: " + e.getMessage());

				}
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		Log.i(TAG, "webservice postrequest method "+response);
		return response;

	}

	/**
	 * To download image from the url and save in the model(Object)
	 * 
	 * @param fileUrl
	 * @param model
	 * @param imageView
	 */
	public static void downloadImage(final String fileUrl, final Object model,
			final ImageView imageView) {

		System.out.println("download file");
		new AsyncTask<String, Void, Bitmap>() {
			Bitmap bitmapImg;

			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();

			}

			protected Bitmap doInBackground(String... params) {
				// TODO Auto-generated method stub

				System.out.println("doing back ground");
				URL myFileUrl = null;
				try {
					myFileUrl = new URL(fileUrl);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {

					HttpURLConnection conn = (HttpURLConnection) myFileUrl
							.openConnection();
					conn.setDoInput(true);
					conn.connect();
					InputStream is = conn.getInputStream();
					bitmapImg = BitmapFactory.decodeStream(is);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return bitmapImg;
			}

			protected void onPostExecute(Bitmap result) {
				// TODO Auto-generated method stub
				System.out.println("on post ******************");
				if (model instanceof Profile) {
					Profile profile = (Profile) model;
					profile.setImage(result);
				}
				imageView.setImageBitmap(result);

			}

		}.execute();

	}

	/**
	 * Service call for the user check in or out
	 * 
	 * @param context
	 * @param venueId
	 * @param url
	 * @return
	 */
	public static String userCheckInOrOut(final Context context,
			String venueId, String url) {
		String response = null;
		SharedPreferences sharedPref = context.getSharedPreferences(
				context.getResources().getString(
						R.string.config_shared_preferences_name),
				Context.MODE_PRIVATE);
		Resources r = context.getResources();
		int bartsyId = sharedPref.getInt(r.getString(R.string.bartsyUserId), 0);

		Log.i(TAG,"bartsyId ::: " + bartsyId);
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
			Log.i(TAG,"response :: " + response);

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
			// To check network connection available or not
			boolean status = isNetworkAvailable(context);
			if (status == true) {

				httpRequest.setURI(new URI(url));

				HttpResponse response = httpClient.execute(httpRequest);

				// To read response by using buffer
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

	/**
	 * Service call to change order status
	 * 
	 * @param order
	 * @param context
	 */
	public static void orderStatusChanged(final Order order,
			final Context context) {
		new Thread() {

			@Override
			public void run() {
				try {
					String response;
					response = postRequest(Constants.URL_UPDATE_ORDER_STATUS,
							order.statusChangedJSON(), context);
					System.out.println("response :: " + response);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	/**
	 * Service call to get lost data locally from server
	 * 
	 * @param order
	 * @param context
	 */
	public static JSONObject syncWithServer(final String venueId,
			final Context context) {

		try {
			String response;
			// To get all checked in profiles and open orders data based on the
			// venue Id
			JSONObject json = new JSONObject();
			json.put("venueId", venueId);

			// Sync service call
			response = postRequest(Constants.URL_SYNC_WITH_SERVER, json,
					context);

			// Return response in JSON object
			return new JSONObject(response);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// alert box
	public static void alertbox(final String message, final Activity a) {

		new AlertDialog.Builder(a).setMessage(message).setCancelable(false)
				.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {

						return;
					}
				}).show();
	}

}
