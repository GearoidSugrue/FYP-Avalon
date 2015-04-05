package com.example.gearoid.testchatapp.character.good;

import com.example.gearoid.testchatapp.character.ConstantsChara;

public class Percival extends GoodCharacter {
											
	@Override
	public String getCharacterName() {
		return ConstantsChara.PERCIVAL;
	}

    @Override
    public String getShortDescription() {
        return "Knows Merlin";
    }
}
