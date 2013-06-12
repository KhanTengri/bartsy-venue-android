package com.vendsy.bartsy.venue.utils;

public class Constants {

//	public static final String DOMAIN_NAME = "http://192.168.0.109:8080/";

	public static final String DOMAIN_NAME = "http://54.235.76.180:8080/";
	public static final String PROJECT_NAME = "Bartsy/";

	public static final boolean USE_ALLJOYN = false;

	// For getting the bars list from server
	public static final String URL_GET_BAR_LIST = DOMAIN_NAME + PROJECT_NAME
			+ "venue/getMenu";
	// For posting the Profiles Data to server
	public static final String URL_POST_PROFILE_DATA = DOMAIN_NAME
			+ PROJECT_NAME + "user/saveUserProfile";
	// For place the order
	public static final String URL_PLACE_ORDER = DOMAIN_NAME + PROJECT_NAME
			+ "order/placeOrder";
	// For getting the venu list from server
	public static final String URL_GET_VENU_LIST = DOMAIN_NAME + PROJECT_NAME
			+ "venue/getVenueList";
	// For User Check In
	public static final String URL_USER_CHECK_IN = DOMAIN_NAME + PROJECT_NAME
			+ "user/userCheckIn";

	// For User Check Out
	public static final String URL_USER_CHECK_OUT = DOMAIN_NAME + PROJECT_NAME
			+ "user/userCheckOut";

	// For saveVenueDetails for bartender
	public static final String URL_SAVE_VENUEDETAILS = DOMAIN_NAME
			+ PROJECT_NAME + "venue/saveVenueDetails";
	

	// For Order status for bartender
	public static final String URL_UPDATE_ORDER_STATUS = DOMAIN_NAME
			+ PROJECT_NAME + "order/updateOrderStatus";

	// For data sync with server for bartender
	public static final String URL_SYNC_WITH_SERVER = DOMAIN_NAME
			+ PROJECT_NAME + "data/syncBartenderApp";
	
	// For heartBeatVenue
	public static final String URL_HEART_BEAT_VENUE = DOMAIN_NAME +PROJECT_NAME
			+ "venue/heartBeatVenue";
	
	// To save ingredients
	public static final String URL_SAVE_INGREDIENTS = DOMAIN_NAME +PROJECT_NAME
			+ "inventory/saveIngredients";
	
	// To save ingredients
	public static final String URL_DELETE_INGREDIENTS = DOMAIN_NAME +PROJECT_NAME
				+ "inventory/deleteIngredient";
	
	// To save ingredients
	public static final String URL_SAVE_COCKTAILS = DOMAIN_NAME +PROJECT_NAME
				+ "inventory/saveCocktails";

	// For setVenueStatus
	public static final String URL_SET_VENUE_STATUS = DOMAIN_NAME
			+ PROJECT_NAME + "venue/setVenueStatus";

	// Android Device Type
	public static final int DEVICE_Type = 0;
	// This is the url for download the facebook picture
	public static final String FB_PICTURE = "https://graph.facebook.com/";
	// CSV file for spirits and mixers
	public static final String INGREDIENTS_CSV_FILE = "Ingredients.csv";
	// CSV file for spirits and mixers
	public static final String COCKTAILS_CSV_FILE = "Cocktails.csv";
	// Current ApiVersion number
	public static final String 	API_VERSION="1";
}
