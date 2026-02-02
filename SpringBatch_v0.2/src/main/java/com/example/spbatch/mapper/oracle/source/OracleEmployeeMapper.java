package com.example.spbatch.mapper.oracle.source;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OracleEmployeeMapper {

	
	List<Map<String, Object>> sourceOracleMapper();
	
}
