package com.ypw.websocketstarter.cache;

import com.alibaba.fastjson.JSON;
import com.ypw.websocketstarter.model.Message;
import com.ypw.websocketstarter.properties.ConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.ypw.websocketstarter.comstants.MessageConstants.MSG_LIST_PREFIX;
import static com.ypw.websocketstarter.comstants.MessageConstants.ROOM_PREFIX;
import static com.ypw.websocketstarter.comstants.MessageConstants.SESSION_ID_PREFIX;
import static com.ypw.websocketstarter.comstants.MessageConstants.SESSION_ID_SPLIT;
import static com.ypw.websocketstarter.comstants.MessageConstants.USER_MSG_LIST_PREFIX;
import static com.ypw.websocketstarter.comstants.MessageConstants.USER_PREFIX;


/**
 * redis操作类封装
 *
 * @author hongmeng
 */
@Slf4j
@Component
public class RedisSessionCacheHelper {
    private static final String KEY_PREFIX = "WEBSOCKET:";
    @Resource(name = "websocketRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private ConfigProperties configProperty;

    /**
     * 获取全局 sessionID,tomcat实现的 wsSession获取的到的 ID 不能保证全局唯一
     *
     * @param nodeName      节点名称
     * @param nodeSessionId 节点 sessionId
     * @return 全局 sessionId
     */
    public String getGlobalSessionId(String nodeName, String nodeSessionId) {
        return StringUtils.join(nodeName, SESSION_ID_SPLIT, nodeSessionId);
    }

    public String getGlobalSessionKey(String nodeName, String nodeSessionId) {
        return StringUtils.join(SESSION_ID_PREFIX, nodeName, SESSION_ID_SPLIT, nodeSessionId);
    }

    /**
     * 注册主机和 session 关系
     * 把当前客户端在哪台机器上的信息注册到redis中去
     *
     * @param sessionId sessionId
     * @param nodeName  节点名称
     */
    public void register(String sessionId, String nodeName) {
        redisTemplate.opsForValue().set(getGlobalSessionKey(nodeName, sessionId), nodeName, configProperty.getSessionIdMaxAlive(), TimeUnit.SECONDS);
    }


    /**
     * 清除主机和 session 关系
     * 把当前客户端的信息注册从redis中清除
     *
     * @param sessionId sessionId
     */
    public void unRegister(String sessionId, String nodeName) {
        redisTemplate.delete(getGlobalSessionKey(nodeName, sessionId));
    }


    /**
     * 获取session所在的主机
     *
     * @param sessionId 客户端标识
     * @return nodeName 机器名称
     */
    public String getNodeNameBySessionId(String sessionId, String nodeName) {
        return redisTemplate.opsForValue().get(getGlobalSessionKey(nodeName, sessionId));
    }


    /**
     * 往sessionId对应的远程机器队列中放入msg
     *
     * @param nodeName 机器（节点）名称
     * @param content  具体的内容
     */
    public void pushMsgToList(String nodeName, String content) {
        redisTemplate.opsForList().leftPush(StringUtils.join(MSG_LIST_PREFIX, nodeName), content);
        log.info("write message to list===>{}-------content:{}", StringUtils.join(MSG_LIST_PREFIX, nodeName), content);
    }


    /**
     * 依次获取尾部元素
     *
     * @param nodeName 机器（节点）名称
     * @return class   返回对应的实体
     */
    public String popMsgFromList(String nodeName) {
        //这里用 BRPOP命令
        return redisTemplate.opsForList().rightPop(StringUtils.join(MSG_LIST_PREFIX, nodeName), configProperty.getStopPullMsgInterval(), TimeUnit.MILLISECONDS);
    }


    /**
     * 维护房间和客户端session的关系
     *
     * @param roomId    房间id，由后端统一分发
     * @param sessionId 客户端sessionId
     */
    public void bindRoomRelation(String roomId, Message message, String sessionId, String nodeName) {
        redisTemplate.opsForHash().put(StringUtils.join(ROOM_PREFIX, roomId), getGlobalSessionId(nodeName, sessionId), JSON.toJSONString(message));
        redisTemplate.expire(StringUtils.join(ROOM_PREFIX, roomId), configProperty.getSessionIdMaxAlive(), TimeUnit.SECONDS);
    }

    /**
     * 解绑房间和客户端session的关系
     *
     * @param roomId    房间id，由后端统一分发
     * @param sessionId sessionId
     * @param nodeName  节点 name
     */
    public void unBindRoomRelation(String roomId, String sessionId, String nodeName) {
        redisTemplate.opsForHash().delete(StringUtils.join(ROOM_PREFIX, roomId), getGlobalSessionId(nodeName, sessionId));
    }

    /**
     * 维护用户 ID 和全局 session 关系
     *
     * @param userId   用户ID
     * @param message  message
     * @param nodeName 节点 name
     */
    public void binUserDaemonRelation(String userId, Message message, String nodeName) {
        redisTemplate.opsForValue().set(StringUtils.join(USER_PREFIX, userId), JSON.toJSONString(message), configProperty.getSessionIdMaxAlive(), TimeUnit.SECONDS);
    }

    /**
     * 获取用户 ID 和全局 session 关系
     *
     * @param userId 用户ID
     */
    public Message getUserDaemonRelation(String userId) {
        try {
            String message = redisTemplate.opsForValue().get(StringUtils.join(USER_PREFIX, userId));
            return JSON.parseObject(message, Message.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前房间下面所有的客户注册信息
     *
     * @param roomId 房间id
     * @return sessionIds
     */
    public Set<String> getRoomRelation(String roomId) {
        Set<String> result = new HashSet<>();
        Set<Object> sessionIds = redisTemplate.opsForHash().keys(StringUtils.join(ROOM_PREFIX, roomId));
        if (CollectionUtils.isEmpty(sessionIds)) {
            return result;
        }
        for (Object sessionId : sessionIds) {
            result.add(sessionId.toString());
        }
        return result;
    }

    public Map<String, Message> getRoomUsers(String roomId) {
        Map<String, Message> result = new HashMap<>(4);
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(StringUtils.join(ROOM_PREFIX, roomId));
        for (Object key : entries.keySet()) {
            Object o = entries.get(key);
            if (Objects.nonNull(key) && Objects.nonNull(o)) {
                Message message = JSON.parseObject(o.toString(), Message.class);
                result.put(key.toString(), message);
            }
        }
        return result;
    }

    public void pushUserMsgToList(String nodeName, String content) {
        redisTemplate.opsForList().leftPush(StringUtils.join(USER_MSG_LIST_PREFIX, nodeName), content);
        log.info("write message to list===>{}-------content:{}", StringUtils.join(USER_MSG_LIST_PREFIX, nodeName), content);
    }

    public <T> T popUserMsgFromList(String nodeName, Class<T> clazz) {
        //这里用 BRPOP命令
        String content = redisTemplate.opsForList().rightPop(StringUtils.join(USER_MSG_LIST_PREFIX, nodeName), configProperty.getStopPullMsgInterval(), TimeUnit.MILLISECONDS);
        return JSON.parseObject(content, clazz);
    }

    public void sendTopicMessage(String topic, String message) {
        redisTemplate.convertAndSend(topic, message);
    }
}
