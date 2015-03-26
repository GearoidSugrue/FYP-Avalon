package com.example.gearoid.testchatapp.multiplayer;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.esotericsoftware.kryonet.Connection;
import com.example.gearoid.testchatapp.ApplicationContext;
import com.example.gearoid.testchatapp.GameLogicFunctions;
import com.example.gearoid.testchatapp.GameSetupActivity;
import com.example.gearoid.testchatapp.SharedPrefManager;
import com.example.gearoid.testchatapp.character.ICharacter;
import com.example.gearoid.testchatapp.singletons.ClientInstance;
import com.example.gearoid.testchatapp.singletons.Player;
import com.example.gearoid.testchatapp.singletons.ServerInstance;

import java.util.ArrayList;

/**
 * Created by gearoid on 25/02/15.
 */
public class Session {

    //Client
    public static Connection myConnection;

    //Server
    public static ArrayList<Player> masterAllPlayers; //Won't be instantiated until Session object is created TODO fix masterAllPlayers ArrayList
    public static ArrayList<Player> leaderOrderAllPlayers;
    public static ArrayList<Integer> leaderOrderList;
    public static int leaderOrderIterator;

    public static ArrayList<ICharacter> allCharacters;
    public static GameLogicFunctions.Board gameBoard;
    public static Drawable boardImage;//Is this necessary??? Use Board enum instead...

    //public static HashMap<Player, Connection> playerConnections;

    public Session(){//Find a better way??..
        masterAllPlayers = new ArrayList<>();
    }


    public static void host(){
        masterAllPlayers = new ArrayList<>();
        ServerInstance.getServerInstance().getServer().start();

    }

    public static void join(final String hostAddress){

        if (!ClientInstance.getKryoClientInstance().getClient().isConnected()) {
            Player.getInstance().userName = SharedPrefManager.getStringDefaults("USERNAME", ApplicationContext.getContext());//TODO find better way to do this
            Player.getInstance().playerID = -1;
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


}
