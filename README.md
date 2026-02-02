------------------------------------------------------------------------------------------------------------------------------------------------
## 기술 Stack
🔹 Language & Runtime 	: Java 17
🔹 Persistence  			: MyBatis 3.0.5
🔹 Build Tool  			: Maven
🔹 Framework  			: Spring Boot 3.5.10  , Spring Batch
🔹 Database
 	- Oracle – 원천 / 타겟 Business DB
 	- MariaDB – STG (Source) DB
 	- MySQL – Batch Meta DB
 	 	- 실행 이력
 	 	- 실패 / 에러 로그
 	 	- 재실행 시점 관리
------------------------------------------------------------------------------------------------------------------------------------------------
📌 아키텍처 특징 (한 줄 요약용)

다중 DB 환경에서 MyBatis 기반 ETL 파이프라인을 구성하고,
Spring Batch MetaDB(MySQL) 를 통해 재실행·장애 복구가 가능한 구조


##  설계 핵심 포인트
✔ Meta DB 분리 (비즈니스 DB 영향 최소화)
✔ 멀티 DB Read / Write 지원
✔ MyBatis 단일 ORM 전략
✔ 재시작 가능한 배치 구조
✔ 운영 환경 친화적 구조

------------------------------------------------------------------------------------------------------------------------------------------------
## ETL 흐름 : 
 1 , Oracle   (hr계정 원천테이블 1개) 	→ mariaDB 	(stg, source_테이블)
 2 , mariaDB  (source_테이블) 		→ OracleDB	(target_테이블)
 3 , OracleDB (target_테이블) 		→ MySQL  	(metaDB : 로그기록/실패/에러/재실행시점)
 4 , metaDB   (기타 처리 ) 


비고  	: 
 	, REST 기반으로 트리거되는 Spring Batch Job이 MyBatis를 통해 멀티 DB 데이터를 처리하고, 실행 이력은 별도의 Meta DB에서 관리되는 구조
 	, 본 테스트 샘플링 프로젝트는 Batch + MyBatis 기반 ETL 구조로, DB Row 표현을 위한 VO를 사용하며 DTO는 사용하지 않는다.



작성자 : 최 수 춘 
작성일 : 20260201 	
------------------------------------------------------------------------------------------------------------------------------------------------

1, MariaDB – STG 테이블 만들기 : stg_employees 
2️, 공용 VO (Oracle ↔ Maria 공통) : EmployeeVO
3, Oracle Mapper (select) -> MariaDB Mapper (insert) 



## 기타 사용 테이블 
, mariaDB
CREATE TABLE stg_employees (
    employee_id 	BIGINT,
    first_name  	VARCHAR(50),
    last_name   	VARCHAR(50),
    email       	VARCHAR(100),
    job_id      	VARCHAR(20),
    salary      	DECIMAL(10,2),
    hire_date   	DATE,
    src_dt      	DATETIME DEFAULT NOW()
);

ALTER TABLE stg_employees ADD COLUMN etl_batch_id VARCHAR(50);

, MySQLDB(metaDB)
CREATE TABLE etl_batch_log (
    batch_id  		VARCHAR(50) PRIMARY KEY
  , step_name  		VARCHAR(50)
  , status  		VARCHAR(20)
  , start_time 		DATETIME
  , end_time 		DATETIME
  , success_cnt 	INT
  , fail_cnt 		INT
  , error_msg 		TEXT
);


# Oracle에 target_hr 계정 생성 → 권한 부여 → 해당 계정 아래에 target 테이블 생성
, CREATE TABLE target_employees (
    employee_id NUMBER,
    first_name  VARCHAR2(50),
    last_name   VARCHAR2(50),
    email       VARCHAR2(100),
    job_id      VARCHAR2(20),
    salary      NUMBER(10,2),
    hire_date   DATE,
    etl_batch_id    VARCHAR2(20),
    ins_dt      DATE DEFAULT SYSDATE
);
