package com.example.gearoid.testchatapp.multiplayer;

import android.content.BroadcastReceiver;
import android.os.PowerManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.esotericsoftware.kryonet.Connection;
import com.example.gearoid.testchatapp.R;
import com.example.gearoid.testchatapp.game.GameBoardFragment;
import com.example.gearoid.testchatapp.utils.ApplicationContext;
import com.example.gearoid.testchatapp.game.GameLogicFunctions;
import com.example.gearoid.testchatapp.utils.SharedPrefManager;
import com.example.gearoid.testchatapp.kryopackage.client.ListenerClient;
import com.example.gearoid.testchatapp.kryopackage.server.ListenerServer;
import com.example.gearoid.testchatapp.kryopackage.Packet;
import com.example.gearoid.testchatapp.singletons.ClientInstance;
import com.example.gearoid.testchatapp.singletons.PlayerConnection;
import com.example.gearoid.testchatapp.singletons.ServerInstance;

import java.util.ArrayList;

/**
 * Created by gearoid on 25/02/15.
 */
public class Session {

    public static Connection myConnection;

    public static ListenerServer serverListener;
    public static ListenerClient clientListener;

    public static ArrayList<PlayerConnection> serverAllPlayerConnections;
    public static ArrayList<Player> serverAllPlayers;
    public static ArrayList<Player> allPlayers;

    public static ArrayList<Integer> leaderOrderList;
    public static int leaderOrderIterator;

    public static GameLogicFunctions.Board currentBoard;
    public static GameLogicFunctions.Quest currentQuest = GameLogicFunctions.Quest.FIRST;
    public static int voteCount = 0;
    public static boolean isGameIntialised = false;
    public static boolean serverIsLadyOfLakeOn = false;
    public static GameState currentGameState = GameState.TEAM_SELECT;
    public static Toolbar mainToolbar;
    public static String gameToolbarText = "";
    public static TextView gameStatusView;
    public static String gameStatusText = "";
    public static TextView gameActionsView;
    public static ArrayList<GameLogicFunctions.Quest> serverQuestResults;
    public static ArrayList<GameLogicFunctions.Quest> clientQuestResults;
    public static PowerManager powerManager;
    public static BroadcastReceiver mReceiver;

    public static ArrayList<Packet.Packet_TeamVoteReply> server_teamVoteReplies;
    public static int[] server_currentProposedTeam;
    public static ArrayList<Packet.Packet_QuestVoteReply> server_questVoteReplies;

    public static GameBoardFragment gameBoardFrag;

    public static LinearLayout playerCharacterFrag;
    public static LinearLayout teamVoteFrag;
    public static LinearLayout questVoteFrag;
    public static LinearLayout teamSelectFrag;
    public static LinearLayout assassinateFrag;
    public static LinearLayout ladyOfLakeFrag;
    public static LinearLayout teamVoteResultFrag;
    public static LinearLayout gameFinishedFrag;
    public static LinearLayout questResultFrag;

    public static boolean isTeamVoteFragVisible = false;
    public static boolean isQuestVoteFragVisible = false;
    public static boolean isTeamSelectFragVisible = false;
    public static boolean isAssassinateFragVisible = false;
    public static boolean isLadyOfLakeFragVisible = false;
    public static boolean isTeamVoteResultFragVisible = false;
    public static boolean isGameFinishedFragVisible = false;
    public static boolean isQuestResultFragVisible = false;

    public static int teamVoteTitleColor;
    public static int teamVoteStatusColor;
    public static int questVoteTitleColor;
    public static int questVoteStatusColor;
    public static int teamSelectTitleColor;
    public static int teamSelectStatusColor;
    public static int assassinateTitleColor;
    public static int assassinateStatusColor;
    public static int ladyOfLakeTitleColor;
    public static int ladyOfLakeStatusColor;
    public static int viewCharacterTitleColor;
    public static int viewCharacterStatusColor;
    public static int standardTitleColor;
    public static int standardStatusColor;


    public enum GameState {
        TEAM_SELECT, QUEST_VOTE, LADY_OF_LAKE, ASSASSIN, //FINISHED
    }

    public Session() {
        serverAllPlayerConnections = new ArrayList<>();
        allPlayers = new ArrayList<>();
    }


    public static void host() {
        serverAllPlayerConnections = new ArrayList<>();
        allPlayers = new ArrayList<>();
        serverAllPlayers = new ArrayList<>();
        serverQuestResults = new ArrayList<>();
        leaderOrderList = new ArrayList<>();
        currentQuest = GameLogicFunctions.Quest.FIRST;
        isGameIntialised = false;
        serverIsLadyOfLakeOn = false;
        gameStatusText = "";
        gameToolbarText = "";
        voteCount = 0;
        leaderOrderIterator = 0;
        currentGameState = GameState.TEAM_SELECT;


        ServerInstance.getServerInstance().getServer().start();
    }

    public static void join(final String hostAddress) {

        if (!ClientInstance.getKryoClientInstance().getClient().isConnected()) {
            allPlayers = new ArrayList<>();
            leaderOrderList = new ArrayList<>();
            clientQuestResults = new ArrayList<>();
            currentQuest = GameLogicFunctions.Quest.FIRST;
            isGameIntialised = false;
            serverIsLadyOfLakeOn = false;
            gameStatusText = "";
            gameToolbarText = "";
            voteCount = 0;
            leaderOrderIterator = 0;
            currentGameState = GameState.TEAM_SELECT;



            PlayerConnection.getInstance().userName = SharedPrefManager.getStringDefaults("USERNAME", ApplicationContext.getContext());//TODO find better way to do this
            PlayerConnection.getInstance().playerID = -1;
            Thread thread = new Thread() {//The host is also a player!!
                @Override
                public void run() {
                    try {
                        ClientInstance.getKryoClientInstance().connectToServer(hostAddress);
                        Log.d("Session", "Starting Host's Kryo Client ");
                    } catch (Exception e) {
                        Log.d("Session", "Error Starting Host's Kryo Client ");
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }
    }

    public static void client_sendPacketToServer(final Object obj) {


        Thread thread = new Thread() {//The host is also a player
            @Override
            public void run() {
                try {
                    Log.d("Session", "Sending Object to Server");
                    ClientInstance.getKryoClientInstance().getClient().sendTCP(obj);
                } catch (Exception e) {
                    Log.d("Session", "Error Sending Object to Server. Player Name: " + PlayerConnection.getInstance().playerID );
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public static void server_sendToEveryone(final Object obj) {

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Log.d("Session", "Server: Sending Object to everyone");
                    ServerInstance.server.getServer().sendToAllTCP(obj);
                } catch (Exception e) {
                    Log.d("Session", "Error Sending Object to everyone");
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public static void server_sendToSelectedPlayers(int[] playerIDs, final Object obj) {
        Log.d("Session", "Server: Sending Object to selected players");

        for (int i = 0; i < playerIDs.length; i++) {

            final int conID = Session.serverAllPlayerConnections.get(playerIDs[i]).playerConnection.getID();

            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        Log.d("Session", "Server: Sending Object to Player: " + conID);
                        ServerInstance.server.getServer().sendToTCP(conID, obj);
                    } catch (Exception e) {
                        Log.d("Session", "Error Sending Object to Player: " + conID);
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }
    }

    public static void server_sendToPlayer(final int playerID, final Object obj) {
        Log.d("Session", "Server: Sending Object to player: " + playerID);


        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Log.d("Session", "Server: Sending Object to Player: " + playerID);
                    ServerInstance.server.getServer().sendToTCP(Session.serverAllPlayerConnections.get(playerID).playerConnection.getID(), obj);
                } catch (Exception e) {
                    Log.d("Session", "Error Sending Object to Player: " + playerID);
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
}


