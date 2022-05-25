package com.ypw.websocketstarter.event.subscriber;

import com.corundumstudio.socketio.SocketIOClient;
import com.ypw.websocketstarter.cache.LocalSessionCache;
import com.ypw.websocketstarter.cache.RedisSessionCacheHelper;
import com.ypw.websocketstarter.model.GlobalRedisEventWrapper;
import com.ypw.websocketstarter.model.SystemProperty;
import com.ypw.websocketstarter.properties.ConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author hongmeng
 * @date 2022/5/10
 */
@Slf4j
@Component
public class SendMessageSubscriber {
    @Resource
    private RedisSessionCacheHelper redisCacheHelper;
    @Resource
    private SystemProperty systemProperty;
    @Resource
    private ConfigProperties configProperty;

    //spel表达式判断重试才会进入这个监听器
    @EventListener(condition = "#redisEventWrapper.eventType.equals('roomEvent')")
    public void messageRetry(GlobalRedisEventWrapper redisEventWrapper) {
        try {
            SocketIOClient localClient = LocalSessionCache.getLocalClient(redisEventWrapper.getGlobalSessionId());
            localClient.sendEvent(redisEventWrapper.getEventType(), redisEventWrapper.getData());
        } catch (Exception e) {
            log.info("SendMessageSubscriber error {}", e.getMessage());
        }
    }
}
