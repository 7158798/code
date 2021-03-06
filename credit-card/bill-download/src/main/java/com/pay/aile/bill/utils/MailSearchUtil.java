package com.pay.aile.bill.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SentDateTerm;
import javax.mail.search.SubjectTerm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;

import com.pay.aile.bill.contant.BankKeywordContants;

/***
 * MailSearchUtil.java
 *
 * @author shinelon
 *
 * @date 2017年10月30日
 *
 */
public class MailSearchUtil {
    private static final Logger logger = LoggerFactory.getLogger(MailSearchUtil.class);

    // 默认邮件开始搜索日期距离当前日期的月份偏移量
    // 默认抓取距今6个月的邮件
    private static final String DEFAULT_START_SEARCH_DATE_MONTH_OFFSET = "-6";

    private static final String REDIS_START_SEARCH_DATE_MONTH_OFFSET = "bill-download-month-offset";

    public static int getMonthOffset() {
        String value = JedisClusterUtils.getString(REDIS_START_SEARCH_DATE_MONTH_OFFSET);
        if (StringUtils.hasText(value)) {
            return Integer.valueOf(value);
        } else {
            JedisClusterUtils.saveString(REDIS_START_SEARCH_DATE_MONTH_OFFSET, DEFAULT_START_SEARCH_DATE_MONTH_OFFSET);
            return Integer.valueOf(DEFAULT_START_SEARCH_DATE_MONTH_OFFSET);
        }
    }

    /***
     * 根据主题搜索关键字
     *
     * @param queryKey
     * @param folder
     * @return
     * @throws MessagingException
     */
    public static Message[] search(String queryKey, Folder folder) throws MessagingException {
        SearchTerm searchTerm = getSearchTermAfterDate(queryKey);
        return folder.search(searchTerm);
    }

    /***
     * 根据主题搜索关键字-多线程搜索
     *
     * @注意 性能瓶颈在网络传输，多线程搜索效率没有提升
     *
     * @param queryKey
     * @param folder
     * @param taskExecutor
     * @return
     * @throws MessagingException
     */
    public static Message[] search(String queryKey, Folder folder, ThreadPoolTaskExecutor taskExecutor)
            throws MessagingException {
        Message[] messageAry = folder.getMessages();
        return mulitSearch(queryKey, messageAry, taskExecutor);
    }

    /**
     *
     * @Description
     * @param queryKey
     * @return
     * @see 需要参考的类或方法
     * @author chao.wang
     * @throws MessagingException
     */
    public static Message[] searchByCount(String queryKey, Folder folder) throws MessagingException {
        int count = folder.getMessageCount();
        int start = count - 199;
        int end = count;
        String[] keywords = queryKey.split(BankKeywordContants.BANK_KEYWORD_SEPARATOR);
        Date halfYear = DateUtil.dateCompute(new Date(), Calendar.MONTH, getMonthOffset());
        List<Message> result = new ArrayList<>();
        Date latestDate = new Date();
        do {
            if (start < 1) {
                start = 1;
            }
            logger.info("start ==== " + start + "; end ==== " + end);
            Message[] messages = folder.getMessages(start, end);
            latestDate = messages[0].getSentDate();
            for (Message m : messages) {
                MimeMessage msg = (MimeMessage) m;
                String subject = MailDecodeUtil.getSubject(msg);
                Date sentDate = msg.getSentDate();
                for (String keyword : keywords) {
                    if (subject.contains(keyword) && sentDate.after(halfYear)) {
                        result.add(m);
                        break;
                    }
                }
            }
            start = start - 200;
            end = end - 200;
        } while (latestDate.after(halfYear) && start > 1);
        if (result.isEmpty()) {
            return new Message[0];
        } else {
            return result.toArray(new Message[result.size()]);
        }
    }

    public static Message[] searchByCountMultiThread(String queryKey, Folder folder) throws MessagingException {
        ExecutorService es = Executors.newFixedThreadPool(5);
        List<FutureTask<Message[]>> taskList = new ArrayList<>();
        int count = folder.getMessageCount();
        int start = count - 99;
        int end = count;
        String[] keywords = queryKey.split(BankKeywordContants.BANK_KEYWORD_SEPARATOR);
        Date halfYear = DateUtil.dateCompute(new Date(), Calendar.MONTH, -6);
        List<Message> result = new ArrayList<>();
        boolean stop = false;
        do {
            class MyCall implements Callable<Message[]> {
                private int start;
                private int end;

                public MyCall(int start, int end) {
                    this.start = start;
                    this.end = end;
                }

                @Override
                public Message[] call() throws Exception {
                    List<Message> r = new ArrayList<>();
                    if (start < 1) {
                        start = 1;
                    }
                    logger.info("start ==== " + start + "; end ==== " + end);
                    Message[] messages = folder.getMessages(start, end);

                    for (Message m : messages) {
                        MimeMessage msg = (MimeMessage) m;
                        String subject = MailDecodeUtil.getSubject(msg);
                        Date sentDate = msg.getSentDate();

                        for (String keyword : keywords) {
                            if (subject.contains(keyword) && sentDate.after(halfYear)) {
                                logger.info("subject = " + subject + ";sentDate=" + sentDate);
                                r.add(m);
                                break;
                            }
                        }
                    }
                    if (r.isEmpty()) {
                        return new Message[0];
                    } else {
                        return r.toArray(new Message[r.size()]);
                    }

                }
            }
            Message[] marray = folder.getMessages(start, start);
            Date sentDate = marray[0].getSentDate();
            if (!sentDate.after(halfYear)) {
                stop = true;
            }
            FutureTask<Message[]> task = new FutureTask<>(new MyCall(start, end));
            taskList.add(task);
            es.submit(task);
            start = start - 100;
            end = end - 100;
        } while (!stop && start > 1);
        for (FutureTask<Message[]> task : taskList) {
            try {
                Message[] m = task.get(3, TimeUnit.MINUTES);
                result.addAll(Arrays.asList(m));
            } catch (InterruptedException | ExecutionException | TimeoutException e) {

            }
        }
        if (result.isEmpty()) {
            return new Message[0];
        } else {
            return result.toArray(new Message[result.size()]);
        }

    }

    private static SearchTerm getSearchTerm(String queryKey) {
        if (-1 != queryKey.indexOf(BankKeywordContants.BANK_KEYWORD_SEPARATOR)) {
            String[] queryKeyAry = queryKey.split(BankKeywordContants.BANK_KEYWORD_SEPARATOR);
            SubjectTerm[] subjectTermAry = new SubjectTerm[queryKeyAry.length];
            for (int i = 0; i < queryKeyAry.length; i++) {
                subjectTermAry[i] = new SubjectTerm(queryKeyAry[i]);
            }
            OrTerm orTerm = new OrTerm(subjectTermAry);
            return orTerm;
        } else {
            SearchTerm subjectTerm = new SubjectTerm(queryKey);
            return subjectTerm;
        }

    }

    /**
     *
     * @Description 根据银行关键字和邮件发送日期进行搜索
     * @param queryKey
     * @return
     * @see 需要参考的类或方法
     * @author chao.wang
     */
    private static SearchTerm getSearchTermAfterDate(String queryKey) {
        SubjectTerm[] subjectTermAry = null;
        OrTerm orTerm = null;
        if (-1 != queryKey.indexOf(BankKeywordContants.BANK_KEYWORD_SEPARATOR)) {
            String[] queryKeyAry = queryKey.split(BankKeywordContants.BANK_KEYWORD_SEPARATOR);
            subjectTermAry = new SubjectTerm[queryKeyAry.length];
            for (int i = 0; i < queryKeyAry.length; i++) {
                subjectTermAry[i] = new SubjectTerm(queryKeyAry[i]);
            }
        } else {
            subjectTermAry = new SubjectTerm[1];
            subjectTermAry[0] = new SubjectTerm(queryKey);
        }
        orTerm = new OrTerm(subjectTermAry);
        Date date = DateUtil.dateCompute(new Date(), Calendar.MONTH, getMonthOffset());
        SearchTerm sentDateTerm = new SentDateTerm(ComparisonTerm.GE, date);
        AndTerm andTerm = new AndTerm(orTerm, sentDateTerm);
        return andTerm;
    }

    private static Message[] mulitSearch(String queryKey, Message[] messageAry, ThreadPoolTaskExecutor taskExecutor) {
        SearchTerm searchTerm = getSearchTerm(queryKey);
        List<Message> retMessageList = new ArrayList<>();
        CountDownLatch doneSignal = new CountDownLatch(messageAry.length);
        for (Message message : messageAry) {
            taskExecutor.execute(() -> {
                try {
                    if (message.match(searchTerm)) {
                        retMessageList.add(message);
                    }
                } catch (Exception e) {
                    logger.error("mulit search mail with keywords error!", e);
                }
                doneSignal.countDown();
            });
        }
        try {
            doneSignal.await();
        } catch (InterruptedException e) {
            logger.error("mulit search error");
            logger.error(e.getMessage(), e);
        }
        Message[] retMessageAry = new Message[retMessageList.size()];
        retMessageList.toArray(retMessageAry);
        return retMessageAry;
    }
}
