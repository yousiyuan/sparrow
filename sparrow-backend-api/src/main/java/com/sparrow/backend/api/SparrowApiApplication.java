package com.sparrow.backend.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableEurekaClient //启用Eureka客户端
@EnableAsync(proxyTargetClass = true)
@SpringBootApplication
public class SparrowApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SparrowApiApplication.class, args);
    }

}
