package com.example.gearoid.testchatapp.singletons;

import com.example.gearoid.testchatapp.kryopackage.KServer;

/**
 * Created by gearoid on 24/02/15.
 */
public class ServerInstance {
    private static ServerInstance instance;
    public static KServer server;


    private ServerInstance(){

    }

    public static ServerInstance getInstance(){

        if(instance == null){
            instance = new ServerInstance();
        }
        return instance;
    }

    public static KServer getServerInstance(){

        if(server == null){
            server = new KServer();
        }
        return server;
    }


}
