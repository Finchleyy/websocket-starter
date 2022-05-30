package com.ypw.websocketstarter.event.subscriber;

import com.alibaba.fastjson.JSON;
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
public class MessageRetrySubscriber {
    @Resource
    private RedisSessionCacheHelper redisCacheHelper;
    @Resource
    private SystemProperty systemProperty;
    @Resource
    private ConfigProperties configProperty;

    //spel表达式判断重试才会进入这个监听器
    @EventListener(condition = "#redisEventWrapper.retryCount>0")
    public void messageRetry(GlobalRedisEventWrapper redisEventWrapper) {
        try {
            Thread.sleep(configProperty.getStopPullMsgInterval());
            redisCacheHelper.pushMsgToList(systemProperty.getNodeName(), JSON.toJSONString(redisEventWrapper));
            log.info("消息重新入队");
        } catch (Exception e) {
            log.info("重试消息失败");
        }
    }
}
