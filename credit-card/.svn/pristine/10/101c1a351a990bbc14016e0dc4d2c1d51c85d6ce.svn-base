package com.pay.aile.bill.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/***
 * IndexController.java
 *
 * @author shinelon
 *
 * @date 2017年10月26日
 *
 */
@Controller
public class IndexController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping("/")
    @ResponseBody
    public String index() {
        logger.debug("bill application is startup!");
        return "bill application is startup!";
    }
}
