package com.example.gearoid.testchatapp.singletons;


import com.esotericsoftware.kryonet.Connection;

public class PlayerConnection {

    //PlayerConnectionID playerConId = new PlayerConnectionID();

    public static PlayerConnection instance;

    public int playerID = -1;
    public Connection playerConnection = null;

	public String userName = "";

    public PlayerConnection(){//change back to private

    }

    public static PlayerConnection getInstance(){

        if(instance == null){
            instance = new PlayerConnection();
        }
        return instance;
    }



}
