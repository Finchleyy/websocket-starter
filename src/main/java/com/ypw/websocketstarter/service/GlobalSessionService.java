package com.ypw.websocketstarter.service;

import com.corundumstudio.socketio.SocketIOClient;
import com.ypw.websocketstarter.model.Message;

import java.util.List;

/**
 * 全局 session 共享 Service
 *
 * @author hongmeng
 * @date 2022/5/24
 */
public interface GlobalSessionService {
    /**
     * 初始化建立的房间链接,维护用户和机器,房间的对应关系
     *
     * @param currentSession currentSession
     * @param message        message
     */
    void initConnectSession(SocketIOClient currentSession, Message message);

    /**
     * 发送 room 全局消息,过滤掉本机session
     *
     * @param currentSession           currentSession
     * @param currentNodeSessionIdList 本机 session 用户(需要过滤掉)
     * @param roomId                   房间 ID
     * @param message                  消息内容
     * @param messageType              消息类型(自定义)
     */
    void sendGlobalRoomMessage(SocketIOClient currentSession, List<String> currentNodeSessionIdList, String roomId, Object message, String messageType);

    /**
     * 初始化用户链接
     *
     * @param currentSession currentSession
     * @param message        消息
     */
    void initDaemonConnectSession(SocketIOClient currentSession, Message message);

    /**
     * 发送全局用户消息
     *
     * @param userId      用户 ID
     * @param message     消息内容
     * @param messageType 消息类型
     */
    void sendGlobalUserMessage(String userId, Object message, String messageType);

    /**
     * 刷新房间内用户信息
     *
     * @param roomId 房间 ID
     */
    void refreshRoomUserInfo(SocketIOClient currentSession, String roomId);

}
