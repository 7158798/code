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
public class PSBCAnalyzerTest {

    @Resource(name = "PSBCAnalyzer")
    private BankMailAnalyzer PSBCAnalyzer;
    @Autowired
    private MongoDownloadUtil downloadUtil;

    @Test
    public void test() throws AnalyzeBillException {
        String content = "";
        try {
            content = downloadUtil.getFile("c2843547-68ed-460b-a74d-c77b89a4f5a0");
        } catch (AnalyzeBillException e) {
            e.printStackTrace();
        }

        content = TextExtractUtil.parseHtml(content, "font");
        System.out.println(content);
        AnalyzeParamsModel amp = new AnalyzeParamsModel();
        amp.setContent(content);
        amp.setOriginContent(content);
        amp.setBankCode("PSBC");
        amp.setBankId(1L);
        amp.setEmail("czb18518679659@126.com");
        // amp.setEmailId(6L);
        PSBCAnalyzer.analyze(amp);
    }

}
