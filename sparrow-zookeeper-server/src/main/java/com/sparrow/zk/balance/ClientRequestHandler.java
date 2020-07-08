package com.sparrow.zk.balance;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.sparrow.zk.ThreadUtils;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;

//TODO: 处理所有客户端的请求
class ClientRequestHandler {

    //TODO: 服务注册路径(系统常量)
    final static private String memberServerPath = "/member";

    //TODO: 从zk获取最新获取的注册服务连接
    final private ZkClient zkClient;
    //TODO: 集群中的服务地址列表
    final private ConcurrentHashMap<String, String> listServer;
    //TODO: 默认第1次调用服务
    private int count = 1;

    ClientRequestHandler() throws Exception {
        Map<String, Object> config = ThreadUtils.getConfig("zookeeper.properties");
        String zkAddr = String.valueOf(config.get("zookeeper.addresses"));
        int zkExpire = Integer.valueOf(String.valueOf(config.get("zookeeper.session.timeout")));

        //TODO: 连接Zookeeper注册中心
        zkClient = new ZkClient(zkAddr, zkExpire, zkExpire);

        //TODO: 获取服务列表
        listServer = new ConcurrentHashMap<>();
        List<String> childNodeList = zkClient.getChildren(memberServerPath);//获取所有的服务节点
        for (String p : childNodeList) {
            listServer.put(p, zkClient.readData(memberServerPath + "/" + p));//更新服务列表
        }

        //TODO: 订阅子节点事件，监控子节点的变化，以及动态更新服务列表
        zkClient.subscribeChildChanges(memberServerPath, new ZookeeperRegistryChildListener());

        System.out.println("\r\n\r\n最新服务信息Server List:  " + listServer.values() + "\r\n\r\n");
    }

    //TODO: 通过负载均衡策略获取客户端当前应请求的server
    private String getServer() {
        // 主备策略（默认只使用一台服务器的服务）
//        synchronized (listServer) {
//            // 默认按照服务名排序或者服务注册顺序排序，取第一个
//            TreeMap<String, String> mapListServer = new TreeMap<>(listServer);
//            Iterator<Map.Entry<String, String>> iterator = mapListServer.entrySet().iterator();
//            if (iterator.hasNext()) {
//                return iterator.next().getValue();
//            }
//            throw new RuntimeException("没有可用服务");
//        }

        // 轮询策略(取模算法)
        synchronized (listServer) {
            List<String> list = new ArrayList<>(listServer.values());
            Collections.sort(list);
            String serverAddress = list.get(count % listServer.size());
            this.count += 1;
            return serverAddress;
        }

        // 权重策略(自定义哪些服务器的服务被选择使用的优先级，权重比代表被选择的概率)... ...

        // 选举策略(主从或主备，类似于分布式锁的实现，就是大家都去尝试创建临时节点，谁创建成功，谁就是主节点，没有创建成功的就是备份节点)
    }

    //TODO: 【注意】这里发送的是符合特定网络传输协议的请求报文（请求头；请求体）
    void accessService(String requestMessageForProtocol) {
        //TODO: 通过负载均衡算法获取本次要调用哪一个服务
        String server = this.getServer();
        String[] cfg = server.split(":");

        //TODO: 模拟请求服务
        Socket socket = null;
        PrintWriter outWriter = null;
        BufferedReader inReader = null;
        try {
            socket = new Socket(cfg[0], Integer.parseInt(cfg[1]));
            outWriter = new PrintWriter(socket.getOutputStream(), true);
            inReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("\r\n\r\n请求服务===>>  " + server);
            outWriter.println(requestMessageForProtocol);
            while (true) {
                String resp = inReader.readLine();
                if (resp == null)
                    //TODO: 服务端无返回值
                    break;
                else if (resp.length() > 0) {
                    //TODO: 获取服务端响应
                    System.out.println("Response Content : " + resp + "\r\n\r\n");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inReader != null) inReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (outWriter != null) {
                outWriter.close();
            }
            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //TODO: 事件监听对象
    class ZookeeperRegistryChildListener implements IZkChildListener {
        @Override
        public void handleChildChange(String parentPath, List<String> childNodes) {
            synchronized (listServer) {
                //TODO: 服务断开通知
                if (childNodes == null) {
                    listServer.clear();
                    childNodes = new ArrayList<>();
                    System.out.println("\r\n\r\n##服务断开连接##  " + String.join(",", listServer.values()));
                }
                //TODO: 服务断开通知
                Iterator<Map.Entry<String, String>> mapIter = listServer.size() > childNodes.size()
                        ? listServer.entrySet().iterator()
                        : null;
                while (mapIter != null && mapIter.hasNext()) {
                    Map.Entry<String, String> entry = mapIter.next();
                    if (childNodes.contains(entry.getKey())) {
                        continue;
                    }
                    mapIter.remove();
                    System.out.println("\r\n\r\n##服务断开连接##  " + entry.getValue());
                }
                //TODO: 服务注册通知
                Iterator<String> listIter = listServer.size() < childNodes.size()
                        ? childNodes.iterator()
                        : null;
                while (listIter != null && listIter.hasNext()) {
                    String childNodeName = listIter.next();
                    if (listServer.containsKey(childNodeName)) {
                        continue;
                    }
                    String serverAddress = zkClient.readData(memberServerPath + "/" + childNodeName);
                    listServer.put(childNodeName, serverAddress);
                    System.out.println("\r\n\r\n##新服务注册到Zookeeper注册中心##  " + serverAddress);
                }
                System.out.println("最新服务信息Server List:  " + listServer.values() + "\r\n\r\n");
            }
        }
    }
}
