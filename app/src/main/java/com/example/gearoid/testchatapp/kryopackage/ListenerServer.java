package com.example.gearoid.testchatapp.kryopackage;

import java.util.LinkedList;

import com.example.gearoid.testchatapp.ApplicationContext;
import com.example.gearoid.testchatapp.kryopackage.Packet.*;
import com.example.gearoid.testchatapp.character.ICharacter;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;


/**
 * Created by gearoid on 15/01/15.
 */
public class ListenerServer extends Listener {

    private Client client;

    public void init(Client cl) {//not used...delete???
        this.client = cl;
        //client.setName("SevyHost");// delete
    }

    public void connected(Connection arg0){
		System.out.println("[Server] Someone has connected: " + arg0.getID());
		
		//Create an ID and send back to the client. Client will store ID and use if necessary to reconnect.
		
    }

    public void disconnected(Connection arg0){
		System.out.println("[Server] Someone has disconnected: " + arg0.getID() + ", " + arg0.toString());

    }

	public void received(Connection c, Object o) {		
		if (o instanceof Packet00_ClientDetails) {
			String userName = ((Packet00_ClientDetails) o).playerName;
			c.setName(c.getID() + "_" + userName);	//Change ID to custom ID.
			System.out.println("[Server] Received details from " + c.getID() + ", " + c.toString());
            ApplicationContext.showToast("[S] Received Packet from: " + userName);

            packetReceivedReply(c);
		}		
		if (o instanceof Packet0_Phase_Leader) { //Not sure if Server should or will receive this.		
			
//			Packet1_LoginResult loginResult = new Packet1_LoginResult();
//			loginResult.accepted = true;
//			c.sendTCP(loginResult);
		}
		if (o instanceof Packet2_Message) {
			String message = ((Packet2_Message) o).message;
			System.out.println(c.getID() + " also known as: " + c.toString() + " says: " + message);
			// do something with message...
		}
		if (o instanceof Packet3_AllPlayers) {
			int senderNumber = ((Packet3_AllPlayers) o).playerNumber;
			LinkedList<Player> allPlayers = ((Packet3_AllPlayers) o).allPlayers;
			
			System.out.println("[Server] All Player Packet Recieved from player number: " + senderNumber);
			System.out.println("[Server] All of their details are as follows:");
			
			for(int i=0; i < allPlayers.size(); i++){
				Player aPlayer = allPlayers.get(i) ;
				System.out.print(aPlayer.userName + " is " );
				
				if(aPlayer.hasLadyOfLake){
					System.out.print(" and has the Lady of The Lake token!");
				}
				if(aPlayer.isLeader){
					System.out.print(" Is the Leader!!!");
				}
				System.out.println();
			}
			//String message = ((Packet3_AllPlayers) o).message;
			// do something with allCharacters...
		}
		if (o instanceof Packet4_QuestSucessVote) {
			System.out.println("[Server] Quest Success Vote Recieved.");	
			ICharacter player = ((Packet4_QuestSucessVote) o).playerWhoVoted;
			boolean vote = ((Packet4_QuestSucessVote) o).isSuccessVote;
			
			System.out.println(player.getCharacterName() + " votes: "  + vote);
			
			Packet2_Message confirmationMassage = new Packet2_Message();
			confirmationMassage.message = "Thanks " + c.getID() + ", also known as " + c.toString() +", Vote Received";
			c.sendTCP(confirmationMassage);
			
			
		}
		if (o instanceof Packet5_TeamSelectVote) {
			System.out.println("[Server] Team Select Vote Recieved.");	

			System.out.println();		
			
		}
		if (o instanceof Packet6_ProposedTeam) {
			System.out.println("[Server] Proposed Team Recieved.");	
			
			System.out.println();	
			
		}
		if (o instanceof packet7_UpdateVoteCounter) {
			System.out.println("[Server] Update Vote Counter Recieved.");	

			System.out.println();	
			
		}
		if (o instanceof packet8_UpdateQuestCounter) {
			System.out.println("[Server] Update Vote Counter Recieved.");	
			
			System.out.println();
			
		}
		if (o instanceof packet9_IsUsingLadyOfLake) {
			System.out.println("[Server] Someone is using Lady of The Lake Recieved.");	

			
			System.out.println();	
			
		}
		if (o instanceof packet10_LadyOfLakeToken) {
			System.out.println("[Server] Lady of The Lake Recieved.");	

			
			System.out.println();				
		}
	}

    public void packetReceivedReply(Connection c){
        Packet2_Message reply = (Packet2_Message) PacketFactory.createPacket(ConstantsKryo.MESSAGE);
        reply.message = "Your Packet arrived successfully!";
        c.sendTCP(reply);
    }
}
