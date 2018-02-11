package com.pay.card.service;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay.card.BaseTest;
import com.pay.card.dao.CreditCardDao;
import com.pay.card.model.CreditCard;

public class CreditCardServiceImplTest extends BaseTest {

    @Autowired
    private CreditCardService cardService;

    @Autowired
    private CreditCardDao creditCardDao;

    @Test
    public void findTest() {
        try {
            CreditCard creditCard = new CreditCard();
            creditCard.setUserId(1l);
            List<CreditCard> list = creditCardDao.findCreditCard(1l);
            // List<CreditCard> list = cardService.findCreditCardList(creditCard);
            for (CreditCard creditCard2 : list) {
                System.out.println(creditCard2);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Test
    public void updateRePayMentTest() {
        // cardService.updateCardRePayMent(2383l, 3l, new BigDecimal(100));
    }

    @Test
    public void updateStatusTest() {
        // cardService.updateCardStatusById(2383l);
        // cardService.updateCardStatusById(2392l, 1l);

    }

    @Test
    public void updateTest() {
        // cardService.updateCardNameById("购物卡", 2383l);
    }

}
