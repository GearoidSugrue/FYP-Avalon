package com.example.gearoid.testchatapp.kryopackage;

import com.example.gearoid.testchatapp.kryopackage.Packet.*;

public class PacketFactory {

	public static Packet createPacket(String packetName){
		
		if (packetName.equalsIgnoreCase(ConstantsKryo.ACTIVATE_LADY_OF_LAKE)) {

		} else if (packetName.equalsIgnoreCase(ConstantsKryo.ALL_PLAYERS)) {
				return new Packet30_AllPlayers();
		} else if (packetName.equalsIgnoreCase(ConstantsKryo.CLIENT_DETAILS)) {
                return new Packet00_ClientDetails();
		} else if (packetName.equalsIgnoreCase(ConstantsKryo.LADY_OF_LAKE_TOKEN)) {

		} else if (packetName.equalsIgnoreCase(ConstantsKryo.MESSAGE)) {
            return new Packet2_Message();
		} else if (packetName.equalsIgnoreCase(ConstantsKryo.PHASE_LEADER)) {

		} else if (packetName.equalsIgnoreCase(ConstantsKryo.PROPOSED_TEAM)) {

		} else if (packetName.equalsIgnoreCase(ConstantsKryo.QUEST_VOTE)) {

		} else if (packetName.equalsIgnoreCase(ConstantsKryo.TEAM_VOTE)) {

		} else if (packetName.equalsIgnoreCase(ConstantsKryo.UPDATE_QUEST_COUNTER)) {

		} else if (packetName.equalsIgnoreCase(ConstantsKryo.UPDATE_VOTE_COUNTER)) {

		} 
		
		return null;		
	}
	
}
