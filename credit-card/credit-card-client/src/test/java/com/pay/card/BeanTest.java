
/**
* @Title: BeanTest.java
* @Package com.pay.card
* @Description: TODO(用一句话描述该文件做什么)
* @author jing.jin
* @date 2017年12月6日
* @version V1.0
*/

package com.pay.card;

import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

/**
 * @ClassName: BeanTest
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年12月6日
 *
 */

public class BeanTest {

    @Test
    public void mapToBean() {
        JSONObject cardJson = new JSONObject();
        // 卡号
        cardJson.put("cardNo", "3157");
        // 银行编码
        cardJson.put("bankCode", "BOC");
        // 持卡人
        cardJson.put("cardholder", "郑辉");
        System.out.println(JSONObject.toJSONString(cardJson));
    }
}
