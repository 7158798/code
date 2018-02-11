/**
 * @Title: DevDataSourceConfig.java
 * @Package com.pay.card.config
 * @Description: TODO(用一句话描述该文件做什么)
 * @author jing.jin
 * @date 2017年11月20日
 * @version V1.0
 */

package com.pay.aile.bill.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.apache.ibatis.plugin.Interceptor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.baomidou.mybatisplus.entity.GlobalConfiguration;
import com.baomidou.mybatisplus.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.spring.MybatisSqlSessionFactoryBean;
/**
 * @ClassName: DevDataSourceConfig
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年11月20日
 */
@EnableTransactionManagement
@Configuration
//@ConditionalOnProperty(name = "environment", havingValue = "dev")
@MapperScan("com.pay.aile.bill.mapper*")
public class DevDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "primary.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean fb = new MybatisSqlSessionFactoryBean();
        fb.setDataSource(dataSource);
        fb.setTypeAliasesPackage("com.pay.aile.bill.entity");
        fb.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/*/*.xml"));
        PaginationInterceptor pagination = new PaginationInterceptor();
        fb.setPlugins(new Interceptor[] { pagination });
        GlobalConfiguration gcf = new GlobalConfiguration();
        gcf.setMetaObjectHandler(new ComMetaObjectHandler());
        gcf.setIdType(0);
        gcf.setDbColumnUnderline(true);
        fb.setGlobalConfig(gcf);
        return fb.getObject();
    }

    @Bean
    public PlatformTransactionManager txManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
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
