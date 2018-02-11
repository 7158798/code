package com.pay.aile.bill.config;

import java.util.Date;

import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baomidou.mybatisplus.mapper.MetaObjectHandler;
import com.pay.aile.bill.enums.CommonStatus;

/***
 * ComMetaObjectHandler.java
 *
 * @author shinelon
 *
 * @date 2017年11月2日
 *
 */
public class ComMetaObjectHandler extends MetaObjectHandler {
    private static final Logger logger = LoggerFactory.getLogger(ComMetaObjectHandler.class);

    private static final String COLUMN_UPDATE_DATE = "updateDate";
    private static final String COLUMN_CREATE_DATE = "createDate";
    private static final String COLUMN_STATUS = "status";

    @Override
    public void insertFill(MetaObject metaObject) {
        Object status = getFieldValByName(COLUMN_STATUS, metaObject);
        if (status == null) {
            setFieldValByName(COLUMN_STATUS, CommonStatus.AVAILABLE.value, metaObject);
        }
        Object createDate = getFieldValByName(COLUMN_CREATE_DATE, metaObject);
        if (createDate == null) {
            setFieldValByName(COLUMN_CREATE_DATE, new Date(), metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Object updateDate = getFieldValByName(COLUMN_UPDATE_DATE, metaObject);
        if (updateDate == null) {
            setFieldValByName(COLUMN_UPDATE_DATE, new Date(), metaObject);
        }
    }

}
