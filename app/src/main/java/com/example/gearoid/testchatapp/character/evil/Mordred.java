package com.example.gearoid.testchatapp.character.evil;

import com.example.gearoid.testchatapp.character.ConstantsChara;
import com.example.gearoid.testchatapp.character.ICharacter;

public class Mordred extends EvilCharacter {
	
	@Override
	public String getCharacterName() {
		return ConstantsChara.MORDRED;
	}
	
	@Override
	public boolean isVisibleTo(ICharacter character) {

		if(character instanceof EvilCharacter && character instanceof Oberon == false){
			return true;
		} 	
		return false;
	}

    @Override
    public String getShortDescription() {
        return "Unknown to Merlin";
    }
}
