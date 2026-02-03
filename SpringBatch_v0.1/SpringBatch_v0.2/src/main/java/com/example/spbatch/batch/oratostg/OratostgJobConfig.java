package com.example.spbatch.batch.oratostg;

import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.spbatch.batch.runner.JobRunner;
import com.example.spbatch.mapper.mariadb.StgEmployeeMapper;
import com.example.spbatch.mapper.oracle.source.OracleEmployeeMapper;

@Configuration
@EnableBatchProcessing
public class OratostgJobConfig {
	
	private static final Logger log = LoggerFactory.getLogger(JobRunner.class);
	
	private final JobRepository 				jobRepository;
	private final PlatformTransactionManager 	mariaTxManager;
	private final OracleEmployeeMapper  		oracleMapper;
	private final StgEmployeeMapper 			stgMapper;
	


	/**
	 * @param jobRepository
	 * @param mariaTxManager
	 * @param oracleMapper
	 * @param stgMapper
	 */
	public OratostgJobConfig(JobRepository jobRepository, PlatformTransactionManager mariaTxManager,
			OracleEmployeeMapper oracleMapper, StgEmployeeMapper stgMapper) {
		super();
		this.jobRepository 	= jobRepository;
		this.mariaTxManager = mariaTxManager;
		this.oracleMapper 	= oracleMapper;
		this.stgMapper 		= stgMapper;
	}
	

	@Bean
	public Job oracleToStgJob() {
		
		return new JobBuilder("oracleToStgJob", jobRepository)
				.start(oracleToStgStep())
				.build();
		
	}
	
	@Bean
	public Step oracleToStgStep() {
		
		return new StepBuilder("oracleToStgStep", jobRepository)
				.<Map<String, Object>, Map<String, Object>> chunk(10, mariaTxManager)
				.reader(oracleItemReader())
				.processor(oracleToStgProcessor(null))	// processor 추가 
				.writer(stgItemWriter(null))
				.build();
	}
	
	/* 1. Processor: 여기서 배치 파라미터를 Map에 심어줍니다 */
    @Bean
    @StepScope
    public ItemProcessor<Map<String, Object>, Map<String, Object>> oracleToStgProcessor(
            @Value("#{jobParameters['datetime']}") String datetime) {
        return item -> {
            // 배치 실행 시 넘어온 datetime 값을 etlBatchId로 설정
            item.put("etlBatchId", datetime);
            return item;
        };
    }
    
	/* Reader */
	@Bean
	@StepScope // 이게 있어야 매 10초마다 Reader가 새로 생성되어 데이터를 다시 읽습니다!
	public ItemReader<Map<String, Object>> oracleItemReader() {
	    return new ItemReader<>() {
	        private Iterator<Map<String, Object>> iterator;

	        @Override
	        public Map<String, Object> read() {
	            if (iterator == null) {
	                iterator = oracleMapper.sourceOracleMapper().iterator();
	            }
	            return iterator.hasNext() ? iterator.next() : null;
	        }
	    };
	}

	
	/* Writer에서 직접 파라미터 받기 */
	@Bean
	@StepScope // 파라미터를 쓰려면 여전히 이게 필요해요!
	public ItemWriter<Map<String, Object>> stgItemWriter(
	        @Value("#{jobParameters['datetime']}") String datetime) {
	    return items -> {
	        for (Map<String, Object> item : items) {
	            // 1. MariaDB (Target)에 Insert
	            item.put("etlBatchId", datetime); // 매번 새로 생성하는 대신, 주입받은 동일한 datetime을 사용!
	            stgMapper.insertStgMariaDB(item);

	            // 2. Oracle (Source)에 '나 읽었음' 표시 업데이트
	            // 여기서 item 안의 'EMPLOYEEID'를 사용해 해당 Row만 Y로 바꿉니다.
	            oracleMapper.updateOracleStatusY(item);
	        }
	        log.info(">>>> {}건 전송 및 Flag 업데이트 완료!", items.size());
	    };
	}
	

}
