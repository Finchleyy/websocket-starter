package com.ypw.websocketstarter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author hongmeng
 */
@Data
@ConfigurationProperties(prefix = "websocket.time")
public class ConfigProperties {

    /**
     * session 最大存活时间ms
     */
    private Long sessionIdMaxAlive = 8 * 60 * 1000L;

    /**
     * 当redis队列中没有数据的时候，线程休息的时间，单位ms
     */
    private Long stopPullMsgInterval = 100L;

}