
package com.vendsy.bartsy.venue.db;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;
import com.vendsy.bartsy.venue.model.Category;
import com.vendsy.bartsy.venue.model.Cocktail;
import com.vendsy.bartsy.venue.model.Ingredient;
import com.vendsy.bartsy.venue.model.Menu;

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
	 * To save Cocktail data in db
	 * 
	 * @param Cocktail
	 */
	public void saveCocktail(Cocktail cocktail) {
		try {
			dbHelper.getCocktailDao().createOrUpdate(cocktail);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * To delete Ingredient data in db
	 * 
	 * @param Ingredient
	 */
	public void deleteIngredient(Ingredient ingredient) {
		try {
			dbHelper.getIngredientDao().delete(ingredient);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * To delete Cocktail data in db
	 * 
	 * @param Cocktail
	 */
	public void deleteCocktail(Cocktail cocktail) {
		try {
			dbHelper.getCocktailDao().delete(cocktail);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Delete Menu data in db
	 * 
	 * @param Menu
	 */
	public void deleteMenu(Menu menu) {
		try {
			dbHelper.getMenuDao().delete(menu);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * To get list of categories based on the type
	 * 
	 * @see Category class contains the different types 
	 * 
	 * @param ingredient
	 */
	public List<Category> getCategories(String type) {
		try {
			QueryBuilder<Category, Integer> builder = dbHelper
					.getSectionDao().queryBuilder();
			builder.where().eq("type", type);
			
			PreparedQuery<Category> query = builder.prepare();
			return dbHelper.getSectionDao().query(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Get Menu based on the menu name
	 * 
	 * @param name
	 */
	public Menu getMenu(String name) {
		try {
			QueryBuilder<Menu, Integer> builder = dbHelper
					.getMenuDao().queryBuilder();
			builder.where().eq("name", name);
			
			PreparedQuery<Menu> query = builder.prepare();
			List<Menu> menuList = dbHelper.getMenuDao().query(query);
			
			if(menuList!=null && menuList.size()>0){
				return menuList.get(0);
			}
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
			QueryBuilder<Ingredient, Integer> builder = dbHelper
					.getIngredientDao().queryBuilder();
			builder.where().eq("category_id", category.getId());
			PreparedQuery<Ingredient> query = builder.prepare();
			
			return dbHelper.getIngredientDao().query(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * To get list of cocktails by category from the db by menu name
	 * 
	 * @param id
	 * @return
	 */
	public List<Cocktail> getCocktails(String menuName) {
		// Try to get menu data by using menu name 
		Menu menu = getMenu(menuName);
		if(menu==null){
			return null;
		}
		try {
			QueryBuilder<Cocktail, Integer> builder = dbHelper
					.getCocktailDao().queryBuilder();
			builder.where().eq("menu_id", menu.getId());
			
			PreparedQuery<Cocktail> query = builder.prepare();
			
			return dbHelper.getCocktailDao().query(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public List<Menu> getAllMenus() {
		// Try to get all menus
		try {
			return dbHelper.getMenuDao().queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 *  Delete all the records of the Ingredient table
	 */
	public void deleteAllIngredients(){
		try {
			TableUtils.clearTable(dbHelper.getConnectionSource(),
					Ingredient.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  Delete all the records of the Cocktails table based on menu name
	 */
	public void deleteAllCocktails(String menuName){
		try {
			// Try to get menu data by using menu name 
			Menu menu = getMenu(menuName);
			if(menu==null){
				return;
			}
			
			DeleteBuilder<Cocktail, Integer> db = dbHelper.getCocktailDao().deleteBuilder();
			db.where().eq("menu_id", menu.getId());
			dbHelper.getCocktailDao().delete(db.prepare());
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
	
	/**
	 * Save menu data in db
	 * 
	 * @param menu
	 */
	public void saveMenu(Menu menu) {
		try {
			dbHelper.getMenuDao().createOrUpdate(menu);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
