package com.example.gearoid.testchatapp;

import android.util.Log;

import com.example.gearoid.testchatapp.kryopackage.Packet;
import com.example.gearoid.testchatapp.kryopackage.PacketFactory;
import com.example.gearoid.testchatapp.multiplayer.Player;
import com.example.gearoid.testchatapp.multiplayer.Session;
import com.example.gearoid.testchatapp.singletons.PlayerConnection;

import java.util.ArrayList;

import static com.example.gearoid.testchatapp.multiplayer.Session.currentBoard;
import static com.example.gearoid.testchatapp.multiplayer.Session.currentQuest;
import static com.example.gearoid.testchatapp.multiplayer.Session.server_currentProposedTeam;
import static com.example.gearoid.testchatapp.multiplayer.Session.server_questVoteReplies;
import static com.example.gearoid.testchatapp.multiplayer.Session.server_sendToEveryone;
import static com.example.gearoid.testchatapp.multiplayer.Session.server_sendToPlayer;
import static com.example.gearoid.testchatapp.multiplayer.Session.server_sendToSelectedPlayers;
import static com.example.gearoid.testchatapp.multiplayer.Session.server_teamVoteReplies;
import static com.example.gearoid.testchatapp.multiplayer.Session.voteCount;

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
        private boolean result;

        private Quest(int value) {
            this.value = value;
        }

        public int getValue(){
            return this.value;
        }
        public void setScore(boolean result){
            this.result = result;
        }
        public boolean getScore(){
            return this.result;
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

    public static int getCurrentLeaderID() {

        return Session.leaderOrderList.get(Session.leaderOrderIterator);

//        for (int i = 0; i < Session.serverAllPlayers.size(); i++) {
//            if (Session.serverAllPlayers.get(i).isLeader) {
//                return i;
//            }
//        }
//        return 0;
    }

    public static void setNextLeader() {
        Session.allPlayers.get(getCurrentLeaderID()).isLeader = false;
        Session.allPlayers.get(getNextLeaderID()).isLeader = true;
        if (Session.leaderOrderIterator == Session.leaderOrderList.size() - 1) {
            Session.leaderOrderIterator = 0;
        } else {
            Session.leaderOrderIterator++;
        }
    }

    public static int getNextLeaderID() {

        if (Session.leaderOrderIterator ==  Session.leaderOrderList.size() - 1) {
            return  Session.leaderOrderList.get(0);
        } else {
            return  Session.leaderOrderList.get(Session.leaderOrderIterator + 1);
        }

//        ArrayList<Player> players = Session.leaderOrderAllPlayers;
//
//        if (getCurrentLeaderID() == players.size() - 1) {
//            return 0;
//        } else {
//            return getCurrentLeaderID() + 1;
//        }
    }


    public static int calculatePlayersRequiredForQuest(Board board, Quest questNumber) {//Move this???
        return GameLogicFunctions.getBoardConfiguration(board)[questNumber.value - 1]; //Returns the number of players that go on a particular quest
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

    public static Player getUserPlayer(){
        return Session.allPlayers.get(PlayerConnection.getInstance().playerID);
    }

    public static int[] getEvilAllegiancePositions(){
        int[] evilPlayers = new int[calculateNumberOfEvilPlayers(Session.currentBoard)];
        int pos = 0;

        for(int i=0; i < Session.allPlayers.size(); i++){
            Player player = Session.allPlayers.get(i);

            if(!player.character.getAllegiance()){
                evilPlayers[pos] = player.playerID;
                pos++;
            }
        }
        return evilPlayers;
    }

    public static int[] getGoodAllegiancePositions(){

        int[] goodPlayers = new int[Session.allPlayers.size() - calculateNumberOfEvilPlayers(Session.currentBoard)];
        int pos = 0;

        for(int i=0; i < Session.allPlayers.size(); i++){
            Player player = Session.allPlayers.get(i);

            if(player.character.getAllegiance()){
                goodPlayers[pos] = player.playerID;
                pos++;
            }
        }
        return goodPlayers;
    }

    public static int[] convertIntegers(ArrayList<Integer> integers)
    {
        int[] ret = new int[integers.size()];
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = integers.get(i).intValue();
        }
        return ret;
    }

    public static int[] getApprovedPlayerPos(ArrayList<Packet.Packet_TeamVoteReply> allVotes){

        ArrayList<Integer> pos = new ArrayList<>();

        for(int i=0; i < allVotes.size(); i++){

            Packet.Packet_TeamVoteReply aVote = allVotes.get(i);
            if(aVote.vote){
                pos.add(aVote.playerID);
            }
        }

        return convertIntegers(pos);
    }

    public static int[] getRejectedPlayerPos(ArrayList<Packet.Packet_TeamVoteReply> allVotes){

        ArrayList<Integer> pos = new ArrayList<>();

        for(int i=0; i < allVotes.size(); i++){

            Packet.Packet_TeamVoteReply aVote = allVotes.get(i);
            if(!aVote.vote){
                pos.add(aVote.playerID);
            }
        }

        return convertIntegers(pos);
    }

    public static boolean[] getQuestVotesFromPackets(ArrayList<Packet.Packet_QuestVoteReply> questVoteReplies){
        boolean[] votes = new boolean[questVoteReplies.size()];

        for (int i=0; i < questVoteReplies.size(); i ++) {
            votes[i] = questVoteReplies.get(i).vote;
        }
        return votes;
    }



    public static boolean checkIfGoodHaveWon(ArrayList<Quest> completedQuests){
        int goodVictories = 0;
        boolean goodWon = false;

        for(int i=0; i < completedQuests.size(); i++){

            if(completedQuests.get(i).getScore()){
                goodVictories++;
            }
        }

        if(goodVictories >= 3){
            goodWon = true;
        }
        return goodWon;
    }

    public static boolean checkIfEvilHaveWon(ArrayList<Quest> completedQuests){
        int evilVictories = 0;
        boolean evilWon = false;

        for(int i=0; i < completedQuests.size(); i++){

            if(!completedQuests.get(i).getScore()){
                evilVictories++;
            }
        }

        if(evilVictories >= 3){
            evilWon = true;
        }
        return evilWon;
    }

    public static Quest getNextQuest(Quest currentQuest){
        Log.d("GameLogicFunctions", "Getting next Quest. CurrentQuest: " + currentQuest);

        switch (currentQuest){
            case FIRST: return Quest.SECOND;
            case SECOND: return Quest.THIRD;
            case THIRD: return Quest.FOURTH;
            case FOURTH: return Quest.FIFTH;
        }
        return Quest.FIRST;
    }

    public static void startNewTeamSelectPhase(Quest currentQuest){

//        voteCount++;
//        setNextLeader(); //done client side

        Packet.Packet_SelectTeam packetSelectTeam = (Packet.Packet_SelectTeam) PacketFactory.createPack(PacketFactory.PacketType.SELECT_TEAM);
        packetSelectTeam.quest = currentQuest;
        packetSelectTeam.teamSize = GameLogicFunctions.calculateFailRequiredForQuest(currentBoard, currentQuest);

        updateAllPlayersGameState(Session.GameState.TEAM_SELECT);
        server_sendToPlayer(GameLogicFunctions.getCurrentLeaderID(), packetSelectTeam);
    }


    public static void startNewQuest(boolean previousQuestResult){
        Log.d("GameLogicFunctions", "startNewQuest. old Quest: " + currentQuest);

        startNewTeamSelectPhase(getNextQuest(currentQuest));

        //currentQuest = getNextQuest(currentQuest);
        Packet.Packet_StartNextQuest packetStartNextQuest = (Packet.Packet_StartNextQuest) PacketFactory.createPack(PacketFactory.PacketType.START_NEXT_QUEST);
        packetStartNextQuest.previousQuestResult = previousQuestResult;

        server_sendToEveryone(packetStartNextQuest);

    }

    public static void sendPlayersOnQuest(Quest currentQuest, int[] proposedTeam){
        Log.d("GameLogicFunctions", "sendPlayersOnQuest. Current Quest: " + currentQuest);

        updateAllPlayersGameState(Session.GameState.QUEST_VOTE);

        Packet.Packet_QuestVote packetQuestVote = (Packet.Packet_QuestVote) PacketFactory.createPack(PacketFactory.PacketType.QUEST_VOTE);
        packetQuestVote.quest = currentQuest;
        packetQuestVote.teamMemberPos = proposedTeam;

        server_questVoteReplies = new ArrayList<>();

        server_sendToSelectedPlayers(proposedTeam, packetQuestVote);
    }

    public static void endGame(boolean result){

        Packet.Packet_GameFinished packetGameFinished = (Packet.Packet_GameFinished) PacketFactory.createPack(PacketFactory.PacketType.GAME_FINISHED);
        packetGameFinished.gameResult = result;

        server_sendToEveryone(packetGameFinished);
    }

    public static void updateAllPlayersGameState(Session.GameState nextGameState){

        Packet.Packet_UpdateGameState packetGameState = (Packet.Packet_UpdateGameState) PacketFactory.createPack(PacketFactory.PacketType.UPDATE_GAMESTATE);
        packetGameState.nextGameState = nextGameState;
        Session.server_sendToEveryone(packetGameState);
    }

}
