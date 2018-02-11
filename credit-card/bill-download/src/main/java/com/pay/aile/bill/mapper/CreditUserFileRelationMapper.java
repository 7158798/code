
/**
* @Title: CreditUserFileRelationMapper.java
* @Package com.pay.aile.bill.mapper
* @Description: TODO(用一句话描述该文件做什么)
* @author jing.jin
* @date 2017年11月30日
* @version V1.0
*/

package com.pay.aile.bill.mapper;

import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.pay.aile.bill.entity.CreditUserFileRelation;

/**
 * @ClassName: CreditUserFileRelationMapper
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年11月30日
 *
 */

public interface CreditUserFileRelationMapper extends BaseMapper<CreditUserFileRelation> {

    void batchInsert(List<CreditUserFileRelation> relationList);

    List<CreditUserFileRelation> findByFile(Long fileId);
}
