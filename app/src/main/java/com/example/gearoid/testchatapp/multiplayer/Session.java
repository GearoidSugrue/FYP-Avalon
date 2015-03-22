package com.example.gearoid.testchatapp.multiplayer;

import com.esotericsoftware.kryonet.Connection;
import com.example.gearoid.testchatapp.singletons.Player;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by gearoid on 25/02/15.
 */
public class Session {

    //Client


    //Server
    public static ArrayList<Player> allPlayers; //Won't be instantiated until Session object is created TODO fix allPlayers ArrayList

    public Session(){//Find a better way??..
        allPlayers = new ArrayList<>();
    }


    public void host(){

    }

    public void join(String hostAddress){

    }


}
