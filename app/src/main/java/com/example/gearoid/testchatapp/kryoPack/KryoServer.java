package com.example.gearoid.testchatapp.kryoPack;

//package com.example.gearoid.testchatapp.kyro;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by gearoid on 15/01/15.
 */
public class KryoServer {

	private Server server;

	public KryoServer() {

		System.out.println("[Server] Server starting.");

		server = new Server();
		
		KryoRegisterAndPort.register(server);// Import KryoRegisterAndPort to remove 'KryoRegisterAndPort.'

		server.addListener(new ServerNetListener());
		
//		ClientNetListener netListener = new ClientNetListener();
//		netListener.init(client);
//		client.addListener(netListener);

		try {
			server.bind(KryoRegisterAndPort.TCP_PORT, KryoRegisterAndPort.UDP_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}

		server.start();

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

    public void closeServer() {
        server.close();
    }

}
