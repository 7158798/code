package com.pay.aile.bill.job;

import java.io.IOException;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.pay.aile.bill.entity.CreditNativeEmail;
import com.pay.aile.bill.enums.NativeMailType;
import com.pay.aile.bill.service.natives.mail.search.NativeMailSearch;
import com.pay.aile.bill.utils.SpringContextUtil;

@Component
public class NativeEmaileSearchScheduler {

    private static final Logger logger = LoggerFactory.getLogger(NativeEmaileSearchScheduler.class);
    
    @Resource(name = "nativeEmaileSearchExecutor")
    private ThreadPoolTaskExecutor taskExecutor;
   
    
    public void searchMail(CreditNativeEmail email) {
    	  try {
    		  String mailAddrSuffix = email.getEmail().substring( email.getEmail().lastIndexOf("@")+1, email.getEmail().length());
    		  NativeMailSearch search = SpringContextUtil.getBean(NativeMailType.getMailType(mailAddrSuffix).getSearchClzz());
    		  
    		  
    		  taskExecutor.execute(() -> {
    			try {
    				search.searchEmail(email);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
                  
              });
          } catch (Exception e) {
           
          }    
        
    }
}
