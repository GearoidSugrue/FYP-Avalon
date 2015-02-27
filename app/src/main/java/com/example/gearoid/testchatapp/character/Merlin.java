package com.example.gearoid.testchatapp.character;

public class Merlin extends GoodCharacter {

	@Override
	public String getCharacterName() {
		return "Merlin";// Use constants
	}

	@Override
	public boolean isVisableTo(ICharacter character) {

		if(character instanceof Percival){
			return true;
		}		
		return false;
	}
}
