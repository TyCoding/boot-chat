package cn.tycoding.service.impl;

import cn.tycoding.constant.CommonConstant;
import cn.tycoding.entity.Message;
import cn.tycoding.entity.User;
import cn.tycoding.service.ChatSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author tycoding
 * @date 2019-06-14
 */
@Slf4j
@Service
public class ChatSessionServiceImpl implements ChatSessionService {

    @Override
    public void pushMessage(String id, String message, HttpSession session) {
        Object user = session.getAttribute(id);
        if (user instanceof User) {
            Message entity = new Message();
            entity.setContent(message);
            entity.setFrom((User) user);
            entity.setTo(null);
            entity.setTime(new Date());

            //这里按照 PREFIX_ID 作为KEY储存消息记录
            //但一个用户可能推送很多消息，VALUE应该是数组
            List<Message> list = new ArrayList<>();
            Object msg = session.getAttribute(CommonConstant.CHAT_COMMON_PREFIX + id);
            if (msg == null) {
                //第一次推送消息
                list.add(entity);
            } else {
                //第n次推送消息
                if (msg instanceof List) {
                    list = (List<Message>) msg;
                    list.add(entity);
                }
            }
            session.setAttribute(CommonConstant.CHAT_COMMON_PREFIX + id, list);
        }
    }
}
