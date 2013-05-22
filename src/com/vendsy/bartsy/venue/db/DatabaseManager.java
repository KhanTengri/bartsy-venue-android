
package com.vendsy.bartsy.venue.db;

import java.sql.SQLException;

import android.content.Context;

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
	 * @param ingredient
	 */
	public void saveIngredient(Ingredient ingredient) {
		try {
			dbHelper.getIngredientDao().createOrUpdate(ingredient);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
//	/**
//	 * To save section data in db
//	 * 
//	 * @param category
//	 */
//	public void saveSection(Category category) {
//		try {
//			dbHelper.getSectionDao().createOrUpdate(category);
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
	
}
