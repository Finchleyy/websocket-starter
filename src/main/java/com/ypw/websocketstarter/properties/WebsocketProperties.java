package com.ypw.websocketstarter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author hongmeng
 * @date 2022/5/24
 */
@Data
@ConfigurationProperties(prefix = "websocket")
public class WebsocketProperties {
    /**
     * endpoint
     */
    private String endpoint;
    /**
     * port
     */
    private Integer port;
}
