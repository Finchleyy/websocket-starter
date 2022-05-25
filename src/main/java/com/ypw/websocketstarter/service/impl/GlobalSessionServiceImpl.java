package com.ypw.websocketstarter.service.impl;

import com.alibaba.fastjson.JSON;
import com.corundumstudio.socketio.SocketIOClient;
import com.ypw.websocketstarter.cache.LocalSessionCache;
import com.ypw.websocketstarter.cache.RedisSessionCacheHelper;
import com.ypw.websocketstarter.model.GlobalRedisEventWrapper;
import com.ypw.websocketstarter.model.Message;
import com.ypw.websocketstarter.model.SystemProperty;
import com.ypw.websocketstarter.service.GlobalSessionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author hongmeng
 * @date 2022/4/7
 */
@Slf4j
@Service
public class GlobalSessionServiceImpl implements GlobalSessionService {
    @Autowired
    private RedisSessionCacheHelper redisSessionCacheHelper;
    @Resource
    private SystemProperty systemProperty;

    @Override
    public void initConnectSession(SocketIOClient currentSession, Message message) {
        String sessionId = currentSession.getSessionId().toString();
        //加入房间
        currentSession.joinRoom(message.getRoomId());
        //注册机器和 session 关系
        redisSessionCacheHelper.register(currentSession.getSessionId().toString(), systemProperty.getNodeName());
        //注册 room 和 session 关系
        message.setGlobalSessionId(redisSessionCacheHelper.getGlobalSessionId(systemProperty.getNodeName(), sessionId));
        message.setNodeName(systemProperty.getNodeName());
        message.setNodeSessionId(sessionId);
        redisSessionCacheHelper.bindRoomRelation(message.getRoomId(), message, sessionId, systemProperty.getNodeName());
    }


    @Override
    public void initDaemonConnectSession(SocketIOClient currentSession, Message message) {
        String sessionId = currentSession.getSessionId().toString();
        //注册机器和 session 关系
        redisSessionCacheHelper.register(currentSession.getSessionId().toString(), systemProperty.getNodeName());
        //注册 userId 和 session 关系
        message.setGlobalSessionId(redisSessionCacheHelper.getGlobalSessionId(systemProperty.getNodeName(), sessionId));
        message.setNodeName(systemProperty.getNodeName());
        message.setNodeSessionId(sessionId);
        redisSessionCacheHelper.binUserDaemonRelation(message.getUserId(), message, systemProperty.getNodeName());
    }

    @Override
    public void sendGlobalRoomMessage(SocketIOClient currentSession, List<String> currentNodeRoomUserSessionIdList, String roomId, Object message, String messageType) {
        try {
            if (Objects.isNull(LocalSessionCache.getLocalClient(currentSession.getSessionId().toString()))) {
                return;
            }
            String sessionId = currentSession.getSessionId().toString();
            //有些 session 过滤器会过滤掉本机 session,这里重新加回来
            currentNodeRoomUserSessionIdList.add(redisSessionCacheHelper.getGlobalSessionId(systemProperty.getNodeName(), sessionId));
            currentNodeRoomUserSessionIdList.forEach(e -> log.info("current node global sessionId list==========================>{}", e));
            //获取房间内所有用户
            Map<String, Message> roomUsers = redisSessionCacheHelper.getRoomUsers(roomId);
            if (Objects.isNull(roomUsers)) {
                return;
            }
            //过滤掉房间内在本机的 session,向其他节点的 session 消息写消息队列
            Set<Map.Entry<String, Message>> entries = roomUsers.entrySet();
            List<Message> otherNodeSessionList = entries.stream()
                    .filter(e -> StringUtils.isNoneBlank(e.getValue().toString()))
                    .map(Map.Entry::getValue)
                    .peek(e -> log.info("room global sessionId===================>{}", e.getGlobalSessionId()))
                    .filter(e -> !currentNodeRoomUserSessionIdList.contains(e.getGlobalSessionId()))
                    .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(otherNodeSessionList)) {
                return;
            }
            log.info("other node session count=============================>{}", otherNodeSessionList.size());
            //向其它 node 所在的 session 发送消息,向目标 session 所在机器队列写入消息
            otherNodeSessionList.forEach(e -> redisSessionCacheHelper.pushMsgToList(e.getNodeName(), JSON.toJSONString(buildGlobalEvent(e.getGlobalSessionId(), message, messageType))));
        } catch (Exception e) {
            log.warn("发送全局房间消息异常:{}", e.getMessage());
        }
    }

    @Override
    public void sendGlobalUserMessage(String userId, Object message, String messageType) {
        //前面已经判断过了该用户 session 不在本机,需要写消息队列
        //获取该用户所在的 node节点
        Message userDaemonRelation = redisSessionCacheHelper.getUserDaemonRelation(userId);
        if (Objects.isNull(userDaemonRelation)) {
            return;
        }
        //向该用户所在消息队列写消息
        redisSessionCacheHelper.pushUserMsgToList(userDaemonRelation.getNodeName(), JSON.toJSONString(buildGlobalEvent(userDaemonRelation.getGlobalSessionId(), message, messageType)));
    }

    @Override
    public void refreshRoomUserInfo(SocketIOClient currentSession, String roomId) {
        //更新房间内用户信息
        Map<String, Message> roomUsers = redisSessionCacheHelper.getRoomUsers(roomId);
        List<Message> collect = roomUsers.values().stream()
                .filter(message -> Objects.equals(message.getNodeSessionId(), currentSession.getSessionId().toString()))
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(collect)) {
            Message message = collect.get(0);
            redisSessionCacheHelper.unBindRoomRelation(roomId, currentSession.getSessionId().toString(), systemProperty.getNodeName());
            redisSessionCacheHelper.bindRoomRelation(roomId, message, currentSession.getSessionId().toString(), systemProperty.getNodeName());
        }
    }

    public GlobalRedisEventWrapper buildGlobalEvent(String globalSessionId, Object data, String messageType) {
        return new GlobalRedisEventWrapper(globalSessionId, data, messageType);
    }
}
