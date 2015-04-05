package com.example.gearoid.testchatapp.character;

public interface ICharacter {

	public void setPlayerName(String playerName);
	public String getPlayerName();

    public void setCharacterName(String name);
    public String getCharacterName();

    public String getShortDescription();

    public boolean isVisibleTo(ICharacter character);
    public boolean getAllegiance();

    public void setIsOnQuest(boolean isOnQuest);
    public boolean getIsOnQuest();

}
