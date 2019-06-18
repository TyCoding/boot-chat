package cn.tycoding.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 会话消息实体
 *
 * @author tycoding
 * @date 2019-06-14
 */
@Data
public class Message implements Serializable {

    /**
     * 消息推送者
     */
    private User from;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 消息接收者：
     *      如果是私有（向指定窗口推送），to即为接受者User对象
     *      如果是公共消息（群组聊天），to设为null
     */
    private User to;

    /**
     * 创建时间
     */
    private String time;

    public void setMessage(String message) {
        this.message = message == null ? "" : message.replaceAll("\r\n|\r|\n", "");
    }
}
