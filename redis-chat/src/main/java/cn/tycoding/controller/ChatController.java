package cn.tycoding.controller;

import cn.tycoding.entity.Message;
import cn.tycoding.exception.GlobalException;
import cn.tycoding.service.ChatSessionService;
import cn.tycoding.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author tycoding
 * @date 2019-06-11
 */
@Slf4j
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatSessionService chatSessionService;

    /**
     * 获取当前窗口用户信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R info(@PathVariable("id") String id) {
        return new R(chatSessionService.findById(id));
    }

    /**
     * 向指定窗口推送消息
     *
     * @param toId    接收方ID
     * @param message 消息
     * @return
     */
    @PostMapping("/push/{toId}")
    public R push(@PathVariable("toId") String toId, @RequestBody Message message) {
        try {
            WebsocketServerEndpoint endpoint = new WebsocketServerEndpoint();
            endpoint.sendTo(toId, message);
            return new R();
        } catch (GlobalException e) {
            e.printStackTrace();
            return new R(500, e.getMsg());
        }
    }

    /**
     * 获取在线用户列表
     *
     * @return
     */
    @GetMapping("/online/list")
    public R onlineList() {
        return new R(chatSessionService.onlineList());
    }

    /**
     * 获取公共聊天消息内容
     *
     * @return
     */
    @GetMapping("/common")
    public R commonList() {
        return new R(chatSessionService.commonList());
    }

    /**
     * 获取指定用户的聊天消息内容
     *
     * @param fromId 该用户ID
     * @param toId   哪个窗口
     * @return
     */
    @GetMapping("/self/{fromId}/{toId}")
    public R selfList(@PathVariable("fromId") String fromId, @PathVariable("toId") String toId) {
        List<Message> list = chatSessionService.selfList(fromId, toId);
        return new R(list);
    }

    /**
     * 退出登录
     *
     * @param id 用户ID
     * @return
     */
    @DeleteMapping("/{id}")
    public R logout(@PathVariable("id") String id) {
        chatSessionService.delete(id);
        return new R();
    }
}
