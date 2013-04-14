package com.ceardannan.java2jpa;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.ceardannan.util.ReflectionUtil;

/**
 * Default implementation of the RenderJpaMappingForClassStrategy interface.
 * 
 * @author Steffen Luypaert
 *
 */
public class RenderJpaMappingForClassStrategyDefaultImpl implements RenderJpaMappingForClassStrategy{

	/**
	 * the log
	 */
	private transient Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * Default implementation of {@link RenderJpaMappingForClassStrategy#classNeedsMapping(Class)}
	 * Needs mapping if top level class, if not interface and if not enum.
	 */
	@Override
	public boolean classNeedsMapping(Class<?> clazz) {
		return (! clazz.isEnum()) && (! clazz.isInterface()) && (! clazz.isLocalClass()) && (! clazz.isAnonymousClass()) && (! clazz.isMemberClass());
	}
	
	/**
	 *  Default implementation of {@link RenderJpaMappingForClassStrategy#getRenderTypeFor(Class)}<br/>
	 *  If {@link RenderJpaMappingForClassStrategy#classNeedsMapping(Class)} returns false for clazz, return null.
	 *  If clazz is abstract, return MAPPEDSUPERCLASS.
	 *  If {@link RenderJpaMappingForClassStrategy#getIdFieldForClass(Class)} returns a value for clazz, return ENTITY.
	 *  Else return EMBEDDABLE.
	 */
	@Override
	public ClassRenderType getRenderTypeFor(Class<?> clazz) {
		if (classNeedsMapping(clazz)){
			if (Modifier.isAbstract(clazz.getModifiers())){
				return ClassRenderType.MAPPEDSUPERCLASS;
			}
			else {
				if (getIdFieldForClass(clazz) != null){
					return ClassRenderType.ENTITY;
				}
				else {
					return ClassRenderType.EMBEDDABLE;
				}
			}
		}
		else {
			return null;
		}
	}

	/**
	 *  Default implementation of {@link RenderJpaMappingForClassStrategy#getIdFieldForClass(Class)}<br/>
	 *  If clazz has a field with name "id" return that field.
	 *  Otherwise, if class has field with name "&gt;simpleClassName&gt;+Id" return that field.
	 */
	@Override
	public Field getIdFieldForClass(Class<?> clazz){
		List<Field> allFields = ReflectionUtil.getAllFieldsFor(clazz);
		List<Field> potentialOtherIdFields = new ArrayList<Field>();
		for (Field field: allFields){
			if (field.getName().toLowerCase().equals("id")){
				return field;
			}
			else if (field.getName().toLowerCase().equals(field.getDeclaringClass().getSimpleName().toLowerCase()+"id")){
				potentialOtherIdFields.add(field);
			}
		}
		if (! potentialOtherIdFields.isEmpty()){
			return potentialOtherIdFields.get(0);
		}
		else {
			return null;
		}
	}
	
	/**
	 * Default implementation of {@link RenderJpaMappingForClassStrategy#fieldNeedsManyToOneMapping(Field)}<br/>
	 * Only needs mapping if type of field is a top level class, not a simple class, not a standard class, not an array, enum, 
	 * and field does not have a transient or static modifier.
	 */
	@Override
	public boolean fieldNeedsManyToOneMapping(Field field){
		Class<?> clazz = (Class<?>) field.getType();
		boolean doesNotNeedManyToOneMapping = ReflectionUtil.isSimpleClass(clazz) || ReflectionUtil.isStandardClass(clazz) 
					|| clazz.isArray() || clazz.isEnum()
					|| clazz.isAnonymousClass() || clazz.isLocalClass() || clazz.isMemberClass()
					|| Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers());
		if (doesNotNeedManyToOneMapping){
			return false;
		}
		else {
			return true;
		}
	}
	
	/**
	 * Default implementation of {@link RenderJpaMappingForClassStrategy#getInheritanceMappingTypeForClass(Class,Collection)}<br/>
	 * <br/>
	 * Returns null if {@link RenderJpaMappingForClassStrategy#getRenderTypeFor(Class)} of class is not ENTITY.<br/>
	 * Returns SUBCLASS_WITH_DISCRIMINATOR if rendertype of class is ENTITY and rendertype of superclass of class is ENTITY.<br/>
	 * Returns NO_SUBCLASS_BUT_IS_SUPERCLASS_SINGLE_TABLE if rendertype of class is ENTITY, rendertype of superclass of class is not ENTITY and clazz is superclass of another class.<br/>
	 * Returns NONE if rendertype of class is ENTITY, rendertype of superclass of class is not ENTITY and clazz is not superclass of another class.
	 */
	@Override
	public InheritanceMappingType getInheritanceMappingTypeForClass(Class<?> clazz, Collection<Class<?>> allClassesToMap) {
		boolean classIsEntity = getRenderTypeFor(clazz).equals(ClassRenderType.ENTITY);
		
		if (classIsEntity){
			Class<?> superClass = clazz.getSuperclass();
			boolean superClassIsEntity;
			if (! superClass.equals(Object.class)){
				superClassIsEntity = getRenderTypeFor(superClass).equals(ClassRenderType.ENTITY);
			}
			else {
				superClassIsEntity = false;
			}
			
			if (! superClassIsEntity){
				//is superclass itself?
				boolean entityIsSuperClass = false;
				for (Class<?> otherClass: allClassesToMap){
					if (otherClass.getSuperclass() != null && otherClass.getSuperclass().equals(clazz)){
						entityIsSuperClass = true;
						break;
					}
				}
				if (entityIsSuperClass){
					return InheritanceMappingType.NO_SUBCLASS_BUT_IS_SUPERCLASS_SINGLE_TABLE;
				}
				else {
					return InheritanceMappingType.NONE;
				}
			}
			else {
				return InheritanceMappingType.SUBCLASS_WITH_DISCRIMINATOR;
			}
		}
		else {
			return InheritanceMappingType.NONE;
		}
	}
	
	/**
	 * Default implementation of {@link RenderJpaMappingForClassStrategy#getCollectionRenderTypeForField(Field)}<br/>
	 * <br/>
	 * "class being rendered" = Class that declares collectionField<br/>
	 * "other class" = ParameterizedType of Collection<br/>
	 * <br/>
	 * Returns null if ParameterizedType of Collection(="other class") unknown.<br/>
	 * Returns ONETOMANY if field of type "class being rendered" is present on "other class".<br/>
	 * Returns SIMPLE if field of type "class being rendered" is not present on "other class" and other class is either a simple class or an enum.<br/>
	 * Returns MANYTOMANY if field of type "class being rendered" is not present on "other class" and other class is not a simple class and not an enum.<br/>
	 */
	@Override
	public CollectionRenderType getCollectionRenderTypeForField(Field collectionField) {
		if (collectionField.getGenericType() instanceof ParameterizedType){
			ParameterizedType collectionListType = (ParameterizedType) collectionField.getGenericType();
			Class<?> classBeingRendered = collectionField.getDeclaringClass();
			Class<?> otherClass = (Class<?>) collectionListType.getActualTypeArguments()[0];
			List<Field> possibleFields = ReflectionUtil.getAllFieldsOfTypeClassOnOtherClass(classBeingRendered, otherClass);
			if (possibleFields.size() == 0){
				if (ReflectionUtil.isSimpleClass(otherClass) || otherClass.isEnum()){
					return CollectionRenderType.SIMPLE;
				}
				else {
					return CollectionRenderType.MANYTOMANY;
				}
			}
			else {
				return CollectionRenderType.ONETOMANY;
			}
		}
		else {
			log.warn("Collection " + collectionField.getName() + " on " + collectionField.getDeclaringClass() + " is not parametrized and thus, cannot be mapped/rendered.");
			return null;
		}
	}
	
}
