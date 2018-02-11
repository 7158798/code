package com.pay.aile.bill.web;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pay.aile.bill.analyze.IParseMail;
import com.pay.aile.bill.model.CreditFileModel;

@Controller
public class TestContoller {

    @Resource
    private IParseMail parseMail;

    @RequestMapping("/test/analyze")
    @ResponseBody
    public String analyze() {
        try {
            parseMail.execute();
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @RequestMapping("/test/analyzeNoDb")
    @ResponseBody
    public String analyzeNoDb() {
        try {
            CreditFileModel cfm = new CreditFileModel();
            cfm.setCreateDate(new Date());
            cfm.setEmail("123@qq.com");
            cfm.setEmailId(1L);
            cfm.setFileName("中国工商银行");
            cfm.setId(1L);
            cfm.setMailType("HTML");
            cfm.setSentDate(new Date());
            cfm.setStatus(1);
            cfm.setSubject("中国工商银行");
            cfm.setUpdateDate(new Date());
            parseMail.executeParseFile(cfm);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}
