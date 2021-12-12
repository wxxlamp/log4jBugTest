package cn.wxxlamp.http;

import java.io.*;
import java.net.*;
import java.util.Objects;

/**
 * @author chenkai
 * @date 2021/12/11 17:57
 */
public class HttpServer implements Runnable {

    private ServerSocket serverSocket;

    public static int PORT=8888;

    public HttpServer() {
        try {
            serverSocket=new ServerSocket(PORT);
        } catch(Exception e) {
            System.out.println("无法启动HTTP服务器:"+e.getLocalizedMessage());
        }
        //无法开始服务器
        if(serverSocket==null) {
            System.exit(1);
        }
        new Thread(this).start();
        System.out.println("HTTP服务器正在运行,端口:"+ PORT);
    }

    @Override
    public void run() {
        while(true) {
            try {
                Socket client;
                client=serverSocket.accept();
                if(client!=null) {
                    System.out.println("连接到服务器的用户:"+client);
                    try {
                        // 第一阶段: 打开输入流
                        BufferedReader in=new BufferedReader(
                                new InputStreamReader(client.getInputStream()));

                        // 读取第一行, 请求地址
                        String line=in.readLine();
                        String resource=line.substring(line.indexOf('/')+1,line.lastIndexOf('/')-5);
                        //获得请求的资源的地址
                        resource=URLDecoder.decode(resource, "UTF-8");

                        // 发送文本形式的首页
                        String contentType="Content-Type: text/html;charset=UTF-8";
                        fileService(resource == null ? "index.html" : resource, client, contentType);
                        closeSocket(client);
                    } catch(Exception e) {
                        System.out.println("HTTP服务器错误:"+e.getLocalizedMessage());
                    }
                }
            } catch(Exception e) {
                System.out.println("HTTP服务器错误:"+e.getLocalizedMessage());
            }
        }
    }


    /**
     * 关闭客户端 socket 并打印一条调试信息.
     */
    void closeSocket(Socket socket) {
        try {
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println(socket + "离开了HTTP服务器");
    }

    /**
     * 读取一个文件的内容并返回给浏览器端.
     * @param fileName 文件名
     * @param socket 客户端 socket.
     */
    private void fileService(String fileName, Socket socket,String contentType) {
        try
        {
            PrintStream out = new PrintStream(socket.getOutputStream(), true);
            File fileToSend = new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource(fileName)).getPath());
            if(fileToSend.exists() && !fileToSend.isDirectory()) {
                out.println("HTTP/1.0 200 OK");
                out.println(contentType);
                out.println("Content-Length: " + fileToSend.length());
                // 根据 HTTP 协议, 空行将结束头信息
                out.println();

                FileInputStream fis = new FileInputStream(fileToSend);
                byte[] data = new byte[fis.available()];
                fis.read(data);
                out.write(data);
                out.close();
                fis.close();
            }
        }catch(Exception e){
            System.out.println("传送文件时出错:" + e.getLocalizedMessage());
        }
    }

    /**
     * 命令行打印用途说明.
     */
    private static void usage() {
        System.out.println("Usage: java HTTPServer <port> Default port is 8080.");
    }

    /**
     * 启动简易 HTTP 服务器
     */
    public static void main(String[] args) {
        try {
            if(args.length != 1) {
                usage();
            } else {
                PORT = Integer.parseInt(args[0]);
            }
        } catch (Exception ex) {
            System.err.println("Invalid port arguments. It must be a integer that greater than 0");
        }

        new HttpServer();   //创建一个
    }
}
