package com.test.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Category implements Serializable {
    private static final long serialVersionUID = -7454630869604964843L;

    private long id;
    private String categoryId;
    private String name;
    private String description;
    
    private List<Product> associatedProducts = new ArrayList<Product>();
    
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
	 * @return the categoryId
	 */
	public String getCategoryId() {
		return categoryId;
	}
	/**
	 * @param categoryId the categoryId to set
	 */
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the associatedProducts
	 */
	public List<Product> getAssociatedProducts() {
		return associatedProducts;
	}
	/**
	 * @param associatedProducts the associatedProducts to set
	 */
	public void setAssociatedProducts(List<Product> associatedProducts) {
		this.associatedProducts = associatedProducts;
	}
}
