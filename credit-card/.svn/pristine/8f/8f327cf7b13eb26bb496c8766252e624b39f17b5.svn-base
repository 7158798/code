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
public class ABCAnalyzerTest {

    @Resource(name = "ABCAnalyzer")
    private BankMailAnalyzer ABCAnalyzer;
    @Autowired
    private MongoDownloadUtil downloadUtil;

    @Autowired
    CreditTemplateMapper creditTemplateMapper;

    @Test
    public void test() throws AnalyzeBillException {
        String content = "";
        try {
            // content = downloadUtil.getFile("中国农业银行金穗信用卡电子对账单");
            content = downloadUtil.getFile("INBOX|7c83a0149e25d1c000000001");
        } catch (AnalyzeBillException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        AnalyzeParamsModel amp = new AnalyzeParamsModel();
        amp.setOriginContent(content);
        amp.setBankCode("ABC");
        amp.setEmail("123@qq.com");
        amp.setBankId(1L);
        amp.setEmailId(1L);
        ABCAnalyzer.analyze(amp);
    }

}
