package com.pay.aile.bill;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.util.MimeMessageParser;

public class Eml {
	public static void main(String args[]) throws Exception {

		test1("E:\\d\\中国农业银行金穗信用卡电子对账单_.eml");
		// test("E:\\d\\转发：中国农业银行金穗信用卡电子对账单_.eml");

	}

	// http://blog.csdn.net/aassdd_zz/article/details/8204344
	public static void parserFile(String emlPath) throws Exception {
		System.out.println(emlPath);
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		InputStream inMsg;
		inMsg = new FileInputStream(emlPath);
		Message msg = new MimeMessage(session, inMsg);
		parseEml(msg);
	}

	private static void parseEml(Message msg) throws Exception {
		// 发件人信息
		Address[] froms = msg.getFrom();
		if (froms != null) {
			// System.out.println("发件人信息:" + froms[0]);
			InternetAddress addr = (InternetAddress) froms[0];
			System.out.println("发件人地址:" + addr.getAddress());
			System.out.println("发件人显示名:" + addr.getPersonal());
		}
		System.out.println("邮件主题:" + msg.getSubject());
		// getContent() 是获取包裹内容, Part相当于外包装
		Object o = msg.getContent();
		if (o instanceof Multipart) {
			Multipart multipart = (Multipart) o;
			reMultipart(multipart);
		} else if (o instanceof Part) {
			Part part = (Part) o;
			rePart(part);
		} else {
			System.out.println("类型" + msg.getContentType());
			System.out.println("内容" + msg.getContent());
		}
	}

	/**
	 * @param part
	 *            解析内容
	 * @throws Exception
	 */
	private static void rePart(Part part) throws Exception {

		if (part.getDisposition() != null) {

			String strFileNmae = part.getFileName();
			if (!StringUtils.isEmpty(strFileNmae)) { // MimeUtility.decodeText解决附件名乱码问题
				strFileNmae = MimeUtility.decodeText(strFileNmae);
				System.out.println("发现附件: " + strFileNmae);

				InputStream in = part.getInputStream();// 打开附件的输入流
				// 读取附件字节并存储到文件中
				java.io.FileOutputStream out = new FileOutputStream(strFileNmae);
				int data;
				while ((data = in.read()) != -1) {
					out.write(data);
				}
				in.close();
				out.close();

			}

			System.out.println("内容类型: " + MimeUtility.decodeText(part.getContentType()));
			System.out.println("附件内容:" + part.getContent());

		} else {
			if (part.getContentType().startsWith("text/plain")) {
				System.out.println("文本内容：" + part.getContent());
			} else {
				// System.out.println("HTML内容：" + part.getContent());
			}
		}
	}

	/**
	 * @param multipart
	 *            // 接卸包裹（含所有邮件内容(包裹+正文+附件)）
	 * @throws Exception
	 */
	private static void reMultipart(Multipart multipart) throws Exception {
		// System.out.println("邮件共有" + multipart.getCount() + "部分组成");
		// 依次处理各个部分
		for (int j = 0, n = multipart.getCount(); j < n; j++) {
			// System.out.println("处理第" + j + "部分");
			Part part = multipart.getBodyPart(j);// 解包, 取出 MultiPart的各个部分,
													// 每部分可能是邮件内容,
			// 也可能是另一个小包裹(MultipPart)
			// 判断此包裹内容是不是一个小包裹, 一般这一部分是 正文 Content-Type: multipart/alternative
			if (part.getContent() instanceof Multipart) {
				Multipart p = (Multipart) part.getContent();// 转成小包裹
				// 递归迭代
				reMultipart(p);
			} else {
				rePart(part);
			}
		}
	}

	@SuppressWarnings("resource")
	public static void test(String emlPath) {
		try {

			System.out.println(emlPath);
			Properties props = new Properties();
		
			Session session = Session.getDefaultInstance(props, null);
			InputStream inMsg;
			inMsg = new FileInputStream(emlPath);
			String dd = MimeUtility.mimeCharset("gb18030");
			System.out.println(dd);

			Message msg = new MimeMessage(session, inMsg);
			if(msg.getAllHeaders().hasMoreElements()) {
				Object tt = msg.getAllHeaders().nextElement();
				System.out.println("ssss"+tt);
			}
			String[] date = msg.getHeader("Date");
			Address[] from = msg.getFrom();
			for (Address address : from) {
				InternetAddress internetAddress = (InternetAddress) address;
				System.out.println(internetAddress.getAddress());
				System.out.println(internetAddress.getPersonal());
			}
				
			Address[] to = msg.getReplyTo();
			Object o = null;
			try {
				msg.getContent();
				 
			} catch (UnsupportedEncodingException uex) {
				
				DataHandler dataHandler= msg.getDataHandler();
			
				InputStream is =dataHandler.getInputStream();
				
				String cc = IOUtils.toString(is);
			
			//	cc = MimeUtility.encodeText(cc, ,"B");
		
//				InputStream is = msg.getInputStream();
//				String cc = IOUtils.toString(is);
//				
//				System.out.println(Arrays.toString(cc.getBytes())); 
				//cc = new String(cc.getBytes("GB2312"),"UTF-8");
				System.out.println(cc);
			}
			
			if (msg.isMimeType("multipart/*") || msg.isMimeType("MULTIPART/*")) {
				System.out.println("multipart");
				Multipart mp = (Multipart) o;

				int totalAttachments = mp.getCount();
				if (totalAttachments > 0) {
					for (int i = 0; i < totalAttachments; i++) {
						Part part = mp.getBodyPart(i);
						String s = getMailContent(part);
						String attachFileName = part.getFileName();
						String disposition = part.getDisposition();
						String contentType = part.getContentType();
						if ((attachFileName != null && attachFileName.endsWith(".ics"))
								|| contentType.indexOf("text/calendar") >= 0) {
							String[] dateHeader = msg.getHeader("date");
						}

						System.out.println(s);
						System.out.println(attachFileName);
						System.out.println(disposition);
						System.out.println(contentType);
						System.out.println("==============");
					}
					inMsg.close();
				}
			} else if (o instanceof Part) {
				Part part = (Part) o;
				rePart(part);
			} else {
				System.out.println("类型" + msg.getContentType());
				System.out.println("内容" + msg.getContent());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String getMailContent(Part part) throws Exception {
		String contenttype = part.getContentType();
		int nameindex = contenttype.indexOf("name");
		boolean conname = false;
		if (nameindex != -1) {
			conname = true;
		}
		StringBuilder bodytext = new StringBuilder();
		if (part.isMimeType("text/plain") && !conname) {
			bodytext.append((String) part.getContent());
		} else if (part.isMimeType("text/html") && !conname) {
			bodytext.append((String) part.getContent());
		} else if (part.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) part.getContent();
			int counts = multipart.getCount();
			for (int i = 0; i < counts; i++) {
				getMailContent(multipart.getBodyPart(i));
			}
		} else if (part.isMimeType("message/rfc822")) {
			getMailContent((Part) part.getContent());
		} else {
		}
		return bodytext.toString();
	}

	public static StringBuffer getUtf8(MimeMessage message) {

		MimeMessageParser parser = new MimeMessageParser(message);

		String string = "";
		StringBuffer buffer = new StringBuffer();
		// 解决乱码问题
		if (string.contains("charset")) {
			string = string.replaceFirst("(?<=charset=)[\\w-]+", "UTF-8");
			return buffer.append(string);
		} else {
			StringBuffer charsetHead = new StringBuffer(
					"<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + "UTF-8" + "\"/></head>");
			//charsetHead.append(content_forward);
			// content_forward.append("<head><meta http-equiv=\"Content-Type\"
			// content=\"text/html; charset="+Constants.CHARSET+"\"/></head>");
			// return content_forward;
			return charsetHead;
		}
	}
	
	
	
	
	public static void test1(String emlPath) {
		try {

			System.out.println(emlPath);
			Properties props = new Properties();
		
			Session session = Session.getDefaultInstance(props, null);
			InputStream inMsg;
			inMsg = new FileInputStream(emlPath);

			
			MimeMessage message = new MimeMessage(session, inMsg);
			System.out.println(message.getContentType());
			String contentType=message.getContentType();
			Enumeration ee =message.getAllHeaderLines();
			//message.setHeader(name, value);
			if(contentType.contains("charset=\"\"")) {
				contentType =   contentType.replaceAll("charset=\"\"", "charset=\"gb18030\"");
				message.setHeader("Content-Type", contentType);
			}
			System.out.println("内容" + message.getContent());
			
//			
			MimeMessageParser parser = new MimeMessageParser(message);
			 parser.parse();
//			 
//			
//			 
//			 String html =parser.getHtmlContent();
//			 
			// System.out.println(html);
			 
		}catch(Exception e) {
			e.getStackTrace();
		}	

	}

	
}
