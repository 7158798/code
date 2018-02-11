/**
 * 
 */
package com.pay.card.model.base;

import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;

/**
 * 
 * @author qiaohui
 *
 */
public enum EntityStatus {
    Normal(0), // 正常
    UserDel(1), // 被用户删除
    AuditDel(2), // 被管理员删除
    ;

    private final int value;

    /**
     * @param value
     */
    private EntityStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    private static final IntObjectMap<EntityStatus> map = new IntObjectHashMap<>();
    static {
        for (EntityStatus e : EntityStatus.values()) {
            map.put(e.getValue(), e);
        }
    }

    public static final EntityStatus fromValue(int status) {
        return map.get(status);
    }

    public static final <T extends HasStatus> boolean isNormal(T item) {
        return (item != null) && (item.status() == EntityStatus.Normal);
    }

    public static final <T extends HasStatus> boolean isNotNormal(T item) {
        return (item == null) || (item.status() != EntityStatus.Normal);
    }

}
