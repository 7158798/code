package com.pay.aile.bill.service.natives.mail.search;

import java.io.IOException;

import com.pay.aile.bill.entity.CreditNativeEmail;

public interface NativeMailSearch {

	public void searchEmail(CreditNativeEmail email) throws IOException;
	
	
}
