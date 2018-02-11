package com.pay.aile.bill.web;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.pay.aile.bill.config.TemplateCache;
import com.pay.aile.bill.entity.CreditCardtype;
import com.pay.aile.bill.entity.CreditTemplate;
import com.pay.aile.bill.mapper.CreditCardtypeMapper;

@Controller
public class CreditTemplateController {
    private static Logger logger = LoggerFactory.getLogger(CreditTemplateController.class);
    @Autowired
    private CreditCardtypeMapper creditCardtypeMapper;

    @RequestMapping(value = "/updateTemplate")
    @ResponseBody
    public String updateTemplate(CreditTemplate template) {
        logger.debug("updateTemplate-----template={}", template);
        try {
            Long cardTypeId = template.getCardtypeId();
            CreditCardtype type = creditCardtypeMapper.selectById(cardTypeId);
            if (type == null) {
                return "未找到信用卡类型";
            }
            String cardCode = type.getCode();
            CreditTemplate t = TemplateCache.templateCache.get(cardCode);
            if (t == null) {
                return "未找到模板";
            }
            copyPropertiesIgnoreNull(template, t);
            logger.debug("updateTemplate success! template={}",
                    JSON.toJSONString(TemplateCache.templateCache.get(cardCode)));
            return "success";
        } catch (Exception e) {
            logger.error("updateTemplate error!");
            return "error";
        }
    }

    private CreditTemplate copyPropertiesIgnoreNull(CreditTemplate source, CreditTemplate target) {
        Field[] fields = target.getClass().getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            try {
                Object value = f.get(source);
                if (value != null) {
                    f.set(target, value);
                }
            } catch (IllegalArgumentException | IllegalAccessException e) {

            }
        }
        return target;
    }
}
