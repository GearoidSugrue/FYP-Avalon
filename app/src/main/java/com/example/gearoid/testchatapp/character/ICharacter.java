package com.example.gearoid.testchatapp.character;

public interface ICharacter {

	public void setPlayerName(String playerName);
	public String getPlayerName();

    public void setCharacterName(String name);
    public String getCharacterName();

    public String getShortDescription();

    public boolean isVisibleTo(ICharacter character);
    public boolean getAllegiance();

    public void setIsLeader(boolean isLeader);
    public boolean getIsLeader();

    public void setIsLadyOfLake(boolean hasLadyOfLake);
    public boolean getIsLadyOfLake();

    public void setIsOnQuest(boolean isOnQuest);
    public boolean getIsOnQuest();

    //public void getVisablePlayers();//return type...string, integer, player objects????
	//may not be needed
}
