/**
 * 
 */
package com.vendsy.bartsy.venue.view;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.vendsy.bartsy.venue.R;
import com.vendsy.bartsy.venue.BartsyApplication;
import com.vendsy.bartsy.venue.MainActivity;
import com.vendsy.bartsy.venue.adapter.ExpandableListAdapter;
import com.vendsy.bartsy.venue.db.DatabaseManager;
import com.vendsy.bartsy.venue.model.Item;
import com.vendsy.bartsy.venue.model.Section;
import com.vendsy.bartsy.venue.utils.Constants;
import com.vendsy.bartsy.venue.utils.WebServices;

/**
 * @author peterkellis
 * 
 */
public class MenuSectionFragment extends Fragment {
	private View mRootView = null;
	private ExpandableListView mDrinksListView = null;
	public BartsyApplication mApp = null;
	public MainActivity mActivity = null;
	private Handler handler = new Handler();
	private Menu mMenu = null;
	String TAG = "DrinksSectionFragment";

	/*
	 * Menu class used to cache the menu to avoid delays in UI response time. For now we save this in the 
	 * fragment. Also consider saving this as part of the active venue structures in the main application
	 */
	
	private class Menu {
		ArrayList<String> headings;
		ArrayList<ArrayList<Item>> items;
		
		Menu (ArrayList<String> headings, ArrayList<ArrayList<Item>> items) {
			this.headings = headings;
			this.items = items;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Log.v(TAG, "onCreateView()");
		
		mRootView = inflater.inflate(R.layout.drinks_main, container, false);
		mDrinksListView = (ExpandableListView) mRootView.findViewById(R.id.view_drinks_for_me_list);

		
		// Make sure the fragment pointed to by the activity is accurate
		mApp = (BartsyApplication) getActivity().getApplication();
		((MainActivity) getActivity()).mDrinksFragment = this;
		mActivity = (MainActivity) getActivity();

		// Update the view
		updateView();
		
		return mRootView;
	}

	
	
	/************
	 * 
	 * TODO - Menu loader. This function is called as soon as the fragment is created, to expedite the 
	 * loading of the menu so that it's available to the UI as quickly as possible.
	 * 
	 * Two loaders are called from the loadMenu() function depending on what is available in terms of cache 
	 * (memory, DB, or nothing): one loads the menu from the web, the other from the local database. Both 
	 * loaders will automatically display the menu at the end of loading, so there is no need to explicitly 
	 * call updateView()
	 * 
	 */
	
	/*
	 * Main menu loader. Decides which sub-loader to use to load the menu either from the DB (if it exists)
	 * or via a call to the web service
	 * 
 	 * Expects mActivity to be pointing to the right context as this function could be called without having 
 	 * initialized this fragment's view. The caller should set mActivity
	 */
	
	boolean mMenuLoading = false;
	
	public void loadMenu() {
		
		Log.v(TAG, "loadMenu()");
		
		// To avoid calling this function multiple times while it's still running, we have an indicator showing
		// if the menu is being loaded. We set this indicator when the function stars and clear it when it's done
		
		if (mMenuLoading) {
			// Another instance of this function is currently running, no need to call it again
			Log.d(TAG, "Another instance of loadMenu() is running. Return.");
			return;
		}
		
		// Indicate start of loading menu
		mMenuLoading = true;
		Log.d(TAG, "Indicate start of menu loading");
		
		// Check if menu has already been cached
		
		if (mMenu == null) {
			Log.v(TAG, "Menu not available in memory");

			// Menu is not already in memory, call the appropriate loader

			new Thread() {

				@Override
				public void run() {

					String apiResponse = null;
			
					// Menu doesn't exist in memory - load it from the server and display it
					Log.v(TAG, "Loading menu from web services...");
					apiResponse = downloadAndDisplayMenu();
					
					if (mMenu == null) {
						// Both loaders failed - abort. 
						Log.d(TAG, "Loaders failed...");
						
						// Mark the end end of menu loading as we failed and we're returning.
						mMenuLoading = false;
						Log.d(TAG, "Indicate end of menu loading");

						return;
					}


					// Loading of the menu successful. Display it using a handler because Android 
					// doesn't allow manipulating views from separate threads
					handler.post(new Runnable() {
						// Use a handler because Android doesn't allow manipulating views from separate threads
						@Override public void run() {
							updateView();
						}
					});				
					
					// Mark the end of menu loading
					mMenuLoading = false;
					Log.d(TAG, "Indicate end of menu loading");
				}
			}.start();
						
		} else {
			// Menu already in memory. Nothing to load so just display it.
			Log.v(TAG, "Menu available in memory - displaying it...");
			updateView();
			
			// Mark the end of menu loading
			mMenuLoading = false;
			Log.d(TAG, "Indicate end of menu loading");			
		}
	}
	
	
	/******
	 * 
	 * TODO - Web service functions for menu loader
	 * 
	 * @return
	 */
	
	
	/*
	 * Web service loader. Downloads the menu from the server using a web service call. 
	 * This is called from a background thread.
	 */
	private String downloadAndDisplayMenu() {

		Log.v(TAG, "downloadAndDisplayMenu()");

		// Step 1 - get the web service response and display the results in the view
		String response = WebServices.getMenuList(mApp, mApp.venueProfileID);
		if (response == null) {
			Log.d(TAG, "Webservice get menu call failed");
			return null;
		} else {
			Log.v(TAG, "Webservice menu response: " + response == null? "null" : response);
		}
		
		// parse the response into a menu in-memory structure
		mMenu = extractMenuFromResponse (response);
		
		return response;
	}


	
	/*
	 * Helper function for downloadAndDisplayMenu. Processes the server response and builds a menu object
	 */
	
	private Menu extractMenuFromResponse (String response) {
		
		ArrayList<String> headings = new ArrayList<String>();
		ArrayList<ArrayList<Item>> items = new ArrayList<ArrayList<Item>>();
	
		try {
		
			JSONObject result = new JSONObject(response);
			String errorCode = result.getString("errorCode");
			String errorMessage = result.getString("errorMessage");
			String menus = result.getString("menus");
	
			JSONArray sections = new JSONArray(menus);
			Log.v(TAG, "Menus length " + sections.length());
	
			// Parse sections 
			for (int i = 0; i < sections.length(); i++) {
	
				JSONObject section = sections.getJSONObject(i);
				if (section.has("section_name") && section.has("subsections")) {

					JSONArray subsections = section.getJSONArray("subsections");
					
					if (subsections != null && subsections.length() > 0) {

						for (int j = 0; j < subsections.length(); j++) {
							
							JSONObject subSection = subsections.getJSONObject(j);
							String subsection_name = subSection.getString("subsection_name");

							String section_name = section.getString("section_name");

							// If it's a top level item (no section or subsection name) use a generic section title
							if (section_name.trim().length() == 0)
								section_name = subsection_name;
							else if (subsection_name.trim().length() > 0)
								section_name += " - " + subsection_name;
							if (section_name.trim().length() == 0)
								section_name = "Various items";
							
							// Add the heading title to the headings list 
							headings.add(section_name);

							// Add the list of items under that heading to the items list
							JSONArray contents = subSection.getJSONArray("contents");
							ArrayList<Item> subsection_contents = new ArrayList<Item>();
							for (int k = 0; k < contents.length(); k++) {
								Item menuDrink = new Item(contents.getJSONObject(k));

								subsection_contents.add(menuDrink);
							}
							
							// Add the contents of the subsection to the list of items
							items.add(subsection_contents);
						}
					}
				}
			}	
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return new Menu(headings, items);
	}
	
	
	
	/*****
	 * 
	 * TODO - Viewer functions
	 * 
	 */
	

	/*
	 * Displays a menu in the view's expandable list adapter. If the menu cache is not loaded yet,
	 * it calls the appropriate loader that will in turn call this function again once they have 
	 * loaded the menu.
	 * 
	 * Expects mActivity to be pointing to the right context
	 */
	
	void updateView(){
		
		Log.v(TAG, "updateView()");
		
		// If menu is not already in memory, call the appropriate loader
		if (mMenu == null) {
			Log.d(TAG, "Menu not available for display");
			loadMenu();
			return;
		}

		// If the view is not yet initialized, don't display the menu. This also 
		if (mDrinksListView == null) {
			Log.d(TAG, "View is not available");
			return;
		}

		
		// Display menu from memory into the view
		
		ArrayList<String> headings = mMenu.headings;
		final ArrayList<ArrayList<Item>> items = mMenu.items;

		Log.v(TAG, "Menu is in cache. Displaying " + headings.size() + " headings");

		try {

			mDrinksListView.setAdapter(new ExpandableListAdapter(mActivity,headings, items));
			
		} catch (Exception e) {
			
			e.printStackTrace();
			return;
		}

	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.v(TAG, "onDestroy()");
		
		// Because the fragment may be destroyed while the activity persists, remove pointer from activity
		((MainActivity) getActivity()).mDrinksFragment = null;
	}

}