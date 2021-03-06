# syslog
公共日志组件，支持以下方式打印日志：
1. @SysLog：指定类或方法上添加此注解便会自动打印日志 
2. 类路径配置：配置类的全路径，对应类的所有方法自动打印日志 
3. 注解配置：配置注解路径，添加了指定注解的类和方法自动打印日志 

## 1、syslog项目打包生成jar包

## 2、项目中引用jar包
   ```
   <dependency>
      <groupId>com.feng.component</groupId>
      <artifactId>syslog</artifactId>
      <version>0.0.1</version>
   </dependency>
   ```
   
## 3、SysLog注解
将SysLog注解注释在类或方法上即可打印日志，其中注解参数内容为：操作描述
```
@SysLog("测试日志打印")
public String test(String str) {
    return str;
}
```

## 4、添加类路径配置
指定路径下类的所有方法打印日志，多个逗号分隔
```
sys.log. paths: com.test.service,com.test.service2
```
## 5、添加注解配置
指定注解注释的类（所有方法）、注释的方法打印日志，多个逗号分割
```
sys.log. annotations: org.springframework.stereotype.Service
```

## 6、自定义日志信息
实现ICustomLog 接口，并将实现类作为bean注入即可
```
@Component
public class DefaultCustomLog implements ICustomLog {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void before(MethodInvocation invocation) {
        log.info("方法执行前");
    }

    @Override
    public void logInfo(MethodInvocation invocation, Object res) {
        log.info("自定义日志信息");
    }

    @Override
    public void errLogInfo(MethodInvocation invocation, Exception e) {
        log.error("方法执行异常时自定义日志信息");
    }

    @Override
    public void after(MethodInvocation invocation, Object res) {
        log.info("方法执行后，返回结果：{}", res);
    }

}
```
