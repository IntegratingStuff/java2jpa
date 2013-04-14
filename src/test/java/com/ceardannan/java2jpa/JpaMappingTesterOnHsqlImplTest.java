package com.ceardannan.java2jpa;

import org.junit.Test;

import com.ceardannan.java2jpa.Java2JpaMappingGenerator;
import com.ceardannan.java2jpa.JpaMappingRendererDefaultImpl;
import com.ceardannan.java2jpa.JpaMappingTester;
import com.ceardannan.java2jpa.JpaMappingTesterOnHsqlImpl;
import com.ceardannan.java2jpa.RenderJpaMappingForClassStrategyDefaultImpl;

public class JpaMappingTesterOnHsqlImplTest {

	@Test
	public void testTestMappedFiles() throws Exception{
		//make sure orm.xml is generated first
		Java2JpaMappingGenerator java2JpaMappingGenerator = new Java2JpaMappingGenerator();
		java2JpaMappingGenerator.setRenderJpaMappingForClassStrategy(new RenderJpaMappingForClassStrategyDefaultImpl());
		JpaMappingRendererDefaultImpl jpaMappingRenderer = new JpaMappingRendererDefaultImpl("target/META-INF/orm.xml");
		java2JpaMappingGenerator.setJpaMappingRenderer(jpaMappingRenderer);
		java2JpaMappingGenerator.generateJpaMappingsForPackages("com.test.model");
		jpaMappingRenderer.createMappedFiles();
		
		//test
		JpaMappingTester jpaMappingTester = new JpaMappingTesterOnHsqlImpl("TESTDB");
		jpaMappingTester.test();
	}
	
}
