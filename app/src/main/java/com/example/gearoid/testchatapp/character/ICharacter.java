package com.example.gearoid.testchatapp.character;

public interface ICharacter {

	public void setPlayerName(String playerName);
	public String getPlayerName();
	public String getCharacterName();
	public boolean getLeader();
	public void setLeader(boolean isLeader);
	public boolean getLadyOfLake();
	public void setLadyofLake(boolean hasLadyOfLake);	
	public boolean isVisableTo(ICharacter character);
	
	public void getVisablePlayers();//return type...string, integer, player objects???? 
	//may not be needed
}
