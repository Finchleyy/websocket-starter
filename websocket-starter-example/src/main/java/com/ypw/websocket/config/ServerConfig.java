package com.ypw.websocket.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.ypw.websocket.handler.SocketExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 如果有特殊参数需要配置可以自己重新定义 server 参数
 *
 * @author hongmeng
 * @date 2022/5/30
 */
@Configuration
@ConditionalOnClass(SocketIOServer.class)
public class ServerConfig {
    @Autowired
    SocketExceptionHandler socketExceptionHandler;

    @Bean("customSocketIOServer")
    public SocketIOServer customSocketIOServer(SocketIOServer SocketIOServerTemplate) {
        com.corundumstudio.socketio.Configuration configuration = SocketIOServerTemplate.getConfiguration();
        configuration.setExceptionListener(socketExceptionHandler);
        return SocketIOServerTemplate;
    }

}
