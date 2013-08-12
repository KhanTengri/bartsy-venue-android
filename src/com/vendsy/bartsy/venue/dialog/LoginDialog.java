package com.vendsy.bartsy.venue.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.vendsy.bartsy.venue.R;
import com.vendsy.bartsy.venue.VenueProfileActivity;

public class LoginDialog extends Dialog {
	
	// The parameters that the dialog sets up and passes to the listener below
	public String username;
	public String password;

	// Parameter passing variables
	LoginDialogListener mListener;		// point to the activity calling this dialog
	
	public LoginDialog(VenueProfileActivity listener) {
		super(listener);
		
		setContentView(R.layout.login_dialog);
		
		mListener = listener;
		
		setTitle("Login to Bartsy");
		
		Button submitButton = (Button)findViewById(R.id.submitButton);
		Button cancelButton = (Button)findViewById(R.id.cancelButton);
		
		submitButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Set return values 
            	username = ((EditText)findViewById(R.id.dialog_login_username)).getText().toString();
            	password = ((EditText)findViewById(R.id.dialog_login_password)).getText().toString();
            	
                // Send the positive button event back to the host activity
                mListener.onDialogPositiveClick(LoginDialog.this);
			}
		});
		
		cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
				// Send the negative button event back to the host activity
                mListener.onDialogNegativeClick(LoginDialog.this);
			}
		});
		
	}
	   
   /* The activity that creates an instance of this dialog must  implement this interface in order to 
    * receive event callbacks. Each method passes the Dialog in case the host needs to query it. */

	public interface LoginDialogListener {
       public void onDialogPositiveClick(LoginDialog dialog);
       public void onDialogNegativeClick(LoginDialog dialog);
   }

}