package com.pay.aile.bill.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pay.aile.bill.entity.CreditBill;
import com.pay.aile.bill.service.CreditBillService;

@Controller
public class CreditBillController {
	@Autowired
	private CreditBillService creditBillService;

	@RequestMapping(value = "billList")
	public String gotoList(CreditBill creditBill, Model model) {
		List<CreditBill> billList = creditBillService.getBillList(creditBill);
		model.addAttribute("billList", billList);
		return "billList";
	}
}
