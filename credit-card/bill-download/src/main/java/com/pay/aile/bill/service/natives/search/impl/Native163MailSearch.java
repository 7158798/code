package com.pay.aile.bill.service.natives.search.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.pay.aile.bill.entity.CreditNativeEmail;

@Component
public class Native163MailSearch extends BaseNativeMailSearch {


	private static Logger logger = LoggerFactory.getLogger(NativeQQMailSearch.class);

	// private String loginUrl =
	// "https://w.mail.qq.com/cgi-bin/mobile?sid=%s&t=phone#list,,search_%s_all__";
	private String searchUrl = "http://mail.163.com/m/s?sid=%s&func=mbox:searchMessages";
	// private String searchUrl =
	// "http://localhost:8888/bill/test/save?sid=%s&func=mbox:searchMessages";
	private String postData = "<?xml version=\"1.0\"?><object><string name=\"order\">date</string><boolean name=\"desc\">true</boolean><string name=\"operator\">or</string><array name=\"condictions\"><object><string name=\"field\">from</string><string name=\"operator\">contains</string><string name=\"operand\">%s</string><boolean name=\"ignoreCase\">true</boolean></object><object><string name=\"field\">to</string><string name=\"operator\">contains</string><string name=\"operand\">%s</string><boolean name=\"ignoreCase\">true</boolean></object><object><string name=\"field\">subject</string><string name=\"operator\">contains</string><string name=\"operand\">%s</string><boolean name=\"ignoreCase\">true</boolean></object></array><int name=\"windowSize\">20</int></object>";
	private String readUrl = "http://mail.163.com/m/s?sid=%s&func=mbox:readMessage&l=read&action=read";

	@Override
	public void searchEmail(CreditNativeEmail email) throws IOException {

		Map<String, String> cookieMap = email.getCookieMap();
		// 获取到sid
		String sid = cookieMap.get("sid");

		super.keywords.forEach(key -> {
			logger.info("keyword ::::: {}", key);
			String keyword = key;// URLEncoder.encode(key);
			String data = String.format(postData, keyword, keyword, keyword);
			String url = String.format(searchUrl, sid);
			logger.info("search_url ::::: {}", url);
			try {
				// 查询到的邮箱的连接地址
				List<String> emailArray = httpPost(url, email.getCookieMap(), data);
				emailArray.forEach(fileId -> {

					String rurl = String.format(readUrl, sid);
					logger.info("search_rurl ::::: {}", rurl);
					email.setUrl(rurl);
					email.setEmailFileId(fileId);
					super.nativeEmaileDownloadScheduler.downLoadMailLoop(email);

				});
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
	public HttpHeaders getHeader(Map<String, String> cookieMap) {
		HttpHeaders headers = new HttpHeaders();
		List<String> cookieList = new ArrayList<String>();
		cookieMap.forEach((K, V) -> {
			cookieList.add(K + "=" + V);

		});

		// List<MediaType> mediaTypeList = new ArrayList<MediaType>();
		// mediaTypeList.add("text/javascript");
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		List<String> mediaTypeList = new ArrayList<String>();
		mediaTypeList.add("text/javascript");
		headers.put(HttpHeaders.ACCEPT, mediaTypeList);

		headers.put(HttpHeaders.COOKIE, cookieList); // 将cookie放入header
		// headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set(HttpHeaders.USER_AGENT, userAgent);

		return headers;
	}

	@SuppressWarnings("rawtypes")
	public List<String> httpPost(String url, Map<String, String> cookieMap, String post) throws IOException {
		// 设置头和cookie信息

		MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
		postParameters.add("var", post);

		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(postParameters, getHeader(cookieMap));

		ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

		//String jsonStr = response.getBody().replaceAll("\"", "").replaceAll("'", "\"");
		

		return findFileId(response.getBody());
	}
	
	
	//
	private List<String> findFileId(String emailStr) {
		String reg = "'id':'\\S+'";
		List<String> result = new ArrayList<String>();
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(emailStr);
		while (matcher.find()) {
			String matchWord = matcher.group(0);
			matchWord = matchWord.replaceAll("'","");
			String[] matchWordArray = matchWord.split(":");
			result.add(matchWordArray[1]+":"+matchWordArray[2]);
		}

		return result;
	}
	
//
//	private String replaceAllDate(String jsonStr) {
//		String reg = "new Date\\(\\d+,\\d+,\\d+,\\d+,\\d+,\\d+\\)";
//
//		Pattern pattern = Pattern.compile(reg);
//		Matcher matcher = pattern.matcher(jsonStr);
//		while (matcher.find()) {
//			String matchWord = matcher.group(0);
//			String property = matchWord;
//			System.out.println(property);
//			jsonStr = jsonStr.replace(matchWord, "\"" + property + "\"");
//		}
//
//		return jsonStr.replace("\"\"", "\"");
//	}
}
