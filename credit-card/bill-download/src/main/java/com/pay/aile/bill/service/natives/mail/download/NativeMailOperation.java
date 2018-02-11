package com.pay.aile.bill.service.natives.mail.download;

import java.io.IOException;

import com.pay.aile.bill.entity.CreditNativeEmail;

public interface NativeMailOperation {

	public void downloadEmail(CreditNativeEmail email) throws IOException;
}
