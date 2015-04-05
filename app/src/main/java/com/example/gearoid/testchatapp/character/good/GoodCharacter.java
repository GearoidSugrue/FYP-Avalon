package com.example.gearoid.testchatapp.character.good;

import com.example.gearoid.testchatapp.character.ConstantsChara;
import com.example.gearoid.testchatapp.character.ICharacter;

public class GoodCharacter implements ICharacter {

	boolean isLeader = false;
	boolean hasLadyOfLake = false;
    public boolean isOnQuest = false;
    String characterName = ConstantsChara.KNIGHT_OF_ARTHUR;//Use constant
    String shortDescription = "Loyal Servant of Arthur";
    String playerName = "";
	
//	public GoodCharacter(){		
//	}
	
	@Override
	public void setPlayerName(String playerName) {
		this.playerName = playerName;		
	}
	
	@Override
	public String getPlayerName() {
		return this.playerName;
	}	
	
	    @Override
    public void setCharacterName(String name) {
        characterName = name;
    }

    @Override
    public String getCharacterName() {
        return characterName;
    }

    @Override
    public String getShortDescription() {
        return shortDescription;
    }

	@Override
	public boolean isVisibleTo(ICharacter character) {
		return false;//Good characters are visible by no one...(evil see other evil)
	}

    @Override
    public boolean getAllegiance() {
        return true;
    }

    @Override
    public void setIsOnQuest(boolean isOnQuest) {
        this.isOnQuest = isOnQuest;
    }

    @Override
    public boolean getIsOnQuest() {
        return this.isOnQuest;
    }
}
