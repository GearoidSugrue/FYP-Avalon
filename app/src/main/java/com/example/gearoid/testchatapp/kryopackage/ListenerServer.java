package com.example.gearoid.testchatapp.kryopackage;

import android.util.Log;

import java.util.LinkedList;

import com.example.gearoid.testchatapp.ApplicationContext;
import com.example.gearoid.testchatapp.kryopackage.Packet.*;
import com.example.gearoid.testchatapp.character.ICharacter;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.example.gearoid.testchatapp.multiplayer.Player;
import com.example.gearoid.testchatapp.singletons.PlayerConnection;
import com.example.gearoid.testchatapp.multiplayer.Session;


/**
 * Created by gearoid on 15/01/15.
 */
public class ListenerServer extends Listener {

    //private Client client;
    //ListenerServer serverListener;
    IActivityServerListener activity;

    public interface IActivityServerListener {
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

    public static ListenerServer initialize() {//not used...delete???
        //this.client = cl;

        return  new ListenerServer();
    }

    public void attachActivityToServerListener(IActivityServerListener activity){
        this.activity = activity;
    }

    public void connected(Connection arg0) {
        System.out.println("[Server] Someone has connected: " + arg0.getID() + "...Requesting Details");
        //Log.d("Server Listener", "Someone has connected");

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
                if (Session.masterAllPlayerConnections.size() > playerConnection.playerID) {
                    Session.masterAllPlayerConnections.get(playerConnection.playerID).playerConnection = con;//Should this be non static way!???
                } else {
                    Log.d("Packet Received", "Error: All players list is missing entries! " + playerConnection.userName + ", ID: " + playerConnection.playerID);
                }

            } else {
                Log.d("Packet Received", "New player connected: " + playerConnection.userName);
                Packet_SendDetails sendPacket = (Packet_SendDetails) PacketFactory.createPack(PacketFactory.PacketType.SEND_DETAILS);

                int idNum = Session.masterAllPlayerConnections.size();//TODO add thread safe way to get IDs

                sendPacket.newPlayerNumber = idNum;
                playerConnection.playerConnection = con;
                Session.masterAllPlayerConnections.add(playerConnection);

                Player player = new Player();
                player.userName = playerConnection.userName;
                player.playerID = idNum;
//                Session.allPlayers.add(player);
                Session.masterAllPlayers.add(player);

                con.sendTCP(sendPacket);
                //Add to specific position using the thread safe ID generated earlier
            }

        }

        if(obj instanceof Packet_TeamVoteReply){
            Packet_TeamVoteReply packet = (Packet_TeamVoteReply) obj;

            activity.server_OnTeamVoteReplyReceived(packet);
        }
        if(obj instanceof Packet_QuestVoteReply){
            Packet_QuestVoteReply packet = (Packet_QuestVoteReply) obj;

            activity.server_OnQuestVoteReplyReceived(packet);
        }
        if(obj instanceof Packet_SelectTeamReply){
            Packet_SelectTeamReply packet = (Packet_SelectTeamReply) obj;

            activity.server_OnSelectTeamReplyReceived(packet);
        }
        if(obj instanceof Packet_AssassinateReply){
            Packet_AssassinateReply packet = (Packet_AssassinateReply) obj;

            activity.server_OnAssassinateReplyReceived(packet);
        }
        if(obj instanceof Packet_LadyOfLakeReply){
            Packet_LadyOfLakeReply packet = (Packet_LadyOfLakeReply) obj;

            activity.server_OnLadyOfLakeReplyReceived(packet);
        }
        if(obj instanceof Packet_GameFinishedReply){
            Packet_GameFinishedReply packet = (Packet_GameFinishedReply) obj;

            activity.server_OnGameFinishedReplyReceived(packet);
        }
        if(obj instanceof Packet_QuestVoteResultRevealed){
            Packet_QuestVoteResultRevealed packet = (Packet_QuestVoteResultRevealed) obj;

            activity.server_OnQuestVoteResultRevealedReceived(packet);
        }
        if(obj instanceof Packet_QuestVoteResultFinished){
            Packet_QuestVoteResultFinished packet = (Packet_QuestVoteResultFinished) obj;

            activity.server_OnQuestVoteResultFinishedReceived(packet);
        }




        if (obj instanceof Packet00_ClientDetails) {
            String userName = ((Packet00_ClientDetails) obj).playerName;
            con.setName(con.getID() + "_" + userName);    //Change ID to custom ID.
            System.out.println("[Server] Received details from " + con.getID() + ", " + con.toString());
            ApplicationContext.showToast("[S] Received Packet from: " + userName);

            if(activity !=null){
                activity.server_OnMessagePacketReceived(userName);
                //activity.startTeamVoteDialog();
            }

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

            if(activity !=null){
                activity.server_OnMessagePacketReceived(message);
            }
            // do something with message...
        }
        if (obj instanceof Packet3_AllPlayers) {
            int senderNumber = ((Packet3_AllPlayers) obj).playerNumber;
            LinkedList<PlayerConnection> allPlayerConnections = ((Packet3_AllPlayers) obj).allPlayerConnections;

            System.out.println("[Server] All Player Packet Recieved from player number: " + senderNumber);
            System.out.println("[Server] All of their details are as follows:");

            for (int i = 0; i < allPlayerConnections.size(); i++) {
                PlayerConnection aPlayerConnection = allPlayerConnections.get(i);
                System.out.print(aPlayerConnection.userName + " is ");

//                if (aPlayerConnection.hasLadyOfLake) {
//                    System.out.print(" and has the Lady of The Lake token!");
//                }
//                if (aPlayerConnection.isLeader) {
//                    System.out.print(" Is the Leader!!!");
//                }
//                System.out.println();
            }
            //String message = ((Packet3_AllPlayers) o).message;
            // do something with allCharacters...
        }
        if (obj instanceof Packet4_QuestSucessVote) {
            System.out.println("[Server] Quest Success Vote Recieved.");
            ICharacter player = ((Packet4_QuestSucessVote) obj).playerWhoVoted;
            boolean vote = ((Packet4_QuestSucessVote) obj).isSuccessVote;

            System.out.println(player.getCharacterName() + " votes: " + vote);

            Packet2_Message confirmationMassage = new Packet2_Message();
            confirmationMassage.message = "Thanks " + con.getID() + ", also known as " + con.toString() + ", Vote Received";
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


    public void packetReceivedReply(Connection c) {
        Packet2_Message reply = (Packet2_Message) PacketFactory.createPacket(ConstantsKryo.MESSAGE);
        reply.message = "Your Packet arrived successfully!";
        c.sendTCP(reply);
    }
}
