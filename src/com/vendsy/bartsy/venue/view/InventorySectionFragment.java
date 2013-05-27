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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vendsy.bartsy.venue.R;
import com.vendsy.bartsy.venue.db.DatabaseManager;
import com.vendsy.bartsy.venue.model.Category;
import com.vendsy.bartsy.venue.model.Ingredient;

/**
 * @author peterkellis
 *
 */
public class InventorySectionFragment extends Fragment {

	View mRootView = null;
	private LinearLayout categoriesList;
	private ArrayList<Ingredient> spiritsIngredients;
	private LayoutInflater inflater;
	private LinearLayout spiritsLayout;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
 
		this.inflater = inflater;
		
		mRootView = inflater.inflate(R.layout.inventory_main, container, false);
		
		updateInventoryView();
		
        return mRootView;
	}
	
	/***
	 * Updates the inventory view
	 */
	public void updateInventoryView() {
		
		Log.i("Bartsy", "InventorySectionFragment.updateOrdersView()");
		
		if (mRootView == null) return;
		
		updateCategoriesView(Category.SPIRITS_TYPE);
	}
	
	/**
	 * To update categories view based on the type
	 * 
	 * @param type
	 */
	private void updateCategoriesView(String type) {
		// To get categories from the database
		List<Category> categories = DatabaseManager.getInstance().getCategories(type);
		if(categories==null) return;
		
		categoriesList = (LinearLayout) mRootView.findViewById(R.id.categoryLayout);
		Log.i("InventorySectionFragment", "About Categorieslist");
		
		// Make sure the list views are all empty
		categoriesList.removeAllViews();
		
		// Add categories in the layout, one by one
		Log.i("InventorySectionFragment", "categories list size = " + categories.size());
		for (Category category : categories) {
		    final View categoryItem = inflater.inflate(R.layout.category_item, null);
		    TextView categoryTextView = (TextView) categoryItem.findViewById(R.id.categoryText);
		    categoryTextView.setText(category.getName());
		    
		    categoriesList.addView(categoryItem);
		    
		    categoryItem.setTag(category);
		    
		    // Add listerner for the 
		    categoryItem.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					updateIngredientsView((Category)categoryItem.getTag());
				}
			});
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
		
		// Add categories in the layout, one by one
		Log.i("InventorySectionFragment", "categories list size = " + ingredients.size());
		
		spiritsLayout = (LinearLayout) mRootView.findViewById(R.id.spiritsLayout);
		
		// Make sure the list views are all empty
		spiritsLayout.removeAllViews();
		
		final FlowLayout flowLayout = new FlowLayout(getActivity());
		spiritsLayout.addView(flowLayout);
		
		for (Ingredient ingredient : ingredients) {
			final View itemView = inflater.inflate(R.layout.ingredient_item, null);
			
		    TextView itemTextView = (TextView) itemView.findViewById(R.id.itemText);
		    itemTextView.setText(ingredient.getName());
		    
		    flowLayout.addView(itemView);
		}
	}
	
}
