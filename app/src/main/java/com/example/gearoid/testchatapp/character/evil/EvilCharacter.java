package com.example.gearoid.testchatapp.character.evil;

import com.example.gearoid.testchatapp.character.ConstantsChara;
import com.example.gearoid.testchatapp.character.ICharacter;
import com.example.gearoid.testchatapp.character.good.Merlin;

public class EvilCharacter implements ICharacter {

    String characterName = ConstantsChara.MINION_OF_MORDRED;
    String shortDescription = "Minion of Mordred";

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


}