package com.vendsy.bartsy.venue.model;
/**
 * Category model to save in the DB
 * 
 * @author Seenu Malireddy
 */
import com.j256.ormlite.field.DatabaseField;

public class Category {
	
	@DatabaseField(generatedId = true) // Auto generated in local db
	private long id;
	@DatabaseField
	private String name;
	
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
	
	
}
