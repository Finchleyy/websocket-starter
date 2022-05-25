package com.ypw.websocketstarter.websocket;

import com.alibaba.fastjson.JSON;
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
    @Autowired
    private SocketIOServer socketIoServer;
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
    public void onEvent(SocketIOClient client, AckRequest request, String message) {
        log.info("事件{}发来消息：{}", "roomEvent", message);
        String sessionId = client.getSessionId().toString();
        //回发消息
        client.sendEvent("roomEvent", message);
        //绑定房间信息
        Message msg = JSON.parseObject(message, Message.class);
        msg.setNodeName(systemProperty.getNodeName());
        msg.setNodeSessionId(sessionId);
        msg.setGlobalSessionId(redisSessionCacheHelper.getGlobalSessionId(systemProperty.getNodeName(), sessionId));
        globalSessionService.initConnectSession(client, msg);
        log.info("绑定房间成功:sessionId = {},roomId={}", sessionId, msg.getRoomId());
        //TODO 各种消息处理器
        globalSessionService.sendGlobalRoomMessage(
                client,
                Lists.newArrayList(client.getSessionId().toString()),
                msg.getRoomId(),
                message,
                "roomEvent");
    }


}
