package com.orbbec.streamdemo;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Observable;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;
public class Sender {
    private Context mContext;
    private static DatagramSocket socket;
    private int port = 12666;
    private String ip = "10.10.6.15";
    private InetAddress address;
    private DatagramPacket packet;

    public Sender(){
        try{
            address = InetAddress.getByName(ip);
            socket = new DatagramSocket(port);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static DatagramSocket getSocket(){
        return socket;
    }

    public void send(byte[] data){
        try{
            if(data!=null){
            packet = new DatagramPacket(data, data.length, address, port);
            socket.send(packet);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public void destroy(){
        if(socket!=null){
            socket.close();
        }
    }
}
