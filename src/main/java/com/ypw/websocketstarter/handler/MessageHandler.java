package com.ypw.websocketstarter.handler;

import javax.websocket.Session;

/**
 * 事件的接口,可以有多种实现(Tomcat,socketIO)
 *
 * @author hongmeng
 * @date 2022/5/24
 */
public interface MessageHandler {
    /**
     * onOpen
     *
     * @param session session
     */
    void OnOpen(Session session);

    /**
     * onMessage
     *
     * @param session session
     * @param message message
     */
    void OnMessage(Session session, String message);

    /**
     * OnClose
     */
    void OnClose(Session session);
}
