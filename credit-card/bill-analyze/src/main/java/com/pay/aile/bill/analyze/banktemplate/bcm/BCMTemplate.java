package com.pay.aile.bill.analyze.banktemplate.bcm;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.pay.aile.bill.analyze.banktemplate.BaseBankSeparateStringTemplate;
import com.pay.aile.bill.config.TemplateCache;
import com.pay.aile.bill.contant.Constant;
import com.pay.aile.bill.entity.CreditBill;
import com.pay.aile.bill.entity.CreditBillDetail;
import com.pay.aile.bill.entity.CreditCard;
import com.pay.aile.bill.entity.CreditTemplate;
import com.pay.aile.bill.enums.CardTypeEnum;
import com.pay.aile.bill.exception.AnalyzeBillException;
import com.pay.aile.bill.model.AnalyzeParamsModel;
import com.pay.aile.bill.utils.DateUtil;
import com.pay.aile.bill.utils.PatternMatcherUtil;

/**
 *
 * @author Charlie
 * @description 交通银行信用卡账单内容解析模板
 */
@Service
public class BCMTemplate extends BaseBankSeparateStringTemplate implements AbstractBCMTemplate {
    public static String parseHtml(String html, String... tagName) {

        Document document = Jsoup.parse(html);

        // 处理还款和消费的问题
        Elements elements = document.getElementsByTag("table");
        boolean flag = true;
        for (int j = 0; j < elements.size(); j++) {
            Element table = elements.get(j);
            if (flag && table.attr("id").equals("table3")) {
                Elements trs = table.getElementsByTag("tbody").get(0).getElementsByTag("tr");
                for (int k = 2; k < trs.size(); k++) {
                    Elements tds = trs.get(k).getElementsByTag("td");
                    Element element3 = tds.get(3);
                    String text = element3.text();

                    text = text.replaceAll("\\s+", "-");
                    element3.text(text);
                    Element element4 = tds.get(4);

                    text = element4.text();
                    text = text.replaceAll("\\s+", "-");
                    element4.text(text);
                }
                flag = false;

            }
        }

        if (tagName != null && tagName.length > 0) {
            for (int i = 0; i < tagName.length; i++) {
                elements = document.getElementsByTag(tagName[i]);

                for (int j = 0; j < elements.size(); j++) {
                    Element element = elements.get(j);
                    // elements.forEach(e -> {

                    // td需要特殊处理
                    if ("td".equals(tagName[i])) {

                        Elements childElements = element.getElementsByTag(tagName[i]);

                        if (childElements != null && childElements.size() > 1) {
                            continue;
                        }
                    }

                    String text = element.text();

                    text = text.replaceAll("\\s+", "");
                    element.text(text);

                    // });
                }
            }

        }

        html = document.toString();
        html = html.replaceAll("(?is)<!DOCTYPE.*?>", ""); // remove html top
                                                          // infomation
        html = html.replaceAll("(?is)<!--.*?-->", ""); // remove html comment
        html = html.replaceAll("(?is)<script.*?>.*?</script>", ""); // remove
                                                                    // javascript
        html = html.replaceAll("(?is)<style.*?>.*?</style>", ""); // remove css
        html = html.replaceAll("(?is)<.*?>", "");
        html = html.replaceAll("&nbsp;", ""); // remove &nbsp;
        html = html.replaceAll("\n", "");// remove \n
        html = html.replaceAll("$", "");// 去掉美元符号
        html = html.replaceAll("＄", "");
        html = html.replaceAll("￥", "");// 去掉人民币符号
        html = html.replaceAll("¥", "");
        html = html.replaceAll(",", "");// 去掉金额分隔符
        html = html.replaceAll(" {2,}", " ");// 去掉多余空格，只留一个
        return html;
    }

    public String getMatcherString(String reg, String content) {
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    @Override
    public void initRules() {
        super.initRules();
        if (rules == null) {
            rules = new CreditTemplate();
            rules.setCardtypeId(7L);
            rules.setCardholder("尊敬的 [\\u4e00-\\u9fa5]+(女士|先生)");
            rules.setCardNumbers("卡号(:|：) *\\d+\\*{6}\\d{4}");
            rules.setCycle(" 账单周期：\\d{4}/\\d{2}/\\d{2}-\\d{4}/\\d{2}/\\d{2}");
            rules.setDueDate("到期还款日 \\d{4}/\\d{2}/\\d{2}");
            rules.setCurrentAmount("本期应还款额 -?\\d+\\.?\\d*");
            rules.setMinimum("最低还款额 \\d+\\.?\\d*");
            rules.setCredits("信用额度 \\d+.?\\d*");
            rules.setCash("取现额度 \\d+\\.?\\d*");
            rules.setIntegral("人民币账户 \\d+");
            rules.setYearMonth("以下是您\\d{4}年\\d{2}月份的信用卡电子账单");
            rules.setDetails("\\d{4}/\\d{2}/\\d{2} \\d{4}/\\d{2}/\\d{2} \\S+ RMB-?\\d+\\.?\\d* RMB-?\\d+\\.?\\d*");
            rules.setTransactionDate("0");
            rules.setBillingDate("1");
            rules.setTransactionDescription("2");
        }
    }

    @Override
    protected void analyzeCurrentAmount(List<CreditBill> billList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getCurrentAmount())) {
            List<String> currentAmountList = getValueListByPattern("currentAmount", content, rules.getCurrentAmount(),
                    "");
            currentAmountList = PatternMatcherUtil.getMatcher(Constant.pattern_amount, currentAmountList);
            if (!currentAmountList.isEmpty()) {
                currentAmountList.stream().map(item -> {
                    if (item.startsWith("-")) {
                        item = "0";
                        return item;
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

    @Override
    protected void analyzeCycle(List<CreditBill> billList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getCycle())) {
            String cycle = getValueByPattern("cycle", content, rules.getCycle(), apm, "：");
            String[] sa = cycle.split("-");
            billList.forEach(bill -> {
                bill.setBeginDate(DateUtil.parseDate(sa[0]));
                bill.setEndDate(DateUtil.parseDate(sa[1]));
            });
        }
    }

    @Override
    protected void analyzeDueDate(List<CreditBill> billList, String content, AnalyzeParamsModel apm) {
        super.analyzeDueDate(billList, content, apm);
        int repaymentCycle = TemplateCache.bankRepaymentCache.get(apm.getBankCode());
        billList.forEach(bill -> {
            if (bill.getDueDate() == null && bill.getEndDate() != null) {
                LocalDate endDate = DateUtil.dateToLocalDate(bill.getEndDate());
                endDate = endDate.plusDays(repaymentCycle + 1);
                bill.setDueDate(DateUtil.localDateToDate(endDate));
            }
        });
    }

    @Override
    protected void analyzeIntegral(List<CreditCard> cardList, List<CreditBill> billList, String content,
            AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getIntegral())) {
            String integral = getValueByPattern1("integral", content, rules.getIntegral(), apm, " ");
            final String finalIntegral = getMatcherString("\\d+\\.?\\d*", integral);
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

    @Override
    protected void analyzeYearMonth(List<CreditBill> billList, String content, AnalyzeParamsModel apm) {
        if (StringUtils.hasText(rules.getYearMonth())) {
            String yearMonth = getValueByPattern("yearMonth", content, rules.getYearMonth(), apm, "");
            yearMonth = yearMonth.replaceAll("年|月|-|/", "");
            yearMonth = PatternMatcherUtil.getMatcherString("\\d{6}", yearMonth);
            if (StringUtils.hasText(yearMonth)) {
                String year = yearMonth.substring(0, 4);
                String month = yearMonth.substring(4);
                billList.forEach(bill -> {
                    bill.setYear(year);
                    bill.setMonth(month);
                });
            }
        }
    }

    @Override
    protected void checkCardAndBill(AnalyzeParamsModel apm) throws AnalyzeBillException {
        if (apm.getResult().getCardList().isEmpty()) {
            throw new AnalyzeBillException("未抓取到卡号");
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
    }

    @Override
    protected void extractBillContent(AnalyzeParamsModel apm) {
        String content = parseHtml(apm.getOriginContent(), defaultExtractTag);
        // logger.info("extractBillContent============================={}",
        // content);
        apm.setContent(content);
    }

    protected String getValueByPattern1(String key, String content, String ruleValue, AnalyzeParamsModel apm,
            String splitSign) {

        if (StringUtils.hasText(ruleValue)) {

            List<String> list = PatternMatcherUtil.getMatcher(ruleValue, content);
            if (list.isEmpty()) {
                // handleNotMatch(key, rules.getDueDate(), apm);
                return "";
            }
            // 判断是否有积分
            if (list.size() > 1) {
                String result = list.get(1);
                if ("".equals(splitSign)) {
                    return result;
                } else {
                    String[] sa = result.split(splitSign);
                    String value = sa[sa.length - 1];
                    return value;
                }

            }

        }
        return "";
    }

    @Override
    protected int indexOfCardDetail(String content, String cardNo) {
        return content.indexOf("主卡卡号末四位" + cardNo.substring(cardNo.lastIndexOf("*") + 1));
    }

    @Override
    protected void setCardType() {
        cardType = CardTypeEnum.BCM_DEFAULT;
    }

    @Override
    protected void setField(CreditBillDetail cbd, int index, String value) {
        value = value.replaceAll("([A-Za-z]{3})(-?\\d+\\.?\\d*)", "$1@$2");
        String[] s = value.split("@");
        if (index == 3) {
            cbd.setTransactionCurrency(s[0]);
            cbd.setTransactionAmount(s[1]);
        }
        if (index == 4) {
            cbd.setAccountType(s[0]);
            cbd.setAccountableAmount(s[1]);
        }
    }

}
