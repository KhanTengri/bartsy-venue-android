package com.vendsy.bartsy.venue.model;
/**
 * Ingredient model to save in the DB
 * 
 * @author Seenu Malireddy
 */
import com.j256.ormlite.field.DatabaseField;

public class Ingredient {
	
	@DatabaseField(generatedId = true) // Auto generated in local db
	private long id;
	@DatabaseField
	private String name;
	@DatabaseField
	private int price;
	
	@DatabaseField(canBeNull = true, foreign = true, foreignAutoRefresh = true, columnName = "category_id")
	private Category category;
	
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the price
	 */
	public int getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(int price) {
		this.price = price;
	}
	/**
	 * @return the category
	 */
	public Category getCategory() {
		return category;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(Category category) {
		this.category = category;
	}
	
	
}
