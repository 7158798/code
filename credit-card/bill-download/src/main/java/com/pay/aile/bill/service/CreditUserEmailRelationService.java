
/**
* @Title: CreditUserEmailRelationService.java
* @Package com.pay.aile.bill.service
* @Description: TODO(用一句话描述该文件做什么)
* @author jing.jin
* @date 2017年11月30日
* @version V1.0
*/

package com.pay.aile.bill.service;

import java.util.List;

import com.pay.aile.bill.entity.CreditEmail;
import com.pay.aile.bill.entity.CreditUserEmailRelation;

/**
 * @ClassName: CreditUserEmailRelationService
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年11月30日
 *
 */

public interface CreditUserEmailRelationService {
    public List<CreditUserEmailRelation> findByEmail(Long emailId);

    public CreditUserEmailRelation findByUser(String userId, Long emailId);

    public List<CreditEmail> findEmailByUser(String userId);

    public CreditUserEmailRelation saveOrUpdate(CreditUserEmailRelation relation);
}
