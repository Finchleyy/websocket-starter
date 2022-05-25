package com.ypw.websocketstarter.config;

import com.ypw.websocketstarter.model.SystemProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.util.UUID;

/**
 * @author hongmeng
 */
@Slf4j
@Configuration
public class SystemPropertyConfig {

    @Bean
    public SystemProperty systemProperty() {
        SystemProperty systemProperty = new SystemProperty();
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            systemProperty.setNodeName(localHost.getHostName());
            log.info("SystemPropertyConfig==========>{}", localHost.getHostName());
            log.info("SystemPropertyConfig==========>{}", localHost.getCanonicalHostName());
            log.info("SystemPropertyConfig==========>{}", localHost.getHostAddress());
            log.info("SystemPropertyConfig==========>{}", localHost.getAddress());
        } catch (Exception e) {
            log.error("SystemPropertyConfig error:{}", e.getMessage());
            systemProperty.setNodeName(UUID.randomUUID().toString());
        }
        return systemProperty;
    }
}