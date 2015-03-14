package com.example.gearoid.testchatapp.character;

public class CharacterFactory {

	public static ICharacter createPlayer(String character) {

		if (character.equalsIgnoreCase(ConstantsChara.GOOD)) {
			return new GoodCharacter();
		} else if (character.equalsIgnoreCase(ConstantsChara.EVIL)) {
			return new EvilCharacter();
		} else if (character.equalsIgnoreCase(ConstantsChara.MERLIN)) {
			return new Merlin();
		} else if (character.equalsIgnoreCase(ConstantsChara.PERCIVAL)) {
			return new Percival();
		} else if (character.equalsIgnoreCase(ConstantsChara.ASSASSIN)) {
			return new Assassin();
		} else if (character.equalsIgnoreCase(ConstantsChara.MORDRED)) {
			return new Mordred();
		} else if (character.equalsIgnoreCase(ConstantsChara.OBERON)) {
			return new Oberon();
		} else if (character.equalsIgnoreCase(ConstantsChara.MORGANA)) {
			return new Morgana();
		}		
		throw new IllegalArgumentException("No such character!"); //Remove this???
	}
}