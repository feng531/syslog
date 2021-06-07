package com.feng.component.syslog.aspect;

import com.feng.component.syslog.annotation.SysLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * {@link SysLog} 注解切面
 */
@Aspect
public class LogAnnotationAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public static final String expression = "@annotation(com.feng.component.syslog.annotation.SysLog) || @within(com.feng.component.syslog.annotation.SysLog)";

    @Pointcut(expression)
    public void logPointCut() {}

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        Class<?> targetClass = pjp.getTarget().getClass();
        //类名
        String className = targetClass.getName();
        //方法名
        String methodName = signature.getName();

        // 方法上注解
        SysLog syslog = method.getAnnotation(SysLog.class);
        if (syslog == null) {
            // 类上注解
            syslog = targetClass.getAnnotation(SysLog.class);
        }

        //操作
        String operator =syslog.value();

        long beginTime = System.currentTimeMillis();

        Object returnValue = null;
        Exception ex = null;
        try {
            returnValue = pjp.proceed();
            return returnValue;
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            long cost = System.currentTimeMillis() - beginTime;
            if (ex != null) {
                log.error("[class: {}][method: {}][operator: {}][cost: {}ms][args: {}][发生异常]",
                        className, methodName, operator, pjp.getArgs(), ex);
            } else {
                log.info("[class: {}][method: {}][operator: {}][cost: {}ms][args: {}][return: {}]",
                        className, methodName, operator, cost, pjp.getArgs(), returnValue);
            }
        }

    }
}
