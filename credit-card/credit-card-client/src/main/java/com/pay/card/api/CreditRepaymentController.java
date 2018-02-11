package com.pay.card.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;

import springfox.documentation.annotations.ApiIgnore;

import com.alibaba.fastjson.JSONObject;
import com.pay.card.enums.CodeEnum;
import com.pay.card.interceptor.RepaymentResolverInterceptor;
import com.pay.card.model.CreditCard;
import com.pay.card.model.CreditRepayment;
import com.pay.card.service.CreditCardService;
import com.pay.card.service.CreditRepaymentService;
import com.pay.card.view.JsonResultView;
import com.pay.card.web.annotation.RedisCacheInterceptor;

@Api("信用卡卡片  API")
@RestController
@RequestMapping("/api/v1/repayment")
public class CreditRepaymentController {
    private static Logger logger = LoggerFactory.getLogger(CreditRepaymentController.class);

    @Autowired
    private CreditRepaymentService creditRepaymentService;

    @Autowired
    private CreditCardService creditCardService;

    @ApiOperation(value = "删除信用卡账单还款", httpMethod = HttpPost.METHOD_NAME, notes = "删除信用卡账单还款")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "customerNo", dataType = "String", required = true, value = "商户编号", defaultValue = "123456789"),
            @ApiImplicitParam(paramType = "query", name = "phoneNo", dataType = "String", required = true, value = "商户手机号", defaultValue = "123456789"),
            @ApiImplicitParam(paramType = "query", name = "channel", dataType = "String", required = true, value = "渠道号", defaultValue = "1001"),
            @ApiImplicitParam(paramType = "query", name = "id", dataType = "Long", required = true, value = "还款记录id", defaultValue = "1") })
    @RequestMapping(value = "/delCreditRepayment", method = RequestMethod.POST)
    @RedisCacheInterceptor
    public
            WebAsyncTask<JsonResultView<?>> delCreditRepayment(@RepaymentResolverInterceptor @ApiIgnore CreditRepayment creditRepayment) {

        Callable<JsonResultView<?>> callable = new Callable<JsonResultView<?>>() {
            @Override
            public JsonResultView<?> call() throws Exception {

                try {
                    CreditRepayment re = creditRepaymentService.delete(creditRepayment);

                    // JSONObject del = new JSONObject();
                    // del.put("userId", String.valueOf(creditRepayment.getUserInfo().getId()));
                    // if (re.getBill() != null) {
                    // del.put("billId", String.valueOf(re.getBill().getId()));
                    // if (re.getBill().getCard() != null) {
                    // del.put("cardId", String.valueOf(re.getBill().getCard().getId()));
                    // }
                    // }
                    // SpringContextUtil.publishCardEvent(del);
                    return new JsonResultView<>().setObject("");
                } catch (Exception e) {
                    logger.error(JSONObject.toJSONString(creditRepayment));
                    logger.error("还款记录id【" + creditRepayment.getId() + "】删除失败:{}", e);
                    return new JsonResultView<>(CodeEnum.DELETE_REPAYMENT_FAIL);
                }

            }
        };
        return new WebAsyncTask<JsonResultView<?>>(callable);
    }

    @ApiOperation(value = "保存信用卡账单还款", httpMethod = HttpPost.METHOD_NAME, notes = "保存信用卡账单还款,还款类型 0 标记还款,1 标记已还清 ,2 实际还款,3 账单还款")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "customerNo", dataType = "String", required = true, value = "商户编号", defaultValue = "123456789"),
            @ApiImplicitParam(paramType = "query", name = "phoneNo", dataType = "String", required = true, value = "商户手机号", defaultValue = "123456789"),
            @ApiImplicitParam(paramType = "query", name = "channel", dataType = "String", required = true, value = "渠道号", defaultValue = "1001"),
            @ApiImplicitParam(paramType = "query", name = "billId", dataType = "Long", required = false, value = "账单id", defaultValue = "3070"),
            @ApiImplicitParam(paramType = "query", name = "amount", dataType = "Double", required = true, value = "还款金额", defaultValue = "100"),
            @ApiImplicitParam(paramType = "query", name = "type", dataType = "Integer", required = true, value = " 0 标记还款,1 标记已还清 ,2 实际还款", defaultValue = "100"),
            @ApiImplicitParam(paramType = "query", name = "year", dataType = "String", required = true, value = "还款年", defaultValue = "2017"),
            @ApiImplicitParam(paramType = "query", name = "month", dataType = "String", required = true, value = "还款月", defaultValue = "12"),
            @ApiImplicitParam(paramType = "query", name = "cardId", dataType = "Long", required = true, value = "卡id", defaultValue = "2864")

    })
    @RequestMapping(value = "/saveCreditRepayment", method = RequestMethod.POST)
    @RedisCacheInterceptor
    public
            WebAsyncTask<JsonResultView<?>> saveCreditRepayment(@RepaymentResolverInterceptor @ApiIgnore CreditRepayment creditRepayment) {

        Callable<JsonResultView<?>> callable = new Callable<JsonResultView<?>>() {
            @Override
            public JsonResultView<?> call() throws Exception {
                Map<String, Object> map = new HashMap<String, Object>();

                try {
                    logger.info("用户id【" + creditRepayment.getUserInfo().getId() + "】保存信还款记录请求参数:{}", creditRepayment);
                    // 标记还清,还款金额服务端算,前端传过来的不要。
                    if (creditRepayment.getType() != null) {
                        if (creditRepayment.getType().intValue() == 1) {
                            CreditCard creditCard = creditCardService.findCreditCardByCardId(creditRepayment.getCardId(), creditRepayment
                                    .getUserInfo().getId());
                            BigDecimal amount = creditCard.getBillAmount().subtract(creditCard.getRepayment());
                            if (amount.doubleValue() > 0) {
                                creditRepayment.setAmount(amount);
                            } else {
                                return new JsonResultView<>(CodeEnum.BILL_PAIDOFF).setObject(new JSONObject());
                            }
                        }
                    }

                    CreditRepayment re = creditRepaymentService.saveOrUpdate(creditRepayment);
                    map.put("id", re.getId());
                    map.put("type", re.getType());
                    map.put("paymentAamout", re.getAmount());
                    return new JsonResultView<>().setObject(map);
                } catch (Exception e) {
                    logger.info("用户id【" + creditRepayment.getUserInfo().getId() + "】保存还款记录异常:{}", e);
                    return new JsonResultView<>(CodeEnum.SAVE_REPAYMENT_FAIL);
                }

            }
        };
        return new WebAsyncTask<JsonResultView<?>>(callable);
    }
}
