package com.ceardannan.java2jpa;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.ceardannan.util.XmlFormatter;

/**
 * Default implementation of the JpaMappingRenderer interface.
 * 
 * @author Steffen Luypaert
 *
 */
public class JpaMappingRendererDefaultImpl implements JpaMappingRenderer{
	
	/**
	 * Ordered list of second level elements(all elements under entity-mappings).
	 */
	private static final List<String> orderedSecondLevelElements = new ArrayList<String>();
	static {
		orderedSecondLevelElements.add("description");
		orderedSecondLevelElements.add("persistence-unit-metadata");
		orderedSecondLevelElements.add("package");
		orderedSecondLevelElements.add("schema");
		orderedSecondLevelElements.add("catalog");
		orderedSecondLevelElements.add("access");
		orderedSecondLevelElements.add("sequence-generator");
		orderedSecondLevelElements.add("table-generator");
		orderedSecondLevelElements.add("named-query");
		orderedSecondLevelElements.add("named-native-query");
		orderedSecondLevelElements.add("sql-result-set-mappingy");
		orderedSecondLevelElements.add("mapped-superclass");
		orderedSecondLevelElements.add("entity");
		orderedSecondLevelElements.add("embeddable");
	}
	/**
	 * Ordered list of attribute elements(currently not complete).
	 */
	private static final List<String> orderedAttributes = new ArrayList<String>();
	static {
		orderedAttributes.add("id");
		orderedAttributes.add("embedded-id");
		orderedAttributes.add("many-to-one");
		orderedAttributes.add("many-to-many");
		orderedAttributes.add("one-to-many");
		orderedAttributes.add("element-collection");
		orderedAttributes.add("embedded");
		orderedAttributes.add("transient");
	}
	
	/**
	 * The filename
	 */
	private String filename;
	
	public JpaMappingRendererDefaultImpl(String filename){
		this.filename = filename;
	}
	
	class MappingInfo{
		private Class<?> clazz;
		private String mappingType;
		private StringBuilder classContentSb = new StringBuilder();
		private Map<String,String> attributes = new HashMap<String, String>();

		MappingInfo(Class<?> clazz){
			this.clazz = clazz;
		}
		void setMappingType(String mappingType) {
			this.mappingType = mappingType;
		}
		
		void appendToClassContent(String s){
			if (mappingType == null){
				throw new IllegalStateException("appendToClassContent cannot be called for this before one of the renderAs(Class) methods has been called for this class first.");
			}
			classContentSb.append(s);
		}
		@SuppressWarnings("synthetic-access")
		void addToAttributes(String name, String content){
			if (mappingType == null){
				throw new IllegalStateException("addToAttributes cannot be called for this class before one of the renderAs(Class) methods has been called for this class first.");
			}
			String storeName;
			if (orderedAttributes.contains(name)){
				storeName = name;
			}
			else {
				storeName = null;
			}
			
			String existingContent = attributes.get(storeName);
			String newContent;
			if (existingContent != null){
				newContent = existingContent + content;
			}
			else {
				newContent = content;
			}
			attributes.put(storeName, newContent);
		}
		
		private StringBuilder getAttributesSb(){
			StringBuilder sb = new StringBuilder();
			for (String orderAttribute: orderedAttributes){
				String content = attributes.get(orderAttribute);
				if (content != null){
					sb.append(content);
				}
			}
			String nullContent = attributes.get(null);
			if (nullContent != null){
				sb.append(nullContent);
			}
			
			return sb;
		}
		
		String getXmlMapping(){
			StringBuilder sb = new StringBuilder();
			sb.append("<"+mappingType+" class=\"" + clazz.getName() + "\">");
			sb.append(classContentSb);
			StringBuilder attributesSb = getAttributesSb();
			if (attributesSb.length() > 0){
				sb.append("<attributes>");
				sb.append(attributesSb);
				sb.append("</attributes>");
			}
			sb.append("</"+mappingType+">");
			return sb.toString();
		}
		/**
		 * @return the clazz
		 */
		Class<?> getClazz() {
			return clazz;
		}
		/**
		 * @return the mappingType
		 */
		String getMappingType() {
			return mappingType;
		}
		
		
	}
	
	private static final String TOP_ORM_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<entity-mappings xmlns=\"http://java.sun.com/xml/ns/persistence/orm\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
		"xsi:schemaLocation=\"http://java.sun.com/xml/ns/persistence/orm orm_2_0.xsd\" version=\"2.0\">"+
		"<persistence-unit-metadata><xml-mapping-metadata-complete/><persistence-unit-defaults><access>PROPERTY</access></persistence-unit-defaults></persistence-unit-metadata>";

	private static final String BOTTOM_ORM_XML = "</entity-mappings>";
	
	private Map<Class<?>,MappingInfo> jpaMappingPerClass = new HashMap<Class<?>, MappingInfo>();

	private MappingInfo getMappingInfoForClass(Class<?> clazz){
		MappingInfo mappingInfo = jpaMappingPerClass.get(clazz);
		if (mappingInfo == null){
			mappingInfo = new MappingInfo(clazz);
			jpaMappingPerClass.put(clazz, mappingInfo);
		}
		return mappingInfo;
	}
	
	@Override
	public void renderClassAsMappedSuperclass(Class<?> clazz){
		getMappingInfoForClass(clazz).setMappingType("mapped-superclass");
	}
	@Override
	public void renderClassAsEmbeddable(Class<?> clazz){
		getMappingInfoForClass(clazz).setMappingType("embeddable");
	}
	@Override
	public void renderClassAsEntity(Class<?> clazz){
		getMappingInfoForClass(clazz).setMappingType("entity");
	}
	
	@Override
	public void addInheritanceStrategyElementForClass(Class<?> clazz,
			String inheritanceStrategyName) {
		getMappingInfoForClass(clazz).appendToClassContent("<inheritance strategy=\""+inheritanceStrategyName+"\" />");
	}
	@Override
	public void addDiscriminatorColumnElementForClass(Class<?> clazz,
			String discriminatorColumnName) {
		getMappingInfoForClass(clazz).appendToClassContent("<discriminator-column name=\""+discriminatorColumnName+"\"/>");
	}
	@Override
	public void addTableElementForClass(Class<?> clazz, String tableName) {
		getMappingInfoForClass(clazz).appendToClassContent("<table name=\"" + tableName + "\"/>");
	}
	@Override
	public void addDiscriminatorValueElementForClass(Class<?> clazz,String discriminatorValue) {
		getMappingInfoForClass(clazz).appendToClassContent("<discriminator-value>"+ discriminatorValue + "</discriminator-value>");
	}
	
	@Override
	public void renderTransientAttributeForClass(Class<?> clazz,
			String transientPropertyName) {
		getMappingInfoForClass(clazz).addToAttributes("transient","<transient name=\""+ transientPropertyName + "\"/>");
	}

	@Override
	public void renderEmbeddedIdForClass(Class<?> clazz, Field embeddedIdField) {
		getMappingInfoForClass(clazz).addToAttributes("embedded-id", "<embedded-id name=\"" + embeddedIdField.getName()+"\"/>");
	}

	@Override
	public void renderIdForClass(Class<?> clazz, Field idField,
			String generatedValueStrategyName) {
		getMappingInfoForClass(clazz).addToAttributes("id","<id name=\"" + idField.getName()
				+ "\"><generated-value strategy=\""+generatedValueStrategyName+"\"/></id>");
	}
	
	@Override
	public void renderEmbeddedAttributeForClass(Class<?> clazz, Field embeddedField) {
		getMappingInfoForClass(clazz).addToAttributes("embedded","<embedded name=\""+ embeddedField.getName() + "\"/>");
	}
	@Override
	public void renderManyToOneAttributeForClass(Class<?> clazz,
			Field manyToOneField, String fetchType, String joinColumnName) {
		getMappingInfoForClass(clazz).addToAttributes("many-to-one","<many-to-one name=\""
				+ manyToOneField.getName() + "\" fetch=\""+fetchType+"\"><join-column name=\"" + joinColumnName + "\"/></many-to-one>");
	}
	
	@Override
	public void renderSimpleCollectionAttributeForClass(Class<?> clazz,
			Field collectionField, String valueColumnName,
			String collectionTableName) {
		getMappingInfoForClass(clazz).addToAttributes("element-collection","<element-collection name=\""
				+ collectionField.getName() + "\"><column name=\""
				+ valueColumnName + "\"/><collection-table name=\""
						+ collectionTableName + "\"/></element-collection>");
	}
	@Override
	public void renderManyToManyAttributeForClass(Class<?> clazz,
			Field collectionField, String joinTableName, String joinColumnName,
			String inverseJoinColumnName) {
		getMappingInfoForClass(clazz).addToAttributes("many-to-many","<many-to-many name=\""
				+ collectionField.getName() + "\"><join-table name=\""
				+ joinTableName + "\"><join-column name=\"" + joinColumnName + "\" /><inverse-join-column name=\""
				+ inverseJoinColumnName + "\" /></join-table></many-to-many>");
	}
	@Override
	public void renderOneToManyAttributeForClass(Class<?> clazz,
			Field collectionField, String mappedByFieldName) {
		getMappingInfoForClass(clazz).addToAttributes("one-to-many","<one-to-many name=\"" + collectionField.getName() 
				+ "\" mapped-by=\"" + mappedByFieldName + "\" />");
	}

	protected String getTopOrmXml(){
		return TOP_ORM_XML;
	}
	protected String getBottomOrmXml(){
		return BOTTOM_ORM_XML;
	}

	private Map<String, List<MappingInfo>> getMappingInfosPerTopLevelElement(){
		Map<String, List<MappingInfo>> mappingInfosPerTopLevelElement = new HashMap<String, List<MappingInfo>>();
		for (MappingInfo mappingInfo: jpaMappingPerClass.values()){
			List<MappingInfo> mappingInfos = mappingInfosPerTopLevelElement.get(mappingInfo.getMappingType());
			if (mappingInfos == null){
				mappingInfos = new ArrayList<MappingInfo>();
				mappingInfosPerTopLevelElement.put(mappingInfo.getMappingType(), mappingInfos);
			}
			mappingInfos.add(mappingInfo);
		}
		return mappingInfosPerTopLevelElement;
	}
	
	@Override
	public Map<String, String> getMappedFilesAsStringMap() {
		Map<String,String> files = new HashMap<String, String>();
		StringBuilder jpaMappingSb = new StringBuilder();
		jpaMappingSb.append(getTopOrmXml());
		Map<String, List<MappingInfo>> mappingInfosPerTopLevelElement = getMappingInfosPerTopLevelElement();
		for (String mappingType: orderedSecondLevelElements){
			List<MappingInfo> mappingInfos = mappingInfosPerTopLevelElement.get(mappingType);
			if (mappingInfos != null){
				for (MappingInfo mappingInfo: mappingInfos){
					jpaMappingSb.append(mappingInfo.getXmlMapping());
				}
			}
		}
		jpaMappingSb.append(getBottomOrmXml());
		files.put(filename, XmlFormatter.format(jpaMappingSb.toString()));
		return files;
	}

	@Override
	public void createMappedFiles() throws IOException{
		Map<String,String> mappedFiles = getMappedFilesAsStringMap();
		for (String mappedFilename: mappedFiles.keySet()){
			FileUtils.writeStringToFile(new File(mappedFilename), mappedFiles.get(mappedFilename));
		}
	}

	/**
	 * @return the jpaMappingPerClass
	 */
	Map<Class<?>, MappingInfo> getJpaMappingPerClass() {
		return jpaMappingPerClass;
	}
	
	
}
