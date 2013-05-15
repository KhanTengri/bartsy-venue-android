package com.vendsy.bartsy.venue.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.vendsy.bartsy.venue.R;
import com.vendsy.bartsy.venue.utils.Constants;

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
			this.profileImageUrl = Constants.DOMAIN_NAME.trim() + "/Bartsy/"
					+ profileImageUrl.trim();

			System.out.println("profileImageUrl " + profileImageUrl);
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
		((ImageView) view.findViewById(R.id.view_user_list_image_resource))
				.setImageBitmap(this.image);
		((TextView) view.findViewById(R.id.view_user_list_name))
				.setText(this.name);

		ImageView profileImageView = (ImageView) view
				.findViewById(R.id.ImageView16);
		image(this.profileImageUrl, profileImageView);
		view.setOnClickListener(listener);

		view.setTag(this);

	}

	/*
	 * This method is used to retrieve image from webservices
	 */
	public static void image(String image3, ImageView imageView) {

		ArrayList<String> al = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(image3, ":");
		String s = null;
		while (st.hasMoreTokens()) {
			s = st.nextToken();
			al.add(s);

		}
		// if (al.get(0).equalsIgnoreCase("http")) {
		//
		// image3 = al.get(0) + "s:" + al.get(1);
		// DownloadImage(image3, imageView);
		//
		// } else {
		DownloadImage(image3, imageView);
		// }

	}

	public static void DownloadImage(String URL, ImageView imageView) {

		Random r = new Random();
		@SuppressWarnings("unused")
		int i = r.nextInt(4);
		downloadFile(URL, imageView);
		Log.i("im url", URL);

	}

	static void downloadFile(final String fileUrl, final ImageView imageView) {

		System.out.println("download file");
		new AsyncTask<String, Void, Bitmap>() {
			Bitmap bmImg;

			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();

			}

			protected Bitmap doInBackground(String... params) {
				// TODO Auto-generated method stub

				System.out.println("doing back ground");
				URL myFileUrl = null;
				try {
					myFileUrl = new URL(fileUrl);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {

					HttpURLConnection conn = (HttpURLConnection) myFileUrl
							.openConnection();
					conn.setDoInput(true);
					conn.connect();
					InputStream is = conn.getInputStream();
					bmImg = BitmapFactory.decodeStream(is);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return bmImg;
			}

			protected void onPostExecute(Bitmap result) {
				// TODO Auto-generated method stub
				System.out.println("on post ******************");

				imageView.setImageBitmap(result);

			}

		}.execute();

	}
}
