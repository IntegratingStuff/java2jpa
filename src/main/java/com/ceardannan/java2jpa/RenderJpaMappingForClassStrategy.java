package com.ceardannan.java2jpa;

import java.lang.reflect.Field;
import java.util.Collection;

/**
 * Represents a strategy to create jpa mappings from classes.
 * For most java domain models, a separate implementation is required.
 * 
 * @author Steffen Luypaert
 *
 */
public interface RenderJpaMappingForClassStrategy {

	/**
	 * Returns whether a class needs a jpa mapping(any of {@link ClassRenderType}).
	 * 
	 * @param clazz
	 * @return Whether the class needs a jpa mapping.
	 */
	public boolean classNeedsMapping(Class<?> clazz);
	/**
	 * Returns how a class should be mapped: any of {@link ClassRenderType}.
	 * 
	 * @param clazz
	 * @return how to map the class
	 */
	public ClassRenderType getRenderTypeFor(Class<?> clazz);
	
	/**
	 * Returns how the inheritance of the class should be mapped: any of {@link InheritanceMappingType}.
	 * 
	 * @param clazz
	 * @param allClassesToMap - list of all classes to map
	 * @return how to map the inheritance of the class
	 */
	public InheritanceMappingType getInheritanceMappingTypeForClass(Class<?> clazz, Collection<Class<?>> allClassesToMap);
	/**
	 * Returns how a collection field should be mapped: any of {@link CollectionRenderType}.
	 * 
	 * @param field - collection field to map for its class
	 * @return how to render the collection represented by the field
	 */
	public CollectionRenderType getCollectionRenderTypeForField(Field field);
	
	/**
	 * Returns the field that represents the database id for the given class
	 * 
	 * @param clazz
	 * @return the field that represents the id for the given class
	 */
	public Field getIdFieldForClass(Class<?> clazz);
	
	/**
	 * Returns whether a certain field requires a many-to-one mapping.
	 * 
	 * @param field
	 * @return whether a field requires a many-to-one mapping
	 */
	public boolean fieldNeedsManyToOneMapping(Field field);
	
}
