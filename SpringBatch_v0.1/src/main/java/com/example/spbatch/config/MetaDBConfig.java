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
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@MapperScan(
    basePackages = "com.example.spbatch.mapper.metamysql",
    sqlSessionFactoryRef = "metaSqlSessionFactory"
)
public class MetaDBConfig {

    @Primary
    @Bean(name = "metaDataSource")
    @ConfigurationProperties(prefix = "spring.datasource-mysql")
    public DataSource metaDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "metaSqlSessionFactory")
    public SqlSessionFactory metaSqlSessionFactory(
            @Qualifier("metaDataSource") DataSource dataSource) throws Exception {

        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        
        // mapper xml 경로 지정해주기 
        factoryBean.setMapperLocations(
            new PathMatchingResourcePatternResolver()
                .getResources("classpath:mapper/metamysql/*.xml")
        );
        
        return factoryBean.getObject();
    }

    @Primary
    @Bean(name = "metaTransactionManager")
    public PlatformTransactionManager metaTransactionManager(
            @Qualifier("metaDataSource") DataSource dataSource) {

        return new DataSourceTransactionManager(dataSource);
    }
}
