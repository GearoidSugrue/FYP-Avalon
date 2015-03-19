package com.example.gearoid.testchatapp.character;

public interface ICharacter {

	public void setPlayerName(String playerName);
	public String getPlayerName();

	public String getCharacterName();
    public void setCharacterName(String name);
    public String getShortDescription();
    public boolean isVisableTo(ICharacter character);



    public boolean getLeader();
	public void setLeader(boolean isLeader);
	public boolean getLadyOfLake();
	public void setLadyofLake(boolean hasLadyOfLake);	

	public void getVisablePlayers();//return type...string, integer, player objects???? 
	//may not be needed
}
