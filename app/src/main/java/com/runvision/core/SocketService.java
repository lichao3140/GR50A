package com.runvision.core;

/**
 * Created by Administrator on 2018/4/9.
 */

import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import com.runvision.utils.JsonTools;
import com.runvision.utils.LogToFile;

import org.json.JSONStringer;

public class SocketService {

    private int port;
    private boolean isEnable;

    private ExecutorService threadPool;
    private ServerSocket socket;
    private Socket remotePeer;

    private Handler handler = null;

    public SocketService(int port, Handler handler) {
        this.port = port;
        threadPool = Executors.newCachedThreadPool();
        this.handler = handler;


    }

    /**
     * 寮�鍚痵erver
     */
    public void startServerAsync() {
        isEnable = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                doProcSync();
            }
        }).start();
    }

    /**
     * 鍏抽棴server
     */
    public void stopServerAsync() throws IOException {
        if (!isEnable) {

        } else {
            isEnable = false;
        }
        if (remotePeer != null) {
            remotePeer.close();
            remotePeer = null;
        }
        if (socket != null) {
            socket.close();
        }
        socket = null;
    }

    private void doProcSync() {
        try {
            InetSocketAddress socketAddress = new InetSocketAddress(port);
            socket = new ServerSocket();

            socket.bind(socketAddress);

            while (isEnable) {
                Log.i("json", "i come socket");
                remotePeer = socket.accept();
                threadPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("json", remotePeer.getRemoteSocketAddress().toString());
                        LogToFile.i("json", remotePeer.getRemoteSocketAddress().toString());
                        onAcceptRemotePeer();
                    }
                });
            }
        } catch (IOException e) {
            Log.e("socket", "doProcSync: error");
            e.printStackTrace();
        }
    }

    private void onAcceptRemotePeer() {
        try {
            InputStream inputStream = remotePeer.getInputStream();
            byte buffer[] = new byte[1024 * 4];
            int temp = 0;

            while ((temp = inputStream.read(buffer)) != -1) {

                String s = new String(buffer, 0, temp, "UTF-8");

                String msg[] = s.split(stuffEnd);

                for (String s1 : msg) {
                    LogToFile.i("socket", "接收到json数据：" + s1 + stuffEnd);
                    System.out.println("接收到json数据：" + s1);
                    if (s1.contains(stuffBegin)) {
//                        System.out.println("截取头部:"+s1.substring(s1.indexOf(stuffBegin),stuffBegin.length()));
                        if (s1.substring(s1.indexOf(stuffBegin), stuffBegin.length()).equals(stuffBegin)) {
//                            System.out.println("接收到json数据:"+s1.substring(stuffBegin.length()));
                            JsonTools.parseStringWithJSON(s1.substring(stuffBegin.length()), handler);
                        }
                    }
                }

            }

        } catch (IOException e) {
            Log.i("socket", "onAcceptRemotePeer: ");
            LogToFile.i("socket", "onAcceptRemotePeer: socket中断" + e.getMessage());
            Log.e("socket", "onAcceptRemotePeer: error");
            e.printStackTrace();
            try {
                remotePeer.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            remotePeer = null;
        }
    }

    public void mOutputStream() {
        try {//
            String sendJson = JsonTools.parseJSONWithString();
            sendJson = stuffBegin + sendJson + stuffEnd;
            System.out.println("发送的json数据：" + sendJson);
            LogToFile.i("socket", "发送的json数据：" + sendJson);
            remotePeer.getOutputStream().write(sendJson.getBytes());
        } catch (Exception e) {
            Log.e("sulin", "mOutputStream");
            e.printStackTrace();
        }
    }

    private String stuffBegin = "<!begin>";
    private String stuffEnd = "<!end>";

    private String getMessage(String str) {


        int i = str.lastIndexOf(stuffBegin);
        int j = str.lastIndexOf(stuffEnd);
        return str.substring(i + stuffBegin.length(), j);
    }

}
