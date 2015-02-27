package com.example.gearoid.testchatapp.kyro;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.example.gearoid.testchatapp.kyro.Packet.*;

/**
 * Created by gearoid on 17/01/15.
 */
public class KNetwork {

    static public final int port = 54555;

    // This registers objects that are going to be sent over the network.
    static public void register(EndPoint endPoint) {//(Kryo kryo)...create EndPoint Class???
        Kryo kryo = endPoint.getKryo();
        kryo.register(Packet0LoginRequest.class);
        kryo.register(Packet1LoginResult.class);
        kryo.register(Packet2Message.class);

//        kryo.register(Login.class);
//        kryo.register(RegistrationRequired.class);
//        kryo.register(Register.class);
//        kryo.register(AddCharacter.class);
//        kryo.register(UpdateCharacter.class);
//        kryo.register(RemoveCharacter.class);
//        kryo.register(Character.class);
//        kryo.register(MoveCharacter.class);
    }

}
