package com.example.gearoid.testchatapp.kryopackage;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.example.gearoid.testchatapp.kryopackage.Packet.*;
import com.example.gearoid.testchatapp.character.*;
import com.example.gearoid.testchatapp.multiplayer.Player;
import com.example.gearoid.testchatapp.singletons.PlayerConnection;

/**
 * Created by gearoid on 17/01/15.
 */
public class KRegisterAndPort {
    // This class is a convenient place to keep things common to both the client and server.

    static public final int TCP_PORT = 54555;
    static public final int UDP_PORT = 54577;

    
    private KRegisterAndPort(){
    	
    }

    // This registers objects that are going to be sent over the network.
    static public void register(EndPoint endPoint) {//(Kryo kryo)...create EndPoint Class???
        Kryo kryo = endPoint.getKryo();
        
        kryo.register(Packet.class);
        kryo.register(Packet_RequestDetails.class);
        kryo.register(Packet_SendDetails.class);

        kryo.register(Packet_StartGame.class);
        kryo.register(Packet_IsLadyOfLake.class);
        kryo.register(Packet_TeamVote.class);
        kryo.register(Packet_TeamVoteResult.class);
        kryo.register(Packet_QuestVote.class);
        kryo.register(Packet_QuestVoteResult.class);
        kryo.register(Packet_SelectTeam.class);
        kryo.register(Packet_GameFinished.class);


        kryo.register(Packet_TeamVoteReply.class);
        kryo.register(Packet_QuestVoteReply.class);
        kryo.register(Packet_SelectTeamReply.class);
        kryo.register(Packet_AssassinateReply.class);
        kryo.register(Packet_LadyOfLakeReply.class);
        kryo.register(Packet_GameFinishedReply.class);
        kryo.register(Packet_QuestVoteResultRevealed.class);
        kryo.register(Packet_QuestVoteResultFinished.class);






        kryo.register(packet10_LadyOfLakeToken.class);


        kryo.register(Packet00_ClientDetails.class);
        kryo.register(Packet0_Phase_Leader.class);
        kryo.register(Packet1_LoginResult.class);
        kryo.register(Packet2_Message.class);
        kryo.register(Packet3_AllPlayers.class);
        kryo.register(Packet30_AllPlayers.class);
        kryo.register(Packet4_QuestSucessVote.class);
        kryo.register(Packet5_TeamSelectVote.class);
        kryo.register(Packet6_ProposedTeam.class);
        kryo.register(packet7_UpdateVoteCounter.class);
        kryo.register(packet8_UpdateQuestCounter.class);
        kryo.register(packet9_IsUsingLadyOfLake.class);
        kryo.register(packet10_LadyOfLakeToken.class);
        
        kryo.register(ICharacter.class);
        kryo.register(GoodCharacter.class);
        kryo.register(EvilCharacter.class);
        kryo.register(Merlin.class);
        kryo.register(Morgana.class);
        kryo.register(Mordred.class);
        kryo.register(Oberon.class);
        kryo.register(Percival.class);
        kryo.register(Assassin.class);

        //kryo.register(Board.class); //enum board???
        kryo.register(PlayerConnection.class);
        kryo.register(Player.class);
        kryo.register(com.example.gearoid.testchatapp.multiplayer.Session.GameState.class);

        kryo.register(com.example.gearoid.testchatapp.GameLogicFunctions.Board.class);
        kryo.register(com.example.gearoid.testchatapp.GameLogicFunctions.Quest.class);
        //kryo.register(com.esotericsoftware.kryonet.Client.class);
        kryo.register(Connection.class);
        kryo.register(com.esotericsoftware.kryonet.Server.class);
        kryo.register(com.esotericsoftware.kryonet.Connection[].class);

        kryo.register(java.util.LinkedList.class);
        kryo.register(java.util.ArrayList.class);
        
        

    }
}