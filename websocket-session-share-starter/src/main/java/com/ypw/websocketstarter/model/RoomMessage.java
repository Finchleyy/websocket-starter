/*
 * Message.java
 * Copyright 2020 Qunhe Tech, all rights reserved.
 * Qunhe PROPRIETARY/CONFIDENTIAL, any form of usage is subject to approval.
 */

package com.ypw.websocketstarter.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Function: 消息实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RoomMessage extends Message implements Serializable {
    /**
     * 消息内容
     */
    private String content;
}
