
/**
* @Title: StatusEnum.java
* @Package com.pay.card.enums
* @Description: TODO(用一句话描述该文件做什么)
* @author jing.jin
* @date 2017年12月6日
* @version V1.0
*/

package com.pay.card.enums;

/**
 * @ClassName: StatusEnum
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年12月6日
 *
 */

public enum StatusEnum {

    DISENABLE(0, "禁用"), ENABLE(1, "启用");
    public static StatusEnum get(String str) {
        for (StatusEnum e : values()) {
            if (e.toString().equals(str)) {
                return e;
            }
        }
        return null;
    }

    private Integer status;

    private String text;

    private StatusEnum(Integer status, String text) {
        this.status = status;
        this.text = text;
    }

    public Integer getStatus() {
        return status;
    }

    public String getText() {
        return text;
    }
}
