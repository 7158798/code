package com.pay.card.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.pay.card.model.CreditChannel;
import com.pay.card.service.CreditChannelService;

@Api("渠道api")
@RestController
@RequestMapping("/api/v1/channel")
public class CreditChannelController {

    private static Logger logger = LoggerFactory.getLogger(CreditChannelController.class);

    @Autowired
    private CreditChannelService creditChannelService;

    @ApiOperation(value = "保存渠道信息", httpMethod = HttpPost.METHOD_NAME)
    @RequestMapping("/save")
    public String saveCreditChannel(String interfaceName) {
        CreditChannel creditChannel = new CreditChannel();
        try {
            creditChannel.setChannelName(interfaceName);
            creditChannel = creditChannelService.saveCreditChannel(creditChannel);
        } catch (Exception e) {
            logger.error("渠道名称【" + interfaceName + "】保存渠道信息异常:{}", e);
        }
        return JSON.toJSONString(creditChannel);

    }
}
