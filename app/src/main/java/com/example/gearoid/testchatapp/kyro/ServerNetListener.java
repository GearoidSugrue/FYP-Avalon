package com.example.gearoid.testchatapp.kyro;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.example.gearoid.testchatapp.kyro.Packet.*;


/**
 * Created by gearoid on 15/01/15.
 */
public class ServerNetListener extends Listener {

    private Client client;

    public void init(Client cl) {
        this.client = cl;
    }

    public void connected(Connection arg0){
        //do something... "[Server] Someone is trying to connect";
    }

    public void disconnected(Connection arg0){
        //do something... "[Server] Someone is trying to disconnect";


    }

    public void received(Connection c, Object o) {
    if(o instanceof Packet0LoginRequest){
        Packet1LoginResult loginResult = new Packet1LoginResult();
        loginResult.accepted = true;
        c.sendTCP(loginResult);
    }
        if (o instanceof Packet2Message){
            String message = ((Packet2Message) o).message;
            //do something with message...
        }

    }
}
