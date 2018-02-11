
/**
* @Title: CreditUserEmailRelationMapper.java
* @Package com.pay.aile.bill.mapper
* @Description: TODO(用一句话描述该文件做什么)
* @author jing.jin
* @date 2017年11月29日
* @version V1.0
*/

package com.pay.aile.bill.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.pay.aile.bill.entity.CreditEmail;
import com.pay.aile.bill.entity.CreditUserEmailRelation;

/**
 * @ClassName: CreditUserEmailRelationMapper
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年11月29日
 *
 */

public interface CreditUserEmailRelationMapper extends BaseMapper<CreditUserEmailRelation> {
    public List<CreditEmail> findEmailByUser(String userId);

    public List<Map<String, Object>> findNewCardList(CreditEmail email);

    public void updateBillStatus(CreditEmail email);

    public void updateCardStatus(CreditEmail email);

    public void updateStatus(CreditEmail email);

}