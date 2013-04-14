package com.ceardannan.java2jpa;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;

import com.ceardannan.util.ReflectionUtil;
import com.ceardannan.util.StringUtil;

/**
 * This generator generates JPA Mappings for a given set of Java (domain) classes.
 * The renderJpaMappingForClassStrategy and jpaMappingRenderer property be set before using an instance of this class.
 * 
 * <p>Example usage:
 * 
 * <pre>
 * {@code
 * Java2JpaMappingGenerator java2JpaMappingGenerator = new Java2JpaMappingGenerator();
 * java2JpaMappingGenerator.setRenderJpaMappingForClassStrategy(new RenderJpaMappingForClassStrategyDefaultImpl());
 * JpaMappingRenderer jpaMappingRenderer = new JpaMappingRendererDefaultImpl("orm.xml");
 * java2JpaMappingGenerator.setJpaMappingRenderer(jpaMappingRenderer);
 * java2JpaMappingGenerator.generateJpaMappingsForPackages("com.test.model");
 * jpaMappingRenderer.createFiles();
 * }
 * </pre>
 * </p>
 * 
 * @author Steffen Luypaert
 *
 */
public class Java2JpaMappingGenerator {

	private transient Logger log = Logger.getLogger(this.getClass());

	/**
	 * The jpaMappingRenderer
	 */
	private JpaMappingRenderer jpaMappingRenderer;
	
	/**
	 * The renderJpaMappingForClassStrategy
	 */
	private RenderJpaMappingForClassStrategy renderJpaMappingForClassStrategy;
	
	/**
	 * Returns the jpaMappingRenderer
	 * 
	 * @return the jpaMappingRenderer
	 */
	public JpaMappingRenderer getJpaMappingRenderer() {
		return jpaMappingRenderer;
	}
	/**
	 * Sets the jpaMappingRenderer
	 * 
	 * @param jpaMappingRenderer
	 */
	public void setJpaMappingRenderer(JpaMappingRenderer jpaMappingRenderer) {
		this.jpaMappingRenderer = jpaMappingRenderer;
	}
	/**
	 * Sets the renderJpaMappingForClassStrategy
	 * 
	 * @param renderJpaMappingForClassStrategy
	 */
	public void setRenderJpaMappingForClassStrategy(
			RenderJpaMappingForClassStrategy renderJpaMappingForClassStrategy) {
		this.renderJpaMappingForClassStrategy = renderJpaMappingForClassStrategy;
	}
	
	/**
	 * Generates JPA Mappings for all classes (for which a JPA Mapping should be created) in the given packages.
	 * 
	 * @param packageNames - names of the packages for which' classes jpa mappings need to be created
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public void generateJpaMappingsForPackages(String... packageNames)
			throws ClassNotFoundException, IOException {
		Collection<Class<?>> allClasses = ReflectionUtil
				.getAllClassesIn(packageNames);
		generateJpaMappingsForPackages(allClasses);
	}

	/**
	 * Generates JPA Mappings for all classes for which a JPA Mapping should be created.
	 * 
	 * @param classes - complete list of classes that will be mapped
	 */
	public void generateJpaMappingsForPackages(Collection<Class<?>> classes) {
		log.debug("Generating mappings..");

		// for each class
		for (Class<?> clazz : classes) {
			addJpaMappingForClass(clazz, classes);
		}
	}

	/**
	 * Adds a JPA Mapping for a class(to the JpaMappingRenderer), if necessary.
	 * Whether rendering is necessary is determined by calling the classNeedsMapping(Class) method 
	 * of the renderJpaMappingForClassStrategy for the given class.
	 * 
	 * @param clazz - the class to (if necessary) add a jpa mapping for.
	 * @param allClassesToMap - complete list of classes that will be mapped
	 */
	protected void addJpaMappingForClass(Class<?> clazz,
			Collection<Class<?>> allClassesToMap) {
		if (renderJpaMappingForClassStrategy.classNeedsMapping(clazz)) {
			log.debug("Creating mapping for class: " + clazz.getName());
			renderClass(clazz, allClassesToMap);
		} else {
			log.info("Ignoring class: " + clazz.getName());
		}
	}

	/**
	 * Returns whether or not a given class should be mapped as an entity.
	 * 
	 * @param clazz - class which should be checked on whether it will be mapped as an entity or not.
	 * @param allClassesToMap - complete list of classes that will be mapped
	 * @return whether or not the class should be mapped as an entity
	 */
	private boolean classWillBeMappedAsEntity(Class<?> clazz, Collection<Class<?>> allClassesToMap){
		return allClassesToMap.contains(clazz) && ClassRenderType.ENTITY.equals(renderJpaMappingForClassStrategy.getRenderTypeFor(clazz));
	}
	
	/**
	 * Returns a joinTableName for a class pair.
	 * 
	 * @param class1 - first class of the class pair to create a join table for
	 * @param class2 - second class of the class pair to create a join table for
	 * @return joinTableName
	 */
	private String getJoinTableName(Class<?> class1, Class<?> class2) {
		if (class1.getSimpleName().compareToIgnoreCase(class2.getSimpleName()) == 1) {
			return StringUtil.javaClassNameToDbName(class1.getSimpleName())
					+ "_"
					+ StringUtil.javaClassNameToDbName(class2.getSimpleName());
		} else {
			return StringUtil.javaClassNameToDbName(class2.getSimpleName())
					+ "_"
					+ StringUtil.javaClassNameToDbName(class1.getSimpleName());
		}
	}

	/**
	 * Renders a field (in the attributes section of a class) as a property in the jpa mapping.
	 * This implementation first determines the classRenderType of the objectField class using the renderJpaMappingForClassStrategy.
	 * Based on the classRenderType, the field is either rendered as an embedded attribute or a many-to-one.
	 * 
	 * @param objectField - the field to render in the jpa mapping
	 * @param allClassesToMap - Collection of all classes for which a jpa mapping will be created. Used to reflect on relations of these classes with the class to map.
	 */
	protected void renderObjectReference(Field objectField, Collection<Class<?>> allClassesToMap) {
		Class<?> fieldClass = objectField.getType();
		if (ClassRenderType.EMBEDDABLE.equals(renderJpaMappingForClassStrategy
				.getRenderTypeFor(fieldClass))) {
			jpaMappingRenderer.renderEmbeddedAttributeForClass(objectField.getDeclaringClass(), objectField);
		} else {
			if (! classWillBeMappedAsEntity(fieldClass, allClassesToMap)){
				log.warn("Type " + fieldClass.getName() + " of property of " + objectField.getName() + " on class " + objectField.getDeclaringClass().getName() + " not mapped. Manual intervention on the mapping will be required.");
			}
			jpaMappingRenderer.renderManyToOneAttributeForClass(objectField.getDeclaringClass(), objectField, "LAZY",  
					StringUtil.javaClassNameToDbName(objectField.getName()) + "_ID");
		}
	}

	/**
	 * Renders a field (in the attributes section of a class) as a collection in the jpa mapping.
	 * This implementation first determines the collectionRenderType of the collectionField using the renderJpaMappingForClassStrategy.
	 * Based on the collectionRenderType, the collection is either rendered a a basic-collection, a many-to-many or a one-to-many.
	 * 
	 * @param collectionField - the field to render in the jpa mapping
	 * @param allClassesToMap - Collection of all classes for which a jpa mapping will be created. Used to reflect on relations of these classes with the class to map.
	 */
	protected void renderCollection(Field collectionField, Collection<Class<?>> allClassesToMap) {
		CollectionRenderType collectionRenderType = renderJpaMappingForClassStrategy
				.getCollectionRenderTypeForField(collectionField);
		if (collectionRenderType != null) {
			ParameterizedType collectionListType = (ParameterizedType) collectionField
					.getGenericType();
			Class<?> otherClass = (Class<?>) collectionListType
					.getActualTypeArguments()[0];
			Class<?> classBeingRendered = collectionField
					.getDeclaringClass();
			switch (collectionRenderType) {
			case SIMPLE:
				jpaMappingRenderer.renderSimpleCollectionAttributeForClass(collectionField.getDeclaringClass(), collectionField, 
						StringUtil.javaClassNameToDbName(collectionField.getName()), 
						StringUtil.javaClassNameToDbName(classBeingRendered.getSimpleName()) + "_"
							+ StringUtil.javaClassNameToDbName(collectionField.getName()));
				break;
			case MANYTOMANY:
				if (! classWillBeMappedAsEntity(otherClass, allClassesToMap)){
					log.warn("Type " + otherClass.getName() + " of elements of mapped collection " + collectionField.getName() + " on class " + classBeingRendered.getName() + " not mapped. Manual intervention on the mapping will be required.");
				}
				jpaMappingRenderer.renderManyToManyAttributeForClass(collectionField.getDeclaringClass(), collectionField, 
						getJoinTableName(classBeingRendered,otherClass), 
						StringUtil.javaClassNameToDbName(classBeingRendered.getSimpleName())+ "_ID", 
						StringUtil.javaClassNameToDbName(otherClass.getSimpleName()) + "_ID");
				break;
			case ONETOMANY:
				if (! classWillBeMappedAsEntity(otherClass, allClassesToMap)){
					log.warn("Type " + otherClass.getName() + " of elements of mapped collection " + collectionField.getName() + " on class " + classBeingRendered.getName() + " not mapped. Manual intervention on the mapping will be required.");
				}
				List<Field> possibleFields = ReflectionUtil
						.getAllFieldsOfTypeClassOnOtherClass(
								classBeingRendered, otherClass);
				if (possibleFields.size() > 1) {
					log.warn("Multiple fields of type "
							+ classBeingRendered
							+ " found on " + otherClass + ". Using first one("
							+ possibleFields.get(0).getName()
							+ ") to map collection "
							+ collectionField.getName() + " on "
							+ classBeingRendered + ".");
				}
				Field mappedByField = possibleFields.get(0);
				jpaMappingRenderer.renderOneToManyAttributeForClass(collectionField.getDeclaringClass(), collectionField, mappedByField.getName());
				break;
			default:
				throw new NotImplementedException(
						"Rendering for collectionRenderType "
								+ collectionRenderType + " not implemented.");
			}
		}
		else {
			//render collection as transient
			jpaMappingRenderer.renderTransientAttributeForClass(collectionField.getDeclaringClass(), collectionField.getName());
		}
	}

	/**
	 * Renders a field (in the attributes section of a class) in the jpa mapping.
	 * This implementation currently maps these fields: ids, java.util.Collections and object references.
	 * Mapping arrays and java.util.Maps currently not supported.
	 * Whether a field is an id is decided by the getIdFieldForClass<?> of the renderJpaMappingForClassStrategy.
	 * Since most fields (primitives, basic Java types, enums,..) are automapped, they are not added explicitly to the mapping.
	 * 
	 * @param field - the field to render in the jpa mapping
	 * @param allClassesToMap - Collection of all classes for which a jpa mapping will be created. Used to reflect on relations of these classes with the class to map.
	 */
	protected void renderField(Field field, Collection<Class<?>> allClassesToMap) {
			// if field is id: map field as id
			if (field.equals(renderJpaMappingForClassStrategy.getIdFieldForClass(field.getDeclaringClass()))) {
				if (ReflectionUtil.isSimpleClass(field.getType())){
					jpaMappingRenderer.renderIdForClass(field.getDeclaringClass(), field, "AUTO");
				}
				else {
					ClassRenderType renderType = renderJpaMappingForClassStrategy.getRenderTypeFor(field.getType());
					if (! ClassRenderType.EMBEDDABLE.equals(renderType)){
						log.warn("Id " + field.getName() + " of " + field.getDeclaringClass() + " is not a simple class or an embeddable. Still mapping it as an embedded-id, but manual intervention for this mapping will be required.");
					}
					jpaMappingRenderer.renderEmbeddedIdForClass(field.getDeclaringClass(), field);
				}
			}
			// render collections
			else if (Collection.class.isAssignableFrom(field.getType())) {
				renderCollection(field,allClassesToMap);
			}
			// render maps
			else if (Map.class.isAssignableFrom(field.getType())) {
				log.warn("Mapping java.util.Maps currently not supported. Hence, cannot map "
						+ field.getName()
						+ " java.util.Map field of class "
						+ field.getDeclaringClass() + ".");
			} else if (field.getType().isArray()) {
				log.warn("Mapping arrays currently not supported. Hence, cannot map "
						+ field.getName()
						+ " array field of class "
						+ field.getDeclaringClass() + ".");
			}
			// render if object reference
			else if (renderJpaMappingForClassStrategy
					.fieldNeedsManyToOneMapping(field)) {
				renderObjectReference(field,allClassesToMap);
			}
	}

	/**
	 * Renders the attributes(columns) section of a given class.
	 * 
	 * @param clazz - the class to create an attributes section for
	 * @param allClassesToMap - Collection of all classes for which a jpa mapping will be created. Used to reflect on relations of these classes with the class to map.
	 */
	protected void renderAttributes(Class<?> clazz, Collection<Class<?>> allClassesToMap) {
		List<Field> fieldsOfClass = ReflectionUtil.getAllDeclaredFieldsFor(clazz);

		for (Field field : fieldsOfClass) {
			renderField(field,allClassesToMap);
		}
		
		for (String transientPropertyName: ReflectionUtil.getNonFieldReadablePropertyNamesOfClass(clazz)){
			jpaMappingRenderer.renderTransientAttributeForClass(clazz, transientPropertyName);
		}
	}

	/**
	 * Renders the given class as an mappedSuperclass.
	 * 
	 * @param clazz - the class to create a jpa mapping for
	 * @param allClassesToMap - Collection of all classes for which a jpa mapping will be created. Used to reflect on relations of these classes with the class to map.
	 */
	protected void renderMappedSuperclass(Class<?> clazz, Collection<Class<?>> allClassesToMap) {
		jpaMappingRenderer.renderClassAsMappedSuperclass(clazz);
		renderAttributes(clazz,allClassesToMap);
	}

	/**
	 * Renders the given class as an embeddable.
	 * 
	 * @param clazz - the class to create a jpa mapping for
	 * @param allClassesToMap - Collection of all classes for which a jpa mapping will be created. Used to reflect on relations of these classes with the class to map.
	 */
	protected void renderEmbeddable(Class<?> clazz, Collection<Class<?>> allClassesToMap) {
		jpaMappingRenderer.renderClassAsEmbeddable(clazz);
		renderAttributes(clazz,allClassesToMap);
	}

	/**
	 * Renders the given class as an entity.
	 * Not only the attributes are rendered.
	 * The inheritance hierarchy of the class is checked, and based on these checks, inheritance related jpa elements are added.
	 * Currently, only SINGLE_TABLE is supported as an inheritance strategy.
	 * 
	 * @param clazz - the class to create a jpa mapping for
	 * @param allClassesToMap - Collection of all classes for which a jpa mapping will be created. Used to reflect on relations of these classes with the class to map.
	 */
	protected void renderEntity(Class<?> clazz, Collection<Class<?>> allClassesToMap) {
		jpaMappingRenderer.renderClassAsEntity(clazz);

		InheritanceMappingType inheritanceMappingType = renderJpaMappingForClassStrategy
				.getInheritanceMappingTypeForClass(clazz, allClassesToMap);
		switch (inheritanceMappingType) {
		case SUBCLASS_WITH_DISCRIMINATOR:
			jpaMappingRenderer.addDiscriminatorValueElementForClass(clazz, StringUtil.javaClassNameToDbName(clazz.getSimpleName()));
			break;
		case NONE:
			jpaMappingRenderer.addTableElementForClass(clazz, StringUtil.javaClassNameToDbName(clazz.getSimpleName()));
			break;
		case NO_SUBCLASS_BUT_IS_SUPERCLASS_SINGLE_TABLE:
			jpaMappingRenderer.addTableElementForClass(clazz, StringUtil.javaClassNameToDbName(clazz.getSimpleName()));
			jpaMappingRenderer.addInheritanceStrategyElementForClass(clazz, "SINGLE_TABLE");
			jpaMappingRenderer.addDiscriminatorValueElementForClass(clazz, StringUtil.javaClassNameToDbName(clazz.getSimpleName()));
			jpaMappingRenderer.addDiscriminatorColumnElementForClass(clazz, "DISCRIMINATOR");
			break;
		default:
			throw new NotImplementedException(
					"Rendering for inheritanceMappingType "
							+ inheritanceMappingType + " not implemented.");
		}
		renderAttributes(clazz,allClassesToMap);
	}

	/**
	 * Renders (the jpa mapping) for a given class.
	 * First, the renderType(mappedSuperclass, embaddable or entity) is determined.
	 * Then the mapping for the class is rendered given the determined renderType.
	 * 
	 * @param clazz - the class to create a jpa mapping for
	 * @param allClassesToMap - Collection of all classes for which a jpa mapping will be created. Used to reflect on relations of these classes with the class to map.
	 */
	protected void renderClass(Class<?> clazz, Collection<Class<?>> allClassesToMap) {
		ClassRenderType renderType = renderJpaMappingForClassStrategy
					.getRenderTypeFor(clazz);
		switch (renderType) {
		case MAPPEDSUPERCLASS:
			renderMappedSuperclass(clazz, allClassesToMap);
			break;
		case EMBEDDABLE:
			renderEmbeddable(clazz, allClassesToMap);
			break;
		case ENTITY:
			renderEntity(clazz, allClassesToMap);
			break;
		default:
			throw new NotImplementedException("Rendering for renderType "
					+ renderType + " not implemented.");
		}
	}
}
