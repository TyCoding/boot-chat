package cn.tycoding.constant;

/**
 * 系统常量值
 *
 * @author tycoding
 * @date 2019-06-14
 */
public interface CommonConstant {

    /**
     * 用户数据 Key前缀标识
     */
    String USER_PREFIX = "USER_";

    /**
     * 群发消息Session Key前缀标识
     */
    String CHAT_COMMON_PREFIX = "CHAT_COMMON_";

    /**
     * 推送至指定用户消息
     *      推送方Session Key前缀标识
     */
    String CHAT_FROM_PREFIX = "CHAT_FROM_";

    /**
     * 推送至指定用户消息
     *      接收方Session Key前缀标识
     */
    String CHAT_TO_PREFIX = "_TO_";

    /**
     * RedisTemplate 根据Key模糊匹配查询前缀
     */
    String REDIS_MATCH_PREFIX = "*";
}
