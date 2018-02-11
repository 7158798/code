package com.pay.aile.bill.mapper;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pay.aile.bill.BillApplication;
import com.pay.aile.bill.entity.CreditBank;

/***
 * TestMapperTest.java
 *
 * @author shinelon
 *
 * @date 2017年11月1日
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BillApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CreditBankMapperTest {

    private static final Logger logger = LoggerFactory.getLogger(CreditBankMapperTest.class);
    @Autowired
    private CreditBankMapper CreditBankMapper;

    @Test
    public void testInsert() {
        CreditBank entity = new CreditBank();
        entity.setName("test2");
        CreditBankMapper.insert(entity);
    }

    @Test
    public void testSelect() {
        List<CreditBank> list = CreditBankMapper.selectList(null);
        logger.info(list.toString());
    }

    @Test
    public void testUpdate() {
        CreditBank entity = new CreditBank();
        entity.setId(3L);
        entity.setName("test333");
        CreditBankMapper.updateById(entity);
    }

}
