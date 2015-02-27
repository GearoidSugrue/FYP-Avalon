package com.example.gearoid.testchatapp.singletons;

import com.example.gearoid.testchatapp.kryoPack.KryoServer;

/**
 * Created by gearoid on 24/02/15.
 */
public class ServerInstance {
    private static ServerInstance instance;
    private static KryoServer server;


    private ServerInstance(){

    }

    public static ServerInstance getInstance(){

        if(instance == null){
            instance = new ServerInstance();
        }
        return instance;
    }

    public static KryoServer getServerInstance(){

        if(server == null){
            server = new KryoServer();
        }
        return server;
    }
}
