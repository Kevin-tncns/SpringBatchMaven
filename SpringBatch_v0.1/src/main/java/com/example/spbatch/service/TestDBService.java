package com.example.spbatch.service;

import org.springframework.stereotype.Service;

import com.example.spbatch.mapper.mariadb.TestMariaDB;
import com.example.spbatch.mapper.metamysql.TestMetaDB;
import com.example.spbatch.mapper.oracle.source.TestSourceOracleDB;
import com.example.spbatch.mapper.oracle.target.TestTargetOracleDB;


@Service
public class TestDBService {
	
	
	private final TestMetaDB  			testMetaDB;   			// metaDB
	private final TestMariaDB   		testMariaDB;  			// mariaDB
	private final TestSourceOracleDB 	testSourceOracleDB; 	// sourceDB
	private final TestTargetOracleDB 	testTargetOracleDB; 	// targetDB

	/**
	 * @param testMetaDB
	 * @param testMariaDB
	 * @param testSourceOracleDB
	 * @param testTargetOracleDB
	 */
	public TestDBService(TestMetaDB testMetaDB, TestMariaDB testMariaDB, TestSourceOracleDB testSourceOracleDB,
			TestTargetOracleDB testTargetOracleDB) {
		super();
		this.testMetaDB = testMetaDB;
		this.testMariaDB = testMariaDB;
		this.testSourceOracleDB = testSourceOracleDB;
		this.testTargetOracleDB = testTargetOracleDB;
	}
	
	public void testCalls() {
		
		String testMetaDB1 = testMetaDB.selectTestOne();
		String testMariaDB2 = testMariaDB.selectTestOne();
		String testSourceOracleDB3 = testSourceOracleDB.selectTestOne();
		String testTargetOracleDB4 = testTargetOracleDB.selectTestOne();
		
		
		System.out.println(
				  "\r  ##### 1 testMetaDB1 #### : " + testMetaDB1 
				+ "\r  ##### 2 testMariaDB2 #### : " + testMariaDB2 
				+ "\r  ##### 3 testSourceOracleDB3 #### : " + testSourceOracleDB3
				+ "\r  ##### 4 testTargetOracleDB4 #### : " + testTargetOracleDB4
				);
	}

}
