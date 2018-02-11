/**
 * 
 */
package com.pay.card.model.base;

/**
 * @author qiaohui
 * 
 */
public interface HasStatus {

    public int getStatus();

    public default EntityStatus status() {
        return EntityStatus.fromValue(getStatus());
    }
}
