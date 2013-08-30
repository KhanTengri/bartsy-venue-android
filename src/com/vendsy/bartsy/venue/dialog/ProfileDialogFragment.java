/**
 * 
 */
package com.vendsy.bartsy.venue.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.vendsy.bartsy.venue.R;
import com.vendsy.bartsy.venue.model.Profile;

/**
 * @author Seenu Malireddy
 * 
 */
public class ProfileDialogFragment extends DialogFragment implements OnClickListener {

	public Profile mUser = null;
	private static final String TAG = "ProfileDialogFragment";

	/*
	 * The activity that creates an instance of this dialog fragment must
	 * implement this interface in order to receive event callbacks. Each method
	 * passes the DialogFragment in case the host needs to query it.
	 */
	public interface ProfileDialogListener {
		public void onUserDialogPositiveClick(ProfileDialogFragment dialog);
		public void onUserDialogNegativeClick(ProfileDialogFragment dialog);
	}

	// Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
	@Override
	public void onAttach(Activity activity) {
		
		super.onAttach(activity);
		
	}

	DialogInterface dialog = null;
	private View view;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		// Inflate and set the layout for the dialog. Pass null as the parent view because its going in the dialog layout
		if (mUser == null)
			view = inflater.inflate(R.layout.dialog_user_profile, null);
		else
			view = mUser.updateDialogView(view);
			
		// Set view and add click listeners by calling the listeners in the calling activity
		builder.setView(view);
	    builder.setTitle("User Profile");
	    builder.setPositiveButton("Send Message", this);
	    builder.setNegativeButton("Cancel", this);
	    
		return builder.create();
	}
	

	@Override
	public void onClick(DialogInterface dialog, int which) {
		
	}

}
