package com.demetrius.vellastra.publish.config;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint("/ws/publish/{taskId}")
public class PublishWebSocket {

    private static final ConcurrentHashMap<String, Session> SESSIONS = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("taskId") String taskId) {
        SESSIONS.put(taskId, session);
        log.debug("WebSocket 连接建立: taskId={}", taskId);
    }

    @OnClose
    public void onClose(@PathParam("taskId") String taskId) {
        SESSIONS.remove(taskId);
        log.debug("WebSocket 连接关闭: taskId={}", taskId);
    }

    @OnError
    public void onError(@PathParam("taskId") String taskId, Throwable error) {
        SESSIONS.remove(taskId);
        log.warn("WebSocket 异常: taskId={}", taskId, error);
    }

    /** 向指定任务推送构建日志 */
    public static void pushLog(Long taskId, String stage, String level, String message) {
        String key = String.valueOf(taskId);
        Session session = SESSIONS.get(key);
        if (session != null && session.isOpen()) {
            try {
                String json = String.format(
                        "{\"stage\":\"%s\",\"level\":\"%s\",\"message\":\"%s\",\"timestamp\":%d}",
                        stage, level, message.replace("\"", "\\\""), System.currentTimeMillis());
                session.getBasicRemote().sendText(json);
            } catch (Exception e) {
                log.warn("WebSocket 推送失败: taskId={}", taskId);
            }
        }
    }
}
