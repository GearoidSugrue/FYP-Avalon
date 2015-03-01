package com.example.gearoid.testchatapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

/**
 * Created by gearoid on 27/02/15.
 */

public class WiFiDirectBroadcastReceiverService extends BroadcastReceiver {

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private WiFiDirectServiceActivity activity;

    /**
     * @param manager  WifiP2pManager system service
     * @param channel  Wifi p2p channel
     * @param activity activity associated with the receiver
     */
    public WiFiDirectBroadcastReceiverService(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                              WiFiDirectServiceActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }


    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        Log.d(WiFiDirectServiceActivity.TAG, action);
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi Direct mode is enabled
                activity.setIsWifiP2pEnabled(true);
                ApplicationContext.showToast("WiFi Direct is enabled");
                Log.d(WiFiDirectServiceActivity.TAG, "WiFi Direct is enabled");
            } else {
                activity.setIsWifiP2pEnabled(false);
                Log.d(WiFiDirectServiceActivity.TAG, "WiFi Direct is disabled");

                //activity.resetData();//clears details like UI, lists...
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if (manager == null) {
                return;
            }
            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                // we are connected with the other device, request connection
                // info to find group owner IP
                ApplicationContext.showToast("Connected to other device. Requesting network details");
                Log.d(WiFiDirectServiceActivity.TAG, "Connected to p2p network. Requesting network details");
                manager.requestConnectionInfo(channel, (WifiP2pManager.ConnectionInfoListener) activity);//Calls

                WiFiDirectServicesList fragment = (WiFiDirectServicesList) activity.getFragmentManager()
                        .findFragmentById(R.id.frag_service_list);//hides progress bar
                fragment.dismissProgressBar();
            } else {
                // It's a disconnect
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            WifiP2pDevice device = (WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            //Update UI
            WiFiDirectServicesList fragment = (WiFiDirectServicesList) activity.getFragmentManager()
                    .findFragmentById(R.id.frag_service_list);
            fragment.updateThisDevice(device);//Say were connected, available etc...

            Log.d(WiFiDirectServiceActivity.TAG, "Device status -" + WiFiDirectServicesList.getDeviceStatus(device.status));
        } else if(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION.equals(action)) {

            final int discoveryState = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, 0);

            if(discoveryState == 1){
                ApplicationContext.showToast("P2P discovery has stopped");
//                WiFiDirectServicesList fragment = (WiFiDirectServicesList) activity.getFragmentManager()
//                        .findFragmentById(R.id.frag_service_list);
//                fragment.dismissProgressBar();

                Log.d(WiFiDirectServiceActivity.TAG, "P2P discovery has stopped");
            } else if (discoveryState == 2 ){//doesn't work...................NEVER CALLED!!!
                ApplicationContext.showToast("P2P discovery has started");
//                WiFiDirectServicesList fragment = (WiFiDirectServicesList) activity.getFragmentManager()
//                        .findFragmentById(R.id.frag_service_list);
//                fragment.showProgressBar();
                Log.d(WiFiDirectServiceActivity.TAG, "P2P discovery has started");
            }

        }

    }
}
