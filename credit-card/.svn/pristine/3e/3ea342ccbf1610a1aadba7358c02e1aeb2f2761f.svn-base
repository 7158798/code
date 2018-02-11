package com.pay.aile.bill.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pay.aile.bill.entity.CreditEmail;
import com.pay.aile.bill.entity.CreditFile;
import com.pay.aile.bill.exception.MailBillException;
import com.pay.aile.bill.job.RedisJobHandle;
import com.pay.aile.bill.service.CreditEmailService;
import com.pay.aile.bill.service.mail.download.DownloadMail;
import com.pay.aile.bill.service.mail.relation.CreditFileRelation;
import com.pay.aile.bill.utils.MongoDownloadUtil;

@Controller
public class EmailController {
    @Autowired
    public CreditEmailService creditEmailService;

    @Autowired
    public DownloadMail downloadMail;
    @Autowired
    private CreditFileRelation creditFileRelation;

    @Autowired
    private MongoDownloadUtil mongoDownloadUtil;
    @Autowired
    private RedisJobHandle redisJobHandle;

    @RequestMapping(value = "/emailForm")
    public String emailForm(Model map, CreditEmail creditEmail) {

        return "emailForm";
    }

    @RequestMapping(value = "/emailList")
    public String emailList(Model map, CreditEmail creditEmail) {
        List emailList = creditEmailService.getEmailList(creditEmail);
        map.addAttribute("emailList", emailList);
        return "emailList";
    }

    @RequestMapping(value = "/getBill")
    public String getBill(CreditEmail creditEmail, String emailKey) {

        creditEmail.setEmail(creditEmail.getEmail() + "@" + emailKey);

        try {
            String pw = creditEmail.getPassword();
            creditEmail = creditEmailService.saveOrUpdate(creditEmail);
            // 添加email到任务队列中
            creditEmail.setPassword(pw);
            redisJobHandle.addJob(creditEmail);
            // downloadMail.execute(creditEmail);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "redirect:/emailList";
    }

    @RequestMapping(value = "/getEmailDate")
    @ResponseBody
    public String getEmailDate(CreditEmail email) {

        return "emailList";
    }

    @RequestMapping(value = "/getFile")
    @ResponseBody
    public String getFile(String fileName) {
        try {
            return mongoDownloadUtil.getFile(fileName);
        } catch (MailBillException e) {
            return "";
        }

    }

    @RequestMapping(value = "/showEmail")
    public String showEmail(CreditEmail email, Model model) {
        // List<EmailFile> fileList = new
        // view.addObject(attributeName, attributeValue)
        List<CreditFile> fileList = creditFileRelation.selectCreditFiles(email.getEmail());
        model.addAttribute("fileList", fileList);
        return "showEmail";
    }
}
