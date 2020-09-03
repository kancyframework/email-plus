package com.github.kancyframework.emailplus.spring.boot.aop.annotation;

import java.lang.annotation.*;

/**
 * EmailNotice
 *
 * @author kancy
 * @date 2020/2/22 20:16
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EmailNotice {

    /**
     * 支持写Spel表达式
     *  例如："#message.headers['messageKey']"
     * @ref https://docs.spring.io/spring/docs/current/spring-framework-reference/#expressions
     * @return
     */
    String value();

    /**
     * 业务方法出现哪些异常时触发
     * @return
     */
    Class<? extends Throwable>[] classes() default {Throwable.class};

    /**
     * 不抛出的异常
     * @return
     */
    Class<? extends Throwable>[] noThrows() default {};

    /**
     * 是否打印异常日志
     * @return
     */
    boolean log() default false;
}
