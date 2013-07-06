package com.vendsy.bartsy.venue.utils;

public class Constants {

	public static final boolean USE_ALLJOYN = false;

	// how many more minutes to delay a local timeout from the server timeout
	public static final int timoutDelay = 2; 
	
	// frequency in which to run the background service, in ms
	public static final long monitorFrequency = 60000 ; 
	
	// This is the url for download the facebook picture
	public static final String FB_PICTURE = "https://graph.facebook.com/";

	// Android Device Type
	public static final int DEVICE_Type = 0;

	// CSV file for spirits and mixers
	public static final String INGREDIENTS_CSV_FILE = "Ingredients.csv";

	// CSV file for spirits and mixers
	public static final String COCKTAILS_CSV_FILE = "Cocktails.csv";
}
