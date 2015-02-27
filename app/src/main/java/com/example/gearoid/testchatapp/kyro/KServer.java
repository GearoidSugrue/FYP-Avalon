package com.example.gearoid.testchatapp.kyro;

import com.esotericsoftware.kryonet.Server;

import java.io.IOException;

/**
 * Created by gearoid on 15/01/15.
 */
public class KServer {

    private Server server;

    public KServer() {
        server = new Server();
        //registerPackets();
        KNetwork.register(server);//Import to remove 'KNetwork.'

        server.addListener(new ServerNetListener());

        try {
            server.bind(KNetwork.port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        server.start();

    }


}
