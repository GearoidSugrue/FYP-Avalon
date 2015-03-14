package com.example.gearoid.testchatapp.kryopackage;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

import com.example.gearoid.testchatapp.ApplicationContext;
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

	public void received(Connection c, Object o) {

		if (o instanceof Packet0_Phase_Leader) {
			// Check if leader. If yes, open fragment for leader to select team.
			// If no do nothing(or show results from last round or something).
			System.out.println("[Client " + client.getID() + ", "
					+ client.toString() + "]: LEADER Phase Packet recieved.");
			// Call GameLogicClient method...
		}

		if (o instanceof Packet1_LoginResult) {
			boolean answer = ((Packet1_LoginResult) o).accepted;

			if (answer) {
				// Do Something...

			} else {
				c.close();
			}
		}
		if (o instanceof Packet.Packet2_Message) {
			String message = ((Packet.Packet2_Message) o).message;
			System.out.println("[Client " + client.getID() + ", "
					+ client.toString() + "] Received message from "
					+ c.getID() + " " + c.toString() + " connection: "
					+ message);

            ApplicationContext.showToast(message);

            // do something with message...
		}
		if (o instanceof Packet3_AllPlayers) {
			int senderNumber = ((Packet3_AllPlayers) o).playerNumber;
			LinkedList<Player> allPlayers = ((Packet3_AllPlayers) o).allPlayers;

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

		if (o instanceof Packet4_QuestSucessVote) {
			System.out.println("[Client] Quest Success Vote Recieved.");
			ICharacter player = ((Packet4_QuestSucessVote) o).playerWhoVoted;
			boolean vote = ((Packet4_QuestSucessVote) o).isSuccessVote;

			System.out.println(player.getPlayerName() + " votes: " + vote);

		}
		if (o instanceof Packet5_TeamSelectVote) {//Clients shouldn't receive this packet
//			System.out.println("[Client] Team Select Vote Recieved.");
//			ICharacter player = ((Packet5_TeamSelectVote) o).playerWhoVoted;
//			boolean vote = ((Packet5_TeamSelectVote) o).isSuccessVote;
//
//			System.out.println(player.getPlayerName() + " votes: " + vote);

		}
		if (o instanceof Packet6_ProposedTeam) {
			System.out.println("[Client] Proposed Team Recieved.");
			//Bring up fragment for player to vote

		}
		if (o instanceof packet7_UpdateVoteCounter) {
			System.out.println("[Client] Update Vote Counter Recieved.");
			//Update the Vote counter
		}
		if (o instanceof packet8_UpdateQuestCounter) {
			System.out.println("[Client] Update Vote Counter Recieved.");
			//Update the Quest counter

		}
		if (o instanceof packet9_IsUsingLadyOfLake) {//Client shouldn't need this?? Or have 2 packets(1 for client, 1 for server)
//			System.out.println("[Client] Someone is using Lady of The Lake Recieved.");
//
//			System.out.println();

		}
		if (o instanceof packet10_LadyOfLakeToken) {
			System.out.println("[Client] Lady of The Lake Recieved.");
			
			
			//Set hasLadyOfLake to true for this player/client
		}

		// String message = ((Packet3_AllPlayers) o).message;
		// do something with allCharacters...
	}

}
