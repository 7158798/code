package com.pay.aile.bill.service.impl;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pay.aile.bill.BillApplication;
import com.pay.aile.bill.entity.CreditFile;
import com.pay.aile.bill.service.mail.relation.CreditFileRelation;

/***
 * CreditFileRelationImplTest.java
 *
 * @author shinelon
 *
 * @date 2017年11月8日
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BillApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CreditFileRelationImplTest {
    private static final Logger logger = LoggerFactory.getLogger(CreditFileRelationImplTest.class);

    @Autowired
    private CreditFileRelation creditFileRelation;

    @Test
    public void testSelect() {
        List<CreditFile> list = creditFileRelation.selectCreditFiles("jinjing_0316@outlook.com");
        logger.info(list.toString());
    }

}
