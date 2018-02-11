package com.pay.aile.bill.mongo;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import com.pay.aile.bill.BillApplication;
import com.pay.aile.bill.entity.CreditEmail;
import com.pay.aile.bill.entity.CreditFile;
import com.pay.aile.bill.entity.EmailFile;
import com.pay.aile.bill.exception.MailBillException;
import com.pay.aile.bill.utils.ApacheMailUtil;
import com.pay.aile.bill.utils.MongoDownloadUtil;

/***
 * EmlToMongoUtil.java
 *
 * @author shinelon
 *
 * @date 2017年11月7日
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BillApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmlToMongoUtilTest {
    private static final Logger logger = LoggerFactory.getLogger(EmlToMongoUtilTest.class);
    @Autowired
    private MongoDownloadUtil MongoDownloadUtil;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void emlToMongo() throws Exception {
        logger.info("emlToMongo");

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        InputStream inMsg = new FileInputStream("D:\\Fw_ 中国建设银行信用卡电子账单.eml");
        Message msg = new MimeMessage(session, inMsg);
        CreditEmail creditEmail = new CreditEmail();
        creditEmail.setEmail("123@qq.com");
        creditEmail.setId(1L);
        EmailFile emailFile = ApacheMailUtil.getEmailFile(msg, creditEmail);
        emailFile.setFileName("Fw_ 中国建设银行信用卡电子账单");
        CreditFile creditFile = ApacheMailUtil.getCreditFile(emailFile, creditEmail);

        List<EmailFile> emailFileList = new ArrayList<>();
        emailFileList.add(emailFile);
        List<CreditFile> creditFileList = new ArrayList<>();
        creditFileList.add(creditFile);
        MongoDownloadUtil.saveEmailFiles(emailFileList);
        // MongoDownloadUtil.saveFile(emailFileList, creditFileList);
    }

    // @Test
    public void testQuery() throws MailBillException {
        Criteria criteria = new Criteria("fileName");
        criteria.is("fef4a602-3378-4fdd-8e1e-296e0a958896");
        Query query = new Query(criteria);
        EmailFile ef = mongoTemplate.findOne(query, EmailFile.class);
        // logger.info("EmailFile:{}", ef);
    }

    @Test
    public void testSaveEmailFiles() {
        List<EmailFile> list = new ArrayList<>();
        EmailFile emailFile1 = new EmailFile();
        emailFile1.setFileName("fef4a602-3378-4fdd-8e1e-296e0a958896");
        EmailFile emailFile2 = new EmailFile();
        emailFile2.setFileName("fef4a602-1231");
        list.add(emailFile1);
        list.add(emailFile2);
        MongoDownloadUtil.saveEmailFiles(list);
    }
}
