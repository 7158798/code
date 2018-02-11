package com.pay.aile.bill.event;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pay.aile.bill.contant.Constant;
import com.pay.aile.bill.entity.CreditBill;
import com.pay.aile.bill.entity.CreditCard;
import com.pay.aile.bill.enums.BankCodeEnum;
import com.pay.aile.bill.mapper.CreditCardMapper;
import com.pay.aile.bill.model.AnalyzeParamsModel;
import com.pay.aile.bill.model.AnalyzeResult;
import com.pay.aile.bill.task.FileQueueRedisHandle;
import com.pay.aile.bill.utils.JedisClusterUtils;

@Component
public class AnalyzeStatusListener implements ApplicationListener<AnalyzeStatusEvent> {

    private static final String REDIS_KEY = "credit_redis_key_";
    public static List<String> clientCacheMethodList = null;
    Logger logger = LoggerFactory.getLogger(AnalyzeStatusListener.class);
    @Autowired
    private CreditCardMapper creditCardMapper;
    @Value("${client.cache.method}")
    private String clientCacheMethod = "";

    private Set<String> getKey(String userId, List<String> methodList) {
        Set<String> keyList = new HashSet<String>();
        String date = LocalDate.now().toString().replaceAll("-", "");
        if (StringUtils.hasText(userId)) {
            for (String method : methodList) {
                String key = "";
                key = REDIS_KEY + userId + method + date;
                keyList.add(key);
            }
        }
        return keyList;
    }

    @Override
    public void onApplicationEvent(AnalyzeStatusEvent event) {
        // logger.info("AnalyzeStatusListener================{}", event);
        if (event.getApm() == null) {
            return;
        }

        AnalyzeParamsModel apm = event.getApm();
        if (apm == null || !apm.getIsNew()) {
            return;
        }
        Exception error = apm.getError();
        if (error == null && apm.getResult() != null) {
            // 解析成功
            AnalyzeResult ar = apm.getResult();
            List<CreditCard> cardList = ar.getCardList();
            if (cardList != null && !cardList.isEmpty()) {
                String key = String.format(Constant.REDIS_EXISTS_BANK_CARD, apm.getEmail(), apm.getUserId());
                Set<String> existNumbers = JedisClusterUtils.setMembers(key);

                for (CreditCard card : cardList) {
                    JSONObject cardJson = new JSONObject();
                    // 获取需要展示的卡号
                    if (StringUtils.hasText(card.getCompleteNumbers())) {
                        String uniqueKey = card.getBankId() + card.getNumbers() + card.getCardholder();
                        String cardNo = card.getCompleteNumbers().substring(card.getCompleteNumbers().length() - 4,
                                card.getCompleteNumbers().length());
                        if (existNumbers == null || existNumbers.isEmpty() || !existNumbers.contains(uniqueKey)) {
                            cardJson.put("cardNo", cardNo);
                            cardJson.put("cardholder", card.getCardholder());
                            cardJson.put("bankCode", apm.getBankCode());
                            cardJson.put("bankName", BankCodeEnum.getByBankCode(apm.getBankCode()).getShortName());
                            cardJson.put("shortName", BankCodeEnum.getByBankCode(apm.getBankCode()).getShortName());
                            JedisClusterUtils.setSave(Constant.REDIS_CARDS + apm.getEmail() + Constant.WORD_SEPARATOR + apm.getUserId(),
                                    cardJson.toJSONString());
                        }
                    }
                }
            }
        }
        if (apm != null) {
            // credit_card_analyzed_status_+email+userId
            long analyzedNum = JedisClusterUtils.incrBy(
                    Constant.REDIS_ANALYZED_STATUS + apm.getEmail() + Constant.WORD_SEPARATOR + apm.getUserId(), 1);
            String downloadNumStr = JedisClusterUtils.getString(Constant.REDIS_ANALYSIS_STATUS + apm.getEmail() + Constant.WORD_SEPARATOR
                    + apm.getUserId());
            long downloadNum = 0;
            if (StringUtils.hasText(downloadNumStr)) {
                try {
                    downloadNum = Long.valueOf(downloadNumStr);
                } catch (NumberFormatException e) {
                    logger.error("number format error!downloadNumStr={}", downloadNumStr);
                }
            }
            JedisClusterUtils.delKey(FileQueueRedisHandle.MAIL_FILE_LIST_CONTENT + apm.getFileName());
            // 删除client查询缓存
            if (apm.getUserId() != null) {
                Set<String> keyList = getKey(apm.getUserId() + "", clientCacheMethodList);
                if (CollectionUtils.isNotEmpty(keyList)) {
                    JedisClusterUtils.delete(keyList);
                }
            }
            logger.debug("AnalyzeStatusListener=======email={},userId={},downloadNum={},analyzedNum={}", apm.getEmail(), apm.getUserId(),
                    downloadNum, analyzedNum);
            if (downloadNum <= analyzedNum) {
                Set<String> billStr = JedisClusterUtils.setMembers(Constant.ANALYZED_BILL + apm.getEmail() + Constant.WORD_SEPARATOR
                        + apm.getUserId());
                logger.debug("AnalyzeStatusListener=======email={},userId={},billStr={}", apm.getEmail(), apm.getUserId(), billStr);
                Map<Long, List<CreditBill>> billsMap = new HashMap<>();
                if (billStr != null && !billStr.isEmpty()) {
                    billsMap = billStr.stream().map(s -> {
                        return JSON.parseObject(s, CreditBill.class);
                    }).collect(Collectors.groupingBy(CreditBill::getCardId));
                    logger.debug("AnalyzeStatusListener=======email={},userId={},billsMap={}", apm.getEmail(), apm.getUserId(), billsMap);
                    if (billsMap != null && !billsMap.isEmpty()) {
                        billsMap.entrySet()
                                .stream()
                                .forEach(
                                        map -> {
                                            Long cardId = map.getKey();
                                            List<CreditBill> bills = map.getValue();
                                            CreditBill maxBill = bills.stream().max(new Comparator<CreditBill>() {
                                                @Override
                                                public int compare(CreditBill o1, CreditBill o2) {
                                                    int year1 = Integer.valueOf(o1.getYear());
                                                    int year2 = Integer.valueOf(o2.getYear());
                                                    int month1 = Integer.valueOf(o1.getMonth());
                                                    int month2 = Integer.valueOf(o2.getMonth());
                                                    if (year1 > year2) {
                                                        return 1;
                                                    } else if (year1 == year2) {
                                                        if (month1 > month2) {
                                                            return 1;
                                                        } else if (month1 == month2) {
                                                            return 0;
                                                        } else {
                                                            return -1;
                                                        }
                                                    } else {
                                                        return -1;
                                                    }
                                                }
                                            }).get();
                                            logger.debug("AnalyzeStatusListener=======email={},userId={},maxBill={}", apm.getEmail(),
                                                    apm.getUserId(), maxBill);
                                            if (maxBill != null) {
                                                CreditCard card = new CreditCard();
                                                card.setId(cardId);
                                                card.setBillDay(maxBill.getBillDay());
                                                card.setDueDay(maxBill.getCardDueDay());
                                                card.setDueDate(maxBill.getCardDueDate());
                                                card.setBeginDate(maxBill.getCardBeginDate());
                                                card.setEndDate(maxBill.getCardEndDate());
                                                card.setIntegral(maxBill.getIntegral());
                                                card.setCredits(maxBill.getCredits());
                                                creditCardMapper.updateById(card);
                                            }
                                        });
                    }
                }

            }
        }
    }

    @PostConstruct
    public void setRelation() {
        String[] array = clientCacheMethod.split(",");
        clientCacheMethodList = Arrays.asList(array);
    }

    // private void updateIntegral(List<CreditCard> cardList) {
    // creditCardMapper.updateIntegral(cardList);
    // }
}
