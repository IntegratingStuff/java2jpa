package com.ceardannan.util;

import junit.framework.Assert;

import org.junit.Test;

import com.ceardannan.util.XmlFormatter;

public class XmlFormatterTest {

	private static final String plainXml = "<test><foo></foo></test>";
	private static final String formattedXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<test>\n  <foo/>\n</test>\n";
	
	@Test
	public void testFormat(){
		Assert.assertEquals(XmlFormatter.format(plainXml),formattedXml);
	}
	
}
