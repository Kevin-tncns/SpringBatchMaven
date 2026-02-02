package com.example.spbatch.mapper.mariadb;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StgEmployeeMapper {
	
	int insertStgMariaDB(Map<String, Object> param);

}
