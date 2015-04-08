package com.example.gearoid.testchatapp.kryopackage;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.example.gearoid.testchatapp.character.evil.Assassin;
import com.example.gearoid.testchatapp.character.evil.EvilCharacter;
import com.example.gearoid.testchatapp.character.evil.Mordred;
import com.example.gearoid.testchatapp.character.evil.Morgana;
import com.example.gearoid.testchatapp.character.evil.Oberon;
import com.example.gearoid.testchatapp.character.good.GoodCharacter;
import com.example.gearoid.testchatapp.character.good.Merlin;
import com.example.gearoid.testchatapp.character.good.Percival;
import com.example.gearoid.testchatapp.game.GameLogicFunctions;
import com.example.gearoid.testchatapp.kryopackage.Packet.*;
import com.example.gearoid.testchatapp.character.*;
import com.example.gearoid.testchatapp.multiplayer.Player;
import com.example.gearoid.testchatapp.singletons.PlayerConnection;

/**
 * Created by gearoid on 17/01/15.
 */
public class KRegisterAndPort {

    static public final int TCP_PORT = 54555;
    static public final int UDP_PORT = 54577;

    private KRegisterAndPort(){
    	
    }

    // This registers objects that are going to be sent over the network. Has to be then exact same on both client and server.
    static public void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        
        kryo.register(Packet.class);
        kryo.register(Packet_RequestDetails.class);
        kryo.register(Packet_SendDetails.class);

        kryo.register(Packet_StartGame.class);
        kryo.register(Packet_LadyOfLakeUpdate.class);
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
        kryo.register(Packet_StartNextQuest.class);
        kryo.register(Packet_PlayerHasLeftApp.class);
        kryo.register(Packet_PlayerHasReturnedToApp.class);


        kryo.register(Packet00_ClientDetails.class);
        kryo.register(Packet2_Message.class);

        kryo.register(ICharacter.class);
        kryo.register(GoodCharacter.class);
        kryo.register(EvilCharacter.class);
        kryo.register(Merlin.class);
        kryo.register(Morgana.class);
        kryo.register(Mordred.class);
        kryo.register(Oberon.class);
        kryo.register(Percival.class);
        kryo.register(Assassin.class);

        kryo.register(PlayerConnection.class);
        kryo.register(Player.class);
        kryo.register(com.example.gearoid.testchatapp.multiplayer.Session.GameState.class);
        kryo.register(GameLogicFunctions.Board.class);
        kryo.register(GameLogicFunctions.Quest.class);
        kryo.register(com.example.gearoid.testchatapp.kryopackage.Packet.Packet_UpdateGameState.class);
        kryo.register(Connection.class);
        kryo.register(com.esotericsoftware.kryonet.Server.class);
        kryo.register(com.esotericsoftware.kryonet.Connection[].class);

        kryo.register(java.util.LinkedList.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(int[].class);
        kryo.register(boolean[].class);
    }
}