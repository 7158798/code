package com.pay.aile.bill.mongo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.pay.aile.bill.BillApplication;
import com.pay.aile.bill.exception.MailBillException;
import com.pay.aile.bill.utils.MongoDownloadUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BillApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class MongoTest {

    @Autowired
    private MongoDownloadUtil downloadUtil;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void downFile() {
        try {
            String s = downloadUtil.getFile("Inbox|223");
            System.out.println(s);
            // s = TextExtractUtil.parseHtml(s, "td");
            // System.out.println(s);
        } catch (MailBillException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // @Test
    // public void saveFile() {
    // try {
    // FileInputStream fis = new FileInputStream(
    // new
    // File("D:\\下载邮件html版本\\CMB_招商银行信用卡电子账单――账单分期最高送100元还款金！-20151017051832.html"));
    //
    // String sb = StreamUtils.copyToString(fis, Charset.forName("utf-8"));
    //
    // EmailFile ef = new EmailFile();
    // ef.setSubject("subject1");
    // ef.setContent(sb);
    // // 随机生成文件名
    // ef.setFileName("CMB_招商银行信用卡电子账单――账单分期最高送100元还款金！-20151017051832.html");
    // ef.setEmail("123@qq.com1");
    // mongoTemplate.insert(ef);
    // } catch (Exception e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }
}
