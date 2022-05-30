//package com.ypw.websocketstarter.config;
//
//import com.ypw.websocketstarter.properties.WebsocketProperties;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//import org.springframework.web.socket.server.standard.ServerEndpointExporter;
//
//@EnableConfigurationProperties(value = WebsocketProperties.class)
//@Configuration
//public class TomcatWebsocketConfig extends WebMvcConfigurerAdapter {
//    @Bean
//    public ServerEndpointExporter serverEndpointExporter() {
//        ServerEndpointExporter serverEndpointExporter = new ServerEndpointExporter();
//        return serverEndpointExporter;
//    }
//}