package com.sparrow.backend.api.configuration;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.*;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Zookeeper配置
 */
@Slf4j
@PropertySource("classpath:zookeeper.properties")
public class ZooConfig implements Watcher {

    @Value("${zookeeper.addresses}")
    private String addresses;

    @Value("${zookeeper.session.timeout}")
    private Integer sessionTimeout;

    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    @Bean(destroyMethod = "close")
    public ZooKeeper zooClient() throws IOException {
/*
        //TODO: 在Zookeeper构造器中使用ClientCnxn对象开启一个新线程用于创建连接Zookeeper的对象
        ZooKeeper zooKeeper = new ZooKeeper(addresses, sessionTimeout, this::process);
        //TODO: 在接收到zookeeper连接对象创建成功的事件通知前，可以阻塞当前线程，直到Zookeeper的连接对象可用
        countDownLatch.await();
        //TODO: 将在 process(WatchedEvent event) 事件通知之后执行
        return zooKeeper;
*/
        //ZkClient zkClient = new ZkClient(addresses, sessionTimeout);
        //zkClient.createPersistent();
        //zkClient.createPersistentSequential();

        //zkClient.createEphemeral();
        //zkClient.createEphemeralSequential()

        //zkClient.subscribeDataChanges();
        //zkClient.unsubscribeDataChanges();

        //zkClient.subscribeStateChanges();
        //zkClient.unsubscribeStateChanges();

        //zkClient.subscribeChildChanges();
        //zkClient.unsubscribeChildChanges();

        //TODO: 新启线程异步创建ZooKeeper的连接对象(具体请看Zookeeper构造器)
        return new ZooKeeper(addresses, sessionTimeout, this);
    }

    @Bean("zkClient")
    public ZkClient zkClient() {
        return new ZkClient(addresses, sessionTimeout);
    }

    /**
     * 事件通知
     */
    public void process(WatchedEvent event) {
        String path = event.getPath(); // 节点路径
        KeeperState state = event.getState(); // 事件状态
        EventType type = event.getType(); // 事件类型

        if (KeeperState.SyncConnected == state) { // 连接成功
            if (EventType.None == type) {
                countDownLatch.countDown();
                log.info("#事件通知# zookeeper连接成功 ... ...");
            } else if (EventType.NodeCreated == type) {
                log.info("#事件通知# 新增节点 - {}", path);
            } else if (EventType.NodeDataChanged == type) {
                log.info("#事件通知# 修改节点 - {}", path);
            } else if (EventType.NodeDeleted == type) {
                log.info("#事件通知# 删除节点 - {}", path);
            } else if (EventType.NodeChildrenChanged == type) {
                log.info("#事件通知# 子节点变更 - {}", path);
            } else {
                log.info("#事件通知# - {}", event.toString());
            }
        } else if (KeeperState.Disconnected == state) { // 连接失败
            log.error("#事件通知# 连接失败 - {}", event.toString());
        } else if (KeeperState.AuthFailed == state) { // 认证失败
            log.error("#事件通知# 认证失败 - {}", event.toString());
        } else if (KeeperState.Expired == state) { // 会话过期
            log.error("#事件通知# 会话过期 - {}", event.toString());
        }

        System.out.println(String.format("#事件通知# %s", event.toString()));
    }
}
