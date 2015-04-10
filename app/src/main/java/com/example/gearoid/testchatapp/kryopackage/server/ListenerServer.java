package com.example.gearoid.testchatapp.kryopackage.server;

import android.util.Log;

import com.example.gearoid.testchatapp.utils.ApplicationContext;
import com.example.gearoid.testchatapp.kryopackage.Packet.*;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.example.gearoid.testchatapp.kryopackage.PacketFactory;
import com.example.gearoid.testchatapp.multiplayer.Player;
import com.example.gearoid.testchatapp.singletons.PlayerConnection;
import com.example.gearoid.testchatapp.multiplayer.Session;


/**
 * Created by gearoid on 15/01/15.
 */
public class ListenerServer extends Listener {

    IGameActivityServerListener gameActivity;
    IConnectionActivityServerListener connectionActivity;

    public interface IGameActivityServerListener {
        void server_OnMessagePacketReceived(String message);

        void server_OnTeamVoteReplyReceived(Packet_TeamVoteReply voteInfo);
        void server_OnQuestVoteReplyReceived(Packet_QuestVoteReply voteInfo);
        void server_OnSelectTeamReplyReceived(Packet_SelectTeamReply teamInfo);
        void server_OnAssassinateReplyReceived(Packet_AssassinateReply assassinateInfo);
        void server_OnLadyOfLakeReplyReceived(Packet_LadyOfLakeReply ladyOfLakeInfo);
        void server_OnGameFinishedReplyReceived(Packet_GameFinishedReply gameInfo);
        void server_OnQuestVoteResultRevealedReceived(Packet_QuestVoteResultRevealed voteInfo);
        void server_OnQuestVoteResultFinishedReceived(Packet_QuestVoteResultFinished resultInfo);

    }

    public interface IConnectionActivityServerListener {
        void server_OnPlayerConnect(Player newlyConnectedPlayer);

    }

    public static ListenerServer initialize() {

        return  new ListenerServer();
    }

    public void attachGameActivityToServerListener(IGameActivityServerListener activity){
        this.gameActivity = activity;
    }

    public void attachConnectionActivityToServerListener(IConnectionActivityServerListener activity){
        this.connectionActivity = activity;
    }

    public void connected(Connection arg0) {
        System.out.println("[Server] Someone has connected: " + arg0.getID() + "...Requesting Details");
        Log.d("Server Listener", "Someone has connected");

        Packet_RequestDetails packet = (Packet_RequestDetails) PacketFactory.createPack(PacketFactory.PacketType.REQUEST_DETAILS);
        arg0.sendTCP(packet);
    }

    public void disconnected(Connection arg0) {
        System.out.println("[Server] Someone has disconnected: " + arg0.getID() + ", " + arg0.toString());

    }

    public void received(Connection con, Object obj) {

        if (obj instanceof Packet_RequestDetails) {
            PlayerConnection playerConnection = ((Packet_RequestDetails) obj).playerConnection;

            if (playerConnection.playerID >= 0) {
                Log.d("Packet Received", "Old player reconnected: " + playerConnection.userName + ", ID: " + playerConnection.playerID);
                if (Session.serverAllPlayerConnections.size() > playerConnection.playerID) {
                    Session.serverAllPlayerConnections.get(playerConnection.playerID).playerConnection = con;//Should this be non static way!???
                } else {
                    Log.d("Packet Received", "Error: All players list is missing entries! " + playerConnection.userName + ", ID: " + playerConnection.playerID);
                }

            } else {
                Log.d("Packet Received", "New player connected: " + playerConnection.userName);
                Packet_SendDetails sendPacket = (Packet_SendDetails) PacketFactory.createPack(PacketFactory.PacketType.SEND_DETAILS);

                int idNum = Session.serverAllPlayerConnections.size();//TODO add thread safe way to get IDs

                sendPacket.newPlayerNumber = idNum;
                playerConnection.playerConnection = con;
                Session.serverAllPlayerConnections.add(playerConnection);

                Player player = new Player();
                player.userName = playerConnection.userName;
                player.playerID = idNum;
                Session.serverAllPlayers.add(player);

                if (connectionActivity != null) {
                    connectionActivity.server_OnPlayerConnect(player);
                }

                con.sendTCP(sendPacket);
            }

        }

        if (obj instanceof Packet_TeamVoteReply) {

            Packet_TeamVoteReply packet = (Packet_TeamVoteReply) obj;
            gameActivity.server_OnTeamVoteReplyReceived(packet);
        }
        if (obj instanceof Packet_QuestVoteReply) {

            Packet_QuestVoteReply packet = (Packet_QuestVoteReply) obj;
            gameActivity.server_OnQuestVoteReplyReceived(packet);
        }
        if (obj instanceof Packet_SelectTeamReply) {

            Packet_SelectTeamReply packet = (Packet_SelectTeamReply) obj;
            gameActivity.server_OnSelectTeamReplyReceived(packet);
        }
        if (obj instanceof Packet_AssassinateReply) {

            Packet_AssassinateReply packet = (Packet_AssassinateReply) obj;
            gameActivity.server_OnAssassinateReplyReceived(packet);
        }
        if (obj instanceof Packet_LadyOfLakeReply) {

            Packet_LadyOfLakeReply packet = (Packet_LadyOfLakeReply) obj;
            gameActivity.server_OnLadyOfLakeReplyReceived(packet);
        }
        if (obj instanceof Packet_GameFinishedReply) {

            Packet_GameFinishedReply packet = (Packet_GameFinishedReply) obj;
            gameActivity.server_OnGameFinishedReplyReceived(packet);
        }
        if (obj instanceof Packet_QuestVoteResultRevealed) {

            Packet_QuestVoteResultRevealed packet = (Packet_QuestVoteResultRevealed) obj;
            gameActivity.server_OnQuestVoteResultRevealedReceived(packet);
        }
        if (obj instanceof Packet_QuestVoteResultFinished) {

            Packet_QuestVoteResultFinished packet = (Packet_QuestVoteResultFinished) obj;
            gameActivity.server_OnQuestVoteResultFinishedReceived(packet);
        }
        if(obj instanceof Packet_PlayerHasLeftApp) {

            Packet_PlayerHasLeftApp packet = (Packet_PlayerHasLeftApp) obj;
            Session.server_sendToEveryone(packet);
        }
        if(obj instanceof Packet_PlayerHasReturnedToApp) {

            Packet_PlayerHasReturnedToApp packet = (Packet_PlayerHasReturnedToApp) obj;
            Session.server_sendToEveryone(packet);
        }


        if (obj instanceof Packet00_ClientDetails) {

            String userName = ((Packet00_ClientDetails) obj).playerName;
            con.setName(con.getID() + "_" + userName);
            System.out.println("[Server] Received details from " + con.getID() + ", " + con.toString());
            ApplicationContext.showToast("[S] Received Packet from: " + userName);

            if (gameActivity != null) {
                gameActivity.server_OnMessagePacketReceived(userName);
            }

            Packet2_Message reply = (Packet2_Message) PacketFactory.testing_createPacket(PacketFactory.MESSAGE);
            reply.message = "Your Packet arrived successfully!";
            con.sendTCP(reply);
        }

        if (obj instanceof Packet2_Message) {
            String message = ((Packet2_Message) obj).message;
            System.out.println(con.getID() + " also known as: " + con.toString() + " says: " + message);

            if (gameActivity != null) {
                gameActivity.server_OnMessagePacketReceived(message);
            }
        }
    }

}
