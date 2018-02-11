package com.pay.aile.bill.job;

import java.io.IOException;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.pay.aile.bill.entity.CreditNativeEmail;
import com.pay.aile.bill.enums.NativeMailType;
import com.pay.aile.bill.service.natives.mail.download.NativeMailOperation;
import com.pay.aile.bill.utils.SpringContextUtil;

@Component
public class NativeEmaileDownloadScheduler {

    private static final Logger logger = LoggerFactory.getLogger(NativeEmaileDownloadScheduler.class);
    
    @Resource(name = "nativeEmaileDownloadExecutor")
    private ThreadPoolTaskExecutor taskExecutor;
   
    
    public void downLoadMailLoop(CreditNativeEmail email) {
    	  try {
    		  String mailAddrSuffix = email.getEmail().substring( email.getEmail().lastIndexOf("@")+1, email.getEmail().length());
    		  NativeMailOperation operation = SpringContextUtil.getBean(NativeMailType.getMailType(mailAddrSuffix).getClzz());
    		  
    		  
    		  taskExecutor.execute(() -> {
    			try {
					operation.downloadEmail(email);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
                  
              });
          } catch (Exception e) {
           
          }    
        
    }
}
