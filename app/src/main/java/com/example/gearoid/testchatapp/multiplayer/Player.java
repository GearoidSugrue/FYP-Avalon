package com.example.gearoid.testchatapp.multiplayer;

import com.example.gearoid.testchatapp.character.ICharacter;

/**
 * Created by gearoid on 31/03/15.
 */
public class Player {


    public int playerID = -1;
    public String userName = "";
    public ICharacter character;

    public boolean isLeader = false;
    public boolean hasLadyOfLake = false;
    public boolean isOnQuest = false;

    public boolean hasUsedLadyOfLake = false;

    public Player(){

    }
}
