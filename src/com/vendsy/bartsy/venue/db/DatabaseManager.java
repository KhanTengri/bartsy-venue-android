
package com.vendsy.bartsy.venue.db;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.vendsy.bartsy.venue.model.Category;
import com.vendsy.bartsy.venue.model.Ingredient;

/**
 * @author Seenu Malireddy
 */
public class DatabaseManager {

	private static DatabaseManager manager;

	private DatabaseHelper dbHelper;

	private DatabaseManager(Context context) {
		dbHelper = new DatabaseHelper(context);
		manager = this;
	}

	public static DatabaseManager getNewInstance(Context context) {
		if (manager == null) {
			manager = new DatabaseManager(context);
		}
		return manager;
	}

	public static DatabaseManager getInstance() {

		return manager;
	}
	/**
	 * To save Ingredient data in db
	 * 
	 * @param ingredient
	 */
	public void saveIngredient(Ingredient ingredient) {
		try {
			dbHelper.getIngredientDao().createOrUpdate(ingredient);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * To get list of categories based on the type
	 * 
	 * @param ingredient
	 */
	public List<Category> getCategories(String type) {
		try {
			QueryBuilder<Category, Integer> surveyQb = dbHelper
					.getSectionDao().queryBuilder();
			surveyQb.where().eq("type", type);
			
			PreparedQuery<Category> query = surveyQb.prepare();
			return dbHelper.getSectionDao().query(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * To get list of ingredients by category from the db
	 * 
	 * @param id
	 * @return
	 */
	public List<Ingredient> getIngredients(Category category) {
		try {
			QueryBuilder<Ingredient, Integer> surveyQb = dbHelper
					.getIngredientDao().queryBuilder();
			surveyQb.where().eq("category_id", category.getId());
			PreparedQuery<Ingredient> query = surveyQb.prepare();
			
			return dbHelper.getIngredientDao().query(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * To save section data in db
	 * 
	 * @param category
	 */
	public void saveSection(Category category) {
		try {
			dbHelper.getSectionDao().createOrUpdate(category);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
