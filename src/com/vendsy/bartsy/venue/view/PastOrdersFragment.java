package com.vendsy.bartsy.venue.view;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.vendsy.bartsy.venue.BartsyApplication;
import com.vendsy.bartsy.venue.R;
import com.vendsy.bartsy.venue.model.Item;
import com.vendsy.bartsy.venue.model.Order;
import com.vendsy.bartsy.venue.utils.Utilities;
import com.vendsy.bartsy.venue.utils.WebServices;

/**
 * 
 * @author Seenu Malireddy
 *
 */

public class PastOrdersFragment extends Fragment{
	private BartsyApplication mApp;
	private TableLayout ordersTableLayout;
	private LayoutInflater mInflater;
	private ViewGroup mContainer;
	private View mRootView;
    DecimalFormat df = new DecimalFormat();
	private double totalAmount = 0;
	private double tipAmount = 0;
	
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		Log.v("Bartsy", "OrdersSectionFragment.onCreateView()");
		
		mInflater = inflater;
		mContainer = container;
		mRootView = mInflater.inflate(R.layout.pastorders, mContainer, false);
		
		// Setup application pointer
		mApp = (BartsyApplication) getActivity().getApplication();
		ordersTableLayout = (TableLayout) mRootView.findViewById(R.id.pastordersLayout);
		
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);

		
		new PastOrders().execute("params");
		
		return mRootView;
	}
	
	
	/**
	 * 
	 * Past orders sys call using async task instead of thread
	 */
	private class PastOrders extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			
			return WebServices.getPastOrders(mApp, mApp.venueProfileID);
		}

		@Override
		protected void onPostExecute(String result) {
			
			if (result != null) {
				updateOrdersView(result);
			}

			((TextView) mRootView.findViewById(R.id.past_tip_total)).setText("$ " + df.format(tipAmount));
			((TextView) mRootView.findViewById(R.id.past_total_amount)).setText("$ " + df.format(totalAmount));

		}

		@Override
		protected void onPreExecute() {

		}

	}
	/**
	 * To update the orders view
	 */
	private void updateOrdersView(String response) {
		
		// Count the tip and total amounts of all orders
		tipAmount = 0;
		totalAmount = 0;	

		// Make sure that layout should be empty to add new views
		if (ordersTableLayout.getChildCount() > 0)
			ordersTableLayout.removeAllViews();
		
		try {
			JSONObject object = new JSONObject(response);
			JSONArray array = object.getJSONArray("pastOrders");
			if (array != null) {

				for (int i = 0; i < array.length(); i++) {
					JSONObject json = null;
					json = array.getJSONObject(i);
					Order order = new Order(json);
					try {
						
						// try to add the view
						ordersTableLayout.addView(order.pastOrdersView(mInflater));

						// Add tip and total
						tipAmount += order.tipAmount;
						totalAmount += order.totalAmount;

					} catch (Exception e) {
						// Simply skip this entry in case of error
						e.printStackTrace();
					}
				}
			}
		} catch (JSONException e) {
		}
	}
	
}
