package com.feng.component.syslog.config;

import com.feng.component.syslog.aspect.LogAnnotationAspect;
import com.feng.component.syslog.aspect.LogClassMethodAspect;
import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SysLogAutoConfigure {

    @Value("${sys.log.paths}")
    private String paths;

    @Value("${sys.log.annotations}")
    private String annotations;

    /**
     * 注入SysLog注解拦截切面
     */
    @Bean
    public LogAnnotationAspect controllerLogAspect(){
        return new LogAnnotationAspect();
    }

    /**
     * 注入配置类路径方法拦截切面
     */
    @Bean
    @ConditionalOnProperty("sys.log.paths")
    @ConditionalOnExpression("!'${sys.log.paths}'.equals('')")
    public AspectJExpressionPointcutAdvisor configurableClassPathAdvisor() {
        // 配置类路径切面表达式
        String pathExpression = buildAspectExpressionByPaths();
        StringBuilder expressionBuilder = new StringBuilder("(");
        expressionBuilder.append(pathExpression);
        expressionBuilder.append(")");
        // 排除SysLog 注解切面，防止重复打印日志
        expressionBuilder.append(" && !(");
        expressionBuilder.append(LogAnnotationAspect.expression);
        expressionBuilder.append(")");
        // 排除配置注解切面，防止重复打印日志
        String annotationsExpression = buildAspectExpressionByAnnotations();
        if (!"".equals(annotationsExpression)) {
            expressionBuilder.append(" && !(");
            expressionBuilder.append(annotationsExpression);
            expressionBuilder.append(")");
        }

        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
        advisor.setExpression(expressionBuilder.toString());
        advisor.setAdvice(new LogClassMethodAspect());
        return advisor;
    }

    /**
     * 注入配置注解拦截切面
     * 注意需要配置注解完整类路径
     */
    @Bean
    @ConditionalOnProperty(value = "sys.log.annotations")
    @ConditionalOnExpression("!'${sys.log.annotations}'.equals('')")
    public AspectJExpressionPointcutAdvisor configurableAnnotationAdvisor() {
        // 配置注解切面
        String annotationsExpression = buildAspectExpressionByAnnotations();
        StringBuilder expressionBuilder = new StringBuilder("(");
        expressionBuilder.append(annotationsExpression);
        expressionBuilder.append(")");
        // 排除SysLog 注解切面，防止重复打印日志
        expressionBuilder.append(" && !(");
        expressionBuilder.append(LogAnnotationAspect.expression);
        expressionBuilder.append(")");

        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
        advisor.setExpression(expressionBuilder.toString());
        advisor.setAdvice(new LogClassMethodAspect());
        return advisor;
    }

    /**
     * 通过注解配置构建切面表达式
     * @return
     */
    private String buildAspectExpressionByAnnotations() {
        if (annotations == null || "".equals(annotations)) {
            return "";
        }
        String[] annotationArray = annotations.split(",");
        StringBuilder pointCutBuilder = new StringBuilder();
        for (String annotation : annotationArray) {
            pointCutBuilder.append(" || @annotation(");
            pointCutBuilder.append(annotation);
            pointCutBuilder.append(")");
            pointCutBuilder.append(" || @within(");
            pointCutBuilder.append(annotation);
            pointCutBuilder.append(")");
        }
        return pointCutBuilder.toString().replaceFirst(" \\|\\|", "");
    }

    /**
     * 通过类路径配置构建切面表达式
     * @return
     */
    private String buildAspectExpressionByPaths() {
        if (paths == null || "".equals(paths)) {
            return "";
        }
        String[] pathArray = paths.split(",");
        StringBuilder pointCutBuilder = new StringBuilder();
        for (String path : pathArray) {
            pointCutBuilder.append(" || within(");
            pointCutBuilder.append(path);
            pointCutBuilder.append("..*)");
        }
        return pointCutBuilder.toString().replaceFirst(" \\|\\|", "");
    }
}