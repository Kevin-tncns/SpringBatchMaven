//package com.example.spbatch.batch.runner;
//
//import java.util.Map;
//
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.JobParametersBuilder;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.core.env.Environment;
//import org.springframework.stereotype.Component;
//
//@Component
//public class BatchRunner implements CommandLineRunner {
//	
//    private final JobLauncher jobLauncher;
//    private final Map<String, Job> jobMap;   // ⭐ 핵심
//    private final Environment env;
//	/**
//	 * @param jobLauncher
//	 * @param jobMap
//	 * @param env
//	 */
//	public BatchRunner(JobLauncher jobLauncher, Map<String, Job> jobMap, Environment env) {
//		super();
//		this.jobLauncher = jobLauncher;
//		this.jobMap = jobMap;
//		this.env = env;
//	}
//	
//	@Override
//	public void run(String... args) throws Exception {
//		
//		if (args.length == 0) {
//			throw new IllegalArgumentException("실행할 Job 이름을 입력하세요.");
//		}
//		
//		String jobName = args[0];
//		
//		Job job = jobMap.get(jobName);
//		
//	    if (job == null) {
//	        throw new IllegalArgumentException("존재하지 않는 Job: " + jobName);
//	    }
//	    
//	    jobLauncher.run(
//	    		job , new JobParametersBuilder()
//	    		.addLong("runtime", System.currentTimeMillis())
//	    		.toJobParameters()
//	    		);
//	    
//		
//	}
//    
//    
//
//}
