package com.ceardannan.util;

import junit.framework.Assert;

import org.junit.Test;

import com.ceardannan.util.StringUtil;

public class StringUtilTest {

	@Test
	public void testJavaClassNameToDbName(){
		Assert.assertEquals(StringUtil.javaClassNameToDbName("ProductDesign"), "PRODUCT_DESIGN");
	}
	
	@Test
	public void testJavaClassNameToVariableName(){
		Assert.assertEquals(StringUtil.javaClassNameToVariableName("ProductDesign"), "productDesign");
	}
	
}
