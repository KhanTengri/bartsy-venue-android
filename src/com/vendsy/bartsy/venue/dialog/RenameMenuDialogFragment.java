
package com.vendsy.bartsy.venue.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.vendsy.bartsy.venue.R;

/**
 * @author Seenu Malireddy
 *
 */
public class RenameMenuDialogFragment extends DialogFragment  {

	DialogInterface dialog = null;
	private EditText nameText;
	private View view;
	private String name;

	// Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
    }
   
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
   
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    view = inflater.inflate(R.layout.rename_dialog, null);
	    
	    nameText = (EditText)view.findViewById(R.id.nameText);
	    nameText.setText(name);
	    
	    builder.setView(view)
	    // Add action buttons
	        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
	        	
	            @Override
	            public void onClick(DialogInterface dialog, int id) {
                    saveAction();
	            }})
	        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	        	
	            @Override
	            public void onClick(DialogInterface dialog, int id) {
                    
	            }});
	    return builder.create();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public EditText getNameText() {
		return nameText;
	}

	public void setNameText(EditText nameText) {
		this.nameText = nameText;
	}

	/**
	 * Invokes this method when the user pressed on save button
	 * 
	 */
	protected void saveAction() {
		String name = nameText.getText().toString();
		// Name should not be empty
		if(name.trim().length()==0){
			// To display error message for name edit text
			nameText.setError("Please enter name");
			return;
		}
		proceedToSaveName();
	}
	// This method implementation is responsible for calling method.
	protected void proceedToSaveName() {}
	
}
