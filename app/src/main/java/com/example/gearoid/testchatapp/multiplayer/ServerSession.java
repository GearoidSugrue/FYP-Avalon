package com.example.gearoid.testchatapp.multiplayer;

import com.example.gearoid.testchatapp.singletons.PlayerConnection;
import com.example.gearoid.testchatapp.singletons.ServerInstance;

import java.util.ArrayList;

/**
 * Created by gearoid on 22/03/15.
 */
public class ServerSession {

    //Server
    public ArrayList<PlayerConnection> allPlayerConnections;
    //public static HashMap<Player, Connection> playerConnections;

    public ServerSession() {//Find a better way??..
        allPlayerConnections = new ArrayList<>();
    }


    public void host() {
        ServerInstance.getServerInstance().getServer().start();

    }
}
