package com.pay.aile.bill;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BBBBB {

	public static void main(String[] args) {
		//1518139925437
//		
//		long l =1518061303l;
//		Date dd = new Date();
//		System.out.println(dd.getTime());
//		
//		Calendar cal = Calendar.getInstance();
//		cal.setTimeInMillis(l*1000);
//		Date date = cal.getTime();
//		
////		Date d = new Date(1518139242*1000);
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");  
//		System.out.println(sdf.format(date));
		String sid="oC468GE9xJXoMTBqu--TFvOA,4,qaDE3RGlueUY2SUFaNFA5NHNnc2YzeTVtKlNXTmljZmJxLUlNKnF0Wk1zNF8.";
		sid = sid.replaceAll("\\S{1}--\\S{5}", "UcDT5vNw");
		System.out.println(sid);
	}

}
