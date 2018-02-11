package com.pay.aile.bill.web;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mongodb.CommandResult;
import com.pay.aile.bill.entity.CreditEmail;
import com.pay.aile.bill.entity.EmailFile;
import com.pay.aile.bill.service.mail.download.DownloadMail;
import com.pay.aile.bill.utils.MongoDownloadUtil;

@Controller
public class TestContoller {
    public static final String MAIL_DOWANLOD_LIST_NAME = "aile-mail-job-list";
    public static final String MAIL_DOWANLOD_JOB_CONTENT = "aile-mail-job-content-";
    private String SECRET_KEY = "E38EADEGHJ22MNBFD0E34B7XCZP29WQO5BE4AA13DEF03KIURE";

    @Autowired
    private MongoDownloadUtil mongoDownloadUtil;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private DownloadMail downloadMail;

    @RequestMapping("/test/download")
    @ResponseBody
    public String analyze() {
        try {
            CreditEmail creditEmail = new CreditEmail();
            creditEmail.setEmail("18518679659@139.com");
            creditEmail.setPassword("12345qwert");
            downloadMail.execute(creditEmail);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @RequestMapping("/test/delCollectionDate")
    @ResponseBody
    public String delCollectionDate(String name) {
        // List<EmailFile> set = mongoTemplate.findAll(EmailFile.class, name);
        // for (EmailFile file : set) {
        // set.remove(file);
        // }
        return "method doesn't do anything.";
        
    }

    @RequestMapping("/test/delCollectionNames")
    @ResponseBody
    public CommandResult delCollectionNames(String name) {
        CommandResult r = mongoTemplate.getDb().command("db." + name + ".drop()");

        return r;
    }

    @RequestMapping("/test/delKeys")
    @ResponseBody
    public Set<String> delKeys(String key) {
        Set<String> keySet = redisTemplate.keys(key);

        redisTemplate.delete(keySet);
        return keySet;
    }

    @RequestMapping("/test/getCollection")
    @ResponseBody
    public List<EmailFile> getCollection(String collectionName) {
        List<EmailFile> set = mongoTemplate.findAll(EmailFile.class, collectionName);

        return set;
    }

    @RequestMapping("/test/getCollectionNames")
    @ResponseBody
    public Set<String> getCollectionNames() {
        Set<String> set = mongoTemplate.getCollectionNames();

        return set;
    }

    @RequestMapping("/test/getFile")
    @ResponseBody
    public EmailFile getFile(String fileName, String email, int month) {
        EmailFile file = null;
        if (StringUtils.hasText(email)) {
            file = mongoDownloadUtil.getFile(fileName, email, month);
        } else {
            file = mongoDownloadUtil.getFile(fileName, month);

        }

        return file;
    }

    @RequestMapping("/test/getfile")
    @ResponseBody
    public String getFile(String secretKey, String fileName, String email, int month) {
        if (StringUtils.isEmpty(secretKey) || StringUtils.isEmpty(fileName) || StringUtils.isEmpty(email)
                || StringUtils.isEmpty(month) || !SECRET_KEY.equals(secretKey)) {
            return "server running.";
        }
        try {
            EmailFile emailFile = mongoDownloadUtil.getFile(fileName, email, month);
            if (emailFile != null) {
                if ("HTML".equals(emailFile.getMailType())) {
                    return emailFile.getContent();
                }
                return emailFile.getAttachment();
            }
            return "No file found.";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @RequestMapping("/test/contentList")
    @ResponseBody
    public String getMailJobContextList(String id) {
        String jsonString = redisTemplate.opsForValue().get(MAIL_DOWANLOD_JOB_CONTENT.concat(id));

        return jsonString;
    }

    @RequestMapping("/test/idList")
    @ResponseBody
    public List<String> getMailJobIdList() {
        List<String> list = redisTemplate.opsForList().range(MAIL_DOWANLOD_LIST_NAME, 0, -1);

        return list;
    }
    

    @RequestMapping("/test/save")
    @ResponseBody
    public String testsave(HttpServletRequest request) {
    	Map<String,String> paramMap = new HashMap<String,String>();
    	Enumeration<String> names=request.getParameterNames();
    	while(names.hasMoreElements()) {
    		String key = names.nextElement();
    		if(!"email".equals(key) && !"password".equals(key) && !"userId".equals(key)) {
        		paramMap.put(key, request.getParameter(key));

    		}
    	}
    	return "{}";
    }
    
}
