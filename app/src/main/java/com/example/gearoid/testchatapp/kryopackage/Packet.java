package com.example.gearoid.testchatapp.kryopackage;

import java.util.ArrayList;
import java.util.LinkedList;

import com.example.gearoid.testchatapp.game.GameLogicFunctions;
import com.example.gearoid.testchatapp.character.ICharacter;
import com.example.gearoid.testchatapp.multiplayer.Player;
import com.example.gearoid.testchatapp.multiplayer.Session;
import com.example.gearoid.testchatapp.singletons.PlayerConnection;

//package com.example.gearoid.testchatapp.kyro;


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



    public static class Packet00_ClientDetails  extends Packet { public String playerName = "nobody";} //need to be public to be accessed outside the package
    public static class Packet0_Phase_Leader  extends Packet { }
    public static class Packet1_LoginResult extends Packet { public boolean accepted = false; }
    public static class Packet2_Message extends Packet {public String message;}
        
    public static class Packet3_AllPlayers extends Packet {    	
    	int playerNumber ;    	
    	LinkedList<PlayerConnection> allPlayerConnections;
    }    
    public static class Packet30_AllPlayers extends Packet {    	
    	int playerID = 0 ;    	
    	LinkedList<ICharacter> allPlayers ;     	
    } 
    public static class Packet4_QuestSucessVote extends Packet {
    	boolean isSuccessVote;
    	ICharacter playerWhoVoted;    	
    }
    public static class Packet5_TeamSelectVote extends Packet {//May be joined with Packet4_QuestSuccessVote????
    	boolean isSuccessVote;
    	ICharacter playerWhoVoted; //is this neccessary???  Use ID instead??  	
    }
    public static class Packet6_ProposedTeam extends Packet {
    	LinkedList<ICharacter> proposedPlayers;    	
    }
    public static class packet7_UpdateVoteCounter extends Packet {
    	int voteCounterValue;    	
    }
    public static class packet8_UpdateQuestCounter extends Packet {
    	int questCounterValue;    	
    }
    public static class packet9_IsUsingLadyOfLake extends Packet {
    	boolean isActivatingLadyLake;
    	PlayerConnection selectedPlayerConnection; //This to let the Server know who the token is being used on.
    	//The server will then tell the other devices about it and pass the token onto the selected player.
    }
    public static class packet10_LadyOfLakeToken extends Packet {//Server passes this to the (next)player who is to receive the token.
    	boolean isLadyOfLake = true;
    }
    
}
