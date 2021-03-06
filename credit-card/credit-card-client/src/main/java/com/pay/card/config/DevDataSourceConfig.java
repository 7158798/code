/**
 * @Title: DevDataSourceConfig.java
 * @Package com.pay.card.config
 * @Description: TODO(用一句话描述该文件做什么)
 * @author jing.jin
 * @date 2017年11月20日
 * @version V1.0
 */

package com.pay.card.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @ClassName: DevDataSourceConfig
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年11月20日
 */
@EnableTransactionManagement
@Configuration
@ConditionalOnProperty(name = "environment", havingValue = "dev")
public class DevDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "primary.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * @Title: getJdbcTemplate @Description: 增加JdbcTemplate模版 @param @param
     *         dataSource @param @return 参数 @return JdbcTemplate 返回类型 @throws
     */
    @Bean
    public JdbcTemplate getJdbcTemplate(DataSource dataSource) {

        return new JdbcTemplate(dataSource);
    }

}
