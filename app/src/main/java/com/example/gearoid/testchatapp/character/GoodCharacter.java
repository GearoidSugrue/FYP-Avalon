package com.example.gearoid.testchatapp.character;

public class GoodCharacter implements ICharacter {

	boolean isLeader = false;
	boolean hasLadyOfLake = false;
	String characterName = ConstantsChara.KNIGHT_OF_ARTHUR;//Use constant
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
	public String getCharacterName() {
		return characterName;
	}

	@Override
	public boolean getLeader() {
		return isLeader;
	}

	@Override
	public boolean getLadyOfLake() {
		return hasLadyOfLake;
	}

	@Override
	public void getVisablePlayers() {//return type ...string, players, etc????
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setLeader(boolean isLeader) {
		this.isLeader = true;	
	}

	@Override
	public void setLadyofLake(boolean hasLadyOfLake) {
		this.hasLadyOfLake = true;
	}

	@Override
	public boolean isVisableTo(ICharacter character) {
		return false;//Good characters are visable by no one...(evil see other evil)
	}

}
