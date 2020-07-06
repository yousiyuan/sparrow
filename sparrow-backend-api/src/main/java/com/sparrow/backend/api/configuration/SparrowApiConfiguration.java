package com.sparrow.backend.api.configuration;

import com.sparrow.backend.api.listener.ApplicationEventAdvice;
import com.sparrow.backend.api.listener.WebServerListener;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@ComponentScan(basePackages = {
        "com.sparrow.framework.aspect",
        "com.sparrow.framework.component",
        "com.sparrow.backend.dao",
        "com.sparrow.backend.service"}
)
@MapperScan(basePackages = {"com.sparrow.backend.dao.mapper"})
@Import(value = {WebServerListener.class, ApplicationEventAdvice.class, JacksonConfig.class, RepositoryConfig.class, ZooConfig.class})
public class SparrowApiConfiguration {

    @Primary
    @Bean("asyncExecutor")
    public ThreadPoolTaskExecutor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(16);
        // 设置最大线程数
        executor.setMaxPoolSize(17);
        // 设置队列容量
        executor.setQueueCapacity(Integer.MAX_VALUE);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(300);
        // 设置默认线程名称
        executor.setThreadNamePrefix("thread-");
        // 设置拒绝策略rejection-policy：当pool已经达到max size的时候，如何处理新任务 CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

}
