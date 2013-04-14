package com.ceardannan.java2jpa;

/**
 * Enum that represents how the inheritance of a class should be rendered.
 * 
 * @author Steffen Luypaert
 *
 */
public enum InheritanceMappingType {
	/**
	 * Represents that no inheritance hierarchy for the class needs to be mapped.
	 */
	NONE, 
	/**
	 * Represents that the class should be mapped as not being a subclass but as an entity that is a superclass of other entities.
	 * Single_table inheritance strategy.
	 */
	NO_SUBCLASS_BUT_IS_SUPERCLASS_SINGLE_TABLE,
	/**
	 * Represents that the class should be mapped as a subclass with a discriminator value.
	 */
	SUBCLASS_WITH_DISCRIMINATOR;
}
