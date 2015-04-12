package com.example.gearoid.testchatapp.kryopackage;

import android.util.Log;

import com.example.gearoid.testchatapp.kryopackage.Packet.Packet00_ClientDetails;
import com.example.gearoid.testchatapp.kryopackage.Packet.Packet2_Message;
import com.example.gearoid.testchatapp.kryopackage.Packet.Packet_AssassinateReply;
import com.example.gearoid.testchatapp.kryopackage.Packet.Packet_GameFinished;
import com.example.gearoid.testchatapp.kryopackage.Packet.Packet_GameFinishedReply;
import com.example.gearoid.testchatapp.kryopackage.Packet.Packet_LadyOfLakeReply;
import com.example.gearoid.testchatapp.kryopackage.Packet.Packet_LadyOfLakeUpdate;
import com.example.gearoid.testchatapp.kryopackage.Packet.Packet_PlayerHasLeftApp;
import com.example.gearoid.testchatapp.kryopackage.Packet.Packet_PlayerHasReturnedToApp;
import com.example.gearoid.testchatapp.kryopackage.Packet.Packet_QuestVote;
import com.example.gearoid.testchatapp.kryopackage.Packet.Packet_QuestVoteReply;
import com.example.gearoid.testchatapp.kryopackage.Packet.Packet_QuestVoteResult;
import com.example.gearoid.testchatapp.kryopackage.Packet.Packet_QuestVoteResultFinished;
import com.example.gearoid.testchatapp.kryopackage.Packet.Packet_QuestVoteResultRevealed;
import com.example.gearoid.testchatapp.kryopackage.Packet.Packet_RequestDetails;
import com.example.gearoid.testchatapp.kryopackage.Packet.Packet_SelectTeam;
import com.example.gearoid.testchatapp.kryopackage.Packet.Packet_SelectTeamReply;
import com.example.gearoid.testchatapp.kryopackage.Packet.Packet_SendDetails;
import com.example.gearoid.testchatapp.kryopackage.Packet.Packet_StartGame;
import com.example.gearoid.testchatapp.kryopackage.Packet.Packet_StartNextQuest;
import com.example.gearoid.testchatapp.kryopackage.Packet.Packet_TeamVote;
import com.example.gearoid.testchatapp.kryopackage.Packet.Packet_TeamVoteReply;
import com.example.gearoid.testchatapp.kryopackage.Packet.Packet_TeamVoteResult;
import com.example.gearoid.testchatapp.kryopackage.Packet.Packet_UpdateGameState;

public class PacketFactory {

    //Constants used during testing
    public static final String TAG = "PacketFactory";
    public static final String CLIENT_DETAILS = "Client Details";
    public static final String MESSAGE = "Message";


    public enum PacketType {
        REQUEST_DETAILS, SEND_DETAILS, START_GAME, LADYOFLAKE_UPDATE, UPDATE_GAMESTATE, TEAM_VOTE,
        TEAM_VOTE_RESULT, QUEST_VOTE,  QUEST_VOTE_RESULT, SELECT_TEAM, GAME_FINISHED,
        TEAM_VOTE_REPLY, QUEST_VOTE_REPLY, SELECT_TEAM_REPLY, ASSASSINATE_REPLY, LADYOFLAKE_REPLY, GAME_FINISHED_REPLY,
        QUESTVOTE_REVEALED, QUESTVOTE_FINISHED, START_NEXT_QUEST, PLAYER_LEFT, PLAYER_RETURNED
    }

    public static Packet createPack(PacketType nameEnum){

        switch(nameEnum) {
            case REQUEST_DETAILS: return new Packet_RequestDetails();
            case SEND_DETAILS: return new Packet_SendDetails();
            case START_GAME: return new Packet_StartGame();
            case LADYOFLAKE_UPDATE: return new Packet_LadyOfLakeUpdate();
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
            case START_NEXT_QUEST: return new Packet_StartNextQuest();
            case PLAYER_LEFT: return new Packet_PlayerHasLeftApp();
            case PLAYER_RETURNED: return new Packet_PlayerHasReturnedToApp();

            default:
                Log.e(TAG, "Packet name not found!"); return new Packet2_Message();
        }
    }


	public static Packet testing_createPacket(String packetName){

        if (packetName.equalsIgnoreCase(CLIENT_DETAILS)) {
                return new Packet00_ClientDetails();

		} else if (packetName.equalsIgnoreCase(MESSAGE)) {
            return new Packet2_Message();
		}
		
		return null;		
	}
}
