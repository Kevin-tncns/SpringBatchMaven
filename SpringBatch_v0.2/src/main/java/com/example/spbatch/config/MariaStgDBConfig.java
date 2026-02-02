package com.example.spbatch.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@MapperScan(
    basePackages = "com.example.spbatch.mapper.mariadb",
    sqlSessionFactoryRef = "mariaSqlSessionFactory"
)
public class MariaStgDBConfig {

    @Bean(name = "mariaDataSource")
    @ConfigurationProperties(prefix = "spring.datasource-mariadb")
    public DataSource mariaDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "mariaSqlSessionFactory")
    public SqlSessionFactory mariaSqlSessionFactory(
            @Qualifier("mariaDataSource") DataSource dataSource) throws Exception {

        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        
        // mapper xml 경로 지정해주기 
        factoryBean.setMapperLocations(
            new PathMatchingResourcePatternResolver()
                .getResources("classpath:mapper/mariadb/*.xml")
        );

        
        return factoryBean.getObject();
    }

    @Bean(name = "mariaTransactionManager")
    public PlatformTransactionManager mariaTransactionManager(
            @Qualifier("mariaDataSource") DataSource dataSource) {

        return new DataSourceTransactionManager(dataSource);
    }
}
