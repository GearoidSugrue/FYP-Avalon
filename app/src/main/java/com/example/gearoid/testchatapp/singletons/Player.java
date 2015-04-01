package com.example.gearoid.testchatapp.singletons;


import com.esotericsoftware.kryonet.Connection;
import com.example.gearoid.testchatapp.ApplicationContext;
import com.example.gearoid.testchatapp.SharedPrefManager;
import com.example.gearoid.testchatapp.character.ICharacter;

import java.io.Serializable;

public class Player {

    //PlayerConnectionID playerConId = new PlayerConnectionID();

    public static Player instance;

    public int playerID = -1;
    public Connection playerConnection = null;

	public String userName = "";
    public ICharacter character;
	
	public boolean isLeader = false;
	public boolean hasLadyOfLake = false;
    public boolean isOnQuest = false;

    public Player(){//change back to private

    }

    public static Player getInstance(){

        if(instance == null){
            instance = new Player();
        }
        return instance;
    }

}
