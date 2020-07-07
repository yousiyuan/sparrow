package com.sparrow.common;

import com.sparrow.framework.component.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.core.env.Environment;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Zookeeper临时节点的生命周期就是Zookeeper Session会话的生命周期，会话结束自动删除
 */
@Slf4j
public class ZkLock {

    //TODO: zk分布式锁存放路径
    private static final String ZK_LOCK_PATH = "/lock";

    //TODO: zk集群地址
    private static final String ZK_ADDRESSES;

    //TODO: zk连接超时时间
    private static final Integer ZK_SESSIONTIMEOUT;

    //TODO: zk客户端工具
    private ZkClient zkClient;

    //TODO: 事件监听对象
    private IZkDataListener zkDataListener = new ZkLockListener();

    //TODO: 信号量
    private CountDownLatch countDownLatch;

    //TODO: 计数器
    private AtomicInteger counter = new AtomicInteger(0);

    static {
        Environment environment = SpringContextUtils.getBean(Environment.class);
        ZK_ADDRESSES = environment.getProperty("zookeeper.addresses");
        ZK_SESSIONTIMEOUT = Integer.valueOf(Objects.requireNonNull(environment.getProperty("zookeeper.session.timeout")));
    }

    public ZkLock() {
        this.zkClient = new ZkClient(ZK_ADDRESSES, ZK_SESSIONTIMEOUT);
    }

    //TODO: 获取锁
    public void gainLock() {
        if (this.tryGainLock()) {
            System.out.println("\r\n获取锁 - 尝试次数：" + counter.get());
            return;
        }
        this.waitForLock();
        this.gainLock();
    }

    //TODO: 尝试获取锁，成功-true; 失败-false
    private boolean tryGainLock() {
        try {
            counter.incrementAndGet();
            zkClient.createEphemeral(ZK_LOCK_PATH);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    //TODO: 等待锁释放
    private void waitForLock() {
        //TODO: 注册节点信息 - 事件监听
        zkClient.subscribeDataChanges(ZK_LOCK_PATH, zkDataListener);

        //TODO: 等待锁释放
        if (zkClient.exists(ZK_LOCK_PATH)) {
            countDownLatch = new CountDownLatch(1);
            try {
                countDownLatch.await(); //① 阻塞(等待节点被删除)
            } catch (Exception ex) {
                log.error(ComUtils.printException(ex));
            }
        }

        //TODO: 删除节点信息 - 事件监听
        zkClient.unsubscribeDataChanges(ZK_LOCK_PATH, zkDataListener);
    }

    //TODO: 释放锁
    public void releaseLock() {
        System.out.println("释放锁\r\n");
        if (zkClient != null) {
            zkClient.close(); //③ 结束Zookeeper Session会话
        }
    }

    @Override
    protected void finalize() {
        if (zkClient != null) {
            zkClient.close();
        }
    }

    class ZkLockListener implements IZkDataListener {
        @Override
        public void handleDataChange(String dataPath, Object data) {
            // 节点Change事件
        }

        @Override
        public void handleDataDeleted(String dataPath) {
            //FIXME: 节点Delete事件
            if (countDownLatch != null) {
                countDownLatch.countDown(); //② 解除阻塞
            }
        }
    }
}
