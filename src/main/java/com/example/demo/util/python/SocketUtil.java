package com.example.demo.util.python;

import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.Socket;

public class SocketUtil {

    public static void testSocket(Integer type, String filePath, String saveFilePath) throws IOException {
        JSONObject jsonObject = new JSONObject();
        //1-预测电量，2-电力定价
        jsonObject.put("type", type);
        jsonObject.put("filePath", filePath);
        jsonObject.put("saveFilePath", saveFilePath);
        String str = jsonObject.toJSONString();
        // 访问服务进程的套接字
        Socket socket = null;
        try {
            // 初始化套接字，设置访问服务的主机和进程端口号，HOST是访问python进程的主机名称，可以是IP地址或者域名，PORT是python进程绑定的端口号
            socket = new Socket("127.0.0.1",12345);
            // 获取输出流对象
            OutputStream os = socket.getOutputStream();
            PrintStream out = new PrintStream(os);
            // 发送内容
            out.print(str);
            // 告诉服务进程，内容发送完毕，可以开始处理
            out.print("over");
            // 获取服务进程的输入流
            InputStream is = socket.getInputStream();
            String text = readInputStreamAsString(is);
            System.out.println(text);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {if(socket!=null) socket.close();} catch (IOException e) {}
            System.out.println("远程接口调用结束.");
        }
    }

    public static String readInputStreamAsString(InputStream inputStream) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    public static void main(String[] args) throws IOException {
        testSocket(1,"filePath","saveFilePath");
    }
}
