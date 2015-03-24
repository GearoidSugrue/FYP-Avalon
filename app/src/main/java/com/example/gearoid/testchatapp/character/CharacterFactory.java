package com.example.gearoid.testchatapp.character;

import android.util.Log;

public class CharacterFactory {

    public enum CharacterType {
        KNIGHT, MINION, MERLIN, PERCIVAL, ASSASSIN, MORDRED, OBERON, MORGANA
    }

    public static ICharacter createPlayer(CharacterType CHARACTER) {

        switch (CHARACTER) {
            case KNIGHT:
                return new GoodCharacter();
            case MINION:
                return new EvilCharacter();
            case MERLIN:
                return new Merlin();
            case PERCIVAL:
                return new Percival();
            case ASSASSIN:
                return new Assassin();
            case MORDRED:
                return new Mordred();
            case OBERON:
                return new Oberon();
            case MORGANA:
                return new Morgana();

            default:
                Log.e("CharacterFactory", "Packet name not found!!! Returning NULL!");
                return null;
        }

    }

}