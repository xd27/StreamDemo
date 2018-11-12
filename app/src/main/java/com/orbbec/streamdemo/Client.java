package com.orbbec.streamdemo;


import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import android.util.Log;

/**
 * Socket通信的工具类(客户端)
 */
public class Client {
    private static Client mClient;
    private static Socket mSocket;
    // 要连接的服务器IP地址
    private static final String HOST_ADDRESS = "10.10.6.15";
    // 要连接的服务器端口号
    private static final int HOST_PORT = 12580;

    /**
     * 单例模式
     */
    public static Client getInstance() {
        if (mClient == null) {
            mClient = new Client();
        }
        return mClient;
    }

    /**
     * 打开Socket通道,连接服务器
     */
    private static Socket getSocket() {
        if (mSocket == null) {
            try {
                mSocket = new Socket(HOST_ADDRESS, HOST_PORT);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return mSocket;
    }

    /**
     * 打开Socket通道,连接服务器
     */
    public void connect() {
        // TODO Auto-generated method stub
        mSocket = getSocket();
        Log.i("Sender", "socket成功连接");
    }

    /**
     * 断开Socket连接
     */
    public void disconnect() {
        if (mSocket != null && mSocket.isConnected()) {
            try {
             //   getOutputStream().flush();
                mSocket.close();
                mSocket.shutdownOutput();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public OutputStream getOutputStream(){
        try {
            return mSocket.getOutputStream();
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
    public void sendLength(byte[] bytes) {
        try {
            if (!mSocket.isConnected()) {
                mSocket = getSocket();
            }
            OutputStream os = mSocket.getOutputStream();
            os.write(bytes);
            os.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendSPSPPS(byte[] bytes) {
        try {
            if (!mSocket.isConnected()) {
                mSocket = getSocket();
            }
            OutputStream os = mSocket.getOutputStream();
            os.write(bytes);
            os.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendFrame(byte[] bytes) {
        try {
            if (!mSocket.isConnected()) {
                mSocket = getSocket();
            }
            OutputStream os = mSocket.getOutputStream();
            os.write(bytes, 0, bytes.length);
            os.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}