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
     * 对List集合中的数据按照时间顺序排序
     *
     * @param list List<Message>
     */
    public static void sort(List<Message> list) {
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
