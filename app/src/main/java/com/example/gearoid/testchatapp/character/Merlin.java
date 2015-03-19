package com.example.gearoid.testchatapp.character;

public class Merlin extends GoodCharacter {

	@Override
	public String getCharacterName() {
		return ConstantsChara.MERLIN;// Use constants
	}

	@Override
	public boolean isVisableTo(ICharacter character) {

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
