/**
 * 
 */
package com.vendsy.bartsy.venue.view;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.internal.co;
import com.vendsy.bartsy.venue.BartsyApplication;
import com.vendsy.bartsy.venue.MainActivity;
import com.vendsy.bartsy.venue.R;
import com.vendsy.bartsy.venue.db.DatabaseManager;
import com.vendsy.bartsy.venue.model.Category;
import com.vendsy.bartsy.venue.model.Cocktail;
import com.vendsy.bartsy.venue.model.Ingredient;
import com.vendsy.bartsy.venue.utils.Constants;
import com.vendsy.bartsy.venue.utils.Utilities;
import com.vendsy.bartsy.venue.utils.WebServices;

/**
 * @author peterkellis
 * @author Seenu Malireddy
 */
public class InventorySectionFragment extends Fragment {

	View mRootView = null;
	private Button spiritsTab;
	private LinearLayout categoriesList;
	private LayoutInflater inflater;
	private TableLayout itemsLayout;
	private ArrayList<ImageView> categoriesArrowViews = new ArrayList<ImageView>();
	private Button mixersTab;
	private Button cocktailsTab;
	private List<Ingredient> ingredients;
	private List<Cocktail> cocktails;
	private ScrollView categoryScrollView;
	private Button saveButton;
	private Category selectedCategory;
	private String selectedType;
	private String venueId;
	
	// Progress dialog
	private ProgressDialog progressDialog;
	// Handler 
	private Handler handler = new Handler();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
 
		this.inflater = inflater;
		
		mRootView = inflater.inflate(R.layout.inventory_main, container, false);
		
		itemsLayout = (TableLayout) mRootView.findViewById(R.id.itemsLayout);
		categoriesList = (LinearLayout) mRootView.findViewById(R.id.categoryLayout);
		categoryScrollView = (ScrollView) mRootView.findViewById(R.id.categoryScrollView);
		saveButton = (Button) mRootView.findViewById(R.id.saveButton);
		
		saveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveAction();
			}
		});
		
		updateInventoryView();
		// To get venue Id from the application global variable
		venueId = ((BartsyApplication)getActivity().getApplication()).venueProfileID;
		
        return mRootView;
	}
	
	/**
	 *  To save updated custom drinks information and send to the server 
	 */
	private void saveAction() {
		// Error handling
		if(selectedType==null) return;
		
		progressDialog = Utilities.progressDialog(getActivity(),"Saving..");
		progressDialog.show();
		
		
		// To call web service in background
		new Thread(){
			private String response;

			public void run() {
				if(selectedCategory!=null && !selectedType.equals(Category.COCKTAILS_TYPE) && ingredients!=null){
					// Save ingredient updated information
					for (Ingredient ingredient : ingredients) {
						DatabaseManager.getInstance().saveIngredient(ingredient);
					}
					
					// Ingredients Web service call
					response = WebServices.saveIngredients(selectedCategory, ingredients, venueId, getActivity());
					
				}else if(selectedType.equals(Category.COCKTAILS_TYPE) && cocktails !=null){
					// Save cocktail updated information
					for (Cocktail cocktail : cocktails) {
						DatabaseManager.getInstance().saveCocktail(cocktail);
					}
					
					// TODO Cocktail Web service call
					
				}
				
				
				// To call UI thread
				handler.post(new Runnable() {
					
					@Override
					public void run() {
						// To stop progress dialog
						progressDialog.dismiss();
						// Error handling
						if(response==null) return;
						// Try to get the message from the server response in JSON format
						try {
							JSONObject json = new JSONObject(response);
							
							Toast.makeText(getActivity(), json.getString("errorMessage"), Toast.LENGTH_LONG);
						} catch (JSONException e) {}
					}
				});
			}
		}.start();
		
	}

	/***
	 * Updates the inventory view
	 */
	public void updateInventoryView() {
		
		Log.i("Bartsy", "InventorySectionFragment.updateOrdersView()");
		
		if (mRootView == null) return;
		// By default Spirits tab is selected, We need to show spirits inventory content
		updateCategoriesView(Category.SPIRITS_TYPE);
		
		// Try to get spirits, mixers and cocktails tabs form the xml layout
		spiritsTab = (Button) mRootView.findViewById(R.id.spiritsButton);
		mixersTab = (Button) mRootView.findViewById(R.id.mixersButton);
		cocktailsTab = (Button) mRootView.findViewById(R.id.cocktailsButton);
		
		// To set click listeners for all the tabs
		spiritsTab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				spiritsTabSelected();
			}
		});
		
		mixersTab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mixersTabSelected();
			}
		});
		
		cocktailsTab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cocktailsTabSelected();
			}
		});
	}
	/**
	 * Invokes when the spirits tab pressed
	 */
	private void spiritsTabSelected() {
		spiritsTab.setBackgroundResource(R.drawable.tab_over);
		mixersTab.setBackgroundResource(R.drawable.tab_bg);
		cocktailsTab.setBackgroundResource(R.drawable.tab_bg);
		
		// Show categories view
		categoryScrollView.setVisibility(View.VISIBLE);
		
		updateCategoriesView(Category.SPIRITS_TYPE);
	}
	
	/**
	 * Invokes when the spirits tab pressed
	 */
	private void mixersTabSelected() {
		spiritsTab.setBackgroundResource(R.drawable.tab_bg);
		mixersTab.setBackgroundResource(R.drawable.tab_over);
		cocktailsTab.setBackgroundResource(R.drawable.tab_bg);
		
		// Show categories view
		categoryScrollView.setVisibility(View.VISIBLE);
		
		updateCategoriesView(Category.MIXER_TYPE);
	}
	
	/**
	 * Invokes when the spirits tab pressed
	 */
	private void cocktailsTabSelected() {
		spiritsTab.setBackgroundResource(R.drawable.tab_bg);
		mixersTab.setBackgroundResource(R.drawable.tab_bg);
		cocktailsTab.setBackgroundResource(R.drawable.tab_over);
		
		// Hide categories view
		categoryScrollView.setVisibility(View.GONE);
		
		updateCocktailView();
	}

	/**
	 * To update categories view based on the type
	 * 
	 * @param type
	 */
	private void updateCategoriesView(final String type) {
		
		selectedType = type;
		
		// To get categories from the database
		List<Category> categories = DatabaseManager.getInstance().getCategories(type);
		if(categories==null) return;
		
		Log.i("InventorySectionFragment", "About Categorieslist");
		
		// Make sure the list views are all empty
		categoriesList.removeAllViews();
		itemsLayout.removeAllViews();
		categoriesArrowViews.clear();
		
		Log.i("InventorySectionFragment", "categories list size = " + categories.size());
		
		// Add categories in the layout, one by one
		for (Category category : categories) {
		    final View categoryItem = inflater.inflate(R.layout.category_item, null);
		    TextView categoryTextView = (TextView) categoryItem.findViewById(R.id.categoryText);
		    categoryTextView.setText(category.getName());
		    
		    final ImageView arrowView = (ImageView) categoryItem.findViewById(R.id.arrowImg);
		    categoriesArrowViews.add(arrowView);
		    
		    categoriesList.addView(categoryItem);
		    
		    categoryItem.setTag(category);
		    
		    // Add listener for the category View
		    categoryItem.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					updateIngredientsView((Category)categoryItem.getTag());
					
					//To hide all category arrows
					hideCategoryArrows();
					arrowView.setVisibility(View.VISIBLE);
				}
			});
		}
	}
	/**
	 * To hide all categories arrow 
	 */
	private void hideCategoryArrows() {
		for (ImageView arrow : categoriesArrowViews) {
			arrow.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * It will trigger when the user pressed on any category.
	 * 
	 * All items are retrieved based on the category and updates the view
	 * 
	 * @param tag
	 */
	protected void updateIngredientsView(Category category){
		
		selectedCategory = category;
		
		// To get Ingredients from the database
		ingredients = DatabaseManager.getInstance().getIngredients(category);
		
		// Make sure the list views are all empty
		itemsLayout.removeAllViews();
		
		// Add Ingredients in the layout, one by onerow
		Log.i("InventorySectionFragment", "Ingredients list size = " + ingredients.size());
		int column = 2;
		TableRow row = null;
		
		for (Ingredient ingredient : ingredients) {
			// To make 3 columns for the table layout  
			if(column==2){
				row = new TableRow(getActivity());
				itemsLayout.addView(row);
				column=0;
			}
			else{
				column++;
			}
			// To create ingredient view
			final View itemView = inflater.inflate(R.layout.ingredient_item, null);
//			itemView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT));
		    TextView itemTextView = (TextView) itemView.findViewById(R.id.ingredientItemText);
		    itemTextView.setText(ingredient.getName());
		    ToggleButton availableButton = (ToggleButton)itemView.findViewById(R.id.availableButton);
		    availableButton.setChecked(ingredient.isAvailability());
		    
		    final Ingredient ingredientObj = ingredient;
		    // Add checked changed listener to the toggle button
		    availableButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					ingredientObj.setAvailability(isChecked);
				}
			});
		   
		    // To add Price layout to the item view
		    LinearLayout priceLayout = (LinearLayout) itemView.findViewById(R.id.priceLayout);
		    priceLayout.addView(getPriceLayout(String.valueOf(ingredient.getPrice()), ingredient));
		    
		    row.addView(itemView);
		    
		}
	}
	/**
	 * To create price layout
	 * 
	 * @return
	 */
	private View getPriceLayout(String price, final Object model){
		// Inflate the number stepper xml layout 
		final View priceView = inflater.inflate(R.layout.number_stepper, null);
		// Try to get all the UI elements from the layout
		final TextView priceValue = (TextView) priceView.findViewById(R.id.tvStepperValue);
		Button decrementButton = (Button) priceView.findViewById(R.id.btnDecrement);
		Button incrementButton = (Button) priceView.findViewById(R.id.btnIncrement);
		
		priceValue.setText(price);
		// To add listeners to the increase and decrease buttons
		decrementButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// To decrease the price value but it should not be negative number
				int price = 0;
				try {
					price = Integer.parseInt(priceValue.getText().toString());
				} catch (NumberFormatException e) {}
				
				if(price>0){
					price--;
					priceValue.setText(String.valueOf(price));
					updatePrice(model, price);
				}
			}
		});
		
		incrementButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// To increase the price value
				int price = 0;
				try {
					price = Integer.parseInt(priceValue.getText().toString());
				} catch (NumberFormatException e) {}
				
				price++;
				priceValue.setText(String.valueOf(price));
				updatePrice(model, price);
			}
		});
		
		return priceView;
	}
	
	/**
	 *  To update price in the model
	 * @param model
	 * @param price
	 */
	protected void updatePrice(Object model, int price) {
		
		if(model instanceof Ingredient){
			Ingredient ingredient = (Ingredient) model;
			ingredient.setPrice(price);
		}else if(model instanceof Cocktail){
			Cocktail cocktail = (Cocktail)model;
			cocktail.setPrice(price);
		}
	}

	/**
	 * It will trigger when the user pressed on any category.
	 * 
	 * All items are retrieved based on the category and updates the view
	 * 
	 * @param tag
	 */
	protected void updateCocktailView(){
		
		selectedCategory = null;
		
		// To get Cocktails from the database
		cocktails = DatabaseManager.getInstance().getCocktails();
		
		// Make sure the list views are all empty
		itemsLayout.removeAllViews();
		
		// Add Cocktail in the layout, one by one
		Log.i("InventorySectionFragment", "Cocktail list size = " + cocktails.size());
		
		int column = 2;
		TableRow row = null;
		
		for (Cocktail cocktail : cocktails) {
			// To make 3 columns for the table layout  
			if(column==2){
				row = new TableRow(getActivity());
				itemsLayout.addView(row);
				column=0;
			}
			else{
				column++;
			}
			final View itemView = inflater.inflate(R.layout.ingredient_item, null);
		    TextView itemTextView = (TextView) itemView.findViewById(R.id.ingredientItemText);
		    itemTextView.setText(cocktail.getName());
		    
//		    // To set more information in the view
//		    LinearLayout moreLayout = (LinearLayout)itemView.findViewById(R.id.moreInfoLayout);
//		    moreLayout.setVisibility(View.VISIBLE);
//		    
//		    TextView ingredientsText = (TextView) itemView.findViewById(R.id.ingredientsText);
//		    ingredientsText.setText(cocktail.getIngredients());
//		    
//		    TextView instructionsText = (TextView) itemView.findViewById(R.id.instructionsText);
//		    instructionsText.setText(cocktail.getInstructions());
		    
		    // To add Price layout to the item view
		    LinearLayout priceLayout = (LinearLayout) itemView.findViewById(R.id.priceLayout);
		    priceLayout.addView(getPriceLayout(String.valueOf(cocktail.getPrice()),cocktail));
		    
		    row.addView(itemView);
		}
	}
	
}
