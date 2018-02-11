package com.pay.aile.bill.service.mail.analyze.impl;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pay.aile.bill.BillAnalyzeApplication;
import com.pay.aile.bill.analyze.BankMailAnalyzer;
import com.pay.aile.bill.exception.AnalyzeBillException;

import com.pay.aile.bill.mapper.CreditTemplateMapper;
import com.pay.aile.bill.model.AnalyzeParamsModel;
import com.pay.aile.bill.utils.MongoDownloadUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BillAnalyzeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BOBAnalyzerTest {

    @Resource(name = "BOBAnalyzer")
    private BankMailAnalyzer BOBAnalyzer;
    @Autowired
    private MongoDownloadUtil downloadUtil;

    @Autowired
    CreditTemplateMapper creditTemplateMapper;

    @Test
    public void test() throws AnalyzeBillException {
        String content = "";
        try {
            content = downloadUtil.getFile("北京银行-信用卡电子账单");
        } catch (AnalyzeBillException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AnalyzeParamsModel amp = new AnalyzeParamsModel();
        amp.setOriginContent(content);
        amp.setBankCode("BOB");
        amp.setBankId(1L);
        amp.setEmail("123@qq.com");
        amp.setEmailId(1L);
        BOBAnalyzer.analyze(amp);
    }

}
