package com.vendsy.bartsy.venue.model;

import org.json.JSONException;
import org.json.JSONObject;
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
	private boolean tableOrdering;
	private boolean isPickupLocution;
	private String hours;
	private String managerName;
	private String managerEmail;
	private String managerPassword;
	private String managerCell;
	private String representativeName;
	private String representativeEmail;
	private String representativeCell;
	private String locuId;
	private String venueImagePath;
	private String locuUsername;
	private String locuPassword;
	private String locuSection;
	private String phoneNumber;
	
	
	/**
	 * TODO - Constructors / parsers
	 */
	
	public Venue(JSONObject json) throws JSONException{
		
		// Parse all the details from the JSON Object
		if(json.has("venueName")){
			name = json.getString("venueName");
		}
		if(json.has("venueId")){
			id = json.getString("venueId");
		}
		if(json.has("venueImagePath")){
			imagePath = json.getString("venueImagePath");
		}
		if(json.has("latitude")){
			latitude = json.getString("latitude");
		}
		if(json.has("longitude")){
			longitude = json.getString("longitude");
		}
		if(json.has("locuId")){
			locuId = json.getString("locuId");
		}
		if(json.has("venueStatus")){
			venueStatus = json.getString("venueStatus");
		}
		if(json.has("wifiPresent")){
			wifiPresent = json.getString("wifiPresent");
		}
		if(json.has("wifiName")){
			wifiName = json.getString("wifiName");
		}
		if(json.has("wifiPassword")){
			wifiPassword = json.getString("wifiPassword");
		}
		if(json.has("typeOfAuthentication")){
			typeOfAuthentication = json.getString("typeOfAuthentication");
		}
		if(json.has("address")){
			address = json.getString("address");
		}
		if(json.has("cancelOrderTime")){
			cancelOrderTime = json.getString("cancelOrderTime");
		}
		if(json.has("totalTaxRate")){
			totalTaxRate = json.getString("totalTaxRate");
		}
		if(json.has("currentTime")){
			currentTime = json.getString("currentTime");
		}
		if(json.has("wifiNetworkType")){
			wifiNetworkType = json.getString("wifiNetworkType");
		}
		if(json.has("tableOrdering")){
			tableOrdering = json.getBoolean("tableOrdering");
		}
		if(json.has("venueLogin")){
			managerEmail = json.getString("venueLogin");
		}
		if(json.has("open_hours")){
			hours = json.getString("open_hours");
		}
		if(json.has("venueImagePath")){
			venueImagePath = json.getString("venueImagePath");
		}
		if(json.has("isPickupLocution")){
			isPickupLocution = json.getBoolean("isPickupLocution");
		}
		if(json.has("locuId")){
			locuId = json.getString("locuId");
		}
		if(json.has("locuUsername")){
			locuUsername = json.getString("locuUsername");
		}
		if(json.has("locuPassword")){
			locuPassword = json.getString("locuPassword");
		}
		if(json.has("phone")){
			phoneNumber = json.getString("phone");
		}
		if(json.has("venuePassword")){
			managerPassword = json.getString("venuePassword");
		}
		if(json.has("locuSection")){
			locuSection = json.getString("locuSection");
		}
	};
	
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


	public boolean getTableOrdering() {
		return tableOrdering;
	}

	public void setTableOrdering(boolean tableOrdering) {
		this.tableOrdering = tableOrdering;
	}

	public String getHours() {
		return hours;
	}

	public void setHours(String hours) {
		this.hours = hours;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	public String getManagerEmail() {
		return managerEmail;
	}

	public void setManagerEmail(String managerEmail) {
		this.managerEmail = managerEmail;
	}

	public String getManagerCell() {
		return managerCell;
	}

	public void setManagerCell(String managerCell) {
		this.managerCell = managerCell;
	}

	public String getRepresentativeName() {
		return representativeName;
	}

	public void setRepresentativeName(String representativeName) {
		this.representativeName = representativeName;
	}

	public String getRepresentativeEmail() {
		return representativeEmail;
	}

	public void setRepresentativeEmail(String representativeEmail) {
		this.representativeEmail = representativeEmail;
	}

	public String getRepresentativeCell() {
		return representativeCell;
	}

	public void setRepresentativeCell(String representativeCell) {
		this.representativeCell = representativeCell;
	}

	public boolean isPickupLocution() {
		return isPickupLocution;
	}

	public void setPickupLocution(boolean isPickupLocution) {
		this.isPickupLocution = isPickupLocution;
	}

	public String getLocuId() {
		return locuId;
	}

	public void setLocuId(String locuId) {
		this.locuId = locuId;
	}

	public String getVenueImagePath() {
		return venueImagePath;
	}

	public void setVenueImagePath(String venueImagePath) {
		this.venueImagePath = venueImagePath;
	}

	public String getLocuUsername() {
		return locuUsername;
	}

	public void setLocuUsername(String locuUsername) {
		this.locuUsername = locuUsername;
	}

	public String getLocuPassword() {
		return locuPassword;
	}

	public void setLocuPassword(String locuPassword) {
		this.locuPassword = locuPassword;
	}

	public String getPhone() {
		return phoneNumber;
	}

	public void setPhone(String phone) {
		this.phoneNumber = phone;
	}

	public String getManagerPassword() {
		return managerPassword;
	}

	public void setManagerPassword(String managerPassword) {
		this.managerPassword = managerPassword;
	}

	public String getLocuSection() {
		return locuSection;
	}

	public void setLocuSection(String locuSection) {
		this.locuSection = locuSection;
	}
	
}
