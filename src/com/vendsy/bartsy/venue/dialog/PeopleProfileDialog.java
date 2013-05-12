/**
 * 
 */
package com.vendsy.bartsy.venue.dialog;

import com.vendsy.bartsy.venue.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;

/**
 * @author peterkellis
 *
 */
public class PeopleProfileDialog extends DialogFragment  {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
   
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(inflater.inflate(R.layout.dialog_user_profile, null));

	    return builder.create();
	}
}
