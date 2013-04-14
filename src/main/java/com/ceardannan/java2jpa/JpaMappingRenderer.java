package com.ceardannan.java2jpa;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * This interface is responsible for rendering JPA mappings for classes.<br/>
 * Implementations usually build an internal representation of the mapping per class,
 * as the different rendering methods of the interface are called.<br/>
 * After constructing the internal representation, 
 * the mapped files can be fetched as a map(getMappedFilesAsStringMap), or created on disk(createFiles).
 * <br/><br/>
 * Note that the rendering calls are meant to be very low level.<br/>
 * For example, a renderClassAsEntity call only indicates that the class should be mapped as an entity.
 * It should not add/register a table element for the mapping for example
 * (because not all entity mappings, f.e. subclass mappings, need such a table element).
 * 
 * @author Steffen Luypaert
 *
 */
public interface JpaMappingRenderer {

	//GENERAL MAPPING RELATED METHODS
	/**
	 * Renders the class as a mapped-superclass.
	 * 
	 * @param clazz - the class to render
	 */
	public void renderClassAsMappedSuperclass(Class<?> clazz);
	/**
	 * Renders the class as an embeddable.
	 * 
	 * @param clazz - the class to render
	 */
	public void renderClassAsEmbeddable(Class<?> clazz);
	/**
	 * Renders the class as an entity.
	 * 
	 * @param clazz - the class to render
	 */
	public void renderClassAsEntity(Class<?> clazz);
	
	//INHERITANCE RELATED METHODS
	/**
	 * Adds an inheritance-strategy element to the mapping of the class, with the given inheritanceStrategyName.
	 * 
	 * @param clazz - the class to render
	 * @param inheritanceStrategyName - name of the inheritance strategy, f.e. SINGLE_TABLE
	 */
	public void addInheritanceStrategyElementForClass(Class<?> clazz, String inheritanceStrategyName);
	/**
	 * Adds a table element to the mapping of the class, with the given tableName.
	 * 
	 * @param clazz - the class to render
	 * @param tableName - name of the table for the class
	 */
	public void addTableElementForClass(Class<?> clazz, String tableName);
	/**
	 * Adds a discriminator-column element to the mapping of the class, with the given discriminatorColumnName.
	 * 
	 * @param clazz - the class to render
	 * @param discriminatorColumnName - name of the discriminator column
	 */
	public void addDiscriminatorColumnElementForClass(Class<?> clazz, String discriminatorColumnName);
	/**
	 * Adds a discriminator-value element to the mapping of the class, with the given discriminatorValue.
	 * 
	 * @param clazz - the class to render
	 * @param discriminatorValue - discriminator value of the class
	 */
	public void addDiscriminatorValueElementForClass(Class<?> clazz, String discriminatorValue);
	
	//ATTRIBUTES RELATED METHODS
	/**
	 * Adds an transient mapping to the (attributes section of the) mapping of the class for the given transientPropertyName.
	 * 
	 * @param clazz - the class that is being rendered
	 * @param transientPropertyName - name of the transient property
	 */
	public void renderTransientAttributeForClass(Class<?> clazz, String transientPropertyName);
	
	/**
	 * Adds an embedded-id mapping to the (attributes section of the) mapping of the class for the given embeddedIdField.
	 * 
	 * @param clazz - the class that is being rendered
	 * @param embeddedIdFieldd - field to add an embedded-id attribute for
	 */
	public void renderEmbeddedIdForClass(Class<?> clazz, Field embeddedIdField);
	/**
	 * Adds an id mapping to the (attributes section of the) mapping of the class for the given IdField, 
	 * with a generated-value-strategy element with the given generatedValueStrategyName.
	 * 
	 * @param clazz - the class that is being rendered
	 * @param idField - field to add an id attribute for
	 * @param generatedValueStrategyName - name of the generated value strategy
	 */
	public void renderIdForClass(Class<?> clazz, Field idField, String generatedValueStrategyName);
	/**
	 * Adds an embedded mapping to the (attributes section of the) mapping of the class for the given embeddedField.
	 * 
	 * @param clazz - the class that is being rendered
	 * @param embeddedField - field to add an embedded attribute for
	 */
	public void renderEmbeddedAttributeForClass(Class<?> clazz, Field embeddedField);
	/**
	 * Adds a many-to-one mapping to the (attributes section of the) mapping of the class for the given manyToOneField,
	 * given the fetchType and joinColumnName.
	 * 
	 * @param clazz - the class that is being rendered
	 * @param manyToOneField - field to add a many-to-one attribute for
	 * @param fetchType - the fetchType, e.g. "LAZY"
	 * @param joinColumnName - the join column name
	 */
	public void renderManyToOneAttributeForClass(Class<?> clazz, Field manyToOneField, String fetchType, String joinColumnName);
	
	/**
	 * Adds a basic-collection mapping to the (attributes section of the) mapping of the class for the given collectionField,
	 * using the given valueColumnName and collectionTableName.
	 * 
	 * @param clazz - the class that is being rendered
	 * @param collectionField - field to add a basic-collection attribute for
	 * @param valueColumnName - name of the value column
	 * @param collectionTableName - name of the collection table
	 */
	public void renderSimpleCollectionAttributeForClass(Class<?> clazz, Field collectionField, String valueColumnName, String collectionTableName);
	/**
	 * Adds a many-to-many mapping to the (attributes section of the) mapping of the class for the given collectionField,
	 * using the given joinTableName, joinColumnName and inverseJoinColumnName.
	 * 
	 * @param clazz - the class that is being rendered
	 * @param collectionField - field to add a many-to-many attribute for
	 * @param joinTableName - name of the join table
	 * @param joinColumnName - name of the join column
	 * @param inverseJoinColumnName - name of the inverse join column
	 */
	public void renderManyToManyAttributeForClass(Class<?> clazz, Field collectionField, String joinTableName, String joinColumnName, String inverseJoinColumnName);
	/**
	 * Adds a one-to-many mapping to the (attributes section of the) mapping of the class for the given collectionField,
	 * using the given mappedByFieldName.
	 * 
	 * @param clazz - the class that is being rendered
	 * @param collectionField  - field to add a one-to-many attribute for
	 * @param mappedByFieldName - name of the mappedBy field
	 */
	public void renderOneToManyAttributeForClass(Class<?> clazz, Field collectionField, String mappedByFieldName);
	
	//OUTPUT RELATED METHODS
	/**
	 * Returns the files to generate as a map, in which the keys represent the filename, 
	 * and the value for a key represents the string content for that file.
	 * 
	 * @return the map of mapped files, in which the keys represent the filenames, and the values the string content for the file.
	 */
	public Map<String,String> getMappedFilesAsStringMap();
	/**
	 * Materializes(actually creates) the files to generate on disk.
	 * 
	 * @throws IOException
	 */
	public void createMappedFiles() throws IOException;
	
}
