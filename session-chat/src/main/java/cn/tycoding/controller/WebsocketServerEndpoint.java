package cn.tycoding.controller;

import cn.tycoding.config.HttpSessionConfig;
import cn.tycoding.entity.Message;
import cn.tycoding.entity.User;
import cn.tycoding.service.ChatSessionService;
import cn.tycoding.utils.CoreUtil;
import cn.tycoding.utils.R;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
    private static long online = 0;

    //用于存放当前Websocket对象的Set集合
    private static CopyOnWriteArraySet<WebsocketServerEndpoint> websocketServerEndpoints = new CopyOnWriteArraySet<>();

    //与客户端的会话Session
    private Session session;

    //当前会话窗口ID
    private String fromId = "";

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

        this.fromId = id;
        try {
            User user = new User();
            Object attribute = httpSession.getAttribute(fromId);
            if (attribute instanceof User) {
                user = (User) attribute;
            }
            //群发消息
            Map<String, Object> map = new HashMap<>();
            map.put("online", getOnlineCount());
            map.put("msg", "用户 " + user.getName() + " 已上线");
            sendMore(JSONObject.toJSONString(map));
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
     */
    @OnMessage
    public void onMessage(String message) throws IOException {
        log.info("接收到窗口：" + fromId + " 的信息：" + message);

        chatSessionService.pushMessage(fromId, null, message, httpSession);

        //群发消息
        sendMore(getData(null, message));
    }

    @OnError
    public void onError(Throwable e) {
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
     * 封装返回消息
     *
     * @param toId    指定窗口ID
     * @param message 消息内容
     * @return
     * @throws IOException
     */
    private String getData(String toId, String message) throws IOException {
        Message entity = new Message();
        entity.setMessage(message);
        entity.setTime(CoreUtil.format(new Date()));
        entity.setOnline(getOnlineCount());
        Object from = httpSession.getAttribute(fromId);
        if (from instanceof User) {
            entity.setFrom((User) from);
        }
        Object to = httpSession.getAttribute(toId);
        if (to instanceof User) {
            entity.setTo((User) to);
        }
        return JSONObject.toJSONString(new R(entity));
    }

    /**
     * 群发消息
     *
     * @param data
     */
    private void sendMore(String data) {
        for (WebsocketServerEndpoint websocketServerEndpoint : websocketServerEndpoints) {
            try {
                websocketServerEndpoint.sendMessage(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 指定窗口推送消息
     *
     * @param entity 推送消息
     * @param toId   接收方ID
     */
    public void sendTo(String toId, Message entity, HttpSession session) {
        fromId = entity.getFrom().getId().toString();
        httpSession = session;
        if (websocketServerEndpoints.size() < 1) {
            throw new RuntimeException("用户未在线");
        }
        for (WebsocketServerEndpoint endpoint : websocketServerEndpoints) {
            try {
                if (toId == null) {
                    log.error("推送失败，找不到该ID对应的窗口");
                    endpoint.sendMessage(JSONObject.toJSONString(new R(500, "推送失败")));
                } else if (endpoint.fromId.equals(toId)) {
                    log.info(entity.getFrom().getId() + " 推送消息到窗口：" + toId + " ，推送内容：" + entity.getMessage());

                    endpoint.sendMessage(getData(toId, entity.getMessage()));
                    chatSessionService.pushMessage(fromId, toId, entity.getMessage(), httpSession);
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    private synchronized void subOnLineCount() {
        WebsocketServerEndpoint.online--;
    }

    private synchronized long getOnlineCount() {
        return online;
    }

    private synchronized void addOnlineCount() {
        WebsocketServerEndpoint.online++;
    }
}
