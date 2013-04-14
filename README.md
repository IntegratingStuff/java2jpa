java2jpa
========

Lib that makes it easy to create JPA mappings from basic Java Objects

Basic usage:

Java2JpaMappingGenerator java2JpaMappingGenerator =
     new Java2JpaMappingGenerator();
java2JpaMappingGenerator.setRenderJpaMappingForClassStrategy(
     new RenderJpaMappingForClassStrategyDefaultImpl());
JpaMappingRendererDefaultImpl jpaMappingRenderer =
     new JpaMappingRendererDefaultImpl("target/META-INF/orm.xml");
java2JpaMappingGenerator.setJpaMappingRenderer(jpaMappingRenderer);
java2JpaMappingGenerator.generateJpaMappingsForPackages("com.test.model");
jpaMappingRenderer.createMappedFiles();


This will create an orm.xml file in the target/META-INF folder for the classes in the com.test.model package.

For more info, read http://www.integratingstuff.com/2013/04/15/java2jpa-automatically-create-jpa-mappings-for-a-java-domain-model/.
