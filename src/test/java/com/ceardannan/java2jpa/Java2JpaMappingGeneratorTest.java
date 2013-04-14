package com.ceardannan.java2jpa;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.ceardannan.java2jpa.Java2JpaMappingGenerator;
import com.ceardannan.java2jpa.JpaMappingRendererDefaultImpl;
import com.ceardannan.java2jpa.RenderJpaMappingForClassStrategyDefaultImpl;
import com.ceardannan.java2jpa.JpaMappingRendererDefaultImpl.MappingInfo;
import com.test.model.Address;
import com.test.model.LineItem;
import com.test.model.PersistentObject;

public class Java2JpaMappingGeneratorTest {

	private static final List<String> contentNeedsToContain = new ArrayList<String>();
	static {
		contentNeedsToContain.add("<one-to-many");
		contentNeedsToContain.add("<many-to-many");
		contentNeedsToContain.add("<many-to-one");
		contentNeedsToContain.add("<element-collection");
		contentNeedsToContain.add("<entity");
		contentNeedsToContain.add("<mapped-superclass");
		contentNeedsToContain.add("<embeddable");
		contentNeedsToContain.add("<embedded-id");
		contentNeedsToContain.add("<transient");
	}
	
	private void checkContent(String xmlContent){
		for (String needsToBePresent: contentNeedsToContain){
			Assert.assertTrue(xmlContent.contains(needsToBePresent));
		}
	}
	
	@Test
	public void testGenerateJpaMappingsForPackages() throws Exception{
		Java2JpaMappingGenerator java2JpaMappingGenerator = new Java2JpaMappingGenerator();
		java2JpaMappingGenerator.setRenderJpaMappingForClassStrategy(new RenderJpaMappingForClassStrategyDefaultImpl());
		JpaMappingRendererDefaultImpl jpaMappingRenderer = new JpaMappingRendererDefaultImpl("target/META-INF/orm.xml");
		java2JpaMappingGenerator.setJpaMappingRenderer(jpaMappingRenderer);
		java2JpaMappingGenerator.generateJpaMappingsForPackages("com.test.model");
		
		Map<Class<?>,MappingInfo> mappings = jpaMappingRenderer.getJpaMappingPerClass();
		Assert.assertEquals(mappings.get(LineItem.class).getMappingType(),"entity");
		Assert.assertEquals(mappings.get(PersistentObject.class).getMappingType(),"mapped-superclass");
		Assert.assertEquals(mappings.get(Address.class).getMappingType(),"embeddable");
		
		Map<String,String> mappedFiles = jpaMappingRenderer.getMappedFilesAsStringMap();
		Assert.assertEquals(mappedFiles.size(),1);
		Assert.assertEquals(mappedFiles.keySet().iterator().next(),"target/META-INF/orm.xml");
		String xmlContent = mappedFiles.values().iterator().next();
		//System.out.println(xmlContent);
		checkContent(xmlContent);
		
		jpaMappingRenderer.createMappedFiles();
		xmlContent = FileUtils.readFileToString(new File("target/META-INF/orm.xml"));
		checkContent(xmlContent);
	}
	
	@Test
	public void testGenerateJpaMappingsForPackagesForSimpleModel() throws Exception{
		Java2JpaMappingGenerator java2JpaMappingGenerator = new Java2JpaMappingGenerator();
		java2JpaMappingGenerator.setRenderJpaMappingForClassStrategy(new RenderJpaMappingForClassStrategyDefaultImpl());
		JpaMappingRendererDefaultImpl jpaMappingRenderer = new JpaMappingRendererDefaultImpl("target/META-INF/orm-simple.xml");
		java2JpaMappingGenerator.setJpaMappingRenderer(jpaMappingRenderer);
		java2JpaMappingGenerator.generateJpaMappingsForPackages("com.test.model.simple");
		
		jpaMappingRenderer.createMappedFiles();
		String xmlContent = FileUtils.readFileToString(new File("target/META-INF/orm-simple.xml"));
	}
	
}
