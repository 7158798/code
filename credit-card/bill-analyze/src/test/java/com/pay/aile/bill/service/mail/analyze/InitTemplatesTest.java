package com.pay.aile.bill.service.mail.analyze;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pay.aile.bill.BillAnalyzeApplication;
import com.pay.aile.bill.analyze.banktemplate.BaseBankTemplate;
import com.pay.aile.bill.entity.CreditTemplate;
import com.pay.aile.bill.service.CreditTemplateService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BillAnalyzeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InitTemplatesTest {

    @Autowired
    private List<BaseBankTemplate> templates;

    @Resource
    private CreditTemplateService creditTemplateService;

//    @Test
//    public void initTemplates() {
//        System.out.println(templates.size());
//        templates.forEach(t -> {
//            CreditTemplate ct = t.getRules();
//            if (ct != null) {
//                creditTemplateService.saveOrUpdate(ct);
//            }
//        });
//    }
}
