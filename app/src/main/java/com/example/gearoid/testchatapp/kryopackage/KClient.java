package com.example.gearoid.testchatapp.kryopackage;

import com.esotericsoftware.kryonet.Client;
import com.example.gearoid.testchatapp.multiplayer.Session;

import java.io.IOException;
import java.net.InetAddress;


/**
 * Created by gearoid on 15/01/15.
 */
public class KClient {

    public Client client;

    public KClient() {
        // //client.discoverHost....????

        System.out.println("[Client] Client starting.");

        client = new Client();
        KRegisterAndPort.register(client);// Import to remove
        // 'KryoRegisterAndPort.'

        Session.clientListener = new ListenerClient();
        Session.clientListener.init(client);
        client.addListener(Session.clientListener);


        //Everything below can be removed and handled by methods below.

        /*

        // client.start();
        new Thread(client).start();//Stops client from disconnecting immediately after connecting

        if(ipAddress.equalsIgnoreCase("unknown")){
            System.out.println("Server IP Address unknown. Searching for Server... ");

            // May not work correctly on android
            InetAddress host = client.discoverHost(KryoRegisterAndPort.UDP_PORT,
                    2000);
            ipAddress = host.toString();
        }
        System.out.println("Server IP Address is: " + ipAddress);
        // pass in IP and TCP_PORT to constructor????
        try {
            client.connect(20000, ipAddress, KryoRegisterAndPort.TCP_PORT,
                    KryoRegisterAndPort.UDP_PORT);// timeout, IP
            // address, TCP_PORT
            // number
        } catch (IOException e) {
            e.printStackTrace();
        }

        */
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
        // pass in IP and TCP_PORT to constructor????
        try {
            client.connect(20000, host, KRegisterAndPort.TCP_PORT,//was 20000
                    KRegisterAndPort.UDP_PORT);// timeout, IP
            // address, TCP_PORT
            // number
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //delete below methods???
    public void setClientName(String name) {
        if (client.isConnected()) {
            client.setName("SC_" + name);
        }
    }

    public void closeClient() {
        client.close();
    }

    public boolean isConnected() {
        return client.isConnected();
    }

//	public void setConnectionName(String name) {
//		Packet00_ClientDetails myName = new Packet00_ClientDetails();// Separate
//																		// from
//																		// kryo
//		myName.playerName = name;
//		if (client.isConnected()) {// Separate from kryo...
//			client.sendTCP(myName);
//		}
//	}
}
