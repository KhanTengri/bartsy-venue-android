package com.vendsy.bartsy.venue.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.analytics.tracking.android.Log;
import com.vendsy.bartsy.venue.R;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
/**
 * 
 * @author Seenu Malireddy
 *
 */
public class Venue {
	
	private String id;
	private String name;
	private String imagePath;
	private String latitude;
	private String longitude;
	private String venueStatus;
	private String wifiPresent;
	private String wifiName;
	private String wifiPassword;
	private String typeOfAuthentication;
	private String address;
	private String cancelOrderTime;
	private String totalTaxRate;
	private String currentTime;
	private String wifiNetworkType;
	private String tableOrdering;
	
	//TODO We are not getting all the fields from the server
	
	
	/**
	 * TODO - Constructors / parsers
	 */
	
	public Venue(JSONObject json) throws JSONException{
		
		// Parse all the details from the JSON format
		name = json.getString("venueName");
		id = json.getString("venueId");
		imagePath = json.getString("venueImagePath");
		latitude = json.getString("latitude");
		longitude = json.getString("longitude");
		venueStatus = json.getString("venueStatus");
		wifiPresent = json.getString("wifiPresent");
		wifiName = json.getString("wifiName");
		wifiPassword = json.getString("wifiPassword");
		typeOfAuthentication = json.getString("typeOfAuthentication");
		address = json.getString("address");
		cancelOrderTime = json.getString("cancelOrderTime");
		totalTaxRate = json.getString("totalTaxRate");
		currentTime = json.getString("currentTime");
		wifiNetworkType = json.getString("wifiNetworkType");
		tableOrdering = json.getString("tableOrdering");
	};
	
	public boolean has(String field) {
		return !(field == null || field.equals(""));
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getVenueStatus() {
		return venueStatus;
	}

	public void setVenueStatus(String venueStatus) {
		this.venueStatus = venueStatus;
	}

	public String getWifiPresent() {
		return wifiPresent;
	}

	public void setWifiPresent(String wifiPresent) {
		this.wifiPresent = wifiPresent;
	}

	public String getWifiName() {
		return wifiName;
	}

	public void setWifiName(String wifiName) {
		this.wifiName = wifiName;
	}

	public String getWifiPassword() {
		return wifiPassword;
	}

	public void setWifiPassword(String wifiPassword) {
		this.wifiPassword = wifiPassword;
	}

	public String getTypeOfAuthentication() {
		return typeOfAuthentication;
	}

	public void setTypeOfAuthentication(String typeOfAuthentication) {
		this.typeOfAuthentication = typeOfAuthentication;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCancelOrderTime() {
		return cancelOrderTime;
	}

	public void setCancelOrderTime(String cancelOrderTime) {
		this.cancelOrderTime = cancelOrderTime;
	}

	public String getTotalTaxRate() {
		return totalTaxRate;
	}

	public void setTotalTaxRate(String totalTaxRate) {
		this.totalTaxRate = totalTaxRate;
	}


	public String getCurrentTime() {
		return currentTime;
	}


	public void setCurrentTime(String currentTime) {
		this.currentTime = currentTime;
	}


	public String getWifiNetworkType() {
		return wifiNetworkType;
	}


	public void setWifiNetworkType(String wifiNetworkType) {
		this.wifiNetworkType = wifiNetworkType;
	}


	public String getTableOrdering() {
		return tableOrdering;
	}


	public void setTableOrdering(String tableOrdering) {
		this.tableOrdering = tableOrdering;
	}

}
