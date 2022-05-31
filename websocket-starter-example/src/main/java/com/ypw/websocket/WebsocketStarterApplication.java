package com.ypw.websocket;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * 启动配置类
 */
@SpringBootApplication
@EnableAsync
public class WebsocketStarterApplication implements CommandLineRunner {
    @Resource(name = "customSocketIOServer")
    private SocketIOServer socketIoServer;

    public static void main(String[] args) {
        SpringApplication.run(WebsocketStarterApplication.class, args);
    }

    @Override
    public void run(String... args) {
        socketIoServer.start();
    }

    @PreDestroy
    public void shutdown() {
        //sessionRoomEventLoop.stop();
        //缓存清理
        //服务器关闭
        socketIoServer.stop();
    }
}
