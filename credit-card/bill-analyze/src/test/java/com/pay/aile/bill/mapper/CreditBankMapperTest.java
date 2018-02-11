package com.pay.aile.bill.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pay.aile.bill.BillAnalyzeApplication;
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
@SpringBootTest(classes = BillAnalyzeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CreditBankMapperTest {

    private static final Logger logger = LoggerFactory.getLogger(CreditBankMapperTest.class);
    @Autowired
    private CreditBankMapper creditBankMapper;

    @Autowired
    private CreditUserEmailRelationMapper relationMapper;

    @Test
    public void testInsert() {
//        List emailList = relationMapper.findByEmail(30L);
//        System.out.println(111);
    	
    	 CreditBank bank = new  CreditBank();
    	 
    	 bank.setName("456222");
    	 bank.setCode("11");
    	 
    	 creditBankMapper.insertOrUpdate(bank);
    	 
    	 System.out.println(bank.getId());
    	 
    }

    // @Test
    // public void testSelect() {
    // List<CreditBank> list = CreditBankMapper.selectList(null);
    // logger.info(list.toString());
    // }
    //
    // @Test
    // public void testUpdate() {
    // CreditBank entity = new CreditBank();
    // entity.setId(3L);
    // entity.setName("test333");
    // CreditBankMapper.updateById(entity);
    // }

}
