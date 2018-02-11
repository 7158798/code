package com.pay.aile.bill.analyze.impl;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pay.aile.bill.BillAnalyzeApplication;
import com.pay.aile.bill.analyze.IParseMail;
import com.pay.aile.bill.entity.CreditFile;
import com.pay.aile.bill.model.CreditFileModel;
import com.pay.aile.bill.service.CreditFileService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BillAnalyzeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ParseMailImplTest {
    @Resource(name = "parseMail")
    private IParseMail parseMail;

    @Resource
    private CreditFileService creditFileService;

    @Test
    public void parseFile() {
        CreditFile creditFile = creditFileService.findById(35L);
        CreditFileModel creditFileModel = new CreditFileModel();
        BeanUtils.copyProperties(creditFile, creditFileModel);
        parseMail.executeParseFile(creditFileModel);
    }
}
