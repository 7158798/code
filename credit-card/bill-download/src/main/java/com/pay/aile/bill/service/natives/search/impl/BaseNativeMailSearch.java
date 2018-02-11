package com.pay.aile.bill.service.natives.search.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import com.pay.aile.bill.entity.CreditNativeEmail;
import com.pay.aile.bill.job.NativeEmaileDownloadScheduler;
import com.pay.aile.bill.service.CreditBankService;
import com.pay.aile.bill.service.natives.mail.search.NativeMailSearch;

public class BaseNativeMailSearch implements NativeMailSearch {
	//userAgent 
	protected String userAgent="Mozilla/5.0 (Linux; Android 7.0; PLUS Build/NRD90M) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.98 Mobile Safari/537.36";
	
    
    @Autowired
    protected NativeEmaileDownloadScheduler nativeEmaileDownloadScheduler;
	
	@Autowired
	protected CreditBankService creditBankService;
	
	@Autowired
	protected RestTemplate restTemplate;
	
	protected List<String> keywords = null;
	
	@PostConstruct
	public void getKeywords() {
		keywords = new ArrayList<String>();
		keywords.add("交通");
		//creditBankService.getKeywords();
	}
	
	@Override
	public void searchEmail(CreditNativeEmail email) throws IOException {
		// TODO Auto-generated method stub

	}

}
