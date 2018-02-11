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
import com.pay.aile.bill.model.AnalyzeParamsModel;
import com.pay.aile.bill.utils.MongoDownloadUtil;
import com.pay.aile.bill.utils.TextExtractUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BillAnalyzeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BOCAnalyzerTest {

    @Resource(name = "BOCAnalyzer")
    private BankMailAnalyzer BOCAnalyzer;
    @Autowired
    private MongoDownloadUtil downloadUtil;

    @Test
    public void test() throws AnalyzeBillException {
        String content = "";
        try {
            content = downloadUtil.getFile("INBOX|1tbikBaLWFag3N99LgAAsz");
        } catch (AnalyzeBillException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // CreditTemplate rules = creditTemplateMapper.selectById(3);
        content = TextExtractUtil.parseHtml(content, "td");
        // content = TextExtractUtil.parseHtmlBoc(content);

        System.out.println(content);
        AnalyzeParamsModel amp = new AnalyzeParamsModel();
        amp.setContent(content);
        amp.setBankCode("BOC");
        amp.setBankId(1L);
        amp.setEmail("czb18518679659@126.com");
        // amp.setEmailId(6L);
        BOCAnalyzer.analyze(amp);
    }
}
