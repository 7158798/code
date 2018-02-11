package com.pay.aile.bill.mail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pay.aile.bill.BillApplication;
import com.pay.aile.bill.entity.CreditEmail;
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
     @Test
    public void testHotMail() {
        try {
            downloadMail.execute("jinjing_0316@outlook.com", "bobo0316");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 开启POP3授权码
    //@Test
    public void testMail126() {
        try {
            downloadMail.execute("jinjing_0316@126.com", "bobo0316");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 不需要POP3授权码
     //@Test
    public void testmail139() {
        try {
            downloadMail.execute("huiwin@139.com", "hui22303");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 开启POP3授权码
     //@Test
    public void testMail163() {
        try {
            downloadMail.execute("goufei1bill@163.com", "mlispig1billimap");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // @Test
    public void testMail189() {
        try {
            downloadMail.execute("huiwin@189.cn", "hui22303");
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
        	long startTime = System.currentTimeMillis();
        	CreditEmail creditEmail = new CreditEmail();
        	creditEmail.setEmail("19315368@qq.com");
        	creditEmail.setPassword("azfpvjszvrsfbidc");
            //downloadMail.execute("19315368@qq.com", "azfpvjszvrsfbidc"); rbwtqrzimmqubajf
            downloadMail.execute("690754534@qq.com", "rbwtqrzimmqubajf");
            						
            long endTime = System.currentTimeMillis();
            
            System.out.println("一共执行了 ============="+(endTime-startTime));
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
    // @Test
    public void testMailSina() {
        try {
            downloadMail.execute("easontian@sina.com", "cindy818");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
