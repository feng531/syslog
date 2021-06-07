package com.feng.component.syslog.aspect;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义类方法切面
 */
public class LogClassMethodAspect implements MethodInterceptor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        String methodName = invocation.getMethod().getName();
        String className = invocation.getThis().getClass().getName();
        Object[] arguments = invocation.getArguments();

        Object returnValue = null;
        Exception ex = null;
        long beginTime = System.currentTimeMillis();
        try {
            returnValue = invocation.proceed();
            return returnValue;
        } catch (Exception e) {
            ex = e;
            throw e;
        } finally {
            long cost = System.currentTimeMillis() - beginTime;
            if (ex != null) {
                log.error("[class: {}][method: {}][operator: {}][cost: {}ms][args: {}][发生异常]",
                        className, methodName, "", arguments, ex);
            } else {
                log.info("[class: {}][method: {}][operator: {}][cost: {}ms][args: {}][return: {}]",
                        className, methodName, "", cost, arguments, returnValue);
            }
        }
    }
}
