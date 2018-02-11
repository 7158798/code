package com.pay.card.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "credit_channel")
@Entity
public class CreditChannel extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -1798677921402802358L;
    /**
     * 渠道号
     */
    private Long channel;
    /**
     * 渠道名称
     */
    private String channelName;
    /**
     * 是否同步
     */
    private String isSynchronization;

    public Long getChannel() {
        return channel;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getIsSynchronization() {
        return isSynchronization;
    }

    public void setChannel(Long channel) {
        this.channel = channel;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public void setIsSynchronization(String isSynchronization) {
        this.isSynchronization = isSynchronization;
    }

    @Override
    public String toString() {
        return "CreditChannel [channel=" + channel + ", channelName=" + channelName + ", isSynchronization="
                        + isSynchronization + "]";
    }

}
