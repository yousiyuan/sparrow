
1、在idea环境下启动控制台应用程序，通过main函数入参(args)设置程序启动的默认端口号

(1) 找到idea菜单下的设置项：

idea菜单 > Run > Edit Configurations... > Application > ServiceApplication > Program arguments: 文本框

(2) 在配置项 Program arguments 后面的文本框中输入main主函数的入参: 18080



2、在命令行(cmd)通过java命令运行idea自动编译好的class文件

(1) 修改pom.xml文件，引入maven插件maven-dependency-plugin，并配置该插件的参数，目的是将控制台应用程序在运行时依赖的所有jar复制到编译目录target\classes\libs下面。

(2) 运行cmd命令，并进入到target\classes目录下面，如下:

Microsoft Windows [版本 10.0.14393]

(c) 2016 Microsoft Corporation。保留所有权利。

C:\projects\SelfProjects\sparrow\sparrow-zookeeper-server\target\classes>


(3) 通过java命令运行class文件，如下:

java -Djava.ext.dirs=./libs com.sparrow.zk.balance.ServiceApplication 18081

注意：① -Djava.ext.dirs用于指定工程依赖jar包的位置，该参数指定的所有jar包将由扩展类加载器完成加载；② class文件名不能包含文件扩展名 .class；③ class文件名需要带上完整的包名；④ 18081表示main函数入参args，多个参数可用空格分隔，本工程用于设置端口号；⑤ 看清楚，这里是直接执行class文件中的程序main主函数，而不是执行jar包。

这里给出运行示例：

Microsoft Windows [版本 10.0.14393]

(c) 2016 Microsoft Corporation。保留所有权利。

C:\projects\SelfProjects\sparrow\sparrow-zookeeper-server\target\classes>java -Djava.ext.dirs=./libs com.sparrow.zk.balance.ServiceApplication 18081




