package com.pay.aile.bill.service.impl;

import java.util.Base64;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pay.aile.bill.BillApplication;

/***
 * Base64Test.java
 *
 * @author shinelon
 *
 * @date 2017年11月8日
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BillApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class Base64Test {

    private static final Logger logger = LoggerFactory.getLogger(Base64Test.class);

    @Test
    public void test() {
        String encode = Base64.getEncoder().encodeToString("123456".getBytes());
        logger.info("encode:{}", encode);
        String decode = new String(Base64.getDecoder().decode(encode));
        logger.info("decode:{}", decode);
    }
}
