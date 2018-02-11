package com.pay.aile.bill;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CCC {

	public static void main(String[] args) {
//		String s = "\"subject\":\"交通银行信用卡电子账单\",\"sentDate\":new Date(2018,1,8,11,41,43),";
//		String reg = "new Date\\(\\d+,\\d+,\\d+,\\d+,\\d+,\\d+\\)";
//
//		Pattern pattern = Pattern.compile(reg);
//		Matcher matcher = pattern.matcher(s);
//		while(matcher.find()){
//			String matchWord = matcher.group(0);
//			String property =matchWord;
//			System.out.println(property);
//			s = s.replace(matchWord, "\""+property+"\"");
//		}
//		System.out.println(s);
		
		String sendDate = "new Date(2017,9,31,11,49,47)";
	
		sendDate = sendDate.substring(sendDate.indexOf("(")+1, sendDate.indexOf(")"));
		System.out.println(sendDate);
		}
}
