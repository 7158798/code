package com.pay.aile.bill;

import java.io.File;
import java.util.Base64;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.pay.aile.bill.utils.PatternMatcherUtil;

public class AAA {
	public static String sendDateReg="发送日期：\\d{4}-\\d{2}-\\d{2}";
	public static String subjectReg="主题：[\\u4e00-\\u9fa5]+ ";
	public static void main(String[] args) {
//		String readEmailNum="100";
//		String emailNum ="956";
//		System.out.println((int)(Double.parseDouble(readEmailNum)/Double.parseDouble(emailNum)*100));	
		String ss= new String(Base64.getDecoder().decode("YmlsbDFpbWFw"));
		System.out.println(ss);
//	    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
//		System.out.println(LocalDate.now().format(dateTimeFormatter));
//		String aa ="jj@126.com";
//		String emailService =aa.substring( aa.lastIndexOf("@")+1, aa.length());
//		System.out.println(emailService);
//		File in = new File("d://yj.txt");
//		try {
//			Document doc = Jsoup.parse(in, "utf-8");
//			
//			Element div = doc.getElementById("isForwardContent");
//			
//			String text = div.text();
//			
//			List<String>  oo = PatternMatcherUtil.getMatcher(sendDateReg, text);
//			System.out.println(oo.get(0));
//			List<String>  ss = PatternMatcherUtil.getMatcher(subjectReg, text);
//			System.out.println(ss.get(0));
//			Elements  elements  = div.getElementsByTag("br");
//			for(Element br : elements) {
//				//br.get
//				Elements selements = br.siblingElements();
//				//System.out.println(11);
//			}
//			//System.out.println(div.html());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
	}
	
	public String getSendDate(String text) {
		return "";
		
	}
	
	public String getSubject(String text) {
		return "";
		
	}
}
