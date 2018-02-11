package com.pay.aile.bill.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.pay.aile.bill.entity.EmailFile;
import com.pay.aile.bill.exception.AnalyzeBillException;
import com.pay.aile.bill.task.FileQueueRedisHandle;

/***
 * DownloadUtil.java
 *
 * @author shinelon
 *
 * @date 2017年10月30日
 *
 */
@Component
public class MongoDownloadUtil {
    private static final Logger logger = LoggerFactory.getLogger(MongoDownloadUtil.class);
    private static final String DOC_KEY_FILE_NAME = "fileName";
    private static final String DOC_KEY_FILE_EMAIL = "email";
    private static final String emailFile = "emailFile_";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private FileQueueRedisHandle fileQueueRedisHandle;

    public String getFile(String fileName) throws AnalyzeBillException {

        try {

            Criteria criteria = new Criteria(DOC_KEY_FILE_NAME);
            criteria.is(fileName);
            Query query = new Query(criteria);
            EmailFile ef = mongoTemplate.findOne(query, EmailFile.class);
            return ef.getContent();
        } catch (Exception e) {

            logger.error(e.getMessage());
            throw new AnalyzeBillException(e.getMessage());
        }

    }

    public EmailFile getFile(String fileName, String email) throws AnalyzeBillException {

        try {

            EmailFile ef = mongoTemplate.findOne(
                    new Query(Criteria.where(DOC_KEY_FILE_NAME).is(fileName).and(DOC_KEY_FILE_EMAIL).is(email)),
                    EmailFile.class);
            return ef;
        } catch (Exception e) {

            logger.error(e.getMessage());
            throw new AnalyzeBillException(e.getMessage());
        }

    }

    public EmailFile getFile(String fileName, String email, int month) throws AnalyzeBillException {
        try {

            EmailFile ef = mongoTemplate.findOne(
                    new Query(Criteria.where(DOC_KEY_FILE_NAME).is(fileName).and(DOC_KEY_FILE_EMAIL).is(email)),
                    EmailFile.class, emailFile + month);
            return ef;
        } catch (Exception e) {

            logger.error(e.getMessage());
            throw new AnalyzeBillException(e.getMessage());
        }
    }
    public EmailFile getFile(String fileName, int month) throws AnalyzeBillException {
        try {

            EmailFile ef = mongoTemplate.findOne(
                    new Query(Criteria.where(DOC_KEY_FILE_NAME).is(fileName)),
                    EmailFile.class, emailFile + month);
            return ef;
        } catch (Exception e) {

            logger.error(e.getMessage());
            throw new AnalyzeBillException(e.getMessage());
        }
    }
}
