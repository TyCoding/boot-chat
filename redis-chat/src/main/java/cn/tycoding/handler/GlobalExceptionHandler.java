package cn.tycoding.handler;

import cn.tycoding.exception.GlobalException;
import cn.tycoding.utils.R;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局Runtime异常处理器
 *
 * @author tycoding
 * @date 2019-06-17
 */
@RestControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public R exception(Exception e) {
        e.printStackTrace();
        return new R(500, "系统异常");
    }

    @ExceptionHandler(value = GlobalException.class)
    public R globalException(GlobalException e) {
        e.printStackTrace();
        return new R(500, e.getMsg());
    }

}
