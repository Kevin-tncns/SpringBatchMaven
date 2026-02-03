package com.example.spbatch.batch.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@EnableScheduling 
public class JobRunner {

    private static final Logger log = LoggerFactory.getLogger(JobRunner.class);

    private final JobLauncher jobLauncher;
    private final Job oracleToStgJob;

    public JobRunner(JobLauncher jobLauncher, Job oracleToStgJob) {
        this.jobLauncher = jobLauncher;
        this.oracleToStgJob = oracleToStgJob;
    }

    /**
     * Cron 표현식: 초 분 시 일 월 요일
     * 예: "0 0/5 * * * *" -> 5분마다 실행
     * 예: "0 0 1 * * *"   -> 매일 새벽 1시에 실행
     * fixedRate = 10000: 이전 작업 시작 시간으로부터 10초마다 실행 (단위: ms)
     */
//    @Scheduled(cron = "0 0/5 * * * *") // 현재 5분마다 실행되도록 설정됨
    @Scheduled(fixedRate = 5000) 
    public void runJobEveryTenSeconds() {
        // 현재 시간을 예쁘게 포맷팅해서 로그에 출력
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        log.info("==========================================================");
        log.info(">>>>> [스케줄러 동작] 실행 시간: {}", now);
        log.info("==========================================================");

        JobParameters params = new JobParametersBuilder()
                .addString("datetime", LocalDateTime.now().toString()) // 매번 다른 파라미터를 넘겨야 배치가 실행됨
//                .addLong("uniqueness", System.nanoTime()) // 나노초 단위 고유값 추가
                .toJobParameters();

        try {
            jobLauncher.run(oracleToStgJob, params);
//            log.info(">>>>> [결과] 10초 주기 배치 성공적으로 완료");
            log.info(">>>>> [결과] 5초 주기 배치 성공적으로 완료");
        } catch (Exception e) {
            log.error(">>>>> [에러] 배치 실행 중 문제 발생: {}", e.getMessage());
        }
    }
}