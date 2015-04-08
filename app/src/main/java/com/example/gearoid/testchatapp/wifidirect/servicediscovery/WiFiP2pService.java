package com.example.gearoid.testchatapp.wifidirect.servicediscovery;

import android.net.wifi.p2p.WifiP2pDevice;


/**
 * Created by gearoid on 27/02/15.
 */

/**
 * Holds service information.
 */
public class WiFiP2pService {
    WifiP2pDevice device;
    String instanceName = null;
    String serviceRegistrationType = null;
    boolean isConnected = false;
}