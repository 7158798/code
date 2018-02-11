
/**
* @Title: MyHealthIndicator.java
* @Package com.pay.card.health
* @Description: TODO(用一句话描述该文件做什么)
* @author jing.jin
* @date 2017年12月20日
* @version V1.0
*/

package com.pay.card.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * @ClassName: MyHealthIndicator
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年12月20日
 *
 */
@Component
public class MyHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        int errorCode = check(); // perform some specific health check
        if (errorCode != 0) {
            return Health.down().withDetail("Error Code", errorCode).build();
        }
        return Health.up().build();
    }

    private int check() {
        // 对监控对象的检测操作
        return 0;
    }
}
