package com.vendsy.bartsy.venue.model;
import org.json.JSONException;
import org.json.JSONObject;

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
	@DatabaseField
	private boolean availability;
	
	@DatabaseField(canBeNull = true, foreign = true, foreignAutoRefresh = true, columnName = "category_id")
	private Category category;
	
	/**
	 * It will return in the JSON format which is used in the web service call
	 * 
	 * @return
	 */
	public JSONObject toJSON(){
		JSONObject json = new JSONObject();
		try {
			json.put("ingredientId", String.valueOf(id));
			json.put("name", name);
			json.put("price", String.valueOf(price));
			json.put("available", String.valueOf(availability));
		} catch (JSONException e) {	}
		
		return json;
	}
	
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
	/**
	 * @return the availability
	 */
	public boolean isAvailability() {
		return availability;
	}
	/**
	 * @param availability the availability to set
	 */
	public void setAvailability(boolean availability) {
		this.availability = availability;
	}
	
	
}
