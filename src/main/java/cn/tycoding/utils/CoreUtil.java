package cn.tycoding.utils;

import cn.tycoding.entity.Message;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author tycoding
 * @date 2019-06-15
 */
public class CoreUtil {

    /**
     * 按照时间顺序向List中push数据
     *
     * @param list
     */
    public static void push(List<Message> list) {
        list.sort(Comparator.comparing(Message::getTime));
    }

    /**
     * format date
     *
     * @param date
     * @return
     */
    public static String format(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
}
