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
	private String category;
	@DatabaseField
	private String type;
	
	
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
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
}
