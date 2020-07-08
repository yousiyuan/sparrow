package com.sparrow.zk.balance;

import org.I0Itec.zkclient.ZkClient;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import com.sparrow.zk.ThreadUtils;

/**
 * 启动服务，并将服务注册到zk注册中心
 */
final class ServiceRegistHandler implements Runnable {
    private static String zkAddr;
    private static int zkExpire;

    private String hostIp;
    private int port;

    ServiceRegistHandler(int port) throws Exception {
        Map<String, Object> config = ThreadUtils.getConfig("zookeeper.properties");
        zkAddr = String.valueOf(config.get("zookeeper.addresses"));
        zkExpire = Integer.valueOf(String.valueOf(config.get("zookeeper.session.timeout")));
        this.port = port;
        this.hostIp = ThreadUtils.getHostAddress();
    }

    // TODO: 将服务信息注册到注册中心上去(zookeeper作为注册中心)
    private void regServer() {
        ZkClient zkClient = new ZkClient(zkAddr, zkExpire, zkExpire);

        //先创建父节点
        String path = "/member";
        if (!zkClient.exists(path)) {
            zkClient.createPersistent(path);
        }

        //同名服务如果存在就删除
        path = "/member/server-" + port;
        if (zkClient.exists(path)) {
            zkClient.delete(path);
        }

        //创建临时节点，表示将服务注册到zk注册中心
        String value = hostIp + ":" + port;
        zkClient.createEphemeral(path, hostIp + ":" + port);


        System.out.println("\r\n\r\n##服务注册成功###  " + value);
        System.out.println("##服务启动端口##  " + port + "\r\n\r\n");
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            // TODO: 将服务信息注册到注册中心上去(zookeeper作为注册中心)
            regServer();

            Socket socket;
            // TODO：监控客户端请求
            while (true) {
                try {
                    socket = serverSocket.accept();

                    //TODO: 启动子线程，处理来自客户端的服务请求
                    new Thread(new ServiceResponseHandler(socket)).start();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}