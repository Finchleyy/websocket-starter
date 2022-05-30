package com.ypw.websocket.handler;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.google.common.collect.Lists;
import com.ypw.websocketstarter.cache.RedisSessionCacheHelper;
import com.ypw.websocketstarter.model.Message;
import com.ypw.websocketstarter.model.SystemProperty;
import com.ypw.websocketstarter.service.GlobalSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * 房间事件Handler
 *
 * @author hongmeng
 * @date 2021/8/25
 */
@Component
@Slf4j
public class SocketIORoomEventHandler {
    @Resource
    private SystemProperty systemProperty;
    @Autowired
    private RedisSessionCacheHelper redisSessionCacheHelper;
    @Autowired
    private GlobalSessionService globalSessionService;

    @OnConnect
    public void onConnect(SocketIOClient client) {
        String sessionId = client.getSessionId().toString();
        log.info("onConnect:sessionId={}", sessionId);
    }

    @OnEvent(value = "roomEvent")
    public void onEvent(SocketIOClient client, AckRequest request, Message message) {
        log.info("事件{}发来消息：{}", "roomEvent", message);
        String sessionId = client.getSessionId().toString();
        //回发消息
        client.sendEvent("roomEvent", message);
        //绑定房间信息
        message.setNodeName(systemProperty.getNodeName());
        message.setNodeSessionId(sessionId);
        message.setGlobalSessionId(redisSessionCacheHelper.getGlobalSessionId(systemProperty.getNodeName(), sessionId));
        globalSessionService.initConnectSession(client, message);
        log.info("绑定房间成功:sessionId = {},roomId={}", sessionId, message.getRoomId());
        //TODO.....各种业务处理
        //发送全局消息
        globalSessionService.sendGlobalRoomMessage(
                client,
                Lists.newArrayList(client.getSessionId().toString()),
                message.getRoomId(),
                message,
                "roomEvent");
    }
}
