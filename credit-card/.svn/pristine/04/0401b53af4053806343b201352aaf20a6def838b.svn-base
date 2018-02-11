package com.pay.aile.bill.mail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pay.aile.bill.BillApplication;
import com.pay.aile.bill.service.mail.download.DownloadMail;

/***
 * DownloadMailTest.java
 *
 * @author shinelon
 *
 * @date 2017年10月30日
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BillApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DownloadMailTest {
    @Autowired
    public DownloadMail downloadMail;

    // 开启POP3授权码
    // @Test
    public void testHotMail() {
        try {
            downloadMail.execute("jinjing_0316@outlook.com", "bobo0316");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 开启POP3授权码
    @Test
    public void testMail126() {
        try {
            downloadMail.execute("czb18518679659@126.com", "12345qwerty");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 不需要POP3授权码
    // @Test
    public void testmail139() {
        try {
            downloadMail.execute("15701680705@139.com", "yanming1710");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 开启POP3授权码
    // @Test
    public void testMail163() {
        try {
            downloadMail.execute("yanming15@163.com", "yanming151");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // @Test
    public void testMail189() {
        try {
            downloadMail.execute("xxxx@189.cn", "vjbvejavwiqobcgf");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // @Test
    public void testMailAliyun() {
        try {
            downloadMail.execute("jinjing_0316@aliyun.com", "jinjing0316");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 开启POP3授权码
    // @Test
    public void testMailQQ() {
        try {
            downloadMail.execute("xxxx@qq.com", "vjbvejavwiqobcgf");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // @Test
    public void testMailWo() {
        try {
            downloadMail.execute("xxxx@wo.cn", "vjbvejavwiqobcgf");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
