package com.pay.card.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay.card.dao.CreditChannelDao;
import com.pay.card.model.CreditChannel;
import com.pay.card.service.CreditChannelService;

@Service
public class CreditChannelServiceImpl implements CreditChannelService {

    @Autowired
    private CreditChannelDao creditChannelDao;

    @Override
    public CreditChannel saveCreditChannel(CreditChannel creditChannel) throws Exception {

        creditChannelDao.saveAndFlush(creditChannel);
        Long id = creditChannel.getId();
        creditChannel.setChannel(1000 + id);
        creditChannelDao.save(creditChannel);
        return creditChannel;
    }

}
