/**
 * @Title: CardDueDayTask.java
 * @Package com.pay.card.task
 * @Description: TODO(用一句话描述该文件做什么)
 * @author jing.jin
 * @date 2017年12月30日
 * @version V1.0
 */

package com.pay.card.task;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pay.card.enums.StatusEnum;
import com.pay.card.model.CreditCard;
import com.pay.card.model.TaskExecutionResult;
import com.pay.card.service.CreditCardService;
import com.pay.card.service.CreditUserCardRelationService;
import com.pay.card.service.TaskExecutionResultService;
import com.pay.card.utils.DateUtil;

/**
 * @ClassName: CardDueDayTask
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年12月30日
 */
@Component
public class CardDueDayTask {
    // 账单日
    private static final String CARD_DUE_DAY = "cardDueDay";
    private static final String LOCK_KEY = "lock";
    // 日志
    private static Logger logger = LoggerFactory.getLogger(CardDueDayTask.class);

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private CreditCardService creditCardService;

    @Autowired
    private CreditUserCardRelationService creditUserCardRelationService;
    @Autowired
    private TaskExecutionResultService taskExecutionResultService;

    public void saveTaskResult(StatusEnum status, String exeDescription) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            TaskExecutionResult result = new TaskExecutionResult();
            result.setCreateDate(new Date());
            result.setStatus(status.getStatus());
            result.setHost(host);
            result.setExeDescription(exeDescription);
            result.setTaskName("CardDueDayTask");
            taskExecutionResultService.save(result);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Scheduled(cron = ("0 */30 * * * ?"))
    // @Scheduled(cron = ("0/30 * * * * ?"))
    public void sendBillDayMessage() {
        boolean lock = false;
        List<CreditCard> cardList = null;
        try {

            lock = redisTemplate.opsForValue().setIfAbsent(CARD_DUE_DAY, LOCK_KEY);
            if (lock) {

                // 如果在执行任务的过程中，程序突然挂了，为了避免程序因为中断而造成一直加锁的情况产生，20分钟后，key值失效，自动释放锁，
                redisTemplate.expire(CARD_DUE_DAY, 10, TimeUnit.MINUTES);
                // 4天后
                LocalDate now = LocalDate.now().plusDays(-4);

                // DateTimeFormatter format =
                // DateTimeFormatter.ofPattern("yyyy-MMM-dd HH:mm:ss");
                // now.format(format);
                // CreditCard creditCard = new CreditCard();
                // creditCard.setBillDay(String.valueOf(day));
                cardList = creditCardService.findCreditCardListByDueDay(DateUtil.localDateToDate(now));
                if (CollectionUtils.isNotEmpty(cardList)) {

                    cardList.forEach(card -> {

                        // 账单金额
                        card.setBillAmount(new BigDecimal(0));
                        // 预借现金额度
                        card.setCash(new BigDecimal(0));
                        // 已消费金额
                        card.setConsumption(new BigDecimal(0));

                        // 最低还款额
                        card.setMinimum(new BigDecimal(0));
                    });
                }

                creditCardService.bathUpdateCreditCard(cardList);

                saveTaskResult(StatusEnum.ENABLE, "执行个数" + (cardList == null ? 0 : cardList.size()));
            }

        } catch (Exception e) {
            logger.error("{}", e);
            saveTaskResult(StatusEnum.DISENABLE, "执行失败个数" + (cardList == null ? 0 : cardList.size()));
        } finally {// 无论如何，最终都要释放锁

            // if (lock) {// 如果获取了锁，则释放锁
            redisTemplate.delete(CARD_DUE_DAY);
            // }
        }
    }
}
