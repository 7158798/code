package com.pay.aile.bill.analyze.banktemplate.icbc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.pay.aile.bill.analyze.banktemplate.BaseBankTemplate;
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
import com.pay.aile.bill.model.AnalyzeParamsModel;
import com.pay.aile.bill.utils.DateUtil;
import com.pay.aile.bill.utils.PatternMatcherUtil;

/**
 * @author ji
 * @description 中国工商银行-牡丹贷记卡
 */
@Service
public class ICBCMDCreditTemplate extends BaseBankTemplate implements AbstractICBCTemplate {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private ThreadLocal<String> incomeOutcome = new ThreadLocal<String>();

    private final String ioSplitSign = ",";

    @Override
    public void initRules() {
        super.initRules();
        if (rules == null) {
            rules = new CreditTemplate();
            rules.setCardtypeId(17L);
            rules.setCardholder("尊敬的[\\u4e00-\\u9fa5]+");
            rules.setDueDate("贷记卡到期还款日 \\d{4}年\\d{1,2}月\\d{1,2}日");
            rules.setCycle("账单周期\\d{4}年\\d{2}月\\d{2}日\\S\\d{4}年\\d{2}月\\d{2}日");
            // rules.setBillDay("对账单生成日\\d{4}年\\d{2}月\\d{2}日");
            rules.setBillDay("账单周期\\d{4}年\\d{2}月\\d{2}日");

            rules.setCurrentAmount(
                    "\\d{4} -?\\d+\\.?\\d*/RMB -?\\d+\\.?\\d*/RMB -?\\d+\\.?\\d*/RMB -?\\d+\\.?\\d*/RMB");
            rules.setCardNumbers("\\d{4} -?\\d+\\.?\\d*/RMB -?\\d+\\.?\\d*/RMB -?\\d+\\.?\\d*/RMB -?\\d+\\.?\\d*/RMB");
            rules.setMinimum("\\d{4}\\(\\S+\\) 人民币 %s/RMB -?\\d+\\.?\\d*/RMB -?\\d+\\.?\\d*/RMB");
            rules.setDetails(
                    "\\d{4} \\d{4}-\\d{2}-\\d{2} \\d{4}-\\d{2}-\\d{2} \\S+ \\S+ \\d+\\.?\\d*/RMB+ \\d+\\.?\\d*/RMB+\\([\\u4e00-\\u9fa5]+\\)");
            rules.setIntegral("个人综合积分 余额\\d+");
            rules.setTransactionDate("1");
            rules.setBillingDate("2");
            rules.setTransactionDescription("4");
        }
    }

    @Override
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
            if (bill.getEndDate().equals(DateUtil.localDateToDate(endDate.plusDays(-1)))) {
                // card.setBillAmount(new BigDecimal(-1));
                // card.setMinimum(new BigDecimal(-1));
                logger.info("addDueBillAmount 已到本月账单日 cardNo/email:{}/{}", card.getNumbers(), apm.getEmail());
                card.setBillAmount(bill.getCurrentAmount());
                card.setMinimum(bill.getMinimum());
                // 是本期账单的还款日显示
                if (bill.getDueDate() != null) {
                    card.setDueDate(bill.getDueDate());
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
            logger.info("addDueBillAmount end 非本期非上期账单 cardNo/email:{}/{}", card.getNumbers(), apm.getEmail());
        }

    }

    @Override
    protected void analyzeBillDate(List<CreditCard> cardList, List<CreditBill> billList, String content,
            AnalyzeParamsModel apm) {
        logger.info("analyzeBillDate============================================================");
        if (StringUtils.hasText(rules.getBillDay())) {
            String billDay = getValueByPattern("billDay", content, rules.getBillDay(), apm, "");
            billDay = PatternMatcherUtil.getMatcherString("\\d{4}年\\d{2}月\\d{2}日", billDay);
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

    @Override
    protected void analyzeCardNo(List<CreditCard> cardList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getCardNumbers())) {
            List<String> matherList = getValueListByPattern("cardNumbers", content, rules.getCardNumbers(), "");
            if (!matherList.isEmpty()) {
                for (int i = 0; i < matherList.size(); i++) {
                    String mather = matherList.get(i);
                    String[] sa = mather.split(defaultSplitSign);
                    String cardNo = sa[0];
                    if (StringUtils.hasText(cardNo)) {
                        cardNo = cardNo.replaceAll("-", "");
                    }
                    CreditCard card = new CreditCard();
                    card.setNumbers(cardNo);
                    cardList.add(card);
                }
            }
        }
    }

    /**
     * 应还款额
     */
    @Override
    protected void analyzeCurrentAmount(List<CreditBill> billList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getCurrentAmount())) {
            List<String> matherList = getValueListByPattern("currentAmount", content, rules.getCurrentAmount(), "");
            if (!matherList.isEmpty()) {
                String incomeOutcomeStr = "";
                for (int i = 0; i < matherList.size(); i++) {
                    String mather = matherList.get(i);
                    mather = mather.replaceAll("/RMB", "");
                    String[] sa = mather.split(defaultSplitSign);
                    BigDecimal income = new BigDecimal(sa[2]);
                    BigDecimal outcome = new BigDecimal(sa[3]);
                    String currentAmount = sa[4];
                    if (!currentAmount.startsWith("-")) {
                        currentAmount = "0";
                    } else {
                        currentAmount = currentAmount.replaceAll("-", "");
                    }
                    CreditBill bill = new CreditBill();
                    bill.setCurrentAmount(new BigDecimal(currentAmount));
                    billList.add(bill);
                    incomeOutcomeStr += income.add(outcome).toString() + ioSplitSign;
                }
                incomeOutcome.set(incomeOutcomeStr);
            }
        }
    }

    @Override
    protected void analyzeCycle(List<CreditBill> billList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getCycle())) {
            String cycle = getValueByPattern("cycle", content, rules.getCycle(), apm, "");
            List<String> list = PatternMatcherUtil.getMatcher("\\d{4}年\\d{2}月\\d{2}日", cycle);
            billList.forEach(bill -> {
                bill.setBeginDate(DateUtil.parseDate(list.get(0)));
                bill.setEndDate(DateUtil.parseDate(list.get(1)));
            });
        }
    }

    @Override
    protected void analyzeIntegral(List<CreditCard> cardList, List<CreditBill> billList, String content,
            AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getIntegral())) {
            String integral = getValueByPattern("integral", content, rules.getIntegral(), apm, "");
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

    /**
     * 最低还款额与额度
     */
    @Override
    protected void analyzeMinimum(List<CreditBill> billList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getMinimum())) {
            billList.forEach(bill -> {
                String currentAmount = bill.getCurrentAmount().toString();
                String rule = String.format(rules.getMinimum(), currentAmount);
                String mather = getValueByPattern("minimun", content, rule, apm, "");
                if (StringUtils.hasText(mather)) {
                    String[] sa = mather.split(" ");
                    String minimumAmount = sa[3].replaceAll("/RMB", "");
                    String credits = sa[4].replaceAll("/RMB", "");
                    bill.setMinimum(new BigDecimal(minimumAmount));
                    bill.setCredits(new BigDecimal(credits));
                }
            });
        }
    }

    @Override
    protected LocalDate getThisDueDate(CreditCard card, CreditBill bill, AnalyzeParamsModel apm) {
        LocalDate today = LocalDate.now();
        if (StringUtils.hasText(card.getBillDay()) || bill.getDueDate() == null) {
            int billDay = Integer.parseInt(card.getBillDay());
            LocalDate dueDate = DateUtil.dateToLocalDate(bill.getDueDate());
            // 1~8号是本月
            if (billDay > 8) {
                return LocalDate.of(today.getYear(), today.getMonth(), dueDate.getDayOfMonth());
            } else {
                return LocalDate.of(today.getYear(), today.getMonthValue(), dueDate.getDayOfMonth()).plusMonths(1);
            }
        }
        return today;

    }

    @Transactional
    @Override
    protected void handleResultInternal(AnalyzeParamsModel apm) {
        Long emailId = apm.getEmailId();
        int year = 0;
        int month = 0;
        // 保存或更新卡信息
        List<CreditCard> cardList = apm.getResult().getCardList();
        List<CreditBill> billList = apm.getResult().getBillList();
        List<CreditCard> saveCardList = new ArrayList<>();
        List<CreditBill> saveBillList = new ArrayList<>();
        cardList.forEach(c -> {
            c.setStatus(apm.getIsNew() ? CardStatus.AVAILABLE.value : CardStatus.NEW.value);
            // c.setStatus(CardStatus.AVAILABLE.value);
        });

        // 查询邮件和用户的关系
        List<CreditUserEmailRelation> emailRelationList = new ArrayList<>();
        if (apm.getIsNew()) {
            CreditUserEmailRelation cuer = new CreditUserEmailRelation();
            cuer.setUserId(apm.getUserId());
            emailRelationList.add(cuer);
        } else {
            emailRelationList = userEmailRelationMapper.findByEmail(emailId);
        }
        for (CreditUserEmailRelation relation : emailRelationList) {
            for (CreditCard card : cardList) {
                CreditCard saveCard = new CreditCard();
                BeanUtils.copyProperties(card, saveCard, CreditCard.class);
                saveCard.setUserId(relation.getUserId());
                saveCardList.add(saveCard);
            }
            for (CreditBill bill : billList) {
                CreditBill saveBill = new CreditBill();
                BeanUtils.copyProperties(bill, saveBill, CreditBill.class);
                saveBillList.add(saveBill);
            }
        }
        if (!saveCardList.isEmpty()) {
            saveCardList = creditCardService.saveOrUpateCreditCard(saveCardList);
        }
        logger.debug("saveCardList == {}", saveCardList);
        // 保存账单
        for (int i = 0; i < saveBillList.size(); i++) {
            CreditBill bill = saveBillList.get(i);
            bill.setCardId(saveCardList.get(i).getId());
            bill.setCardNumbers(saveCardList.get(i).getCompleteNumbers());
            bill.setEmailId(emailId);
            bill.setSentDate(apm.getSentDate());
            bill.setFileId(apm.getFileId());
            if (year == 0) {
                year = Integer.valueOf(bill.getYear());
            }
            if (month == 0) {
                month = Integer.valueOf(bill.getMonth());
            }
        }

        boolean repeatBill = false;
        repeatBill = saveBillList(saveBillList, apm);
        logger.debug("repeatBill={},saveBillList == {}", repeatBill, saveBillList);

        if (!repeatBill) {

            List<CreditBillDetail> detailList = apm.getResult().getDetailList();
            List<CreditBillDetailRelation> relationList = new ArrayList<CreditBillDetailRelation>();
            if (!detailList.isEmpty()) {
                creditBillDetailService.batchSaveBillDetail(year, month, detailList);
                String s = incomeOutcome.get();
                incomeOutcome.remove();
                logger.info("ICBC牡丹卡:email={},incomeoutcome={}", apm.getEmail(), s);
                s = s.substring(0, s.length() - 1);
                String[] ioList = {};
                if (StringUtils.hasText(s)) {
                    ioList = s.split(ioSplitSign);
                }

                List<String> cardNumbers = new ArrayList<>();// 记录卡号的顺序
                saveBillList.forEach(bill -> {
                    if (!cardNumbers.contains(bill.getCardNumbers())) {
                        cardNumbers.add(bill.getCardNumbers());
                    }
                });
                // 根据卡号将bill分组
                Map<String, List<CreditBill>> groupBillList = saveBillList.stream()
                        .collect(Collectors.groupingBy(CreditBill::getCardNumbers));
                int detailIndex = 0;
                for (int i = 0; i < cardNumbers.size(); i++) {
                    List<CreditBill> bills = groupBillList.get(cardNumbers.get(i));
                    BigDecimal ioTarget = new BigDecimal(ioList[i]);
                    BigDecimal ioAmount = BigDecimal.ZERO;
                    for (; detailIndex < detailList.size(); detailIndex++) {
                        CreditBillDetailRelation relation = new CreditBillDetailRelation();
                        if (bills != null && !bills.isEmpty()) {
                            for (CreditBill bill : bills) {
                                relation.setBillId(bill.getId());
                                relation.setBillDetailId(detailList.get(detailIndex).getId());
                                relationList.add(relation);
                            }
                            BigDecimal amount = new BigDecimal(detailList.get(detailIndex).getAccountableAmount())
                                    .abs();
                            ioAmount = ioAmount.add(amount);
                            if (ioAmount.doubleValue() == ioTarget.doubleValue()) {
                                detailIndex++;
                                break;
                            }
                        }
                    }
                }
                creditBillDetailRelationService.batchSaveBillDetailRelation(year, month, relationList);
            }

            // 保存账单和用户关系。
            List<CreditUserBillRelation> billRelationList = new ArrayList<CreditUserBillRelation>();

            for (int i = 0; i < saveCardList.size(); i++) {
                CreditCard card = saveCardList.get(i);
                CreditBill bill = saveBillList.get(i);
                CreditUserBillRelation billRelation = new CreditUserBillRelation();
                if (bill.getId() != null) {
                    billRelation.setBillId(bill.getId());
                    billRelation.setUserId(card.getUserId());
                    billRelationList.add(billRelation);
                }

            }
            creditUserBillRelationService.batchSave(billRelationList);
        }
        // 卡片和用户的关系
        // 保存卡片和用户关系
        List<CreditUserCardRelation> cardRelationList = new ArrayList<CreditUserCardRelation>();
        saveCardList.forEach(card -> {
            CreditUserCardRelation cardRelation = new CreditUserCardRelation();
            cardRelation.setCardId(card.getId());
            cardRelation.setUserId(card.getUserId());
            cardRelation.setStatus(apm.getIsNew() ? CardStatus.AVAILABLE.value : CardStatus.NEW.value);
            // cardRelation.setStatus(CardStatus.AVAILABLE.value);
            cardRelationList.add(cardRelation);
        });
        // 保存
        creditUserCardRelationService.batchSave(cardRelationList);
        // 修改积分
        updateIntegral(saveBillList, apm);
    }

    /**
     * 设置信用卡类型
     */
    @Override
    protected void setCardType() {
        cardType = CardTypeEnum.ICBC_MDC;
    }

    @Override
    protected void setField(CreditBillDetail cbd, int index, String value) {
        if (index == 5) {
            String transamount = PatternMatcherUtil.getMatcherString("\\d+\\.?\\d*", value);
            if (value.indexOf("存入") != -1) {
                transamount = "-" + transamount;
            }
            cbd.setTransactionAmount(transamount);
            String currency = PatternMatcherUtil.getMatcherString("[a-zA-Z]+", value);
            cbd.setTransactionCurrency(currency);
        } else if (index == 6) {
            String accountamount = PatternMatcherUtil.getMatcherString("\\d+\\.?\\d*", value);
            if (value.indexOf("存入") != -1) {
                accountamount = "-" + accountamount;
                // 设置交易金额的值
                cbd.setTransactionAmount("-" + cbd.getTransactionAmount());
            }
            cbd.setAccountableAmount(accountamount);
            String currency = PatternMatcherUtil.getMatcherString("[a-zA-Z]+", value);
            cbd.setAccountType(currency);
        }
    }
}
