package com.example.demo.util.webSocket;
import org.springframework.stereotype.Controller;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@Controller
@ServerEndpoint("/websocket/{tableId}")
public class WebSocket {
    private Session session;
    public List<Double> IaList;
    public List<Double> IcList;
    public List<Double> IbList;

    public static CopyOnWriteArraySet<WebSocket> webSockets =new CopyOnWriteArraySet<>();
    private static Map<String,Session> sessionPool = new HashMap<String,Session>();

    @OnOpen
    public void onOpen(Session session, @PathParam(value="tableId")String code) {
        this.session = session;
        webSockets.add(this);
        sessionPool.put(code, session);
       // Constants.WEBSOCKET = true;//定义常量  是否开启websocket连接
        System.out.println("【websocket消息】有新的连接，总数为:"+webSockets.size());
    }

    @OnClose
    public void onClose() {
        webSockets.remove(this);
        //Constants.WEBSOCKET = false;
        System.out.println("【websocket消息】连接断开，总数为:"+webSockets.size());
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("【websocket消息】收到客户端消息:"+message);
    }

    // 此为广播消息
    public void sendAllMessage(String message) {
        for(WebSocket webSocket : webSockets) {
            System.out.println("【websocket消息】广播消息:"+message);
            try {
                webSocket.session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 此为单点消息
    public void sendOneMessage(String code, String message) {
        Session session = sessionPool.get(code);
        System.out.println(code);
        /*在发送数据之前先确认 session是否已经打开 使用session.isOpen() 为true 则发送消息
         * 不然会报错:The WebSocket session [0] has been closed and no method (apart from close()) may be called on a closed session */
        if (session != null && session.isOpen()) {
            try {
                session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
