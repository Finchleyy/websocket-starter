package com.ypw.websocketstarter.websocket;

import com.ypw.websocketstarter.config.TomcatWebsocketConfig;
import com.ypw.websocketstarter.handler.AbstractWebsocketHandler;
import com.ypw.websocketstarter.handler.MessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * @author hongmeng
 * @date 2022/5/24
 */
@Slf4j
@Component
@ConditionalOnClass(value = TomcatWebsocketConfig.class)
@ServerEndpoint(value = "/test/tomcat")
public class TomcatWebsocketHandler extends AbstractWebsocketHandler implements MessageHandler {
    @OnOpen
    @Override
    public void OnOpen(Session session) {
        log.info("OnOpen");
    }

    @OnMessage
    @Override
    public void OnMessage(Session session, String message) {
        log.info("OnMessage");

    }

    @OnClose
    @Override
    public void OnClose(Session session) {
        log.info("OnClose");

    }
}
