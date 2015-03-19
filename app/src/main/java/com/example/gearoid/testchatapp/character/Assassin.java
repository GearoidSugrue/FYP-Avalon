package com.example.gearoid.testchatapp.character;

public class Assassin extends EvilCharacter {

	@Override
	public String getCharacterName() {
		return ConstantsChara.ASSASSIN;
	}

    @Override
    public String getShortDescription() {
        return "Attempts to assassinate Merlin";
    }
}
