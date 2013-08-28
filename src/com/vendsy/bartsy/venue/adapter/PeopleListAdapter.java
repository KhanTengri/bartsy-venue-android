package com.vendsy.bartsy.venue.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.vendsy.bartsy.venue.model.Profile;

public class PeopleListAdapter extends ArrayAdapter<Profile> {

		private ArrayList<Profile> profiles;
		private int resource;

		public PeopleListAdapter(Context context, int resource, ArrayList<Profile> profiles) {
		    super(context, resource, profiles);
		    this.profiles = profiles;
		    this.resource = resource;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

		    if (convertView == null) 
		        convertView = LayoutInflater.from(getContext()).inflate(resource, null);

		    Profile profile = profiles.get(position);
		    profile.updateView(convertView);

		    return convertView;
		}
}
