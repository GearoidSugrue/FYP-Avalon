package com.example.gearoid.testchatapp.multiplayer;

import com.esotericsoftware.kryonet.Connection;
import com.example.gearoid.testchatapp.character.ICharacter;

/**
 * Created by gearoid on 31/03/15.
 */
public class PlayerBasic{


    public int playerID = -1;
    public String userName = "";
    public ICharacter character;

    public boolean isLeader = false;
    public boolean hasLadyOfLake = false;
    public boolean isOnQuest = false;

    public PlayerBasic(){

    }
}
