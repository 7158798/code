package com.pay.card.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@Configuration
public class DataSourceConfig {
    @Bean
    @ConfigurationProperties(prefix = "pay.datasource")
    public DataSource dataSourceInit() {
        return DataSourceBuilder.create().build();
    }

}