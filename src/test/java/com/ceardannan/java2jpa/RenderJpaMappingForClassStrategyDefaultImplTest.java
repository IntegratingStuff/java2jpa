package com.ceardannan.java2jpa;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.ceardannan.java2jpa.ClassRenderType;
import com.ceardannan.java2jpa.CollectionRenderType;
import com.ceardannan.java2jpa.InheritanceMappingType;
import com.ceardannan.java2jpa.RenderJpaMappingForClassStrategy;
import com.ceardannan.java2jpa.RenderJpaMappingForClassStrategyDefaultImpl;
import com.ceardannan.util.ReflectionUtil;
import com.test.model.Account;
import com.test.model.Address;
import com.test.model.Category;
import com.test.model.Company;
import com.test.model.Describable;
import com.test.model.InternationalCompany;
import com.test.model.Item;
import com.test.model.LineItem;
import com.test.model.PersistentObject;
import com.test.model.Product;
import com.test.model.StatusType;
import com.test.model.Supplier;

public class RenderJpaMappingForClassStrategyDefaultImplTest {
	
	private Field getFieldOfClass(Class<?> clazz, String fieldName){
		return ReflectionUtil.getFieldOfClass(clazz, fieldName);
	}
	
	@Test
	public void testClassNeedsMapping() throws Exception{
		RenderJpaMappingForClassStrategy renderJpaMappingForClassStrategy = 
			new RenderJpaMappingForClassStrategyDefaultImpl();
		Assert.assertTrue(renderJpaMappingForClassStrategy.classNeedsMapping(PersistentObject.class));
		Assert.assertTrue(renderJpaMappingForClassStrategy.classNeedsMapping(LineItem.class));
		Assert.assertTrue(renderJpaMappingForClassStrategy.classNeedsMapping(Address.class));
		Assert.assertTrue(! renderJpaMappingForClassStrategy.classNeedsMapping(Describable.class));
		Assert.assertTrue(! renderJpaMappingForClassStrategy.classNeedsMapping(StatusType.class));
	}
	
	@Test
	public void testGetRenderTypeFor() throws Exception{
		RenderJpaMappingForClassStrategy renderJpaMappingForClassStrategy = 
			new RenderJpaMappingForClassStrategyDefaultImpl();
		Assert.assertEquals(renderJpaMappingForClassStrategy.getRenderTypeFor(PersistentObject.class),ClassRenderType.MAPPEDSUPERCLASS);
		Assert.assertEquals(renderJpaMappingForClassStrategy.getRenderTypeFor(LineItem.class),ClassRenderType.ENTITY);
		Assert.assertEquals(renderJpaMappingForClassStrategy.getRenderTypeFor(Address.class),ClassRenderType.EMBEDDABLE);
		Assert.assertEquals(renderJpaMappingForClassStrategy.getRenderTypeFor(Describable.class),null);
		Assert.assertEquals(renderJpaMappingForClassStrategy.getRenderTypeFor(StatusType.class),null);
	}
	
	@Test
	public void testGetIdFieldForClass() throws Exception{
		RenderJpaMappingForClassStrategy renderJpaMappingForClassStrategy = 
			new RenderJpaMappingForClassStrategyDefaultImpl();
		Assert.assertEquals(renderJpaMappingForClassStrategy.getIdFieldForClass(PersistentObject.class),getFieldOfClass(PersistentObject.class, "id"));
		Assert.assertEquals(renderJpaMappingForClassStrategy.getIdFieldForClass(LineItem.class),getFieldOfClass(LineItem.class, "id"));
		Assert.assertEquals(renderJpaMappingForClassStrategy.getIdFieldForClass(Product.class),getFieldOfClass(Product.class, "id"));
		Assert.assertEquals(renderJpaMappingForClassStrategy.getIdFieldForClass(Item.class),getFieldOfClass(Item.class, "itemId"));
		Assert.assertEquals(renderJpaMappingForClassStrategy.getIdFieldForClass(Address.class),null);
		Assert.assertEquals(renderJpaMappingForClassStrategy.getIdFieldForClass(Describable.class),null);
		Assert.assertEquals(renderJpaMappingForClassStrategy.getIdFieldForClass(StatusType.class),null);
	}
	
	@Test
	public void testFieldNeedsManyToOneMapping() throws Exception{
		RenderJpaMappingForClassStrategy renderJpaMappingForClassStrategy = 
			new RenderJpaMappingForClassStrategyDefaultImpl();
		Assert.assertTrue(renderJpaMappingForClassStrategy.fieldNeedsManyToOneMapping(getFieldOfClass(Account.class, "homeAddress")));
		Assert.assertTrue(! renderJpaMappingForClassStrategy.fieldNeedsManyToOneMapping(getFieldOfClass(Account.class, "creditCards")));
		Assert.assertTrue(! renderJpaMappingForClassStrategy.fieldNeedsManyToOneMapping(getFieldOfClass(Account.class, "homePhone")));
		Assert.assertTrue(! renderJpaMappingForClassStrategy.fieldNeedsManyToOneMapping(getFieldOfClass(Item.class, "itemId")));
		Assert.assertTrue(! renderJpaMappingForClassStrategy.fieldNeedsManyToOneMapping(getFieldOfClass(Category.class, "serialVersionUID")));
		Assert.assertTrue(! renderJpaMappingForClassStrategy.fieldNeedsManyToOneMapping(getFieldOfClass(Address.class, "log")));
	}
	
	@Test
	public void testGetInheritanceMappingTypeForClass() throws Exception{
		RenderJpaMappingForClassStrategy renderJpaMappingForClassStrategy = 
			new RenderJpaMappingForClassStrategyDefaultImpl();
		
		List<Class<?>> allClassesToMap = new ArrayList<Class<?>>();
		allClassesToMap.add(Supplier.class);
		allClassesToMap.add(Company.class);
		allClassesToMap.add(InternationalCompany.class);
		allClassesToMap.add(PersistentObject.class);
		allClassesToMap.add(Address.class);
		
		Assert.assertEquals(renderJpaMappingForClassStrategy.getInheritanceMappingTypeForClass(Supplier.class, allClassesToMap),InheritanceMappingType.NO_SUBCLASS_BUT_IS_SUPERCLASS_SINGLE_TABLE);
		Assert.assertEquals(renderJpaMappingForClassStrategy.getInheritanceMappingTypeForClass(Company.class, allClassesToMap),InheritanceMappingType.SUBCLASS_WITH_DISCRIMINATOR);
		Assert.assertEquals(renderJpaMappingForClassStrategy.getInheritanceMappingTypeForClass(InternationalCompany.class, allClassesToMap),InheritanceMappingType.SUBCLASS_WITH_DISCRIMINATOR);
		Assert.assertEquals(renderJpaMappingForClassStrategy.getInheritanceMappingTypeForClass(PersistentObject.class, allClassesToMap),InheritanceMappingType.NONE);
		Assert.assertEquals(renderJpaMappingForClassStrategy.getInheritanceMappingTypeForClass(Address.class, allClassesToMap),InheritanceMappingType.NONE);
	}
	
	@Test
	public void testGetCollectionRenderTypeForField() throws Exception{
		RenderJpaMappingForClassStrategy renderJpaMappingForClassStrategy = 
			new RenderJpaMappingForClassStrategyDefaultImpl();
		Assert.assertEquals(renderJpaMappingForClassStrategy.getCollectionRenderTypeForField(getFieldOfClass(Account.class, "creditCards")),CollectionRenderType.MANYTOMANY);
		Assert.assertEquals(renderJpaMappingForClassStrategy.getCollectionRenderTypeForField(getFieldOfClass(Category.class, "associatedProducts")),CollectionRenderType.ONETOMANY);
		Assert.assertEquals(renderJpaMappingForClassStrategy.getCollectionRenderTypeForField(getFieldOfClass(Item.class, "attributes")),CollectionRenderType.SIMPLE);
		Assert.assertNull(renderJpaMappingForClassStrategy.getCollectionRenderTypeForField(getFieldOfClass(Account.class, "anyInfo")));
	}
}
