package com.pay.aile.bill.service.natives.mail.download.impl;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.pay.aile.bill.entity.CreditNativeEmail;
import com.pay.aile.bill.entity.EmailFile;
import com.pay.aile.bill.enums.DwonloadMailType;
import com.pay.aile.bill.exception.MailBillException;
import com.pay.aile.bill.utils.DateUtil;
import com.pay.aile.bill.utils.MongoDownloadUtil;

@Component
public class NativeMail126OperationImpl extends BaseNativeMailOperationImpl {
	private static final Logger logger = LoggerFactory.getLogger(NativeMail126OperationImpl.class);

	private String postDate = "<?xml version=\"1.0\"?><object><string name=\"id\">%s</string><string name=\"mode\">both</string><boolean name=\"autoName\">true</boolean><boolean name=\"supportTNEF\">true</boolean><boolean name=\"filterStylesheets\">true</boolean><boolean name=\"returnImageInfo\">true</boolean></object>";
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private MongoDownloadUtil downloadUtil;

	public void downloadEmail(CreditNativeEmail email) throws IOException {
		String data = String.format(postDate, email.getEmailFileId());
		JSONObject emailJson = httpPost(email.getUrl(), email.getCookieMap(), data);

		try {
			saveFile(emailJson, email);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	public EmailFile getEmailFile(JSONObject emailObject, CreditNativeEmail creditEmail)
			throws MailBillException, MessagingException {
		EmailFile emailFile = new EmailFile();

		String subject = emailObject.getString("subject");
		String senderAdd = "";
		String receiveAdd = "";// emailObject.getJSONObject("inf").getJSONObject("from").getString("addr");

		String content = emailObject.getJSONObject("html").getString("content");
		content = String.format(htmlString, content);
		// 文件名
		emailFile.setFileName(UUID.randomUUID().toString());

		String sentDate =  getSentDate(emailObject.getString("sentDate"));

		logger.info("subject:{} receiveAdd:{} senderAdd:{} sentData:{}", subject, receiveAdd, senderAdd, sentDate);
		emailFile.setSubject(subject);
		emailFile.setSentDate(sentDate);
		emailFile.setEmail(creditEmail.getEmail());
		emailFile.setMailType(DwonloadMailType.HTML.toString());

		emailFile.setContent(content);
		emailFile.setEmail(creditEmail.getEmail());
		return emailFile;
	}
	
	public String  getSentDate(String sendDate) {
		sendDate = sendDate.substring(sendDate.indexOf("(")+1, sendDate.indexOf(")"));
		String[] sendDateArray =sendDate.split(",");
		Date date =new Date(Integer.parseInt(sendDateArray[0]), Integer.parseInt(sendDateArray[1]), Integer.parseInt(sendDateArray[2]), Integer.parseInt(sendDateArray[3]), Integer.parseInt(sendDateArray[4]), Integer.parseInt(sendDateArray[3]));
		
		return DateUtil.formatDate(date,"yyyyMMddHH:mm:ss");
		
	}
	

	/**
	 * 设置头信息
	 * 
	 * @param cookieMap
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected HttpHeaders getHeader(Map<String, String> cookieMap) {
		HttpHeaders headers = new HttpHeaders();
		List<String> cookieList = new ArrayList<String>();
		cookieMap.forEach((K, V) -> {
			cookieList.add(K + "=" + V);

		});
		List<String> mediaTypeList = new ArrayList<String>();
		mediaTypeList.add("text/javascript");
		headers.put(HttpHeaders.ACCEPT, mediaTypeList);
		headers.put(HttpHeaders.COOKIE, cookieList); // 将cookie放入header
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.set(HttpHeaders.USER_AGENT, userAgent);

		return headers;
	}

	@SuppressWarnings("rawtypes")
	protected JSONObject httpPost(String url, Map<String, String> cookieMap, String post) throws IOException {
		// 设置头和cookie信息

		MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
		postParameters.add("var", post);

		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(postParameters, getHeader(cookieMap));

		ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
		String data = response.getBody();
		logger.info(data);
		data.replaceAll("\'", "\"").replaceAll("\"\"", "\"");
		data = replaceAllDate(data);
		JSONObject json = JSONObject.parseObject(data);
		JSONObject var = json.getJSONObject("var");

		return var;
	}

	//
	private String replaceAllDate(String jsonStr) {
		String reg = "new Date\\(\\d+,\\d+,\\d+,\\d+,\\d+,\\d+\\)";

		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(jsonStr);
		while (matcher.find()) {
			String matchWord = matcher.group(0);
			String property = matchWord;
			System.out.println(property);
			jsonStr = jsonStr.replace(matchWord, "\"" + property + "\"");
		}

		return jsonStr;
	}

}
