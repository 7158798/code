
/**
* @Title: AstrotrainProducerConfig.java
* @Package com.pay.card.config
* @Description: TODO(用一句话描述该文件做什么)
* @author jing.jin
* @date 2017年12月8日
* @version V1.0
*/

package com.pay.card.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @ClassName: AstrotrainProducerConfig
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年12月8日
 *
 */

@Configuration
public class AstrotrainProducerConfig {
    @Value("${astrotrain.appId}")
    private String appId;
    @Value("${astrotrain.group.name}")
    private String groupName;
    @Value("${astrotrain.instance.name}")
    private String instanceName;
    @Value("${astrotrain.namesrv.address}")
    private String namesrvAddr;

    /**
     * 消息生产者注册
     *
     * @Description 一句话描述方法用法
     * @return
     * @see 需要参考的类或方法
     */
//    @Bean(name = "atProducer", initMethod = "start", destroyMethod = "shutdown")
//    public DefaultATProducer defaultProducer() {
//        DefaultATProducer producer = new DefaultATProducer();
//        producer.setAppId(appId);
//        producer.setGroupName(groupName);
//        producer.setInstanceName(instanceName);
//        producer.setNamesrvAddr(namesrvAddr);
//        return producer;
//    }
}
