package com.example.gearoid.testchatapp.multiplayer;

import android.util.Log;

import com.esotericsoftware.kryonet.Connection;
import com.example.gearoid.testchatapp.singletons.ClientInstance;
import com.example.gearoid.testchatapp.singletons.PlayerConnection;
import com.example.gearoid.testchatapp.utils.ApplicationContext;
import com.example.gearoid.testchatapp.utils.SharedPrefManager;

/**
 * Created by gearoid on 22/03/15.
 */
public class ClientSession {


    public Connection myConnection = null;

    public ClientSession(){

    }

    public void join(final String hostAddress) {

        if (!ClientInstance.getKryoClientInstance().getClient().isConnected()) {
            Thread thread = new Thread() {//The host is also a player!!
                @Override
                public void run() {
                    try {
                        PlayerConnection.getInstance().userName = SharedPrefManager.getStringDefaults("USERNAME", ApplicationContext.getContext());//TODO find better way to do this
                        ClientInstance.getKryoClientInstance().connectToServer(hostAddress);
                        Log.d("Session", "Starting Kryo Client ");
                    } catch (Exception e) {
                        Log.d("Session", "Error Starting Kryo Client ");
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }
    }
}
