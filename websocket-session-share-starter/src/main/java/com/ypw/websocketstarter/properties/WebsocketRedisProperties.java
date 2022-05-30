package com.ypw.websocketstarter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author hongmeng
 * @date 2022/5/24
 */
@Data
@ConfigurationProperties(prefix = "websocket.redis")
public class WebsocketRedisProperties {
    /**
     * host
     */
    private String host;
    /**
     * database
     */
    private Integer database;
    /**
     * password
     */
    private String password;
    /**
     * username
     */
    private String username;
}
