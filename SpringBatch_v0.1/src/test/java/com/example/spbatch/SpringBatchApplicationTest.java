package com.example.spbatch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.spbatch.mapper.metamysql.TestMetaDB;
import com.example.spbatch.service.TestDBService;


@SpringBootTest
@ActiveProfiles("test")
class SpringBatchApplicationTest {

	
	@Autowired
	private  TestDBService testDBService;
	

	/**
	 * service 호출하여 DB-4개 연결 테스트
	 * , sourceDB 	: Oracle
	 * , stgDB  	: MariaDB 
	 * , targetDB 	: Oracle
	 * , metaDB 	: MySQL
	 */
	@Test
	public void healthCheck() {
	    try { 
	    	testDBService.testCalls();
			System.out.println("##### Success : ##### " );
	    } catch(Exception e) { 
	    	System.out.println("##### Exception : ##### " + e.getMessage());
	    }
	}

}
