package com.vendsy.bartsy.venue.model;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.vendsy.bartsy.venue.R;
import com.vendsy.bartsy.venue.utils.Constants;
import com.vendsy.bartsy.venue.utils.Utilities;
import com.vendsy.bartsy.venue.utils.WebServices;

public class Profile {

	public String userID; // Unique ID enforced by Bartsy service
	public Bitmap image; // user's main profile image
	public String username; // user's first name / last name
	public String location; // use string for now
	public String info; // info string
	public String description;
	private String name;
	private String email;
	private String gender;
	private String type;
	private String socialNetworkId;
	private String profileImageUrl;
	private String displayName;
	private String birthday;
	private String aboutMe;
	private String relationshipStatus;
	
	// Ordering statistics
	private String firstOrderDate;
	private int orderCount;
	private int last30DaysOrderCount;
	
	// Check in statistics
	private String firstCheckInDate;
	private int checkInCount;
	private int last30DaysCheckInCount;
	
	
	private boolean imageDownloaded;

	ArrayList<Profile> likes = new ArrayList<Profile>();
	ArrayList<Profile> favorites = new ArrayList<Profile>();
	// ArrayList<Message> messages = new ArrayList<Message>();

	public View view = null; // the view of a particular user in a list, expect
								// a layout type of user_item.xml

	public Profile() {

	}

	public Profile(JSONObject json) {
		try {
			if (json.has("bartsyId"))
				userID = json.getString("bartsyId");
			if (json.has("gender"))
				gender = json.getString("gender");
			if (json.has("name"))
				name = json.getString("name");
			if (json.has("userImagePath")) 
				profileImageUrl = WebServices.DOMAIN_NAME +  json.getString("userImagePath").trim();

			if (json.has("firstOrderDate"))
				firstOrderDate = json.getString("firstOrderDate");
			if (json.has("orderCount"))
				orderCount = json.getInt("orderCount");
			if (json.has("last30DaysOrderCount"))
				last30DaysOrderCount = json.getInt("last30DaysOrderCount");
		
			if (json.has("firstCheckInDate"))
				firstCheckInDate = json.getString("firstCheckInDate");
			if (json.has("checkInCount"))
				checkInCount = json.getInt("checkInCount");
			if (json.has("last30DaysCheckInCount"))
				last30DaysCheckInCount = json.getInt("last30DaysCheckInCount");
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		
		JSONObject json = new JSONObject();
		
		try {
			json.put("bartsyId", userID);
			json.put("gender", gender);
			json.put("name", name);
			json.put("userImagePath", profileImageUrl);
			json.put("firstOrderDate", firstOrderDate);
			json.put("orderCount", orderCount);
			json.put("last30DaysOrderCount", last30DaysOrderCount);
			json.put("firstCheckInDate", firstCheckInDate);
			json.put("checkInCount", checkInCount);
			json.put("last30DaysCheckInCount", last30DaysCheckInCount);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return json.toString();
	}

	public View updateView(View view) {
		// Update the profile image - if the image doesn't exist locally, get it from the server 
		ImageView profileImageView = (ImageView) view.findViewById(R.id.view_user_list_image_resource);
		if(profileImageView==null){
			profileImageView = (ImageView) view.findViewById(R.id.view_customer_details_picture);
		}
		if (image == null && !imageDownloaded) {
			WebServices.downloadImage(this.profileImageUrl, this, profileImageView);
		} else {
			profileImageView.setImageBitmap(image);
		}

		// Update customer visible name
		((TextView) view.findViewById(R.id.view_customer_details_name)).setText(getName());

		// Update customer details
		
		String checkin = getFirstCheckInDate() == null ? "-" : Utilities.getFriendlyDate(getFirstCheckInDate(), "d MMM yyyy HH:mm:ss 'GMT'");
		String order   = getFirstOrderDate() == null ? "-" : Utilities.getFriendlyDate(getFirstOrderDate(), "d MMM yyyy HH:mm:ss 'GMT'");
		
		String details = "Customer since: " + checkin + 
				"\nFirst Bartsy order: " + order + 
				"\n" + getOrderCount() + " total, " + getLast30DaysOrderCount() + " recent orders" +
				"\n" + getCheckInCount() + " recent, " + getLast30DaysCheckInCount() + " total visits";
		((TextView) view.findViewById(R.id.view_customer_details_info)).setText(details);
		
		return view;
	}
	
	
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getAboutMe() {
		return aboutMe;
	}

	public void setAboutMe(String aboutMe) {
		this.aboutMe = aboutMe;
	}

	public String getRelationshipStatus() {
		return relationshipStatus;
	}

	public void setRelationshipStatus(String relationshipStatus) {
		this.relationshipStatus = relationshipStatus;
	}

	public String getSocialNetworkId() {
		return socialNetworkId;
	}

	public void setSocialNetworkId(String socialNetworkId) {
		this.socialNetworkId = socialNetworkId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}
	

	public boolean isImageDownloaded() {
		return imageDownloaded;
	}

	public void setImageDownloaded(boolean imageDownloaded) {
		this.imageDownloaded = imageDownloaded;
	}

	public void updateView(OnClickListener listener) {

		((TextView) view.findViewById(R.id.view_user_list_name)).setText(this.name);

		// Update the profile image - if the image doesn't exist locally, get it from the server
		ImageView profileImageView = (ImageView) view.findViewById(R.id.view_user_list_image_resource);
		if (image == null && !imageDownloaded) {
			WebServices.downloadImage(this.profileImageUrl, this, profileImageView);
		} else {
			profileImageView.setImageBitmap(image);
		}
		view.setOnClickListener(listener);

		// We use the view's tag as a pointer to the class so we can easily find the class from the UI
		view.setTag(this);

	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public String getFirstOrderDate() {
		return firstOrderDate;
	}

	public boolean hasFirstOrderDate() {
		return firstOrderDate == null || firstOrderDate.equals("");
	}
	
	public void setFirstOrderDate(String firstOrderDate) {
		this.firstOrderDate = firstOrderDate;
	}

	public int getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
	}

	public int getLast30DaysOrderCount() {
		return last30DaysOrderCount;
	}

	public void setLast30DaysOrderCount(int last30DaysOrderCount) {
		this.last30DaysOrderCount = last30DaysOrderCount;
	}

	public boolean hasFirstCheckInDate() {
		return firstCheckInDate == null || firstCheckInDate.equals("");
	}
	
	public String getFirstCheckInDate() {
		return firstCheckInDate;
	}

	public void setFirstCheckInDate(String firstCheckInDate) {
		this.firstCheckInDate = firstCheckInDate;
	}

	public int getCheckInCount() {
		return checkInCount;
	}

	public void setCheckInCount(int checkInCount) {
		this.checkInCount = checkInCount;
	}

	public int getLast30DaysCheckInCount() {
		return last30DaysCheckInCount;
	}

	public void setLast30DaysCheckInCount(int last30DaysCheckInCount) {
		this.last30DaysCheckInCount = last30DaysCheckInCount;
	}
	
}
