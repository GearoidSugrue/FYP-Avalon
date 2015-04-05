package com.example.gearoid.testchatapp.wifidirect.peerdiscovery;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gearoid.testchatapp.utils.ApplicationContext;
import com.example.gearoid.testchatapp.R;
import com.example.gearoid.testchatapp.utils.SharedPrefManager;
import com.example.gearoid.testchatapp.kryopackage.Packet;
import com.example.gearoid.testchatapp.kryopackage.PacketFactory;
import com.example.gearoid.testchatapp.singletons.ClientInstance;

/**
 * Created by gearoid on 16/02/15.
 */
public class DeviceDetailFragment extends Fragment implements WifiP2pManager.ConnectionInfoListener {


    /**
     * A fragment that manages any given peer and allows interaction with the phone
     * like setting up network connections and transferring data.
     */
    public static int PORT = 8988;


    protected static final int CHOOSE_FILE_RESULT_CODE = 20;
    private View mContentView = null;
    private WifiP2pDevice device;
    private WifiP2pInfo info;
    ProgressDialog progressDialog = null;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContentView = inflater.inflate(R.layout.device_detail, null);
        mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {//sets up new connection when you click search glass icon
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                config.groupOwnerIntent = WiFiDirectActivity.groupOwnerIntent;

                dismissProgressDialog();

                progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
                        "Connecting to :" + device.deviceAddress, true, true
                );
                ((DeviceListFragment.DeviceActionListener) getActivity()).connect(config);

            }
        });

        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ((DeviceListFragment.DeviceActionListener) getActivity()).disconnect();
                    }
                });

        mContentView.findViewById(R.id.btn_start_client).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) { //not needed
                        // Allow user to pick an image from Gallery or other
                        // registered apps
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);
                    }
                });

        return mContentView;
    }

    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //For 2 way file transfer. Needs Utils class...
        /*
        String localIP = Utils.getLocalIPAddress();
		// Trick to find the ip in the file /proc/net/arp
		String client_mac_fixed = new String(device.deviceAddress).replace("99", "19");
		String clientIP = Utils.getIPFromMac(client_mac_fixed);

         */


        // the user/client has picked an image and is going to transfer it to group owner/server i.e peer using
        // FileTransferService but this can be modified to just send any kind of data.

//            Uri uri = data.getData();
//            TextView statusText = (TextView) mContentView.findViewById(R.id.status_text);
//            statusText.setText("Sending: " + uri);
//            Log.d(WiFiDirectActivity.TAG, "Intent----------- " + uri);
//
//            Intent serviceIntent = new Intent(getActivity(), FileTransferService.class);
//            serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
//            serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());//not needed
//            serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
//                    info.groupOwnerAddress.getHostAddress());
//            serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
//
//            getActivity().startService(serviceIntent);
    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        dismissProgressDialog();

        this.info = info;

        try {
            this.getView().setVisibility(View.VISIBLE);
        } catch (NullPointerException e) {
            // ApplicationContext.showToast("Failed to get View.");
            Log.e(WiFiDirectActivity.TAG, e.getMessage());
        }

        // The owner IP is now known.
        TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(getResources().getString(R.string.group_owner_text)
                + ((info.isGroupOwner == true) ? getResources().getString(R.string.yes)
                : getResources().getString(R.string.no)));

        // ip from WifiP2pInfo struct.
        view = (TextView) mContentView.findViewById(R.id.device_info);


        String ownerIP = "Unknown!";
        try {
            ownerIP = this.info.groupOwnerAddress.getHostAddress();//Can cause app to crash...Samsung devices
        } catch (NullPointerException e) {
            ApplicationContext.showToast("Error: Unable to obtain IP address!");
            Log.e(WiFiDirectActivity.TAG, "Error: Unable to obtain IP address!!!!");
        }
        final String ownerIpFinal = ownerIP;
        view.setText("Group Owner IP - " + ownerIpFinal);


        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server
        // socket its the handiest way with no needlessly complicated shiet.
        if (info.groupFormed && info.isGroupOwner) {

//                new FileServerAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text))
//                        .execute();
            //ApplicationContext.showToast("Group owner: True");

            //ServerInstance.getServerInstance();

            Thread thread = new Thread() {//The host is also a player!!
                @Override
                public void run() {
                    try {
                        sleep(1000);
                        if (!ClientInstance.getKryoClientInstance().getClient().isConnected()) {
                            ClientInstance.getKryoClientInstance().connectToServer(ownerIpFinal);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };

            thread.start();

            Thread sendPacketThread = new Thread() {//The host is also a player!!
                @Override
                public void run() {
                    try {
                        sleep(1200);
                        if (ClientInstance.getKryoClientInstance().getClient().isConnected()) {
                            //ClientInstance.getKryoClientInstance().connectToServer(ownerIP);
                            Packet.Packet00_ClientDetails testPacket = (Packet.Packet00_ClientDetails) PacketFactory.createPacket("Client Details");
                            testPacket.playerName = SharedPrefManager.getStringDefaults("USERNAME", ApplicationContext.getContext());
                            //testPacket.playerName = "Mope";  //SharedPrefManager.getStringDefaults("USERNAME", ApplicationContext.getContext());
                            ApplicationContext.showToast("[C] Sending Packet...");
                            ClientInstance.getKryoClientInstance().getClient().sendTCP(testPacket);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            sendPacketThread.start();
//            KryoServer server = new KryoServer();
//
//            Thread thread = new Thread() {//The host is also a player!!
//                @Override
//                public void run() {
//                    try {
//                        sleep(1000);
//                        KryoClient client = new KryoClient(ownerIP);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            };
//
//            thread.start();

            //You are the group owner, start Kryonet server here???

        } else if (info.groupFormed) {
            //Below line not working here
            mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);//Don't want clients connecting to other devices...I think???
            mContentView.findViewById(R.id.btn_disconnect).setVisibility(View.VISIBLE);

            //ApplicationContext.showToast("Group owner: False");

            Thread thread = new Thread() {//The host is also a player!!
                @Override
                public void run() {
                    try {
                        sleep(1000);
                        if (!ClientInstance.getKryoClientInstance().getClient().isConnected()) {
                            ClientInstance.getKryoClientInstance().connectToServer(ownerIpFinal);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();

            //final String userName = SharedPrefManager.getStringDefaults("USERNAME", this);
            Thread sendPacketThread = new Thread() {//The host is also a player!!
                @Override
                public void run() {
                    try {
                        sleep(1500);
                        if (ClientInstance.getKryoClientInstance().getClient().isConnected()) {
                            //ClientInstance.getKryoClientInstance().connectToServer(ownerIP);
                            Packet.Packet00_ClientDetails testPacket = (Packet.Packet00_ClientDetails) PacketFactory.createPacket("Client Details");
                            testPacket.playerName = SharedPrefManager.getStringDefaults("USERNAME", ApplicationContext.getContext());
                            //testPacket.playerName = "Mope";  //SharedPrefManager.getStringDefaults("USERNAME", ApplicationContext.getContext());
                            ApplicationContext.showToast("[C] Sending Packet...");
                            ClientInstance.getKryoClientInstance().getClient().sendTCP(testPacket);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            sendPacketThread.start();


//            Thread thread = new Thread() {
//                @Override
//                public void run() {
//                    try {
//                            sleep(1000);
//                            KryoClient client = new KryoClient(ownerIP);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            };
//
//            thread.start();


            // The other device acts as the client. In this case, we enable the
            // get file button.

            //You are the client. Start kryonet client here. Add a delay to allow server to start.


//                mContentView.findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);
//                ((TextView) mContentView.findViewById(R.id.status_text)).setText(getResources()
//                        .getString(R.string.client_text));
        }

        // hide the connect button
       // mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
    }

    /**
     * Updates the UI with device data
     *
     * @param device the device to be displayed duh!
     */
    public void showDetails(WifiP2pDevice device) {
        this.device = device;
        try {
            this.getView().setVisibility(View.VISIBLE);
        } catch (NullPointerException e) {
            Log.e(WiFiDirectActivity.TAG, e.getMessage());
        }
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(device.deviceAddress);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(device.toString());

        //if(this.device.isGroupOwner())    //find away to prevent clients from being able to connect to each other.

        if(device.status == 0){
            mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
            mContentView.findViewById(R.id.btn_disconnect).setVisibility(View.VISIBLE);
        } else {
            mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
            mContentView.findViewById(R.id.btn_disconnect).setVisibility(View.GONE);
        }

    }

    public void hideButtons(){
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
        mContentView.findViewById(R.id.btn_disconnect).setVisibility(View.GONE);
    }

    public void hideDisconnectButton(){
        mContentView.findViewById(R.id.btn_disconnect).setVisibility(View.GONE);
    }

    public void hideConnectButton(){
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
    }

    /**
     * Clears the UI fields after a disconnect or direct mode disable operation.
     */
    public void resetViews() {
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.status_text);
        view.setText(R.string.empty);
        mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
        try {
            this.getView().setVisibility(View.GONE);
        } catch (NullPointerException e) {
            Log.e(WiFiDirectActivity.TAG, e.getMessage());
        }
    }

//    public void resetViews() {
//        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
//        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
//        view.setText(R.string.empty);
//        view = (TextView) mContentView.findViewById(R.id.device_info);
//        view.setText(R.string.empty);
//        view = (TextView) mContentView.findViewById(R.id.group_owner);
//        view.setText(R.string.empty);
//        view = (TextView) mContentView.findViewById(R.id.status_text);
//        view.setText(R.string.empty);
//        mContentView.findViewById(R.id.btn_start_client).setVisibility(View.GONE);
//        this.getView().setVisibility(View.GONE);
//    }

    /**
     * A simple server socket that accepts connection and writes some data on
     * the stream. THIS IS THE BACKBONE OF THE WHOLE LOT SO GET THIS RIGHT AND YOUR LAUGHING
     */
    public static class FileServerAsyncTask extends AsyncTask<Void, Void, String> {

        private Context context;
        private TextView statusText;

        /**
         * @param context    not quiet sure what these do tbh :/
         * @param statusText
         */
        public FileServerAsyncTask(Context context, View statusText) {
            this.context = context;
            this.statusText = (TextView) statusText;
        }

        //            @Override
//            protected String doInBackground(Void... params) {//transfer files - keeps going
//                try {
//                    ServerSocket serverSocket = new ServerSocket(8988);
//                    Log.d(WiFiDirectActivity.TAG, "Server: Socket opened");
//                    Socket client = serverSocket.accept();
//                    Log.d(WiFiDirectActivity.TAG, "Server: connection done");
//                    final File f = new File(Environment.getExternalStorageDirectory() + "/"
//                            + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
//                            + ".jpg");
//
//                    File dirs = new File(f.getParent());
//                    if (!dirs.exists())
//                        dirs.mkdirs();
//                    f.createNewFile();
//
//                    Log.d(WiFiDirectActivity.TAG, "server: copying files " + f.toString());
//                    InputStream inputstream = client.getInputStream();
//                    copyFile(inputstream, new FileOutputStream(f));
//                    serverSocket.close();
//                    return f.getAbsolutePath();
//                } catch (IOException e) {
//                    Log.e(WiFiDirectActivity.TAG, e.getMessage());
//                    return null;
//                }
//            }
        @Override
        protected String doInBackground(Void... params) {//transfer files - keeps going
            try {

                //KryoNet stuff here...??????
                return null;
            } catch (Exception e) {
                Log.e(WiFiDirectActivity.TAG, e.getMessage());
                return null;
            }
        }


        //            @Override
//            protected void onPostExecute(String result) {
//                if (result != null) {
//                    statusText.setText("File copied - " + result);
//                    Intent intent = new Intent();
//                    intent.setAction(android.content.Intent.ACTION_VIEW);
//                    intent.setDataAndType(Uri.parse("file://" + result), "image/*");
//                    context.startActivity(intent);
//                }
//
//            }
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                statusText.setText("File copied - " + result);
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + result), "image/*");
                context.startActivity(intent);
            }

        }

        @Override
        protected void onPreExecute() {
            statusText.setText("Opening a server socket");
        }

    }

//        public static boolean copyFile(InputStream inputStream, OutputStream out) {// not needed
//            byte buf[] = new byte[1024];
//            int len;
//            try {
//                while ((len = inputStream.read(buf)) != -1) {
//                    out.write(buf, 0, len);
//
//                }
//                out.close();
//                inputStream.close();
//            } catch (IOException e) {
//                Log.d(WiFiDirectActivity.TAG, e.toString());
//                return false;
//            }
//            return true;
//        }


}

