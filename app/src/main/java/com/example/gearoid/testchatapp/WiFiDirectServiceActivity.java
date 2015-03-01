package com.example.gearoid.testchatapp;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gearoid.testchatapp.kryoPack.KryoRegisterAndPort;
import com.example.gearoid.testchatapp.kryoPack.Packet;
import com.example.gearoid.testchatapp.kryoPack.PacketFactory;
import com.example.gearoid.testchatapp.singletons.ClientInstance;
import com.example.gearoid.testchatapp.singletons.ServerInstance;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gearoid on 27/02/15.
 */

public class WiFiDirectServiceActivity extends ActionBarActivity implements WiFiDirectServicesList.DeviceClickListener, WifiP2pManager.ConnectionInfoListener, WifiP2pManager.ChannelListener {
    //DeviceListFragment.DeviceActionListener {


    public static int groupOwnerIntent;//15 highest intention to be group owner. May be ignored if a device remembers previous group.

    public static final String TAG = "wifidirectdemo";
    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;

    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;

    private boolean isHost = false;

    //Service discovery stuff...
    private WifiP2pDnsSdServiceRequest serviceRequest;
    WifiP2pDnsSdServiceInfo serviceInfo;
    private WiFiDirectServicesList servicesList;

    public static final String SERVICE_INSTANCE_HOST = "_avalonhost";//has to be lower case
    public static final String SERVICE_INSTANCE_JOIN = "_avalonjoin";//has to be lower case


    final HashMap<String, String> buddies = new HashMap<String, String>();

    private TextView statusTxtView;


    /**
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_direct_service);

        //crashes app if manifest file does not include what theme is being used
        Toolbar toolbar = (Toolbar) findViewById(R.id.wifidirect_service_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        statusTxtView = (TextView) findViewById(R.id.status_text);

        // add necessary intent values to be matched.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);



        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);

        //manager.addLocalService();   //Add host service to device so clients can see it.............???
        channel = manager.initialize(this, getMainLooper(), null);

        Intent intent = getIntent();
        isHost = intent.getBooleanExtra("isHost", false);

        if(isHost){
            groupOwnerIntent = 15;
            ServerInstance.getServerInstance();
            WiFiDirectServicesList fragment = (WiFiDirectServicesList) this.getFragmentManager()
                        .findFragmentById(R.id.frag_service_list);
                fragment.setFindingTextView(getString(R.string.finding_players));
            /*
            manager.createGroup(channel,new WifiP2pManager.ActionListener() {//Create group as group owner.
                @Override
                public void onSuccess() {
                    // Success!
                    //ApplicationContext.showToast("Successfully added service request");
                    appendStatus("[Host] Successfully created group");
                    Log.d(TAG, "[Host] Successfully created group");
                }

                @Override
                public void onFailure(int reasonCode) {
                    final String errFinal = getWiFiP2pFailureMessage(reasonCode);
                    //ApplicationContext.showToast("Failed to add service request:" + errFinal);
                    appendStatus("[Host] Failed to create group");
                    Log.e(TAG, "[Host] Failed to create group:" + errFinal);
                }
            });
            */
        }

        Thread discoverServiceThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                    startRegistration();//registers local service to manager
                    discoverService();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        discoverServiceThread.start();
    }

    public void appendStatus(String status) {
        String current = statusTxtView.getText().toString();
        statusTxtView.setText(current + "\n" + status);
    }

    /**
     * Adds local service too the WifiP2pManager
     */
    private void startRegistration() {

        if (!isWifiP2pEnabled) {
            ApplicationContext.showToast("P2P not enabled. Ensure WiFi is turned on.");
        } else {
            Log.d(TAG, "method called: startRegistration");


            //  Create a string map containing information about your service.
            Map record = new HashMap();
            record.put("TCPport", String.valueOf(KryoRegisterAndPort.TCP_PORT));
            record.put("UDPport", String.valueOf(KryoRegisterAndPort.UDP_PORT));
            record.put("buddyname", SharedPrefManager.getStringDefaults("USERNAME", this));
            record.put("groupOwnerIntent", "" + groupOwnerIntent);
            record.put("available", "visible");

            // Service information.  Pass it an instance name, service type
            // _protocol._transportlayer , and the map containing
            // information other devices will want once they connect to this one.

            if (isHost) {
                serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(SERVICE_INSTANCE_HOST, "_presence._tcp", record);

            } else {
                serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(SERVICE_INSTANCE_JOIN, "_presence._tcp", record);//may need to change full domain
            }
            //may need to change full domain
            addLocalService(serviceInfo);

            // Add the local service, sending the service info, network channel,
            // and listener that will be used to indicate success or failure of
            // the request.

            // discoverService();//Move to onCreate/ on button push???...............................
        }
    }

    private void discoverService() {
        if (!isWifiP2pEnabled) {
            ApplicationContext.showToast("Error: P2P not enabled. Ensure WiFi is turned on.");//Make this a long toast...

        } else {
            Log.d(TAG, "method called: discoverService");

            WifiP2pManager.DnsSdTxtRecordListener txtListener = new WifiP2pManager.DnsSdTxtRecordListener() {
                @Override
        /* Callback includes:
         * fullDomain: full domain name: e.g "printer._ipp._tcp.local."
         * record: TXT record dta as a map of key/value pairs.
         * device: The device running the advertised service.
         */
                //May need to filter using fullDomain...and groupOwnerIntent
                public void onDnsSdTxtRecordAvailable(String fullDomain, Map record, WifiP2pDevice device) {

                    Log.d(TAG, "DnsSdTxtRecord available -" + fullDomain);

                    if (!isHost && fullDomain.startsWith(SERVICE_INSTANCE_HOST)) {//...SERVICE_INSTANCE_JOIN
                        ApplicationContext.showToast("DnsSdTxtRecord found: " + record.get("buddyname").toString());
                        buddies.put(device.deviceAddress, record.get("buddyname").toString());
                        appendStatus("DnsSdTxtRecord found: " + record.get("buddyname").toString());

                        Log.d(TAG, "DnsSdTxtRecord available -" + record.get("buddyname").toString() + fullDomain);

                    } else if (isHost && fullDomain.startsWith(SERVICE_INSTANCE_JOIN)) {
                        ApplicationContext.showToast("[Host] DnsSdTxtRecord found: " + record.get("buddyname").toString());
                        buddies.put(device.deviceAddress, record.get("buddyname").toString());
                        appendStatus("DnsSdTxtRecord found: " + record.get("buddyname").toString() + " - " + fullDomain);

                        Log.d(TAG, "DnsSdTxtRecord available -" + record.get("buddyname").toString() + fullDomain);
                    }
                }
            };

            //May need to filter by instanceName....
            WifiP2pManager.DnsSdServiceResponseListener servListener = new WifiP2pManager.DnsSdServiceResponseListener() {
                @Override
                public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice srcDevice) {

                    Log.d(TAG, "onBonjourServiceAvailable " + instanceName);

                    if (!isHost && instanceName.equalsIgnoreCase(SERVICE_INSTANCE_HOST)) {//May need to change to diff between host and join...
                        // Update the device name with the human-friendly version from
                        // the DnsTxtRecord, assuming one arrived.
                        srcDevice.deviceName = buddies
                                .containsKey(srcDevice.deviceAddress) ? buddies
                                .get(srcDevice.deviceAddress) : srcDevice.deviceName;

                        ApplicationContext.showToast("DnsSdService found: " + srcDevice.deviceName);
                        appendStatus("DnsSdService found: " + srcDevice.deviceName);

                        // Add to the custom adapter defined specifically for showing
                        // wifi devices.
                        WiFiDirectServicesList fragment = (WiFiDirectServicesList) getFragmentManager().findFragmentById(R.id.frag_service_list);

                        if (fragment != null) {
                            WiFiDirectServicesList.WiFiDevicesAdapter adapter = ((WiFiDirectServicesList.WiFiDevicesAdapter) fragment
                                    .getListAdapter());
                            WiFiP2pService service = new WiFiP2pService();
                            service.device = srcDevice;
                            service.instanceName = instanceName;
                            service.serviceRegistrationType = registrationType;
                            adapter.add(service);
                            adapter.notifyDataSetChanged();
                        }
                    } else if (isHost && instanceName.equalsIgnoreCase(SERVICE_INSTANCE_JOIN)) {

                        // Update the device name with the human-friendly version from
                        // the DnsTxtRecord, assuming one arrived.
                        srcDevice.deviceName = buddies
                                .containsKey(srcDevice.deviceAddress) ? buddies
                                .get(srcDevice.deviceAddress) : srcDevice.deviceName;

                        ApplicationContext.showToast("[Host] DnsSdService found: " + srcDevice.deviceName);
                        appendStatus("[Host] DnsSdService found: " + srcDevice.deviceName + " - " + buddies.get(srcDevice.deviceAddress));

                        // Add to the custom adapter defined specifically for showing
                        // wifi devices.
                        WiFiDirectServicesList fragment = (WiFiDirectServicesList) getFragmentManager().findFragmentById(R.id.frag_service_list);

                        if (fragment != null) {
                            WiFiDirectServicesList.WiFiDevicesAdapter adapter = ((WiFiDirectServicesList.WiFiDevicesAdapter) fragment
                                    .getListAdapter());
                            WiFiP2pService service = new WiFiP2pService();
                            service.device = srcDevice;
                            service.instanceName = instanceName;
                            service.serviceRegistrationType = registrationType;
                            adapter.add(service);
                            adapter.notifyDataSetChanged();
                        }
                    }

                }

            };

            manager.setDnsSdResponseListeners(channel, servListener, txtListener);
            // After attaching listeners, create a service request and initiate discovery.

            serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
            manager.addServiceRequest(channel,
                    serviceRequest,
                    new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            // Success!
                            //ApplicationContext.showToast("Successfully added service request");
                            appendStatus("Added service discovery request");
                            Log.d(TAG, "Added service discovery request");
                        }

                        @Override
                        public void onFailure(int reasonCode) {
                            // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                            final String errFinal = getWiFiP2pFailureMessage(reasonCode);
                            //ApplicationContext.showToast("Failed to add service request:" + errFinal);
                            appendStatus("Failed adding service discovery request");
                            Log.e(TAG, "Failed to add service request:" + errFinal);
                        }
                    });

            manager.discoverServices(channel, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    // Success!
                    //ApplicationContext.showToast("Successfully started service discovery");
                    appendStatus("Service discovery initiated");
                    Log.d(TAG, "Service discovery initiated");
                }

                @Override
                public void onFailure(int reasonCode) {
                    // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                    final String errFinal = getWiFiP2pFailureMessage(reasonCode);
                    ApplicationContext.showToast("Failed to initiate service discovery: " + errFinal);
                    appendStatus("Failed to initiate service discovery: " + errFinal);
                    Log.d(TAG, "Failed to initiate service discovery: " + errFinal);
                }
            });
        }
    }

    public void addLocalService(final WifiP2pDnsSdServiceInfo serviceInfo) {
        manager.addLocalService(channel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // Command successful! Code isn't necessarily needed here,
                // Unless you want to update the UI or add logging statements.
                //ApplicationContext.showToast("Successfully added local service");
                appendStatus("Added Local Service");
                Log.d(TAG, "Local service added");
            }

            @Override
            public void onFailure(int reasonCode) {
                final String errFinal = getWiFiP2pFailureMessage(reasonCode);
                //ApplicationContext.showToast("Failed to add local service:" + errFinal);
                appendStatus("Failed to add local service:" + errFinal);
                Log.e(TAG, "Failed to add local service:" + errFinal);
                // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
            }
        });
    }

    public void removeLocalServices(final WifiP2pDnsSdServiceInfo serviceInfo) {
        if (serviceInfo != null) {
            manager.removeLocalService(channel, serviceInfo, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    // Command successful! Code isn't necessarily needed here,
                    // Unless you want to update the UI or add logging statements.
                    //ApplicationContext.showToast("Successfully added local service");
                    appendStatus("Removed Local Service");
                    Log.d(TAG, "Removed Local Service ");
                }

                @Override
                public void onFailure(int reasonCode) {
                    final String errFinal = getWiFiP2pFailureMessage(reasonCode);
                    //ApplicationContext.showToast("Failed to add local service:" + errFinal);
                    appendStatus("Failed to remove service");
                    Log.e(TAG, "Failed to remove service: " + errFinal);
                    // Command failed.  Check for P2P_UNSUPPORTED, ERROR, or BUSY
                }
            });
        }
    }

    @Override
    public void connectP2p(WiFiP2pService service) {//Called when an item in the device list fragment is clicked
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = service.device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        config.groupOwnerIntent = groupOwnerIntent;//this value is ignored by remembered groups
        if (serviceRequest != null)
            manager.removeServiceRequest(channel, serviceRequest,
                    new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Successfully removed service.");
                        }

                        @Override
                        public void onFailure(int errorCode) {
                            Log.d(TAG, "Failed to remove service: " + getWiFiP2pFailureMessage(errorCode));
                        }
                    });

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                ApplicationContext.showToast("Connecting to service...");
                appendStatus("Connecting to service");
                Log.d(TAG, "Connecting to service");
                //appendStatus("Connecting to service");
            }

            @Override
            public void onFailure(int errorCode) {
                ApplicationContext.showToast("Failed connecting to service" + getWiFiP2pFailureMessage(errorCode));
                appendStatus("Failed connecting to service");
                Log.d(TAG, "Failed connecting to service: " + getWiFiP2pFailureMessage(errorCode));
                //appendStatus("Failed connecting to service");
            }
        });
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {

        if (p2pInfo.isGroupOwner) {
            ApplicationContext.showToast("Connected as Group Owner");
//            WiFiDirectServicesList fragment = (WiFiDirectServicesList) activity.getFragmentManager()
//                    .findFragmentById(R.id.frag_service_list);
//            fragment.updateThisDevice(device);//Update each device status using our own service class/adapter class

            Log.d(TAG, "Connected as group owner");
        } else if(p2pInfo.groupFormed) {
            ApplicationContext.showToast("Connected as Peer");
            Log.d(TAG, "Connected as peer");
            //statusTxtView.setVisibility(View.GONE);


            //Start client....????

        }

    }


    /**
     * register the BroadcastReceiver with the intent values to be matched
     */
    @Override
    public void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiverService(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

//    @Override
//    protected void onRestart() {
//        //Fragment frag = getFragmentManager().findFragmentByTag("services");
//
//        Log.d(TAG, "onRestart() called");
//        ApplicationContext.showToast("onRestart() called");
//
//        WiFiDirectServicesList frag = (WiFiDirectServicesList) getFragmentManager().findFragmentById(R.id.frag_service_list);
//
//        if (frag != null) {
//            getFragmentManager().beginTransaction().remove(frag).commit();
//        }
//        super.onRestart();
//    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy() Called");
        ApplicationContext.showToast("onDestroy() Called");

        if (manager != null && channel != null) {
            removeLocalServices(serviceInfo);//Removes local service.
            manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onFailure(int reasonCode) {
                    Log.d(TAG, "Failed to Remove Group: " + getWiFiP2pFailureMessage(reasonCode));
                }

                @Override
                public void onSuccess() {
                    Log.d(TAG, "Successfully Removed Group");
                }
            });
        }
        super.onDestroy();
    }

//    @Override
//    protected void onStop() {
//        if (manager != null && channel != null) {
//            removeLocalServices(serviceInfo);//Removes local service.
//            manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
//                @Override
//                public void onFailure(int reasonCode) {
//                    Log.d(TAG, "Failed to Remove Group: " + getWiFiP2pFailureMessage(reasonCode));
//                }
//
//                @Override
//                public void onSuccess() {
//                    Log.d(TAG, "Successfully Removed Group");
//                }
//            });
//        }
//        super.onStop();
//    }

/*

    public void initiateDiscoverPeers() {//Only initiates peer discovery
        if (!isWifiP2pEnabled) {
            ApplicationContext.showToast("P2P not enabled!");
        } else {
            final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
                    .findFragmentById(R.id.frag_list);
            fragment.onInitiateDiscovery();
            manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    ApplicationContext.showToast("Discovery Initiated");
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

    */

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
    /*
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
    */
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
            removeLocalServices(serviceInfo);
            startRegistration();
            discoverService();
            //initiateDiscoverPeers();
        }

        return super.onOptionsItemSelected(item);
    }

    /*
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
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {

                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
                //send data to others.
                //Toast.makeText(WiFiDirectActivity.this, "Connected Successfully.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) { //Test This.................................................
                ApplicationContext.showToast("Connect failed. Retry.");
                DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
                        .findFragmentById(R.id.frag_detail);
                fragment.dismissProgressDialog();
            }
        });
    }
*/
//    @Override
//    public void disconnect() {
//        final DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
//                .findFragmentById(R.id.frag_detail);
//        fragment.resetViews();
//        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
//
//            @Override
//            public void onFailure(int reasonCode) {
//                Log.d(TAG, "Disconnect failed. Reason:" + reasonCode);
//            }
//
//            @Override
//            public void onSuccess() {
//                fragment.getView().setVisibility(View.GONE);
//            }
//
//        });
//    }

    @Override //ChannelListener
    public void onChannelDisconnected() {//for disconnects
        // we will try once more
        if (manager != null && !retryChannel) {
            ApplicationContext.showToast("Channel lost. Trying again");
            //resetData();
            retryChannel = true;
            manager.initialize(this, getMainLooper(), this);
        } else {
            ApplicationContext.showToast("Severe! Channel is probably lost permanently. Try Disable/Re-Enable P2P.");
        }
    }

    /**
     * A cancel abort request by user. Disconnect i.e. removeGroup if
     * already connected. Else, request WifiP2pManager to abort the ongoing
     * request
     */

           /*
    @Override
    public void cancelDisconnect() {//file transfer


        if (manager != null) {
            final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
                    .findFragmentById(R.id.frag_list);
            if (fragment.getDevice() == null
                    || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
                //disconnect();
            } else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE
                    || fragment.getDevice().status == WifiP2pDevice.INVITED) {

                manager.cancelConnect(channel, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        ApplicationContext.showToast("Aborting connection");
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        ApplicationContext.showToast("Connect abort request failed. Reason Code: " + reasonCode);
                    }
                });
            }
        }

    }
    */


}
