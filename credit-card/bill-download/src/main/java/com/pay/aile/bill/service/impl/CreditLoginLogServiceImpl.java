
/**
* @Title: CreditLoginLogServiceImpl.java
* @Package com.pay.aile.bill.service.impl
* @Description: TODO(用一句话描述该文件做什么)
* @author jing.jin
* @date 2017年12月12日
* @version V1.0
*/

package com.pay.aile.bill.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay.aile.bill.entity.CreditLoginLog;
import com.pay.aile.bill.mapper.CreditLoginLogMapper;
import com.pay.aile.bill.service.CreditLoginLogService;

/**
 * @ClassName: CreditLoginLogServiceImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年12月12日
 *
 */
@Service
public class CreditLoginLogServiceImpl implements CreditLoginLogService {
    @Autowired
    private CreditLoginLogMapper creditLoginLogMapper;

    /**
     *
     * @Title: saveCreditLoginLog
     * @Description: 保存
     * @param log
     * @return void 返回类型 @throws
     */
    @Override
    public void saveCreditLoginLog(CreditLoginLog log) {
        creditLoginLogMapper.insert(log);
    }
}
