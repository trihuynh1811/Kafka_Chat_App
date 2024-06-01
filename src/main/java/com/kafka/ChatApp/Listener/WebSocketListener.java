package com.kafka.ChatApp.Listener;

import com.kafka.ChatApp.Entity.Contact;
import com.kafka.ChatApp.Repository.AccountRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketListener {

    @Autowired
    AccountRepo accountRepo;

    final SimpMessageSendingOperations messageTemplate;

    private static Map<String, HttpSession> webSocketSessionToHttpSessionMap;
    private static String sessionId;
    private static HttpServletRequest request_;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        HttpSession httpSession = webSocketSessionToHttpSessionMap.get(sessionId);
        if (httpSession != null || request_.isRequestedSessionIdValid()){
            Contact acc = (Contact) httpSession.getAttribute("authUser");
            acc.setLogoutTimeStamp(LocalDateTime.now());
            accountRepo.save(acc);
            httpSession.setMaxInactiveInterval(0);
            httpSession.invalidate();
        }
    }

    public static void setWebSocketSessionToHttpSessionMap(Map<String, HttpSession> SocketSessionToHttpSessionMap) {
        webSocketSessionToHttpSessionMap = SocketSessionToHttpSessionMap;
    }

    public static void setSessionId(String id) {
        sessionId = id;
    }

    public static void setHttpServletRequest(HttpServletRequest request) {
        request_ = request;
    }
}
