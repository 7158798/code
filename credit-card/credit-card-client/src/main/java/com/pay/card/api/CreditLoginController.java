package com.pay.card.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import springfox.documentation.annotations.ApiIgnore;

import com.alibaba.fastjson.JSONObject;
import com.pay.card.Constants;
import com.pay.card.enums.ChannelEnum;
import com.pay.card.enums.CodeEnum;
import com.pay.card.enums.StatusEnum;
import com.pay.card.interceptor.UserResolverInterceptor;
import com.pay.card.model.CreditUserInfo;
import com.pay.card.service.CreditSetService;
import com.pay.card.service.CreditUserInfoService;
import com.pay.card.utils.JedisUtil;

@Api("信用卡入口")
@RestController
@RequestMapping("/api/v1/login")
public class CreditLoginController extends BaseController {

    @Autowired
    private CreditUserInfoService creditUserInfoService;
    @Autowired
    private CreditSetService creditSetService;

    @ApiOperation(value = "信用卡首页入口", httpMethod = HttpGet.METHOD_NAME)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "customerNo", dataType = "String", required = true, value = "商户编号", defaultValue = "123456789"),
            @ApiImplicitParam(paramType = "query", name = "phoneNo", dataType = "String", required = true, value = "商户手机号", defaultValue = "123456789"),
            @ApiImplicitParam(paramType = "query", name = "channel", dataType = "String", required = true, value = "渠道号", defaultValue = "1001") })
    @RequestMapping("toIndex")
    public
            JSONObject creditLogin(@UserResolverInterceptor @ApiIgnore CreditUserInfo user, HttpServletResponse response) {

        try {
            // 如果没有则保存
            if (user.getId() == null) {
                logger.info("用户id【" + user.getPhoneNo() + "】userId为空");
                saveCreditUserInfo(user);

                return CodeEnum.SUCCESS.getJsonMsg();
            }

        } catch (Exception e) {
            logger.error("登录信息{}跳转信用卡首页异常:{}", user.toString(), e);

            return CodeEnum.FAIL.getJsonMsg();
        }

        // String url = "redirect:/api/v1/card/findCardIndex?customerNo=%s&phoneNo=%s&channel=%s";
        // url = String.format(url, user.getCustomerNo(), user.getPhoneNo(), user.getChannel());
        logger.info("用户id【" + user.getId() + "】已注册");
        return CodeEnum.SUCCESS.getJsonMsg();

    }

    private String getKey(CreditUserInfo user) {
        String key = "";
        if (ChannelEnum.MPOS.getCode().equals(user.getChannel())) {
            key = user.getPhoneNo() + user.getChannel();
        } else if (ChannelEnum.POS.getCode().equals(user.getChannel())) {
            key = user.getCustomerNo() + user.getChannel();
        }
        return key;
    }

    private void saveCreditUserInfo(CreditUserInfo user) throws Exception {
        String key = getKey(user);
        CreditUserInfo userInfo = creditUserInfoService.findCreditUserInfo(user);

        logger.info("userInfo:{}", userInfo);
        if (userInfo == null) {
            // 添加默认值
            logger.info("用户手机号【" + user.getPhoneNo() + "】未注册,开始注册");
            user.setStatus(StatusEnum.ENABLE.getStatus());
            user.setCreateDate(new Date());
            Long id = creditUserInfoService.saveCreditUserInfo(user);
            // 添加消息提醒配置
            // CreditSet creditSet = new CreditSet();
            // CreditUserInfo info = new CreditUserInfo();
            // info.setId(id);
            // creditSet.setUserInfo(info);
            // creditSetService.saveCreditSet(creditSet);
            user.setId(id);
            JedisUtil.saveString(Constants.REDIS_USERID_KEY + key, id + "");

            // LocalCacheUtil.put(key, id);
        } else {
            logger.info("用户手机号【" + user.getPhoneNo() + "】已注册,放入redis缓存");
            JedisUtil.saveString(Constants.REDIS_USERID_KEY + key, userInfo.getId() + "");
            // LocalCacheUtil.put(key, userInfo.getId());
        }
    }
    // private void saveCreditUserInfo(String phoneNo, String customerNo, String channel, String key) throws Exception {
    //
    // CreditUserInfo creditUserInfo = new CreditUserInfo();
    // creditUserInfo.setCustomerNo(customerNo);
    // creditUserInfo.setPhoneNo(phoneNo);
    // creditUserInfo.setChannel(channel);
    // creditUserInfo.setStatus(StatusEnum.ENABLE.getStatus());
    // creditUserInfo.setCreateDate(new Date());
    // CreditUserInfo userInfo = creditUserInfoService.findCreditUserInfo(creditUserInfo);
    // if (userInfo == null) {
    // Long id = creditUserInfoService.saveCreditUserInfo(creditUserInfo);
    // LocalCacheUtil.put(key, id);
    // } else {
    // LocalCacheUtil.put(key, userInfo.getId());
    // }
    // }
}
