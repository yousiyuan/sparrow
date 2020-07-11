package com.sparrow.backend.api.election;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.List;

/**
 * 基于Zookeeper注册中心实现选举策略与哨兵机制
 */
@Slf4j
@Component
public class ZkElectionStrategy implements ApplicationRunner {

    public ZkElectionStrategy(ZkClient zkClient) {
        this.zkClient = zkClient;
    }

    @Value("${server.port}")
    private Integer serverPort;

    private ZkClient zkClient;

    private static final String ELECTION_PATH = "/master/election";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.createEphemeral();
        //③ 使用事件监听临时节点是否被删除，如果接收到临时节点被删除的事件通知，重新开始选举（即，重新开始创建临时节点）。
        zkClient.subscribeChildChanges("/master", (String parentPath, List<String> childNodes) -> {
            if (childNodes == null || childNodes.size() <= 0) {
                //④ 重新选举
                this.createEphemeral();
            }
        });
    }

    //① 服务启动的时候在ZK上创建相同的一个临时节点
    private void createEphemeral() {
        if (!zkClient.exists("/master")) {
            zkClient.createPersistent("/master");
        }
        try {
            String localAddress = InetAddress.getLocalHost().getHostAddress() + ":" + serverPort;
            zkClient.createEphemeral(ELECTION_PATH, localAddress);
            //② 谁能够创建成功，谁就是主服务器
            ElectionMaster.IS_SURVIVAL = true;
            log.info("主节点选举成功 {}", localAddress);
        } catch (Exception ignored) {
            ElectionMaster.IS_SURVIVAL = false;
        }
    }

}
