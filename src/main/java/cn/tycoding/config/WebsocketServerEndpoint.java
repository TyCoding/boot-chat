package cn.tycoding.config;

import cn.tycoding.service.ChatSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author tycoding
 * @date 2019-06-10
 */
@Slf4j
@Component
@ServerEndpoint(value = "/chat/{id}", configurator = HttpSessionConfig.class)
public class WebsocketServerEndpoint {
    private HttpSession httpSession;

    private static ChatSessionService chatSessionService;
    @Autowired
    public void setChatSessionService(ChatSessionService chatSessionService) {
        WebsocketServerEndpoint.chatSessionService = chatSessionService;
    }

    //在线连接数
    private static int onlineCount = 0;

    //用于存放当前Websocket对象的Set集合
    private static CopyOnWriteArraySet<WebsocketServerEndpoint> websocketServerEndpoints = new CopyOnWriteArraySet<>();

    //与客户端的会话Session
    private Session session;

    //会话窗口的ID标识
    private String id = "";

    /**
     * 链接成功调用的方法
     *
     * @param session
     * @param id
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("id") String id, EndpointConfig config) {
        log.info("onOpen >> 链接成功");
        this.session = session;
        httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());

        //将当前websocket对象存入到Set集合中
        websocketServerEndpoints.add(this);

        //在线人数+1
        addOnlineCount();

        log.info("有新窗口开始监听：" + id + ", 当前在线人数为：" + getOnlineCount());

        this.id = id;

        try {
            sendMessage("有新窗口开始监听：" + id + ", 当前在线人数为：" + getOnlineCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 链接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        log.info("onClose >> 链接关闭");

        //移除当前Websocket对象
        websocketServerEndpoints.remove(this);

        //在内线人数-1
        subOnLineCount();

        log.info("链接关闭，当前在线人数：" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("接收到窗口：" + id + " 的信息：" + message);

        chatSessionService.pushMessage(id, message, httpSession);

        //发送信息
        for (WebsocketServerEndpoint websocketServerEndpoint : websocketServerEndpoints) {
            try {
                websocketServerEndpoint.sendMessage("接收到窗口：" + id + " 的信息：" + message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable e) {
        e.printStackTrace();
    }

    /**
     * 推送消息
     *
     * @param message
     */
    private void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 自定义推送消息
     *
     * @param message
     * @param id
     */
    public static void sendInfo(String id, String message) {
        log.info("推送消息到窗口：" + id + " ，推送内容：" + message);
        for (WebsocketServerEndpoint endpoint : websocketServerEndpoints) {
            try {
                if (id == null) {
                    endpoint.sendMessage(message);
                } else if (endpoint.id.equals(id)) {
                    endpoint.sendMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    private void subOnLineCount() {
        WebsocketServerEndpoint.onlineCount--;
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    private void addOnlineCount() {
        WebsocketServerEndpoint.onlineCount++;
    }
}
