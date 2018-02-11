package com.pay.aile.bill.job;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pay.aile.bill.BillApplication;

/***
 * DownloadMailSchedulerTest.java
 *
 * @author shinelon
 *
 * @date 2017年11月8日
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BillApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DownloadMailSchedulerTest {
    private static final Logger logger = LoggerFactory.getLogger(DownloadMailSchedulerTest.class);
    @Autowired
    private DownloadMailScheduler downloadMailScheduler;

    // @Test
    public void testJob() {
        downloadMailScheduler.downLoadMail();
    }

    // @Test
    // public void testJobLoop() throws InterruptedException {
    // downloadMailScheduler.downLoadMailLoop();
    // Thread.sleep(100000);
    // // downloadMailScheduler.offJobLoop();
    // }
}
