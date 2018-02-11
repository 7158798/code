package com.pay.aile.bill.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pay.aile.bill.entity.CreditBank;
import com.pay.aile.bill.entity.CreditCardtype;
import com.pay.aile.bill.entity.CreditEmail;
import com.pay.aile.bill.entity.CreditTemplate;
import com.pay.aile.bill.service.CreditBankService;
import com.pay.aile.bill.service.CreditCardtypeService;
import com.pay.aile.bill.service.CreditTemplateService;

@Controller
public class TemplateController {
	@Autowired
	private CreditBankService creditBankService;
	@Autowired
	private CreditCardtypeService creditCardtypeService;

	@Autowired
	private CreditTemplateService creditTemplateService;

	@RequestMapping(value = "/analyze")
	@ResponseBody
	public String analyze(CreditEmail creditEmail, Model model) {

		return "";
	}

	@RequestMapping(value = "/templateList")
	public String gotoTemplateList(CreditTemplate creditTemplate, Model model) {
		List<CreditTemplate> templateList = creditTemplateService.getList(creditTemplate);
		model.addAttribute("templateList", templateList);
		return "templateList";
	}

	@RequestMapping(value = "/saveTemplate")
	public String saveTemplate(CreditTemplate creditTemplate, CreditCardtype creditCardType, Model model) {
		creditCardType = creditCardtypeService.saveOrUpdate(creditCardType);
		creditTemplate.setCardtypeId(creditCardType.getId());
		creditTemplateService.saveOrUpdate(creditTemplate);

		return "redirect:/templateList";
	}

	@RequestMapping(value = "/templateForm")
	public String templateForm(CreditTemplate creditTemplate, Model model) {
		List<CreditBank> bankList = creditBankService.getAllList(new CreditBank());
		model.addAttribute("bankList", bankList);
		return "templateForm";
	}
}
