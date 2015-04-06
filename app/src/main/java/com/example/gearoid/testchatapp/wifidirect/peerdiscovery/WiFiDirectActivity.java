package com.example.gearoid.testchatapp.wifidirect.peerdiscovery;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import android.support.v7.app.ActionBarActivity;


import com.example.gearoid.testchatapp.utils.ApplicationContext;
import com.example.gearoid.testchatapp.wifidirect.peerdiscovery.DeviceListFragment.DeviceActionListener;
import com.example.gearoid.testchatapp.R;
import com.example.gearoid.testchatapp.singletons.ServerInstance;

import java.util.HashMap;


public class WiFiDirectActivity extends ActionBarActivity implements ChannelListener, DeviceActionListener {

    public static int groupOwnerIntent;//15 highest intention to be group owner. May be ignored if a device remembers previous group.

    public static final String TAG = "wifidirectdemo";
    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;
    private boolean isHost = false;

    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private BroadcastReceiver receiver = null;

    //Service discovery stuff...
    private WifiP2pDnsSdServiceRequest serviceRequest;

    final HashMap<String, String> buddies = new HashMap<String, String>();


    /**
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.peerdiscovery);

        //crashes app if manifest file does not include what theme is being used
        //layout xml also needs to include the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.wifidirect_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // add necessary intent values to be matched.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);

        channel = manager.initialize(this, getMainLooper(), null);

        isHost = getIntent().getBooleanExtra("isHost", false);

        if (isHost) {
            groupOwnerIntent = 15;
//            ServerInstance.getServerInstance().startServer();//Start KryoNet server
            ServerInstance.server.getServer().start();
        }

    }


    /**
     * register the BroadcastReceiver with the intent values to be matched
     */
    @Override
    public void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public void initiateDiscoverPeers() {//Only initiates peer discovery
        if (!isWifiP2pEnabled) {
            Toast.makeText(WiFiDirectActivity.this, "P2P not enabled!",
                    Toast.LENGTH_SHORT).show();
        } else {
            final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
                    .findFragmentById(R.id.frag_list);
            fragment.onInitiateDiscovery();
            manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    Toast.makeText(WiFiDirectActivity.this, "Discovery Initiated",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int reasonCode) {

                    final String errFinal = getWiFiP2pFailureMessage(reasonCode);

                    Thread thread = new Thread() {//The host is also a player!!
                        @Override
                        public void run() {
                            try {
                                sleep(300);
                                fragment.dismissProgressDialog();
                                ApplicationContext.showToast("Discovery Not Initiated: " + errFinal);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    thread.start();
                }
            });
        }

    }

    private static String getWiFiP2pFailureMessage(int errorCode) {
        Log.d(WiFiDirectActivity.TAG, "Discovery Not Initiated: " + errorCode);
        switch (errorCode) {
            case WifiP2pManager.BUSY:
                return "Busy";
            case WifiP2pManager.ERROR:
                return "Error";
            case WifiP2pManager.P2P_UNSUPPORTED:
                return "P2P_Unsupported";
            default:
                return "Unknown";
        }
    }

    /**
     * Remove all peers and clear all fields. This is called on
     * BroadcastReceiver receiving a state change event.
     */
    public void resetData() {
        DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager()
                .findFragmentById(R.id.frag_list);
        DeviceDetailFragment fragmentDetails = (DeviceDetailFragment) getFragmentManager()
                .findFragmentById(R.id.frag_detail);
        if (fragmentList != null) {
            fragmentList.clearPeers();
        }
        if (fragmentDetails != null) {
            fragmentDetails.resetViews();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wi_fi_direct, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.option_direct_discover) {
            initiateDiscoverPeers();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showDetails(WifiP2pDevice device) {
        DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
                .findFragmentById(R.id.frag_detail);
        fragment.showDetails(device);


        //Hide Connect and disconnect button...until the user selects a peer from device list
        //fragment.hideButtons();
//        if(device.status == 0 && !device.isGroupOwner()) {
//            fragment.hideConnectButton();
//        }
//        }  else {
//            fragment.hideDisconnectButton();
//            fragment.hideConnectButton();
//        }
        //mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
    }

    @Override
    public void connect(WifiP2pConfig config) {
        manager.connect(channel, config, new ActionListener() {

            @Override
            public void onSuccess() {

                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
                //send data to others.
                //Toast.makeText(WiFiDirectActivity.this, "Connected Successfully.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) { //Test This.................................................
                Toast.makeText(WiFiDirectActivity.this, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
                DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
                        .findFragmentById(R.id.frag_detail);
                fragment.dismissProgressDialog();
                //Dismiss dialog...
            }
        });
    }

    @Override
    public void disconnect() {
        final DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
                .findFragmentById(R.id.frag_detail);
        fragment.resetViews();
        manager.removeGroup(channel, new ActionListener() {

            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Disconnect failed. Reason:" + reasonCode);
            }

            @Override
            public void onSuccess() {
                fragment.getView().setVisibility(View.GONE);
            }

        });
    }

    @Override //ChannelListener
    public void onChannelDisconnected() {//for disconnects
        // we will try once more
        if (manager != null && !retryChannel) {
            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
            resetData();
            retryChannel = true;
            manager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this,
                    "Severe! Channel is probably lost permanently. Try Disable/Re-Enable P2P.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void cancelDisconnect() {//file transfer

        /*
         * A cancel abort request by user. Disconnect i.e. removeGroup if
         * already connected. Else, request WifiP2pManager to abort the ongoing
         * request
         */
        if (manager != null) {
            final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
                    .findFragmentById(R.id.frag_list);
            if (fragment.getDevice() == null
                    || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
                disconnect();
            } else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE
                    || fragment.getDevice().status == WifiP2pDevice.INVITED) {

                manager.cancelConnect(channel, new ActionListener() {

                    @Override
                    public void onSuccess() {
                        Toast.makeText(WiFiDirectActivity.this, "Aborting connection",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(WiFiDirectActivity.this,
                                "Connect abort request failed. Reason Code: " + reasonCode,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }
}
