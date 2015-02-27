package com.example.gearoid.testchatapp.character;

public class CharacterFactory {

	public static ICharacter createPlayer(String character) {

		if (character.equalsIgnoreCase(CharaConstants.GOOD)) {
			return new GoodCharacter();
		} else if (character.equalsIgnoreCase(CharaConstants.EVIL)) {
			return new EvilCharacter();
		} else if (character.equalsIgnoreCase(CharaConstants.MERLIN)) {
			return new Merlin();
		} else if (character.equalsIgnoreCase(CharaConstants.PERCIVAL)) {
			return new Percival();
		} else if (character.equalsIgnoreCase(CharaConstants.ASSASSIN)) {
			return new Assassin();
		} else if (character.equalsIgnoreCase(CharaConstants.MORDRED)) {
			return new Mordred();
		} else if (character.equalsIgnoreCase(CharaConstants.OBERON)) {
			return new Oberon();
		} else if (character.equalsIgnoreCase(CharaConstants.MORGANA)) {
			return new Morgana();
		}		
		throw new IllegalArgumentException("No such character!"); //Remove this???
	}
}