package com.pay.card.api;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.client.methods.HttpGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.WebAsyncTask;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pay.card.Constants;
import com.pay.card.bean.CreditBillBean;
import com.pay.card.bean.CreditCardBean;
import com.pay.card.dao.StatisticsDao;
import com.pay.card.enums.CodeEnum;
import com.pay.card.enums.RedisStatusEnum;
import com.pay.card.interceptor.BillResolverInterceptor;
import com.pay.card.interceptor.CardResolverInterceptor;
import com.pay.card.model.CreditBank;
import com.pay.card.model.CreditBill;
import com.pay.card.model.CreditBillDetail;
import com.pay.card.model.CreditCard;
import com.pay.card.model.CreditEmail;
import com.pay.card.model.CreditRepayment;
import com.pay.card.model.CreditUserEmailRelation;
import com.pay.card.model.CreditUserInfo;
import com.pay.card.service.CreditBillDetailService;
import com.pay.card.service.CreditBillService;
import com.pay.card.service.CreditCardService;
import com.pay.card.service.CreditRepaymentService;
import com.pay.card.service.CreditUserBillRelationService;
import com.pay.card.service.CreditUserEmailRelationService;
import com.pay.card.service.CreditUserInfoService;
import com.pay.card.utils.AmountUtil;
import com.pay.card.utils.DateUtil;
import com.pay.card.utils.JedisUtil;
import com.pay.card.view.AnalysisResultView;
import com.pay.card.view.CreditBillDetailView;
import com.pay.card.view.CreditBillView;
import com.pay.card.view.CreditCardView;
import com.pay.card.view.JsonResultView;
import com.pay.card.web.annotation.RedisCacheInterceptor;
import com.pay.card.web.context.CardBuildContext;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@Api("信用卡账单 API")
@RestController
@RequestMapping("/api/v1/bill")
public class CreditBillController extends BaseController {

    public static final long loopIntervalSeconds = 180L;
    @Autowired
    private CreditBillService creditBillService;
    @Autowired
    private CreditCardService creditCardService;
    @Autowired
    private CreditUserInfoService creditUserInfoService;
    @Autowired
    private CreditBillDetailService creditBillDetailService;
    @Autowired
    private CreditRepaymentService creditRepaymentService;

    @Autowired
    private StatisticsDao statisticsDao;
    @Autowired
    private CreditUserBillRelationService creditUserBillRelationService;
    @Autowired
    private CreditUserEmailRelationService creditUserEmailRelationService;
    @Value("${billDownloadUrl}")
    private String billDownloadUrl = "";
    @Autowired
    private RestTemplate restTemplate;

    // @ApiOperation(value = "删除信用卡账单")
    // @ApiImplicitParams({
    // @ApiImplicitParam(paramType = "query", name = "billId", dataType =
    // "Long", required = true, value = "账单id", defaultValue = "2405"),
    // @ApiImplicitParam(paramType = "query", name = "phoneNo", dataType =
    // "String", required = true, value = "手机号", defaultValue = "12345678900"),
    // @ApiImplicitParam(paramType = "query", name = "customerNo", dataType =
    // "String", required = true, value = "商户号", defaultValue = "8622632348"),
    // @ApiImplicitParam(paramType = "query", name = "channel", dataType =
    // "String", required = true, value = "渠道号", defaultValue = "1001") })
    @RequestMapping(value = "/deleteCreditBill", method = RequestMethod.GET)
    public WebAsyncTask<JsonResultView<?>> deleteCreditBill(
            @BillResolverInterceptor @ApiIgnore CreditBillBean creditBillBean) {
        Callable<JsonResultView<?>> callable = new Callable<JsonResultView<?>>() {

            @Override
            public JsonResultView<?> call() throws Exception {
                Map<String, String> map = new HashMap<String, String>();
                creditBillService.updateBillStatusById(creditBillBean.getBillId(), creditBillBean.getUserId());
                map.put("resCode", "0000");
                map.put("resMsg", "删除成功");
                return new JsonResultView<>().setObject(map);
            }
        };
        return new WebAsyncTask<JsonResultView<?>>(callable);
    }

    @ApiOperation(value = "查询解析结果", httpMethod = HttpGet.METHOD_NAME, notes = "查询解析结果")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "email", dataType = "String", required = true, value = "邮箱用户名", defaultValue = "czb18518679659@126.com"),
            @ApiImplicitParam(paramType = "query", name = "customerNo", dataType = "String", required = true, value = "商户编号", defaultValue = "123456789"),
            @ApiImplicitParam(paramType = "query", name = "channel", dataType = "String", required = true, value = "渠道", defaultValue = "1001"),
            @ApiImplicitParam(paramType = "query", name = "phoneNo", dataType = "String", required = true, value = "商户手机号", defaultValue = "123456789") })
    @RequestMapping(value = "/findAnalysisResult", method = RequestMethod.GET)
    @SuppressWarnings("rawtypes")
    public WebAsyncTask<JsonResultView> findAnalysisResult(
            @BillResolverInterceptor @ApiIgnore CreditBillBean creditBillBean) {
        Callable<JsonResultView> callable = new Callable<JsonResultView>() {

            @SuppressWarnings("unchecked")
            @Override
            public JsonResultView call() throws Exception {
                CreditCard creditCard = new CreditCard();
                creditCard.setEmail(creditBillBean.getEmail());
                creditCard.setUserId(creditBillBean.getUserId());
                List<AnalysisResultView> billViewList = new ArrayList<AnalysisResultView>();

                try {
                    logger.info("用户手机号【" + creditBillBean.getPhoneNo() + "】查询解析结果请求参数:{},userId:{}", creditCard,
                            creditBillBean.getUserId());
                    List<CreditCard> cardList = creditCardService.findCreditCardList(creditCard);
                    logger.info("用户手机号【" + creditBillBean.getPhoneNo() + "】信用卡信息:{}", cardList);
                    List<Long> billId = new ArrayList<Long>();
                    List<CreditCard> newCardList = new ArrayList<CreditCard>();
                    if (cardList != null) {
                        for (CreditCard creditCard2 : cardList) {
                            CreditBill creditBill = new CreditBill();
                            creditBill.setCard(creditCard2);
                            creditBill.setNewStatus(1);
                            // creditBill.setUserId(creditBillBean.getUserId());
                            List<CreditBill> billList = creditBillService.findNewCreditBillList(creditBill);

                            // 根据是否有新账单显示
                            if (billList != null && billList.size() > 0) {
                                creditCard2.setBillSize(billList.size());
                                billList.forEach(bill -> {
                                    billId.add(bill.getId());
                                });
                                newCardList.add(creditCard2);
                            }

                        }
                        cardList = newCardList;
                        // String key =
                        // String.format(Constants.CREDIT_CMB_SIMPLE_NOT_SUPPORT,
                        // creditBillBean.getEmail(),
                        // String.valueOf(creditBillBean.getUserId()));

                        // 是否有未解析的招商简版账单
                        // String not = JedisUtil.getString(key);
                        //
                        // logger.info("not======================{}", not);
                        // 解析的信用卡的列表
                        Set<String> cardSet = JedisUtil.getMembers(
                                Constants.REDIS_CARDS + creditBillBean.getEmail() + "_" + creditBillBean.getUserId());

                        logger.info("cardSet======================{}", JSONObject.toJSONString(cardSet));
                        // Set<String> cardNoSet = new HashSet<String>();
                        // cardSet.forEach(card -> {
                        // JSONObject cardJson = (JSONObject)
                        // JSONObject.parse(card);
                        // cardNoSet.add(cardJson.getString("cardNo"));
                        // });
                        // logger.info("cardNoSet======================{}",
                        // JSONObject.toJSONString(cardNoSet));

                        CardBuildContext buildContext = apiHelper.getBuildContext2();
                        apiHelper.getModelBuilder().buildMulti(cardList, buildContext);
                        billViewList = apiHelper.getOverrideViewMapper().map(cardList, buildContext);

                        // logger.info("billViewList======================{}",
                        // JSONObject.toJSONString(billViewList));
                        // 已经展示过的卡号不再进行展示
                        // billViewList = billViewList.stream().filter(card ->
                        // cardNoSet.contains(card.getNumbers()))
                        // .collect(Collectors.toList());
                        logger.info("用户手机号【" + creditBillBean.getPhoneNo() + "】查询解析结果返回视图:{}",
                                JSONObject.toJSONString(billViewList));
                        // 招商银行简版
                        List<Map<String, Object>> cmbList = statisticsDao.findCmbSimple(creditBillBean.getEmail());
                        if (cmbList != null && cmbList.size() > 0) {
                            CreditCard cmbCard = new CreditCard();
                            CreditBank bank = new CreditBank();
                            bank.setName("招商银行");
                            bank.setShortName("招行");
                            bank.setCode("CMB");
                            cmbCard.setBank(bank);
                            cmbCard.setCardholder("");
                            cmbCard.setBillSize(0);
                            cmbCard.setId(0L);
                            cmbCard.setNumbers("");
                            apiHelper.getModelBuilder().buildSingle(cmbCard, buildContext);
                            AnalysisResultView cmbCardView = apiHelper.getOverrideViewMapper().map(cmbCard,
                                    buildContext);

                            billViewList.add(cmbCardView);

                        }

                        // AnalysisBuildContext buildContext =
                        // analysisHelper.getAnalysisBuildContext();
                        //
                        // analysisHelper.getAnalysisModelBuilder().buildMulti(cardList,
                        // buildContext);
                        // List<AnalysisResultView> billViewList =
                        // analysisHelper.getViewMapper().map(cardList,
                        // buildContext);
                        //
                        // return new JsonResultView().setObject(billViewList);
                        // 删除招商简版不支持的缓存

                        // 修改
                        if (billId != null && billId.size() > 0) {
                            creditBillService.updateNewStatus(billId);

                        }
                        return new JsonResultView().setObject(billViewList);
                    } else {
                        return new JsonResultView().setObject(new JSONArray());
                    }
                } catch (Exception e) {
                    logger.error("用户手机号【" + creditBillBean.getPhoneNo() + "】查询解析结果异常:{}", e);
                }
                return null;

            }
        };
        return new WebAsyncTask<JsonResultView>(callable);
    }

    // @ApiOperation(value = "查询账单的解析状态", httpMethod = HttpGet.METHOD_NAME,
    // notes = "查询账单的解析状态")
    // @ApiImplicitParams({
    // @ApiImplicitParam(paramType = "query", name = "email", dataType =
    // "String", required = true, value = "邮箱用户名", defaultValue =
    // "czb18518679659@126.com"),
    // @ApiImplicitParam(paramType = "query", name = "customerNo", dataType =
    // "String", required = true, value = "商户编号", defaultValue = "123456789"),
    // @ApiImplicitParam(paramType = "query", name = "channel", dataType =
    // "String", required = true, value = "渠道", defaultValue = "1001"),
    // @ApiImplicitParam(paramType = "query", name = "phoneNo", dataType =
    // "String", required = true, value = "商户手机号", defaultValue = "123456789")
    // })
    // @RequestMapping(value = "/findAnalysisStatus", method =
    // RequestMethod.GET)
    // @SuppressWarnings("rawtypes")
    // public
    // WebAsyncTask<JsonResultView> findAnalysisStatus(@BillResolverInterceptor
    // @ApiIgnore CreditBillBean creditBillBean) {
    // Callable<JsonResultView> callable = new Callable<JsonResultView>() {
    // @SuppressWarnings("unchecked")
    // @Override
    // public JsonResultView call() throws Exception {
    //
    // try {
    // logger.info("用户手机号【" + creditBillBean.getPhoneNo() + "】查询账单解析状态请求参数:{}",
    // creditBillBean);
    // // 获取账单数量
    // String billNumber = JedisUtil.getString(Constants.REDIS_ANALYSIS_STATUS +
    // creditBillBean.getEmail() + "_"
    // + creditBillBean.getUserId());
    // // 获取解析数量
    // String analyzedNum = JedisUtil.getString(Constants.REDIS_ANALYZED_STATUS
    // + creditBillBean.getEmail() + "_"
    // + creditBillBean.getUserId());
    // // 解析的信用卡的列表
    // Set<String> cardSet = JedisUtil.getMembers(Constants.REDIS_CARDS +
    // creditBillBean.getEmail() + "_"
    // + creditBillBean.getUserId());
    //
    // Set<String> hasCardSet =
    // JedisUtil.getMembers(String.format(Constants.REDIS_EXISTS_CARD_NO,
    // creditBillBean.getEmail(),
    // String.valueOf(creditBillBean.getUserId())));
    // JSONArray cardArray = new JSONArray();
    // cardSet.forEach(card -> {
    // JSONObject cardJson = JSONObject.parseObject(card);
    // if (hasCardSet != null &&
    // !hasCardSet.contains(cardJson.getString("cardNo"))) {
    // cardArray.add(cardJson);
    // }
    // });
    // JSONObject result = new JSONObject();
    // result.put("billNumber", billNumber);
    // result.put("analyzedNum", analyzedNum);
    // result.put("cardList", cardArray);
    //
    // logger.info("用户手机号【" + creditBillBean.getPhoneNo() + "】查询账单解析状态返回结果:{}",
    // result);
    // return new JsonResultView().setObject(result);
    // } catch (Exception e) {
    // logger.error("用户手机号【" + creditBillBean.getPhoneNo() + "】查询账单解析状态异常:{}",
    // e);
    // }
    // return new JsonResultView().setObject(new JSONObject());
    // }
    // };
    // return new WebAsyncTask<JsonResultView>(callable);
    // }

    @ApiOperation(value = "查询账单的解析状态", httpMethod = HttpGet.METHOD_NAME, notes = "查询账单的解析状态")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "email", dataType = "String", required = true, value = "邮箱用户名", defaultValue = "czb18518679659@126.com"),
            @ApiImplicitParam(paramType = "query", name = "customerNo", dataType = "String", required = true, value = "商户编号", defaultValue = "123456789"),
            @ApiImplicitParam(paramType = "query", name = "channel", dataType = "String", required = true, value = "渠道", defaultValue = "1001"),
            @ApiImplicitParam(paramType = "query", name = "phoneNo", dataType = "String", required = true, value = "商户手机号", defaultValue = "123456789") })
    @RequestMapping(value = "/findAnalysisStatus", method = RequestMethod.GET)
    @SuppressWarnings("rawtypes")
    public WebAsyncTask<JsonResultView> findAnalysisStatus(
            @BillResolverInterceptor @ApiIgnore CreditBillBean creditBillBean) {
        Callable<JsonResultView> callable = new Callable<JsonResultView>() {
            @SuppressWarnings("unchecked")
            @Override
            public JsonResultView call() throws Exception {

                try {
                    logger.info("用户手机号【" + creditBillBean.getPhoneNo() + "】查询账单解析状态请求参数:{}", creditBillBean);

                    // 获取账单数量
                    String billNumber = JedisUtil.getString(Constants.REDIS_ANALYSIS_STATUS + creditBillBean.getEmail()
                            + "_" + creditBillBean.getUserId());
                    // 获取解析数量
                    String analyzedNum = JedisUtil.getString(Constants.REDIS_ANALYZED_STATUS + creditBillBean.getEmail()
                            + "_" + creditBillBean.getUserId());

                    // 获取解析数量
                    String emailNum = JedisUtil.getString(Constants.REDIS_EMAIL_NUMBER_STATUS
                            + creditBillBean.getEmail() + "_" + creditBillBean.getUserId());
                    // 获取解析数量
                    String readEmailNum = JedisUtil.getString(Constants.REDIS_EMAIL_READ_NUMBER_STATUS
                            + creditBillBean.getEmail() + "_" + creditBillBean.getUserId());

                    // 解析的信用卡的列表
                    Set<String> cardSet = JedisUtil.getMembers(
                            Constants.REDIS_CARDS + creditBillBean.getEmail() + "_" + creditBillBean.getUserId());

                    Set<String> hasCardSet = JedisUtil.getMembers(String.format(Constants.REDIS_EXISTS_CARD_NO,
                            creditBillBean.getEmail(), String.valueOf(creditBillBean.getUserId())));
                    JSONArray cardArray = new JSONArray();
                    cardSet.forEach(card -> {
                        JSONObject cardJson = JSONObject.parseObject(card);
                        if (hasCardSet != null && !hasCardSet.contains(cardJson.getString("cardNo"))) {
                            cardArray.add(cardJson);
                        }
                    });
                    JSONObject result = new JSONObject();
                    result.put("billNumber", billNumber);
                    result.put("analyzedNum", analyzedNum);

                    result.put("emailNum", emailNum);
                    result.put("readEmailNum", readEmailNum);

                    if (Integer.parseInt(emailNum) == -1) {
                        result.put("schedule", 100);
                    } else if (Integer.parseInt(emailNum) == 0) {
                        result.put("schedule", 0);
                    } else {
                        if (Double.parseDouble(readEmailNum) / Double.parseDouble(emailNum) > 0.9) {
                            if (Integer.parseInt(billNumber) <= Integer.parseInt(analyzedNum)) {
                                result.put("schedule", 100);

                            } else {
                                result.put("schedule", 90);

                            }
                        } else {
                            result.put("schedule",
                                    (int) (Double.parseDouble(readEmailNum) / Double.parseDouble(emailNum) * 100));
                        }

                    }

                    result.put("cardList", cardArray);

                    logger.info("用户手机号【" + creditBillBean.getPhoneNo() + "】查询账单解析状态返回结果:{}", result);
                    return new JsonResultView().setObject(result);
                } catch (Exception e) {
                    logger.error("用户手机号【" + creditBillBean.getPhoneNo() + "】查询账单解析状态异常:{}", e);
                }
                return new JsonResultView().setObject(new JSONObject());
            }
        };
        return new WebAsyncTask<JsonResultView>(callable);
    }

    @ApiOperation(value = "查询账单日历", httpMethod = HttpGet.METHOD_NAME, notes = "查询账单日历")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "customerNo", dataType = "String", required = true, value = "商户编号", defaultValue = "123456789"),
            @ApiImplicitParam(paramType = "query", name = "channel", dataType = "String", required = true, value = "渠道", defaultValue = "1001"),
            @ApiImplicitParam(paramType = "query", name = "phoneNo", dataType = "String", required = true, value = "商户手机号", defaultValue = "123456789") })
    @RequestMapping(value = "/findBillCalendar", method = RequestMethod.GET)
    @SuppressWarnings("rawtypes")
    @RedisCacheInterceptor
    public WebAsyncTask<JsonResultView> findBillCalendar(
            @CardResolverInterceptor @ApiIgnore CreditCardBean creditCardBean, HttpServletRequest req) {

        Callable<JsonResultView> callable = new Callable<JsonResultView>() {
            @Override
            @SuppressWarnings("unchecked")
            public JsonResultView call() throws Exception {
                try {
                    logger.info("用户手机号【" + creditCardBean.getPhoneNo() + "】查询账单日历请求参数:{}", creditCardBean);
                    if (req.getAttribute("cacheValue") != null) {
                        JSONObject jsonObject = JSONObject.parseObject(req.getAttribute("cacheValue").toString());
                        logger.info("用户手机号【" + creditCardBean.getPhoneNo() + "】查询账单日历返回缓存数据:{}", jsonObject);
                        return new JsonResultView<>().setObject(jsonObject);
                    }
                    JSONArray biillArray = creditBillService.findBillCalendar(creditCardBean);
                    redisCache(biillArray, RedisStatusEnum.QUERY, "");
                    logger.info("用户手机号【" + creditCardBean.getPhoneNo() + "】查询账单日历返回结果:{}", biillArray);
                    return new JsonResultView().setObject(biillArray);
                } catch (Exception e) {
                    logger.error("用户手机号【" + creditCardBean.getPhoneNo() + "】查询账单日历异常:{}", e);
                }
                return new JsonResultView().setObject(new JSONArray());

            }
        };
        return new WebAsyncTask<JsonResultView>(callable);
    }

    @SuppressWarnings("rawtypes")
    @ApiOperation(value = "获取账单详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "billId", dataType = "String", required = true, value = "账单id", defaultValue = "3092"),
            @ApiImplicitParam(paramType = "query", name = "yearMonth", dataType = "String", required = true, value = "账单年月", defaultValue = "2017_10"),
            @ApiImplicitParam(paramType = "query", name = "customerNo", dataType = "String", required = true, value = "商户编号", defaultValue = "123456789"),
            @ApiImplicitParam(paramType = "query", name = "channel", dataType = "String", required = true, value = "渠道", defaultValue = "1001"),
            @ApiImplicitParam(paramType = "query", name = "phoneNo", dataType = "String", required = true, value = "商户手机号", defaultValue = "123456789") })
    @RequestMapping(value = "/findbillDetail", method = RequestMethod.GET)
    @RedisCacheInterceptor
    public WebAsyncTask<JsonResultView> findBillDetail(
            @BillResolverInterceptor @ApiIgnore CreditBillBean creditBillBean, HttpServletRequest req) {
        Callable<JsonResultView> callable = new Callable<JsonResultView>() {

            @SuppressWarnings("unchecked")
            @Override
            public JsonResultView call() throws Exception {

                try {
                    logger.info("用户手机号【" + creditBillBean.getPhoneNo() + "】查询账单详情请求参数:{}", creditBillBean);
                    if (req.getAttribute("cacheValue") != null) {
                        JSONObject jsonObject = JSONObject.parseObject(req.getAttribute("cacheValue").toString());
                        logger.info("用户手机号【" + creditBillBean.getPhoneNo() + "】查询账单详情返回缓存数据:{}", jsonObject);
                        return new JsonResultView<>().setObject(jsonObject);
                    }

                    Map<String, Object> map = new HashMap<String, Object>();
                    List<CreditBillDetailView> billDetailViewList = new ArrayList<CreditBillDetailView>();

                    List<CreditBillDetail> billDetails = creditBillDetailService.findBillDetailList(
                            creditBillBean.getBillId(), creditBillBean.getUserId(), creditBillBean.getYearMonth());

                    if (CollectionUtils.isNotEmpty(billDetails)) {
                        CardBuildContext buildContext = apiHelper.getBuildContext();
                        apiHelper.getModelBuilder().buildSingle(billDetails, buildContext);
                        billDetailViewList = apiHelper.getViewMapper().map(billDetails, buildContext);
                    }

                    map.put("billDetailViewList", billDetailViewList);
                    redisCache(map, RedisStatusEnum.QUERY, "");
                    logger.info("用户手机号【" + creditBillBean.getPhoneNo() + "】查询账单详情返回结果:{}", map);
                    return new JsonResultView().setObject(map);
                } catch (Exception e) {
                    logger.error("用户手机号【" + creditBillBean.getPhoneNo() + "】查询账单详情异常:{}", e);
                }
                return new JsonResultView().setObject(new JSONObject());
            }
        };

        return new WebAsyncTask<JsonResultView>(callable);

    }

    /**
     * @Title: findBillIndex
     * @Description:查询账单首页的数据
     * @param cardId
     * @return WebAsyncTask<JsonResultView> 返回类型 @throws
     */
    @ApiOperation(value = "查询账单首页数据", httpMethod = HttpGet.METHOD_NAME, notes = "查询账单首页数据")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "customerNo", dataType = "String", required = true, value = "商户编号", defaultValue = "123456789"),
            @ApiImplicitParam(paramType = "query", name = "channel", dataType = "String", required = true, value = "渠道", defaultValue = "1001"),
            @ApiImplicitParam(paramType = "query", name = "phoneNo", dataType = "String", required = true, value = "商户手机号", defaultValue = "123456789"),
            @ApiImplicitParam(paramType = "query", name = "cardId", dataType = "String", required = true, value = "银行卡Id", defaultValue = "2860") })
    @RequestMapping(value = "/findBillIndex", method = RequestMethod.GET)
    @SuppressWarnings("rawtypes")
    @RedisCacheInterceptor
    public WebAsyncTask<JsonResultView> findBillIndex(@BillResolverInterceptor @ApiIgnore CreditBillBean creditBillBean,
            HttpServletRequest req) {

        Callable<JsonResultView> callable = new Callable<JsonResultView>() {
            @Override
            @SuppressWarnings({ "unchecked", "unused" })
            public JsonResultView call() throws Exception {

                try {
                    logger.info("用户手机号【" + creditBillBean.getPhoneNo() + "】查询账单首页请求参数:{}", creditBillBean);
                    if (req.getAttribute("cacheValue") != null) {
                        JSONObject jsonObject = JSONObject.parseObject(req.getAttribute("cacheValue").toString());
                        logger.info("用户手机号【" + creditBillBean.getPhoneNo() + "】查询账单首页返回缓存数据:{}", jsonObject);
                        return new JsonResultView<>().setObject(jsonObject);
                    }
                    Map<String, Object> map = new HashMap<String, Object>();
                    List<CreditBillView> billViewList = new ArrayList<CreditBillView>();
                    LocalDate localDate = null;
                    // 查询信用卡
                    CreditCard creditCard = new CreditCard();
                    creditCard.setId(creditBillBean.getCardId());
                    creditCard.setUserId(creditBillBean.getUserId());
                    creditCard = creditCardService.findCreditCard(creditCard);
                    logger.info("用户手机号【" + creditBillBean.getPhoneNo() + "】查询账单首页信用卡信息:{}", creditCard);
                    if (creditCard != null) {
                        CardBuildContext buildContext = apiHelper.getBuildContext();
                        apiHelper.getModelBuilder().buildSingle(creditCard, buildContext);
                        CreditCardView cardView = apiHelper.getViewMapper().map(creditCard, buildContext);

                        map = getRequestMap(creditBillBean, creditCard);

                        billViewList = (List<CreditBillView>) map.get("billViewList");

                        String year = String.valueOf(DateUtil.dateToLocalDate(creditCard.getEndDate()).getYear());
                        String month = String
                                .valueOf(DateUtil.dateToLocalDate(creditCard.getEndDate()).getMonthValue());

                        // int sumAmount = 0;
                        // sumAmount =
                        // creditBillDetailService.findFutureBillAmountCount(creditBillBean.getCardId(),
                        // creditBillBean.getUserId());
                        //
                        // // 未出账单明细
                        // List<CreditBillDetail> futureBillDetailList =
                        // creditBillDetailService.findFutureBillDetail(
                        // creditBillBean.getCardId(),
                        // creditBillBean.getUserId());
                        // if (CollectionUtils.isNotEmpty(futureBillDetailList))
                        // {
                        // buildContext = apiHelper.getBuildContext();
                        // apiHelper.getModelBuilder().buildMulti(futureBillDetailList,
                        // buildContext);
                        // futureBillDetailViewList =
                        // apiHelper.getViewMapper().map(futureBillDetailList,
                        // buildContext);
                        // }
                        //
                        // // 还有n天出账单
                        // int nextBillDays = getNextBillDays(creditCard);
                        //
                        // logger.info("用户手机号【" + creditBillBean.getPhoneNo() +
                        // "】查询账单详情未出账单明细:{}",
                        // JsonUtils.toJsonString(futureBillDetailViewList));
                        // // 账单列表
                        // String futureBillCycle = "";
                        // CreditBill creditBill = new CreditBill();
                        // CreditCard card = new CreditCard();
                        // card.setId(creditBillBean.getCardId());
                        // creditBill.setCard(card);
                        // creditBill.setUserId(creditBillBean.getUserId());
                        // List<CreditBill> billList =
                        // creditBillService.findCreditBillList(creditBill);
                        //
                        // // 最新一期账单明细
                        // if (CollectionUtils.isNotEmpty(billList)) {
                        // buildContext = apiHelper.getBuildContext();
                        // apiHelper.getModelBuilder().buildMulti(billList,
                        // buildContext);
                        // billViewList =
                        // apiHelper.getViewMapper().map(billList,
                        // buildContext);
                        // logger.info("用户手机号【" + creditBillBean.getPhoneNo() +
                        // "】查询账单详情账单列表:{}",
                        // JsonUtils.toJsonString(billViewList));
                        //
                        // String billDate = billList.get(0).getYear() +
                        // billList.get(0).getMonth() +
                        // billList.get(0).getBillDay();
                        // futureBillCycle =
                        // DateUtil.getFutureBillCycle(billDate);
                        //
                        // Long billId = billList.get(0).getId();
                        // String year = billList.get(0).getYear();
                        // String method = billList.get(0).getMonth();
                        // List<CreditBillDetail> billDetails =
                        // creditBillDetailService.findBillDetailList(billId,
                        // creditBillBean.getUserId(), year + "_" + method);
                        // if (CollectionUtils.isNotEmpty(billDetails)) {
                        // apiHelper.getModelBuilder().buildSingle(billDetails,
                        // buildContext);
                        // billDetailViewList =
                        // apiHelper.getViewMapper().map(billDetails,
                        // buildContext);
                        // }
                        // logger.info("用户手机号【" + creditBillBean.getPhoneNo() +
                        // "】查询账单详情最新一期账单明细:{}", billDetailViewList);
                        //
                        // String date = billList.get(0).getYear() + "-" +
                        // billList.get(0).getMonth() + "-" +
                        // billList.get(0).getBillDay();
                        //
                        // if (creditCard.getSource() == 0) { // 邮箱导入
                        // if (creditCard.getBank() != null) {
                        //
                        // localDate =
                        // DateUtil.getDueDate(Integer.valueOf(creditCard.getBillDay()),
                        // Integer.valueOf(creditCard
                        // .getDueDay()), LocalDate.parse(date).plusMonths(1),
                        // creditCard.getBank().getRepaymentCycle());
                        // }
                        // } else { // 手工导入
                        // if (creditCard.getUserCardRelation() != null) {
                        // int dueType =
                        // creditCard.getUserCardRelation().getDueType();
                        // int billDay =
                        // creditCard.getUserCardRelation().getBillDay();
                        // int dueDay =
                        // creditCard.getUserCardRelation().getDueDay();
                        // localDate = DateUtil.getDueDate(billDay, dueDay,
                        // LocalDate.parse(date).plusMonths(1), dueType == 0 ? 0
                        // : dueDay);
                        // }
                        // }
                        // }
                        //
                        // futureBillMap.put("futureSumAmount",
                        // AmountUtil.amountFormat(new BigDecimal(sumAmount)));
                        // futureBillMap.put("nextBillDays", nextBillDays);
                        // futureBillMap.put("futureBillDay",
                        // creditCard.getUserCardRelation().getBillDay());
                        // futureBillMap.put("futureBillCycle",
                        // futureBillCycle);
                        // String futureDueDay = "";
                        // if (futureDueDay != null) {
                        // futureDueDay =
                        // DateFormatUtils.format(DateUtil.localDateToDate(localDate),
                        // "MM月dd日");
                        // futureBillMap.put("futureDueDay", futureDueDay);
                        // } else {
                        // futureBillMap.put("futureDueDay", futureDueDay);
                        // }
                        cardView.setYear(year);
                        cardView.setMonth(month);
                        cardView.setLastUpdatTime(getLastUpdateTime(creditBillBean.getUserId()));
                        map.put("cardView", cardView);
                        // map.put("futureBillView", futureBillMap);
                        // map.put("futureBillDetailViewList",
                        // futureBillDetailViewList);
                        // map.put("billViewList", billViewList);
                        // map.put("billDetailViewList", billDetailViewList);
                        redisCache(map, RedisStatusEnum.QUERY, "");

                    }
                    return new JsonResultView().setObject(map);
                } catch (Exception e) {
                    logger.error("用户手机号【" + creditBillBean.getPhoneNo() + "】查询账单详情异常:{}", e);
                }
                return new JsonResultView().setObject(new JSONObject());
            }

        };
        return new WebAsyncTask<JsonResultView>(callable);
    }

    /**
     * @Title: findBillList
     * @Description:查询账单列表
     * @param cardId
     * @return WebAsyncTask<JsonResultView> 返回类型 @throws
     */
    @ApiOperation(value = "查询账单列表", httpMethod = HttpGet.METHOD_NAME, notes = "查询账单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "cardId", dataType = "String", required = true, value = "信用卡id", defaultValue = "2860"),
            @ApiImplicitParam(paramType = "query", name = "customerNo", dataType = "String", required = true, value = "商户编号", defaultValue = "123456789"),
            @ApiImplicitParam(paramType = "query", name = "channel", dataType = "String", required = true, value = "渠道", defaultValue = "1001"),
            @ApiImplicitParam(paramType = "query", name = "phoneNo", dataType = "String", required = true, value = "商户手机号", defaultValue = "123456789") })
    @RequestMapping(value = "/findBillList", method = RequestMethod.GET)
    @SuppressWarnings("rawtypes")
    @RedisCacheInterceptor
    public WebAsyncTask<JsonResultView> findBillList(@BillResolverInterceptor @ApiIgnore CreditBillBean creditBillBean,
            HttpServletRequest req) {
        Callable<JsonResultView> callable = new Callable<JsonResultView>() {
            @Override
            @SuppressWarnings({ "unchecked" })
            public JsonResultView call() throws Exception {

                try {
                    logger.info("用户手机号【" + creditBillBean.getPhoneNo() + "】查询账单列表请求参数:{}", creditBillBean);
                    if (req.getAttribute("cacheValue") != null) {
                        JSONObject jsonObject = JSONObject.parseObject(req.getAttribute("cacheValue").toString());
                        logger.info("用户手机号【" + creditBillBean.getPhoneNo() + "】查询账单列表返回缓存数据:{}", jsonObject);
                        return new JsonResultView<>().setObject(jsonObject);
                    }

                    Map<String, Object> map = getRequestMap(creditBillBean, new CreditCard());

                    redisCache(map, RedisStatusEnum.QUERY, "");

                    return new JsonResultView().setObject(map);
                } catch (Exception e) {
                    logger.error("用户手机号【" + creditBillBean.getPhoneNo() + "】查询账单列表异常:{}", e);
                }
                return new JsonResultView().setObject(new JSONObject());
            }
        };
        return new WebAsyncTask<JsonResultView>(callable);
    }

    @ApiOperation(value = "查询邮箱的刷新状态", httpMethod = HttpGet.METHOD_NAME, notes = "查询邮箱的刷新状态")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "customerNo", dataType = "String", required = true, value = "商户编号", defaultValue = "123456789"),
            @ApiImplicitParam(paramType = "query", name = "channel", dataType = "String", required = true, value = "渠道", defaultValue = "1001"),
            @ApiImplicitParam(paramType = "query", name = "phoneNo", dataType = "String", required = true, value = "商户手机号", defaultValue = "123456789") })
    @RequestMapping(value = "/findRefreshStatus", method = RequestMethod.GET)
    @SuppressWarnings("rawtypes")
    public WebAsyncTask<JsonResultView> findRefreshStatus(
            @BillResolverInterceptor @ApiIgnore CreditBillBean creditBillBean) {
        Callable<JsonResultView> callable = new Callable<JsonResultView>() {
            @SuppressWarnings("unchecked")
            @Override
            public JsonResultView call() throws Exception {

                try {
                    logger.info("用户手机号【" + creditBillBean.getPhoneNo() + "】查询邮箱刷新状态请求参数:{}", creditBillBean);
                    List<CreditEmail> emailList = creditBillService.findEmailByUser(creditBillBean.getUserId());
                    logger.info("用户手机号【" + creditBillBean.getPhoneNo() + "】查询邮箱刷新状态emailList============={}",
                            JSONObject.toJSONString(emailList));
                    int billNumberSum = 0;
                    int analyzedNumSum = 0;

                    List<String> billNumberList = new ArrayList<String>();
                    List<String> analyzedNumList = new ArrayList<String>();
                    // 构建查询的list
                    for (CreditEmail email : emailList) {
                        billNumberList.add(
                                Constants.REDIS_ANALYSIS_STATUS + email.getEmail() + "_" + creditBillBean.getUserId());
                        analyzedNumList.add(
                                Constants.REDIS_ANALYZED_STATUS + email.getEmail() + "_" + creditBillBean.getUserId());
                    }
                    logger.info("用户手机号【" + creditBillBean.getPhoneNo() + "】查询邮箱刷新状态billNumberList============={}",
                            JSONObject.toJSONString(billNumberList));
                    logger.info("用户手机号【" + creditBillBean.getPhoneNo() + "】查询邮箱刷新状态analyzedNumList============={}",
                            JSONObject.toJSONString(analyzedNumList));
                    // 获取下载数量
                    billNumberList = JedisUtil.multiGetString(billNumberList);
                    // 获取解析数量
                    analyzedNumList = JedisUtil.multiGetString(analyzedNumList);
                    // 累加下载数量
                    for (String value : billNumberList) {
                        if (StringUtils.hasText(value)) {
                            billNumberSum = billNumberSum + Integer.parseInt(value);
                        }
                    }
                    // 累加解析数量
                    for (String value : analyzedNumList) {
                        if (StringUtils.hasText(value)) {
                            analyzedNumSum = analyzedNumSum + Integer.parseInt(value);
                        }
                    }
                    logger.info("用户手机号【" + creditBillBean.getPhoneNo() + "】查询邮箱刷新状态billNumber============={}",
                            billNumberSum);
                    logger.info("用户手机号【" + creditBillBean.getPhoneNo() + "】查询邮箱刷新状态analyzedNumSum============={}",
                            analyzedNumSum);
                    // 构建结果返回
                    JSONObject result = new JSONObject();
                    result.put("billNumber", billNumberSum);
                    result.put("analyzedNum", analyzedNumSum);

                    return new JsonResultView().setObject(result);
                } catch (Exception e) {

                }
                return new JsonResultView().setObject(new JSONObject());
            }
        };
        return new WebAsyncTask<JsonResultView>(callable);
    }

    // @ApiOperation(value = "在邮箱解析的过程总获取解析得到的信用卡", httpMethod =
    // HttpGet.METHOD_NAME, notes = "在邮箱解析的过程总获取解析得到的信用卡")
    // @ApiImplicitParams({
    // @ApiImplicitParam(paramType = "query", name = "email", dataType =
    // "String", required = true, value = "邮箱", defaultValue =
    // "jinjing_0316@outlook.com"),
    // @ApiImplicitParam(paramType = "query", name = "channel", dataType =
    // "String", required = true, value = "渠道", defaultValue = "1001"),
    // @ApiImplicitParam(paramType = "query", name = "phoneNo", dataType =
    // "String", required = true, value = "商户手机号", defaultValue = "123456789")
    // })
    // @RequestMapping(value = "/findCardList", method = RequestMethod.GET)
    // @SuppressWarnings("rawtypes")
    // public WebAsyncTask<JsonResultView> findCardList(
    // @BillResolverInterceptor @ApiIgnore CreditBillBean creditBillBean) {
    // Callable<JsonResultView> callable = new Callable<JsonResultView>() {
    // @Override
    // @SuppressWarnings("unchecked")
    // public JsonResultView call() throws Exception {
    //
    // String cardArray = JedisUtil.getString(Constants.REDIS_CARDS +
    // creditBillBean.getEmail());
    // return new JsonResultView().setObject(cardArray);
    // }
    // };
    // return new WebAsyncTask<JsonResultView>(callable);
    // }

    // /**
    // * @Title: findLoginStatus
    // * @Description:查询邮箱的登陆状态
    // * @param email
    // * @return WebAsyncTask<JsonResultView> 返回类型 @throws
    // */
    // @ApiOperation(value = "获取邮箱的登陆状态0000:登录成功 ,0001:登录失败,0003:登陆中",
    // httpMethod = HttpGet.METHOD_NAME, notes = "获取邮箱的登陆状态0000:登录成功
    // ,0001:登录失败,0003:登陆中")
    // @ApiImplicitParams({
    // @ApiImplicitParam(paramType = "query", name = "email", dataType =
    // "String", required = true, value = "邮箱用户名", defaultValue = "xxx@qq.com")
    // })
    // @RequestMapping(value = "/findLoginStatus", method = RequestMethod.GET)
    // @SuppressWarnings("rawtypes")
    // public WebAsyncTask<JsonResultView> findLoginStatus(
    // @BillResolverInterceptor @ApiIgnore CreditBillBean creditBillBean) {
    // Callable<JsonResultView> callable = new Callable<JsonResultView>() {
    // @Override
    // public JsonResultView call() throws Exception {
    // Object status = JedisUtil.hashGet(Constants.REDIS_LOGGIN_STATUS,
    // creditBillBean.getEmail());
    // return new JsonResultView(String.valueOf(status));
    // }
    // };
    // return new WebAsyncTask<JsonResultView>(callable);
    // }

    @ApiOperation(value = "查询还款记录", httpMethod = HttpGet.METHOD_NAME, notes = "查询还款记录")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "cardId", dataType = "String", required = true, value = "信用卡id", defaultValue = "2881"),
            @ApiImplicitParam(paramType = "query", name = "customerNo", dataType = "String", required = true, value = "商户编号", defaultValue = "123456789"),
            @ApiImplicitParam(paramType = "query", name = "channel", dataType = "String", required = true, value = "渠道", defaultValue = "1001"),
            @ApiImplicitParam(paramType = "query", name = "phoneNo", dataType = "String", required = true, value = "商户手机号", defaultValue = "123456789") })
    @RequestMapping(value = "/findRepaymentDetail", method = RequestMethod.GET)
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @RedisCacheInterceptor
    public WebAsyncTask<JsonResultView> findRepaymentDetail(
            @BillResolverInterceptor @ApiIgnore CreditBillBean creditBillBean, HttpServletRequest req) {

        Callable<JsonResultView> callable = new Callable<JsonResultView>() {
            @Override
            public JsonResultView call() throws Exception {
                try {

                    if (req.getAttribute("cacheValue") != null) {
                        JSONObject jsonObject = JSONObject.parseObject(req.getAttribute("cacheValue").toString());
                        logger.info("用户卡id【" + creditBillBean.getCardId() + "】查询账单列表返回缓存数据:{}", jsonObject);
                        return new JsonResultView<>().setObject(jsonObject);
                    }

                    CreditRepayment creditRepayment = new CreditRepayment();
                    creditRepayment.setCardId(creditBillBean.getCardId());
                    CreditUserInfo creditUserInfo = new CreditUserInfo();
                    creditUserInfo.setId(creditBillBean.getUserId());
                    creditRepayment.setUserInfo(creditUserInfo);
                    logger.info("用户卡id【" + creditBillBean.getCardId() + "】查询还款记录请求参数:{}", creditRepayment);
                    List<JSONObject> list = creditRepaymentService.findRePaymentDetail(creditRepayment);
                    logger.info("用户卡id【" + creditBillBean.getCardId() + "】查询还款记录返回结果:{}", list);
                    redisCache(list, RedisStatusEnum.QUERY, "");
                    return new JsonResultView().setObject(list);
                } catch (Exception e) {
                    logger.error("用户卡id【" + creditBillBean.getCardId() + "】查询还款记录异常:{}", e);
                }
                return new JsonResultView().setObject(new JSONArray());
            }
        };

        return new WebAsyncTask<JsonResultView>(callable);
    }

    @ApiOperation(value = "查询解析结果", httpMethod = HttpGet.METHOD_NAME, notes = "查询解析结果")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "email", dataType = "String", required = true, value = "邮箱用户名", defaultValue = "jinjing_0316@outlook.com"),
            @ApiImplicitParam(paramType = "query", name = "phoneNo", dataType = "String", required = true, value = "手机号", defaultValue = "12345678900"),
            @ApiImplicitParam(paramType = "query", name = "customerNo", dataType = "String", required = true, value = "商户号", defaultValue = "8622632348"),
            @ApiImplicitParam(paramType = "query", name = "channel", dataType = "String", required = true, value = "渠道号", defaultValue = "1001") })
    @RequestMapping(value = "/findUpdateResult", method = RequestMethod.GET)
    @SuppressWarnings("rawtypes")
    public WebAsyncTask<JsonResultView> findUpdateResult(
            @BillResolverInterceptor @ApiIgnore CreditBillBean creditBillBean) {
        Callable<JsonResultView> callable = new Callable<JsonResultView>() {

            @SuppressWarnings("unchecked")
            @Override
            public JsonResultView call() throws Exception {
                CreditCard creditCard = new CreditCard();
                creditCard.setEmail(creditBillBean.getEmail());
                creditCard.setUserId(creditBillBean.getUserId());
                logger.info("用户手机号【" + creditBillBean.getPhoneNo() + "】查询解析结果请求参数:{}", creditCard);
                List<CreditCard> cardList = creditCardService.findCreditCardList(creditCard);
                if (cardList != null) {
                    // AnalysisBuildContext buildContext =
                    // analysisHelper.getAnalysisBuildContext();
                    //
                    // analysisHelper.getAnalysisModelBuilder().buildMulti(cardList,
                    // buildContext);
                    // List<AnalysisResultView> billViewList =
                    // analysisHelper.getViewMapper().map(cardList,
                    // buildContext);
                    //
                    // return new JsonResultView().setObject(billViewList);
                    CardBuildContext buildContext = apiHelper.getBuildContext2();
                    apiHelper.getModelBuilder().buildMulti(cardList, buildContext);
                    List<AnalysisResultView> billViewList = apiHelper.getOverrideViewMapper().map(cardList,
                            buildContext);

                       return new JsonResultView().setObject(billViewList);
                } else {
                    return new JsonResultView().setObject(new JSONArray());
                }

            }
        };
        return new WebAsyncTask<JsonResultView>(callable);
    }

    // @ApiOperation(value = "查询还款记录", httpMethod = HttpGet.METHOD_NAME)
    // @ApiImplicitParams({
    // @ApiImplicitParam(paramType = "query", name = "billId", dataType =
    // "Long", required = true, value = "账单id", defaultValue = "3070"),
    // @ApiImplicitParam(paramType = "query", name = "cardId", dataType =
    // "Long", required = true, value = "信用卡id", defaultValue = "3070"),
    // @ApiImplicitParam(paramType = "query", name = "year", dataType =
    // "String", required = true, value = "还款年", defaultValue = "2017"),
    // @ApiImplicitParam(paramType = "query", name = "month", dataType =
    // "String", required = true, value = "还款月", defaultValue = "12"),
    // @ApiImplicitParam(paramType = "query", name = "phoneNo", dataType =
    // "String", required = true, value = "手机号", defaultValue = "123456789"),
    // @ApiImplicitParam(paramType = "query", name = "customerNo", dataType =
    // "String", required = true, value = "商户号", defaultValue = "123456789"),
    // @ApiImplicitParam(paramType = "query", name = "channel", dataType =
    // "String", required = true, value = "渠道号", defaultValue = "1001") })
    // @RequestMapping("/findRePaymentDetail")
    // @RedisCacheInterceptor
    // public
    // WebAsyncTask<JsonResultView<?>>
    // findRePaymentDetail(@BillResolverInterceptor @ApiIgnore CreditBillBean
    // creditBillBean,
    // HttpServletRequest req) {
    // Callable<JsonResultView<?>> callable = new Callable<JsonResultView<?>>()
    // {
    //
    // @SuppressWarnings("unchecked")
    // @Override
    // public JsonResultView<?> call() throws Exception {
    // if (req.getAttribute("cacheValue") != null) {
    // JSONObject jsonObject =
    // JSONObject.parseObject(req.getAttribute("cacheValue").toString());
    // return new JsonResultView<>().setObject(jsonObject);
    // }
    //
    // CreditRepayment creditRepayment = new CreditRepayment();
    // CreditBill creditBill = new CreditBill();
    // CreditUserInfo creditUserInfo = new CreditUserInfo();
    //
    // creditUserInfo.setId(creditBillBean.getUserId());
    // creditBill.setId(creditBillBean.getBillId());
    // creditRepayment.setBill(creditBill);
    // creditRepayment.setUserInfo(creditUserInfo);
    // List<CreditRepayment> rePaymentList =
    // creditRepaymentService.findRepaymentList(creditRepayment);
    // CardBuildContext buildContext = apiHelper.getBuildContext();
    // apiHelper.getModelBuilder().buildMulti(rePaymentList, buildContext);
    // List<CreditRePayMentView> rePayMentViewViewList =
    // apiHelper.getViewMapper().map(rePaymentList, buildContext);
    //
    // redisCache(rePayMentViewViewList, RedisStatusEnum.QUERY, "");
    //
    // return new JsonResultView().setObject(rePayMentViewViewList);
    // }
    // };
    //
    // return new WebAsyncTask<JsonResultView<?>>(callable);
    // }

    @ApiOperation(value = "导入邮箱的账单", httpMethod = HttpGet.METHOD_NAME, notes = "导入邮箱的账单")
    @SuppressWarnings("rawtypes")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "customerNo", dataType = "String", required = true, value = "商户编号", defaultValue = "123456789"),
            @ApiImplicitParam(paramType = "query", name = "email", dataType = "String", required = true, value = "邮箱用户名", defaultValue = "xxx@qq.com"),
            @ApiImplicitParam(paramType = "query", name = "password", dataType = "String", required = true, value = "邮箱密码", defaultValue = "123456"),
            @ApiImplicitParam(paramType = "query", name = "phoneNo", dataType = "String", required = true, value = "商户手机号", defaultValue = "123456789"),
            @ApiImplicitParam(paramType = "query", name = "channel", dataType = "String", required = true, value = "渠道标识", defaultValue = "1001") })
    @RequestMapping(value = "/importBill", method = RequestMethod.GET)
    public WebAsyncTask<JsonResultView> importBill(@BillResolverInterceptor @ApiIgnore CreditBillBean creditBillBean) {

        Callable<JsonResultView> callable = new Callable<JsonResultView>() {
            @Override
            public JsonResultView call() throws Exception {

                // 设置初始状态

                // String url =
                // "http://localhost:8080/credit-card/card/findCardList";
                try {
                    String url = billDownloadUrl + "/getEmailBill?email=%s&password=%s&userId=%s";

                    url = String.format(url, creditBillBean.getEmail(), creditBillBean.getPassword(),
                            creditBillBean.getUserId());

                    logger.info("url=================={}", url);
                    JSONObject json = restTemplate.getForEntity(url, JSONObject.class).getBody();
                    JsonResultView view = new JsonResultView();
                    view.setCode(json.getString("code"));
                    view.setMsg(json.getString("msg"));
                    return view;
                } catch (Exception e) {
                    logger.error("手机号【" + creditBillBean.getPhoneNo() + "】登录邮箱失败:{}", e);
                    return new JsonResultView(CodeEnum.FAIL);
                }
                // return json.toJavaObject(JsonResultView.class);
            }
        };
        return new WebAsyncTask<JsonResultView>(callable);
    }

    @ApiOperation(value = "保存其他邮箱", httpMethod = HttpGet.METHOD_NAME, notes = "保存其他邮箱")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "customerNo", dataType = "String", required = true, value = "商户编号", defaultValue = "123456789"),
            @ApiImplicitParam(paramType = "query", name = "channel", dataType = "String", required = true, value = "渠道", defaultValue = "1001"),
            @ApiImplicitParam(paramType = "query", name = "phoneNo", dataType = "String", required = true, value = "商户手机号", defaultValue = "123456789"),
            @ApiImplicitParam(paramType = "query", name = "email", dataType = "String", required = true, value = "邮箱帐号", defaultValue = "xxx@qq.com"),
            @ApiImplicitParam(paramType = "query", name = "password", dataType = "String", required = true, value = "邮箱密码", defaultValue = "12345656"),
            @ApiImplicitParam(paramType = "query", name = "server", dataType = "String", required = true, value = "服务器", defaultValue = "pop.example.com"),
            @ApiImplicitParam(paramType = "query", name = "port", dataType = "String", required = true, value = "端口", defaultValue = "110"),
            @ApiImplicitParam(paramType = "query", name = "ssl", dataType = "String", required = true, value = "0 不需要 1 需要", defaultValue = "0") })
    @RequestMapping(value = "/importOtherEmail", method = RequestMethod.GET)
    @SuppressWarnings("rawtypes")
    public WebAsyncTask<JsonResultView> importOtherEmail(
            @BillResolverInterceptor @ApiIgnore CreditBillBean creditBillBean,
            @ApiIgnore CreditUserEmailRelation relation) {
        Callable<JsonResultView> callable = new Callable<JsonResultView>() {

            @Override
            public JsonResultView call() throws Exception {

                // 设置初始状态

                // String url =
                // "http://localhost:8080/credit-card/card/findCardList";
                String url = billDownloadUrl
                        + "/getOtherEmailBill?email=%s&password=%s&userId=%s&server=%s&posr=%d&ssl=%d";

                url = String.format(url, creditBillBean.getEmail(), creditBillBean.getPassword(),
                        creditBillBean.getUserId(), relation.getServer(), relation.getPort(), relation.getSsl());

                logger.info("url=================={}", url);
                JSONObject json = restTemplate.getForEntity(url, JSONObject.class).getBody();
                JsonResultView view = new JsonResultView();
                view.setCode(json.getString("code"));
                view.setMsg(json.getString("msg"));
                return view;
                // return json.toJavaObject(JsonResultView.class);
            }
        };
        return new WebAsyncTask<JsonResultView>(callable);
    }

    @ApiOperation(value = "刷新信用卡账单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "phoneNo", dataType = "String", required = true, value = "手机号", defaultValue = "123456789"),
            @ApiImplicitParam(paramType = "query", name = "customerNo", dataType = "String", required = true, value = "商户号", defaultValue = "123456789"),
            @ApiImplicitParam(paramType = "query", name = "channel", dataType = "String", required = true, value = "渠道号", defaultValue = "1001") })
    @RequestMapping(value = "/refreshCreditBill", method = RequestMethod.GET)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public WebAsyncTask<JsonResultView<?>> refreshCreditBill(
            @BillResolverInterceptor @ApiIgnore CreditBillBean creditBillBean) {
        Callable<JsonResultView<?>> callable = new Callable<JsonResultView<?>>() {

            @Override
            public JsonResultView<?> call() throws Exception {
                // List<CreditEmail> emailList =
                // creditBillService.findEmailByUser(creditBillBean.getUserId());

                String url = billDownloadUrl + "/updateBill?userId=" + creditBillBean.getUserId();
                JSONObject result = restTemplate.getForEntity(url, JSONObject.class).getBody();
                // 在缓存中增加刷新时间
                String key = String.format(Constants.REDIS_REFRESH_STATUS, creditBillBean.getUserId());
                return new JsonResultView().setObject(result);
                // return JSONObject.toJavaObject(result, JsonResultView.class);
            }
        };
        return new WebAsyncTask<JsonResultView<?>>(callable);
    }

    @ApiOperation(value = "再次导入邮箱的账单", httpMethod = HttpGet.METHOD_NAME, notes = "再次导入邮箱的账单")
    @SuppressWarnings("rawtypes")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "customerNo", dataType = "String", required = true, value = "商户编号", defaultValue = "123456789"),
            @ApiImplicitParam(paramType = "query", name = "emailId", dataType = "String", required = true, value = "邮箱id", defaultValue = "xxx@qq.com"),
            @ApiImplicitParam(paramType = "query", name = "phoneNo", dataType = "String", required = true, value = "商户手机号", defaultValue = "123456"),
            @ApiImplicitParam(paramType = "query", name = "channel", dataType = "String", required = true, value = "渠道标识", defaultValue = "1") })
    @RequestMapping(value = "/repeatImportBill", method = RequestMethod.GET)
    public WebAsyncTask<JsonResultView> repeatImportBill(
            @BillResolverInterceptor @ApiIgnore CreditBillBean creditBillBean) {

        Callable<JsonResultView> callable = new Callable<JsonResultView>() {
            @Override
            public JsonResultView call() throws Exception {

                // 设置初始状态

                // String url =
                // "http://localhost:8080/credit-card/card/findCardList";
                String url = billDownloadUrl + "/getEmailBill?email=%s&password=%s&userId=%s";

                url = String.format(url, creditBillBean.getEmail(), creditBillBean.getPassword(),
                        creditBillBean.getUserId());

                logger.info("url=================={}", url);
                JSONObject json = restTemplate.getForEntity(url, JSONObject.class).getBody();

                return json.toJavaObject(JsonResultView.class);
            }
        };
        return new WebAsyncTask<JsonResultView>(callable);
    }

    private String getLastUpdateTime(Long userId) throws Exception {
        // 根据用户查询邮件
        List<CreditEmail> emailList = creditUserEmailRelationService.findEmailByUser(userId);

        List<String> emailKeys = new ArrayList<String>();
        emailList.forEach(email -> {
            String key = Constants.MAIL_DOWANLOD_JOB_CONTENT.concat(String.valueOf(email.getId()));
            emailKeys.add(key);
        });
        List<String> emailContent = JedisUtil.getString(emailKeys);
        long minLastJobTimestamp = System.currentTimeMillis();
        long current = minLastJobTimestamp;
        for (int i = 0; i < emailContent.size(); i++) {
            String emailJson = emailContent.get(i);
            JSONObject email = JSONObject.parseObject(emailJson);
            if (email == null) {
                return "1分钟前更新";
            } else {
                String key = String.format(Constants.REDIS_REFRESH_STATUS, String.valueOf(userId));
                String time = JedisUtil.getString(key);
                if (StringUtils.hasText(time)) {
                    minLastJobTimestamp = Long.parseLong(time);
                }

                long lastJobTimestamp = email.getLong("lastJobTimestamp");
                if (minLastJobTimestamp < lastJobTimestamp) {
                    minLastJobTimestamp = lastJobTimestamp;
                }
            }

        }
        long difference = current - minLastJobTimestamp;
        if (difference >= 180L * 24L * 3600L * 1000L) {
            return "半年前更新";
        } else if (difference >= 24L * 3600L * 1000L) {
            return (difference / (24L * 3600L * 1000L)) + "天前更新";
        } else if (difference >= 30L * 24L * 3600L * 1000L) {
            return (difference / (30L * 24L * 3600L * 1000L)) + "月前更新";
        } else if (difference >= 3600L * 1000L) {
            // 小时
            return (difference / (3600L * 1000L)) + "小时前更新";
        } else if (difference > 60L * 1000L) {

            // 分钟
            return (difference / (60L * 1000L)) + "分钟前更新";
        }
        return "1分钟前更新";
    }

    /**
     * @Title: getNextBillDays
     * @Description: 还有n天出账单
     * @param
     * @return
     */
    private int getNextBillDays(CreditCard creditCard, String futureBillCycle) {
        int nextBillDays = 0;
        LocalDate today = LocalDate.now();

        int nowMonth = Integer.valueOf(futureBillCycle.substring(6, 8));

        if (nowMonth == today.getMonthValue()) {
            nextBillDays = Integer.valueOf(creditCard.getBillDay()) - today.getDayOfMonth();
        } else {
            LocalDate lastDay = today.with(TemporalAdjusters.lastDayOfMonth());
            Period p = Period.between(today, lastDay);
            logger.info("还有多少天出账单：{},{}", p.getDays(), creditCard.getBillDay());
            nextBillDays = p.getDays() + Integer.valueOf(creditCard.getBillDay());
        }

        return nextBillDays;
    }

    /**
     * @Title: getRequestMap
     * @Description: 构建账单列表，未出账单Map
     * @param
     * @return
     */
    @SuppressWarnings("unused")
    private Map<String, Object> getRequestMap(CreditBillBean creditBillBean, CreditCard creditCard) {
        // TODO
        try {
            String futureBillCycle = "";
            int sumAmount = 0;
            int nextBillDays = 0;
            LocalDate localDate = null;
            CreditCard card = new CreditCard();
            CreditBill creditBill = new CreditBill();
            Map<String, Object> map = new HashMap<String, Object>();
            Map<String, Object> futureBillMap = new HashMap<String, Object>();
            List<CreditBillView> billViewList = new ArrayList<CreditBillView>();
            List<CreditBillView> futureBillDetailViewList = new ArrayList<CreditBillView>();
            List<CreditBillDetailView> billDetailViewList = new ArrayList<CreditBillDetailView>();

            card.setId(creditBillBean.getCardId());
            card.setUserId(creditBillBean.getUserId());
            creditBill.setCard(card);
            creditBill.setUserId(creditBillBean.getUserId());
            List<CreditBill> billList = creditBillService.findCreditBillList(creditBill);
            logger.info("用户手机号【" + creditBillBean.getPhoneNo() + "】查询账单列表结果:{}", billList);
            if (CollectionUtils.isNotEmpty(billList)) {

                CardBuildContext buildContext = apiHelper.getBuildContext();
                apiHelper.getModelBuilder().buildMulti(billList, buildContext);
                billViewList = apiHelper.getViewMapper().map(billList, buildContext);


                Long billId = billList.get(0).getId();
                String year = billList.get(0).getYear();
                String method = billList.get(0).getMonth();
                logger.info(
                        "用户手机号【" + creditBillBean.getPhoneNo() + "】查询最新一期账单明细请求参数:billId:{},userId:{},year_month:{}",
                        billId, creditBillBean.getUserId(), year + "_" + method);
                List<CreditBillDetail> billDetails = creditBillDetailService.findBillDetailList(billId,
                        creditBillBean.getUserId(), year + "_" + method);
                logger.info("用户手机号【" + creditBillBean.getPhoneNo() + "】查询最新一期账单明细结果:{}", billDetails);
                if (CollectionUtils.isNotEmpty(billDetails)) {
                    apiHelper.getModelBuilder().buildSingle(billDetails, buildContext);
                    billDetailViewList = apiHelper.getViewMapper().map(billDetails, buildContext);
                }

                // 未出账单总金额
                sumAmount = creditBillDetailService.findFutureBillAmountCount(creditBillBean.getCardId(),
                        creditBillBean.getUserId());

                // 未出账单明细
                List<CreditBillDetail> futureBillDetailList = creditBillDetailService
                        .findFutureBillDetail(creditBillBean.getCardId(), creditBillBean.getUserId());
                if (CollectionUtils.isNotEmpty(futureBillDetailList)) {
                    buildContext = apiHelper.getBuildContext();
                    apiHelper.getModelBuilder().buildMulti(futureBillDetailList, buildContext);
                    futureBillDetailViewList = apiHelper.getViewMapper().map(futureBillDetailList, buildContext);
                }
            }
            // if (creditCard == null) {
            creditCard = creditCardService.findCreditCard(card);
            // }
            // 还有n天出账单
            // String billDate = billList.get(0).getYear() + "-" +
            // billList.get(0).getMonth() + "-"
            // + billList.get(0).getCard().getBillDay();

            // futureBillCycle = DateUtil.getFutureBillCycle(billDate);
            // String date = billList.get(0).getYear() + "-" +
            // billList.get(0).getMonth() + "-"
            // + billList.get(0).getCard().getBillDay();
            localDate = LocalDate.now();
            if (creditCard != null) {
                String billDate = DateUtil.formatDate3(creditCard.getEndDate());
                String date = billDate;
                futureBillCycle = DateUtil.getFutureBillCycle(creditCard.getEndDate());
                // 还有n天出账单
                nextBillDays = getNextBillDays(creditCard, futureBillCycle);
                if (creditCard.getSource() == 0) { // 邮箱导入
                    if (creditCard.getBank() != null) {

                        localDate = DateUtil.getDueDate(Integer.valueOf(creditCard.getBillDay()),
                                Integer.valueOf(creditCard.getDueDay()), LocalDate.parse(date).plusMonths(1),
                                creditCard.getBank().getRepaymentCycle());
                    }
                } else { // 手工导入
                    if (creditCard.getUserCardRelation() != null) {
                        int dueType = creditCard.getUserCardRelation().getDueType();
                        int billDay = creditCard.getUserCardRelation().getBillDay();
                        int dueDay = creditCard.getUserCardRelation().getDueDay();
                        localDate = DateUtil.getDueDate(billDay, dueDay, LocalDate.parse(date).plusMonths(1),
                                dueType == 0 ? 0 : dueDay);
                    }
                }

                if (StringUtils.hasText(futureBillCycle)) {
                    String billDay = futureBillCycle.substring(6, 8) + "月" + creditCard.getBillDay() + "日";

                    futureBillMap.put("futureBillDay", billDay);
                    futureBillMap.put("futureSumAmount", AmountUtil.amountFormat(new BigDecimal(sumAmount)));
                }

            }

            futureBillMap.put("nextBillDays", nextBillDays);
            futureBillMap.put("futureBillCycle", futureBillCycle);

            String futureDueDay = "";
            if (futureDueDay != null) {
                futureDueDay = DateFormatUtils.format(DateUtil.localDateToDate(localDate), "MM月dd日");
                futureBillMap.put("futureDueDay", futureDueDay);
            } else {
                futureBillMap.put("futureDueDay", futureDueDay);
            }

            map.put("futureBillView", futureBillMap);
            map.put("billViewList", billViewList);
            map.put("futureBillDetailViewList", futureBillDetailViewList);
            map.put("billDetailViewList", billDetailViewList);
            return map;
        } catch (Exception e) {
            logger.error("用户手机号【" + creditBillBean.getPhoneNo() + "】获取账单内容异常:{}", e);
        }
        return null;
    }

}
