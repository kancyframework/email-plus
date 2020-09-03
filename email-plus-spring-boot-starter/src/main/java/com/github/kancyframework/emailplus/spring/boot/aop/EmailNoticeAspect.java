package com.github.kancyframework.emailplus.spring.boot.aop;

import com.github.kancyframework.emailplus.spring.boot.aop.annotation.EmailNotice;
import com.github.kancyframework.emailplus.spring.boot.properties.EmailplusProperties;
import com.github.kancyframework.emailplus.spring.boot.properties.NoticeProperties;
import com.github.kancyframework.emailplus.spring.boot.properties.EmailDefinition;
import com.github.kancyframework.emailplus.spring.boot.service.EmailplusService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * EmailNoticeAspect
 *
 * @author kancy
 * @date 2020/2/22 20:16
 */
@Aspect
public class EmailNoticeAspect {
    private static final Logger log = LoggerFactory.getLogger(EmailNoticeAspect.class);

    @Autowired
    private Map<String, EmailNoticeTrigger> emailNoticeTriggerMap;
    @Autowired
    private EmailplusService emailplusService;
    @Autowired
    private EmailplusProperties emailplusProperties;

    private ExpressionParser parser = new SpelExpressionParser();

    private LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    /**
     * 声明切点
     */
    @Pointcut("within(@com.github.kancyframework.emailplus.spring.boot.aop.annotation.EmailNotice *) " +
            "|| @annotation(com.github.kancyframework.emailplus.spring.boot.aop.annotation.EmailNotice))")
    public void pointCut() {

    }

    @Around("pointCut()")
    public Object monitor(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            EmailNotice annotation = null;
            try {
                Method method = getMethod(joinPoint);
                annotation = method.getDeclaredAnnotation(EmailNotice.class);
                if (Objects.isNull(annotation)){
                    annotation = joinPoint.getTarget().getClass().getAnnotation(EmailNotice.class);
                }

                boolean conditionOnThrowable = false;
                Class<? extends Throwable>[] classes = annotation.classes();
                for (Class<? extends Throwable> throwableClass : classes) {
                    if (throwableClass.isAssignableFrom(throwable.getClass())){
                        conditionOnThrowable = true;
                    }
                }

                boolean noThrow = false;
                Class<? extends Throwable>[] noThrows = annotation.noThrows();
                for (Class<? extends Throwable> throwableClass : noThrows) {
                    if (throwableClass.isAssignableFrom(throwable.getClass())){
                        noThrow = true;
                    }
                }

                if (!conditionOnThrowable){
                    if (noThrow){
                        return result;
                    }
                    throw throwable;
                }

                Object[] arguments = joinPoint.getArgs();
                String spel = annotation.value();
                String[] params = discoverer.getParameterNames(method);
                EvaluationContext context = new StandardEvaluationContext();
                for (int len = 0; len < params.length; len++) {
                    context.setVariable(params[len], arguments[len]);
                }
                try {
                    Expression expression = parser.parseExpression(spel);
                    System.out.println(expression.getValue(context));
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }

                NoticeProperties noticeProperties = emailplusProperties.getEmailNotices().get(annotation.value());
                EmailNoticeTrigger emailNoticeTrigger = findEmailNoticeTrigger(noticeProperties.getTrigger());
                if (Objects.isNull(emailNoticeTrigger)){
                    if (noThrow){
                        return result;
                    }
                    throw throwable;
                }

                if (emailNoticeTrigger.isTrigger(annotation.value())){
                    try {
                        // 发送邮件
                        EmailDefinition emailDefinition = emailplusProperties.getEmailDefinitions().get(noticeProperties.getEmailKey());
                        if (Objects.nonNull(emailDefinition.getTemplate())){
                            Map<String, Object> templateData = new HashMap<>();
                            templateData.put("ex", throwable);
                            templateData.put("methodName", method.getName());
                            templateData.put("className", joinPoint.getTarget().getClass().getName());
                            templateData.put("name", annotation.value());
                            templateData.put("notice", noticeProperties);
                            templateData.put("email", emailDefinition);
                            templateData.put("emailKey", noticeProperties.getEmailKey());
                            emailplusService.sendTemplateEmail(noticeProperties.getEmailKey(), templateData);
                        } else {
                            emailplusService.sendSimpleEmail(noticeProperties.getEmailKey());
                        }
                        log.info("Trigger [{}] email notice, and send email [{}] success.", annotation.value(), noticeProperties.getEmailKey());
                    } catch (Exception e) {
                        log.warn("Trigger [{}] email notice, but send email [{}] fail.", annotation.value(), noticeProperties.getEmailKey());
                    }
                }
                if (noThrow){
                    return result;
                }
                throw throwable;
            } finally {
                doBeforeReturn(annotation, throwable);
            }
        }
        return result;
    }

    private void doBeforeReturn(EmailNotice annotation, Throwable throwable) {
        if (annotation.log()){
            log.error("程序运行异常：", throwable);
        }
    }

    private EmailNoticeTrigger findEmailNoticeTrigger(String beanName) {
        return emailNoticeTriggerMap.getOrDefault(beanName,
                emailNoticeTriggerMap.get(String.format("%s%s", beanName, EmailNoticeTrigger.class.getSimpleName())));
    }

    private Method getMethod(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        if (method.getDeclaringClass().isInterface()) {
            try {
                method = joinPoint
                        .getTarget()
                        .getClass()
                        .getDeclaredMethod(joinPoint.getSignature().getName(),
                                method.getParameterTypes());
            } catch (SecurityException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return method;
    }

}
