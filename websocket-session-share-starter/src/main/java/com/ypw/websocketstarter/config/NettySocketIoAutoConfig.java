package com.ypw.websocketstarter.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import com.corundumstudio.socketio.protocol.JacksonJsonSupport;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.ypw.websocketstarter.cache.RedisSessionCacheHelper;
import com.ypw.websocketstarter.properties.ConfigProperties;
import com.ypw.websocketstarter.properties.WebsocketProperties;
import com.ypw.websocketstarter.service.GlobalSessionService;
import com.ypw.websocketstarter.service.impl.GlobalSessionServiceImpl;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hongmeng
 */
@EnableConfigurationProperties({ConfigProperties.class, WebsocketProperties.class})
@Configuration
public class NettySocketIoAutoConfig {
    @Value("${websocket.port}")
    private Integer socketPort;

    @Bean("SocketIOServerTemplate")
    public SocketIOServer SocketIOServerTemplate() {
        //netty-socket io服务器
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        //端口
        config.setPort(socketPort);
        //心跳间隔
        config.setPingInterval(10 * 1000);
        //心跳超时时间
        config.setPingTimeout(20 * 1000);
        //最大每帧处理长度
        config.setMaxFramePayloadLength(64 * 1024);
        //http 内容长度
        config.setMaxHttpContentLength(64 * 1024);
        //认证
        config.setAuthorizationListener(data -> {
            //认证
            return true;
        });
        //跨域请求
        config.setOrigin(null);
        //异常处理,这个使用方自行注入
        //config.setExceptionListener(socketExceptionHandler);
        JacksonJsonSupport jacksonJsonSupport = new JacksonJsonSupport(new Jdk8Module());
        config.setJsonSupport(jacksonJsonSupport);
        return new SocketIOServer(config);
    }

    /**
     * 用于扫描netty-socket的注解，比如 @OnConnect、@OnEvent
     */
    @Bean
    public SpringAnnotationScanner springAnnotationScanner() {
        return new SpringAnnotationScanner(SocketIOServerTemplate());
    }

    @Bean
    @ConditionalOnClass(RedissonClient.class)
    public RedisSessionCacheHelper redisSessionCacheHelper() {
        return new RedisSessionCacheHelper();
    }

    @Bean
    @ConditionalOnClass({RedisSessionCacheHelper.class, SystemPropertyAutoConfig.class})
    public GlobalSessionService globalSessionService() {
        return new GlobalSessionServiceImpl();
    }


}