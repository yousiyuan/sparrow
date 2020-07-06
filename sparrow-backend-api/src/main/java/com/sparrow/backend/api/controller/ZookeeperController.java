package com.sparrow.backend.api.controller;

import com.sparrow.backend.service.ThreadTest;
import com.sparrow.common.ComUtils;
import com.sparrow.common.ZkUtils;
import com.sparrow.framework.enums.ReturnEnum;
import com.sparrow.framework.response.BaseResponse;
import com.sparrow.framework.response.BaseResult;
import org.apache.zookeeper.ZooDefs;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/api/zk")
public class ZookeeperController {

    @GetMapping("/node/add")
    public String addNodeTest() {
        ZkUtils.addNodePersistent("a/b/c/d", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);//持久节点
        //ZookeeperUtils.addNodePersistentSequential("zxb", 32, ZooDefs.Ids.OPEN_ACL_UNSAFE);//持久有序节点
        ZkUtils.addNodeEphemeral("ysy", 33, ZooDefs.Ids.OPEN_ACL_UNSAFE);//临时节点
        //ZookeeperUtils.addNodeEphemeralSequential("lnln", 34, ZooDefs.Ids.OPEN_ACL_UNSAFE);//临时有序节点

        //持久节点
//        ZookeeperUtils.addNodePersistent("/a", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/1", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/1", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/2", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/c", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/c/1", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/c/2", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/c/3", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/c/d", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/c/d/1", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/c/d/2", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/c/d/3", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/c/d/4", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/c/d/e", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/c/d/e/1", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/c/d/e/2", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/c/d/e/3", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/c/d/e/4", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/c/d/e/5", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/c/d/e/f", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/c/d/e/f/1", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/c/d/e/f/2", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/c/d/e/f/3", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/c/d/e/f/4", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/c/d/e/f/5", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/c/d/e/f/6", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/c/d/e/f/g", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/c/d/e/f/g/1", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/c/d/e/f/g/2", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/c/d/e/f/g/3", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/c/d/e/f/g/4", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/c/d/e/f/g/5", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/c/d/e/f/g/6", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);
//        ZookeeperUtils.addNodePersistent("/a/b/c/d/e/f/g/7", 31, ZooDefs.Ids.OPEN_ACL_UNSAFE);

        return "创建Zookeeper节点";
    }

    @GetMapping("/node/delete")
    public String deleteNodeTest() {
        ZkUtils.deleteNode("/a");
        //ZookeeperUtils.deleteNode("zxb");//有序节点路径自动追加整型序号
        ZkUtils.deleteNode("/ysy");
        //ZookeeperUtils.deleteNode("lnln/");//有序节点路径自动追加整型序号
        return "删除Zookeeper节点";
    }

    @GetMapping("/node/rw")
    public String rwNodeTest() {
        String key1 = "a/b/c/d/";
        if (ZkUtils.exists(key1)) {
            ZkUtils.setNode(key1, ComUtils.formatDateObj(new Date()));
            System.out.println(ZkUtils.readNode("a/b/c/d/"));
        }
        String key2 = "ysy";
        if (ZkUtils.exists(key2)) {
            ZkUtils.setNode(key2, ComUtils.formatDateObj(new Date()));
            System.out.println(ZkUtils.readNode(key2));
        }
        return "Zookeeper节点读写测试";
    }

    /**
     * 分布式锁演示，保证 ”幂等性“
     */
    @GetMapping("/lock")
    public BaseResult zkTest() {
        ThreadTest threadTest = new ThreadTest();
        for (int i = 0; i < 100; i++) {
            new Thread(threadTest, "thread_app_" + i).start();
        }
        return BaseResponse.success(ReturnEnum.SUCCESS, "测试Redis分布式锁");
    }

}
