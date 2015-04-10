package com.example.gearoid.testchatapp.character;

public interface ICharacter {

    public void setCharacterName(String name);
    public String getCharacterName();

    public String getShortDescription();

    public boolean isVisibleTo(ICharacter character);
    public boolean getAllegiance();

}
