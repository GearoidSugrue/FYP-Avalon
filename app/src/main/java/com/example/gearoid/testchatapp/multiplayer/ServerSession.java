package com.example.gearoid.testchatapp.multiplayer;

import com.example.gearoid.testchatapp.singletons.Player;
import com.example.gearoid.testchatapp.singletons.ServerInstance;

import java.util.ArrayList;

/**
 * Created by gearoid on 22/03/15.
 */
public class ServerSession {

    //Server
    public ArrayList<Player> allPlayers; //Won't be instantiated until Session object is created
    //public static HashMap<Player, Connection> playerConnections;

    public ServerSession() {//Find a better way??..
        allPlayers = new ArrayList<>();
    }


    public void host() {
        ServerInstance.getServerInstance().getServer().start();
    }
}
