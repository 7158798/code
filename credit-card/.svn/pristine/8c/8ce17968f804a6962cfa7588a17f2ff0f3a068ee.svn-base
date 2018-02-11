package com.pay.card.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.pay.card.model.CreditBank;
import com.pay.card.service.CreditBankService;
import com.pay.card.utils.SpringContextUtil;
import com.pay.card.view.JsonResultView;
import com.pay.card.web.context.CardBuildContext;

/**
 * @author qiaohui
 *         测试用例
 */
@Api("银行 api")
@RestController
@RequestMapping("/api/v1/bank")
public class CreditBankController extends BaseController {

    @Autowired
    private CreditBankService bankService;

    @ApiOperation(value = "获取所有银行", httpMethod = HttpGet.METHOD_NAME)
    @RequestMapping(value = "/findtBankList")
    public JsonResultView<?> findtBankList() {
        List<CreditBank> rv = bankService.getBanks();
        CardBuildContext buildContext = apiHelper.getBuildContext();
        apiHelper.getModelBuilder().buildMulti(rv, buildContext);
        return new JsonResultView<>().setObject(apiHelper.getViewMapper().map(rv, buildContext));
    }

    @RequestMapping(value = "/pushEvent")
    public String pushEvent() {
        JSONObject json = new JSONObject();
        json.put("userId", "1");

        SpringContextUtil.publishCardEvent(json);
        return "";
    }

}
