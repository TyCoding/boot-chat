package cn.tycoding.utils;

import cn.tycoding.constant.CommonConstant;

/**
 * 核心工具类
 *
 * @author tycoding
 * @date 2019-06-14
 */
public class CoreUtil {

    public static String SUB_MESSAGE_PREFIX(String text) {
        if (text != null) {
            if (text.startsWith(CommonConstant.CHAT_COMMON_PREFIX)) {
                return text.substring(text.indexOf(CommonConstant.CHAT_COMMON_PREFIX));
            }
            if (text.startsWith(CommonConstant.CHAT_ID_PREFIX)) {
                return text.substring(text.indexOf(CommonConstant.CHAT_ID_PREFIX));
            }
        }
        return null;
    }
}
