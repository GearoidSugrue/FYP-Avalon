package com.example.gearoid.testchatapp.character.evil;

import com.example.gearoid.testchatapp.character.ConstantsChara;
import com.example.gearoid.testchatapp.character.ICharacter;
import com.example.gearoid.testchatapp.character.good.Merlin;
import com.example.gearoid.testchatapp.character.good.Percival;

public class Morgana extends EvilCharacter {
	
	@Override
	public String getCharacterName() {
		return ConstantsChara.MORGANA;
	}
	
	@Override
	public boolean isVisibleTo(ICharacter character) {
		
		if(character instanceof EvilCharacter && character instanceof Oberon == false){
			return true;
		} else if(character instanceof Percival){
			return true;
		} else if(character instanceof Merlin){
			return true;
		}
		return false;
	}

    @Override
    public String getShortDescription() {
        return "Appears as Merlin";
    }

}
