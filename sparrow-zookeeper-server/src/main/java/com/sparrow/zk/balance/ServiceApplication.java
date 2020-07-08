package com.sparrow.zk.balance;

public class ServiceApplication {

    /**
     * 设置args参数(端口号)
     * idea菜单 > Run > Edit Configurations... > Application > ZkServerSocket > Program arguments: 在文本框输入参数(18080)
     */
    //TODO: 启动服务入口(想象成SpringBoot工程的main主函数)
    public static void main(String[] args) throws Exception {
        if (args == null || args.length <= 0) {
            throw new RuntimeException("没有端口号");
        }

        ServiceRegistHandler server = new ServiceRegistHandler(Integer.valueOf(args[0]));
        //TODO: 启动线程，注册服务
        new Thread(server).start();
    }

}
