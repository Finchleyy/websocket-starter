package com.ypw.websocketstarter.eventloop;

import com.alibaba.fastjson.JSON;
import com.corundumstudio.socketio.SocketIOClient;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ypw.websocketstarter.cache.LocalSessionCache;
import com.ypw.websocketstarter.cache.RedisSessionCacheHelper;
import com.ypw.websocketstarter.comstants.MessageConstants;
import com.ypw.websocketstarter.model.GlobalRedisEventWrapper;
import com.ypw.websocketstarter.model.SystemProperty;
import com.ypw.websocketstarter.properties.ConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author hongmeng
 */
@Component
@Slf4j
public class SessionRoomEventLoop implements InitializingBean {
    @Resource
    private RedisSessionCacheHelper redisCacheHelper;
    @Resource
    private SystemProperty systemProperty;
    @Resource
    private ConfigProperties configProperty;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    ThreadPoolExecutor threadPoolExecutor;

    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void afterPropertiesSet() {
        Runnable runnable = () -> {
            while (true) {
                try {
                    String message = redisCacheHelper.popMsgFromList(systemProperty.getNodeName());
                    if (StringUtils.isBlank(message)) {
                        //防止CPU空转,阻塞POP共同作用
                        Thread.sleep(configProperty.getStopPullMsgInterval());
                    } else {
                        GlobalRedisEventWrapper redisEventWrapper = JSON.parseObject(message, GlobalRedisEventWrapper.class);
                        log.info("拉取到队列{},消息{}", systemProperty.getNodeName(), JSON.toJSONString(redisEventWrapper));
                        String globalSessionId = redisEventWrapper.getGlobalSessionId();
                        //推送消息
                        SocketIOClient localClient = LocalSessionCache.getLocalClient(globalSessionId);
                        if (Objects.nonNull(localClient)) {
                            handleMessage(redisEventWrapper);
                        } else {
                            //此时 session 可能还没有维护到本机内存中就被拉取到,消息需要回源重试
                            retryEvent(redisEventWrapper);
                        }
                    }
                } catch (Exception e) {
                    log.warn("拉取redis队列msg出现异常queueName={}", systemProperty.getNodeName(), e);
                }
            }
        };
        threadPoolExecutor = new ThreadPoolExecutor(
                1,
                1,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(16),
                (new ThreadFactoryBuilder())
                        .setDaemon(true)
                        .setNameFormat("pull room msg thread ".concat(systemProperty.getNodeName()))
                        .build(),
                new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolExecutor.execute(runnable);
    }

    private void handleMessage(GlobalRedisEventWrapper redisEventWrapper) {
        //这里用监听者模式进行解耦,根据各自的 messageType实现监听器
        eventPublisher.publishEvent(redisEventWrapper);
    }

    private void retryEvent(GlobalRedisEventWrapper redisEventWrapper) {
        if (redisEventWrapper.getRetryCount() < MessageConstants.MAX_RETRY_TIMES) {
            log.info("retry count {}", redisEventWrapper.getRetryCount());
            redisEventWrapper.setRetryCount(redisEventWrapper.getRetryCount() + 1);
            //可能拉取到的消息session 还没有保存到内存中,没有写到LocalSessionCache,这时消息重新入队
            //可能因为链接异常断开导致队列消息一直无法被消费,这里要加一个最大重试次数,丢弃消息
            eventPublisher.publishEvent(redisEventWrapper);
        }
    }

    public void stop() {
        threadPoolExecutor.shutdown();
    }
}