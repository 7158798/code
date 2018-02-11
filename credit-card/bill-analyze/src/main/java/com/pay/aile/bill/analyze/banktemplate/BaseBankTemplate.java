package com.pay.aile.bill.analyze.banktemplate;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pay.aile.bill.analyze.BankMailAnalyzerTemplate;
import com.pay.aile.bill.analyze.MailContentExtractor;
import com.pay.aile.bill.config.TemplateCache;
import com.pay.aile.bill.contant.Constant;
import com.pay.aile.bill.entity.CreditBill;
import com.pay.aile.bill.entity.CreditBillDetail;
import com.pay.aile.bill.entity.CreditBillDetailRelation;
import com.pay.aile.bill.entity.CreditCard;
import com.pay.aile.bill.entity.CreditTemplate;
import com.pay.aile.bill.entity.CreditUserBillRelation;
import com.pay.aile.bill.entity.CreditUserCardRelation;
import com.pay.aile.bill.entity.CreditUserEmailRelation;
import com.pay.aile.bill.enums.CardStatus;
import com.pay.aile.bill.enums.CardTypeEnum;
import com.pay.aile.bill.exception.AnalyzeBillException;
import com.pay.aile.bill.mapper.CreditCardMapper;
import com.pay.aile.bill.mapper.CreditUserEmailRelationMapper;
import com.pay.aile.bill.model.AnalyzeParamsModel;
import com.pay.aile.bill.model.AnalyzeResult;
import com.pay.aile.bill.service.CreditBillDetailRelationService;
import com.pay.aile.bill.service.CreditBillDetailService;
import com.pay.aile.bill.service.CreditBillService;
import com.pay.aile.bill.service.CreditCardService;
import com.pay.aile.bill.service.CreditTemplateService;
import com.pay.aile.bill.service.CreditUserBillRelationService;
import com.pay.aile.bill.service.CreditUserCardRelationService;
import com.pay.aile.bill.utils.DateUtil;
import com.pay.aile.bill.utils.JedisClusterUtils;
import com.pay.aile.bill.utils.PatternMatcherUtil;

/**
 * @author Charlie
 * @description 卡种解析基础模板 可解决多账户统一还款的账单解析 可解决多账户分开还款的账单部分内容的解析
 *              多账户统一还款:应还金额等合计在一起,消费明细可能有多张卡的明细,从明细中选取消费数量最多的卡作为此账户的卡号保存
 *              一封邮件只能解析一个卡号、一个账单 多账户分开还款:应还款额、额度等是分开显示的，即一个账单中可以抓抓取多个卡号和账单
 *              1.消费明细不能区分是属于哪张卡的,那么所有的消费明细,属于所有的卡和账单
 *              2.消费明细能区分属于哪张卡,那么要将消费明细与其所属卡关联
 */
public abstract class BaseBankTemplate implements BankMailAnalyzerTemplate, InitializingBean {

    protected static final String DEFAULT_NUMBERS = "11111";
    private static final int ONE_HOUR = 60 * 60;
    @Resource(name = "textExtractor")
    protected MailContentExtractor extractor;

    /**
     * 统计每一种模板的调用次数 用于不同卡种之间的排序,调用次数高的排位靠前
     */
    private int count;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 信用卡类型 由子类去初始化自己是什么信用卡类型
     */
    protected CardTypeEnum cardType;

    @Resource
    protected CreditBillDetailService creditBillDetailService;

    @Resource
    protected CreditBillService creditBillService;

    @Resource
    protected CreditCardService creditCardService;

    @Resource
    protected CreditBillDetailRelationService creditBillDetailRelationService;
    @Resource
    protected CreditUserEmailRelationMapper userEmailRelationMapper;
    @Resource
    protected CreditUserBillRelationService creditUserBillRelationService;
    @Resource
    protected CreditUserCardRelationService creditUserCardRelationService;
    @Autowired
    private CreditCardMapper creditCardMapper;

    /**
     * 存放明细规则的map
     */
    protected Map<Integer, String> detailMap = new HashMap<Integer, String>();

    /**
     * 模板解析邮件时需要的关键字及对应的规则 key:到期还款日/应还款金额.eg value:规则 根据银行和信用卡类型,从缓存中初始化
     */
    protected CreditTemplate rules;

    @Autowired
    protected CreditTemplateService creditTemplateService;

    /**
     * 默认的分隔符-空格
     */
    protected String defaultSplitSign = " ";

    /**
     * 默认的对账单明细中进行去空格等格式化处理的标签
     */
    protected String defaultExtractTag = "td";

    @Override
    public void afterPropertiesSet() throws Exception {
        setCardType();
    }

    @Override
    public void analyze(AnalyzeParamsModel apm) throws AnalyzeBillException {
        count++;
        apm.setCardCode(cardType.getCardCode());
        initRules();
        if (rules != null) {
            apm.setCardtypeId(rules.getCardtypeId());
        }
        initDetail();
        extractBillContent(apm);
        beforeAnalyze(apm);
        analyzeInternal(apm);
        checkCardAndBill(apm);
        afterAnalyze(apm);
    }

    /**
     * 用于不同卡种之间的排序,调用次数高的排位靠前
     */
    @Override
    public int compareTo(BankMailAnalyzerTemplate o) {
        if (o == null) {
            return 1;
        }
        BaseBankTemplate other = (BaseBankTemplate) o;
        int otherCount = other.count;
        return count > otherCount ? 1 : -1;
    }

    public CardTypeEnum getCardType() {
        return cardType;
    }

    @Override
    public void handleResult(AnalyzeParamsModel apm) {
        handleResultInternal(apm);
    }

    /**
     *
     * @Title: getSaveBillYearAndMonth
     * @Description: 获取最早保存的年月
     *
     * @return String 返回类型 @throws
     */
    private String getSaveBillYearAndMonth() {
        LocalDate now = LocalDate.now().plusMonths(-7);

        return now.getYear() + "" + now.getMonthValue();

    }

    protected void addDueBillAmount(CreditCard card, CreditBill bill, AnalyzeParamsModel apm) {
        if (bill != null && (bill.getEndDate() != null || bill.getBeginDate() != null)) {
            if (bill.getBeginDate() == null) {
                bill.setBeginDate(DateUtil
                        .localDateToDate(DateUtil.dateToLocalDate(bill.getEndDate()).plusMonths(-1).plusDays(1)));
            }
            if (bill.getEndDate() == null) {
                bill.setEndDate(DateUtil
                        .localDateToDate(DateUtil.dateToLocalDate(bill.getBeginDate()).plusMonths(1).plusDays(-1)));
            }
            // 当前日期
            LocalDate localToday = LocalDate.now();
            // 获取到的账单的账单日
            LocalDate billLocalBillDate = DateUtil.dateToLocalDate(card.getEndDate());
            // // 判断账单日、还款日、还款金额不能为空
            logger.info("addDueBillAmount=======CreditBill=========={}", JSONObject.toJSONString(bill));
            if (StringUtils.isEmpty(card.getBillDay()) || bill.getDueDate() == null
                    || bill.getCurrentAmount() == null) {
                logger.info("addDueBillAmount 账单日、还款日、账单金额为空 cardNo/email:{}/{}", card.getNumbers(), apm.getEmail());
                return;
            }

            // 非本月还款周期即上月账单，判断是否到本月账单日，没到本月账单日则写入上月数据，到本月账单日则不写数据等待抓取本月账单
            logger.info("addDueBillAmount================={}", JSONObject.toJSONString(card));
            int billDay = Integer.parseInt(card.getBillDay());
            // 本月账单日
            LocalDate localBillDate = LocalDate.of(localToday.getYear(), localToday.getMonthValue(), billDay);

            LocalDate beginDate = localBillDate.plusMonths(-1).plusDays(1);
            LocalDate endDate = localBillDate;

            // 当前日期未过账单日,说明本月还未出账单,使用上一个账单周期
            if (billDay > localToday.getDayOfMonth()) {
                beginDate = beginDate.plusMonths(-1);
                endDate = endDate.plusMonths(-1);
            }
            card.setBeginDate(DateUtil.localDateToDate(beginDate));
            card.setEndDate(DateUtil.localDateToDate(endDate));
            LocalDate localDueDate = getThisDueDate(card, bill, apm);
            card.setDueDate(DateUtil.localDateToDate(localDueDate));
            card.setDueDay(String.valueOf(localDueDate.getDayOfMonth()));

            // 本期账单
            // if (!billLocalBillDate.isBefore(localBillDate)) {// 账单日期在本月账单日期之前
            // if (bill.getBeginDate().equals(card.getBeginDate()) &&
            // bill.getEndDate().equals(card.getEndDate())) {
            // 只判断账单日
            if (bill.getEndDate().equals(card.getEndDate())) {
                // card.setBillAmount(new BigDecimal(-1));
                // card.setMinimum(new BigDecimal(-1));
                logger.info("addDueBillAmount 已到本月账单日 cardNo/email:{}/{}", card.getNumbers(), apm.getEmail());
                card.setBillAmount(bill.getCurrentAmount());
                card.setMinimum(bill.getMinimum());
                // 是本期账单的还款日显示
                if (bill.getDueDate() != null) {
                    card.setDueDate(bill.getDueDate());
                    card.setDueDay(String.valueOf(DateUtil.dateToLocalDate(bill.getDueDate()).getDayOfMonth()));
                    LocalDate now = LocalDate.now();
                    LocalDate due = DateUtil.dateToLocalDate(bill.getDueDate());
                    if (due.plusDays(3).isBefore(now)) {
                        card.setBillAmount(BigDecimal.ZERO);
                        card.setMinimum(BigDecimal.ZERO);
                    }
                }

            }
            bill.setCardDueDate(card.getDueDate());
            bill.setCardBeginDate(card.getBeginDate());
            bill.setCardEndDate(card.getEndDate());
            bill.setCardDueDay(card.getDueDay());
            logger.info("addDueBillAmount end 非本期非上期账单 cardNo/email:{}/{}", card.getNumbers(), apm.getEmail());
        }

    }

    /**
     * @param apm
     * @throws AnalyzeBillException
     */
    protected void afterAnalyze(AnalyzeParamsModel apm) throws AnalyzeBillException {

    }

    protected void analyzeBillDate(List<CreditCard> cardList, List<CreditBill> billList, String content,
            AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getBillDay())) {
            String billDay = getValueByPattern("billDay", content, rules.getBillDay(), apm, " ");
            if (StringUtils.hasText(billDay)) {
                billDay = billDay.replaceAll("年", "").replaceAll("月", "").replaceAll("日", "").replaceAll("\\s+", "");
                final String finalBillDay = billDay.substring(billDay.length() - 2);
                cardList.forEach(card -> {
                    card.setBillDay(finalBillDay);
                });
                billList.forEach(bill -> {
                    bill.setBillDay(finalBillDay);
                });
            }
        }
    }

    /**
     * @Title: analyzeDueDate
     * @Description: 解析参数
     * @param card
     * @param content
     * @param apm
     * @return void 返回类型 @throws
     */
    protected void analyzeCardholder(List<CreditCard> cardList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getCardholder())) {
            String cardholder = getValueByPattern("cardholder", content, rules.getCardholder(), apm, "");
            final String finalCardholder = cardholder.replaceAll("亲爱的|尊敬的|先生|女士|您好|\\s+", "");
            cardList.forEach(card -> {
                card.setCardholder(finalCardholder);
            });
        }
    }

    protected void analyzeCardNo(List<CreditCard> cardList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getCardNumbers())) {
            Exception error = null;
            try {
                Integer.valueOf(rules.getCardNumbers());
            } catch (Exception e) {
                error = e;
            }
            if (error != null) {
                List<String> cardNos = getValueListByPattern("cardNumbers", content, rules.getCardNumbers(), "");
                if (!cardNos.isEmpty()) {
                    for (int i = 0; i < cardNos.size(); i++) {
                        String cardNo = cardNos.get(i);
                        cardNo = StringUtils
                                .collectionToDelimitedString(PatternMatcherUtil.getMatcher("\\d+|\\*+|-", cardNo), "");

                        CreditCard card = new CreditCard();
                        card.setNumbers(cardNo);
                        if (!cardList.contains(card)) {
                            cardList.add(card);
                        }
                    }
                }
            }
        }
    }

    /**
     * @Title: analyzeCash
     * @Description:预借现金
     * @param bill
     * @param content
     * @param apm
     * @return void 返回类型 @throws
     */
    protected void analyzeCash(List<CreditBill> billList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getCash())) {
            List<String> cash = getValueListByPattern("cash", content, rules.getCash(), " ");
            cash = PatternMatcherUtil.getMatcher(Constant.pattern_amount, cash);
            if (!cash.isEmpty()) {
                if (cash.size() == billList.size()) {
                    for (int i = 0; i < cash.size(); i++) {
                        CreditBill bill = billList.get(i);
                        bill.setCash(new BigDecimal(cash.get(i)));
                    }
                } else {
                    for (CreditBill bill : billList) {
                        bill.setCash(new BigDecimal(cash.get(0)));
                    }
                }
            }
        }
    }

    /**
     * @Title: analyzeCredits
     * @Description: 信用额度
     * @param bill
     * @param content
     * @param apm
     * @return void 返回类型 @throws
     */
    protected void analyzeCredits(List<CreditBill> billList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getCredits())) {
            List<String> credits = getValueListByPattern("credits", content, rules.getCredits(), " ");
            credits = PatternMatcherUtil.getMatcher(Constant.pattern_amount, credits);
            if (!credits.isEmpty()) {
                if (credits.size() == billList.size()) {
                    for (int i = 0; i < credits.size(); i++) {
                        CreditBill bill = billList.get(i);
                        bill.setCredits(new BigDecimal(credits.get(i)));
                    }
                } else {
                    for (CreditBill bill : billList) {
                        bill.setCredits(new BigDecimal(credits.get(0)));
                    }
                }
            }
        }
        billList.forEach(bill -> {
            if (bill.getCredits() == null) {
                bill.setCredits(new BigDecimal("-1"));
            }
        });
    }

    /**
     * @Title: analyzeCurrentAmount
     * @Description: 应还款额
     * @param bill
     * @param content
     * @param apm
     * @return void 返回类型 @throws
     */
    protected void analyzeCurrentAmount(List<CreditBill> billList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getCurrentAmount())) {
            List<String> currentAmountList = getValueListByPattern("currentAmount", content, rules.getCurrentAmount(),
                    defaultSplitSign);
            currentAmountList = PatternMatcherUtil.getMatcher(Constant.pattern_amount, currentAmountList);
            if (!currentAmountList.isEmpty()) {
                currentAmountList.stream().map(item -> {
                    if (item.startsWith("-")) {
                        return item.replaceAll("-", "");
                    } else {
                        return item;
                    }
                }).forEach(currentAmount -> {
                    CreditBill bill = new CreditBill();
                    bill.setCurrentAmount(new BigDecimal(currentAmount));
                    billList.add(bill);
                });
            }
        }
    }

    protected void analyzeCycle(List<CreditBill> billList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getCycle())) {

            String cycle = getValueByPattern("cycle", content, rules.getCycle(), apm, defaultSplitSign);
            String[] sa = cycle.split("-");
            billList.forEach(bill -> {
                bill.setBeginDate(DateUtil.parseDate(sa[0]));
                bill.setEndDate(DateUtil.parseDate(sa[1]));
            });
        }
    }

    protected void analyzeDetails(List<CreditBillDetail> detailList, String content, AnalyzeParamsModel apm,
            List<CreditCard> cardList) {
        List<String> list = null;
        if (StringUtils.hasText(rules.getDetails())) {
            // 交易明细
            list = PatternMatcherUtil.getMatcher(rules.getDetails(), content);
            if (!list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    String s = list.get(i);
                    detailList.add(setCreditBillDetail(s));
                    setCardNumbers(cardList, s);
                }
            }
        }
    }

    /**
     * @Title: analyzeDueDate
     * @Description: 还款日
     * @param bill
     * @param content
     * @param apm
     * @return void 返回类型 @throws
     */
    protected void analyzeDueDate(List<CreditBill> billList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getDueDate())) {
            String date = getValueByPattern("dueDate", content, rules.getDueDate(), apm, defaultSplitSign);
            billList.forEach(bill -> {
                bill.setDueDate(DateUtil.parseDate(date));
            });

        }
    }

    /**
     * @Description: 积分余额
     * @param card
     * @param content
     * @param apm
     */
    protected void analyzeIntegral(List<CreditCard> cardList, List<CreditBill> billList, String content,
            AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getIntegral())) {
            String integral = getValueByPattern("integral", content, rules.getIntegral(), apm, " ");
            final String finalIntegral = PatternMatcherUtil.getMatcherString("\\d+\\.?\\d*", integral);
            if (StringUtils.hasText(finalIntegral)) {
                cardList.forEach(card -> {
                    card.setIntegral(new BigDecimal(finalIntegral));
                });

                billList.forEach(bill -> {
                    bill.setIntegral(new BigDecimal(finalIntegral));
                });
            }
        }
    }

    protected void analyzeInternal(AnalyzeParamsModel apm) throws AnalyzeBillException {
        logger.info("账单内容：{}", apm.toString());
        String content = apm.getContent();
        AnalyzeResult ar = new AnalyzeResult();
        // ka
        List<CreditCard> cardList = ar.getCardList();
        // 账单
        List<CreditBill> billList = ar.getBillList();

        List<CreditBillDetail> detailList = ar.getDetailList();
        if (rules == null) {
            throw new AnalyzeBillException("账单模板规则未初始化");
        }

        // 本期账单金额
        analyzeCurrentAmount(billList, content, apm);
        // 最低还款额
        analyzeMinimum(billList, content, apm);
        // 卡号
        analyzeCardNo(cardList, content, apm);
        // 账单周期
        analyzeCycle(billList, content, apm);
        // 年月
        analyzeYearMonth(billList, content, apm);
        // 还款日
        analyzeDueDate(billList, content, apm);
        // 信用额度
        analyzeCredits(billList, content, apm);
        // 取取现金额
        analyzeCash(billList, content, apm);
        // 消费明细
        analyzeDetails(detailList, content, apm, cardList);
        // 持卡人
        analyzeCardholder(cardList, content, apm);
        // 账单日
        analyzeBillDate(cardList, billList, content, apm);
        // 积分余额
        analyzeIntegral(cardList, billList, content, apm);
        // 设置卡片
        setCard(cardList, billList, apm);
        // 设置是否包含外币
        setForeignCurrency(billList, apm);
        apm.setResult(ar);
    }

    protected void analyzeMinimum(List<CreditBill> billList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getMinimum())) {
            List<String> minimumList = getValueListByPattern("minimum", content, rules.getMinimum(), defaultSplitSign);
            minimumList = PatternMatcherUtil.getMatcher(Constant.pattern_amount, minimumList);
            if (!minimumList.isEmpty()) {
                minimumList = minimumList.stream().map(item -> {
                    if (item.startsWith("-")) {
                        return item.replaceAll("-", "");
                    } else {
                        return item;
                    }
                }).collect(Collectors.toList());
                for (int i = 0; i < minimumList.size(); i++) {
                    CreditBill bill = null;
                    if (!billList.isEmpty()) {
                        bill = billList.get(i);
                    } else {
                        bill = new CreditBill();
                        billList.add(bill);
                    }
                    bill.setMinimum(new BigDecimal(minimumList.get(i)));
                }
            }
        }
    }

    protected void analyzeYearMonth(List<CreditBill> billList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getYearMonth())) {
            String yearMonth = getValueByPattern("yearMonth", content, rules.getYearMonth(), apm, "");
            yearMonth = StringUtils.collectionToDelimitedString(PatternMatcherUtil.getMatcher("\\d+", yearMonth), "");
            // yearMonth = yearMonth.replaceAll("年|月|-|/", "");
            yearMonth = PatternMatcherUtil.getMatcherString("\\d{6}", yearMonth);
            if (StringUtils.hasText(yearMonth)) {
                String year = yearMonth.substring(0, 4);
                String month = yearMonth.substring(4);
                billList.forEach(bill -> {
                    bill.setYear(year);
                    bill.setMonth(month);
                });
            }
        } else {
            billList.forEach(bill -> {
                if (bill.getEndDate() != null) {
                    bill.setYear(DateUtil.getDateField(bill.getEndDate(), Calendar.YEAR));
                    bill.setMonth(DateUtil.getDateField(bill.getEndDate(), Calendar.MONTH));
                }
            });
        }
    }

    /**
     * @param apm
     */
    protected void beforeAnalyze(AnalyzeParamsModel apm) {
    }

    /**
     * @throws AnalyzeBillException
     * @Title: checkCardAndBill @Description: 检查数据合法性 @param apm @return void
     *         返回类型 @throws
     */
    protected void checkCardAndBill(AnalyzeParamsModel apm) throws AnalyzeBillException {

        if (apm.getResult().getCardList().isEmpty()) {
            throw new AnalyzeBillException("未找到信用卡");
        }
        if (apm.getResult().getBillList().isEmpty()) {
            throw new AnalyzeBillException("未抓取到账单");
        }
        // 检查是否包含卡号和持卡人
        for (CreditCard card : apm.getResult().getCardList()) {
            if (!StringUtils.hasText(card.getNumbers())) {
                throw new AnalyzeBillException("无法获取卡号");
            }
        }

        for (CreditBill bill : apm.getResult().getBillList()) {
            if (bill.getDueDate() == null || bill.getCurrentAmount() == null
                    || !StringUtils.hasText(bill.getBillDay())) {
                throw new AnalyzeBillException("应还款日期、应还款额、账单日不能为空!");
            }
            if (!StringUtils.hasText(bill.getYear()) || !StringUtils.hasText(bill.getMonth())) {
                throw new AnalyzeBillException("未抓取到账单年月!");
            }
            String billYearAndMonth = bill.getYear() + bill.getMonth();
            String yearAndMonth = getSaveBillYearAndMonth();
            if (billYearAndMonth.compareTo(yearAndMonth) == -1) {
                throw new AnalyzeBillException("未抓取到账单年月超过半年，不进行保存!");
            }
        }

    }

    /**
     * @Title: extractBillContent @Description: 初始化需要解析的内容 @param @param apm
     *         参数 @return void 返回类型 @throws
     */
    protected void extractBillContent(AnalyzeParamsModel apm) {
        String content = extractor.extract(apm.getOriginContent(), defaultExtractTag);
        // logger.info("extractBillContent============================={}",
        // content);
        apm.setContent(content);
    }

    /**
     * 信用卡中添加还款日以及账单金额（本期或上期账单） 1、今天在本期还款周期内（本期）则写入本期账单金额
     * 2、今天在上期还款日后，本期账单日前，则写入上期账单，非上期则不写
     *
     * @param card
     * @param bill
     * @param apm
     */
    // private void addDueBillAmount(CreditCard card, CreditBill bill,
    // AnalyzeParamsModel apm) {
    // // 获得今天最小时间
    // Date date = DateUtil.getMinTime(new Date());
    // // 判断账单日、还款日、还款金额不能为空
    // if (StringUtils.isEmpty(card.getBillDay()) || bill.getDueDate() == null
    // || bill.getCurrentAmount() == null) {
    // logger.info("addDueBillAmount 账单日、还款日、还款金额为空 cardNo/email:{}/{}",
    // card.getNumbers(), apm.getEmail());
    // return;
    // }
    // // 判断今天是否过了账单中的还款日期
    // if (date.compareTo(bill.getDueDate()) <= 0) {// 本月还款周期内
    // String dueDay = DateUtil.getDateField(bill.getDueDate(),
    // Calendar.DAY_OF_MONTH);
    // card.setDueDate(bill.getDueDate());
    // card.setDueDay(dueDay);
    // card.setBillAmount(bill.getCurrentAmount());
    // logger.info("addDueBillAmount 本期账单
    // cardNo/email/billAmount/dueDate:{}/{}/{}/{}", card.getNumbers(),
    // apm.getEmail(), bill.getCurrentAmount(),
    // DateUtil.formatDate(bill.getDueDate()));
    // return;
    // }
    // logger.info("addDueBillAmount 非本期账单 cardNo/email:{}/{}",
    // card.getNumbers(), apm.getEmail());
    // // 非本月还款周期即上月账单，判断是否到本月账单日，没到本月账单日则写入上月数据，到本月账单日则不写数据等待抓取本月账单
    // // 获取本月账单日
    // Date billDate = DateUtil.getMonthDay(date,
    // Integer.parseInt(card.getBillDay()));
    // if (date.compareTo(billDate) >= 0) {// 到了本月账单日，则不写本条数据
    // logger.info("addDueBillAmount 已到本月账单日 cardNo/email:{}/{}",
    // card.getNumbers(), apm.getEmail());
    // return;
    // }
    // // 未到本月账单日，写入上月数据
    // // 计算上个月的账单日，如果当前还款日在这个月和上个月账单日之间，则还款日为上个月还款日
    // Date lastBillDate = DateUtil.getLastMonth(billDate);
    // if (DateUtil.isInDateRange(bill.getDueDate(), lastBillDate, billDate)) {
    // String dueDay = DateUtil.getDateField(bill.getDueDate(),
    // Calendar.DAY_OF_MONTH);
    // card.setDueDate(bill.getDueDate());
    // card.setDueDay(dueDay);
    // card.setBillAmount(bill.getCurrentAmount());
    // logger.info("addDueBillAmount 写入上期账单
    // cardNo/email/billAmount/dueDate:{}/{}/{}/{}", card.getNumbers(),
    // apm.getEmail(), bill.getCurrentAmount(),
    // DateUtil.formatDate(bill.getDueDate()));
    // return;
    // }
    // logger.info("addDueBillAmount end 非本期非上期账单 cardNo/email:{}/{}",
    // card.getNumbers(), apm.getEmail());
    // }
    /**
     *
     * @Title: getThisDueDate @Description: 获取本期账单的还款日 @param card @param
     *         bill @param apm @return 参数 @return Date 返回类型 @throws
     */
    protected LocalDate getThisDueDate(CreditCard card, CreditBill bill, AnalyzeParamsModel apm) {
        int repaymentCycle = TemplateCache.bankRepaymentCache.get(apm.getBankCode());
        LocalDate localBillDate = DateUtil.dateToLocalDate(card.getEndDate());
        LocalDate localDueDate = LocalDate.now();
        if (repaymentCycle != 0) {
            localDueDate = localBillDate.plusDays(repaymentCycle);
        } else {
            if (bill.getDueDate() != null) {
                int dueDay = DateUtil.dateToLocalDate(bill.getDueDate()).getDayOfYear();
                int billDay = localBillDate.getDayOfMonth();
                if (dueDay < billDay) {
                    LocalDate nextLocalBillDate = localBillDate.plusMonths(1);
                    localDueDate = LocalDate.of(nextLocalBillDate.getYear(), nextLocalBillDate.getMonth(), dueDay);
                } else {
                    localDueDate = LocalDate.of(localBillDate.getYear(), localBillDate.getMonth(), dueDay);
                }
            }
        }
        return localDueDate;
    }

    protected String getValueByPattern(String key, String content, String ruleValue, AnalyzeParamsModel apm,
            String splitSign) {
        if (StringUtils.hasText(ruleValue)) {

            List<String> list = PatternMatcherUtil.getMatcher(ruleValue, content);
            if (list.isEmpty()) {
                // handleNotMatch(key, rules.getDueDate(), apm);
                return "";
            }
            String result = list.get(0);

            if ("".equals(splitSign)) {
                return result;
            } else {
                String[] sa = result.split(splitSign);
                String value = sa[sa.length - 1];
                return value;
            }

        }
        return "";
    }

    protected List<String> getValueListByPattern(String key, String content, String ruleValue, String splitSign) {
        if (StringUtils.hasText(ruleValue)) {
            List<String> list = PatternMatcherUtil.getMatcher(ruleValue, content);
            if (list.isEmpty()) {
                return list;
            }
            if ("".equals(splitSign)) {
                return list;
            } else {
                return list.stream().map((item) -> {
                    String[] sa = item.split(splitSign);
                    return sa[sa.length - 1];
                }).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    protected void handleNotMatch(String key, String reg, AnalyzeParamsModel apm) throws AnalyzeBillException {
        throw new AnalyzeBillException(String.format("未找到匹配值,bank=%s,cardType=%s,key=%s,reg=%s",
                cardType.getBankCode().getBankCode(), cardType.getCardCode(), key, reg));
    }

    /**
     * @Description 只能解决多账户统一还款类账单
     * @param apm
     * @see 需要参考的类或方法
     * @author chao.wang
     */
    @Transactional
    protected void handleResultInternal(AnalyzeParamsModel apm) {
        Long emailId = apm.getEmailId();

        int year = 0;
        int month = 0;
        List<CreditCard> cardList = apm.getResult().getCardList();
        List<CreditBill> billList = apm.getResult().getBillList();

        List<CreditBillDetail> detailList = apm.getResult().getDetailList();
        CreditCard saveCard = null;
        CreditBill saveBill = null;
        // 根据明细中出现的卡号的数量最多的选出一个卡号进行保存
        // 若没有明细,那么无法抓取到卡号,此账单宣告解析失败
        if (!detailList.isEmpty()) {
            Map<String, Long> groupDetail = detailList.stream()
                    .collect(Collectors.groupingBy(CreditBillDetail::getCardNumbers, Collectors.counting()));
            String cardNumbers = groupDetail.entrySet().stream().max((o1, o2) -> {
                return o1.getValue() > o2.getValue() ? 1 : -1;
            }).get().getKey();
            saveCard = cardList.stream().filter(c -> c.getNumbers().equals(cardNumbers)).findFirst().get();
        } else {
            // 北京银行
            saveCard = cardList.get(0);
        }
        if (billList.size() > 0) {
            saveBill = billList.get(0);
        }
        // 保存或更新卡信息,若已经存在同一银行,相同邮箱的卡记录,说明已经抓取到一个统一还款卡号,则此次抓取的卡号不予记录
        if (saveCard != null) {
            // 多账户统一还款,卡号放到完整卡号中,原始卡号设置为空
            saveCard.setCompleteNumbers(saveCard.getNumbers());
            saveCard.setNumbers(DEFAULT_NUMBERS);
            apm.getResult().setCardList(Arrays.asList(saveCard));// 将cardList设置成为真正要保存的卡,否则前端会将抓取到的所有卡号都显示
        }
        // 查询邮件和用户的关系
        List<CreditUserEmailRelation> emailRelationList = new ArrayList<>();
        if (apm.getIsNew()) {
            CreditUserEmailRelation cuer = new CreditUserEmailRelation();
            cuer.setUserId(apm.getUserId());
            emailRelationList.add(cuer);
        } else {
            emailRelationList = userEmailRelationMapper.findByEmail(emailId);
        }
        // bankId,numbers,cardholder,userId为联合唯一索引
        List<CreditCard> saveCardList = new ArrayList<>();
        for (CreditUserEmailRelation relation : emailRelationList) {
            CreditCard cardItem = new CreditCard();
            BeanUtils.copyProperties(saveCard, cardItem, CreditCard.class);
            cardItem.setUserId(relation.getUserId());
            saveCardList.add(cardItem);
        }
        if (!saveCardList.isEmpty()) {
            saveCardList = creditCardService.saveOrUpateCreditCard(saveCardList);
        }

        logger.debug("saveCardList == {}", saveCardList);
        List<CreditBill> saveBillList = new ArrayList<>();
        if (saveBill != null && saveCardList.size() > 0) {
            saveBill.setEmailId(emailId);
            saveBill.setSentDate(apm.getSentDate());
            saveBill.setFileId(apm.getFileId());
            month = Integer.valueOf(saveBill.getMonth());
            year = Integer.valueOf(saveBill.getYear());
            for (CreditCard card : saveCardList) {
                CreditBill bill = new CreditBill();
                BeanUtils.copyProperties(saveBill, bill, CreditBill.class);
                bill.setCardId(card.getId());
                saveBillList.add(bill);
            }
            boolean repeatBill = false;
            repeatBill = saveBillList(saveBillList, apm);
            logger.debug("repeatBill={},saveBillList == {}", repeatBill, saveBillList);

            if (!repeatBill) {
                // 保存明细
                if (!detailList.isEmpty()) {
                    creditBillDetailService.batchSaveBillDetail(year, month, detailList);
                }

                // 账单和明细的关系
                List<CreditBillDetailRelation> relationList = new ArrayList<CreditBillDetailRelation>();
                for (CreditBillDetail detail : detailList) {
                    saveBillList.forEach(bill -> {
                        CreditBillDetailRelation relation = new CreditBillDetailRelation();
                        if (bill.getId() != null) {

                            relation.setBillId(bill.getId());
                            relation.setBillDetailId(detail.getId());
                            relationList.add(relation);
                        }

                    });
                }
                // 保存账单和明细的关系
                creditBillDetailRelationService.batchSaveBillDetailRelation(year, month, relationList);
                // 保存账单和用户关系
                saveUserBillRelation(saveCardList, saveBillList);
            }
        }
        // 保存账单

        // 保存卡片和用户关系
        saveUserCardRelation(saveCardList, apm);
        // 修改积分
        updateIntegral(saveBillList, apm);
    }

    protected void initDetail() {
        if (rules != null && StringUtils.hasText(rules.getDetails())) {
            if (StringUtils.hasText(rules.getTransactionDate())) {
                try {
                    detailMap.put(Integer.parseInt(rules.getTransactionDate()), "transactionDate");
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
            if (StringUtils.hasText(rules.getBillingDate())) {
                try {
                    detailMap.put(Integer.parseInt(rules.getBillingDate()), "billingDate");
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
            if (StringUtils.hasText(rules.getTransactionDescription())) {
                try {
                    detailMap.put(Integer.parseInt(rules.getTransactionDescription()), "transactionDescription");
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
            if (StringUtils.hasText(rules.getTransactionCurrency())) {
                try {
                    detailMap.put(Integer.parseInt(rules.getTransactionCurrency()), "transactionCurrency");
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
            if (StringUtils.hasText(rules.getTransactionAmount())) {
                try {
                    detailMap.put(Integer.parseInt(rules.getTransactionAmount()), "transactionAmount");
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
            if (StringUtils.hasText(rules.getAccountableAmount())) {
                try {
                    detailMap.put(Integer.parseInt(rules.getAccountableAmount()), "accountableAmount");
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }

            if (StringUtils.hasText(rules.getCardNumbers())) {
                try {
                    detailMap.put(Integer.parseInt(rules.getCardNumbers()), "cardNumbers");
                } catch (Exception e) {

                }
            }
        }
    }

    /**
     * 获取模板对应的关键字
     */
    protected void initRules() {
        // 根据cardCode从缓存中获取对应的规则
        String cardCode = cardType.getCardCode();
        rules = TemplateCache.templateCache.get(cardCode);
        logger.info("cardCod======rules=e====={}==============={}", cardCode, rules);

    }

    /**
     *
     * @Description 保存账单
     * @param saveBillList
     * @see 需要参考的类或方法
     * @author chao.wang
     * @throws AnalyzeBillException
     */
    protected boolean saveBillList(List<CreditBill> saveBillList, AnalyzeParamsModel apm) {
        try {
            creditBillService.saveCreditBill(saveBillList);
        } catch (Exception e) {
            if (e instanceof DuplicateKeyException) {
                // 以卡号,账单年余月为唯一性索引,触发唯一性索引说明这是同一张卡的同一个账单,之前保存过，此次可以不处理,后续的关系也已经处理过
                logger.warn("saveBillList duplicateKey!email={},bankcode={},cardcode={}", apm.getEmail(),
                        apm.getBankCode(), apm.getCardCode());
                return true;
            } else {
                throw new RuntimeException("saveBill error!", e);
            }
        }
        return false;
    }

    /**
     *
     * @Description 保存账单和用户关系
     * @param saveCardList
     * @param saveBillList
     * @see 需要参考的类或方法
     * @author chao.wang
     */
    protected void saveUserBillRelation(List<CreditCard> saveCardList, List<CreditBill> saveBillList) {
        // 账单和用户的关系
        List<CreditUserBillRelation> billRelationList = new ArrayList<CreditUserBillRelation>();
        // 保存账单和用户关系。
        for (int i = 0; i < saveCardList.size(); i++) {
            CreditCard card = saveCardList.get(i);
            CreditBill bill = saveBillList.get(i);
            if (bill.getId() != null) {
                CreditUserBillRelation billRelation = new CreditUserBillRelation();
                billRelation.setBillId(bill.getId());
                billRelation.setUserId(card.getUserId());
                billRelationList.add(billRelation);
            }

        }
        creditUserBillRelationService.batchSave(billRelationList);

    }

    /**
     *
     * @Description 保存卡片和用户的关系
     * @param saveCardList
     * @see 需要参考的类或方法
     * @author chao.wang
     */
    protected void saveUserCardRelation(List<CreditCard> saveCardList, AnalyzeParamsModel apm) {
        // 卡片和用户的关系
        List<CreditUserCardRelation> cardRelationList = new ArrayList<CreditUserCardRelation>();
        // 保存卡片和用户关系

        saveCardList.forEach(card -> {
            CreditUserCardRelation cardRelation = new CreditUserCardRelation();
            cardRelation.setCardId(card.getId());
            cardRelation.setUserId(card.getUserId());
            // 如果是email导入的则status=1 ，如果是定时任务获取到的则status=2
            cardRelation.setStatus(apm.getIsNew() ? CardStatus.AVAILABLE.value : CardStatus.NEW.value);
            // cardRelation.setStatus(CardStatus.AVAILABLE.value);
            cardRelationList.add(cardRelation);
        });
        // 保存
        creditUserCardRelationService.batchSave(cardRelationList);
    }

    /**
     * @Title: setCard
     * @Description: 设置行用卡
     * @param card
     * @param bill
     * @param apm
     * @return void 返回类型 @throws
     */
    protected void setCard(List<CreditCard> cardList, List<CreditBill> billList, AnalyzeParamsModel apm) {
        for (int i = 0; i < cardList.size(); i++) {
            CreditCard card = cardList.get(i);
            CreditBill bill = null;
            if (billList.size() > 0) {
                if (i > billList.size() - 1) {
                    bill = billList.get(billList.size() - 1);
                } else {
                    bill = billList.get(i);
                }
                if (bill.getEndDate() == null) {
                    int year = StringUtils.hasText(bill.getYear()) ? Integer.valueOf(bill.getYear()) : -1;
                    int month = StringUtils.hasText(bill.getMonth()) ? Integer.valueOf(bill.getMonth()) : -1;
                    int day = StringUtils.hasText(card.getBillDay()) ? Integer.valueOf(card.getBillDay()) : -1;
                    if (year > 0 && month > 0 && day >= 0) {
                        bill.setEndDate(DateUtil.getDate(year, month, day));
                    }
                }
                if (!StringUtils.hasText(card.getBillDay()) && bill.getEndDate() != null) {
                    String billDay = DateUtil.getDateField(bill.getEndDate(), Calendar.DAY_OF_MONTH);
                    card.setBillDay(billDay);
                    bill.setBillDay(billDay);
                }
                card.setCash(bill.getCash());
                card.setCredits(bill.getCredits());
                card.setBeginDate(bill.getBeginDate());
                card.setEndDate(bill.getEndDate());
            }
            // 信用卡中添加还款日和账单金额
            addDueBillAmount(card, bill, apm);
            card.setBankId(new Long(apm.getBankId()));
            card.setEmail(apm.getEmail());
            card.setStatus(apm.getIsNew() ? CardStatus.AVAILABLE.value : CardStatus.NEW.value);
            // card.setStatus(CardStatus.AVAILABLE.value);

            if (StringUtils.hasText(card.getNumbers())) {
                card.setNumbers(card.getNumbers().replaceAll("-", ""));
            }
            card.setCompleteNumbers(card.getNumbers());
            if (StringUtils.hasText(card.getNumbers())) {
                String numbers = card.getNumbers();
                if (numbers.length() > 4) {
                    numbers = numbers.substring(numbers.length() - 4);
                    card.setNumbers(numbers);
                }
            }
            if (card.getCredits() != null && card.getCredits().doubleValue() > 0) {
                // 抓取到额度了,则使用抓取到的额度,不允许自行修改额度
                card.setCreditsType(0);// 0表示额度不可编辑
            } else {
                card.setCreditsType(1);// 1标识额度可编辑
            }
        }
    }

    /**
     * @Title: setCardNumbers @Description: 卡号
     * @param card
     * @param number
     * @return void 返回类型 @throws
     */
    protected void setCardNumbers(List<CreditCard> cardList, String number) {
        if (StringUtils.hasText(rules.getCardNumbers())) {
            try {
                int n = Integer.parseInt(rules.getCardNumbers());
                String[] detailArray = number.split(" ");
                String cardNo = detailArray[n];
                CreditCard card = new CreditCard();
                if (StringUtils.hasText(cardNo)) {
                    cardNo = cardNo.replaceAll("-", "");
                }
                card.setNumbers(cardNo);
                if (!cardList.contains(card)) {
                    cardList.add(card);
                }
            } catch (Exception e) {

            }
        }
    }

    /**
     * 设置信用卡类型
     */
    protected void setCardType() {

    }

    protected CreditBillDetail setCreditBillDetail(String detail) {
        CreditBillDetail cbd = new CreditBillDetail();
        String[] detailArray = detail.split(" ");
        for (Integer i = 0; i < detailArray.length; i++) {
            if (detailMap.containsKey(i)) {
                Field field;
                try {
                    field = CreditBillDetail.class.getDeclaredField(detailMap.get(i));
                    if (field.getType() == Date.class) {
                        ReflectionUtils.setField(field, cbd, DateUtil.parseDate(detailArray[i]));
                    } else {
                        ReflectionUtils.setField(field, cbd, detailArray[i]);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }

            } else {
                // /
                setField(cbd, i, detailArray[i]);
            }

        }
        return cbd;

    }

    protected void setField(CreditBillDetail cbd, int index, String value) {

    }

    /**
     * @Description 判断账单中是否包含外币
     * @param billList
     * @param apm
     * @see 需要参考的类或方法
     * @author chao.wang
     */
    protected void setForeignCurrency(List<CreditBill> billList, AnalyzeParamsModel apm) {
        String originalContent = apm.getOriginContent();
        String result = PatternMatcherUtil.getMatcherString(Constant.FOREIGN_CURRENCY, originalContent);
        int status = StringUtils.hasText(result) ? 1 : 0;
        billList.forEach(bill -> {
            bill.setForeignCurrency(status);
        });
    }

    protected void updateIntegral(List<CreditBill> billList, AnalyzeParamsModel apm) {
        // if (cardList != null && cardList.size() > 0) {
        // cardList.forEach(card -> {
        // creditCardMapper.updateIntegral(card.getId());
        // });
        //
        // }
        if (!apm.getIsNew()) {
            return;
        }
        if (billList != null && !billList.isEmpty()) {
            billList.forEach(bill -> {
                JedisClusterUtils.setSave(
                        Constant.ANALYZED_BILL + apm.getEmail() + Constant.WORD_SEPARATOR + apm.getUserId(),
                        JSON.toJSONString(bill));
            });
            JedisClusterUtils.expire(
                    Constant.ANALYZED_BILL + apm.getEmail() + Constant.WORD_SEPARATOR + apm.getUserId(), ONE_HOUR);
        }
    }
}
