package com.ypw.websocketstarter.websocket;

import com.alibaba.fastjson.JSON;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.ypw.websocketstarter.cache.RedisSessionCacheHelper;
import com.ypw.websocketstarter.model.SystemProperty;
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

    @OnConnect
    public void onConnect(SocketIOClient client) {
        log.info("onConnect{}", client.getSessionId());
        //维护全局缓存
        redisSessionCacheHelper.register(client.getSessionId().toString(), systemProperty.getNodeName());
    }

    @OnEvent(value = "roomEvent")
    public void onEvent(SocketIOClient client, AckRequest request, String message) {
        log.info("事件{}发来消息：{}", "roomEvent", JSON.toJSONString(message));
        //回发消息
        client.sendEvent("roomEvent", "服务端收到消息" + message);
        //TODO 各种消息处理器

    }


}
