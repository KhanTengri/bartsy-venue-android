package com.vendsy.bartsy.venue;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.vendsy.bartsy.venue.utils.Constants;
import com.vendsy.bartsy.venue.utils.Utilities;
import com.vendsy.bartsy.venue.utils.WebServices;

public class VenueProfileActivity extends Activity implements OnClickListener {

	private static final String TAG = "VenueRegistrationActivity";
	
	// Form elements
	private EditText locuId, paypal, wifiName, wifiPassword,orderTimeOut;
	private Handler handler = new Handler();
	
	// Progress dialog
	private ProgressDialog progressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.venue_profile);
		
		// Try to get all form elements from the XML
		locuId = (EditText) findViewById(R.id.locuId);
		paypal = (EditText) findViewById(R.id.paypalEdit);
		wifiName = (EditText) findViewById(R.id.wifiName);
		wifiPassword = (EditText) findViewById(R.id.wifiPassword);
		orderTimeOut = (EditText) findViewById(R.id.orderTimeOut);

		// Setup listeners
		findViewById(R.id.view_registration_button_submit).setOnClickListener(this);
		findViewById(R.id.view_registration_button_cancel).setOnClickListener(this);
		findViewById(R.id.view_registration_wifi_checkbox).setOnClickListener(this);
	}

	
	/**
	 * Click listener
	 */
	
	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
			
			case R.id.view_registration_button_submit:
				Log.d("Bartsy", "Clicked on submit button");
				
				// Perform registration - for now assume all will go well
				processRegistration();
				break;
				
			case R.id.view_registration_button_cancel:
				finish();
				break;
				
			case R.id.view_registration_wifi_checkbox:
				
				if (((CheckBox) findViewById(R.id.view_registration_wifi_checkbox)).isChecked()) {
					findViewById(R.id.view_registration_wifi).setVisibility(View.VISIBLE);
				} else {
					findViewById(R.id.view_registration_wifi).setVisibility(View.GONE);
				}
				break;
		}
	}
	
	
	/**
	 * Validates input then sends to server and starts main activity
	 */
	
	public void processRegistration() {
		
		Log.v(TAG, "processRegistration()");

		int selectedTypeOfAuthentication =  ((RadioGroup) findViewById(R.id.typeOfAuthentication)).getCheckedRadioButtonId();

		// Gets a reference to our "selected" radio button
		RadioButton typeOfAuthentication = (RadioButton) findViewById(selectedTypeOfAuthentication);
		SharedPreferences settings = getSharedPreferences(GCMIntentService.REG_ID, 0);
		String deviceToken = settings.getString("RegId", "");

		System.out.println("sumbit");
		
		// To check GCM token received or not
		if (deviceToken.trim().length() > 0) {

			final JSONObject postData = new JSONObject();
			try {
				// Prepare registration information in JSON format to the web service
				postData.put("locuId", locuId.getText().toString());
				postData.put("deviceToken", deviceToken);
				postData.put("wifiName", wifiName.getText().toString());
				postData.put("wifiPassword", wifiPassword.getText().toString());
				postData.put("typeOfAuthentication",
						typeOfAuthentication == null ? ""
								: typeOfAuthentication.getText().toString());
				postData.put("paypalId", paypal.getText().toString());
				postData.put("deviceType", "0");
				postData.put("cancelOrderTime",orderTimeOut.getText().toString());

				if (((CheckBox) findViewById(R.id.view_registration_wifi_checkbox)).isChecked())
					postData.put("wifiPresent", "1");
				else
					postData.put("wifiPresent", "0");

			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			// Start progress dialog from here
			progressDialog = Utilities.progressDialog(this, "Loading..");
			progressDialog.show();
			
			// Call web service in the background
			new Thread() {
				@Override
				public void run() {
					try {
						// Post venue details to the server
						final String response = WebServices.postRequest(
								Constants.URL_SAVE_VENUEDETAILS, postData,
								VenueProfileActivity.this);

						Log.d("Bartsy", "response :: " + response);
						
						// To check response received from the server or not - Error Handling
						if (response != null) {
							// Handler to access UI thread
							handler.post(new Runnable() {

								@Override
								public void run() {
									try {
										processVenueResponse(new JSONObject(response));
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							});
						}

					} catch (Exception e) {
						Log.d("Venue Reg", "Exception :: " + e);
					}

				}
			}.start();
		} 
		// To stop sending details to server if the GCM device token is failed
		else {
			WebServices.alertbox("Please try again....",
					VenueProfileActivity.this);
		}
	}
	/**
	 * To parse venue registration response in JSON format
	 * 
	 * @param json
	 */
	private void processVenueResponse(JSONObject json) {
		try {
			int errorCode = Integer.parseInt(json
					.getString("errorCode"));
			String errorMessage = json
					.getString("errorMessage");
			String venueName = null, venueId = null;
			Toast.makeText(getApplicationContext(),
					errorMessage, Toast.LENGTH_LONG)
					.show();
			BartsyApplication app;
			switch (errorCode) {
			case 1:
				// venue already exists - still save
				// the
				// profile locally for now
				// venueName = "Chaya Venice";
				// venueId = "5a0999dda39f9fe07a44";
			case 0:
				// Save the venue id in shared preferences
				venueId = venueId == null ? json.getString("venueId") : venueId;
				venueName = venueName == null ? json.getString("venueName") : venueName;
				// To save venue details in the shared preference
				SharedPreferences sharedPref = getSharedPreferences(getResources().getString(R.string.config_shared_preferences_name), Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString("RegisteredVenueId", venueId);
				editor.putString("RegisteredVenueName", venueName);
				app = (BartsyApplication) getApplication();
				app.venueProfileID = venueId;
				app.venueProfileName = venueName;
				
				editor.commit();
				
				// Remove progress dialog
				if(progressDialog!=null){
					progressDialog.dismiss();
				}
				
				// To navigate main page and try to close this screen
				Intent intent = new Intent(
						VenueProfileActivity.this,
						MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
				
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
}
