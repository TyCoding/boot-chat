package cn.tycoding.config;

import cn.tycoding.constant.CommonConstant;
import cn.tycoding.entity.User;
import cn.tycoding.service.ChatSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 定时任务
 *
 * @author tycoding
 * @date 2019-06-17
 */
@Slf4j
@Component
@Configuration
@EnableScheduling
public class ScheduleTaskConfig {
    private static final Long MINUTE_30 = 1000 * 60 * 30L;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ChatSessionService chatSessionService;

    @Scheduled(cron = "0 */30 * * * ?")
    private void clearUser() {
        log.info("定时任务 >>>>>>>>>> 清除注册时间超过30分钟的账户，以及其会话信息");

        List<User> userList = chatSessionService.onlineList();
        userList.forEach(user -> {
            if ((new Date().getTime() - user.getId()) >= MINUTE_30) {
                chatSessionService.delete(user.getId().toString());
                if (redisTemplate.boundValueOps(CommonConstant.CHAT_COMMON_PREFIX + user.getId()).get() != null) {
                    redisTemplate.delete(CommonConstant.CHAT_COMMON_PREFIX + user.getId());
                }
                if (redisTemplate.boundValueOps(CommonConstant.CHAT_FROM_PREFIX + user.getId()).get() != null) {
                    redisTemplate.delete(CommonConstant.CHAT_FROM_PREFIX + user.getId());
                }
            }
        });
    }
}
