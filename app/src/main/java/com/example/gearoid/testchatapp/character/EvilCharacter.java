package com.example.gearoid.testchatapp.character;

public class EvilCharacter implements ICharacter {

	boolean isLeader = false;
	boolean hasLadyOfLake = false;
    public boolean isOnQuest = false;
    String characterName = ConstantsChara.MINION_OF_MORDRED;
    String shortDescription = "Minion of Mordred";
	String playerName = "";

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

		if(character instanceof EvilCharacter && character instanceof Oberon == false){
			return true;
		} else if(character instanceof Merlin){
			return true;
		}		
		return false;
	}

    @Override
    public boolean getAllegiance() {
        return false;
    }

    @Override
    public void setIsLeader(boolean isLeader) {
        this.isLeader = isLeader;
    }

    @Override
    public boolean getIsLeader() {
        return this.isLeader;
    }

    @Override
    public void setIsLadyOfLake(boolean hasLadyOfLake) {
        this.hasLadyOfLake = hasLadyOfLake;
    }

    @Override
    public boolean getIsLadyOfLake() {
        return this.hasLadyOfLake;
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