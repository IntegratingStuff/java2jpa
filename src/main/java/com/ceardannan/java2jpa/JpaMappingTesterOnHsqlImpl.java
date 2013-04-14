package com.ceardannan.java2jpa;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hsqldb.Server;

/**
 * Implementation of the JpaMappingTester interface.
 * Tests a mapping based on the Meta-INF/persistence.xml file on the classpath.
 * Checks whether the mapping validates against a HQSL in-memory database.
 * 
 * @author Steffen Luypaert
 *
 */
public class JpaMappingTesterOnHsqlImpl implements JpaMappingTester{

	/**
	 * the dbName, usually "TESTDB"
	 * has to match the one in the connectionstring in the persistence.xml file
	 */
	private String dbName;
	
	public JpaMappingTesterOnHsqlImpl(String dbName){
		this.dbName = dbName;
	}
	
	/**
	 * Starts an hsql server and initializes a entityManagerFactory based on the META-INF/persistence.xml file found on the classpath.
	 * This is a good way to validate the generated mapping, especially when generate_dll is set to true in the persistence.xml file.
	 * 
	 * @see com.ceardannan.java2jpa.JpaMappingTester#test(java.util.Map)
	 * 
	 */
	@Override
	public void test() throws Exception{
		Server hsqlServer = null;
		try {
			//start hsql server
			hsqlServer = new Server();
			hsqlServer.setLogWriter(null);
			hsqlServer.setSilent(true);
			hsqlServer.setDatabaseName(0, "xdb");
			hsqlServer.setDatabasePath(0, "file:target/"+dbName);
			hsqlServer.start();
			
			//initialize entityManagerFactory
			@SuppressWarnings("unused")
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("TestEntityManagerFactory");
		} catch (Exception e) {
			hsqlServer.stop();
			throw e;
		}

		
	}

	
	
}
