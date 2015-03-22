package com.example.gearoid.testchatapp.kryopackage;

import java.util.LinkedList;

import com.example.gearoid.testchatapp.character.ICharacter;
import com.example.gearoid.testchatapp.singletons.Player;

//package com.example.gearoid.testchatapp.kyro;


/**
 * Created by gearoid on 15/01/15.
 */
public class Packet {

    public static class Packet_RequestDetails extends Packet { public Player player; }
    public static class Packet_SendDetails extends Packet { public int newPlayerNumber = -1;}

    public static class Packet00_ClientDetails  extends Packet { public String playerName = "nobody";} //need to be public to be accessed outside the package
    public static class Packet0_Phase_Leader  extends Packet { }
    public static class Packet1_LoginResult extends Packet { public boolean accepted = false; }
    public static class Packet2_Message extends Packet {public String message;}
        
    public static class Packet3_AllPlayers extends Packet {    	
    	int playerNumber ;    	
    	LinkedList<Player> allPlayers ;
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
    	Player selectedPlayer; //This to let the Server know who the token is being used on. 
    	//The server will then tell the other devices about it and pass the token onto the selected player.
    }
    public static class packet10_LadyOfLakeToken extends Packet {//Server passes this to the (next)player who is to receive the token.
    	boolean isLadyOfLake = true;
    }
    
}
