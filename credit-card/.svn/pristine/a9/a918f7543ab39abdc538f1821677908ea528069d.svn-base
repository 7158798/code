package com.pay.aile.bill.service.mail.analyze.impl;

import java.util.List;

import com.pay.aile.bill.utils.PatternMatcherUtil;

public class A {

	public static void main(String[] args) {
		String content = "交易摘要 Description 交易金额 Trans Amount 卡号后四位Card Number Last 4 Digits 2017/09/16 2017/09/16 北京金鹰轩商贸有限公司TK2713SEXIAOMIPAY 1000.00 1248 2017/09/16 2017/09/16 支付宝快捷支付 18.80 1050 2017/09/17 2017/09/17 北京市三友百姓快餐食堂TK2713SEXIAOMIPAY 269.00 1248 2017/09/19 2017/09/19 北京吉品味食府TK2713SEXIAOMIPAY 279.00 1248 2017/09/20 2017/09/20 财付通支付科技有限公司客户备付金 2.98 1248 2017/09/21 2017/09/21 北京市兰亭苑零售工艺品TK2713SEXIAOMIPAY 207.00 1248 2017/09/27 2017/09/27 网银跨行互联转入 -921.05 1248 美元交易明细USDTrans.Details 交易日 Trans Date 记账日Post Date 交易摘要 Description 交易金额 Trans Amount 卡号后四位Card Number Last 4 Digits 说明Instruction";

		String r = "\\d{4}/\\d{2}/\\d{2} \\d{4}/\\d{2}/\\d{2} \\S+ \\S+ \\S+";
		List<String> list = PatternMatcherUtil.getMatcher(r, content);

		System.out.println(list.size());
	}

}
