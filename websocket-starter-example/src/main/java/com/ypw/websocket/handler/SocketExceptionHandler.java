package com.ypw.websocket.handler;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.ExceptionListener;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author hongmeng
 * @date 2021/8/27
 */
@Slf4j
@Component
public class SocketExceptionHandler implements ExceptionListener {
    @Override
    public void onEventException(Exception e, List<Object> args, SocketIOClient client) {
        //do something
    }

    @Override
    public void onDisconnectException(Exception e, SocketIOClient client) {
        //do something
    }

    @Override
    public void onConnectException(Exception e, SocketIOClient client) {
        log.error("连接异常:{}", e.getMessage());
        client.sendEvent("error", e.getMessage());
        client.disconnect();
    }

    @Override
    public void onPingException(Exception e, SocketIOClient client) {
        //do something
    }

    @Override
    public boolean exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        log.error("exceptionCaught:{}", e.getMessage());
        return false;
    }
}
