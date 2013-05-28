/**
 * 
 */
package com.vendsy.bartsy.venue.view;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vendsy.bartsy.venue.R;
import com.vendsy.bartsy.venue.db.DatabaseManager;
import com.vendsy.bartsy.venue.model.Category;
import com.vendsy.bartsy.venue.model.Cocktail;
import com.vendsy.bartsy.venue.model.Ingredient;

/**
 * @author peterkellis
 * @author Seenu Malireddy
 */
public class InventorySectionFragment extends Fragment {

	View mRootView = null;
	private Button spiritsTab;
	private LinearLayout categoriesList;
	private LayoutInflater inflater;
	private LinearLayout itemsLayout;
	private ArrayList<ImageView> categoriesArrowViews = new ArrayList<ImageView>();
	private Button mixersTab;
	private Button cocktailsTab;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
 
		this.inflater = inflater;
		
		mRootView = inflater.inflate(R.layout.inventory_main, container, false);
		
		itemsLayout = (LinearLayout) mRootView.findViewById(R.id.spiritsLayout);
		categoriesList = (LinearLayout) mRootView.findViewById(R.id.categoryLayout);
		
		updateInventoryView();
		
        return mRootView;
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
		
		updateCategoriesView(Category.SPIRITS_TYPE);
	}
	
	/**
	 * Invokes when the spirits tab pressed
	 */
	private void mixersTabSelected() {
		spiritsTab.setBackgroundResource(R.drawable.tab_bg);
		mixersTab.setBackgroundResource(R.drawable.tab_over);
		cocktailsTab.setBackgroundResource(R.drawable.tab_bg);
		
		updateCategoriesView(Category.MIXER_TYPE);
	}
	
	/**
	 * Invokes when the spirits tab pressed
	 */
	private void cocktailsTabSelected() {
		spiritsTab.setBackgroundResource(R.drawable.tab_bg);
		mixersTab.setBackgroundResource(R.drawable.tab_bg);
		cocktailsTab.setBackgroundResource(R.drawable.tab_over);
		
		updateCategoriesView(Category.COCKTAILS_TYPE);
	}

	/**
	 * To update categories view based on the type
	 * 
	 * @param type
	 */
	private void updateCategoriesView(final String type) {
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
					
					if(type.equals(Category.COCKTAILS_TYPE)){
						updateCocktailView((Category)categoryItem.getTag());
					}else{
						updateIngredientsView((Category)categoryItem.getTag());
					}
					
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
		// To get Ingredients from the database
		List<Ingredient> ingredients = DatabaseManager.getInstance().getIngredients(category);
		
		// Make sure the list views are all empty
		itemsLayout.removeAllViews();
		
		final FlowLayout flowLayout = new FlowLayout(getActivity());
		itemsLayout.addView(flowLayout);
		
		// Add Ingredients in the layout, one by one
		Log.i("InventorySectionFragment", "Ingredients list size = " + ingredients.size());
		for (Ingredient ingredient : ingredients) {
			final View itemView = inflater.inflate(R.layout.ingredient_item, null);
			
		    TextView itemTextView = (TextView) itemView.findViewById(R.id.itemText);
		    itemTextView.setText(ingredient.getName());
		   
		    // To add Price layout to the item view
		    LinearLayout priceLayout = (LinearLayout) itemView.findViewById(R.id.priceLayout);
		    priceLayout.addView(getPriceLayout());
		    
		    flowLayout.addView(itemView);
		}
	}
	
	private View getPriceLayout(){
		// Inflate the number stepper xml layout 
		final View priceView = inflater.inflate(R.layout.number_stepper, null);
		// Try to get all the UI elements from the layout
		TextView priceValue = (TextView) priceView.findViewById(R.id.tvStepperValue);
		Button decrementButton = (Button) priceView.findViewById(R.id.btnDecrement);
		Button incrementButton = (Button) priceView.findViewById(R.id.btnIncrement);
		
		decrementButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO
			}
		});
		
		incrementButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO
			}
		});
		
		return priceView;
	}
	
	/**
	 * It will trigger when the user pressed on any category.
	 * 
	 * All items are retrieved based on the category and updates the view
	 * 
	 * @param tag
	 */
	protected void updateCocktailView(Category category){
		// To get Cocktails from the database
		List<Cocktail> cocktails = DatabaseManager.getInstance().getCocktails(category);
		
		// Make sure the list views are all empty
		itemsLayout.removeAllViews();
		
		final FlowLayout flowLayout = new FlowLayout(getActivity());
		itemsLayout.addView(flowLayout);
		
		// Add Cocktail in the layout, one by one
		Log.i("InventorySectionFragment", "Cocktail list size = " + cocktails.size());
		for (Cocktail cocktail : cocktails) {
			final View itemView = inflater.inflate(R.layout.ingredient_item, null);
			
		    TextView itemTextView = (TextView) itemView.findViewById(R.id.itemText);
		    itemTextView.setText(cocktail.getName());
		    
		    flowLayout.addView(itemView);
		}
	}
	
}
