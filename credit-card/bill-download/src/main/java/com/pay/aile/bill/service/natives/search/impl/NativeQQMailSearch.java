package com.pay.aile.bill.service.natives.search.impl;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.URLEditor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pay.aile.bill.entity.CreditNativeEmail;

@Component
public class NativeQQMailSearch extends BaseNativeMailSearch{
	
	private static Logger logger = LoggerFactory.getLogger(NativeQQMailSearch.class);
	
	//private String loginUrl = "https://w.mail.qq.com/cgi-bin/mobile?sid=%s&t=phone#list,,search_%s_all__";
	private String searchUrl = "https://w.mail.qq.com/cgi-bin/mail_list?ef=js&r=%f&sid=%s&t=mobile_data.json&s=list&cursor=max&cursorcount=20&cursorsearch=1&folderid=all&receiver=%s&sender=%s&subject=%s&keyword=%s&combinetype=or&device=android&app=phone&ver=app";
	
	private String readUrl = "https://w.mail.qq.com/cgi-bin/readmail?ef=js&r=%f&sid=%s&t=mobile_data.json&s=read&showreplyhead=1&disptype=html&mailid=%s";
	@Override
	public void searchEmail(CreditNativeEmail email) throws IOException {
		
		Map<String,String> cookieMap = email.getCookieMap();
		//获取到sid
		String sid = cookieMap.get("msid");
		String qmsk = cookieMap.get("qm_sk");
		String[] qmskArray =qmsk.split("&");
		super.keywords.forEach(key->{
			logger.info("keyword ::::: {}",key);
			String keyword = key;//URLEncoder.encode(key);
			
			String url = String.format(searchUrl, Math.random(),sid,keyword,keyword,keyword,keyword);
			logger.info("search_url ::::: {}",url);
			try {
				//查询到的邮箱的连接地址				
				JSONArray emailArray = httpGet(url, email.getCookieMap());
				emailArray.forEach(eamil ->{
					String newSid =  "";
					if(sid.contains("--")) {
						newSid = sid.replaceAll("\\S{1}--\\S{5}", qmskArray[1]);
					}else {
						newSid = sid;
					}
					 
					String id = ((JSONObject)eamil).getJSONObject("inf").getString("id");
					String rurl = String.format(readUrl, Math.random(),newSid,id);
					logger.info("search_rurl ::::: {}",rurl);
					email.setUrl(rurl);
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
		
		return mls;
	}

	
}
