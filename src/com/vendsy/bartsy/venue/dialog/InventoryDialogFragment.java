
package com.vendsy.bartsy.venue.dialog;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

import com.vendsy.bartsy.venue.R;
import com.vendsy.bartsy.venue.db.DatabaseManager;
import com.vendsy.bartsy.venue.model.Category;
import com.vendsy.bartsy.venue.model.Cocktail;
import com.vendsy.bartsy.venue.model.Ingredient;

/**
 * @author Seenu Malireddy
 *
 */
public class InventoryDialogFragment extends DialogFragment  {

	DialogInterface dialog = null;
	private EditText nameText;
	private Spinner categoriesSpinner;
	private Category selectedCategory;
	private String type;
	private View view;
	
    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	

	public Category getSelectedCategory() {
		return selectedCategory;
	}

	public void setSelectedCategory(Category selectedCategory) {
		this.selectedCategory = selectedCategory;
	}

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
	    if(type!=null && !type.equals(Category.COCKTAILS_TYPE)){
	    	view = inflater.inflate(R.layout.dialog_inventory, null);
	    }else{
	    	view = inflater.inflate(R.layout.dialog_cocktails, null);
	    }
	    
	    nameText = (EditText)view.findViewById(R.id.nameText);
	    
	    categoriesSpinner = (Spinner)view.findViewById(R.id.categoriesList);
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
		String name = nameText.getText().toString();
		// Name should not be empty
		if(name.trim().length()==0){
			// To display error message for name edit text
			nameText.setError("Please enter name");
			return;
		}
		// There is different table structure for Ingredients and cocktails. Need to create objects based on the type and 
		if(type!=null && !type.equals(Category.COCKTAILS_TYPE)){
			Ingredient ingredient = new Ingredient();
			ingredient.setName(name);
			ingredient.setCategory(selectedCategory);
			// Here, Saving in the database
			DatabaseManager.getInstance().saveIngredient(ingredient);
		}else{
			
			Cocktail cocktail = new Cocktail();
			// Set all values to the cocktails object from menu 
			cocktail.setIngredients(((EditText)view.findViewById(R.id.ingredientsText)).getText().toString());
			cocktail.setInstructions(((EditText)view.findViewById(R.id.instructionsText)).getText().toString());
			
			// Here, Saving in the database
			DatabaseManager.getInstance().saveCocktail(cocktail);
		}
		
	}

	/**
	 * To update categories in the spinner
	 * 
	 * @param type
	 */
	public void updateCategoriesSpinner(){
		
		int position=0;
		
		ArrayList<String> categoryNames = new ArrayList<String>();
		// To get list of categories from the database
		final List<Category> categories = DatabaseManager.getInstance().getCategories(type);
		for (int i = 0; i < categories.size(); i++) {
			Category category= categories.get(i);
			// To get selected category position
			if(selectedCategory!=null && selectedCategory.getId()==category.getId()){
				position = i;
			}
			categoryNames.add(categories.get(i).getName());
		}
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
				// To get selected category from the categories array list
				selectedCategory = categories.get(categoriesSpinner.getSelectedItemPosition());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
	}
	
	
}
