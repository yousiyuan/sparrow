分布式协调工具Zookeeper
1、zookeeper应用场景
(1) 数据的发布与订阅(即，分布式配置中心)
(2) 负载均衡
(3) 命名服务(即，分布式注册中心)
(4) 分布式通知/协调
解读：① 另一种心跳检测机制(ping); ② 另一种系统调度模式; ③ 另一种工作汇报模式;
(5) 集群管理与Master选举
(6) 分布式锁

2、zookeeper实现分布式锁
流程原理：
①临时节点随着会话连接的失效自动删除；
②多个服务在zk上创建同一个临时节点，谁能够创建成功，谁就能拿到锁；
③其它服务没有在zk上创建节点成功，就继续等待(信号量CountDownLatch)；
④使用事件通知获取节点被删除的信号，唤醒程序或解除阻塞，重新尝试获取锁资源；
⑤通过结束当前zk会话的方式删除临时节点
场景：分布式场景，生成全局唯一ID

3、负载均衡
(1) 负载均衡算法：权重、IP绑定、轮询
(2) zookeeper实现负载均衡


