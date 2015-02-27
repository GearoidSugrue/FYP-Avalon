package com.example.gearoid.testchatapp.singletons;

import com.esotericsoftware.kryonet.Client;
import com.example.gearoid.testchatapp.kryoPack.KryoClient;

/**
 * Created by gearoid on 24/02/15.
 */
public class ClientInstance {
    private static ClientInstance instance;
    private static KryoClient client;

    private ClientInstance(){

    }

    public static ClientInstance getInstance(){

        if(instance == null){
            instance = new ClientInstance();
        }
        return instance;
    }

    public static KryoClient getKryoClientInstance(){

        if(client == null){
            client = new KryoClient();
        }
        return client;
    }

}
