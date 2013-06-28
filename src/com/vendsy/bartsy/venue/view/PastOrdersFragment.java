package com.vendsy.bartsy.venue.view;

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
			
			return WebServices.getPastOrders(getActivity(), mApp.venueProfileID);
		}

		@Override
		protected void onPostExecute(String result) {

			if (result != null) {
				updateOrdersView(result);
			}

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
				final View itemView1 = mInflater.inflate(
						R.layout.pastorderrow, null);
	
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
	/**
	 * To add new order to the table view
	 * 
	 * @param order
	 */
	private void addNewOrderRow(Order order) {
		
		LayoutInflater inflater = mInflater;
		final View itemView = inflater.inflate(R.layout.pastorderrow, null);
		// Get all text views from the view
		TextView orderId = (TextView) itemView.findViewById(R.id.orderId);
		orderId.setText(order.serverID);
		
		TextView itemName = (TextView) itemView.findViewById(R.id.itemName);
		itemName.setText(order.title);
		
		String status = "?";
		switch(order.status) {
		case Order.ORDER_STATUS_CANCELLED:
			status = "Failed";
			break;
		case Order.ORDER_STATUS_COMPLETE:
			status = "OK";
			break;
		case Order.ORDER_STATUS_READY:
			status = "Open";
			break;
		case Order.ORDER_STATUS_FAILED:
			status = "Failed";
			break;
		case Order.ORDER_STATUS_IN_PROGRESS:
			status = "Open";
			break;
		case Order.ORDER_STATUS_INCOMPLETE:
			status = "Failed";
			break;
		case Order.ORDER_STATUS_NEW:
			status = "Open";
			break;
		case Order.ORDER_STATUS_REJECTED:
			status = "Failed";
			break;
		}
		
		TextView orderStatus = (TextView) itemView.findViewById(R.id.orderStatus);
		orderStatus.setText(status);
		
		TextView dateCreated = (TextView) itemView.findViewById(R.id.dateCreated);
		dateCreated.setText(order.createdDate);
		
		TextView basePrice = (TextView) itemView.findViewById(R.id.basePrice);
		basePrice.setText(String.valueOf(order.totalAmount));
		
		TextView description = (TextView) itemView.findViewById(R.id.description);
		description.setText(order.description);

		ordersTableLayout.addView(itemView);
	}
	
}
