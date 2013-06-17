package com.vendsy.bartsy.venue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.vendsy.bartsy.venue.model.Order;
import com.vendsy.bartsy.venue.utils.WebServices;

/**
 * 
 * @author Seenu Malireddy
 *
 */

public class PastOrdersActivity extends Activity {
	private BartsyApplication mApp;
	private TableLayout ordersTableLayout;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pastorders);
		
		// Setup application pointer
		mApp = (BartsyApplication) getApplication();
		ordersTableLayout = (TableLayout) findViewById(R.id.pastordersLayout);
		new PastOrders().execute("params");
	}
	
	/**
	 * 
	 * Past orders sys call using async task instead of thread
	 */
	private class PastOrders extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			
			return WebServices.getPastOrders(PastOrdersActivity.this, mApp.venueProfileID);
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
				LayoutInflater inflater = getLayoutInflater();
				final View itemView1 = inflater.inflate(
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
		
		LayoutInflater inflater = getLayoutInflater();
		final View itemView = inflater.inflate(R.layout.pastorderrow, null);
		// Get all text views from the view
		TextView orderId = (TextView) itemView.findViewById(R.id.orderId);
		orderId.setText(order.serverID);
		
		TextView itemName = (TextView) itemView.findViewById(R.id.itemName);
		itemName.setText(order.title);
		
		TextView orderStatus = (TextView) itemView.findViewById(R.id.orderStatus);
		orderStatus.setText(String.valueOf(order.status));
		
		TextView dateCreated = (TextView) itemView.findViewById(R.id.dateCreated);
		dateCreated.setText(order.createdDate);
		
		TextView basePrice = (TextView) itemView.findViewById(R.id.basePrice);
		basePrice.setText(String.valueOf(order.totalAmount));
		
		TextView description = (TextView) itemView.findViewById(R.id.description);
		description.setText(order.description);

		ordersTableLayout.addView(itemView);
	}
	
}
