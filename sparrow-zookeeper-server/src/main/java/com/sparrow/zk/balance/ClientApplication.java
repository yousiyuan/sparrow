package com.sparrow.zk.balance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

//TODO: 客户端主线程入口类
public class ClientApplication {

    public static void main(String[] args) throws Exception {
        //TODO: 客户端只要启动，马上就去拉取Zookeeper注册中心的服务信息
        ClientRequestHandler client = new ClientRequestHandler();

        //region 模拟客户端调用
        //TODO: 通过控制台输入，模拟用户请求，调用注册中心的服务
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        //TODO: 监控客户端行为
        while (true) {
            String name;
            try {
                name = console.readLine();
                if ("exit".equals(name)) {
                    System.exit(0);
                }
                //TODO: 模拟客户端调用远程服务
                client.accessService(name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //endregion
    }

}
