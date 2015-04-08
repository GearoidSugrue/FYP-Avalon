package com.example.gearoid.testchatapp.kryopackage.client;

import com.esotericsoftware.kryonet.Client;
import com.example.gearoid.testchatapp.kryopackage.KRegisterAndPort;
import com.example.gearoid.testchatapp.multiplayer.Session;

import java.io.IOException;
import java.net.InetAddress;


/**
 * Created by gearoid on 15/01/15.
 */
public class KClient {

    public Client client;

    public KClient() {

        System.out.println("[Client] Client starting.");

        client = new Client();
        KRegisterAndPort.register(client);

        Session.clientListener = new ListenerClient();
        Session.clientListener.init(client);
        client.addListener(Session.clientListener);

    }

    public Client getClient(){
        return client;
    }

    public InetAddress findServerInetAddress() {
        InetAddress host = client.discoverHost(KRegisterAndPort.UDP_PORT, 2000); // May not work correctly on android
        System.out.println("Server IP Address is: " + host.toString());
        return host;
    }

    public void connectToServer(String host) {//InetAddress host

        new Thread(client).start();
        try {
            client.connect(40000, host, KRegisterAndPort.TCP_PORT,
                    KRegisterAndPort.UDP_PORT);// timeout, IP address, TCP_PORT, UDP Port

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
