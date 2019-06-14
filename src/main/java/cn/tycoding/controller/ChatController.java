package cn.tycoding.controller;

import cn.tycoding.config.WebsocketServerEndpoint;
import cn.tycoding.constant.CommonConstant;
import cn.tycoding.entity.Message;
import cn.tycoding.entity.User;
import cn.tycoding.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author tycoding
 * @date 2019-06-11
 */
@Slf4j
@Controller
@RequestMapping("/chat")
public class ChatController {

    /**
     * 获取当前窗口用户信息
     *
     * @param id
     * @param request
     * @return
     */
    @ResponseBody
    @GetMapping("/{id}")
    public R info(@PathVariable("id") String id, HttpServletRequest request) {
        User user = new User();
        if (request.getSession().getAttribute(id) instanceof User) {
            user = (User) request.getSession().getAttribute(id);
        }
        return new R(user);
    }

    /**
     * 向指定窗口推送消息
     *
     * @param id
     * @param message
     * @return
     */
    @ResponseBody
    @GetMapping("/push/{id}")
    public String push(@PathVariable("id") String id, @RequestParam("message") String message) {
        try {
            WebsocketServerEndpoint.sendInfo(id, message);
            return "推送消息成功";
        } catch (Exception e) {
            e.printStackTrace();
            return "推送消息失败";
        }
    }

    /**
     * 获取在线用户列表
     *
     * @param request 从Session中获取
     * @return
     */
    @ResponseBody
    @GetMapping("/online/list")
    public R onlineList(HttpServletRequest request) {
        List<User> list = new ArrayList<>();
        Enumeration<String> ids = request.getSession().getAttributeNames();
        while (ids.hasMoreElements()) {
            String id = ids.nextElement();
            if (request.getSession().getAttribute(id) instanceof User) {
                list.add((User) request.getSession().getAttribute(id));
            }
        }
        return new R(list);
    }

    /**
     * 获取公共聊天消息内容
     *
     * @param request 从Session中获取
     * @return
     */
    @ResponseBody
    @GetMapping("/common")
    public R common(HttpServletRequest request) {
        List<Message> list = new ArrayList<>();
        Enumeration<String> ids = request.getSession().getAttributeNames();
        while (ids.hasMoreElements()) {
            String id = ids.nextElement();
            //指定前缀标识
            if (id.startsWith(CommonConstant.CHAT_COMMON_PREFIX)) {
                Object attribute = request.getSession().getAttribute(id);
                if (attribute instanceof List) {
                    list = (List<Message>) attribute;
                }
            }
        }
        return new R(list);
    }

    /**
     * 获取指定用户的聊天消息内容
     *
     * @param id
     * @param request 从Session中获取
     * @return
     */
    @ResponseBody
    @GetMapping("/self/{id}")
    public R self(@PathVariable("id") String id, HttpServletRequest request) {
        List<Message> list = new ArrayList<>();
        request.getSession().getAttribute(id);
        return new R(list);
    }

    /**
     * 退出登录
     *
     * @param id
     * @param request
     * @return
     */
    @ResponseBody
    @GetMapping("/logout")
    public R logout(String id, HttpServletRequest request) {
        if (id != null) {
            request.getSession().removeAttribute(id);
        }
        return new R();
    }
}
