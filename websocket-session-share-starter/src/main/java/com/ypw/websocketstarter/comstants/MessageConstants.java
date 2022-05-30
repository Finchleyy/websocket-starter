package com.ypw.websocketstarter.comstants;

/**
 * session 共享消息常量
 *
 * @author 鸿蒙
 */
public class MessageConstants {
    /**
     * sessionId前缀
     */
    public static final String SESSION_ID_PREFIX = "DIP-SESSION-NODE:";
    /**
     * 房间前缀
     */
    public static final String ROOM_PREFIX = "ROOM:";
    /**
     * 用户消息前缀
     */
    public static final String USER_PREFIX = "USER:";
    /**
     * 消息队列前缀
     */
    public static final String MSG_LIST_PREFIX = "MESSAGE-LIST:" + ROOM_PREFIX;
    /**
     * user消息队列前缀
     */
    public static final String USER_MSG_LIST_PREFIX = "MESSAGE-LIST:" + USER_PREFIX;
    /**
     * sessionId分隔符
     */
    public static final String SESSION_ID_SPLIT = "-";
    /**
     * 消息最大重试次数
     */
    public static final int MAX_RETRY_TIMES = 10;
}
