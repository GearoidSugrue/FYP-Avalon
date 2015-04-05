package com.example.gearoid.testchatapp.wifidirect.servicediscovery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.example.gearoid.testchatapp.utils.ApplicationContext;
import com.example.gearoid.testchatapp.R;
import com.example.gearoid.testchatapp.wifidirect.peerdiscovery.WiFiDirectActivity;


/**
 * Created by gearoid on 27/02/15.
 */

public class WiFiDirectServiceBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private WiFiDirectServiceActivity activity;

    /**
     * @param manager  WifiP2pManager system service
     * @param channel  Wifi p2p channel
     * @param activity activity associated with the receiver
     */
    public WiFiDirectServiceBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
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
                activity.setIsWifiP2pEnabled(true);
                Log.d(WiFiDirectServiceActivity.TAG, "WiFi Direct is enabled");
            } else {
                activity.clearAllServiceRequests(false);
                activity.mThisDevice = null; // reset this device status
                //activity.mPeers.clear();
                //reset data
                activity.setIsWifiP2pEnabled(false);
                activity.updateThisDevice(null);//Says re-enable wifi direct

                WiFiDirectServicesList fragment = (WiFiDirectServicesList) activity.getFragmentManager()
                        .findFragmentById(R.id.frag_service_list);
                fragment.clearNonConnectedPeers();

                Log.d(WiFiDirectServiceActivity.TAG, "WiFi Direct is disabled");
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // The peer list has changed!  We should probably do something about
            // that.
            // Request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (manager != null) {
                //manager.requestPeers(channel, (WifiP2pManager.PeerListListener) activity.getFragmentManager()
                        //.findFragmentById(R.id.frag_list));
                //manager.requestPeers(channel, (WifiP2pManager.PeerListListener) activity.getFragmentManager().findFragmentById(R.id.frag_service_list));
                Log.d(WiFiDirectActivity.TAG, "P2P peers changed");
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
                manager.requestConnectionInfo(channel, (WifiP2pManager.ConnectionInfoListener) activity);//Results in connectionInfoAvailable being called

                //delete line below....
                //manager.requestPeers(channel,(WifiP2pManager.PeerListListener)  new ArrayList<>() );

                WiFiDirectServicesList fragment = (WiFiDirectServicesList) activity.getFragmentManager()
                        .findFragmentById(R.id.frag_service_list);//hides progress bar
                fragment.dismissProgressBar();
                //fragment.refreshList();//Move to onConnectionInfoAvailable

                activity.dismissConnectingDialog();
            } else {
                // It's a disconnect
                WiFiDirectServicesList fragment = (WiFiDirectServicesList) activity.getFragmentManager()
                        .findFragmentById(R.id.frag_service_list);//hides progress bar
                fragment.clearNonConnectedPeers();

                Log.d(WiFiDirectServiceActivity.TAG, "Someone disconnected from the p2p network.");

            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            activity.mThisDevice = (WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            activity.mDeviceName = activity.mThisDevice.deviceName;
            //Update UI
            activity.updateThisDevice(activity.mThisDevice);//Says were connected, available etc...

            Log.d(WiFiDirectServiceActivity.TAG, "Device status - " + WiFiDirectServicesList.getDeviceStatus(activity.mThisDevice.status));
        } else if(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION.equals(action)) {//delete...doesn't work for services

            final int discoveryState = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, 0);

            if(discoveryState == 1){
                //ApplicationContext.showToast("P2P discovery has stopped");
//                WiFiDirectServicesList fragment = (WiFiDirectServicesList) activity.getFragmentManager()
//                        .findFragmentById(R.id.frag_service_list);
//                fragment.dismissProgressBar();

                Log.d(WiFiDirectServiceActivity.TAG, "P2P discovery has stopped");
            } else if (discoveryState == 2 ){//doesn't work...................
                //ApplicationContext.showToast("P2P discovery has started");
//                WiFiDirectServicesList fragment = (WiFiDirectServicesList) activity.getFragmentManager()
//                        .findFragmentById(R.id.frag_service_list);
//                fragment.showProgressBar();
                Log.d(WiFiDirectServiceActivity.TAG, "P2P discovery has started");
            }

        }

    }
}
