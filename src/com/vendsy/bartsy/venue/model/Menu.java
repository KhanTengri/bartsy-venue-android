package com.vendsy.bartsy.venue.model;

import com.j256.ormlite.field.DatabaseField;
/**
 * 
 * @author Seenu Malireddy
 *
 */
public class Menu {
	
	@DatabaseField(generatedId = true)
	private long id;
	@DatabaseField
	private String name;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
