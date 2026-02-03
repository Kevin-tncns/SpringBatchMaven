package com.example.spbatch.mapper.oracle.source;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OracleEmployeeMapper {
	
	// 1. 데이터 읽기
    List<Map<String, Object>> sourceOracleMapper();
    
    // 2. 상태 업데이트 (String 대신 Map을 받거나, 정확한 ID 타입을 명시)
    // Map을 받으면 MyBatis XML에서 #{EMPLOYEEID}로 바로 접근 가능합니다.
    int updateOracleStatusY(Map<String, Object> item);
	
}
