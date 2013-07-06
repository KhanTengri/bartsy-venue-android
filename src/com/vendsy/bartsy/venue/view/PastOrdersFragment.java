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
import com.vendsy.bartsy.venue.model.Order;
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

			tipAmount = 0;
			totalAmount = 0;	
			
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
		
		// Make sure that layout should be empty to add new views
		if (ordersTableLayout.getChildCount() > 0)
			ordersTableLayout.removeAllViews();
		
		try {
			JSONObject object = new JSONObject(response);
			JSONArray array = object.getJSONArray("pastOrders");
			if (array != null) {

				// To add Table headers
				final View itemView1 = mInflater.inflate(R.layout.orders_past_row, null);
				ordersTableLayout.addView(itemView1);
				
				for (int i = 0; i < array.length(); i++) {
					JSONObject json = null;
					json = array.getJSONObject(i);
					Order order = new Order(json);
					addNewOrderRow(order);
				}
			}
		} catch (JSONException e) {
		}
	}
	
	private double totalAmount = 0;
	private double tipAmount = 0;
	
	/**
	 * To add new order to the table view
	 * 
	 * @param order
	 */
	private void addNewOrderRow(Order order) {
		
		
		// Extract time from UTC field
		String inputText = order.createdDate.replace("T", " ").replace("Z", ""); // example: 2013-06-27 10:20:15
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        inputFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        String time = "";
        try {
			date = inputFormat.parse(inputText);
			time = outputFormat.format(date);
		} catch (ParseException e) {
			// Bad date format - leave time blank
			e.printStackTrace();
			Log.e("PastOrdersFragment", "Bad date format in getPastOrders syscall");
			return;
		} 
		
        // Don't display order placed before beta starts
        SimpleDateFormat betaDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date betaDate;
        try {
			betaDate = betaDateFormat.parse("2013-06-30 20:15:00");
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
        if (date.before(betaDate)) {
        	// Don't show rows before the beta start date
        	return;
        }
        
		final View itemView = mInflater.inflate(R.layout.orders_past_row, null);


		((TextView) itemView.findViewById(R.id.dateCreated)).setText(time);
		((TextView) itemView.findViewById(R.id.orderId)).setText(order.serverID);
		
		String status = "?";
		switch(order.last_status) {
		case Order.ORDER_STATUS_CANCELLED:
			status = "Timeout";
			break;
		case Order.ORDER_STATUS_COMPLETE:
			status = "OK";
			break;
		case Order.ORDER_STATUS_READY:
			status = "Ready";
			break;
		case Order.ORDER_STATUS_FAILED:
			status = "Failed";
			break;
		case Order.ORDER_STATUS_IN_PROGRESS:
			status = "In progress";
			break;
		case Order.ORDER_STATUS_INCOMPLETE:
			status = "Unfinished";
			break;
		case Order.ORDER_STATUS_NEW:
			status = "New";
			break;
		case Order.ORDER_STATUS_REJECTED:
			status = "Rejected";
			break;
		}
		
		((TextView) itemView.findViewById(R.id.orderStatus)).setText(String.valueOf(status));
		((TextView) itemView.findViewById(R.id.itemName)).setText(order.title);
		
		
		// Totals
		
		if (order.status == Order.ORDER_STATUS_COMPLETE) {
		
			totalAmount += order.totalAmount;
			tipAmount += order.tipAmount;
			
			((TextView) itemView.findViewById(R.id.tipAmount)).setText("$ " + df.format(order.tipAmount));
			((TextView) itemView.findViewById(R.id.totalPrice)).setText("$ " + df.format(order.totalAmount));
		} else {
			((TextView) itemView.findViewById(R.id.tipAmount)).setText("-");
			((TextView) itemView.findViewById(R.id.totalPrice)).setText("-");
		}

		ordersTableLayout.addView(itemView);
	}
	
}
