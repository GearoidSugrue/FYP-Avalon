package com.example.gearoid.testchatapp.singletons;

import com.example.gearoid.testchatapp.kryopackage.KClient;

/**
 * Created by gearoid on 24/02/15.
 */
public class ClientInstance {
    private static ClientInstance instance;
    private static KClient client;

    private ClientInstance(){

    }

    public static ClientInstance getInstance(){

        if(instance == null){
            instance = new ClientInstance();
        }
        return instance;
    }

    public static KClient getKryoClientInstance(){

        if(client == null){
            client = new KClient();
        }
        return client;
    }
    


}
