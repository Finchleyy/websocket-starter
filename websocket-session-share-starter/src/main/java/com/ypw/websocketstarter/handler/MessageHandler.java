package com.ypw.websocketstarter.handler;


/**
 * 事件的接口,可以有多种实现(socketIO)
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
    void OnOpen(Object session);

    /**
     * onMessage
     *
     * @param session session
     * @param message message
     */
    void OnMessage(Object session, String message);

    /**
     * OnClose
     */
    void OnClose(Object session);
}
