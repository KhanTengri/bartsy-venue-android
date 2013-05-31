
package com.vendsy.bartsy.venue.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.vendsy.bartsy.venue.R;

/**
 * @author Seenu Malireddy
 *
 */
public class InventoryDialogFragment extends DialogFragment  {

    
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
    }	
	
	DialogInterface dialog = null;
    
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
   
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    
	    View view = inflater.inflate(R.layout.dialog_inventory, null);
	    
	    	    
	    builder.setView(view)
	    // Add action buttons
	        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
	        	
	            @Override
	            public void onClick(DialogInterface dialog, int id) {
                    
	            }})
	        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	        	
	            @Override
	            public void onClick(DialogInterface dialog, int id) {
                    
	            }});
	    return builder.create();
	}
	
}
