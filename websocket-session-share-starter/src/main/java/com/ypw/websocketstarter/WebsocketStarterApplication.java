//package com.ypw.websocketstarter;
//
//import com.corundumstudio.socketio.SocketIOServer;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.scheduling.annotation.EnableAsync;
//
//import javax.annotation.PreDestroy;
//
//@SpringBootApplication
//@EnableAsync
//public class WebsocketStarterApplication implements CommandLineRunner {
//    @Autowired
//    private SocketIOServer socketIoServer;
//
//    public static void main(String[] args) {
//        SpringApplication.run(WebsocketStarterApplication.class, args);
//    }
//
//    @Override
//    public void run(String... args) {
//        socketIoServer.start();
//    }
//
//    @PreDestroy
//    public void shutdown() {
//        //sessionRoomEventLoop.stop();
//        //缓存清理
//        //服务器关闭
//        socketIoServer.stop();
//    }
//}
