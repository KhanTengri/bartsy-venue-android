/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vendsy.bartsy.venue.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.format.DateUtils;
import android.util.Log;

import com.google.android.gms.internal.cp;
import com.vendsy.bartsy.venue.R;
import com.vendsy.bartsy.venue.db.DatabaseManager;
import com.vendsy.bartsy.venue.model.Category;
import com.vendsy.bartsy.venue.model.Cocktail;
import com.vendsy.bartsy.venue.model.Ingredient;
import com.vendsy.bartsy.venue.model.Menu;

/**
 * Helper class providing methods and constants common to other classes in the
 * app.
 */
public final class Utilities {

	/**
	 * Base URL of the Demo Server (such as http://my_host:8080/gcm-demo)
	 */
	static final String SERVER_URL = "";

	/**
	 * Tag used on log messages.
	 */

	static final String TAG = "GCMDemo";

	/**
	 * Intent used to display a message in the screen.
	 */
	public static final String DISPLAY_MESSAGE_ACTION = "com.vendsy.bartsy.venue.DISPLAY_MESSAGE";

	/**
	 * Intent's extra that contains the message to be displayed.
	 */
	public static final String EXTRA_MESSAGE = "message";
	
	public static final String DEFAULT_MENU_NAME="Cocktails";

	/**
	 * Notifies UI to display a message.
	 * <p>
	 * This method is defined in the common helper because it's used both by the
	 * UI and the background service.
	 * 
	 * @param context
	 *            application's context.
	 * @param message
	 *            message to be displayed.
	 */
	public static void displayMessage(Context context, String message) {
		Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
		intent.putExtra(EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}
	
	/**
	 * To prepare progress dialog
	 * 
	 * @param context
	 * @return
	 */
	public static ProgressDialog progressDialog(Context context, String message){
		ProgressDialog mProgressDialog = new ProgressDialog(context);
		
		// To configure the loading dialog
        mProgressDialog.setMessage(message);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(true);
        
        return mProgressDialog;
	}
	
	/**
	 * To read ingredients data from CSV file and save in the db
	 * 
	 * @param context
	 */
	public static void saveIngredientsFromCSVFile(Context context, InputStream is){
		String data[] = null;
        // Try to read ingredients data from CSV file 
        try {
        	// Initialize the CSVReader object with CSV file by using InputStream
            CSVReader reader = new CSVReader(new InputStreamReader(is));
            data = reader.readNext();
            
            ArrayList<Category> categories = new ArrayList<Category>();
            Category category = null;
            
            //Loop until the end of the line
            while(data!=null) {
                data = reader.readNext();
                
                // To ignore empty categories and empty types
                if(data != null && data.length==5 && !data[1].trim().equals("") && !data[2].trim().equals("")) {
                	// To set the properties for Ingredient model
                	Ingredient ingredient = new Ingredient();
                	ingredient.setName(data[0]);
                	try {
						ingredient.setPrice(Integer.parseInt(data[3]));
					} catch (NumberFormatException e) {
					}
                	ingredient.setAvailability((data[4]!=null) && (data[4].equalsIgnoreCase("Yes")));
                	// To avoid duplicate categories in the list
                	category = getExistingCategoryInList(categories, data[2], data[1]);
                	if(category==null){
                		category = new Category();
                		category.setName(data[2]);
                		category.setType(data[1]);
                		// Here, Category is saving in the  DB
                    	DatabaseManager.getInstance().saveSection(category);
                    	
                		categories.add(category);
                	}
                	ingredient.setCategory(category);
                	
                	// Here saving in the DB
                	DatabaseManager.getInstance().saveIngredient(ingredient);
                } 
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * To check category is already exist or not. If it is already exist then it will return the object otherwise it will return null
	 * 
	 * @param list
	 * @param category
	 * @param type
	 * @return
	 */
	public static Category getExistingCategoryInList(ArrayList<Category> list, String category, String type){
		for (Category categoryItem : list) {		
			if(category.equals(categoryItem.getName()) && type.equals(categoryItem.getType())){
				return categoryItem;
			}
		}
		return null;
	}
	
	/**
	 * To read Cocktails data from CSV file and save in the db
	 * 
	 * @param context
	 */
	public static void saveCocktailsFromCSVFile(Context context, InputStream is, String menuName){
		
		// Make sure that menu name should not be empty or null
		if(menuName==null || menuName.trim().equals("")){
			menuName = DEFAULT_MENU_NAME;
		}
		
		String data[] = null;
        // Try to read ingredients data from CSV file 
        try {
        	// Initialize the CSVReader object with CSV file by using InputStream
            CSVReader reader = new CSVReader(new InputStreamReader(is));
            // To skip excel sheet headings
            reader.setSeparator('|');
            data = reader.readNext();
            
            //Loop until the end of the line
            while(data!=null) {
                data = reader.readNext();
                
                // To ignore empty categories and empty types
                if(data != null && data.length>=10 && !data[1].trim().equals("") && !data[3].trim().equals("")) {
                	// To set the properties for Ingredient model
                	Cocktail cocktail = new Cocktail();
                	cocktail.setName(data[0]);
                	cocktail.setCategory(data[1]);
                	// Try to get menu from db
                	Menu menu = DatabaseManager.getInstance().getMenu(menuName);
                	if(menu==null){
                		// Create new menu record in Menu table
                		menu = new Menu();
                		menu.setName(menuName);
                		DatabaseManager.getInstance().saveMenu(menu);
                	}
                	// Set all values
                	cocktail.setAlcohol(data[2]);
                	cocktail.setGlassType(data[3]);
                	cocktail.setIngredients(data[4]);
                	cocktail.setInstructions(data[5]);
                	cocktail.setShopping(data[6]);
                	cocktail.setDescription(data[7]);
                	cocktail.setPrice(Integer.parseInt(data[8]));
                	cocktail.setAvailability("Yes".equals(data[9]));
                	cocktail.setMenu(menu);
                	
                	// Here saving in the DB
                	DatabaseManager.getInstance().saveCocktail(cocktail);
                } 
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	/**
	 * Returns a Date with the GMT string provided as input in the local time zone. The 
	 * @param date
	 * @param format
	 * @return
	 */
	public static Date getLocalDateFromGMTString(String input, String format) {
		
        SimpleDateFormat inputFormat = new SimpleDateFormat(format, Locale.getDefault());
        inputFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm");
        Date output = null;
        String time = "";
        try {
			output = inputFormat.parse(input);
			time = outputFormat.format(output);
		} catch (ParseException e) {
			// Bad date format - leave time blank
			e.printStackTrace();
			Log.e(TAG, "Bad date format in getPastOrders syscall");
			return null;
		}
		return output; 
	}
	
	/**
	 * Returns the date in string in "time ago format"
	 * 
	 * @param input
	 * @param format
	 * @return
	 */
	public static String getFriendlyDate(String input, String format){

		// Parse date using the provided format
		Date date = getLocalDateFromGMTString(input, format);

		// Make sure the date is valid, if not simply return the input string
		if(date==null){
			return input;
		}
		return (String) DateUtils.getRelativeTimeSpanString(date.getTime(), System.currentTimeMillis(),DateUtils.SECOND_IN_MILLIS,DateUtils.FORMAT_ABBREV_RELATIVE);
	}
	
	
	/**
	 * TODO - Prefs
	 * 
	 * Some shortcuts for saving and retrieving string preferences
	 * 
	 */

	public static void savePref(Context context, String key, String value) {

		SharedPreferences sharedPref = context.getSharedPreferences(
				context.getResources().getString(R.string.config_shared_preferences_name), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public static void savePref(Context context, int key, String value) {

		SharedPreferences sharedPref = context.getSharedPreferences(
				context.getResources().getString(R.string.config_shared_preferences_name), Context.MODE_PRIVATE);
		Resources r = context.getResources();
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString(r.getString(key), value);
		editor.commit();
	}
	
	public static void savePref(Context context, int key, int value) {

		SharedPreferences sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.config_shared_preferences_name), Context.MODE_PRIVATE);
		Resources r = context.getResources();
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt(r.getString(key), value);
		editor.commit();
	}
	
	public static String loadPref(Context context, String key, String defaultValue) {
		SharedPreferences sharedPref = context.getSharedPreferences(context.getResources()
				.getString(R.string.config_shared_preferences_name), Context.MODE_PRIVATE);
		return sharedPref.getString(key, defaultValue);
	}
	
	public static String loadPref(Context context, int key, String defaultValue) {
		SharedPreferences sharedPref = context.getSharedPreferences(context.getResources()
				.getString(R.string.config_shared_preferences_name), Context.MODE_PRIVATE);
		Resources r = context.getResources();
		return sharedPref.getString(r.getString(key), defaultValue);
	}
	
	public static int loadPref(Context context, int key, int defaultValue) {
		SharedPreferences sharedPref = context.getSharedPreferences(context.getResources()
				.getString(R.string.config_shared_preferences_name), Context.MODE_PRIVATE);
		Resources r = context.getResources();
		return sharedPref.getInt(r.getString(key), defaultValue);
	}

	public static void removePref(Context context, int key) {
		SharedPreferences sharedPref = context.getSharedPreferences(context.getResources()
				.getString(R.string.config_shared_preferences_name), Context.MODE_PRIVATE);
		Resources r = context.getResources();
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.remove(r.getString(key));
		editor.commit();	
	}
	
	
	
	/**
	 * 
	 * TODO Getters and setters
	 * 
	 */

	public static boolean has(String field) {
		return !(field == null || field.equals(""));
	}
	
	public boolean has(double field) {
		return field != 0;
	}

	public boolean has(boolean field) {
		return field;
	}
	
	public boolean has(Object field) {
		return field != null;
	}
}
