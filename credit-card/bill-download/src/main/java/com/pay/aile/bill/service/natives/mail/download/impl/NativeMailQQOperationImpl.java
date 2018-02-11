package com.pay.aile.bill.service.natives.mail.download.impl;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pay.aile.bill.entity.CreditEmail;
import com.pay.aile.bill.entity.CreditFile;
import com.pay.aile.bill.entity.CreditNativeEmail;
import com.pay.aile.bill.entity.EmailFile;
import com.pay.aile.bill.enums.DwonloadMailType;
import com.pay.aile.bill.exception.MailBillException;
import com.pay.aile.bill.utils.ApacheMailUtil;
import com.pay.aile.bill.utils.DateUtil;
import com.pay.aile.bill.utils.MailDecodeUtil;
import com.pay.aile.bill.utils.MongoDownloadUtil;

@Component
public class NativeMailQQOperationImpl extends BaseNativeMailOperationImpl {
	private static final Logger logger = LoggerFactory.getLogger(NativeMailQQOperationImpl.class);
	
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private MongoDownloadUtil downloadUtil;

	public void downloadEmail(CreditNativeEmail email) throws IOException {
		
		JSONArray emailArray = httpGet(email.getUrl(), email.getCookieMap());
		emailArray.forEach(eamil ->{
			try {
				saveFile(((JSONObject)eamil),email);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		});

	}

	/**
	 * 设置头信息
	 * 
	 * @param cookieMap
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public HttpEntity setHeader(Map<String, String> cookieMap) {
		HttpHeaders headers = new HttpHeaders();
		List<String> cookieList = new ArrayList<String>();
		cookieMap.forEach((K, V) -> {
			cookieList.add(K + "=" + V);

		});
		List<MediaType> mediaTypeList = new ArrayList<MediaType>();
		mediaTypeList.add(MediaType.APPLICATION_JSON);
		headers.setAccept(mediaTypeList);
		headers.put(HttpHeaders.COOKIE, cookieList); // 将cookie放入header
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set(HttpHeaders.USER_AGENT, userAgent);
		HttpEntity request = new HttpEntity(null, headers);

		return request;

	}

	@SuppressWarnings("rawtypes")
	public JSONArray httpGet(String url, Map<String, String> cookieMap) throws IOException {
		// 设置头和cookie信息
		HttpEntity request = setHeader(cookieMap);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

		JSONObject json = JSONObject.parseObject(response.getBody());
		JSONArray mls = json.getJSONArray("mls");
		
		return  mls;
	}

	// public String httpPost(String url,Map<String,String> map,Map<String,String>
	// cookieMap) throws IOException{
	// //获取请求连接
	// Connection con = Jsoup.connect(url);
	// //遍历生成参数
	// if(map!=null){
	// for (Entry<String, String> entry : map.entrySet()) {
	// //添加参数
	// con.data(entry.getKey(), entry.getValue());
	// }
	// }
	// cookieMap.forEach((K,V) ->{
	// con.cookie(K, V);
	// });
	//
	// con.userAgent(userAgent);
	//
	//
	// Document doc = con.post();
	// String html = StringEscapeUtils.unescapeHtml3(doc.toString());
	//
	// return html;
	// }
	//
	// public String httpGet(String url,Map<String,String> map,Map<String,String>
	// cookieMap) throws IOException{
	// //获取请求连接
	// Connection con = Jsoup.connect(url);
	//// //遍历生成参数
	//// if(map!=null){
	//// for (Entry<String, String> entry : map.entrySet()) {
	//// //添加参数
	//// con.data(entry.getKey(), entry.getValue());
	//// }
	//// }
	// cookieMap.forEach((K,V) ->{
	// con.cookie(K, V);
	// });
	//
	// con.userAgent(userAgent);
	//
	//
	// Document doc = con.get();
	// String html = StringEscapeUtils.unescapeHtml3(doc.toString());
	//
	// return html;
	// }
	//
	// public Document httpGet(String url,Map<String,String> cookieMap) throws
	// IOException{
	// //获取请求连接
	// Connection con = Jsoup.connect(url);
	//
	// cookieMap.forEach((K,V) ->{
	// con.cookie(K, V);
	// });
	//
	// con.userAgent(userAgent);
	//
	//
	// Document doc = con.get();
	//
	//
	// return doc;
	// }
	public EmailFile getEmailFile(JSONObject emailObject, CreditNativeEmail creditEmail)
			throws MailBillException, MessagingException {
		EmailFile emailFile = new EmailFile();

		String subject = emailObject.getJSONObject("inf").getString("subj");
		String senderAdd = emailObject.getJSONObject("inf").getJSONObject("from").getString("addr");
		String receiveAdd = emailObject.getJSONObject("inf").getJSONObject("from").getString("addr");
		long time = emailObject.getJSONObject("inf").getLong("date");

		String content = emailObject.getJSONObject("content").getString("body");
		content = String.format(htmlString, content);
		// 文件名
		emailFile.setFileName(UUID.randomUUID().toString());

		String sentDate = DateUtil.getDateStr(time * 1000);
		
		logger.info("subject:{} receiveAdd:{} senderAdd:{} sentData:{}", subject, receiveAdd, senderAdd, sentDate);
		emailFile.setSubject(subject);
		emailFile.setSentDate(sentDate);
		emailFile.setEmail(creditEmail.getEmail());
		emailFile.setMailType(DwonloadMailType.HTML.toString());
		
		
		emailFile.setContent(content);
		emailFile.setEmail(creditEmail.getEmail());
		return emailFile;
	}

	
}
