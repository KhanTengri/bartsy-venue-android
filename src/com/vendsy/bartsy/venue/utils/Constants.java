package com.vendsy.bartsy.venue.utils;

public class Constants {

//	public static final String DOMAIN_NAME = "http://192.168.0.109:8080/";

	public static final String DOMAIN_NAME = "http://54.235.76.180:8080/";
	public static final String PROJECT_NAME = "Bartsy/";

	public static final boolean USE_ALLJOYN = false;

	// For getting the bars list from server
	public static final String URL_GET_BAR_LIST = DOMAIN_NAME + PROJECT_NAME+ "venue/getMenu";
	// For posting the Profiles Data to server
	public static final String URL_POST_PROFILE_DATA = DOMAIN_NAME +PROJECT_NAME
			+ "user/saveUserProfile";
	// For place the order
	public static final String URL_PLACE_ORDER = DOMAIN_NAME +PROJECT_NAME
			+ "order/placeOrder";
	// For getting the venu list from server
	public static final String URL_GET_VENU_LIST = DOMAIN_NAME +PROJECT_NAME
			+ "venue/getVenueList";
	// For User Check In
	public static final String URL_USER_CHECK_IN = DOMAIN_NAME +PROJECT_NAME
			+ "user/userCheckIn";

	// For User Check Out
	public static final String URL_USER_CHECK_OUT = DOMAIN_NAME +PROJECT_NAME
			+ "user/userCheckOut";

	// For saveVenueDetails for bartender
	public static final String URL_SAVE_VENUEDETAILS = DOMAIN_NAME +PROJECT_NAME
			+ "venue/saveVenueDetails";

	// For Order status for bartender
	public static final String URL_UPDATE_ORDER_STATUS = DOMAIN_NAME +PROJECT_NAME
			+ "order/updateOrderStatus";

	// For data sync with server for bartender
	public static final String URL_SYNC_WITH_SERVER = DOMAIN_NAME +PROJECT_NAME
				+ "data/syncBartenderApp";


	// Android Device Type
	public static final int DEVICE_Type = 0;
	// This is the url for download the facebook picture
	public static final String FB_PICTURE = "https://graph.facebook.com/";
}
