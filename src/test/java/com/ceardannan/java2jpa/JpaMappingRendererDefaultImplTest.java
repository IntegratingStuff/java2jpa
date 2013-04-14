package com.ceardannan.java2jpa;

import java.io.File;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.ceardannan.java2jpa.JpaMappingRendererDefaultImpl;
import com.ceardannan.util.ReflectionUtil;
import com.test.model.Address;
import com.test.model.LineItem;
import com.test.model.PersistentObject;
import com.test.model.Person;
import com.test.model.Supplier;

public class JpaMappingRendererDefaultImplTest {
	
	@Test
	public void testRenderClassAs() throws Exception{
		JpaMappingRendererDefaultImpl jpaMappingRenderer = new JpaMappingRendererDefaultImpl("target/orm-rendertest.xml");
		jpaMappingRenderer.renderClassAsEntity(LineItem.class);
		jpaMappingRenderer.renderClassAsEntity(Supplier.class);
		jpaMappingRenderer.renderClassAsEntity(Person.class);
		jpaMappingRenderer.renderClassAsEmbeddable(Address.class);
		jpaMappingRenderer.renderClassAsMappedSuperclass(PersistentObject.class);
		jpaMappingRenderer.createMappedFiles();
		
		String xmlContent = FileUtils.readFileToString(new File("target/orm-rendertest.xml"));
		
		Assert.assertTrue(xmlContent.contains("<entity class=\""+LineItem.class.getName()+"\"/>"));
		Assert.assertTrue(xmlContent.contains("<entity class=\""+Supplier.class.getName()+"\"/>"));
		Assert.assertTrue(xmlContent.contains("<entity class=\""+Person.class.getName()+"\"/>"));
		Assert.assertTrue(xmlContent.contains("<embeddable class=\""+Address.class.getName()+"\"/>"));
		Assert.assertTrue(xmlContent.contains("<mapped-superclass class=\""+PersistentObject.class.getName()+"\"/>"));
		
		//expected elements are not mapped by renderAs, need to be added by separate calls
		Assert.assertTrue(! xmlContent.contains("<id"));
		Assert.assertTrue(! xmlContent.contains("<attributes"));
		Assert.assertTrue(! xmlContent.contains("<table"));
	}
	
	@Test
	public void testExtraRendering() throws Exception{
		JpaMappingRendererDefaultImpl jpaMappingRenderer = new JpaMappingRendererDefaultImpl("target/orm-rendertest2.xml");
		try {
			jpaMappingRenderer.renderIdForClass(LineItem.class, ReflectionUtil.getFieldOfClass(LineItem.class, "id"), "AUTO");
			jpaMappingRenderer.addTableElementForClass(Supplier.class, "SUPPLIER");
			Assert.fail();
		} catch (IllegalStateException e) {
			//ok
		}
		
		jpaMappingRenderer.renderClassAsEntity(LineItem.class);
		jpaMappingRenderer.renderClassAsEntity(Supplier.class);
		jpaMappingRenderer.renderIdForClass(LineItem.class, ReflectionUtil.getFieldOfClass(LineItem.class, "id"), "AUTO");
		jpaMappingRenderer.addTableElementForClass(Supplier.class, "SUPPLIER");
		jpaMappingRenderer.createMappedFiles();
		
		String xmlContent = FileUtils.readFileToString(new File("target/orm-rendertest2.xml"));
		
		Assert.assertTrue(xmlContent.contains("<entity class=\""+LineItem.class.getName()+"\">"));
		Assert.assertTrue(xmlContent.contains("<entity class=\""+Supplier.class.getName()+"\">"));
		
		Assert.assertTrue(xmlContent.contains("<id"));
		Assert.assertTrue(xmlContent.contains("<attributes"));
		Assert.assertTrue(xmlContent.contains("<table"));
	}
}
