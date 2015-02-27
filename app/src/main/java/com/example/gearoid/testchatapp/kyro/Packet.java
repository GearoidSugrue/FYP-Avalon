package com.example.gearoid.testchatapp.kyro;

/**
 * Created by gearoid on 15/01/15.
 */
public class Packet {

    public static class Packet0LoginRequest { }
    public static class Packet1LoginResult { boolean accepted = false; }
    public static class Packet2Message { String message;}

}
