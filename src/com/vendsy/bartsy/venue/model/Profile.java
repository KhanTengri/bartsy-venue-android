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

	ArrayList<Profile> likes = new ArrayList<Profile>();
	ArrayList<Profile> favorites = new ArrayList<Profile>();
	// ArrayList<Message> messages = new ArrayList<Message>();

	public View view = null; // the view of a particular user in a list, expect
								// a layout type of user_item.xml

	public Profile() {

	}

	public Profile(String userid, String username, String location,
			String info, String description, Bitmap image) {
		this.image = image;
		this.userID = userid;
		this.username = username;
		this.location = location;
		this.info = info;
		this.description = description;
	}

	public Profile(JSONObject json) {
		try {
			userID = json.getString("bartsyId");
			gender = json.getString("gender");
			name = json.getString("name");
			profileImageUrl = json.getString("userImagePath");
			this.profileImageUrl = Constants.DOMAIN_NAME
					+ profileImageUrl.trim();

		} catch (JSONException e) {
			e.printStackTrace();
		}
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

	public void updateView(OnClickListener listener) {

		((TextView) view.findViewById(R.id.view_user_list_name)).setText(this.name);

		// Update the profile image - if the image doesn't exist locally, get it from the server
		ImageView profileImageView = (ImageView) view.findViewById(R.id.view_user_list_image_resource);
		if (image == null) {
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
	
}
