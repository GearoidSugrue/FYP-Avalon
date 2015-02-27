package com.example.gearoid.testchatapp.kryoPack;

import com.example.gearoid.testchatapp.kryoPack.Packet.*;

public class PacketFactory {

	public static Packet createPacket(String packetName){
		
		if (packetName.equalsIgnoreCase(KryoConstants.ACTIVATE_LADY_OF_LAKE)) {

		} else if (packetName.equalsIgnoreCase(KryoConstants.ALL_PLAYERS)) {
				return new Packet30_AllPlayers();
		} else if (packetName.equalsIgnoreCase(KryoConstants.CLIENT_DETAILS)) {
                return new Packet00_ClientDetails();
		} else if (packetName.equalsIgnoreCase(KryoConstants.LADY_OF_LAKE_TOKEN)) {

		} else if (packetName.equalsIgnoreCase(KryoConstants.MESSAGE)) {
            return new Packet2_Message();
		} else if (packetName.equalsIgnoreCase(KryoConstants.PHASE_LEADER)) {

		} else if (packetName.equalsIgnoreCase(KryoConstants.PROPOSED_TEAM)) {

		} else if (packetName.equalsIgnoreCase(KryoConstants.QUEST_VOTE)) {

		} else if (packetName.equalsIgnoreCase(KryoConstants.TEAM_VOTE)) {

		} else if (packetName.equalsIgnoreCase(KryoConstants.UPDATE_QUEST_COUNTER)) {

		} else if (packetName.equalsIgnoreCase(KryoConstants.UPDATE_VOTE_COUNTER)) {

		} 
		
		return null;		
	}
	
}
