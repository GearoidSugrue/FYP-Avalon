package com.example.gearoid.testchatapp.singletons;


import com.esotericsoftware.kryonet.Connection;
import com.example.gearoid.testchatapp.character.ICharacter;

public class Player {

    //PlayerConnectionID playerConId = new PlayerConnectionID();

    private static Player instance;

    public int playerID = -1;
    public Connection playerConnection = null;

	public String userName = "";
    public ICharacter character;
	
	public boolean isLeader = false;
	public boolean hasLadyOfLake = false;

    private Player(){

    }

    public static Player getInstance(){

        if(instance == null){
            instance = new Player();
        }
        return instance;
    }

}
