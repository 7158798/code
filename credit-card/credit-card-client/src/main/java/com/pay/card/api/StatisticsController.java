package com.pay.card.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pay.card.model.CreditSet;
import com.pay.card.service.CreditFileService;
import com.pay.card.service.CreditSetService;
import com.pay.card.service.StatisticsService;
import com.pay.card.utils.AmountUtil;
import com.pay.card.view.JsonResultView;

@Api("信用卡数据查询 API")
@RestController
@RequestMapping("/api/v1/data")
public class StatisticsController {

    private static Logger logger = LoggerFactory.getLogger(StatisticsController.class);

    @Autowired
    private CreditSetService creditSetService;
    @Autowired
    private CreditFileService creditFileService;
    @Autowired
    private StatisticsService statisticsService;

    @ApiOperation(value = "账单解析记录", httpMethod = HttpGet.METHOD_NAME)
    @ApiImplicitParams({ @ApiImplicitParam(paramType = "query", name = "page", dataType = "String", required = true, value = "页码", defaultValue = "1") })
    @RequestMapping(value = "/queryCreditFile")
    public
            JsonResultView<?> queryCreditFile(String page) {

        Integer pg = AmountUtil.isNumber(page) ? new Integer(page) : new Integer(1);
        List<Map<String, Object>> pageList = null;
        try {
            pageList = creditFileService.findCreditFileList(pg);
        } catch (Exception e) {
            logger.error("查询账单解析记录异常:{}", e);
        }
        return new JsonResultView<>().setObject(pageList);
    }

    @ApiOperation(value = "获取信用卡信息", httpMethod = HttpGet.METHOD_NAME)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "page", dataType = "String", required = true, value = "页码", defaultValue = "1"),
            @ApiImplicitParam(paramType = "query", name = "num", dataType = "String", required = true, value = "每页数量", defaultValue = "10") })
    @RequestMapping(value = "/queryCreditInfo")
    public
            JsonResultView<?> queryCreditInfo(String page, String num) {
        Integer pg = AmountUtil.isNumber(page) ? new Integer(page) : new Integer(1);
        Integer nm = AmountUtil.isNumber(num) ? new Integer(num) : new Integer(10);
        List<Map<String, Object>> pageList = statisticsService.findCreditInfoList(pg, nm);
        return new JsonResultView<>().setObject(pageList);
    }

    @ApiOperation(value = "邮箱登录失败记录", httpMethod = HttpGet.METHOD_NAME)
    @ApiImplicitParams({ @ApiImplicitParam(paramType = "query", name = "page", dataType = "String", required = true, value = "页码", defaultValue = "1") })
    @RequestMapping(value = "/queryCreditLoginLog")
    public
            JsonResultView<?> queryCreditLoginLog(String page) {

        Integer pg = AmountUtil.isNumber(page) ? new Integer(page) : new Integer(1);
        List<Map<String, Object>> pageList = statisticsService.findCreditLoginLog(pg);
        return new JsonResultView<>().setObject(pageList);
    }

    @ApiOperation(value = "获取提醒配置", httpMethod = HttpGet.METHOD_NAME)
    @ApiImplicitParams({ @ApiImplicitParam(paramType = "query", name = "page", dataType = "String", required = true, value = "页码", defaultValue = "1") })
    @RequestMapping(value = "/queryCreditSet")
    public
            JsonResultView<?> querySets(String page) {
        CreditSet creditSet = new CreditSet();

        Integer pg = AmountUtil.isNumber(page) ? new Integer(page) : new Integer(1);
        List<Map<String, Object>> pageList = null;
        try {
            pageList = creditSetService.findCreditSetList(creditSet, pg);
        } catch (Exception e) {
            logger.error("获取提醒配置异常:{}", e);

        }
        return new JsonResultView<>().setObject(pageList);
    }

    @ApiOperation(value = "获取还款状态", httpMethod = HttpGet.METHOD_NAME)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "page", dataType = "String", required = true, value = "页码", defaultValue = "1"),
            @ApiImplicitParam(paramType = "query", name = "num", dataType = "String", required = true, value = "每页数量", defaultValue = "10") })
    @RequestMapping(value = "/queryCreditBillStatus")
    public
            JsonResultView<?> queryStatus(String page, String num) {
        Integer pg = AmountUtil.isNumber(page) ? new Integer(page) : new Integer(1);
        Integer nm = AmountUtil.isNumber(num) ? new Integer(num) : new Integer(10);
        List<Map<String, Object>> pageList = statisticsService.findCreditBillStatusList(pg, nm);
        return new JsonResultView<>().setObject(pageList);
    }
}
