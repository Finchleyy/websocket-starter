package com.ypw.websocketstarter.model;

import lombok.Data;

import java.io.Serializable;


/**
 * @author hongmeng
 */
@Data
public class SystemProperty implements Serializable {

    /**
     * 节点名称
     */
    private String nodeName;

}
