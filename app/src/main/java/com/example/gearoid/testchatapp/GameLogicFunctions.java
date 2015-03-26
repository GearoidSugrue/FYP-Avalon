package com.example.gearoid.testchatapp;

import com.example.gearoid.testchatapp.multiplayer.Session;

/**
 * Created by gearoid on 25/03/15.
 */
public class GameLogicFunctions {

    public enum Board {
        FIVE, SIX, SEVEN, EIGHT, NINE, TEN;
    }

    public enum Quest {
        FIRST(1), SECOND(2), THIRD(3), FOURTH(4), FIFTH(5);
        private int value;

        private Quest(int value) {
            this.value = value;
        }
    }

    public static Board calculateBoard(int numberOfPlayers) {
        switch (numberOfPlayers) {
            case 5:
                return Board.FIVE;
            case 6:
                return Board.SIX;
            case 7:
                return Board.SEVEN;
            case 8:
                return Board.EIGHT;
            case 9:
                return Board.NINE;
            case 10:
                return Board.TEN;
        }
        return Board.FIVE;
    }

    public static int calculateNumberOfEvilPlayers(Board board) {

        switch (board) {
            case FIVE:
            case SIX:
                return 2;
            case SEVEN:
            case EIGHT:
            case NINE:
                return 3;
            case TEN:
                return 4;
        }
        return 2;
    }

    public static int[] getBoardConfiguration(Board board) {//Returns an int array that holds the number of players needed for each quest

        int[] arr;
        switch (board) {
            case FIVE:
                return arr = new int[]{2, 3, 2, 3, 3};
            case SIX:
                return arr = new int[]{2, 3, 4, 3, 4};
            case SEVEN:
                return arr = new int[]{2, 3, 3, 4, 4};
            case EIGHT:
            case NINE:
            case TEN:
                return arr = new int[]{3, 4, 4, 5, 5};
        }
        return arr = new int[]{0, 0, 0, 0, 0};
    }

    public static int getCurrentLeaderIndexInAllPlayerList() {

        return Session.leaderOrderList.get(Session.leaderOrderIterator);

//        for (int i = 0; i < Session.masterAllPlayers.size(); i++) {
//            if (Session.masterAllPlayers.get(i).isLeader) {
//                return i;
//            }
//        }
//        return 0;
    }

    public static void setNextLeader() {
        Session.masterAllPlayers.get(getCurrentLeaderIndexInAllPlayerList()).isLeader = false;
        Session.masterAllPlayers.get(getNextLeaderIndexInAllPlayerList()).isLeader = true;
        if (Session.leaderOrderIterator == Session.leaderOrderList.size() - 1) {
            Session.leaderOrderIterator = 0;
        } else {
            Session.leaderOrderIterator++;
        }
    }

    public static int getNextLeaderIndexInAllPlayerList() {

        if (Session.leaderOrderIterator ==  Session.leaderOrderList.size() - 1) {
            return  Session.leaderOrderList.get(0);
        } else {
            return  Session.leaderOrderList.get(Session.leaderOrderIterator + 1);
        }

//        ArrayList<Player> players = Session.leaderOrderAllPlayers;
//
//        if (getCurrentLeaderIndexInAllPlayerList() == players.size() - 1) {
//            return 0;
//        } else {
//            return getCurrentLeaderIndexInAllPlayerList() + 1;
//        }
    }

    public static int calculatePlayersRequiredForQuest(Board board, Quest questNumber) {//Move this???
        return GameLogicFunctions.getBoardConfiguration(board)[questNumber.value]; //Returns the number of players that go on a particular quest
    }

    public static int calculateFailRequiredForQuest(Board board, Quest questNumber) {

        if (questNumber == Quest.FOURTH) {
            switch (board) {
                case SEVEN:
                case EIGHT:
                case NINE:
                case TEN:
                    return 2;
            }
        }
        return 1;
    }

}
