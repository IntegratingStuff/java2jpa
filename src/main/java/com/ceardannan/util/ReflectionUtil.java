package com.ceardannan.util;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * Utility class with reflection related methods.
 * 
 * @author Steffen Luypaert
 *
 */
public class ReflectionUtil {

	/**
	 * Returns all the readable(has getter) property names of a class for which no instance field is present (itself).
	 * Does not return the property names for its superclasses.
	 * 
	 * @param clazz - class to return the property names for
	 * @return a list of the non-field property names of the class
	 */
	public static List<String> getNonFieldReadablePropertyNamesOfClass(Class<?>clazz){
		List<Field> allFields = getAllFieldsFor(clazz);
		List<String> nonFieldPropertyNamesOfClass= new ArrayList<String>();
		for (String propertyName: getReadablePropertyNamesOfClass(clazz)){
			boolean found = false;
			for (Field field: allFields){
				if (field.getName().equals(propertyName)){
					found = true;
					break;
				}
			}
			if (! found){
				nonFieldPropertyNamesOfClass.add(propertyName);
			}
		}
		return nonFieldPropertyNamesOfClass;
	}
	
	/**
	 * Returns all the readable(has getter) property names of a class (itself).
	 * Does not return the property names for its superclasses.
	 * 
	 * @param clazz - class to return the property names for
	 * @return a list of the property names of the class
	 */
	public static List<String> getReadablePropertyNamesOfClass(Class<?>clazz){
		PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(clazz);
		List<String> propertyNamesOfClass= new ArrayList<String>();
		for (int i = 0; i < descriptors.length; i++) {
			PropertyDescriptor propertyDescriptor = descriptors[i];
			String name = propertyDescriptor.getName();
			//only readmethods on class itself!
			if (propertyDescriptor.getReadMethod() != null && propertyDescriptor.getReadMethod().getDeclaringClass().equals(clazz)) {
				propertyNamesOfClass.add(name);
			}
		}
		return propertyNamesOfClass;
	}
	
	/**
	 * Returns whether the class is a standard Java class(part of a java.* package).
	 * 
	 * @param clazz - class to return the property names for
	 * @return whether the class is a standard class
	 */
	public static boolean isStandardClass(Class<?>clazz){
		return clazz.getName().startsWith("java.");
	}
	/**
	 * Returns whether the class is a simple Java class: java.util.Calendar, 
	 * java.math.BigDecimal, java.math.BigInteger, java.util.Date, part of java.lang or a primitive.
	 * 
	 * @param clazz - class to return the property names for
	 * @return whether the class is a simple class
	 */
	public static boolean isSimpleClass(Class<?>clazz){
		return clazz.getName().startsWith("java.lang") || clazz.equals(Calendar.class)|| clazz.equals(BigDecimal.class)|| clazz.equals(BigInteger.class) || clazz.equals(Date.class)|| clazz.isPrimitive();
	}
	
	/**
	 * Returns the field of a class, given a fieldName.
	 * Does also return fields for superclass fields.
	 * 
	 * @param clazz - class to return a field for
	 * @param fieldName - name of the field to return
	 * @return field with the given fieldName of the class
	 */
	public static Field getFieldOfClass(Class<?>clazz, String fieldName){
		for (Field field: getAllFieldsFor(clazz)){
			if (field.getName().equals(fieldName)){
				return field;
			}
		}
		return null;
	}
	
	/**
	 * Returns a collection of all classes present in the packages with the given packageNames.
	 * 
	 * @param packageNames - names of the packages to scan for classes
	 * @return all classes present in the packages with the given packageNames
	 */
	public static Collection<Class<?>> getAllClassesIn(String... packageNames) {
		Collection<Class<?>> allClasses = new ArrayList<Class<?>>();
		for (String packageName: packageNames){
			allClasses.addAll(getAllClassesIn(packageName));
		}
		return allClasses;
	}
	
	/**
	 * Returns a collection of all classes present in the package with the given packageName.
	 * 
	 * @param packageName - name of the package to scan for classes
	 * @return all classes present in the packages with the given packageName
	 */
	public static Collection<Class<?>> getAllClassesIn(String packageName) {
		Collection<String> classNames = getFullyQualifiedClassNamesForPackage(packageName);

		Collection<Class<?>> allClasses = new ArrayList<Class<?>>();
		for (String className: classNames){
			try {
				allClasses.add(Class.forName(className));
			} catch (ClassNotFoundException e) {
				//cannot happen, ensured by getFullyQualifiedClassNamesForPackage call?
				e.printStackTrace();
			}
		}

		return allClasses;
	}
	
	/**
	 * Returns all the fully qualified class names of the classes within a package.
	 * Starts from the Classloader of the current thread and then uses a filebased approach to scan files.
	 * 
	 * @param packageName - the name of the package to get class names for
	 * @return a list of fully qualified class names present in the package with given packageName
	 */
	public static Collection<String> getFullyQualifiedClassNamesForPackage(String packageName){
		List<String> fullyQualifiedClassNames = new ArrayList<String>();
		for (String className: getSimpleClassNamesForPackage(packageName)){
			fullyQualifiedClassNames.add(packageName+"."+className);
		}
		return fullyQualifiedClassNames;
	}
	
	/**
	 * Returns all the simple class names of the classes within a package.
	 * Starts from the Classloader of the current thread and then uses a filebased approach to scan files.
	 * 
	 * @param packageName - the name of the package to get class names for
	 * @return a list of simple(without package) class names present in the package with given packageName
	 */
	public static Collection<String> getSimpleClassNamesForPackage(String packageName){
	    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	    URL packageURL;
	    ArrayList<String> names = new ArrayList<String>();
	    
	    String packageFolder = packageName.replace(".", "/");
	    packageURL = classLoader.getResource(packageFolder);

	    if(packageURL.getProtocol().equals("jar")){
	        String jarFileName;
	        JarFile jf ;
	        Enumeration<JarEntry> jarEntries;
	        String entryName;

	        // build jar file name, then loop through zipped entries
	        try {
				jarFileName = URLDecoder.decode(packageURL.getFile(), "UTF-8");
				jarFileName = jarFileName.substring(5,jarFileName.indexOf("!"));
				System.out.println(">"+jarFileName);
				jf = new JarFile(jarFileName);
				jarEntries = jf.entries();
				while(jarEntries.hasMoreElements()){
				    entryName = jarEntries.nextElement().getName();
				    if(entryName.startsWith(packageFolder) && entryName.length()>packageFolder.length()+5){
				        entryName = entryName.substring(packageFolder.length(),entryName.lastIndexOf('.'));
				        names.add(entryName);
				    }
				}
			} catch (UnsupportedEncodingException e) {
				//Cannot happen? UTF-8 always supported?
				e.printStackTrace();
			} catch (IOException e) {
				//Cannot happen? Jar file always present?
				e.printStackTrace();
			}

	    // loop through files in classpath
	    }
	    else {
	        File folder = new File(packageURL.getFile());
	        File[] contenuti = folder.listFiles();
	        String entryName;
	        for(File actual: contenuti){
	            entryName = actual.getName();
	            if (actual.getName().endsWith(".class")){
	        		entryName = entryName.substring(0, entryName.lastIndexOf('.'));
	        		names.add(entryName);
	        	}
	        }
	    }
	    return names;
	}
	
	/**
	 * Returns all the fields that have the given propertyClass as their type on the given classToScan.
	 * 
	 * @param propertyClass - type of the fields to look for
	 * @param classToScan - class to introspect
	 * @return list of fields of the classToScan with the given propertyClass type
	 */
	public static List<Field> getAllFieldsOfTypeClassOnOtherClass(Class<?>propertyClass, Class<?>classToScan){
		List<Field> fields = new ArrayList<Field>();
		for (Field field: getAllFields(classToScan)){
			if (field.getGenericType().equals(propertyClass)){
				fields.add(field);
			}
		}
		return fields;
	}
	
	/**
	 * Returns all the declared fields of a class as a List.
	 * 
	 * @param clazz - class to introspect
	 * @return list of the declared fields of a class
	 */
	public static List<Field> getAllDeclaredFieldsFor(Class<?>clazz){
		return Arrays.asList(clazz.getDeclaredFields());
	}
	/**
	 * Returns all(also adds superclass fields) the fields of a class as a List.
	 * 
	 * @param clazz - class to introspect
	 * @returnlist of the all of the fields of a class
	 */
	public static List<Field> getAllFieldsFor(Class<?>clazz){
		return Arrays.asList(getAllFields(clazz));
	}
	
	/**
	   * Return a list of all fields (whatever access status, and on whatever
	   * superclass they were defined) that can be found on this class.
	   * This is like a union of {@link Class#getDeclaredFields()} which
	   * ignores and super-classes, and {@link Class#getFields()} which ignores
	   * non-public fields.
	   * 
	   * @param clazz - the class to introspect
	   * @return the complete list of fields
	   */
	  public static Field[] getAllFields(Class<?> clazz){
	      List<Class<?>> classes = getAllSuperclasses(clazz);
	      classes.add(clazz);
	      return getAllFields(classes);
	  }
	  /**
	   * As {@link #getAllFields(Class)} but acts on a list of {@link Class}s and
	   * uses only {@link Class#getDeclaredFields()}.
	   * 
	   * @param classes - the list of classes to reflect on
	   * @return the complete list of fields
	   */
	  private static Field[] getAllFields(List<Class<?>> classes){
	      Collection<Field> fields = new ArrayList<Field>();
	      for (Class<?> clazz : classes)
	      {
	          fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
	      }

	      return fields.toArray(new Field[fields.size()]);
	  }
	  /**
	   * Returns a List of superclasses for the given class.
	   * 
	   * @param clazz - the class to look up
	   * @return the List of super-classes in order going up from this one
	   */
	  public static List<Class<?>> getAllSuperclasses(Class<?> clazz) {
	      List<Class<?>> classes = new ArrayList<Class<?>>();

	      Class<?> superclass = clazz.getSuperclass();
	      while (superclass != null)
	      {
	          classes.add(superclass);
	          superclass = superclass.getSuperclass();
	      }

	      return classes;
	  }
	
}
