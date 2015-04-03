package com.example.gearoid.testchatapp.multiplayer;

import android.util.Log;
import android.widget.TextView;

import com.esotericsoftware.kryonet.Connection;
import com.example.gearoid.testchatapp.ApplicationContext;
import com.example.gearoid.testchatapp.GameLogicFunctions;
import com.example.gearoid.testchatapp.SharedPrefManager;
import com.example.gearoid.testchatapp.kryopackage.ListenerClient;
import com.example.gearoid.testchatapp.kryopackage.ListenerServer;
import com.example.gearoid.testchatapp.kryopackage.Packet;
import com.example.gearoid.testchatapp.kryopackage.PacketFactory;
import com.example.gearoid.testchatapp.singletons.ClientInstance;
import com.example.gearoid.testchatapp.singletons.PlayerConnection;
import com.example.gearoid.testchatapp.singletons.ServerInstance;

import java.util.ArrayList;

/**
 * Created by gearoid on 25/02/15.
 */
public class Session {

    //Client
    public static Connection myConnection;

    //Server
    public static ListenerServer serverListener;
    public static ListenerClient clientListener;

    public static ArrayList<PlayerConnection> masterAllPlayerConnections; //Won't be instantiated until Session object is created TODO fix masterAllPlayers ArrayList
    public static ArrayList<Player> masterAllPlayers;
    public static ArrayList<Player> allPlayers; //Won't be instantiated until Session object is created TODO fix masterAllPlayers ArrayList

    //public static ArrayList<Player> leaderOrderAllPlayers;
    public static ArrayList<Integer> leaderOrderList;
    public static int leaderOrderIterator;

    //public static ArrayList<ICharacter> allCharacters;
    public static GameLogicFunctions.Board currentBoard;
    public static GameLogicFunctions.Quest currentQuest = GameLogicFunctions.Quest.FIRST;
    public static int voteCount = 0;
    public static GameState currentGameState = GameState.TEAM_SELECT;
    public static TextView gameStatusView;
    public static String gameStatusText;

    public static ArrayList<Packet.Packet_TeamVoteReply> teamVoteReplies;
    public static int[] server_currentProposedTeam;
    public static ArrayList<Packet.Packet_QuestVoteReply> questVoteReplies;

    //public static Drawable boardImage;//Is this necessary??? Use Board enum instead...

    //public static HashMap<Player, Connection> playerConnections;

    public enum GameState {
        TEAM_SELECT, TEAM_VOTE, TEAM_VOTE_RESULT, QUEST_VOTE, QUEST_VOTE_RESULT, LADY_OF_LAKE, ASSASSIN, FINISHED
    }

    public Session() {//Find a better way??..
        masterAllPlayerConnections = new ArrayList<>();
        allPlayers = new ArrayList<>();
    }


    public static void host() {
        masterAllPlayerConnections = new ArrayList<>();
        allPlayers = new ArrayList<>();
        masterAllPlayers = new ArrayList<>();

//        ServerInstance.getInstance();
//        ServerInstance.server.getServer().start();
        ServerInstance.getServerInstance().getServer().start();

    }

    public static void join(final String hostAddress) {

        if (!ClientInstance.getKryoClientInstance().getClient().isConnected()) {
            allPlayers = new ArrayList<>();
            leaderOrderList = new ArrayList<>();
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


        Thread thread = new Thread() {//The host is also a player!!
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

    public static void server_sendToEveryone(Object obj) {
        Log.d("Session", "Server: Sending Object to everyone");

        ServerInstance.server.getServer().sendToAllTCP(obj);
    }

    public static void server_sendToSelectedPlayers(int[] playerIDs, Object obj) {
        Log.d("Session", "Server: Sending Object to selected players");

        for (int i = 0; i < playerIDs.length; i++) {

            int conID = Session.masterAllPlayerConnections.get(playerIDs[i]).playerConnection.getID();
            ServerInstance.server.getServer().sendToTCP(conID, obj);
        }

    }

    public static void server_sendToPlayer(int playerID, Object obj) {
        Log.d("Session", "Server: Sending Object to player: " + playerID);


        ServerInstance.server.getServer().sendToTCP(Session.masterAllPlayerConnections.get(playerID).playerConnection.getID(), obj);
    }
}


