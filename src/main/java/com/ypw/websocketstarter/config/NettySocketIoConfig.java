package com.ypw.websocketstarter.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import com.ypw.websocketstarter.handler.SocketExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hongmeng
 */
@Configuration
public class NettySocketIoConfig {
    @Value("${websocket.port}")
    private Integer socketPort;
    @Autowired
    private SocketExceptionHandler socketExceptionHandler;

    @Bean
    public SocketIOServer getServer() {
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
            //TODO 认证
            return true;
        });
        //跨域请求
        config.setOrigin(null);
        //异常处理
        config.setExceptionListener(socketExceptionHandler);
        return new SocketIOServer(config);
    }

    /**
     * 用于扫描netty-socket的注解，比如 @OnConnect、@OnEvent
     */
    @Bean
    public SpringAnnotationScanner springAnnotationScanner() {
        return new SpringAnnotationScanner(getServer());
    }


}