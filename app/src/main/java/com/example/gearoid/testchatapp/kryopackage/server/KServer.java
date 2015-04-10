package com.example.gearoid.testchatapp.kryopackage.server;


import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.example.gearoid.testchatapp.kryopackage.KRegisterAndPort;
import com.example.gearoid.testchatapp.multiplayer.Session;

import java.io.IOException;

/**
 * Created by gearoid on 15/01/15.
 */
public class KServer {

	private Server server;

	public KServer() {

		System.out.println("[Server] Server starting.");

		server = new Server();
		
		KRegisterAndPort.register(server);

        Session.serverListener = ListenerServer.initialize();

		server.addListener(Session.serverListener);

		try {
			server.bind(KRegisterAndPort.TCP_PORT, KRegisterAndPort.UDP_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public Server getServer(){
        return server;
    }

	public void sendToEveryone(Object obj) {
		server.sendToAllTCP(obj);
	}

	public void sendToSingleClient(int connectionID, Object obj) {
		server.sendToTCP(connectionID, obj);

	}

}
