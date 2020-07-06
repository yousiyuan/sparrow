package com.sparrow.backend.api.listener;

import com.sparrow.common.ComUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;

import javax.servlet.ServletContext;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;

/**
 * 在 ServletWebServerApplicationContext 已经刷新 并且 WebServer已经准备好 之后 发布的事件
 * 用于获取正在运行的服务器的本地端口
 * 正常情况下，它将被启动，哈哈哈
 */
@Slf4j
public class WebServerListener implements ApplicationListener<ServletWebServerInitializedEvent> {

    @Autowired
    public WebServerListener(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    private ServletContext servletContext;

    private String root;

    String getRoot() {
        return root;
    }

    @Override
    public void onApplicationEvent(@NonNull ServletWebServerInitializedEvent webServerInitializedEvent) {
        this.root = getHostAddress(servletContext.getContextPath(), webServerInitializedEvent.getWebServer().getPort());
    }

    private static String getHostAddress(String contextPath, Integer port) {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            if (localHost == null) {
                return null;
            }
            return MessageFormat.format("http://{0}:{1}{2}", localHost.getHostAddress(), String.valueOf(port), contextPath);
        } catch (UnknownHostException e) {
            log.error(ComUtils.printException(e));
            return null;
        }
    }
}
