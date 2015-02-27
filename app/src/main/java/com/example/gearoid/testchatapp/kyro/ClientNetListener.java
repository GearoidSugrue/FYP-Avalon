package com.example.gearoid.testchatapp.kyro;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.example.gearoid.testchatapp.kyro.Packet.*;


/**
 * Created by gearoid on 17/01/15.
 */
public class ClientNetListener extends Listener {

    private Client client;

    public void init(Client cl) {
        this.client = cl;
    }

    public void connected(Connection arg0) {
        //do something... "[Client] You are connecting";
        client.sendTCP(new Packet0LoginRequest());
    }

    public void disconnected(Connection arg0) {
        //do something... "[Client] You are disconnecting";


    }

    public void received(Connection c, Object o) {
        if (o instanceof Packet1LoginResult) {
            boolean answer = ((Packet1LoginResult) o).accepted;

            if (answer) {
                //Do Something...

            } else {
                c.close();
            }
        }
        if (o instanceof Packet.Packet2Message) {
            String message = ((Packet.Packet2Message) o).message;
            //do something with message...
        }

    }
}
