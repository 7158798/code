package com.pay.aile.bill;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TestA {

	public static void main(String[] args) {
		LocalDate dNow = LocalDate.now();
		LocalTime now = LocalTime.of(11, 0);
		//LocalTime now = LocalTime.now();
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DateTimeFormatter sdf = DateTimeFormatter.ofPattern("HH:mm:ss");
		DateTimeFormatter sdf2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String sendTime = sdf2.format(dNow)+" "+sdf.format(now);
		System.out.println(sendTime);
		// String ss = "北京银行-信用卡电子账单";
		//
		// String regEx="\\S*北京\\S*";
		// //String regEx
		// ="(\\S+ABC\\S+)|(\\S+农业\\S+)|(\\S+BCM\\S+)|(\\S+交通\\S+)|(\\S+BOB\\S+)|(\\S+北京\\S+)|(\\S+BOC\\S+)|(\\S+中国银行\\S+)|(\\S+中国\\S+)|(\\S+CCB\\S+)|(\\S+建设\\S+)|(\\S+CEB\\S+)|(\\S+光大\\S+)|(\\S+CIB\\S+)|(\\S+兴业\\S+)|(\\S+CITIC\\S+)|(\\S+中信\\S+)|(\\S+CMB\\S+)|(\\S+招商\\S+)|(\\S+信用管家消费提醒\\S+)|(\\S+CMBC\\S+)|(\\S+民生\\S+)|(\\S+GDB\\S+)|(\\S+广发\\S+)|(\\S+ICBC\\S+)|(\\S+工商\\S+)|(\\S+SDB\\S+)|(\\S+PAB\\S+)|(\\S+平安\\S+)|(\\S+PSBC\\S+)|(\\S+邮储\\S+)|(\\S+浙商\\S+)|(\\S+华夏\\S+)";
		// Pattern pattern = Pattern.compile(regEx);
		//
		// Matcher matcher = pattern.matcher(ss);
		//
		// boolean rs = matcher.matches();
		// System.out.println(rs);

	}

}
