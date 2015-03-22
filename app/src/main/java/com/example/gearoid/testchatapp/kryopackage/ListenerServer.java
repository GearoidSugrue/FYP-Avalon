package com.example.gearoid.testchatapp.kryopackage;

import android.util.Log;

import java.util.LinkedList;

import com.example.gearoid.testchatapp.ApplicationContext;
import com.example.gearoid.testchatapp.kryopackage.Packet.*;
import com.example.gearoid.testchatapp.character.ICharacter;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.example.gearoid.testchatapp.singletons.Player;
import com.example.gearoid.testchatapp.multiplayer.Session;


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
        //Log.d("Server Listener", "Someone has connected");


        Packet_RequestDetails packet = (Packet_RequestDetails) PacketFactory.createPack(PacketFactory.PacketType.REQUEST_DETAILS);
        arg0.sendTCP(packet);
    }

    public void disconnected(Connection arg0){
		System.out.println("[Server] Someone has disconnected: " + arg0.getID() + ", " + arg0.toString());

    }

	public void received(Connection con, Object obj) {

        if (obj instanceof Packet_RequestDetails) {
            Player player = ((Packet_RequestDetails) obj).player;

            if(player.playerID >= 0){
                Log.d("Packet Received", "Old player reconnected: " + player.userName + ", ID:" + player.playerID);
                Session.allPlayers.get(player.playerID).playerConnection = con;//Should this be non static way!???

            } else {
                Log.d("Packet Received", "New player connected: " + player.userName);
                Packet_SendDetails sendPacket = (Packet_SendDetails) PacketFactory.createPack(PacketFactory.PacketType.SEND_DETAILS);

                sendPacket.newPlayerNumber = Session.allPlayers.size();//TODO add thread safe way to get IDs
                con.sendTCP(sendPacket);

                player.playerConnection = con;
                Session.allPlayers.add(player);//Add to specific position using the thread safe ID generated earlier
            }

        }

            if (obj instanceof Packet00_ClientDetails) {
			String userName = ((Packet00_ClientDetails) obj).playerName;
			con.setName(con.getID() + "_" + userName);	//Change ID to custom ID.
			System.out.println("[Server] Received details from " + con.getID() + ", " + con.toString());
            ApplicationContext.showToast("[S] Received Packet from: " + userName);

            packetReceivedReply(con);
		}		
		if (obj instanceof Packet0_Phase_Leader) { //Not sure if Server should or will receive this.
			
//			Packet1_LoginResult loginResult = new Packet1_LoginResult();
//			loginResult.accepted = true;
//			c.sendTCP(loginResult);
		}
		if (obj instanceof Packet2_Message) {
			String message = ((Packet2_Message) obj).message;
			System.out.println(con.getID() + " also known as: " + con.toString() + " says: " + message);
			// do something with message...
		}
		if (obj instanceof Packet3_AllPlayers) {
			int senderNumber = ((Packet3_AllPlayers) obj).playerNumber;
			LinkedList<Player> allPlayers = ((Packet3_AllPlayers) obj).allPlayers;
			
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
		if (obj instanceof Packet4_QuestSucessVote) {
			System.out.println("[Server] Quest Success Vote Recieved.");	
			ICharacter player = ((Packet4_QuestSucessVote) obj).playerWhoVoted;
			boolean vote = ((Packet4_QuestSucessVote) obj).isSuccessVote;
			
			System.out.println(player.getCharacterName() + " votes: "  + vote);
			
			Packet2_Message confirmationMassage = new Packet2_Message();
			confirmationMassage.message = "Thanks " + con.getID() + ", also known as " + con.toString() +", Vote Received";
			con.sendTCP(confirmationMassage);
			
			
		}
		if (obj instanceof Packet5_TeamSelectVote) {
			System.out.println("[Server] Team Select Vote Recieved.");	

			System.out.println();		
			
		}
		if (obj instanceof Packet6_ProposedTeam) {
			System.out.println("[Server] Proposed Team Recieved.");	
			
			System.out.println();	
			
		}
		if (obj instanceof packet7_UpdateVoteCounter) {
			System.out.println("[Server] Update Vote Counter Recieved.");	

			System.out.println();	
			
		}
		if (obj instanceof packet8_UpdateQuestCounter) {
			System.out.println("[Server] Update Vote Counter Recieved.");	
			
			System.out.println();
			
		}
		if (obj instanceof packet9_IsUsingLadyOfLake) {
			System.out.println("[Server] Someone is using Lady of The Lake Recieved.");	

			
			System.out.println();	
			
		}
		if (obj instanceof packet10_LadyOfLakeToken) {
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
