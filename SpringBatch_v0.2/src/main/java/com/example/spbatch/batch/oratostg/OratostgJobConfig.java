package com.example.spbatch.batch.oratostg;

import java.util.Iterator;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.spbatch.mapper.mariadb.StgEmployeeMapper;
import com.example.spbatch.mapper.oracle.source.OracleEmployeeMapper;

@Configuration
@EnableBatchProcessing
public class OratostgJobConfig {
	
	
	
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
				.<Map<String, Object>, Map<String, Object>> chunk(5, mariaTxManager)
				.reader(oracleItemReader())
				.writer(stgItemWriter())
				.build();
	}
	
	
	/* Reader */
	@Bean
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

	
	/* Writer */
	@Bean
	public ItemWriter<Map<String, Object>> stgItemWriter() {
	    return items -> {
	        String batchId = String.valueOf(System.currentTimeMillis());

	        for (Map<String, Object> item : items) {
	            item.put("etlBatchId", batchId);
	            stgMapper.insertStgMariaDB(item);
	        }
	    };
	}
	

}
