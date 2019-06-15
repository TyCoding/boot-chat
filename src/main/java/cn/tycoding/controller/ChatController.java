package cn.tycoding.controller;

import cn.tycoding.entity.Message;
import cn.tycoding.entity.User;
import cn.tycoding.service.ChatSessionService;
import cn.tycoding.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author tycoding
 * @date 2019-06-11
 */
@Slf4j
@Controller
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatSessionService chatSessionService;

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
    public R push(@PathVariable("id") String id, @RequestParam("message") String message) {
        try {
            WebsocketServerEndpoint endpoint = new WebsocketServerEndpoint();
            endpoint.sendTo(id, message);
            return new R();
        } catch (Exception e) {
            e.printStackTrace();
            return new R(500, "消息推送失败");
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
        List<User> list = chatSessionService.onlineList(request.getSession());
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
    public R commonList(HttpServletRequest request) {
        List<Message> list = chatSessionService.commonList(request.getSession());
        return new R(list);
    }

    /**
     * 获取指定用户的聊天消息内容
     *
     * @param fromId  该用户ID
     * @param toId    哪个窗口
     * @param request 从Session中获取
     * @return
     */
    @ResponseBody
    @GetMapping("/self/{fromId}/{toId}")
    public R selfList(@PathVariable("fromId") String fromId, @PathVariable("toId") String toId, HttpServletRequest request) {
        List<Message> list = chatSessionService.selfList(fromId, toId, request.getSession());
        return new R(list);
    }

    /**
     * 退出登录
     *
     * @param id      用户ID
     * @param request 从Session中剔除
     * @return
     */
    @ResponseBody
    @DeleteMapping("/{id}")
    public R logout(@PathVariable("id") String id, HttpServletRequest request) {
        if (id != null) {
            request.getSession().removeAttribute(id);
        }
        return new R();
    }
}
