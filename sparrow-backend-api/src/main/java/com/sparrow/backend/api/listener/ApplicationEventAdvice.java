package com.sparrow.backend.api.listener;

import com.sparrow.common.ComUtils;
import com.sparrow.common.ZkUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.ZooDefs;
import org.springframework.boot.context.event.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;

import java.util.Date;

/**
 * springboot 启动过程 事件监听处理
 */
@Slf4j
public class ApplicationEventAdvice implements ApplicationListener<ApplicationEvent> {

    @Override
    public void onApplicationEvent(@NonNull ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof ApplicationStartingEvent) {
            System.out.println("springboot应用启动且未作任何处理（除listener注册和初始化）的时候发送ApplicationStartingEvent");
        } else if (applicationEvent instanceof ApplicationEnvironmentPreparedEvent) {
            System.out.println("确定springboot应用使用的Environment且context创建之前发送这个事件");
        } else if (applicationEvent instanceof ApplicationPreparedEvent) {
            log.info("context已经创建且没有refresh发送个事件");
        } else if (applicationEvent instanceof ApplicationStartedEvent) {
            log.info("context已经refresh且application and command-line runners（如果有） 调用之前发送这个事件");
        } else if (applicationEvent instanceof ApplicationReadyEvent) {
            log.info("application and command-line runners （如果有）执行完后发送这个事件，此时应用已经启动完毕.这个事件比较常用，常常在系统启动完后做一些初始化操作");
            ApplicationReadyEvent applicationReadyEvent = (ApplicationReadyEvent) applicationEvent;
            ApplicationContext applicationContext = applicationReadyEvent.getApplicationContext();
            Environment environment = applicationContext.getBean(Environment.class);
            String applicationName = environment.getProperty("spring.application.name");
            WebServerListener webServerListener = applicationContext.getBean(WebServerListener.class);
            System.out.println(String.format("------- Springboot工程 %s 已经启动 -------\r\n访问地址:\r\n%s\r\n", applicationName, webServerListener.getRoot()));

            // TODO：初始化工作
            ZkUtils.addNodeEphemeral("/" + applicationName, ComUtils.formatDateObj(new Date()), ZooDefs.Ids.OPEN_ACL_UNSAFE);
        } else if (applicationEvent instanceof ApplicationFailedEvent) {
            log.info("springboot应用启动失败后产生这个事件");
        }
    }

}
