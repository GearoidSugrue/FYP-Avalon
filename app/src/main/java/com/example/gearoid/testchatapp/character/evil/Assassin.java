package com.example.gearoid.testchatapp.character.evil;

import com.example.gearoid.testchatapp.character.ConstantsChara;

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
