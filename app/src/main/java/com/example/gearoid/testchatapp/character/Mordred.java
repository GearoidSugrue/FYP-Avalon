package com.example.gearoid.testchatapp.character;

public class Mordred extends EvilCharacter{
	
	@Override
	public String getCharacterName() {
		return CharaConstants.MORDRED;
	}
	
	@Override
	public boolean isVisableTo(ICharacter character) {

		if(character instanceof EvilCharacter && character instanceof Oberon == false){
			return true;
		} 	
		return false;
	}
}
