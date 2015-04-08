package com.example.gearoid.testchatapp.kryopackage;

import java.util.ArrayList;

import com.example.gearoid.testchatapp.game.GameLogicFunctions;
import com.example.gearoid.testchatapp.multiplayer.Player;
import com.example.gearoid.testchatapp.multiplayer.Session;
import com.example.gearoid.testchatapp.singletons.PlayerConnection;


/**
 * Created by gearoid on 15/01/15.
 */
public class Packet {

    public static class Packet_RequestDetails extends Packet { public PlayerConnection playerConnection; }
    public static class Packet_SendDetails extends Packet { public int newPlayerNumber = -1;}

    public static class Packet_TeamVote extends Packet { public int[] proposedTeam; public GameLogicFunctions.Quest quest; public int voteCount;}
    public static class Packet_TeamVoteResult extends Packet { public boolean isApproved; public int[] playerApprovedPos; public int[] playerRejectedPos; public GameLogicFunctions.Quest quest; public int voteNumber;}
    public static class Packet_QuestVote extends Packet { public int[] teamMemberPos; public GameLogicFunctions.Quest quest;}
    public static class Packet_QuestVoteResult extends Packet { public int[] teamMemberPos; public boolean[] votes; public GameLogicFunctions.Quest quest;}
    public static class Packet_SelectTeam extends Packet { public int teamSize; public GameLogicFunctions.Quest quest;}
    public static class Packet_GameFinished extends Packet { public boolean gameResult; }

    public static class Packet_TeamVoteReply extends Packet { public boolean vote; public int playerID;}
    public static class Packet_QuestVoteReply extends Packet { public boolean vote; public int playerID;}
    public static class Packet_SelectTeamReply extends Packet { public int[] teamPos; public int playerID;}
    public static class Packet_AssassinateReply extends Packet { public boolean isSuccess; public int playerID;}
    public static class Packet_LadyOfLakeReply extends Packet { public int selectedPlayerIndex; public int playerID;}
    public static class Packet_GameFinishedReply extends Packet { public boolean playAgain; public int playerID;}
    public static class Packet_QuestVoteResultRevealed extends Packet { public int voteNumber; public boolean voteResult; public int playerID;}
    public static class Packet_QuestVoteResultFinished extends Packet { public boolean result; public int playerID;}

    public static class Packet_StartNextQuest extends Packet { public boolean previousQuestResult; }
    public static class Packet_StartGame extends Packet { public ArrayList<Player> allPlayers; public ArrayList<Integer> leaderOrderList; public GameLogicFunctions.Board currentBoard;}
    public static class Packet_LadyOfLakeUpdate extends Packet { public int newTokenHolderID; public int previousTokenHolderID;}

    public static class Packet_PlayerHasLeftApp extends Packet { public String playerName;}
    public static class Packet_PlayerHasReturnedToApp extends Packet { public String playerName;}

    public static class Packet_UpdateGameState extends Packet {public Session.GameState nextGameState;}

    public static class Packet00_ClientDetails  extends Packet { public String playerName = "nobody";}
    public static class Packet2_Message extends Packet {public String message;}

    
}
