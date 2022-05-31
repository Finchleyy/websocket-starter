/*
 * Message.java
 * Copyright 2020 Qunhe Tech, all rights reserved.
 * Qunhe PROPRIETARY/CONFIDENTIAL, any form of usage is subject to approval.
 */

package com.ypw.websocketstarter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Function: 消息实体
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Message implements Serializable {
    /**
     * 消息Id，唯一标识
     */
    private String id;
    /**
     * 房间Id
     */
    private String roomId;
    /**
     * 房间id，外部传入
     */
    private String obsSessionId;
    /**
     * 消息类型
     */
    private Integer type;
    /**
     * 用户类型
     */
    private Integer userType;
    /**
     * 用户Id不需要外部传入
     */
    private String userId;
    /**
     * 全局sessionId
     */
    private String globalSessionId;
    /**
     * 本机 sessionId
     */
    private String nodeSessionId;
    /**
     * 机器节点标识
     */
    private String nodeName;
    /**
     * 租户 ID
     */
    private Long tenantId;
}
