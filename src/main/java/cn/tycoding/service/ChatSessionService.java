package cn.tycoding.service;

import javax.servlet.http.HttpSession;

/**
 * @author tycoding
 * @date 2019-06-14
 */
public interface ChatSessionService {

    /**
     * 推送消息，储存到Session中
     *
     * @param id
     * @param message
     * @param session
     */
    void pushMessage(String id, String message, HttpSession session);
}
