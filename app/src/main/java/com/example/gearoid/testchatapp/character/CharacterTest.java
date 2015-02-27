package com.example.gearoid.testchatapp.character;

import java.util.LinkedList;

public class CharacterTest {

	public static void main(String[] args) {

		int numberOfPlayers = 10;
		int numberOfGood = 4;
		int numberOfEvil = 3;
		int numberOfMerlin = 1;
		int numberOfPercival = 1;
		int numberOfMordred = 1;

		LinkedList<ICharacter> allPlayers = new LinkedList<ICharacter>();

		for (int i = 0; i < numberOfGood; i++) {
			ICharacter c = CharacterFactory.createPlayer("Good");
			c.setPlayerName("Good_" + i);
			allPlayers.add(c);
		}
		for (int i = 0; i < numberOfEvil; i++) {
			ICharacter c = CharacterFactory.createPlayer("Evil");
			c.setPlayerName("Evil" + i);
			allPlayers.add(c);
		}
		ICharacter merly = CharacterFactory.createPlayer("Merlin");
		merly.setPlayerName("Merly");
		ICharacter percy = CharacterFactory.createPlayer("Percival");
		percy.setPlayerName("Percy");
		ICharacter mordy = CharacterFactory.createPlayer("Mordred");
		mordy.setPlayerName("Mordy");
		ICharacter morganitus = CharacterFactory.createPlayer("Morgana");
		morganitus.setPlayerName("Morganitus");
		ICharacter ninja = CharacterFactory.createPlayer("Assassin");
		ninja.setPlayerName("Ninja");
		ICharacter obiWon = CharacterFactory.createPlayer("Oberon");
		obiWon.setPlayerName("Obi Won");

		allPlayers.add(merly);
		allPlayers.add(percy);
		allPlayers.add(mordy);
		allPlayers.add(morganitus);
		allPlayers.add(ninja);
		allPlayers.add(obiWon);
		
		ICharacter player1 = allPlayers.get(0); // Good
		System.out.println(player1.getCharacterName() + ": "
				+ player1.getPlayerName() + " can see the following:");
		for (int i = 0; i < allPlayers.size(); i++) {
			if (i != 5) {
				ICharacter otherPlayer = allPlayers.get(i);
				if (otherPlayer.isVisableTo(player1)) {
					System.out.println(otherPlayer.getCharacterName() + ": "
							+ otherPlayer.getPlayerName());
				}
			}
		}
		System.out.println();

		ICharacter player6 = allPlayers.get(5); // evil
		System.out.println(player6.getCharacterName() + ": "
				+ player6.getPlayerName() + " can see the following:");

		for (int i = 0; i < allPlayers.size(); i++) {
			if (i != 5) {
				ICharacter otherPlayer = allPlayers.get(i);
				if (otherPlayer.isVisableTo(player6)) {
					System.out.println(otherPlayer.getCharacterName() + ": "
							+ otherPlayer.getPlayerName());
				}
			}
		}
		System.out.println();

		ICharacter player8 = allPlayers.get(7); // Merlin
		
		if(player8 instanceof GoodCharacter){
			System.out.println("A Good character.");
		}
		
		System.out.println(player8.getCharacterName() + ": "
				+ player8.getPlayerName() + " can see the following:");
		for (int i = 0; i < allPlayers.size(); i++) {
			if (i != 7) {
				ICharacter otherPlayer = allPlayers.get(i);
				if (otherPlayer.isVisableTo(player8)) {
					System.out.println(otherPlayer.getCharacterName() + ": "
							+ otherPlayer.getPlayerName());
				}
			}
		}
		System.out.println();

		ICharacter player9 = allPlayers.get(8); // Percival
		System.out.println(player9.getCharacterName() + ": "
				+ player9.getPlayerName() + " can see the following:");
		for (int i = 0; i < allPlayers.size(); i++) {
			if (i != 8) {
				ICharacter otherPlayer = allPlayers.get(i);
				if (otherPlayer.isVisableTo(player9)) {
					System.out.println(otherPlayer.getCharacterName() + ": "
							+ otherPlayer.getPlayerName());
				}
			}
		}
		System.out.println();

		ICharacter player10 = allPlayers.get(9); // Mordred
		System.out.println(player10.getCharacterName() + ": "
				+ player10.getPlayerName() + " can see the following:");
		for (int i = 0; i < allPlayers.size(); i++) {
			if (i != 9) {
				ICharacter otherPlayer = allPlayers.get(i);
				if (otherPlayer.isVisableTo(player10)) {
					System.out.println(otherPlayer.getCharacterName() + ": "
							+ otherPlayer.getPlayerName());
				}
			}
		}
		System.out.println();
		
		ICharacter player11 = allPlayers.get(10); // Morgana
		System.out.println(player11.getCharacterName() + ": "
				+ player11.getPlayerName() + " can see the following:");
		for (int i = 0; i < allPlayers.size(); i++) {
			if (i != 10) {
				ICharacter otherPlayer = allPlayers.get(i);
				if (otherPlayer.isVisableTo(player11)) {
					System.out.println(otherPlayer.getCharacterName() + ": "
							+ otherPlayer.getPlayerName());
				}
			}
		}
		System.out.println();
		
		ICharacter player12 = allPlayers.get(11); // Assassin
		System.out.println(player12.getCharacterName() + ": "
				+ player12.getPlayerName() + " can see the following:");
		for (int i = 0; i < allPlayers.size(); i++) {
			if (i != 11) {
				ICharacter otherPlayer = allPlayers.get(i);
				if (otherPlayer.isVisableTo(player12)) {
					System.out.println(otherPlayer.getCharacterName() + ": "
							+ otherPlayer.getPlayerName());
				}
			}
		}
		System.out.println();
		
		ICharacter player13 = allPlayers.get(12); // Oberon
		
		if(player13 instanceof EvilCharacter){
			System.out.println("An Evil charcter");
		}
		if(player13 instanceof GoodCharacter){
			System.out.println("A Good charcter");
		}
		
		System.out.println(player13.getCharacterName() + ": "
				+ player13.getPlayerName() + " can see the following:");
		for (int i = 0; i < allPlayers.size(); i++) {
			if (i != 12) {
				ICharacter otherPlayer = allPlayers.get(i);
				if (otherPlayer.isVisableTo(player13)) {
					System.out.println(otherPlayer.getCharacterName() + ": "
							+ otherPlayer.getPlayerName());
				}
			}
		}
		System.out.println();

	}

}
