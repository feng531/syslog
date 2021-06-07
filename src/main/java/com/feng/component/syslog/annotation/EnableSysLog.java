package com.feng.component.syslog.annotation;

import com.feng.component.syslog.config.SysLogAutoConfigure;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SysLogAutoConfigure.class)
public @interface EnableSysLog {

}
