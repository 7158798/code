package com.pay.card.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.http.client.methods.HttpPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;

import springfox.documentation.annotations.ApiIgnore;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pay.card.Exception.CreditClientException;
import com.pay.card.bean.CreditCardBean;
import com.pay.card.enums.RedisStatusEnum;
import com.pay.card.interceptor.CardResolverInterceptor;
import com.pay.card.model.CreditCard;
import com.pay.card.model.CreditUserCardRelation;
import com.pay.card.service.CreditCardService;
import com.pay.card.utils.CardComparator;
import com.pay.card.utils.CardIndexComparator;
import com.pay.card.view.CreditCardView;
import com.pay.card.view.JsonResultView;
import com.pay.card.web.annotation.RedisCacheInterceptor;
import com.pay.card.web.context.CardBuildContext;

@Api("信用卡卡片  API")
@RestController
@RequestMapping("/api/v1/card")
public class CreditCardController extends BaseController {

    @Autowired
    private CreditCardService creditCardService;

    @ApiOperation(value = "删除信用卡")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "cardId", dataType = "Long", required = true, value = "信用卡id", defaultValue = ""),
            @ApiImplicitParam(paramType = "query", name = "customerNo", dataType = "String", required = true, value = "商户编号", defaultValue = "123456789"),
            @ApiImplicitParam(paramType = "query", name = "channel", dataType = "String", required = true, value = "渠道", defaultValue = "1001"),
            @ApiImplicitParam(paramType = "query", name = "phoneNo", dataType = "String", required = true, value = "商户手机号", defaultValue = "123456789") })
    @RequestMapping(value = "/deleteCard", method = RequestMethod.GET)
    @RedisCacheInterceptor
    public
            WebAsyncTask<JsonResultView<?>> deleteCreditCard(@CardResolverInterceptor @ApiIgnore CreditCardBean creditCardBean) {
        Callable<JsonResultView<?>> callable = new Callable<JsonResultView<?>>() {

            @Override
            public JsonResultView<?> call() throws Exception {
                Map<String, String> map = new HashMap<String, String>();
                creditCardService.updateCardStatusById(creditCardBean.getCardId(), creditCardBean.getUserId());
                map.put("resCode", "0000");
                map.put("resMsg", "删除成功");
                // RedisRequestContext.setRequestContext("redisStatus",
                // "update");
                // JSONObject del = new JSONObject();
                // del.put("userId", creditCardBean.getUserId());
                // SpringContextUtil.publishCardEvent(del);
                return new JsonResultView<>().setObject(map);
            }
        };

        return new WebAsyncTask<JsonResultView<?>>(callable);

    }

    @ApiOperation(value = "获取信用卡首页数据", notes = "获取推荐卡和信用卡首页列表信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "customerNo", dataType = "String", required = true, value = "商户编号", defaultValue = "123456789"),
            @ApiImplicitParam(paramType = "query", name = "channel", dataType = "String", required = true, value = "渠道", defaultValue = "1001"),
            @ApiImplicitParam(paramType = "query", name = "phoneNo", dataType = "String", required = true, value = "商户手机号", defaultValue = "123456789"),
            @ApiImplicitParam(paramType = "query", name = "cardId", dataType = "String", required = true, value = "信用卡id", defaultValue = "2862") })
    @ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"), @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
    @RequestMapping(value = "/findCard", method = RequestMethod.GET)
    public
            WebAsyncTask<JsonResultView<?>> findCard(@CardResolverInterceptor @ApiIgnore CreditCardBean creditCardBean,
                    HttpServletRequest req, HttpServletResponse res) {

        Callable<JsonResultView<?>> callable = new Callable<JsonResultView<?>>() {

            @Override
            public JsonResultView<?> call() throws Exception {
                CreditCard creditCard = new CreditCard();
                creditCard.setId(creditCardBean.getCardId());
                creditCard.setUserId(creditCardBean.getUserId());
                logger.info("用户手机号【" + creditCardBean.getPhoneNo() + "】获取信用卡首页数据请求参数:{}", creditCard);
                CreditCard card = creditCardService.findCreditCard(creditCard);
                if (card != null) {
                    CardBuildContext buildContext = apiHelper.getBuildContext();
                    apiHelper.getModelBuilder().buildSingle(card, buildContext);
                    CreditCardView cardView = apiHelper.getViewMapper().map(card, buildContext);

                    return new JsonResultView<>().setObject(cardView);
                } else {
                    return new JsonResultView<>().setObject(new JSONObject());
                }
            }
        };

        return new WebAsyncTask<JsonResultView<?>>(callable);

    }

    @ApiOperation(value = "获取信用卡首页数据", notes = "获取推荐卡和信用卡首页列表信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "customerNo", dataType = "String", required = true, value = "商户编号", defaultValue = "123456789"),
            @ApiImplicitParam(paramType = "query", name = "channel", dataType = "String", required = true, value = "渠道", defaultValue = "1001"),
            @ApiImplicitParam(paramType = "query", name = "phoneNo", dataType = "String", required = true, value = "商户手机号", defaultValue = "123456789") })
    @ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"), @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
    @RequestMapping(value = "/findCardIndex", method = RequestMethod.GET)
    @RedisCacheInterceptor
    public
            WebAsyncTask<JsonResultView<?>> findCardIndex(@CardResolverInterceptor @ApiIgnore CreditCardBean creditCardBean,
                    HttpServletRequest req, HttpServletResponse res) {
        Callable<JsonResultView<?>> callable = new Callable<JsonResultView<?>>() {

            @Override
            public JsonResultView<?> call() throws Exception {
                CreditCard creditCard = new CreditCard();
                creditCard.setUserId(creditCardBean.getUserId());

                if (req.getAttribute("cacheValue") != null) {
                    JSONObject jsonObject = JSONObject.parseObject(req.getAttribute("cacheValue").toString());
                    logger.info("用户手机号【" + creditCardBean.getPhoneNo() + "】获取信用卡首页返回缓存数据:{}", jsonObject);
                    return new JsonResultView<>().setObject(jsonObject);
                }
                try {
                    logger.info("用户手机号【" + creditCardBean.getPhoneNo() + "】获取信用卡首页数据请求参数:{}", creditCard);
                    List<CreditCard> list = creditCardService.findCreditCardList(creditCard);
                    logger.info("用户手机号【" + creditCardBean.getPhoneNo() + "】获取信用卡List:{}", list);
                    if (CollectionUtils.isNotEmpty(list)) {
                        CardBuildContext buildContext = apiHelper.getBuildContext();
                        apiHelper.getModelBuilder().buildMulti(list, buildContext);
                        List<CreditCardView> viewList = apiHelper.getViewMapper().map(list, buildContext);
                        Collections.sort(viewList, new CardIndexComparator());
                        CreditCardView recommendCard = getRecommendCard(viewList);
                        Map<String, Object> indexMap = new HashMap<String, Object>();
                        if (CollectionUtils.isNotEmpty(viewList)) {
                            indexMap.put("cardList", viewList);
                            indexMap.put("recommendCard", recommendCard);
                            redisCache(indexMap, RedisStatusEnum.QUERY, "");
                        }
                        return new JsonResultView<>().setObject(indexMap);

                    }
                    return new JsonResultView<>().setObject(new JSONObject());
                } catch (Exception e) {
                    logger.error("用户手机号【" + creditCardBean.getPhoneNo() + "】获取信用卡首页异常:{}", e);
                }
                return null;

            }
        };

        return new WebAsyncTask<JsonResultView<?>>(callable);

    }

    @ApiOperation(value = "获取推荐信用", notes = "获取信用卡列表信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "customerNo", dataType = "String", required = true, value = "商户编号", defaultValue = "123456789"),
            @ApiImplicitParam(paramType = "query", name = "channel", dataType = "String", required = true, value = "渠道", defaultValue = "1001"),
            @ApiImplicitParam(paramType = "query", name = "phoneNo", dataType = "String", required = true, value = "商户手机号", defaultValue = "123456789") })
    @ApiResponses({ @ApiResponse(code = 400, message = "请求参数没填好"), @ApiResponse(code = 404, message = "请求路径没有或页面跳转路径不对") })
    @RequestMapping(value = "/findRecommendCardList", method = RequestMethod.GET)
    public
            WebAsyncTask<JsonResultView<?>> findRecommendCardList(@CardResolverInterceptor @ApiIgnore CreditCardBean creditCardBean,
                    HttpServletRequest req) {

        Callable<JsonResultView<?>> callable = new Callable<JsonResultView<?>>() {
            @Override
            public JsonResultView<?> call() throws Exception {
                CreditCard creditCard = new CreditCard();
                creditCard.setUserId(creditCardBean.getUserId());

                try {
                    if (req.getAttribute("cacheValue") != null) {
                        JSONObject jsonObject = JSONObject.parseObject(req.getAttribute("cacheValue").toString());
                        logger.info("用户手机号【" + creditCardBean.getPhoneNo() + "】获取推荐卡返回缓存数据:{}", jsonObject);
                        return new JsonResultView<>().setObject(jsonObject);
                    }
                    logger.info("用户手机号【" + creditCardBean.getPhoneNo() + "】获取推荐卡请求参数:{}", creditCard);
                    List<CreditCard> list = creditCardService.findCreditCardList(creditCard);
                    CardBuildContext buildContext = apiHelper.getBuildContext();
                    apiHelper.getModelBuilder().buildMulti(list, buildContext);
                    List<CreditCardView> viewList = apiHelper.getViewMapper().map(list, buildContext);
                    viewList = getRecommendCardList(viewList);
                    redisCache(viewList, RedisStatusEnum.QUERY, "");

                    return new JsonResultView<>().setObject(viewList);
                } catch (Exception e) {
                    logger.error("用户手机号【" + creditCardBean.getPhoneNo() + "】获取推荐卡异常:{}", e);
                }
                return new JsonResultView<>().setObject(new JSONArray());
            }
        };
        return new WebAsyncTask<JsonResultView<?>>(callable);
    }

    /**
     * @Title: findRecommendCard
     * @Description:查找推荐卡
     * @param list
     * @param @return
     *        参数 @return CreditCardView 返回类型 @throws
     */
    private CreditCardView getRecommendCard(List<CreditCardView> list) {
        List<CreditCardView> recommendList = getRecommendCardList(list);
        // 按照剩余额度大于20% 并且
        // list = list.stream()
        // .filter((card) -> ((Double.valueOf(card.getCredits()) -
        // Double.valueOf(card.getBillAmount())
        // - Double.valueOf(card.getConsumption()) +
        // Double.valueOf(card.getRepayment()))
        // / Double.valueOf(card.getCredits()) > 0.2 && card.getSource() == 0))
        // .sorted((CreditCardView o1, CreditCardView o2) ->
        // o1.getFreeInterestPeriod().intValue()
        // - o2.getFreeInterestPeriod().intValue())
        // .collect(Collectors.toList());
        // List<CreditCardView> recommendList = getRecommendCardList(list);
        if (CollectionUtils.isNotEmpty(recommendList)) {
            return recommendList.get(0);
        }

        return null;
    }

    /**
     * @Title: getRecommendCardList
     * @Description:获取推荐用卡
     * @param list
     * @param @return
     *        参数 @return List<CreditCardView> 返回类型 @throws
     */
    private List<CreditCardView> getRecommendCardList(List<CreditCardView> list) {

        // List<CreditCardView> blist = list.stream()
        // .filter((card) -> ((Double.valueOf(card.getCredits()) -
        // Double.valueOf(card.getBillAmount())
        // - Double.valueOf(card.getConsumption()) +
        // Double.valueOf(card.getRepayment()))
        // / Double.valueOf(card.getCredits()) > 0.2
        // && Double.valueOf(card.getCredits()) -
        // Double.valueOf(card.getBillAmount())
        // - Double.valueOf(card.getConsumption()) +
        // Double.valueOf(card.getRepayment()) > 1000
        // && card.getSource() == 0))
        //
        // .collect(Collectors.toList());
        List<CreditCardView> recommendCardList = new ArrayList<CreditCardView>();
        List<CreditCardView> noRecommendCardList = new ArrayList<CreditCardView>();
        for (CreditCardView cardView : list) {
            if (cardView.filterCard()) {
                recommendCardList.add(cardView);
            } else {
                noRecommendCardList.add(cardView);
            }
        }

        Collections.sort(recommendCardList, new CardComparator());
        Collections.sort(noRecommendCardList, new CardComparator());

        recommendCardList.addAll(noRecommendCardList);
        return recommendCardList;
    }

    @ApiOperation(value = "保存信用卡卡片", httpMethod = HttpPost.METHOD_NAME, notes = "对应手输账单、设置页面")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "id", dataType = "long", required = false, value = "信用卡id", defaultValue = "1"),
            @ApiImplicitParam(paramType = "query", name = "customerNo", dataType = "String", required = true, value = "商户编号", defaultValue = "123456789"),
            @ApiImplicitParam(paramType = "query", name = "name", dataType = "String", required = false, value = "卡的别名", defaultValue = "买菜卡"),
            @ApiImplicitParam(paramType = "query", name = "channel", dataType = "String", required = true, value = "渠道", defaultValue = "1001"),
            @ApiImplicitParam(paramType = "query", name = "phoneNo", dataType = "String", required = true, value = "商户手机号", defaultValue = "123456789"),
            @ApiImplicitParam(paramType = "query", name = "numbers", dataType = "String", required = true, value = "信用卡卡号", defaultValue = "123456789123456789"),
            @ApiImplicitParam(paramType = "query", name = "bank.code", dataType = "String", required = true, value = "对应银行id", defaultValue = "ABC"),
            @ApiImplicitParam(paramType = "query", name = "billDay", dataType = "Integer", required = false, value = "账单日", defaultValue = "10"),
            @ApiImplicitParam(paramType = "query", name = "dueDay", dataType = "Integer", required = false, value = "还款日", defaultValue = "10"),
            @ApiImplicitParam(paramType = "query", name = "dueType", dataType = "Integer", required = false, value = "还款日类型 0固定还款日 1账单日多少天后还款", defaultValue = "10"),
            @ApiImplicitParam(paramType = "query", name = "credits", dataType = "BigDecimal", required = false, value = "信用额度", defaultValue = "5000"),
            @ApiImplicitParam(paramType = "query", name = "billAmount", dataType = "BigDecimal", required = false, value = "本期账单金额", defaultValue = "1000"),
            @ApiImplicitParam(paramType = "query", name = "cardholder", dataType = "String", required = false, value = "持卡人", defaultValue = "1"),
            @ApiImplicitParam(paramType = "query", name = "source", dataType = "Integer", required = true, value = "数据来源，手工添加", defaultValue = "1")

    })
    @RequestMapping(value = "/saveCreditCard", method = RequestMethod.POST)
    @RedisCacheInterceptor
    public
            WebAsyncTask<JsonResultView<?>> saveCreditCard(@CardResolverInterceptor @ApiIgnore CreditCardBean creditCardBean,
                    @ApiIgnore CreditCard creditCard, @ApiIgnore CreditUserCardRelation relation) {

        Callable<JsonResultView<?>> callable = new Callable<JsonResultView<?>>() {
            @Override
            public JsonResultView<?> call() throws Exception {

                try {
                    logger.info("用户手机号【" + creditCardBean.getPhoneNo() + "】保存信用卡卡片请求参数creditCardBean:{},creditCard:{},relation:{}",
                            creditCardBean, creditCard, relation);
                    logger.info("cardId:{},credits:{}", creditCard.getId(), creditCard.getCredits());

                    CreditCard card = creditCardService.saveOrUpdateCreditCard(creditCard, creditCardBean, relation);

                    return new JsonResultView<>().setObject(card.getId());
                } catch (Exception e) {

                    logger.error("用户手机号【" + creditCardBean.getPhoneNo() + "】保存信用卡卡片异常:{}", e);
                    if (e instanceof CreditClientException) {

                        return new JsonResultView<>(((CreditClientException) e).getCodeEnum()).setObject("");
                    }
                    return new JsonResultView<>().setObject("");
                }

            }
        };
        return new WebAsyncTask<JsonResultView<?>>(callable);
    }

}
