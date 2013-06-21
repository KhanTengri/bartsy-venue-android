package com.vendsy.bartsy.venue.model;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vendsy.bartsy.venue.R;
import com.vendsy.bartsy.venue.utils.WebServices;

public class Order  {

	static final String TAG = "Order";
	
	// Each order has an ID that is unique within a session number
	public String serverID; 
	
	// Title and description are arbitrary strings
	public String title, description;
	public String itemId;
	
	// The total price is in the local denomination and is the sum of price * quantity, fee and tax
	public int quantity = 1;
	public int image_resource;
	
	// Fees: total = base + tax + fee + tip
	public float baseAmount;
	public float feeAmount;
	public float taxAmount;
	public float tipAmount;
	public float totalAmount;
	
	public String updatedDate;
	public String createdDate;
	
	public String profileId;
	
	// Each order contains the sender and the recipient (another single in the bar or a friend to pick the order up)
	public Profile orderSender;
	public Profile orderReceiver;
	
	// The view displaying this order or null. The view is the display of the order in a list. 
	// The list could be either on the client or the server and it looks different in both cases
	// but the code manages the differences. 
	public View view = null;
	
	// Order states
	// (received) -> NEW -> (accepted) -> IN_PROGRESS -> (completed) -> READY   -> (picked_up) -> COMPLETE    -> (timed out, error, etc) -> CANCELLED
	//                      (rejected) -> REJECTED       (failed)    -> FAILED     (forgotten) -> INCOMPLETE  
	
	public static final int ORDER_STATUS_NEW			= 0;
    public static final int ORDER_STATUS_REJECTED    	= 1;
    public static final int ORDER_STATUS_IN_PROGRESS  	= 2;
    public static final int ORDER_STATUS_READY 			= 3;
	public static final int ORDER_STATUS_FAILED	 		= 4;
	public static final int ORDER_STATUS_COMPLETE	 	= 5;
	public static final int ORDER_STATUS_INCOMPLETE	 	= 6;
	public static final int ORDER_STATUS_CANCELLED	 	= 7;
	public static final int ORDER_STATUS_COUNT			= 8;
	
	// The states are implemented in a status variable and each state transition has an associated time
	public int status;	
	private String errorReason = ""; // used to send an error reason for negative order states
    public Date[] state_transitions = new Date[ORDER_STATUS_COUNT];
    // Decimal Format
    //public  DecimalFormat df = new DecimalFormat("#.##");

    DecimalFormat df = new DecimalFormat();
    
    
	/**
	 *  Default constructor
	 */
    
	public Order() {
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);
	}

	
	/** 
	 * When an order is initialized the state transition times are undefined except for the 
	 * first state, which is when the order is received
	 */
	
	public void initialize (String server_id, String title, String description, 
			String price, String image_resource, Profile order_sender) {
		this.serverID = server_id;
		this.title = title;
		this.description = description;
//		this.price = Float.parseFloat(price);
//		this.image_resource = Integer.parseInt(image_resource); 
		this.orderSender = order_sender;

		// Orders starts in the "NEW" status
		status = ORDER_STATUS_NEW;
		state_transitions[status] = new Date();
	}
	
	
	/**
	 * Constructor to parse all the information from the JSON
	 * 
	 * @param json
	 */
	
	public Order(JSONObject json) {
		
		try {
			status = Integer.valueOf(json.getString("orderStatus"));
			state_transitions[status] = new Date();
			title = json.getString("itemName");
			updatedDate = json.getString("orderTime");
			serverID = json.getString("orderId");

			
			baseAmount = Float.valueOf(json.getString("basePrice"));
			tipAmount = Float.valueOf(json.getString("tipPercentage"));
			totalAmount = Float.valueOf(json.getString("totalPrice"));
			taxAmount = totalAmount - tipAmount - baseAmount;
			
			profileId = json.getString("bartsyId");
			description = json.getString("description");
			if (json.has("dateCreated"))
			  createdDate = json.getString("dateCreated");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Set our format 
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);
		
	}
	
	
	/**
	 * To process next positive state for the order
	 */
	
	public void nextPositiveState() {
		switch (status) {
		case ORDER_STATUS_NEW:
			status = ORDER_STATUS_IN_PROGRESS;
			break;
		case ORDER_STATUS_IN_PROGRESS:
			status = ORDER_STATUS_READY;
			break;
		case ORDER_STATUS_READY:
			status = ORDER_STATUS_COMPLETE;
			break;
		default:
			return;
		}
		
		// Mark the time of the state transition in the timetable
		state_transitions[status] = new Date();
	}

	
	/**
	 * To process next negative state for the order
	 */
	
	public void nextNegativeState(String errorReason) {
		
		int oldStatus = status;
		
		switch (status) {
		case ORDER_STATUS_NEW:
			status = ORDER_STATUS_REJECTED;
			break;
		case ORDER_STATUS_IN_PROGRESS:
			status = ORDER_STATUS_FAILED;
			break;
		case ORDER_STATUS_READY:
			status = ORDER_STATUS_INCOMPLETE;
			break;
		}
		
		// Log the state change and update the order with an error reason
		Log.i(TAG, "Order " + serverID + " changed status from " + oldStatus + " to " + status + " for reason: "  + errorReason);
		this.errorReason = errorReason;
		
		// Mark the time of the state transition in the timetable
		state_transitions[status] = new Date();
	}
	 
	
	/**
	 * It will returns JSON format to update order status
	 */
	public JSONObject statusChangedJSON(){
		final JSONObject orderData = new JSONObject();
		try {
			orderData.put("orderId", serverID);
			orderData.put("orderStatus", status);
			orderData.put("orderRejectionReason", errorReason);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return orderData;
	}
	/**
	 * To update timeout value from the local timer
	 * 
	 * @param timeOut
	 */
	public void updateTimeOut(int timeOut){
		if (view == null) return;
		
		// Calculate duration of timeout value and duration of order updated time
		long duration  = timeOut - ((System.currentTimeMillis() - (state_transitions[status]).getTime()))/60000;
		// If the order already expired - duration should not be negative value
		if(duration<0){
			((TextView) view.findViewById(R.id.orderOutTime)).setText("Expired");
		}
		// To update duration should not be negative value
		else{
			((TextView) view.findViewById(R.id.orderOutTime)).setText(String.valueOf(duration)+" min left");
		}
	}
	
	
	/**
	 * Updates the order view. Notice the view holds a pointer to the object being displayed through the "tag" field
	 */
	
	public void updateView () {

		if (view == null) return;

		((TextView) view.findViewById(R.id.view_order_title)).setText(title);
		((TextView) view.findViewById(R.id.view_order_description)).setText(description);
		((TextView) view.findViewById(R.id.view_order_time)).setText(DateFormat.getTimeInstance().format(state_transitions[status]));
		((TextView) view.findViewById(R.id.view_order_date)).setText(DateFormat.getDateInstance().format(state_transitions[status]));	

		// Set base price
		((TextView) view.findViewById(R.id.view_order_price)).setText(df.format(baseAmount));
		
		// Set the totals (we'll update again if we have more mini orders...)
		((TextView) view.findViewById(R.id.view_order_tip_amount)).setText(df.format(tipAmount));
		((TextView) view.findViewById(R.id.view_order_tax_amount)).setText(df.format(taxAmount));
		((TextView) view.findViewById(R.id.view_order_total_amount)).setText(df.format(totalAmount)); 
		
		
		if (orderSender != null ) {

			// Update sender profile section
			
			ImageView profileImageView = ((ImageView)view.findViewById(R.id.view_order_profile_picture));
			if (orderSender.image==null) {
				// Download image from the profile URL
				WebServices.downloadImage(orderSender.getProfileImageUrl(), orderSender, profileImageView);
			} else {
				// Set the saved image to the imageView
				profileImageView.setImageBitmap(orderSender.image);
			}
			
			
			((TextView) view.findViewById(R.id.view_order_profile_name)).setText(orderSender.getName());
		}

		// Update buttons and background
		
		String positive="", negative="";
		switch (status) {
		case ORDER_STATUS_NEW:
			positive = "ACCEPT";
			negative = "REJECT";
			view.findViewById(R.id.view_order_header).setBackgroundResource(R.drawable.rounded_corner_red);
			break;
		case ORDER_STATUS_IN_PROGRESS:
			positive = "COMPLETED";
			negative = "FAILED";
			view.findViewById(R.id.view_order_header).setBackgroundResource(R.drawable.rounded_corner_orange);
			break;
		case ORDER_STATUS_READY:
			positive = "PICKED UP";
			negative = "NO SHOW";
			view.findViewById(R.id.view_order_header).setBackgroundResource(R.drawable.rounded_corner_green);
			break;
		}
		((TextView) view.findViewById(R.id.view_order_number)).setText(serverID);
		((Button) view.findViewById(R.id.view_order_button_positive)).setText(positive);
		((Button) view.findViewById(R.id.view_order_button_positive)).setTag(this);
		((Button) view.findViewById(R.id.view_order_button_negative)).setText(negative);
		((Button) view.findViewById(R.id.view_order_button_negative)).setTag(this);
		((Button) view.findViewById(R.id.view_order_button_remove)).setTag(this);
		
		// Set a pointer to the object being displayed 
		view.setTag(this);
		
	}
	
	
	public View getMiniView(LayoutInflater inflater, ViewGroup container ) {
		
		LinearLayout view = (LinearLayout) inflater.inflate(R.layout.bartender_order_mini, container, false);
		
		((TextView) view.findViewById(R.id.view_order_title)).setText(title);
		((TextView) view.findViewById(R.id.view_order_description)).setText(description);
		((TextView) view.findViewById(R.id.view_order_mini_base_amount)).setText(df.format(baseAmount));

		// Set a pointer to hte object being displayed
		view.findViewById(R.id.view_order_button_remove).setTag(this);

		return view;
	}

}
