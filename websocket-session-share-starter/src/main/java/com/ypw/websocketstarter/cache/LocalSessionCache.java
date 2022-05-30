package com.ypw.websocketstarter.cache;

import com.corundumstudio.socketio.SocketIOClient;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hongmeng
 * @date 2022/5/24
 */
public class LocalSessionCache {

    private static final ConcurrentHashMap<String, SocketIOClient> localSessionMap = new ConcurrentHashMap<>();

    /**
     * 添加本地客户端
     *
     * @param globalSessionId 会话sessionId
     * @param client          ws会话客户端
     */
    public static void addLocalClient(String globalSessionId, SocketIOClient client) {
        localSessionMap.put(globalSessionId, client);
    }

    /**
     * 根据sessionId获取本地客户端
     *
     * @param globalSessionId 会话sessionId
     * @return SocketIOClient  ws会话客户端
     */
    public static SocketIOClient getLocalClient(String globalSessionId) {
        return localSessionMap.get(globalSessionId);
    }

    /**
     * 根据sessionId获取移除本地客户端
     *
     * @param globalSessionId 会话sessionId
     */
    public static void removeClient(String globalSessionId) {
        localSessionMap.remove(globalSessionId);
    }
}
