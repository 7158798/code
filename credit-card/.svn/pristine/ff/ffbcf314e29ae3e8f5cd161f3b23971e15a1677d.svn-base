/**
 * 
 */
package com.pay.card.model.base;

import com.pay.card.utils.UuidUtils;

/**
 * @author qiaohui
 *
 */
public interface HasUuid<N extends Number> extends HasId<N> {

    public default String getUuid() {
        if (getId() == null) {
            throw new NullPointerException();
        }
        if (getId() instanceof Integer) {
            return UuidUtils.getUuid(getId().intValue());
        }
        if (getId() instanceof Long) {
            return UuidUtils.getUuid(getId().longValue());
        }
        throw new IllegalArgumentException("no support uuid:" + getId().getClass());
    }
}
