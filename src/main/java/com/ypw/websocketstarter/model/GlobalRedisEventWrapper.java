package com.ypw.websocketstarter.model;


import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;


/**
 * @author hongmeng
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GlobalRedisEventWrapper implements Serializable {
    /**
     * 全局客户端标识
     */
    private String globalSessionId;

    /**
     * node 节点客户端标识
     */
    private String nodeSessionId;
    /**
     * 要发送的内容对象
     */
    private Object data;
    /**
     * 事件名称
     */
    private String eventName;
    /**
     * 事件类型
     */
    private String eventType;
    /**
     * 重试次数
     */
    private Integer retryCount;
    /**
     * 租户 ID
     */
    private Long tenantId;

    public GlobalRedisEventWrapper(String globalSessionId, Object data, String messageType) {
        this.globalSessionId = globalSessionId;
        this.data = data;
        this.eventName = messageType;
    }
}
