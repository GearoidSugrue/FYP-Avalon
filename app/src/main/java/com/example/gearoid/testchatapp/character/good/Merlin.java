package com.example.gearoid.testchatapp.character.good;

import com.example.gearoid.testchatapp.character.ConstantsChara;
import com.example.gearoid.testchatapp.character.ICharacter;

public class Merlin extends GoodCharacter {

	@Override
	public String getCharacterName() {
		return ConstantsChara.MERLIN;
	}

	@Override
	public boolean isVisibleTo(ICharacter character) {

        if(character instanceof Percival){
			return true;
		}		
		return false;
	}

    @Override
    public String getShortDescription() {
        return "Knows evil, must remain hidden";
    }
}
