package com.sparrow.common;

import com.sparrow.framework.component.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 1、当创建有序节点(持久和临时)时，一个序号会被追加到路径之后。
 * 2、有序节点和临时节点不能有子节点
 * 3、只有持久节点(无序的) 才可以创建多级子节点
 * 4、有序节点的变更无事件通知
 */
@Slf4j
public class ZkUtils {

    private static ZooKeeper zooClient;
    private static ThreadPoolTaskExecutor taskExecutor;

    static {
        zooClient = SpringContextUtils.getBean(ZooKeeper.class);
        taskExecutor = SpringContextUtils.getBean(ThreadPoolTaskExecutor.class);
    }

    /**
     * 判断节点是否存在
     */
    public static boolean exists(String path) {
        try {
            path = formatPath(path);
            return zooClient.exists(path, true) != null;
        } catch (KeeperException | InterruptedException ex) {
            log.error(ComUtils.printException(ex));
            return false;
        }
    }

    /**
     * 节点值 读取，无事件通知
     */
    public static Object readNode(String path) {
        try {
            path = formatPath(path);
            byte[] nodeData = zooClient.getData(path, true, null);
            return new String(nodeData, StandardCharsets.UTF_8);
        } catch (KeeperException | InterruptedException ex) {
            log.error(ComUtils.printException(ex));
            return "";
        }
    }

    /**
     * 节点值 修改
     */
    public static void setNode(String path, Object value) {
        try {
            path = formatPath(path);
            zooClient.setData(path, ComUtils.str(value).getBytes(StandardCharsets.UTF_8), -1);
        } catch (KeeperException | InterruptedException ex) {
            log.error(ComUtils.printException(ex));
        }
    }

    /**
     * 节点 删除
     */
    public static void deleteNode(String path) {
        final String nodePath = formatPath(path);
        execute(() -> {
            try {
                List<String> pathList = new ArrayList<>();
                getChildNodeList(nodePath, pathList);
                Collections.reverse(pathList);
                for (String item : pathList) {
                    if (zooClient.exists(item, true) != null) {
                        synchronized (ZkUtils.class) {
                            if (zooClient.exists(item, true) != null) {
                                zooClient.delete(item, -1);
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                log.error(ComUtils.printException(ex));
            }
        });
    }

    /**
     * 持久化节点 创建
     */
    public static void addNodePersistent(String path, Object value, List<ACL> acl) {
        String nodePath = formatPath(path);
        execute(() -> create(nodePath, ComUtils.str(value).getBytes(StandardCharsets.UTF_8), acl, CreateMode.PERSISTENT, new ArrayList<>()));
    }

    /**
     * 持久化有序节点 创建，无事件通知
     */
    public static void addNodePersistentSequential(String path, Object value, List<ACL> acl) {
        String nodePath = formatPath(path);
        if (nodePath.indexOf("/") != nodePath.lastIndexOf("/")) {
            log.error("持久有序节点不能创建多级: {} \r\n\r\tat {}", path, Thread.currentThread().getStackTrace()[1].toString());
            return;
        }
        execute(() -> create(nodePath, ComUtils.str(value).getBytes(StandardCharsets.UTF_8), acl, CreateMode.PERSISTENT_SEQUENTIAL, new ArrayList<>()));
    }

    /**
     * 临时节点 创建
     */
    public static void addNodeEphemeral(String path, Object value, List<ACL> acl) {
        String nodePath = formatPath(path);
        if (nodePath.indexOf("/") != nodePath.lastIndexOf("/")) {
            log.error("临时节点不能创建多级: {} \r\n\r\tat {}", path, Thread.currentThread().getStackTrace()[1].toString());
            return;
        }
        execute(() -> create(nodePath, ComUtils.str(value).getBytes(StandardCharsets.UTF_8), acl, CreateMode.EPHEMERAL, new ArrayList<>()));
    }

    /**
     * 临时有序节点 创建，无事件通知
     */
    public static void addNodeEphemeralSequential(String path, Object value, List<ACL> acl) {
        String nodePath = formatPath(path);
        if (nodePath.indexOf("/") != nodePath.lastIndexOf("/")) {
            log.error("临时有序节点不能创建多级: {} \r\n\r\tat {}", path, Thread.currentThread().getStackTrace()[1].toString());
            return;
        }
        execute(() -> create(nodePath, ComUtils.str(value).getBytes(StandardCharsets.UTF_8), acl, CreateMode.EPHEMERAL_SEQUENTIAL, new ArrayList<>()));
    }

    /**
     * 获取节点 递归
     */
    private static void getChildNodeList(String path, List<String> list) {
        try {
            if (zooClient.exists(path, true) == null) {
                return;
            }
            list.add(path);
            // 获取子节点
            List<String> childPathList = zooClient.getChildren(path, true);
            for (String childPath : childPathList) {
                getChildNodeList(path + formatPath(childPath), list);
            }
        } catch (KeeperException | InterruptedException ex) {
            log.error(ComUtils.printException(ex));
        }
    }

    /**
     * 递归创建Zookeeper节点
     *
     * @param nodePath    节点路径
     * @param nodeData    节点数据
     * @param accessLevel 节点访问权限
     * @param nodeType    节点类型：①持久节点；②临时节点；③有序节点；④无序节点
     */
    private static void create(String nodePath, byte[] nodeData, List<ACL> accessLevel, CreateMode nodeType, List<String> pathList) {
        try {
            // 获取父节点
            String parentNodePath = nodePath.substring(0, nodePath.lastIndexOf("/"));
            if ("".equals(parentNodePath)) {//要创建的节点是在根节点下面
                pathList.add(nodePath);
                createNode(pathList, nodeData, accessLevel, nodeType);
                return;
            }
            if (zooClient.exists(parentNodePath, true) != null) {
                pathList.add(nodePath);
                createNode(pathList, nodeData, accessLevel, nodeType);
                return;
            }
            // 创建父节点
            pathList.add(nodePath);
            create(parentNodePath, nodeData, accessLevel, nodeType, pathList);
        } catch (KeeperException | InterruptedException ex) {
            log.error(ComUtils.printException(ex));
        }
    }

    /**
     * 循环创建所有节点
     */
    private static void createNode(List<String> pathList, byte[] nodeData, List<ACL> accessLevel, CreateMode nodeType) throws KeeperException, InterruptedException {
        Collections.reverse(pathList);
        for (int i = 0; i < pathList.size(); i++) {
            String path = pathList.get(i);
            if (zooClient.exists(path, true) == null) {
                synchronized (ZkUtils.class) {
                    if (zooClient.exists(path, true) == null) {
                        zooClient.create(path, i == pathList.size() - 1 ? nodeData : null, accessLevel, nodeType);
                    }
                }
            }
        }
    }

    /**
     * 格式化节点路径
     */
    private static String formatPath(String nodePath) {
        while (nodePath.contains("//")) {
            nodePath = nodePath.replace("//", "/");
        }
        if (!nodePath.startsWith("/")) {
            nodePath = "/" + nodePath;
        }
        while (nodePath.endsWith("/")) {
            nodePath = ComUtils.trimEnd(nodePath, "/");
        }
        return nodePath;
    }

    /**
     * 执行Zookeeper节点操作前的状态判断
     */
    private static void execute(ExecuteAction action) {
        taskExecutor.execute(() -> {
            AtomicInteger counter = new AtomicInteger(0);
            try {
                while (ZooKeeper.States.CONNECTING == zooClient.getState() && counter.incrementAndGet() <= 30) {
                    log.info("Zookeeper正在连接({}) ... ...", counter.get());
                    Thread.sleep(1000);
                }
                if (ZooKeeper.States.CONNECTED == zooClient.getState()) {
                    action.process();
                    return;
                }
                log.error("Zookeeper未处于‘已连接’状态，无法执行任务！！！");
            } catch (InterruptedException ex) {
                log.error(ComUtils.printException(ex));
            }
        });
    }

    interface ExecuteAction {
        void process();
    }
}
