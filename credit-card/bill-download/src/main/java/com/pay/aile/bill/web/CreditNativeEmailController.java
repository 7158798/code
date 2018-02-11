package com.pay.aile.bill.web;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.pay.aile.bill.contant.Constant;
import com.pay.aile.bill.entity.CreditNativeEmail;
import com.pay.aile.bill.enums.ResultEmun;
import com.pay.aile.bill.job.NativeEmaileDownloadScheduler;
import com.pay.aile.bill.job.NativeEmaileSearchScheduler;
import com.pay.aile.bill.service.CreditNativeEmailService;
import com.pay.aile.bill.utils.JedisClusterUtils;
@Controller
@RequestMapping(value = "/native")
public class CreditNativeEmailController {
    private static Logger logger = LoggerFactory.getLogger(CreditNativeEmailController.class);
    @Autowired
    private CreditNativeEmailService creditNativeEmailService;
    
    @Autowired
    private NativeEmaileSearchScheduler nativeEmaileSearchScheduler;
    
    @Autowired
    private NativeEmaileDownloadScheduler nativeEmaileDownloadScheduler;
    @RequestMapping(value = "/saveEmail")
    @ResponseBody
    public JSONObject saveEmail(CreditNativeEmail email ,HttpServletRequest request) {
        logger.info("需要导入的邮件：{}", "");
     
        try {
        	String value = JedisClusterUtils.getString(String.format(Constant.REDIS_EMAIL_COOKIE, email.getUserId(),email.getEmail()));
        	if(StringUtils.hasText(value)) {
        		String password = email.getPassword();
        		
        		email.setPassword(null);
        		email = creditNativeEmailService.findCreditNative(email);
        		email.setPassword(password);
        	}
    		Map<String,String> paramMap = getRequestMap(request);
     		creditNativeEmailService.saveOrUpdate(email, paramMap);
     	
       		//放入緩存
    		JedisClusterUtils.saveString(String.format(Constant.REDIS_EMAIL_COOKIE, email.getUserId(),email.getEmail()), JSONObject.toJSONString(paramMap));
    		
        	email.setCookieMap(paramMap);
        	//查询
        	searchEmail(email);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResultEmun.EMAIL_LOGIN_FAIL.getJsonMsg();
        } finally {
          
        }

        return ResultEmun.SUCCESS.getJsonMsg();
    }
    
    public void searchEmail(CreditNativeEmail email) {
       
        try {
        	
        	nativeEmaileSearchScheduler.searchMail(email);
        } catch (Exception e) {
            logger.error(e.getMessage());
          
        } finally {
          
        }

    }
    
    @RequestMapping(value = "/getEmailBill")
    @ResponseBody
    public JSONObject getEmailBill(CreditNativeEmail email ,HttpServletRequest request) {
        logger.info("需要导入的邮件：{}", "");     
        try {
        	String value = JedisClusterUtils.getString(String.format(Constant.REDIS_EMAIL_COOKIE, email.getUserId(),email.getEmail()));
        	Map<String,String> cookieMap = JSONObject.parseObject(value, Map.class);
        	email.setCookieMap(cookieMap);
        	Map<String,String> postDataMap = getRequestMap(request);
        	email.setPostDataMap(postDataMap);
        	nativeEmaileDownloadScheduler.downLoadMailLoop(email);
        } catch (Exception e) {
            logger.error("邮箱:{}登录失败");
            return ResultEmun.EMAIL_LOGIN_FAIL.getJsonMsg();
        } finally {
          
        }

        return ResultEmun.SUCCESS.getJsonMsg();
    }
    private Map<String,String> getRequestMap(HttpServletRequest request){
    	
    	Map<String,String> paramMap = new HashMap<String,String>();
    	Enumeration<String> names=request.getParameterNames();
    	while(names.hasMoreElements()) {
    		String key = names.nextElement();
    		if(!"email".equals(key) && !"password".equals(key) && !"userId".equals(key)) {
        		paramMap.put(key, request.getParameter(key));

    		}
    	}
    	
    	return paramMap;
    } 
    
    
}
