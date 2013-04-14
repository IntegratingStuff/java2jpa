package com.ceardannan.java2jpa;

/**
 * Enum that represents how a class should be rendered.
 * 
 * @author Steffen Luypaert
 *
 */
public enum ClassRenderType {
	/**
	 * Represents a mapped-superclass rendering
	 */
	MAPPEDSUPERCLASS,
	/**
	 * Represents an embeddable rendering
	 */
	EMBEDDABLE,
	/**
	 * Represents an entity rendering
	 */
	ENTITY;
}
