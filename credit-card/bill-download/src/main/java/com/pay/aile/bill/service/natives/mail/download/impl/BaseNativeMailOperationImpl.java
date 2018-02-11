package com.pay.aile.bill.service.natives.mail.download.impl;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import javax.mail.MessagingException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pay.aile.bill.entity.CreditFile;
import com.pay.aile.bill.entity.CreditNativeEmail;
import com.pay.aile.bill.entity.EmailFile;
import com.pay.aile.bill.enums.DwonloadMailType;
import com.pay.aile.bill.exception.MailBillException;
import com.pay.aile.bill.mapper.CreditFileMapper;
import com.pay.aile.bill.mapper.CreditUserEmailRelationMapper;

import com.pay.aile.bill.service.natives.mail.download.NativeMailOperation;
import com.pay.aile.bill.utils.DateUtil;
import com.pay.aile.bill.utils.MongoDownloadUtil;

public class BaseNativeMailOperationImpl implements NativeMailOperation {
	private static final Logger logger = LoggerFactory.getLogger(BaseNativeMailOperationImpl.class);
	
	protected  String htmlString ="<!doctype html>\r\n" + 
			"<html lang=\"en\">\r\n" + 
			" <head>\r\n" + 
			"  <meta charset=\"UTF-8\">\r\n" + 
			"  <meta name=\"Generator\" content=\"EditPlus®\">\r\n" + 
			"  <meta name=\"Author\" content=\"\">\r\n" + 
			"  <meta name=\"Keywords\" content=\"\">\r\n" + 
			"  <meta name=\"Description\" content=\"\">\r\n" + 
			"  <title>Document</title>\r\n" + 
			" </head>\r\n" + 
			" <body>\r\n" + 
			" %s" + 
			" </body>\r\n" + 
			"</html>";
    @Autowired
    private CreditUserEmailRelationMapper emailRelationMapper;
    
    @Autowired
    private CreditFileMapper creditFileMapper;
    
    @Autowired
    private MongoDownloadUtil downloadUtil;
	
	protected String userAgent="Mozilla/5.0 (Linux; Android 7.0; PLUS Build/NRD90M) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.98 Mobile Safari/537.36";
	public void downloadEmail(CreditNativeEmail email) throws IOException{
		//String html = httpPost(email.getUrl(),email.getPostDataMap(),email.getCookieMap());
		//String html = httpGet(email.getUrl(),email.getPostDataMap(),email.getCookieMap());
		//parseEmailHtml(html);
	}	
//	public String httpPost(String url,Map<String,String> map,Map<String,String> cookieMap) throws IOException{
//        //获取请求连接
//        Connection con = Jsoup.connect(url);
//        //遍历生成参数
//        if(map!=null){
//            for (Entry<String, String> entry : map.entrySet()) {     
//              //添加参数
//              con.data(entry.getKey(), entry.getValue());
//           } 
//        }
//        cookieMap.forEach((K,V) ->{
//        	con.cookie(K, V);
//        });
//		
//        con.userAgent(userAgent);
//   
//      
//        Document doc = con.post();  
//        String html = StringEscapeUtils.unescapeHtml3(doc.toString());
//       
//        return html;
//    }
//	
//	public String httpGet(String url,Map<String,String> map,Map<String,String> cookieMap) throws IOException{
//        //获取请求连接
//        Connection con = Jsoup.connect(url);
////        //遍历生成参数
////        if(map!=null){
////            for (Entry<String, String> entry : map.entrySet()) {     
////              //添加参数
////              con.data(entry.getKey(), entry.getValue());
////           } 
////        }
//        cookieMap.forEach((K,V) ->{
//        	con.cookie(K, V);
//        });
//		
//        con.userAgent(userAgent);
//   
//      
//        Document doc = con.get();  
//        String html = StringEscapeUtils.unescapeHtml3(doc.toString());
//       
//        return html;
//    }
//	
//	public Document httpGet(String url,Map<String,String> cookieMap) throws IOException{
//        //获取请求连接
//        Connection con = Jsoup.connect(url);
//
//        cookieMap.forEach((K,V) ->{
//        	con.cookie(K, V);
//        });
//		
//        con.userAgent(userAgent);
//   
//      
//        Document doc = con.get();  
//     
//       
//        return doc;
//    }
//	
//	protected Map<String,Object> parseEmailHtml(String html){
//		System.out.println(html);
//		return null;
//	}
//	
	protected EmailFile getEmailFile(JSONObject emailObject, CreditNativeEmail creditEmail)
			throws MailBillException, MessagingException {
		return null;
	}
	
	protected  CreditFile getCreditFile(EmailFile emailFile, CreditNativeEmail creditEmail) {
		CreditFile creditFile = new CreditFile();
		creditFile.setEmail(creditEmail.getEmail());
		creditFile.setEmailId(creditEmail.getId());
		creditFile.setFileName(emailFile.getFileName());
		creditFile.setSubject(emailFile.getSubject());
		creditFile.setMailType(emailFile.getMailType());
		creditFile.setIsNew(true);
		creditFile.setUserId(creditEmail.getUserId());
		try {
			creditFile.setSentDate(DateUtils.parseDate(emailFile.getSentDate(), "yyyyMMddHH:mm:ss"));
		} catch (ParseException e) {
			logger.error(e.getMessage(), e);
		}
		creditFile.setProcessResult(0);
		return creditFile;
	}

	
	
	protected void saveFile(JSONObject jsonObject, CreditNativeEmail email) throws IOException {

		try {
			EmailFile emailFile = getEmailFile(jsonObject, email);
			CreditFile creditFile = getCreditFile(emailFile, email);
		   	List<EmailFile> emailFileList = new ArrayList<EmailFile>();
	    	emailFileList.add(emailFile);
	    	List<CreditFile> creditFileList = new ArrayList<CreditFile>();
	    	creditFileList.add(creditFile);
	    	//保存附件
			downloadUtil.saveFile(emailFileList, creditFileList, email);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}
}
