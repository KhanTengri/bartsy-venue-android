package com.vendsy.bartsy.venue.model;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Cocktails model to save in the DB
 * 
 * @author Seenu Malireddy
 */
import com.j256.ormlite.field.DatabaseField;

public class Cocktail {
	
	@DatabaseField(generatedId = true) // Auto generated in local db
	private long id;
	@DatabaseField
	private String name;
	@DatabaseField
	private String category;
	@DatabaseField
	private String glassType;
	@DatabaseField
	private String alcohol;
	@DatabaseField
	private String ingredients;
	@DatabaseField
	private String instructions;
	@DatabaseField
	private String description;
	@DatabaseField
	private String shopping;
	@DatabaseField
	private int price;
	@DatabaseField
	private boolean availability;
	
	@DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true, columnName = "menu_id")
	protected Menu menu;
	
	/**
	 * It will return in the JSON format which is used in the web service call
	 * 
	 * @return
	 */
	public JSONObject toJSON(){
		JSONObject json = new JSONObject();
		try {
			json.put("menuId", String.valueOf(id));
			json.put("available", availability);
			json.put("name", name);
			json.put("price", String.valueOf(price));
			json.put("available", String.valueOf(availability));
			json.put("category", category);
			json.put("glass", glassType);
			json.put("alcohol", alcohol);
			json.put("ingredients", ingredients);
			json.put("instructions", instructions);
			json.put("shopping", shopping);
			json.put("description", description);
		} catch (JSONException e) {	}
		
		return json;
	}
	
	public Cocktail(){
		// TODO Category is empty for now
		category = "";
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
	 * @return the ingredients
	 */
	public String getIngredients() {
		return ingredients;
	}
	/**
	 * @param ingredients the ingredients to set
	 */
	public void setIngredients(String ingredients) {
		this.ingredients = ingredients;
	}
	/**
	 * @return the instructions
	 */
	public String getInstructions() {
		return instructions;
	}
	/**
	 * @param instructions the instructions to set
	 */
	public void setInstructions(String instructions) {
		this.instructions = instructions;
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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getGlassType() {
		return glassType;
	}

	public void setGlassType(String glassType) {
		this.glassType = glassType;
	}

	public String getAlcohol() {
		return alcohol;
	}

	public void setAlcohol(String alcohol) {
		this.alcohol = alcohol;
	}

	public String getShopping() {
		return shopping;
	}

	public void setShopping(String shopping) {
		this.shopping = shopping;
	}

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
