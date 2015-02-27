package com.example.gearoid.testchatapp.kyro;

import com.esotericsoftware.kryonet.Client;

import java.io.IOException;

/**
 * Created by gearoid on 15/01/15.
 */
public class KClient {

    public Client client;

    public KClient() {

        client = new Client();
        KNetwork.register(client);//Import to remove 'KNetwork.'

        ClientNetListener netListener = new ClientNetListener();
        netListener.init(client);
        client.addListener(netListener);

        client.start();

        //pass in IP and port to constructor????
        try {
            client.connect(10000, "ip address goes here", KNetwork.port);//timeout, IP address, port number
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
