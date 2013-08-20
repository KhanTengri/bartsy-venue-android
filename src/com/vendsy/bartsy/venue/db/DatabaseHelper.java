/**
 * @author Seenu Malireddy
 */
package com.vendsy.bartsy.venue.db;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.vendsy.bartsy.venue.model.Category;
import com.vendsy.bartsy.venue.model.Cocktail;
import com.vendsy.bartsy.venue.model.Ingredient;
import com.vendsy.bartsy.venue.model.Menu;

/**
 * Database helper class used to manage the creation and upgrading of your
 * database. This class also usually provides the DAOs used by the other
 * classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	// name of the database file for your application -- change to something
	// appropriate for your app
	private static final String DATABASE_NAME = "BartsyVenue.db";
	// any time you make changes to your database objects, you may have to
	// increase the database version
	private static final int DATABASE_VERSION = 2;
	// the DAO object we use to access the Ingredient table
	private Dao<Ingredient, Integer> ingredientDao = null;
	// the DAO object we use to access the Category table
	private Dao<Category, Integer> categoryDao = null;
	private Dao<Cocktail, Integer> cocktailDao;
	private Dao<Menu, Integer> menuDao;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * This is called when the database is first created. Usually you should
	 * call createTable statements here to create the tables that will store
	 * your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			
			TableUtils.createTable(connectionSource, Category.class);
			TableUtils.createTable(connectionSource, Ingredient.class);
			TableUtils.createTable(connectionSource, Cocktail.class);
			TableUtils.createTable(connectionSource, Menu.class);
			
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			// throw new RuntimeException(e);
		}

	}

	/**
	 * This is called when your application is upgraded and it has a higher
	 * version number. This allows you to adjust the various data to match the
	 * new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
			int oldVersion, int newVersion) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onUpgrade");

			TableUtils.dropTable(connectionSource, Ingredient.class, true);
			TableUtils.dropTable(connectionSource, Cocktail.class, true);
			TableUtils.dropTable(connectionSource, Category.class, true);
			TableUtils.dropTable(connectionSource, Menu.class, true);
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			// throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the Database Access Object (DAO) for our Ingredient class. It
	 * will create it or just give the cached value.
	 */
	public Dao<Ingredient, Integer> getIngredientDao()
			throws SQLException {
		if (ingredientDao == null) {
			ingredientDao = getDao(Ingredient.class);
		}
		return ingredientDao;
	}
	
	/**
	 * Returns the Database Access Object (DAO) for our Cocktail class. It
	 * will create it or just give the cached value.
	 */
	public Dao<Cocktail, Integer> getCocktailDao()
			throws SQLException {
		if (cocktailDao == null) {
			cocktailDao = getDao(Cocktail.class);
		}
		return cocktailDao;
	}
	
	/**
	 * Returns the Database Access Object (DAO) for our Section class. It
	 * will create it or just give the cached value.
	 */
	public Dao<Category, Integer> getSectionDao()
			throws SQLException {
		if (categoryDao == null) {
			categoryDao = getDao(Category.class);
		}
		return categoryDao;
	}
	
	/**
	 * Returns the Database Access Object (DAO) for our Menu class. It
	 * will create it or just give the cached value.
	 */
	public Dao<Menu, Integer> getMenuDao()
			throws SQLException {
		if (menuDao == null) {
			menuDao = getDao(Menu.class);
		}
		return menuDao;
	}

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
	}

}
