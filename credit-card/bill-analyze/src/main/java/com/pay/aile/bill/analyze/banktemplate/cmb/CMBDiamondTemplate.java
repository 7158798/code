
/**
* @Title: CMBDiamondTemplate.java
* @Package com.pay.aile.bill.analyze.banktemplate.cmb
* @Description: TODO(用一句话描述该文件做什么)
* @author jing.jin
* @date 2018年1月30日
* @version V1.0
*/

package com.pay.aile.bill.analyze.banktemplate.cmb;

import java.time.LocalDate;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.pay.aile.bill.analyze.banktemplate.BaseBankTemplate;
import com.pay.aile.bill.entity.CreditBill;
import com.pay.aile.bill.entity.CreditBillDetail;
import com.pay.aile.bill.entity.CreditCard;
import com.pay.aile.bill.entity.CreditTemplate;
import com.pay.aile.bill.enums.CardTypeEnum;
import com.pay.aile.bill.model.AnalyzeParamsModel;
import com.pay.aile.bill.utils.DateUtil;

/**
 * @ClassName: CMBDiamondTemplate
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2018年1月30日
 *
 */
@Service
public class CMBDiamondTemplate extends BaseBankTemplate implements AbstractCMBTemplate {

    private final String fillTransDate = "1234";

    @Override
    public void initRules() {
        super.initRules();
        if (rules == null) {
            rules = new CreditTemplate();
            rules.setCardtypeId(25L);
            rules.setCardholder("尊敬的[\\u4e00-\\u9fa5]+");
            rules.setCycle("账单周期  \\d{4}/\\d{2}/\\d{2}-\\d{4}/\\d{2}/\\d{2}");
            rules.setCurrentAmount("本期还款总额 NewBalance -?\\d+\\.?\\d*");
            rules.setMinimum("本期最低还款额 Min.Payment -?\\d+\\.?\\d*");
            rules.setDueDate("到期还款日 \\d{4}/\\d{2}/\\d{2}");
            rules.setIntegral("本期兑换积分总数 \\d+\\");
            rules.setDetails("\\d{4} \\d{4} \\S+ -?\\d+\\.?\\d* \\d{4} [a-zA-Z]+ -?\\d+\\.?\\d*");
            rules.setTransactionDescription("2");
            rules.setAccountableAmount("3");
            rules.setTransactionAmount("6");
            rules.setTransactionCurrency("5");
            rules.setCardNumbers("4");
        }
    }

    private String parseHtml(String html, String... tagName) {
        Document document = Jsoup.parse(html);

        if (tagName != null && tagName.length > 0) {
            for (int i = 0; i < tagName.length; i++) {
                Elements elements = document.getElementsByTag(tagName[i]);

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

                    if (" ".equals(text)) {
                        int currentcyIndex = element.parent().parent().parent().childNodeSize() == 9 ? 7 : 6;
                        if (element.parent().parent().siblingIndex() == 1) {
                            text = fillTransDate;
                        } else if (element.parent().parent().siblingIndex() == currentcyIndex) {
                            text = "CN";
                        } else if (element.parent().parent().siblingIndex() == (currentcyIndex + 1)) {
                            text = "0";
                        }
                    }
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

    @Override
    protected void extractBillContent(AnalyzeParamsModel apm) {
        String content = parseHtml(apm.getOriginContent(), "font");
        apm.setContent(content);
    }

    @Override
    protected LocalDate getThisDueDate(CreditCard card, CreditBill bill, AnalyzeParamsModel apm) {
        // 账单日：02、03、05、06、07、08、09、10、12、14、15、16、17、18、19、20、21、22、25
        // 还款日：20、21、23、24、25、26、27、28、01、02、03、04、05、06、07、08、09、10、13
        if (!StringUtils.hasText(card.getBillDay()) || bill.getDueDate() == null || card.getEndDate() == null) {
            return LocalDate.now();
        }
        int billDay = Integer.valueOf(card.getBillDay());
        int dueDay = DateUtil.dateToLocalDate(bill.getDueDate()).getDayOfMonth();

        LocalDate endDate = DateUtil.dateToLocalDate(card.getEndDate());
        // 账单日和还款日在同一个月
        LocalDate dueDate = LocalDate.of(endDate.getYear(), endDate.getMonth(), dueDay);
        if (billDay > 10) {
            // 还款日在账单日的下一个月
            dueDate = dueDate.plusMonths(1);
        }
        return dueDate;
    }

    @Override
    protected void setCardType() {
        cardType = CardTypeEnum.CMB_DIAMON;
    }

    @Override
    protected void setField(CreditBillDetail cbd, int index, String value) {
        if (index == 0 || index == 1) {
            if (value.equals(fillTransDate)) {
                return;
            }
            String month = value.substring(0, 2);
            String year = DateUtil.getBillYearByMonth(month);
            value = year + value;
            if (index == 0) {
                cbd.setTransactionDate(DateUtil.parseDate(value));
            } else if (index == 1) {
                cbd.setBillingDate(DateUtil.parseDate(value));
                if (cbd.getTransactionDate() == null) {
                    cbd.setTransactionDate(cbd.getBillingDate());
                }
            }

        }
    }
}
