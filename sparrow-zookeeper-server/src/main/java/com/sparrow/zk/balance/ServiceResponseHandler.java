package com.sparrow.zk.balance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * TODO: ServerHandler（可有理解为Controller层）
 */
public class ServiceResponseHandler implements Runnable {
    private Socket socket;

    ServiceResponseHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            //TODO: 根据服务协议解读客户端报文
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            out = new PrintWriter(this.socket.getOutputStream(), true);
            //TODO: 处理客户端请求(例如，根据url之类的信息确认调用Controller层的哪个方法或api)
            String body;
            while (true) {
                body = in.readLine();
                if (body == null)
                    break;
                System.out.println("\r\n\r\nRequest Content: " + body + "\r\n");
                //TODO: 服务端处理来自客户端的请求，将处理结果反馈给客户端
                out.println("Hello, " + body);
            }
        } catch (Exception e) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (out != null) {
                out.close();
            }
            if (this.socket != null) {
                try {
                    this.socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                this.socket = null;
            }
        }
    }
}
