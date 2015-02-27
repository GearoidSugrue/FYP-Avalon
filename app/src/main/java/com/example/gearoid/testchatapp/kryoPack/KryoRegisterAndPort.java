package com.example.gearoid.testchatapp.kryoPack;


//import kyroPack.Packet.*;
//import character.Assassin;
//import character.EvilCharacter;
//import character.GoodCharacter;
//import character.ICharacter;
//import character.Merlin;
//import character.Mordred;
//import character.Morgana;
//import character.Oberon;
//import character.Percival;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.example.gearoid.testchatapp.kryoPack.Packet.*;
import com.example.gearoid.testchatapp.character.*;

/**
 * Created by gearoid on 17/01/15.
 */
public class KryoRegisterAndPort {

    static public final int TCP_PORT = 54555;
    static public final int UDP_PORT = 54555;

    
    private KryoRegisterAndPort(){
    	
    }

    // This registers objects that are going to be sent over the network.
    static public void register(EndPoint endPoint) {//(Kryo kryo)...create EndPoint Class???
        Kryo kryo = endPoint.getKryo();
        
        kryo.register(Packet.class);
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

        kryo.register(java.util.LinkedList.class);
        
        
        kryo.register(Player.class);//remove later...

    }

}
