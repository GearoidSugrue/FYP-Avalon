package com.example.gearoid.testchatapp.kryopackage.client;

import android.content.Intent;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import com.example.gearoid.testchatapp.utils.ApplicationContext;
import com.example.gearoid.testchatapp.game.GameActivity;
import com.example.gearoid.testchatapp.game.gamesetup.GameSetupActivity;
import com.example.gearoid.testchatapp.kryopackage.ConstantsKryo;
import com.example.gearoid.testchatapp.kryopackage.Packet.*;

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
import com.example.gearoid.testchatapp.kryopackage.PacketFactory;
import com.example.gearoid.testchatapp.singletons.PlayerConnection;
import com.example.gearoid.testchatapp.multiplayer.Session;

/**
 * Created by gearoid on 17/01/15.
 */
public class ListenerClient extends Listener {

    private Client client;
    IActivityClientListener activity;
    final AtomicInteger reconnectCount = new AtomicInteger();//Necessary???

    public interface IActivityClientListener {
        void client_OnMessagePacketReceived(String message);

        void client_OnUpdateGameStateReceived(Packet_UpdateGameState nextGameStateInfo);

        void client_OnTeamVoteReceived(Packet_TeamVote voteInfo);

        void client_OnTeamVoteResultReceived(Packet_TeamVoteResult voteResultInfo);

        void client_OnQuestVoteReceived(Packet_QuestVote questInfo);

        void client_OnQuestVoteResultReceived(Packet_QuestVoteResult questResultInfo);

        void client_OnQuestVoteResultRevealed(Packet_QuestVoteResultRevealed voteInfo);

        void client_OnLadyOfLakeUpdateReceived(Packet_LadyOfLakeUpdate updateInfo);

        void client_OnSelectTeamReceived(Packet_SelectTeam questInfo);

        void client_OnStartNextQuestReceived(Packet_StartNextQuest previousQuestResult);

        void client_OnGameFinishedReceived(Packet_GameFinished gameResultInfo);


    }

    public void init(Client cl) {
        this.client = cl;
    }

    public void attachActivityToClientListener(IActivityClientListener activity) {
        this.activity = activity;
    }

    public void connected(Connection arg0) {
        System.out.println("[Client " + client.getID() + ", "
                + client.toString() + "]: Connection to Server established");
        Session.myConnection = arg0;

        reconnectCount.set(0);
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
            packet.playerConnection = PlayerConnection.getInstance();

            con.sendTCP(packet);
        }
        if (obj instanceof Packet_SendDetails) {

            PlayerConnection.getInstance().playerID = ((Packet_SendDetails) obj).newPlayerNumber;
        }

        if (obj instanceof Packet_StartGame) {

            Packet_StartGame packet = (Packet_StartGame) obj;

            Session.allPlayers.addAll(packet.allPlayers);
            Session.leaderOrderList.addAll(packet.leaderOrderList);
            Session.currentBoard = packet.currentBoard;

            Intent i = new Intent(ApplicationContext.getContext(), GameActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ApplicationContext.getContext().startActivity(i);
        }
        if (obj instanceof Packet_UpdateGameState) {

            Packet_UpdateGameState packet = (Packet_UpdateGameState) obj;
            activity.client_OnUpdateGameStateReceived(packet);
        }
        if (obj instanceof Packet_TeamVote) {

            Packet_TeamVote packet = (Packet_TeamVote) obj;
            activity.client_OnTeamVoteReceived(packet);
        }
        if (obj instanceof Packet_TeamVoteResult) {

            Packet_TeamVoteResult packet = (Packet_TeamVoteResult) obj;
            activity.client_OnTeamVoteResultReceived(packet);
        }
        if (obj instanceof Packet_QuestVote) {

            Packet_QuestVote packet = (Packet_QuestVote) obj;
            activity.client_OnQuestVoteReceived(packet);
        }
        if (obj instanceof Packet_SelectTeam) {

            Packet_SelectTeam packet = (Packet_SelectTeam) obj;
            activity.client_OnSelectTeamReceived(packet);
        }
        if (obj instanceof Packet_QuestVoteResult) {

            Packet_QuestVoteResult packet = (Packet_QuestVoteResult) obj;
            activity.client_OnQuestVoteResultReceived(packet);
        }
        if (obj instanceof Packet_GameFinished) {

            Packet_GameFinished packet = (Packet_GameFinished) obj;
            activity.client_OnGameFinishedReceived(packet);
        }
        if (obj instanceof Packet_QuestVoteResultRevealed) {

            Packet_QuestVoteResultRevealed packet = (Packet_QuestVoteResultRevealed) obj;
            activity.client_OnQuestVoteResultRevealed(packet);
        }
        if (obj instanceof Packet_StartNextQuest) {

            Packet_StartNextQuest packet = (Packet_StartNextQuest) obj;
            activity.client_OnStartNextQuestReceived(packet);
        }
        if (obj instanceof Packet_LadyOfLakeUpdate) {

            Packet_LadyOfLakeUpdate packet = (Packet_LadyOfLakeUpdate) obj;
            activity.client_OnLadyOfLakeUpdateReceived(packet);
        }
        if (obj instanceof Packet_PlayerHasLeftApp) {

            Packet_PlayerHasLeftApp packet = (Packet_PlayerHasLeftApp) obj;
            ApplicationContext.showToast(packet.playerName + " has left the app");
        }
        if (obj instanceof Packet_PlayerHasReturnedToApp) {

            Packet_PlayerHasReturnedToApp packet = (Packet_PlayerHasReturnedToApp) obj;
            ApplicationContext.showToast(packet.playerName + " has returned to the app");
        }


        if (obj instanceof Packet00_ClientDetails) {
            String userName = ((Packet00_ClientDetails) obj).playerName;
            con.setName(con.getID() + "_" + userName);    //Change ID to custom ID.
            System.out.println("[Client] Received details from " + con.getID() + ", " + con.toString());
            ApplicationContext.showToast("[Client] Received Packet from: " + userName);

            Packet2_Message reply = (Packet2_Message) PacketFactory.testing_createPacket(ConstantsKryo.MESSAGE);
            reply.message = "Your Packet arrived successfully!";
            con.sendTCP(reply);

        }

        if (obj instanceof Packet2_Message) {
            String message = ((Packet2_Message) obj).message;
            System.out.println("[Client " + client.getID() + ", "
                    + client.toString() + "] Received message from "
                    + con.getID() + " " + con.toString() + " connection: "
                    + message);

            if (message.equalsIgnoreCase("Start")) {//TODO fix this..
                Intent i = new Intent(ApplicationContext.getContext(), GameSetupActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ApplicationContext.getContext().startActivity(i);
            }

            ApplicationContext.showToast(message);
        }
    }
}
