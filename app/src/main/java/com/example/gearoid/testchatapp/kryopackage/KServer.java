package com.example.gearoid.testchatapp.kryopackage;

//package com.example.gearoid.testchatapp.kyro;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.example.gearoid.testchatapp.multiplayer.Session;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by gearoid on 15/01/15.
 */
public class KServer {

	private Server server;

	public KServer() {

		System.out.println("[Server] Server starting.");

		server = new Server();
		
		KRegisterAndPort.register(server);// Import KryoRegisterAndPort to remove 'KryoRegisterAndPort.'

        Session.serverListener = ListenerServer.initialize();

		server.addListener(Session.serverListener);
		
//		ClientNetListener netListener = new ClientNetListener();
//		netListener.init(client);
//		client.addListener(netListener);

		try {
			server.bind(KRegisterAndPort.TCP_PORT, KRegisterAndPort.UDP_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}

		//server.start();

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

	public void sendToSelectedClients(LinkedList<Integer> connectionIDs, Object obj) {

		for (int i = 0; i < connectionIDs.size(); i++) {
			server.sendToTCP(connectionIDs.get(i), obj);
		}
		server.getConnections();//What this for????
		
	}



	 public Connection[] getConnections(){
		 Connection[] connections = server.getConnections();
		 return connections;
	 }

    public void startServer() {
        System.out.println("[Server] Starting Server.");
        server.start();
    }

    public void closeServer() {
        System.out.println("[Server] Closing Server.");
        server.close();
    }

}
