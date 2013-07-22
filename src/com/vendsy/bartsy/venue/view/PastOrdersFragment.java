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
        Date date = Utilities.getLocalDateFromGMTString(order.createdDate.replace("T", " ").replace("Z", ""), "yyyy-MM-dd HH:mm:ss");;
        String time = new SimpleDateFormat("MM/dd/yy HH:mm", Locale.getDefault()).format(date);
		
        // Don't display order placed before beta starts
        SimpleDateFormat betaDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date betaDate;
        try {
			betaDate = betaDateFormat.parse("2013-07-15 10:15:00");
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
		((TextView) itemView.findViewById(R.id.orderId)).setText(order.orderId);
		
		String statusString = "?";
		
		int status = order.status == Order.ORDER_STATUS_REMOVED ? order.last_status : order.status;
		
		switch(status) {
		case Order.ORDER_STATUS_CANCELLED:
			statusString = "Timeout";
			break;
		case Order.ORDER_STATUS_COMPLETE:
			statusString = "Completed";
			break;
		case Order.ORDER_STATUS_READY:
			statusString = "Ready";
			break;
		case Order.ORDER_STATUS_FAILED:
			statusString = "Failed";
			break;
		case Order.ORDER_STATUS_IN_PROGRESS:
			statusString = "In progress";
			break;
		case Order.ORDER_STATUS_INCOMPLETE:
			statusString = "Unfinished";
			break;
		case Order.ORDER_STATUS_NEW:
			statusString = "New";
			break;
		case Order.ORDER_STATUS_REJECTED:
			statusString = "Rejected";
			break;
		}
		
		((TextView) itemView.findViewById(R.id.orderStatus)).setText(String.valueOf(statusString));

		// Set title
		String title = "";
		int size = order.items.size();
		for (int i=0; i< size ; i++) {
			Item item = order.items.get(i);
		    DecimalFormat df = new DecimalFormat();
			df.setMaximumFractionDigits(0);
			df.setMinimumFractionDigits(0);

			title += item.getTitle() + "  ($"+ df.format(Float.parseFloat(item.getPrice())) + ")";
			if (i != size && size > 1)
				title +="\n";
		}
		((TextView) itemView.findViewById(R.id.itemName)).setText(title);
		
		// Set up recipient
		((TextView) itemView.findViewById(R.id.recipient)).setText(order.recipientNickname);
		
		// Totals
		if (status == Order.ORDER_STATUS_COMPLETE) {
		 
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
