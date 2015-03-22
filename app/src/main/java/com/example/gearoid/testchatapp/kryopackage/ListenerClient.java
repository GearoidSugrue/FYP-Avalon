package com.example.gearoid.testchatapp.kryopackage;

import android.content.Intent;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import com.example.gearoid.testchatapp.ApplicationContext;
import com.example.gearoid.testchatapp.GameSetupActivity;
import com.example.gearoid.testchatapp.kryopackage.Packet.*;
import com.example.gearoid.testchatapp.character.ICharacter;

//import kyroPack.Packet.Packet0_Phase_Leader;
//import kyroPack.Packet.Packet1_LoginResult;
//import kyroPack.Packet.Packet3_AllPlayers;
//import kyroPack.Packet.Packet4_QuestSucessVote;
//import kyroPack.Packet.Packet5_TeamSelectVote;
//import kyroPack.Packet.Packet6_ProposedTeam;
//import kyroPack.Packet.packet10_LadyOfLakeToken;
//import kyroPack.Packet.packet7_UpdateVoteCounter;
//import kyroPack.Packet.packet8_UpdateQuestCounter;
//import kyroPack.Packet.packet9_IsUsingLadyOfLake;
//import character.ICharacter;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.example.gearoid.testchatapp.singletons.Player;
import com.example.gearoid.testchatapp.multiplayer.Session;

/**
 * Created by gearoid on 17/01/15.
 */
public class ListenerClient extends Listener {

	private Client client;
	final AtomicInteger reconnectCount = new AtomicInteger();//Necessary???

	public void init(Client cl) {
		this.client = cl;
	}

	public void connected(Connection arg0) {
		System.out.println("[Client " + client.getID() + ", "
				+ client.toString() + "]: Connection to Server established");
        Session.myConnection = arg0;

		reconnectCount.set(0);
		// System.out.println(arg0.getID() + ", " + arg0.toString());

		// client.sendTCP(new Packet0_LoginRequest());
	}

	public void disconnected(Connection arg0) {
		System.out.println("[Client " + client.getID() + ", "
				+ client.toString() + "]: Disconnected from Server!");

		// if (reconnectCount.getAndIncrement() < 10) {
		new Thread() {
			public void run() {
				if (reconnectCount.getAndIncrement() < 5) {//Move out side thread??? Not working at the moment.

					try {
						System.out.println("[Client " + client.getID() + ", "
								+ client.toString() + "]: Reconnecting... "
								+ reconnectCount.get());
						client.reconnect();
					} catch (IOException ex) {
						System.out
								.println("[Client " + client.getID() + ", "
										+ client.toString()
										+ "]: Failed to reconnect!");
						ex.printStackTrace();
					}
				}
			}
		}.start();

	}

	public void received(Connection con, Object obj) {

        if (obj instanceof Packet_RequestDetails) {
            Packet_RequestDetails packet = (Packet_RequestDetails) obj;
            packet.player = Player.getInstance();

            con.sendTCP(packet);
        }
        if (obj instanceof Packet_SendDetails){
            Player.getInstance().playerID = ((Packet_SendDetails) obj).newPlayerNumber;
        }


        if (obj instanceof Packet00_ClientDetails) {
            String userName = ((Packet00_ClientDetails) obj).playerName;
            con.setName(con.getID() + "_" + userName);	//Change ID to custom ID.
            System.out.println("[Client] Received details from " + con.getID() + ", " + con.toString());
            ApplicationContext.showToast("[Client] Received Packet from: " + userName);

            packetReceivedReply(con);
        }

        if (obj instanceof Packet0_Phase_Leader) {
			// Check if leader. If yes, open fragment for leader to select team.
			// If no do nothing(or show results from last round or something).
			System.out.println("[Client " + client.getID() + ", "
					+ client.toString() + "]: LEADER Phase Packet recieved.");
			// Call GameLogicClient method...
		}

		if (obj instanceof Packet1_LoginResult) {
			boolean answer = ((Packet1_LoginResult) obj).accepted;

			if (answer) {
				// Do Something...

			} else {
				con.close();
			}
		}
		if (obj instanceof Packet.Packet2_Message) {
			String message = ((Packet.Packet2_Message) obj).message;
			System.out.println("[Client " + client.getID() + ", "
					+ client.toString() + "] Received message from "
					+ con.getID() + " " + con.toString() + " connection: "
					+ message);

            //ListenerClient.KryoNetClientCallback.messageRecieved(message);
            if(message.equalsIgnoreCase("Start")){//TODO fix this..
                Intent i = new Intent(ApplicationContext.getContext(), GameSetupActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ApplicationContext.getContext().startActivity(i);
            }

            ApplicationContext.showToast(message);

            // do something with message...
		}
		if (obj instanceof Packet3_AllPlayers) {
			int senderNumber = ((Packet3_AllPlayers) obj).playerNumber;
			LinkedList<Player> allPlayers = ((Packet3_AllPlayers) obj).allPlayers;

			//GameLogicClient.allPlayers = allPlayers;

			System.out.println("[[Client " + client.getID() + ", "
					+ client.toString()
					+ "]: All Player Packet Recieved from player number: "
					+ senderNumber);
			System.out.println("[[Client " + client.getID() + ", "
					+ client.toString()
					+ "]: All of their details are as follows:");

			for (int i = 0; i < allPlayers.size(); i++) {
				Player aPlayer = allPlayers.get(i);
				System.out.print(aPlayer.userName + " is ");

				if (aPlayer.hasLadyOfLake) {
					System.out.print(" and has the Lady of The Lake token!");
				}
				if (aPlayer.isLeader) {
					System.out.print(" LEADER!");
				}
				System.out.println();
			}

		}

		if (obj instanceof Packet4_QuestSucessVote) {
			System.out.println("[Client] Quest Success Vote Recieved.");
			ICharacter player = ((Packet4_QuestSucessVote) obj).playerWhoVoted;
			boolean vote = ((Packet4_QuestSucessVote) obj).isSuccessVote;

			System.out.println(player.getPlayerName() + " votes: " + vote);

		}
		if (obj instanceof Packet5_TeamSelectVote) {//Clients shouldn't receive this packet
//			System.out.println("[Client] Team Select Vote Recieved.");
//			ICharacter player = ((Packet5_TeamSelectVote) o).playerWhoVoted;
//			boolean vote = ((Packet5_TeamSelectVote) o).isSuccessVote;
//
//			System.out.println(player.getPlayerName() + " votes: " + vote);

		}
		if (obj instanceof Packet6_ProposedTeam) {
			System.out.println("[Client] Proposed Team Recieved.");
			//Bring up fragment for player to vote

		}
		if (obj instanceof packet7_UpdateVoteCounter) {
			System.out.println("[Client] Update Vote Counter Recieved.");
			//Update the Vote counter
		}
		if (obj instanceof packet8_UpdateQuestCounter) {
			System.out.println("[Client] Update Vote Counter Recieved.");
			//Update the Quest counter

		}
		if (obj instanceof packet9_IsUsingLadyOfLake) {//Client shouldn't need this?? Or have 2 packets(1 for client, 1 for server)
//			System.out.println("[Client] Someone is using Lady of The Lake Recieved.");
//
//			System.out.println();

		}
		if (obj instanceof packet10_LadyOfLakeToken) {
			System.out.println("[Client] Lady of The Lake Recieved.");
			
			
			//Set hasLadyOfLake to true for this player/client
		}

		// String message = ((Packet3_AllPlayers) o).message;
		// do something with allCharacters...
	}

    public void packetReceivedReply(Connection c){
        Packet2_Message reply = (Packet2_Message) PacketFactory.createPacket(ConstantsKryo.MESSAGE);
        reply.message = "Your Packet arrived successfully!";
        c.sendTCP(reply);
    }


}
