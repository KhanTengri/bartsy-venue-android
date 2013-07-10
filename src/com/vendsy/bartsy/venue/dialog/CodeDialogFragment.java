/**
 * 
 */
package com.vendsy.bartsy.venue.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.vendsy.bartsy.venue.BartsyApplication;
import com.vendsy.bartsy.venue.R;

/**
 * @author peterkellis
 * 
 */
public class CodeDialogFragment extends DialogFragment implements OnClickListener, OnTouchListener {

	public final static String TAG = "CodeDialogFragment";
	
	private BartsyApplication mApp = null;
	public String mCode = "";
    

    /**
     * TODO - Dialog interface
     */

    // The activity that creates an instance of this dialog fragment must implement this interface in order to receive event callbacks. Each method
    // passes the DialogFragment in case the host needs to query it.
	public interface CodeDialogListener {
		public void onCodeComplete(CodeDialogFragment dialog);
	}
	
	// Use this instance of the interface to deliver action events
	CodeDialogListener mListener;

	// Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NoticeDialogListener so we can send events to the host
			mListener = (CodeDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString() + " must implement CodeDialogListener");
		}
		
		mApp = (BartsyApplication) getActivity().getApplication();
	}
	
	
    /**
     * TODO - Dialog views
     */


	@Override
    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CodeDialogStyle);
	    // this setStyle is VERY important.
	    // STYLE_NO_FRAME means that I will provide my own layout and style for the whole dialog
	    // so for example the size of the default dialog will not get in my way
	    // the style extends the default one. see bellow.        
    }

	/*
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		// Create dialog and set animation styles
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		// Inflate and set the layout for the dialog. Pass null as the parent view because its going in the dialog layout
		View view = inflater.inflate(R.layout.code_dialog, null);

		// Setup up title and buttons
		builder.setView(view);
//		builder.setTitle("Pickup code");

		// Set up button listeners
		view.findViewById(R.id.view_code_dialog_button_1).setOnClickListener(this);
		view.findViewById(R.id.view_code_dialog_button_2).setOnClickListener(this);
		view.findViewById(R.id.view_code_dialog_button_3).setOnClickListener(this);
		view.findViewById(R.id.view_code_dialog_button_4).setOnClickListener(this);
		view.findViewById(R.id.view_code_dialog_button_5).setOnClickListener(this);
		view.findViewById(R.id.view_code_dialog_button_6).setOnClickListener(this);
		view.findViewById(R.id.view_code_dialog_button_7).setOnClickListener(this);
		view.findViewById(R.id.view_code_dialog_button_8).setOnClickListener(this);
		view.findViewById(R.id.view_code_dialog_button_9).setOnClickListener(this);
		
		// Create dialog and set up animation
		return builder.create();
	}
	 */
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

		// Inflate and set the layout for the dialog. Pass null as the parent view because its going in the dialog layout
		View view = inflater.inflate(R.layout.code_dialog, container);
		 
		// Set up buttons
		((StateListDrawable) ((Button) view.findViewById(R.id.view_code_dialog_button_1)).getBackground()).setAlpha(0xff);
		((StateListDrawable) ((Button) view.findViewById(R.id.view_code_dialog_button_2)).getBackground()).setAlpha(0xff);
		((StateListDrawable) ((Button) view.findViewById(R.id.view_code_dialog_button_3)).getBackground()).setAlpha(0xff);
		((StateListDrawable) ((Button) view.findViewById(R.id.view_code_dialog_button_4)).getBackground()).setAlpha(0xff);
		((StateListDrawable) ((Button) view.findViewById(R.id.view_code_dialog_button_5)).getBackground()).setAlpha(0xff);
		((StateListDrawable) ((Button) view.findViewById(R.id.view_code_dialog_button_6)).getBackground()).setAlpha(0xff);
		((StateListDrawable) ((Button) view.findViewById(R.id.view_code_dialog_button_7)).getBackground()).setAlpha(0xff);
		((StateListDrawable) ((Button) view.findViewById(R.id.view_code_dialog_button_8)).getBackground()).setAlpha(0xff);
		((StateListDrawable) ((Button) view.findViewById(R.id.view_code_dialog_button_9)).getBackground()).setAlpha(0xff);

		view.findViewById(R.id.view_code_dialog_button_1).setOnClickListener(this);
		view.findViewById(R.id.view_code_dialog_button_2).setOnClickListener(this);
		view.findViewById(R.id.view_code_dialog_button_3).setOnClickListener(this);
		view.findViewById(R.id.view_code_dialog_button_4).setOnClickListener(this);
		view.findViewById(R.id.view_code_dialog_button_5).setOnClickListener(this);
		view.findViewById(R.id.view_code_dialog_button_6).setOnClickListener(this);
		view.findViewById(R.id.view_code_dialog_button_7).setOnClickListener(this);
		view.findViewById(R.id.view_code_dialog_button_8).setOnClickListener(this);
		view.findViewById(R.id.view_code_dialog_button_9).setOnClickListener(this);
		
		return view;
	}
	
	@Override
	public void onClick(View v) {

		Log.v(TAG, "onClick()");
		
		String code = "";

		switch (v.getId()) {
		case R.id.view_code_dialog_button_1:
			code = "1";
			break;
		case R.id.view_code_dialog_button_2:
			code = "2";
			break;
		case R.id.view_code_dialog_button_3:
			code = "3";
			break;
		case R.id.view_code_dialog_button_4:
			code = "4";
			break;
		case R.id.view_code_dialog_button_5:
			code = "5";
			break;
		case R.id.view_code_dialog_button_6:
			code = "6";
			break;
		case R.id.view_code_dialog_button_7:
			code = "7";
			break;
		case R.id.view_code_dialog_button_8:
			code = "8";
			break;
		case R.id.view_code_dialog_button_9:
			code = "9";
			break;
		}
		
		if (!code.equals("")) {
			mCode += code;
			if (mCode.length() >= 3) {
				mListener.onCodeComplete(this);
			}
		}
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		
		Log.v(TAG, "onTouch()");

		String code = "";
		switch (arg0.getId()) {
		case R.id.view_code_dialog_button_1:
			code = "1";
			break;
		case R.id.view_code_dialog_button_2:
			code = "2";
			break;
		case R.id.view_code_dialog_button_3:
			code = "3";
			break;
		case R.id.view_code_dialog_button_4:
			code = "4";
			break;
		case R.id.view_code_dialog_button_5:
			code = "5";
			break;
		case R.id.view_code_dialog_button_6:
			code = "6";
			break;
		case R.id.view_code_dialog_button_7:
			code = "7";
			break;
		case R.id.view_code_dialog_button_8:
			code = "8";
			break;
		case R.id.view_code_dialog_button_9:
			code = "9";
			break;
		}
		
		if (!code.equals("")) {
			
			mCode += code;
			
			if (mCode.length() >= 3) {
				mListener.onCodeComplete(this);
			}
		}
		
		return false;
	}

}
