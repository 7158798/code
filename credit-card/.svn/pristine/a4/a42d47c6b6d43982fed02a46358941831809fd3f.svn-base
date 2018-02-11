package com.pay.aile.bill.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pay.aile.bill.BillApplication;
import com.pay.aile.bill.service.CreditBankService;

/***
 * CreditBankServiceImplTest.java
 *
 * @author shinelon
 *
 * @date 2017年11月7日
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BillApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CreditBankServiceImplTest {
    private static final Logger logger = LoggerFactory.getLogger(CreditBankServiceImplTest.class);
    @Autowired
    private CreditBankService creditBankService;

    @Test
    public void testInitKeywords() {
        creditBankService.initKeywords();
    }
}
