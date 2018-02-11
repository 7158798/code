package com.pay.aile.bill.service.mail.analyze.impl;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pay.aile.bill.BillAnalyzeApplication;
import com.pay.aile.bill.analyze.IParseMail;
import com.pay.aile.bill.contant.Constant;
import com.pay.aile.bill.entity.CreditTemplate;
import com.pay.aile.bill.utils.JedisClusterUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BillAnalyzeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ParseMailImplTest {

	@Resource
	private IParseMail parseMail;

	@Test
	public void testExecute() {
		parseMail.execute();
	}

}
