package com.ceardannan.java2jpa;

/**
 * Enum that represents how a collection field of a class should be rendered.
 * 
 * @author Steffen Luypaert
 *
 */
public enum CollectionRenderType {
	/**
	 * Represents a many-to-many mapping
	 */
	MANYTOMANY, 
	/**
	 * Represents a one-to-many mapping
	 */
	ONETOMANY, 
	/**
	 * Represents a basic-collection mapping
	 */
	SIMPLE;
}
