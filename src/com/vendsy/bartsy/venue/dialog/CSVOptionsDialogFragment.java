
package com.vendsy.bartsy.venue.dialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.vendsy.bartsy.venue.R;
import com.vendsy.bartsy.venue.db.DatabaseManager;
import com.vendsy.bartsy.venue.utils.Utilities;

/**
 * @author Seenu Malireddy
 *
 */
public class CSVOptionsDialogFragment extends DialogFragment  {

	private Spinner categoriesSpinner;
	private Uri uri;
	private CheckBox uploadCheckBox;
	private int position;
	
	private static final String TAG = "CSVOptionsDialogFragment";
	
	// Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
    
	public Uri getUri() {
		return uri;
	}

	public void setUri(Uri uri) {
		this.uri = uri;
		
		String filePath = uri.getEncodedPath();
		// Try to identify the type based on filename
		if(filePath.contains("cocktails")){
			position = 0;
		}else{
			position = 1;
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
   
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    View view = inflater.inflate(R.layout.dialog_csv_options, null);
	    
	    categoriesSpinner = (Spinner)view.findViewById(R.id.categoriesList);
	    uploadCheckBox = (CheckBox)view.findViewById(R.id.uploadCheckBox);
	    
	    
	    updateCategoriesSpinner();
	    
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
	
	/**
	 * Invokes this method when the user pressed on save button
	 * 
	 */
	protected void saveAction() {
		
			if(uri==null) return;
			
		 	String scheme = uri.getScheme();
	        Log.d(TAG, "scheme :: "+scheme);
	        // Try to get the file from ContentResolver
	        if(ContentResolver.SCHEME_CONTENT.equals(scheme)){
	            try{
	                ContentResolver cr = getActivity().getContentResolver();
	                AssetFileDescriptor afd = cr.openAssetFileDescriptor(uri, "r");
	                InputStream is = cr.openInputStream(uri);
	                if(is == null) return;
	                saveCSVFile(is, uploadCheckBox.isChecked(), categoriesSpinner.getSelectedItemPosition());
	            }
	            catch(FileNotFoundException e){
	                return;
	            }
	        } 
	        // Try to get the file from normal file path
	        else{
	            String filePath = uri.getEncodedPath();
	            if(filePath == null) return;
	            File file = new File(filePath);
	            InputStream is=null;
	            try{
	                is = new FileInputStream(file);
	                saveCSVFile(is, uploadCheckBox.isChecked(), categoriesSpinner.getSelectedItemPosition());
	            }
	            catch(FileNotFoundException e){
	            }
	        }
	}
	
	protected void saveCSVFile(InputStream is, boolean autoUpload, int type){
		
	}

	/**
	 * To update categories in the spinner
	 * 
	 * @param type
	 */
	public void updateCategoriesSpinner(){
		
		
		ArrayList<String> categoryNames = new ArrayList<String>();

		categoryNames.add("Cocktails");
		categoryNames.add("Ingredients");
		
		// Initiate array adapter to display categories in the spinner
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, categoryNames);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		categoriesSpinner.setAdapter(adapter);
		categoriesSpinner.setSelection(position, true);
		
		// Event listener for item selection in the category spinner
		categoriesSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
	
	
}
