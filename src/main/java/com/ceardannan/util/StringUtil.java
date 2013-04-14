package com.ceardannan.util;

/**
 * Utility classes with string related methods.
 * 
 * @author Steffen Luypaert
 *
 */
public class StringUtil {

	/**
	 * Returns a database table name for a given simple java class name.
	 * 
	 * @param className - simple classname to return the database table name for
	 * @return default name of the database table for the class
	 */
	public static String javaClassNameToDbName(String className){
		return className.replaceAll("(.)(\\p{Lu})", "$1_$2").toUpperCase();
	}
	
	/**
	 * Returns a default variableName for an instance of a class given its simple name.
	 * 
	 * @param className - simple classname to return the default instance variable name for
	 * @return default variable name for an instance of the class
	 */
	public static String javaClassNameToVariableName(String className){
		return className.substring(0, 1).toLowerCase() + className.substring(1);
	}
	
}
