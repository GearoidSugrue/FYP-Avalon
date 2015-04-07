package com.example.gearoid.testchatapp.wifidirect.servicediscovery;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
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
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gearoid.testchatapp.utils.ApplicationContext;
import com.example.gearoid.testchatapp.game.gamesetup.GameSetupActivity;
import com.example.gearoid.testchatapp.PlayerListViewAdapter;
import com.example.gearoid.testchatapp.R;
import com.example.gearoid.testchatapp.utils.SharedPrefManager;
import com.example.gearoid.testchatapp.kryopackage.KRegisterAndPort;
import com.example.gearoid.testchatapp.kryopackage.server.ListenerServer;
import com.example.gearoid.testchatapp.kryopackage.Packet;
import com.example.gearoid.testchatapp.kryopackage.PacketFactory;
import com.example.gearoid.testchatapp.multiplayer.Player;
import com.example.gearoid.testchatapp.multiplayer.Session;
import com.example.gearoid.testchatapp.singletons.ClientInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.gearoid.testchatapp.multiplayer.Session.serverListener;

/**
 * Created by gearoid on 27/02/15.
 */

public class WiFiDirectServiceActivity extends ActionBarActivity implements WiFiDirectServicesList.DeviceClickListener, WifiP2pManager.ConnectionInfoListener,
        WifiP2pManager.ChannelListener, ListenerServer.IConnectionActivityServerListener {

    public static int groupOwnerIntent;//15 highest intention to be group owner. May be ignored if a device remembers previous group.
    boolean isHost = false;
    PlayerListViewAdapter adapterConnectedPlayers;
    ListView connectedPlayersView;

    public static final String TAG = "wifidirectservice";
    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;

    String mDeviceName = null; // the p2p name that is configurated from UI.
    WifiP2pDevice mThisDevice = null;

    private final IntentFilter intentFilter = new IntentFilter();
    WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;

    ProgressDialog progressDialog = null;

    //Service discovery stuff...
    private WifiP2pDnsSdServiceRequest serviceRequest;
    WifiP2pDnsSdServiceInfo serviceInfo = null;
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

        Intent intent = getIntent();
        isHost = intent.getBooleanExtra("isHost", false);

        statusTxtView = (TextView) findViewById(R.id.status_text);

        TextView statusTextView = (TextView) findViewById(R.id.status_text);
        statusTextView.setMovementMethod(new ScrollingMovementMethod());

        // add necessary intent values to be matched.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);//Not really needed


        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);

        channel = manager.initialize(this, getMainLooper(), null);

        WiFiDirectServicesList fragment = (WiFiDirectServicesList) this.getFragmentManager()
                .findFragmentById(R.id.frag_service_list);

        if (isHost) {
            Session.host(); //TODO check this host method works and add other related functions to it(ie creating KryoServer)
            getSupportActionBar().setTitle("Hosting Game");
            groupOwnerIntent = 15;
            fragment.setFindingTextView(getString(R.string.finding_players));
            Toolbar tb = (Toolbar) findViewById(R.id.wifidirect_service_toolbar);
            tb.setBackgroundColor(getResources().getColor(R.color.GreenDark));

            initialiseConnectedPlayersList();

            //CreateGroup here???

        } else {
            getSupportActionBar().setTitle("Joining Game");
            groupOwnerIntent = 0;

            Toolbar tb = (Toolbar) findViewById(R.id.wifidirect_service_toolbar);
            tb.setBackgroundColor(getResources().getColor(R.color.SlateBlueDark));

            TextView infoTv = (TextView) findViewById(R.id.textview_label_info);
            infoTv.setBackgroundColor(getResources().getColor(R.color.SlateBlue));

            fragment.setColorsToBlue();
        }

        //Allows time for isWifiP2pEnabled to be set to true when starting this activity
        Thread discoverServiceThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000);
                    startRegistration();//registers local service to manager
                    setServiceListenersAndServiceRequest();//Sets Service Listeners, Service Requests + starts discovery

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        discoverServiceThread.start();

        initialiseButtons();
    }

    public void initialiseConnectedPlayersList(){
        LinearLayout connectedPLayersLayout = (LinearLayout) findViewById(R.id.linearLayout_connectedPlayers);
        connectedPLayersLayout.setVisibility(View.VISIBLE);

        if (serverListener != null) {
            serverListener.attachConnectionActivityToServerListener(this);
        }

        connectedPlayersView = (ListView) findViewById(R.id.listview_connectedPlayers);
        ArrayList<Player> connectedPlayersArray = new ArrayList<>();

        adapterConnectedPlayers = new PlayerListViewAdapter(this, R.layout.row_players, android.R.id.text1, connectedPlayersArray);
        connectedPlayersView.setAdapter(adapterConnectedPlayers);
    }

    public void initialiseButtons() { //TODO hide this until at least 4 other devices are connected
        Button button_gameSetup = (Button) findViewById(R.id.button_continueToSetup);

        if(isHost) {
            button_gameSetup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ApplicationContext.getContext(), GameSetupActivity.class);
                    int numberOfConnections = Session.serverAllPlayerConnections.size();
                    Log.d(TAG, "Number of devices connected to KryoServer is: " + numberOfConnections);
                    intent.putExtra("PLAYER_COUNT", numberOfConnections); //TODO Fix this. Pass in number of connected devices
                    startActivity(intent);

                }
            });
        } else {
            button_gameSetup.setVisibility(View.GONE);
        }
    }

    public void appendStatus(String status) {
        String current = statusTxtView.getText().toString();
        statusTxtView.setText(current + "\n" + "- " + status);
    }

    /**
     * Adds local service too the WifiP2pManager
     */
    private void startRegistration() {

        if (isWifiP2pEnabled) {
            Log.d(TAG, "Starting Registration");

            //  Create a string map containing information about your service.
            Map record = new HashMap();
            record.put("TCPport", String.valueOf(KRegisterAndPort.TCP_PORT));
            record.put("UDPport", String.valueOf(KRegisterAndPort.UDP_PORT));
            record.put("buddyname", SharedPrefManager.getStringDefaults("USERNAME", this));
            record.put("groupOwnerIntent", "" + groupOwnerIntent);

            if(mThisDevice.status == WifiP2pDevice.CONNECTED){
                record.put("available", "false");
            } else {
                record.put("available", "true");
            }

            // Service information.  Pass it an instance name, service type
            // _protocol._transportlayer , and the map containing
            // information other devices will want once they connect to this one.

            if (isHost) {
                serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(SERVICE_INSTANCE_HOST, "_presence._tcp", record);
            } else {
                serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(SERVICE_INSTANCE_JOIN, "_presence._tcp", record);//may need to change full domain
            }
            //may need to change full domain
            //
            // addLocalService(serviceInfo);

            manager.addLocalService(channel, serviceInfo, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {

                    appendStatus("Added Local Service");
                    Log.d(TAG, "Local service added");
                }

                @Override
                public void onFailure(int reasonCode) {
                    appendStatus("Failed to add local service:" + getWiFiP2pFailureMessage(reasonCode));
                    Log.e(TAG, "Failed to add local service:" + getWiFiP2pFailureMessage(reasonCode));
                }
            });
        }
    }

    private void setServiceListenersAndServiceRequest() {
        if (!isWifiP2pEnabled) {
            ApplicationContext.showToast("P2P not enabled. Ensure WiFi is turned on.");//Make this a long toast...

        } else {
            Log.d(TAG, "method called: setServiceListenersAndServiceRequest");

            WifiP2pManager.DnsSdTxtRecordListener txtListener = new WifiP2pManager.DnsSdTxtRecordListener() {
                @Override
        /* Callback includes:
         * fullDomain: full domain name: e.g "printer._ipp._tcp.local."
         * record: TXT record dta as a map of key/value pairs.
         * device: The device running the advertised service.
         */
                //May need to filter using fullDomain...and groupOwnerIntent
                public void onDnsSdTxtRecordAvailable(String fullDomain, Map record, WifiP2pDevice device) {

                    Log.d(TAG, "DnsSdTxtRecord available: " + fullDomain);

                    if (!isHost && fullDomain.startsWith(SERVICE_INSTANCE_HOST)) {
                        buddies.put(device.deviceAddress, record.get("buddyname").toString());
                        //appendStatus("DnsSdTxtRecord Found: " + record.get("buddyname").toString());
                        Log.d(TAG, "DnsSdTxtRecord available: " + record.get("buddyname").toString() + fullDomain);

                    } else if (isHost && fullDomain.startsWith(SERVICE_INSTANCE_JOIN)) {
                        buddies.put(device.deviceAddress, record.get("buddyname").toString());
                        //appendStatus("[Host] DnsSdTxtRecord Found: " + record.get("buddyname").toString());
                        Log.d(TAG, "DnsSdTxtRecord available: " + record.get("buddyname").toString() + fullDomain);
                    }
                }
            };

            WifiP2pManager.DnsSdServiceResponseListener servListener = new WifiP2pManager.DnsSdServiceResponseListener() {
                @Override
                public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice srcDevice) {

                    Log.d(TAG, "onBonjourServiceAvailable: " + instanceName);

                    if (!isHost && instanceName.equalsIgnoreCase(SERVICE_INSTANCE_HOST)) {//May need to change to diff between host and join...
                        // Update the device name with the human-friendly version from
                        // the DnsTxtRecord, assuming one arrived.
                        srcDevice.deviceName = buddies
                                .containsKey(srcDevice.deviceAddress) ? buddies
                                .get(srcDevice.deviceAddress) : srcDevice.deviceName;

                        ApplicationContext.showToast("Host found: " + srcDevice.deviceName);
                        appendStatus("Host found: " + srcDevice.deviceName);

                        // Add to the custom adapter defined specifically for showing
                        // wifi devices.
                        WiFiDirectServicesList fragment = (WiFiDirectServicesList) getFragmentManager().findFragmentById(R.id.frag_service_list);

                        if (fragment != null) {
                            WiFiDirectServicesList.WiFiDevicesAdapter adapter = ((WiFiDirectServicesList.WiFiDevicesAdapter) fragment
                                    .getListAdapter());

                            int position = fragment.checkIfDeviceAlreadyAdded(srcDevice.deviceAddress, srcDevice.deviceName);

                            WiFiP2pService service = new WiFiP2pService();
                            service.device = srcDevice;
                            service.instanceName = instanceName;
                            service.serviceRegistrationType = registrationType;

                            if (position >= 0) {
                                Log.d(TAG, "Replacing WiFiDirectP2pService: " + position + ", " + service.instanceName + ", " + registrationType);
                                //TODO test or remove this, service replace 1
                                fragment.replaceExistingService(position, service);//Is this really needed..??
                            } else {
                                adapter.add(service);
                                adapter.notifyDataSetChanged();
                                Log.d(TAG, "Adding a WiFiDirectP2pService: " + service.instanceName + ", " + registrationType);
                            }

                        } else {
                            Log.d(TAG, "Failed to find fragment...........");
                        }
                    } else if (isHost && instanceName.equalsIgnoreCase(SERVICE_INSTANCE_JOIN)) {
                        // Update the device name with the human-friendly version from
                        // the DnsTxtRecord, assuming one arrived.
                        srcDevice.deviceName = buddies
                                .containsKey(srcDevice.deviceAddress) ? buddies
                                .get(srcDevice.deviceAddress) : srcDevice.deviceName;

                        ApplicationContext.showToast("Player found: " + srcDevice.deviceName);
                        appendStatus("Player found: " + srcDevice.deviceName);

                        // Add to the custom adapter defined specifically for showing
                        // wifi devices.
                        WiFiDirectServicesList fragment = (WiFiDirectServicesList) getFragmentManager().findFragmentById(R.id.frag_service_list);

                        if (fragment != null) {
                            WiFiDirectServicesList.WiFiDevicesAdapter adapter = ((WiFiDirectServicesList.WiFiDevicesAdapter) fragment
                                    .getListAdapter());
                            int position = fragment.checkIfDeviceAlreadyAdded(srcDevice.deviceAddress, srcDevice.deviceName);

                            WiFiP2pService service = new WiFiP2pService();
                            service.device = srcDevice;
                            service.instanceName = instanceName;
                            service.serviceRegistrationType = registrationType;

                            if (position >= 0) {
                                Log.d(TAG, "Replacing WiFiDirectP2pService: " + position + ", " + service.instanceName + ", " + registrationType);
                                //TODO test or remove this, service replace 2
                                fragment.replaceExistingService(position, service);
                            } else {
                                adapter.add(service);
                                adapter.notifyDataSetChanged();
                                Log.d(TAG, "Adding a WiFiDirectP2pService: " + service.instanceName + ", " + registrationType);
                            }
                        }

                    }

                }


            };

            manager.setDnsSdResponseListeners(channel, servListener, txtListener);
            // After attaching listeners(Record & Service), create a Service Request and Initiate Discovery.

            serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
            manager.addServiceRequest(channel,
                    serviceRequest,
                    new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            appendStatus("Added Service Discovery Request");
                            Log.d(TAG, "Added Service Discovery Request");

                            discoverServices();//Setup successfully finished, start Discovery.
                        }

                        @Override
                        public void onFailure(int reasonCode) {
                            appendStatus("Failed To Add Service Discovery Request: " + getWiFiP2pFailureMessage(reasonCode));
                            appendStatus("Select 'Discover' To Try Again.");
                            Log.e(TAG, "Failed to add service request:" + getWiFiP2pFailureMessage(reasonCode));
                        }
                    });
        }
    }

    public void discoverServices() {//Stop P2P discovery, Remove ALL service Requests and reset listeners before calling this

        manager.discoverServices(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                appendStatus("Started Service Discovery.");
                Log.d(TAG, "Started Service Discovery.");
            }

            @Override
            public void onFailure(int reasonCode) {

                if (reasonCode != WifiP2pManager.NO_SERVICE_REQUESTS) {
                    ApplicationContext.showToast("Failed To Start Service Discovery: " + getWiFiP2pFailureMessage(reasonCode));
                    ApplicationContext.showToast("Select 'Discover' to try again");
                    appendStatus("Failed To Start Service Discovery: " + getWiFiP2pFailureMessage(reasonCode));
                    appendStatus("Select 'Discover' To Try Again.");
                    Log.d(TAG, "Failed To Start Service Discovery: " + getWiFiP2pFailureMessage(reasonCode));
                } else {// Error: NO_SERVICE_REQUESTS
                    ApplicationContext.showToast("Failed To Start Service Discovery: " + getWiFiP2pFailureMessage(reasonCode));
                    ApplicationContext.showToast("Use 'Reset WiFi' and select 'Discover' to try again");
                    appendStatus("Failed To Start Service Discovery: " + getWiFiP2pFailureMessage(reasonCode));
                    appendStatus("Use 'Reset WiFi' if error reoccurs, then select 'Discover' to try again.");
                    Log.d(TAG, "Failed To Start Service Discovery: " + getWiFiP2pFailureMessage(reasonCode));
                }
            }
        });
    }

    public void clearAllServiceRequests(final boolean restartDiscovery) {

        if (manager != null && channel != null) {

            manager.stopPeerDiscovery(channel, new WifiP2pManager.ActionListener() {//Necessary to prevent infinite loop of Error: NO_SERVICE_REQUESTS
                @Override
                public void onSuccess() {
                    // initiate clearing of the all service requests
                    manager.clearServiceRequests(channel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            appendStatus("Cleared All Service Requests");
                            Log.d(TAG, "Cleared All Service Requests");

                            if (restartDiscovery) {//Reset and restart Service Discovery
                                setServiceListenersAndServiceRequest();
                            }
                        }

                        @Override
                        public void onFailure(int reasonCode) {
                            Log.d(TAG, "FAILED to clear service requests ");
                            appendStatus("Failed to clear Service Requests: " + getWiFiP2pFailureMessage(reasonCode));
                        }
                    });

                }

                @Override
                public void onFailure(int reasonCode) {
                    Log.d(TAG, "FAILED to stop discovery");
                    appendStatus("Failed to stop discovery: " + getWiFiP2pFailureMessage(reasonCode));
                }
            });
        }
    }

    public void clearLocalServices() {
        if (manager != null && channel != null) {

            manager.stopPeerDiscovery(channel, new WifiP2pManager.ActionListener() {//Necessary to prevent infinite loop or Error: NO_SERVICE_REQUESTS
                @Override
                public void onSuccess() {
                    // initiate clearing of the all service requests
                    manager.clearLocalServices(channel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            appendStatus("Cleared All Local Services");
                            Log.d(TAG, "Cleared All Local Services");
                        }

                        @Override
                        public void onFailure(int reasonCode) {
                            Log.d(TAG, "FAILED to clear local services: " + getWiFiP2pFailureMessage(reasonCode));
                            appendStatus("Failed to clear local Service: " + getWiFiP2pFailureMessage(reasonCode));
                        }
                    });

                }

                @Override
                public void onFailure(int reasonCode) {
                    Log.d(TAG, "FAILED to stop discovery");
                    appendStatus("Failed to stop discovery: " + getWiFiP2pFailureMessage(reasonCode));
                }
            });
        }

    }

    public void showConnectingDialog(String deviceName, final WifiP2pDevice device) {
        progressDialog = ProgressDialog.show(this, "Press Back to Cancel",
                "Connecting to: " + deviceName, true, true, new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        cancelConnection();
                    }
                });
    }

    public void cancelConnection() {
        if (manager != null) {
            final WiFiDirectServicesList fragment = (WiFiDirectServicesList) getFragmentManager()
                    .findFragmentById(R.id.frag_service_list);

            if (fragment.getThisDevice() == null
                    || fragment.getThisDevice().status == WifiP2pDevice.CONNECTED) {
                //disconnect();
                Log.d(TAG, "Cancel Connection Called but already connected");

            } else if (fragment.getThisDevice().status == WifiP2pDevice.AVAILABLE
                    || fragment.getThisDevice().status == WifiP2pDevice.INVITED) {

                clearAllServiceRequests(false);

                manager.cancelConnect(channel, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        ApplicationContext.showToast("Aborting connection");
                        Log.d(TAG, "Aborting connection");
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        ApplicationContext.showToast("Connect abort request failed. Reason Code: " + getWiFiP2pFailureMessage(reasonCode));
                        Log.d(TAG, "Connect abort request failed. Reason Code: " + getWiFiP2pFailureMessage(reasonCode));
                    }
                });
            }
        }
    }

    public void dismissConnectingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    /**
     * process WIFI_P2P_THIS_DEVICE_CHANGED_ACTION intent, refresh this device.
     */
    public void updateThisDevice(final WifiP2pDevice device) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WiFiDirectServicesList fragment = (WiFiDirectServicesList) getFragmentManager()
                        .findFragmentById(R.id.frag_service_list);
                fragment.updateThisDevice(device);
            }
        });
    }

    @Override
    public void connectP2p(final WiFiP2pService service) {//Called when an item in the device list fragment is clicked
        final String deviceName = service.device.deviceName;

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = service.device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        config.groupOwnerIntent = groupOwnerIntent;//This value is ignored by remembered groups

        //Peer Discovery must be running to connect to a device
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                showConnectingDialog(deviceName, service.device);

                Log.d(TAG, "Starting To Connect To: " + deviceName + ", " + service.device.deviceAddress);
            }

            @Override
            public void onFailure(int errorCode) {
                ApplicationContext.showToast("Failed To Start Connecting To " + deviceName + ": " + getWiFiP2pFailureMessage(errorCode));
                appendStatus("Failed To Start Connecting To " + deviceName + ": " + getWiFiP2pFailureMessage(errorCode));
                Log.d(TAG, "Failed To Start Connecting To " + deviceName + ": " + getWiFiP2pFailureMessage(errorCode));
            }
        });
    }

    public void disconnect() {

        final WiFiDirectServicesList fragment = (WiFiDirectServicesList) getFragmentManager()
                .findFragmentById(R.id.frag_service_list);

        WiFiDirectServicesList.WiFiDevicesAdapter adapter = ((WiFiDirectServicesList.WiFiDevicesAdapter) fragment
                .getListAdapter());
        adapter.clear();
        adapter.notifyDataSetChanged();

        clearAllServiceRequests(false);
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Disconnect failed. Reason:" + getWiFiP2pFailureMessage(reasonCode));
            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "Disconnect succeed.");
            }

        });
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {

        final WiFiDirectServicesList fragment = (WiFiDirectServicesList) getFragmentManager()
                .findFragmentById(R.id.frag_service_list);

        if (p2pInfo.groupFormed && p2pInfo.isGroupOwner) {
            Log.d(TAG, "Connected as group owner");

            final String ownerIpFinal = p2pInfo.groupOwnerAddress.getHostAddress();
            Session.join(ownerIpFinal);//Host is also a player

            fragment.setOtherDeviceStatusToConnected();

            ApplicationContext.showToast("Connected as: Host");

//            Thread sendPacketThread = new Thread() {//The host is also a player!!
//                @Override
//                public void run() {
//                    try {
//                        sleep(1200);
//                        if (ClientInstance.getKryoClientInstance().getClient().isConnected()) {
//                            Packet.Packet00_ClientDetails testPacket = (Packet.Packet00_ClientDetails) PacketFactory.createPacket("Client Details");
//                            testPacket.playerName = SharedPrefManager.getStringDefaults("USERNAME", ApplicationContext.getContext());
//                            ApplicationContext.showToast("[C] Sending Test Packet...");
//                            ClientInstance.getKryoClientInstance().getClient().sendTCP(testPacket);
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            };
//            sendPacketThread.start();

        } else if (p2pInfo.groupFormed) {
            Log.d(TAG, "Connected as peer");

            final String ownerIpFinal = p2pInfo.groupOwnerAddress.getHostAddress();
            Session.join(ownerIpFinal);

            TextView waitingForHostView = (TextView) findViewById(R.id.textView_waitingForHost);
            TextView informationView = (TextView) findViewById(R.id.status_text);
            informationView.setVisibility(View.GONE);
            waitingForHostView.setVisibility(View.VISIBLE);

            fragment.setOtherDeviceStatusToConnected();

            ApplicationContext.showToast("Connected as: Player");

//            Thread sendPacketThread = new Thread() {//The host is also a player!!
//                @Override
//                public void run() {
//                    try {
//                        sleep(1500);
//                        if (ClientInstance.getKryoClientInstance().getClient().isConnected()) {
//                            Packet.Packet00_ClientDetails testPacket = (Packet.Packet00_ClientDetails) PacketFactory.createPacket("Client Details");
//                            testPacket.playerName = SharedPrefManager.getStringDefaults("USERNAME", ApplicationContext.getContext());
//                            ApplicationContext.showToast("[C] Sending Test Packet...");
//                            ClientInstance.getKryoClientInstance().getClient().sendTCP(testPacket);
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            };
//            sendPacketThread.start();
        }
    }

    /**
     * register the BroadcastReceiver with the intent values to be matched
     */
    @Override
    public void onResume() {
        super.onResume();
        receiver = new WiFiDirectServiceBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() { //Called after when finished activity. Ensure wifi direct + kryoNet is not stopped
        Log.d(TAG, "onDestroy() Called");
        //ApplicationContext.showToast("onDestroy() Called");
/*
        if (manager != null && channel != null) {
            //removeLocalServices(serviceInfo); //Removes a Local Service.
            //manager.removeServiceRequest(); //Removes a Service Request.
            ServerInstance.getServerInstance().closeServer();
            clearAllServiceRequests(false);
            clearLocalServices();
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
        serviceInfo = null;
        */

        super.onDestroy();
    }

    private static String getWiFiP2pFailureMessage(int errorCode) {
        switch (errorCode) {
            case WifiP2pManager.BUSY:
                return "BUSY";
            case WifiP2pManager.ERROR:
                return "ERROR";
            case WifiP2pManager.P2P_UNSUPPORTED:
                return "P2P_UNSUPPORTED";
            case WifiP2pManager.NO_SERVICE_REQUESTS:
                return "NO_SERVICE_REQUESTS";
            default:
                return "UNKNOWN - ERRORCODE: " + errorCode;
        }
    }

    public void turnWifiOffandOn() {
        final WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
            ApplicationContext.showToast("Turning WiFi Off");


            Thread discoverServiceThread = new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(2500);
                        ApplicationContext.showToast("Turning WiFi Back On");
                        wifiManager.setWifiEnabled(true);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            discoverServiceThread.start();
        } else {
            ApplicationContext.showToast("Turning WiFi On");
            wifiManager.setWifiEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wifi_direct_service, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reset_wifi) {

            //TODO test this
            turnWifiOffandOn();
            return true;
        } else if (id == R.id.option_direct_discover) {

            WiFiDirectServicesList fragment = (WiFiDirectServicesList) this.getFragmentManager()
                    .findFragmentById(R.id.frag_service_list);
            fragment.clearNonConnectedPeers(); //resets list, only connected peers remain//Not sure if working....
            fragment.showProgressBar();

            if (serviceInfo == null) {
                startRegistration();
            }
            //updateThisDevice(mThisDevice);//may need to delete or mod
            //fragment.updatePeerDetails();//.............
            clearAllServiceRequests(true);
            //manager.requestPeers(channel, (WifiP2pManager.PeerListListener) getFragmentManager().findFragmentById(R.id.frag_service_list));

            return true;
        } else if (id == R.id.option_direct_disconnect) {
            disconnect();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override //ChannelListener
    public void onChannelDisconnected() {

        if (manager != null && !retryChannel) {
            ApplicationContext.showToast("Channel lost. Trying again");

            retryChannel = true;
            manager.initialize(this, getMainLooper(), this);
            Log.d(TAG, "Channel lost. Trying again");
        } else {
            ApplicationContext.showToast("Channel is lost. Try Disable/Re-Enable WiFi");
            Log.d(TAG, "Channel is lost. Try Disable/Re-Enable WiFi");

        }
    }

    @Override
    public void server_OnPlayerConnect(final Player newlyConnectedPlayer) {
        Log.d(TAG, "server_OnPlayerConnect received from ServerListener. Player name: " + newlyConnectedPlayer.userName);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "runOnUiThread: adding player to adapterConnectedPlayers");

                adapterConnectedPlayers.add(newlyConnectedPlayer);

                TextView connectedPlayersLabel = (TextView) findViewById(R.id.textview_label_connectedPlayers);
                connectedPlayersLabel.setText("Connected Players " + adapterConnectedPlayers.getCount() + " (Including You)");

                if(adapterConnectedPlayers.getCount() == 2){
                    View item = adapterConnectedPlayers.getView(0, null, connectedPlayersView);
                    item.measure(0, 0);
                    ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (2 * item.getMeasuredHeight()));
                    connectedPlayersView.setLayoutParams(params);
                } else if(adapterConnectedPlayers.getCount() > 2){
                    View item = adapterConnectedPlayers.getView(0, null, connectedPlayersView);
                    item.measure(0, 0);
                    ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (2.5 * item.getMeasuredHeight()));
                    connectedPlayersView.setLayoutParams(params);
                }

            }
        });


    }
}
