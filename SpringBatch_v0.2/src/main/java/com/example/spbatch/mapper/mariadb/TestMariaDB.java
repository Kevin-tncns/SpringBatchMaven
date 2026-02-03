package com.example.spbatch.mapper.mariadb;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TestMariaDB {
	
	String selectTestOne();

}
