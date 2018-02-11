package com.pay.card.view;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.pay.card.model.CreditCard;
import com.pay.card.utils.AmountUtil;
import com.pay.card.utils.DateUtil;
import com.pay.card.web.context.CardBuildContext;

/**
 * @ClassName: CreditCardView
 * @Description:信用卡视图
 * @author jinjing
 * @date 2017年11月15日
 */
public class CreditCardView {
    private static Logger logger = LoggerFactory.getLogger(CreditCardView.class);
    private final CreditCard creditCard;

    private final CardBuildContext buildContext;

    public String lastUpdatTime;

    public String year;

    public String month;

    public CreditCardView(CreditCard card, CardBuildContext buildContext) {
        creditCard = card;
        this.buildContext = buildContext;
    }

    public boolean filterCard() {

        if (creditCard.getMinimum() == null || creditCard.getBillAmount() == null || creditCard.getRepayment() == null
                || creditCard.getCredits() == null || creditCard.getCredits().doubleValue() < 0) {

            return false;
        }

        if (creditCard.getCredits().doubleValue() - creditCard.getBillAmount().doubleValue()
                + creditCard.getRepayment().doubleValue() / creditCard.getCredits().doubleValue() >= 0.2
                && creditCard.getCredits().doubleValue() - creditCard.getBillAmount().doubleValue()
                        + creditCard.getRepayment().doubleValue() > 1000
                && creditCard.getSource() == 0) {
            return true;
        }

        return false;
    }

    /**
     * @Title: getBankCode
     * @Description: 银行编码
     * @param
     * @return
     */
    public String getBankCode() {
        if (creditCard.getBank() != null) {
            return creditCard.getBank().getCode();
        }
        return "";
    }

    /**
     * @Title: getBankName @Description:银行名称 @return String @throws
     */
    public String getBankName() {
        if (creditCard.getBank() != null) {
            return creditCard.getBank().getName();
        }
        return "";
    }

    /**
     * @Title: getBankShortName
     * @Description:银行简称
     * @return String 返回类型 @throws
     */
    public String getBankShortName() {
        if (creditCard.getBank() != null) {
            return creditCard.getBank().getShortName();
        }
        return "";
    }

    /**
     * @Title: getBillAmount
     * @Description:账单金额
     * @return BigDecimal 返回类型 @throws
     */
    public String getBillAmount() {
        if (creditCard.getBillAmount() != null) {
            return AmountUtil.amountFormat(creditCard.getBillAmount());
        }
        return "-1";
    }

    public String getBillButton() {
        String note = "立即还款";
        LocalDate nowDate = LocalDate.now();
        // Date now = DateUtil.localDateToDate(nowDate);
        Date dueDate = creditCard.getDueDate();
        // int day = nowDate.getDayOfMonth();
        // int billDay = Integer.parseInt(creditCard.getBillDay());

        // logger.info("卡号:[" + creditCard.getNumbers() +
        // "]dueDate:{},BillDay:{}", dueDate, creditCard.getBillDay());
        if (dueDate == null || creditCard.getBillDay() == null) {
            return note;
        }

        if (creditCard.getMinimum() != null && creditCard.getRepayment() != null
                && creditCard.getRepayment().doubleValue() > 0) {
            // logger.info("卡号:[" + creditCard.getNumbers() +
            // "]====================");
            // 首先得判断逾期
            // long d = DateUtil.getdifferenceDay(creditCard.getDueDate(), now);
            // 过了还款日
            // creditCard.getRepayment() != null && creditCard.getBillAmount()
            // != null
            // && (creditCard.getBillAmount().doubleValue() < 0 &&
            // creditCard.getRepayment()
            // .doubleValue() >= creditCard.getBillAmount().doubleValue())

            if (creditCard.getRepayment() != null && creditCard.getBillAmount() != null
                    && creditCard.getRepayment().doubleValue() >= creditCard.getBillAmount().doubleValue()) {
                note = "上月全部还清";
                return note;
            } else if (creditCard.getRepayment() != null && creditCard.getMinimum() != null
                    && creditCard.getRepayment().doubleValue() >= creditCard.getMinimum().doubleValue()) {
                note = "上月已还最低";
                return note;
            } else if (creditCard.getRepayment() != null && creditCard.getMinimum() != null
                    && creditCard.getRepayment().doubleValue() > 0) {
                note = "继续还款";
                return note;
            }
        } else {
            if (creditCard.getRepayment() != null && creditCard.getMinimum() != null
                    && creditCard.getRepayment().doubleValue() > 0) {
                note = "继续还款";
                return note;
            } else {
                note = "立即还款";
                return note;
            }

        }
        return note;
    }

    /**
     * @Title: getBillCycle
     * @Description: 账单周期
     * @param
     * @return
     */
    public String getBillCycle() {
        String b = "";
        if (creditCard.getBeginDate() == null || creditCard.getEndDate() == null) {
            return "";
        }
        if (creditCard.getBeginDate() != null) {
            b = DateUtil.formatMMDD3(creditCard.getBeginDate());
        }
        String e = "";
        if (creditCard.getEndDate() != null) {
            e = DateUtil.formatMMDD3(creditCard.getEndDate());
        }

        return b + "-" + e;

    }

    /**
     * @Title: getBillDay
     * @Description:
     * @return Integer 返回类型 @throws
     */
    public Integer getBillDay() {
        if (creditCard.getSource() == 1) {
            if (creditCard.getUserCardRelation() != null && creditCard.getUserCardRelation().getBillDay() != null) {
                return creditCard.getUserCardRelation().getBillDay();
            }

        } else {
            if (StringUtils.hasText(creditCard.getBillDay())) {
                try {
                    return Integer.parseInt(creditCard.getBillDay());
                } catch (Exception e) {
                    return 1;
                }
            }

        }
        return 1;
        // return creditCard.getBillDay() == null ? 0 :
        // Integer.parseInt(creditCard.getBillDay());
        // return
        // com.pay.card.utils.DateUtil.getBillDay(creditCard.getBillDay());

    }

    public String getBillNote() {
        String note = "";
        LocalDate nowDate = LocalDate.now();
        Date now = DateUtil.localDateToDate(nowDate);
        int day = nowDate.getDayOfMonth();
        int billDay = Integer.parseInt(creditCard.getBillDay());

        // TODO 修改creditCard.getDueDate()
        Date dueDate = creditCard.getDueDate();

        if (dueDate == null) {
            dueDate = new Date();
        }

        // 首先得判断逾期
        long d = DateUtil.getdifferenceDay(now, dueDate);
        if (creditCard.getMinimum() != null && creditCard.getRepayment() != null
                && creditCard.getMinimum().doubleValue() != -1
                && creditCard.getMinimum().doubleValue() > creditCard.getRepayment().doubleValue() && d > 0 && d <= 3) {
            // 显示逾期
            note = String.format("已逾期%d天", d);
            return note;
        } else if (now.before(dueDate) || now.equals(dueDate)) {

            if (creditCard.getMinimum() != null && creditCard.getRepayment() != null) {

                if (creditCard.getBillAmount().doubleValue() <= creditCard.getRepayment().doubleValue()) {

                    Period between = Period.between(nowDate,
                            DateUtil.dateToLocalDate(creditCard.getEndDate()).plusMonths(1));
                    if (between.getDays() == 0) {
                        note = "今天出账单";
                    } else if (between.getDays() == 1) {
                        note = "明天出账单";
                    } else if (between.getDays() == 2) {
                        note = "后天出账单";
                    } else {
                        note = String.format("%d天后出账单", between.getDays());
                    }
                } else {
                    Period between = Period.between(nowDate, DateUtil.dateToLocalDate(dueDate));
                    if (between.getDays() == 0) {
                        note = "今日还款";
                    } else if (between.getDays() == 1) {
                        note = "明天还款";
                    } else if (between.getDays() == 2) {
                        note = "后天还款";
                    } else {
                        note = String.format("%d天后还款", between.getDays());
                    }
                }

            } else {

                Period between = Period.between(nowDate, DateUtil.dateToLocalDate(dueDate));
                if (between.getDays() == 0) {
                    note = "今日还款";
                } else if (between.getDays() == 1) {
                    note = "明天还款";
                } else if (between.getDays() == 2) {
                    note = "后天还款";
                } else {
                    note = String.format("%d天后还款", between.getDays());
                }
            }

        } else {
            logger.info("dueDate:{}============================", dueDate);
            Period between = Period.between(nowDate, DateUtil.dateToLocalDate(creditCard.getEndDate()).plusMonths(1));
            if (between.getDays() == 0) {
                note = "今天出账单";
            } else if (between.getDays() == 1) {
                note = "明天出账单";
            } else if (between.getDays() == 2) {
                note = "后天出账单";
            } else {
                note = String.format("%d天后出账单", between.getDays());
            }
        }

        return note;

        // String note = "";
        // LocalDate nowDate = LocalDate.now();
        // Date now = DateUtil.localDateToDate(nowDate);
        // int day = nowDate.getDayOfMonth();
        // int billDay = Integer.parseInt(creditCard.getBillDay());
        //
        // // TODO 修改creditCard.getDueDate()
        // Date dueDate = creditCard.getDueDate();
        // if (dueDate == null) {
        // dueDate = new Date();
        // }
        //
        // // 首先得判断逾期
        // long d = DateUtil.getdifferenceDay(now, dueDate);
        // if (creditCard.getMinimum() != null && creditCard.getRepayment() !=
        // null
        // && creditCard.getMinimum().doubleValue() != -1
        // && creditCard.getMinimum().doubleValue() >
        // creditCard.getRepayment().doubleValue() && d > 0 && d <= 3) {
        // // 显示逾期
        // note = String.format("已逾期%d天", d);
        // } else if (dueDate.equals(now)) {
        // note = "今日还款";
        // } else if (now.before(dueDate) || now.equals(dueDate)) {
        // if (creditCard.getMinimum() != null && creditCard.getRepayment() !=
        // null
        // && creditCard.getBillAmount().doubleValue() <=
        // creditCard.getRepayment().doubleValue()) {
        // Period between = Period.between(nowDate,
        // DateUtil.dateToLocalDate(creditCard.getEndDate()).plusMonths(1));
        // if (between.getDays() == 0) {
        // note = "今天出账单";
        // } else if (between.getDays() == 1) {
        // note = "明天出账单";
        // } else if (between.getDays() == 2) {
        // note = "后天出账单";
        // } else {
        // note = String.format("%d天后出账单", between.getDays());
        // }
        // } else {
        // Period between = Period.between(nowDate,
        // DateUtil.dateToLocalDate(dueDate));
        // if (between.getDays() == 0) {
        // note = "今日还款";
        // } else if (between.getDays() == 1) {
        // note = "明天还款";
        // } else if (between.getDays() == 2) {
        // note = "后天还款";
        // } else {
        // note = String.format("%d天后还款", between.getDays());
        // }
        // }
        //
        // } else {
        // Period between = Period.between(nowDate,
        // DateUtil.dateToLocalDate(creditCard.getEndDate()).plusMonths(1));
        // if (between.getDays() == 0) {
        // note = "今天出账单";
        // } else if (between.getDays() == 1) {
        // note = "明天出账单";
        // } else if (between.getDays() == 2) {
        // note = "后天出账单";
        // } else {
        // note = String.format("%d天后出账单", between.getDays());
        // }
        // }
        //
        // return note;
    }

    /**
     * @Title: getCardholder
     * @Description:持卡人
     * @return String 返回类型 @throws
     */
    public String getCardholder() {
        if (StringUtils.hasText(creditCard.getCardholder())) {
            return creditCard.getCardholder();
        }
        return "";
    }

    public String getCardNote() {

        String note = "";
        LocalDate nowDate = LocalDate.now();
        Date now = DateUtil.localDateToDate(nowDate);

        // TODO 修改creditCard.getDueDate()
        Date dueDate = creditCard.getDueDate();
        if (dueDate == null) {
            dueDate = new Date();
        }
        LocalDate dueDateLocal = DateUtil.dateToLocalDate(dueDate);

        if (creditCard.getMinimum() != null) {

            // 首先得判断逾期
            long d = DateUtil.getdifferenceDay(now, dueDate);
            if (creditCard.getRepayment() != null && creditCard.getMinimum().doubleValue() != -1
                    && creditCard.getMinimum().doubleValue() > creditCard.getRepayment().doubleValue() && d > 0
                    && d <= 3) {
                // 显示逾期
                note = String.format("已逾期%d天", d);
            }
            // else if (dueDate.equals(now)) {
            // note = "今日还款";
            // }
            else if (now.before(dueDate) || nowDate.equals(dueDateLocal)) {

                // 过了账单日
                // 今天是还款日

                if (creditCard.getRepayment() != null && creditCard.getBillAmount() != null
                        && creditCard.getRepayment().doubleValue() >= creditCard.getBillAmount().doubleValue()) {
                    // note = "全部还清";
                    Period between = Period.between(nowDate,
                            DateUtil.dateToLocalDate(creditCard.getEndDate()).plusMonths(1));
                    if (between.getDays() == 0) {
                        note = String.format("今日出账单");
                        return note;
                    } else {
                        note = String.format("%d天后出账单", between.getDays());
                        return note;
                    }

                } else if (creditCard.getRepayment() != null && creditCard.getMinimum() != null
                        && creditCard.getRepayment().doubleValue() >= creditCard.getMinimum().doubleValue()) {
                    note = "已还最低";
                    return note;
                } else {
                    Period between = Period.between(nowDate, DateUtil.dateToLocalDate(dueDate));
                    if (between.getDays() == 0) {
                        note = "今日还款";
                        return note;
                    } else if (between.getDays() == 1) {
                        note = "明天还款";
                        return note;
                    } else if (between.getDays() == 2) {
                        note = "后天还款";
                        return note;
                    } else {
                        note = String.format("%d天后还款", between.getDays());
                        return note;
                    }

                }

            } else {
                Period between = Period.between(nowDate,
                        DateUtil.dateToLocalDate(creditCard.getEndDate()).plusMonths(1));
                if (between.getDays() == 0) {
                    note = String.format("今日出账单");
                    return note;
                } else {
                    note = String.format("%d天后出账单", between.getDays());
                    return note;
                }

            }

        } else {
            Period between = Period.between(nowDate, DateUtil.dateToLocalDate(creditCard.getEndDate()).plusMonths(1));
            note = String.format("%d天后出账单", between.getDays());
        }

        return note;
    }

    /**
     * @Title: getCardNoteColour
     * @Description: 首页字体的显示的颜色 0 黑色 1 蓝色 2 红色
     * @return Integer 返回类型 @throws
     */
    public Integer getCardNoteColour() {

        LocalDate nowDate = LocalDate.now();
        Date now = DateUtil.localDateToDate(nowDate);
        Date dueDate = creditCard.getDueDate();
        if (dueDate == null) {
            dueDate = new Date();
        }
        LocalDate dueDateLocal = DateUtil.dateToLocalDate(dueDate);
        logger.info("id:{},now:{},dueDate:{}", creditCard.getId(), now, dueDate);
        if (creditCard.getMinimum() != null && creditCard.getMinimum().doubleValue() != -1) {

            // 首先得判断逾期
            long d = DateUtil.getdifferenceDay(now, dueDate);
            if (creditCard.getRepayment() != null
                    && creditCard.getMinimum().doubleValue() > creditCard.getRepayment().doubleValue() && d > 0
                    && d <= 3) {
                // 显示逾期
                return 2;
            } else if (dueDateLocal.equals(nowDate)) {
                return 2;
            } else if (now.before(dueDate)) {

                // 过了账单日
                // 今天是还款日
                //
                 if (creditCard.getRepayment() != null &&
                 creditCard.getBillAmount() != null
                 && creditCard.getRepayment().doubleValue() >=
                 creditCard.getBillAmount().doubleValue()) {
                	 return 0;
                 } else
                if (creditCard.getRepayment() != null && creditCard.getMinimum() != null
                        && creditCard.getMinimum().doubleValue() > 0
                        && creditCard.getRepayment().doubleValue() >= creditCard.getMinimum().doubleValue()) {
                    return 1;
                } else {
                    Period between = Period.between(nowDate, DateUtil.dateToLocalDate(dueDate));
                    if (between.getDays() <= 7) {
                        return 2;
                    } else {
                        return 0;
                    }
                }
            } else {
                return 0;
            }

        } else {
            return 0;
        }

    }

    /**
     * @Title: getCardType @Description: 卡的状态
     * @return Integer 返回类型 @throws 3逾期 2没还 1还最低 0未出账单
     */
    public Integer getCardStatus() {

        LocalDate nowDate = LocalDate.now();
        Date now = DateUtil.localDateToDate(nowDate);

        Date dueDate = creditCard.getDueDate();
        if (dueDate == null) {
            dueDate = new Date();
        }

        if (creditCard.getMinimum() != null) {

            // 首先得判断逾期
            long d = DateUtil.getdifferenceDay(now, dueDate);
            if (creditCard.getRepayment() != null && creditCard.getMinimum().doubleValue() != -1
                    && creditCard.getMinimum().doubleValue() > creditCard.getRepayment().doubleValue() && d > 0
                    && d <= 3) {
                // 显示逾期
                return 3;
            } else {

                // 过了账单日
                // 今天是还款日

                if (creditCard.getRepayment() != null && creditCard.getBillAmount() != null
                        && creditCard.getRepayment().doubleValue() >= creditCard.getBillAmount().doubleValue()) {
                    return 0;
                } else if (creditCard.getRepayment() != null && creditCard.getMinimum() != null
                        && creditCard.getRepayment().doubleValue() >= creditCard.getMinimum().doubleValue()) {
                    return 1;
                } else {
                    // 未出账单
                    if (creditCard.getBillAmount() == null && creditCard.getBillAmount().doubleValue() <= 0) {
                        return 0;
                    } else {
                        return 2;
                    }

                }

            }

        } else {
            return 0;
        }

    }

    /**
     * @Title: getConsumption
     * @Description:未出账单金额
     * @return BigDecimal 返回类型 @throws
     */
    public String getConsumption() {
        if (creditCard.getConsumption() != null) {
            if (creditCard.getConsumption().doubleValue() == 0) {
                return "-1";
            }
            return AmountUtil.amountFormat(creditCard.getConsumption());
        }
        return "-1";
    }

    /**
     * @Title: getCredits
     * @Description:信用卡额度
     * @return BigDecimal 返回类型 @throws
     */
    public String getCredits() {
        if (creditCard.getCredits() != null) {
            return creditCard.getCredits().toString();
        }
        return "-1";

    }

    /**
     * @Title: getDay
     * @Description: 卡的状态
     * @return Integer 返回类型
     * @throws 3逾期
     *             2没还 1还最低 0未出账单
     */
    public Map<String, Integer> getDay() {
        Map<String, Integer> map = new HashMap<String, Integer>();

        LocalDate nowDate = LocalDate.now();
        Date now = DateUtil.localDateToDate(nowDate);

        Date dueDate = creditCard.getDueDate();
        if (dueDate == null) {
            dueDate = new Date();
        }

        if (creditCard.getMinimum() != null) {

            // 首先得判断逾期
            long d = DateUtil.getdifferenceDay(now, dueDate);
            if (creditCard.getRepayment() != null && creditCard.getMinimum().doubleValue() != -1
                    && creditCard.getMinimum().doubleValue() > creditCard.getRepayment().doubleValue() && d > 0
                    && d <= 3) {
                // 显示逾期

                map.put("cardStatus", 3);
                map.put("day", Integer.valueOf(d + ""));
                return map;
            } else {

                // 过了账单日
                // 今天是还款日

                if (creditCard.getRepayment() != null && creditCard.getBillAmount() != null
                        && creditCard.getRepayment().doubleValue() >= creditCard.getBillAmount().doubleValue()) {
                    Period between = Period.between(nowDate,
                            DateUtil.dateToLocalDate(creditCard.getEndDate()).plusMonths(1));
                    map.put("cardStatus", 0);
                    map.put("day", between.getDays());
                    return map;
                } else if (creditCard.getRepayment() != null && creditCard.getMinimum() != null
                        && creditCard.getRepayment().doubleValue() >= creditCard.getMinimum().doubleValue()) {

                    map.put("cardStatus", 1);
                    return map;
                } else {
                    // 未出账单
                    if (creditCard.getBillAmount() == null && creditCard.getBillAmount().doubleValue() <= 0) {
                        Period between = Period.between(nowDate,
                                DateUtil.dateToLocalDate(creditCard.getEndDate()).plusMonths(1));
                        map.put("cardStatus", 0);
                        map.put("day", between.getDays());
                        return map;
                    } else {
                        Period between = Period.between(nowDate, DateUtil.dateToLocalDate(dueDate));
                        map.put("cardStatus", 2);
                        map.put("day", between.getDays());
                        return map;
                    }

                }

            }

        } else {
            map.put("cardStatus", 0);
            map.put("day", 0);
            return map;
        }

    }

    public Date getDueDate() {

        return creditCard.getDueDate();

    }

    /**
     * @Title: getDueDay
     * @Description:获取还款时间
     * @return Long 返回类型 @throws
     */
    public String getDueDay() {
        if (creditCard.getDueDate() != null) {
            return String.valueOf(DateUtil.dateToLocalDate(creditCard.getDueDate()).getDayOfMonth());

        }
        return "1";
    }

    /**
     * 0 固定 1 账单日后多少天
     */
    public Integer getDueType() {
        if (creditCard.getSource() == 1) {
            if (creditCard.getUserCardRelation() != null) {
                return creditCard.getUserCardRelation().getDueType();
            } else {
                return 0;
            }

        } else {
            if (creditCard.getBank() != null) {
                if (creditCard.getBank().getRepaymentCycle() == 0) {
                    return 0;
                } else {
                    return 1;
                }
            } else {
                return 0;
            }
        }

    }

    /**
     * @Title: getFreeInterestPeriod
     * @Description: 获取免息期
     * @return Long 返回类型 @throws
     */
    public Long getFreeInterestPeriod() {
        try {
            long freeInterestPeriod = 0;
            if (creditCard.getSource() == 1 && creditCard.getUserCardRelation() != null) {
                // 固定
                if (creditCard.getUserCardRelation().getDueType() == 0) {
                    freeInterestPeriod = DateUtil.getFreeInterestPeriod(creditCard.getUserCardRelation().getBillDay(),
                            creditCard.getUserCardRelation().getDueDay(), 0);
                } else {
                    // 不固定
                    freeInterestPeriod = DateUtil.getFreeInterestPeriod(creditCard.getUserCardRelation().getBillDay(),
                            creditCard.getUserCardRelation().getDueDay(), creditCard.getUserCardRelation().getDueDay());
                }
            } else {

                // logger.info("{},{},{}", creditCard.getBillDay(),
                // creditCard.getDueDay(),
                // creditCard.getBank().getRepaymentCycle());
                freeInterestPeriod = DateUtil.getFreeInterestPeriod(Integer.parseInt(creditCard.getBillDay()),
                        Integer.parseInt(creditCard.getDueDay()), creditCard.getBank().getRepaymentCycle());
            }

            return freeInterestPeriod;

        } catch (Exception e) {
            logger.info("获取免息期异常:{}", e);
        }
        return 0L;
    }

    /**
     * @Title: getFullNumbers
     * @Description: 获取完整卡号
     * @param
     * @return
     */
    public String getFullNumbers() {
        if (StringUtils.hasText(creditCard.getNumbers())) {
            if (!creditCard.getNumbers().contains("_")) {
                return creditCard.getNumbers().trim();
            } else {
                return creditCard.getNumbers().split("_")[1].trim();
            }

        }
        return creditCard.getNumbers();
    }

    public Long getId() {
        return creditCard.getId();
    }

    /**
     * @Title: getIntegral
     * @Description: 积分
     * @return BigDecimal 返回类型 @throws
     */
    public String getIntegral() {
        if (creditCard.getIntegral() != null && creditCard.getIntegral().doubleValue() > 0) {
            return creditCard.getIntegral() + "";
        }
        return "";

    }

    public Integer getIsNew() {
        if (creditCard.getCreateDate() != null) {
            LocalDate localCreditCard = DateUtil.dateToLocalDate(creditCard.getCreateDate());
            if (localCreditCard.equals(LocalDate.now())) {
                return 1;
            } else {
                return 0;
            }
        }
        return 0;
    }

    /**
     * @Title: getIsPayOff
     * @Description: 账单是否还清 1: 还清,0:未还清
     * @param
     * @return
     */
    public String getIsPayOff() {
        if (creditCard.getRepayment().compareTo(creditCard.getBillAmount()) >= 0) {
            return "1";
        } else {
            return "0";
        }
    }

    /**
     * @Title: getLastUpdatTime
     * @Description:最后更新时间
     * @return String 返回类型 @throws
     */
    public String getLastUpdatTime() {

        return lastUpdatTime;
    }

    /**
     * @Title: getNextBillDays
     * @Description: 还有n天出账单
     * @param
     * @return
     */
    // public String getNextBillDays() {
    // LocalDate today = LocalDate.now();
    // LocalDate lastDay = today.with(TemporalAdjusters.lastDayOfMonth());
    // Period p = Period.between(today, lastDay);
    // if (StringUtils.hasText(creditCard.getBillDay())) {
    // return p.getDays() + Integer.valueOf(creditCard.getBillDay()) + "";
    // }
    // return null;
    // }
    /**
     * @Title: getMinimum @Description: 最低还款额 @param @return 参数 @return String
     *         返回类型 @throws
     */
    public String getMinimum() {
        if (creditCard.getMinimum() != null) {
            return AmountUtil.amountFormat(creditCard.getMinimum());

        }
        return "-1";
    }

    public String getMonth() {
        return month;
    }

    public String getName() {
        if (StringUtils.hasText(creditCard.getName())) {
            return creditCard.getName();
        }
        return "";
    }

    /**
     * @Title: getNumbers
     * @Description:银行卡号
     * @return String @throws
     */
    public String getNumbers() {
        String numbers = creditCard.getNumbers();
        if (numbers != null && numbers.length() > 4) {
            numbers = numbers.substring(numbers.length() - 4, numbers.length());
            return numbers;
        }
        return numbers;
    }

    public String getOriginalDueDay() {
        if (creditCard.getSource() == 1) {
            // 手工添加
            if (creditCard.getUserCardRelation() != null) {
                return String.valueOf(creditCard.getUserCardRelation().getDueDay());
            }

        } else {
            if (creditCard.getBank() != null) {
                if (creditCard.getBank().getRepaymentCycle() == 0) {
                    return String.valueOf(creditCard.getDueDay());
                } else {
                    return String.valueOf(creditCard.getBank().getRepaymentCycle());
                }

            }
        }
        return "1";
    }

    /**
     * @Title: getPhoneNo
     * @Description: 手机号
     * @param
     * @return String @throws
     */
    public String getPhoneNo() {
        if (StringUtils.hasText(creditCard.getPhoneNo())) {
            return creditCard.getPhoneNo();
        }
        return "";
    }

    /**
     * @Title: getRemainingAmount
     * @Description:剩余应还金额
     * @return String @throws
     */
    public String getRemainingAmount() {
        if (creditCard.getBillAmount() != null && creditCard.getRepayment() != null) {
            if (creditCard.getBillAmount().doubleValue() >= 0) {
                if (creditCard.getBillAmount().doubleValue() >= 0) {
                    AmountUtil.amountFormat(creditCard.getBillAmount());
                } else {
                    AmountUtil.amountFormat(creditCard.getBillAmount().subtract(creditCard.getRepayment()));
                }
            } else {
                return "-1";
            }
            return AmountUtil.amountFormat(creditCard.getBillAmount().subtract(creditCard.getRepayment()));
        }
        return "0";
    }

    /**
     * @Title: getRemainingCredits
     * @Description: 剩余额度
     * @param
     * @return String @throws
     */
    public String getRemainingCredits() {
        if (creditCard.getCredits() != null) {

            if (creditCard.getCredits().doubleValue() < 0) {
                return "-1";
            } else if (creditCard.getBillAmount() != null && creditCard.getRepayment() != null) {
                double billAmout = creditCard.getBillAmount().doubleValue();
                double repayment = creditCard.getRepayment().doubleValue();
                if (billAmout < 0) {
                    billAmout = 0;
                }
                if (repayment < 0) {
                    repayment = 0;
                }
                if (creditCard.getCredits().doubleValue() <= creditCard.getCredits().doubleValue() - billAmout
                        + repayment - creditCard.getConsumption().doubleValue()) {

                    return AmountUtil.amountFormat2(creditCard.getCredits());
                }
                return AmountUtil.amountFormat2(new BigDecimal(creditCard.getCredits().doubleValue() - billAmout
                        + repayment - creditCard.getConsumption().doubleValue()));

            } else {
                return AmountUtil.amountFormat2(creditCard.getCredits());
            }

        } else {

            return "-1";
        }
    }

    public String getRemainingMinimum() {
        if (creditCard.getMinimum() != null && creditCard.getRepayment() != null) {
            BigDecimal minimum = creditCard.getMinimum().subtract(creditCard.getRepayment());
            if (minimum.doubleValue() < 0) {
                return "-1";
            } else {
                return AmountUtil.amountFormat(minimum);
            }

        }
        return "0";
    }

    /**
     * @Title: getRepayment
     * @Description: 已还款额
     * @param
     * @return
     */

    public String getRepayment() {
        if (creditCard.getRepayment() != null) {
            return AmountUtil.amountFormat(creditCard.getRepayment());
        }
        return "0";
    }

    public String getShowBillDay() {

        LocalDate nowDate = LocalDate.now();
        Date now = DateUtil.localDateToDate(nowDate);

        Date dueDate = creditCard.getDueDate();
        if (dueDate == null) {
            dueDate = new Date();
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM月dd");
        if (creditCard.getMinimum() != null) {

            // 首先得判断逾期
            long d = DateUtil.getdifferenceDay(now, dueDate);
            if (creditCard.getRepayment() != null && creditCard.getMinimum().doubleValue() != -1
                    && creditCard.getMinimum().doubleValue() > creditCard.getRepayment().doubleValue() && d > 0
                    && d <= 3) {
                // 显示逾期
                if (creditCard.getEndDate() != null) {
                    return DateUtil.formatMMDD4(creditCard.getEndDate());
                } else {
                    return "";
                }
            } else {

                // 过了账单日
                // 今天是还款日
                // 当前日期小于等于账单日,显示当期账单日
                if (now.before(dueDate) || now.equals(dueDate)) {

                    // 账单已还清,账单日加一个月
                    if (creditCard.getRepayment() != null && creditCard.getBillAmount() != null
                            && creditCard.getRepayment().doubleValue() >= creditCard.getBillAmount().doubleValue()) {
                        if (creditCard.getEndDate() != null) {
                            return dtf.format(DateUtil.dateToLocalDate(creditCard.getEndDate()));
                        } else {
                            return "";
                        }
                        // 账单已还最低
                    } else if (creditCard.getRepayment() != null && creditCard.getMinimum() != null
                            && creditCard.getRepayment().doubleValue() >= creditCard.getMinimum().doubleValue()) {
                        if (creditCard.getEndDate() != null) {
                            return DateUtil.formatMMDD4(creditCard.getEndDate());
                        } else {
                            return "";
                        }
                    } else {
                        // 未出账单
                        if (creditCard.getBillAmount() == null && creditCard.getBillAmount().doubleValue() <= 0) {
                            if (creditCard.getEndDate() != null) {
                                return dtf.format(DateUtil.dateToLocalDate(creditCard.getEndDate()));
                            } else {
                                return "";
                            }
                        } else {
                            if (creditCard.getEndDate() != null) {
                                return DateUtil.formatMMDD4(creditCard.getEndDate());
                            } else {
                                return "";
                            }
                        }

                    }
                } else {
                    // 账单已还清,账单日加一个月
                    if (creditCard.getRepayment() != null && creditCard.getBillAmount() != null
                            && creditCard.getRepayment().doubleValue() >= creditCard.getBillAmount().doubleValue()) {
                        if (creditCard.getEndDate() != null) {
                            return dtf.format(DateUtil.dateToLocalDate(creditCard.getEndDate()).plusMonths(1));
                        } else {
                            return "";
                        }
                        // 账单已还最低
                    } else if (creditCard.getRepayment() != null && creditCard.getMinimum() != null
                            && creditCard.getRepayment().doubleValue() >= creditCard.getMinimum().doubleValue()) {
                        if (creditCard.getEndDate() != null) {
                            return DateUtil.formatMMDD4(creditCard.getEndDate());
                        } else {
                            return "";
                        }
                    } else {
                        // 未出账单
                        if (creditCard.getBillAmount() == null && creditCard.getBillAmount().doubleValue() <= 0) {
                            if (creditCard.getEndDate() != null) {
                                return dtf.format(DateUtil.dateToLocalDate(creditCard.getEndDate()).plusMonths(1));
                            } else {
                                return "";
                            }
                        } else {
                            if (creditCard.getEndDate() != null) {
                                return DateUtil.formatMMDD4(creditCard.getEndDate());
                            } else {
                                return "";
                            }
                        }

                    }
                }

            }

        } else {
            if (creditCard.getEndDate() != null) {
                return dtf.format(DateUtil.dateToLocalDate(creditCard.getEndDate()).plusMonths(1));
            } else {
                return "";
            }
        }

        // return
        // com.pay.card.utils.DateUtil.getBillDay(creditCard.getBillDay());

    }

    public String getShowDueDay() {

        LocalDate nowDate = LocalDate.now();
        Date now = DateUtil.localDateToDate(nowDate);

        Date dueDate = creditCard.getDueDate();
        LocalDate dueDateLocal = DateUtil.dateToLocalDate(dueDate);

        if (dueDate == null) {
            dueDate = new Date();
        }

        if (creditCard.getMinimum() != null) {

            // 首先得判断逾期
            long d = DateUtil.getdifferenceDay(now, dueDate);
            if (creditCard.getRepayment() != null && creditCard.getMinimum().doubleValue() != -1
                    && creditCard.getMinimum().doubleValue() > creditCard.getRepayment().doubleValue() && d > 0
                    && d <= 3) {
                // 显示逾期
                return DateUtil.formatMMDD4(creditCard.getDueDate());
            } else {

                // 过了账单日
                // 今天是还款日

                if (now.before(dueDate) || nowDate.equals(dueDateLocal)) {
                    if (creditCard.getRepayment() != null && creditCard.getBillAmount() != null
                            && creditCard.getRepayment().doubleValue() >= creditCard.getBillAmount().doubleValue()) {

                        // if (creditCard.getSource() == 1) {
                        // if (creditCard.getUserCardRelation() != null) {
                        // if (creditCard.getUserCardRelation().getDueType() ==
                        // 0) {
                        // return
                        // DateUtil.formatMMDD4(DateUtil.localDateToDate(DateUtil.dateToLocalDate(creditCard.getDueDate())
                        // .plusMonths(1)));
                        // } else {
                        // return
                        // DateUtil.formatMMDD4(DateUtil.localDateToDate(DateUtil.dateToLocalDate(creditCard.getEndDate())
                        // .plusMonths(1).plusDays(creditCard.getUserCardRelation().getDueDay())));
                        // }
                        // } else {
                        // return DateUtil.formatMMDD4(creditCard.getDueDate());
                        // }
                        //
                        // } else {
                        // if (creditCard.getBank() != null) {
                        // if (creditCard.getBank().getRepaymentCycle() == 0) {
                        // return
                        // DateUtil.formatMMDD4(DateUtil.localDateToDate(DateUtil.dateToLocalDate(creditCard.getDueDate())));
                        //
                        // } else {
                        // return
                        // DateUtil.formatMMDD4(DateUtil.localDateToDate(DateUtil.dateToLocalDate(creditCard.getEndDate())
                        // .plusMonths(1).plusDays(creditCard.getBank().getRepaymentCycle())));
                        // }
                        // } else {
                        // return DateUtil.formatMMDD4(creditCard.getDueDate());
                        // }
                        // }
                        return DateUtil.formatMMDD4(creditCard.getDueDate());

                    } else if (creditCard.getRepayment() != null && creditCard.getMinimum() != null
                            && creditCard.getRepayment().doubleValue() >= creditCard.getMinimum().doubleValue()) {
                        return DateUtil.formatMMDD4(creditCard.getDueDate());
                    }
                    // else {
                    // // 未出账单
                    // if (creditCard.getBillAmount() == null &&
                    // creditCard.getBillAmount().doubleValue() <= 0) {
                    // if (creditCard.getSource() == 1) {
                    // if (creditCard.getUserCardRelation() != null) {
                    // if (creditCard.getUserCardRelation().getDueType() == 0) {
                    // return
                    // DateUtil.formatMMDD4(DateUtil.localDateToDate(DateUtil.dateToLocalDate(creditCard
                    // .getDueDate())));
                    // } else {
                    // return
                    // DateUtil.formatMMDD4(DateUtil.localDateToDate(DateUtil.dateToLocalDate(
                    // creditCard.getDueDate()).plusDays(creditCard.getUserCardRelation().getDueDay())));
                    // }
                    // } else {
                    // return DateUtil.formatMMDD4(creditCard.getDueDate());
                    // }
                    //
                    // } else {
                    // if (creditCard.getBank() != null) {
                    // if (creditCard.getBank().getRepaymentCycle() == 0) {
                    // return
                    // DateUtil.formatMMDD4(DateUtil.localDateToDate(DateUtil.dateToLocalDate(creditCard
                    // .getDueDate())));
                    //
                    // } else {
                    // return
                    // DateUtil.formatMMDD4(DateUtil.localDateToDate(DateUtil.dateToLocalDate(
                    // creditCard.getDueDate()).plusDays(creditCard.getBank().getRepaymentCycle())));
                    // }
                    // } else {
                    // return DateUtil.formatMMDD4(creditCard.getDueDate());
                    // }
                    // }
                    // } else {
                    // return DateUtil.formatMMDD4(creditCard.getDueDate());
                    // }
                    //
                    // }
                } else {
                    if (creditCard.getRepayment() != null && creditCard.getBillAmount() != null
                            && creditCard.getRepayment().doubleValue() >= creditCard.getBillAmount().doubleValue()) {

                        if (creditCard.getSource() == 1) {
                            if (creditCard.getUserCardRelation() != null) {
                                if (creditCard.getUserCardRelation().getDueType() == 0) {
                                    return DateUtil.formatMMDD4(DateUtil.localDateToDate(
                                            DateUtil.dateToLocalDate(creditCard.getDueDate()).plusMonths(1)));
                                } else {
                                    return DateUtil.formatMMDD4(DateUtil.localDateToDate(
                                            DateUtil.dateToLocalDate(creditCard.getEndDate()).plusMonths(1)
                                                    .plusDays(creditCard.getUserCardRelation().getDueDay())));
                                }
                            } else {
                                return DateUtil.formatMMDD4(creditCard.getDueDate());
                            }

                        } else {
                            if (creditCard.getBank() != null) {
                                if (creditCard.getBank().getRepaymentCycle() == 0) {
                                    return DateUtil.formatMMDD4(DateUtil.localDateToDate(
                                            DateUtil.dateToLocalDate(creditCard.getDueDate()).plusMonths(1)));

                                } else {
                                    return DateUtil.formatMMDD4(
                                            DateUtil.localDateToDate(DateUtil.dateToLocalDate(creditCard.getEndDate())
                                                    .plusMonths(1).plusDays(creditCard.getBank().getRepaymentCycle())));
                                }
                            } else {
                                return DateUtil.formatMMDD4(creditCard.getDueDate());
                            }
                        }

                    } else if (creditCard.getRepayment() != null && creditCard.getMinimum() != null
                            && creditCard.getRepayment().doubleValue() >= creditCard.getMinimum().doubleValue()) {
                        return DateUtil.formatMMDD4(creditCard.getDueDate());
                    } else {
                        // 未出账单
                        if (creditCard.getBillAmount() == null && creditCard.getBillAmount().doubleValue() <= 0) {
                            if (creditCard.getSource() == 1) {
                                if (creditCard.getUserCardRelation() != null) {
                                    if (creditCard.getUserCardRelation().getDueType() == 0) {
                                        return DateUtil.formatMMDD4(DateUtil.localDateToDate(
                                                DateUtil.dateToLocalDate(creditCard.getDueDate()).plusMonths(1)));
                                    } else {
                                        return DateUtil.formatMMDD4(DateUtil
                                                .localDateToDate(DateUtil.dateToLocalDate(creditCard.getDueDate())
                                                        .plusDays(creditCard.getUserCardRelation().getDueDay())));
                                    }
                                } else {
                                    return DateUtil.formatMMDD4(creditCard.getDueDate());
                                }

                            } else {
                                if (creditCard.getBank() != null) {
                                    if (creditCard.getBank().getRepaymentCycle() == 0) {
                                        return DateUtil.formatMMDD4(DateUtil.localDateToDate(
                                                DateUtil.dateToLocalDate(creditCard.getDueDate()).plusMonths(1)));

                                    } else {
                                        return DateUtil.formatMMDD4(DateUtil
                                                .localDateToDate(DateUtil.dateToLocalDate(creditCard.getDueDate())
                                                        .plusDays(creditCard.getBank().getRepaymentCycle())));
                                    }
                                } else {
                                    return DateUtil.formatMMDD4(creditCard.getDueDate());
                                }
                            }
                        } else {
                            return DateUtil.formatMMDD4(creditCard.getDueDate());
                        }

                    }
                }

            }

        } else {
            if (creditCard.getSource() == 1) {
                if (creditCard.getUserCardRelation() != null) {
                    if (creditCard.getUserCardRelation().getDueType() == 0) {
                        return DateUtil.formatMMDD4(DateUtil
                                .localDateToDate(DateUtil.dateToLocalDate(creditCard.getDueDate()).plusMonths(1)));
                    } else {
                        return DateUtil
                                .formatMMDD4(DateUtil.localDateToDate(DateUtil.dateToLocalDate(creditCard.getDueDate())
                                        .plusDays(creditCard.getUserCardRelation().getDueDay())));
                    }
                } else {
                    return DateUtil.formatMMDD4(creditCard.getDueDate());
                }

            } else {
                if (creditCard.getBank() != null) {
                    if (creditCard.getBank().getRepaymentCycle() == 0) {
                        return DateUtil.formatMMDD4(DateUtil
                                .localDateToDate(DateUtil.dateToLocalDate(creditCard.getDueDate()).plusMonths(1)));

                    } else {
                        return DateUtil
                                .formatMMDD4(DateUtil.localDateToDate(DateUtil.dateToLocalDate(creditCard.getDueDate())
                                        .plusDays(creditCard.getBank().getRepaymentCycle())));
                    }
                } else {
                    return DateUtil.formatMMDD4(creditCard.getDueDate());
                }
            }
        }
        return DateUtil.formatMMDD4(creditCard.getDueDate());
    }

    /**
     * @Title: getSource
     * @Description: 还款类型
     * @param
     * @return
     */
    public Integer getSource() {
        return creditCard.getSource();
    }

    public String getYear() {
        return year;
    }

    public void setLastUpdatTime(String lastUpdatTime) {
        this.lastUpdatTime = lastUpdatTime;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
