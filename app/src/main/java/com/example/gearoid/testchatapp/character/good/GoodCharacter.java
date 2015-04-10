package com.example.gearoid.testchatapp.character.good;

import com.example.gearoid.testchatapp.character.ConstantsChara;
import com.example.gearoid.testchatapp.character.ICharacter;

public class GoodCharacter implements ICharacter {

    String characterName = ConstantsChara.KNIGHT_OF_ARTHUR;//Use constant
    String shortDescription = "Loyal Servant of Arthur";

	
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

}
