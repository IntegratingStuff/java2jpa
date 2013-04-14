package com.ceardannan.util;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.ceardannan.util.ReflectionUtil;
import com.test.model.Account;
import com.test.model.Address;
import com.test.model.Category;
import com.test.model.Item;
import com.test.model.Product;
import com.test.model.StatusType;

public class ReflectionUtilTest {

	@Test
	public void testGetReadablePropertyNamesOfClass(){
		List<String> readableProperties = 
			ReflectionUtil.getReadablePropertyNamesOfClass(Account.class);
		Assert.assertTrue(! readableProperties.contains("class"));
		Assert.assertTrue(readableProperties.contains("statements"));
		Assert.assertTrue(readableProperties.contains("firstStatement"));
		Assert.assertTrue(! readableProperties.contains("account"));
	}
	
	@Test
	public void testGetNonFieldReadablePropertyNamesOfClass(){
		List<String> nonFieldReadableProperties = 
			ReflectionUtil.getNonFieldReadablePropertyNamesOfClass(Account.class);
		Assert.assertTrue(! nonFieldReadableProperties.contains("class"));
		Assert.assertTrue(! nonFieldReadableProperties.contains("statements"));
		Assert.assertTrue(nonFieldReadableProperties.contains("firstStatement"));
		Assert.assertTrue(! nonFieldReadableProperties.contains("account"));
	}
	
	@Test
	public void testIsSimpleClass(){
		Assert.assertTrue(ReflectionUtil.isSimpleClass(String.class));
		Assert.assertTrue(ReflectionUtil.isSimpleClass(Date.class));
		Assert.assertTrue(ReflectionUtil.isSimpleClass(BigDecimal.class));
		Assert.assertTrue(! ReflectionUtil.isSimpleClass(Collection.class));
		Assert.assertTrue(! ReflectionUtil.isSimpleClass(Product.class));
		Assert.assertTrue(! ReflectionUtil.isSimpleClass(StatusType.class));
	}
	
	@Test
	public void testIsStandardClass(){
		Assert.assertTrue(ReflectionUtil.isStandardClass(String.class));
		Assert.assertTrue(ReflectionUtil.isStandardClass(Collection.class));
		Assert.assertTrue(! ReflectionUtil.isStandardClass(Product.class));
		Assert.assertTrue(! ReflectionUtil.isStandardClass(StatusType.class));
	}
	
	@Test
	public void testGetFieldOfClass(){
		Assert.assertNotNull(ReflectionUtil.getFieldOfClass(Item.class, "itemId"));
		Assert.assertNotNull(ReflectionUtil.getFieldOfClass(Category.class, "serialVersionUID"));
		Assert.assertNotNull(ReflectionUtil.getFieldOfClass(Address.class, "log"));
	}
	
}
