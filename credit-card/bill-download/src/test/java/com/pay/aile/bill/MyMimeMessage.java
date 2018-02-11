package com.pay.aile.bill;

import java.io.InputStream;

import javax.activation.DataHandler;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimePart;
import javax.mail.internet.MimePartDataSource;
import javax.mail.internet.MimeBodyPart;

public class MyMimeMessage extends MimeMessage implements MimePart{

	protected MyMimeMessage(Folder folder, InputStream is, int msgnum) throws MessagingException {
		super(folder, is, msgnum);
		
	}
	
}
