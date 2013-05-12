package com.vendsy.bartsy.venue.utils;

public class Constants {
	//public static final String DOMAIN_NAME = "http://192.168.0.109:8080";

	 public static final String DOMAIN_NAME = "http://54.235.76.180:8080";

	public static final boolean USE_ALLJOYN = false;

	// For getting the bars list from server
	public static final String URL_GET_BAR_LIST = DOMAIN_NAME
			+ "/Bartsy/venue/getMenu";
	// For posting the Profiles Data to server
	public static final String URL_POST_PROFILE_DATA = DOMAIN_NAME
			+ "/Bartsy/user/saveUserProfile";
	// For place the order
	public static final String URL_PLACE_ORDER = DOMAIN_NAME
			+ "/Bartsy/order/placeOrder";
	// For getting the venu list from server
	public static final String URL_GET_VENU_LIST = DOMAIN_NAME
			+ "/Bartsy/venue/getVenueList";
	// For User Check In
	public static final String URL_USER_CHECK_IN = DOMAIN_NAME
			+ "/Bartsy/user/userCheckIn";

	// For User Check Out
	public static final String URL_USER_CHECK_OUT = DOMAIN_NAME
			+ "/Bartsy/user/userCheckOut";

	// For saveVenueDetails for bartender
	public static final String URL_SAVE_VENUEDETAILS = DOMAIN_NAME
			+ "/Bartsy/venue/saveVenueDetails";
	
	// For Order status for bartender
	public static final String URL_UPDATE_ORDER_STATUS = DOMAIN_NAME
			+ "/Bartsy/order/updateOrderStatus";
	
	
	// Android Device Type
	public static final int DEVICE_Type = 0;
	// This is the url for download the facebook picture
	public static final String FB_PICTURE="https://graph.facebook.com/";
}
