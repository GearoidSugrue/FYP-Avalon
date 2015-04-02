package com.example.gearoid.testchatapp.kryopackage;

import android.util.Log;

import com.example.gearoid.testchatapp.kryopackage.Packet.*;

public class PacketFactory {

    public enum PacketType {
        REQUEST_DETAILS, SEND_DETAILS, START_GAME, IS_LADYOFLAKE, UPDATE_GAMESTATE, TEAM_VOTE,
        TEAM_VOTE_RESULT, QUEST_VOTE,  QUEST_VOTE_RESULT, SELECT_TEAM, GAME_FINISHED,
        TEAM_VOTE_REPLY, QUEST_VOTE_REPLY, SELECT_TEAM_REPLY, ASSASSINATE_REPLY, LADYOFLAKE_REPLY, GAME_FINISHED_REPLY,
        QUESTVOTE_REVEALED, QUESTVOTE_FINISHED
    }

    public static Packet createPack(PacketType nameEnum){

        switch(nameEnum) {
            case REQUEST_DETAILS: return new Packet_RequestDetails();
            case SEND_DETAILS: return new Packet_SendDetails();
            case START_GAME: return new Packet_StartGame();
            case IS_LADYOFLAKE: return new Packet_IsLadyOfLake();
            case UPDATE_GAMESTATE: return new Packet_UpdateGameState();
            case TEAM_VOTE: return new Packet_TeamVote();
            case TEAM_VOTE_RESULT: return new Packet_TeamVoteResult();
            case QUEST_VOTE: return new Packet_QuestVote();
            case QUEST_VOTE_RESULT: return new Packet_QuestVoteResult();
            case SELECT_TEAM: return new Packet_SelectTeam();
            case GAME_FINISHED: return new Packet_GameFinished();

            case TEAM_VOTE_REPLY: return new Packet_TeamVoteReply();
            case QUEST_VOTE_REPLY: return new Packet_QuestVoteReply();
            case SELECT_TEAM_REPLY: return new Packet_SelectTeamReply();
            case ASSASSINATE_REPLY: return new Packet_AssassinateReply();
            case LADYOFLAKE_REPLY: return new Packet_LadyOfLakeReply();
            case GAME_FINISHED_REPLY: return new Packet_GameFinishedReply();
            case QUESTVOTE_REVEALED: return new Packet_QuestVoteResultRevealed();
            case QUESTVOTE_FINISHED: return new Packet_QuestVoteResultFinished();




            default:
                Log.e("PacketFactory", "Packet name not found!!! Returning NULL!"); return null;
        }

    }

	public static Packet createPacket(String packetName){ //TODO remove later

        switch (packetName) {
            case "Packet_RequestDetails": return new Packet_RequestDetails();
            case "Packet_SendDetails": return new Packet_SendDetails();


            default:

                //throw new IllegalArgumentException("Invalid character name: " + characterName );
        }

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
