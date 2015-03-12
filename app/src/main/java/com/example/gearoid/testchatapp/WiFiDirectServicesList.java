package com.example.gearoid.testchatapp;

import android.app.ListFragment;

import android.app.ProgressDialog;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v4.app.Fragment;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gearoid on 27/02/15.
 */

/**
 * A simple ListFragment that shows the available services as published by the
 * peers
 */
public class WiFiDirectServicesList extends ListFragment implements WifiP2pManager.PeerListListener {
    WiFiDevicesAdapter listAdapter = null;
    private List<WiFiP2pService> peers = new ArrayList<WiFiP2pService>();
    private View mContentView = null;
    private WifiP2pDevice myDevice;

    private List<WifiP2pDevice> allNearbyPeers = new ArrayList<WifiP2pDevice>();


    interface DeviceClickListener {
        public void connectP2p(WiFiP2pService wifiP2pService);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //progressBar = container.findViewById(R.id.discoveryProgressBar);
        mContentView = inflater.inflate(R.layout.devices_list, null);

        return mContentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listAdapter = new WiFiDevicesAdapter(getActivity(), android.R.layout.simple_list_item_2, android.R.id.text1,
                peers); //Was new ArrayList<WiFiP2pService>()
        this.setListAdapter(listAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        WiFiP2pService selectedService = (WiFiP2pService) l.getItemAtPosition(position);

        if (!SharedPrefManager.getBooleanDefaults("HOST", ApplicationContext.getContext())) {
            if (myDevice.status == WifiP2pDevice.CONNECTED) {
                ApplicationContext.showToast("Already Connected to a Host!");
                ApplicationContext.showToast("Disconnect before changing Host");

            } else if (selectedService.device.status == WifiP2pDevice.INVITED) {
                ApplicationContext.showToast("Already sent connection request");
                ApplicationContext.showToast("Try Disconnect and Discover again");

                ((DeviceClickListener) getActivity()).connectP2p(selectedService); //Delete this......
                ((TextView) v.findViewById(android.R.id.text2)).setText("Connecting");
            } else {
                ((DeviceClickListener) getActivity()).connectP2p(selectedService);
                ((TextView) v.findViewById(android.R.id.text2)).setText("Connecting");
            }
        } else {
            ((DeviceClickListener) getActivity()).connectP2p(selectedService);
            ((TextView) v.findViewById(android.R.id.text2)).setText("Connecting");
        }

    }

    public class WiFiDevicesAdapter extends ArrayAdapter<WiFiP2pService> {
        private List<WiFiP2pService> items;

        public WiFiDevicesAdapter(Context context, int resource, int textViewResourceId, List<WiFiP2pService> items) {
            super(context, resource, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(android.R.layout.simple_list_item_2, null);
            }
            WiFiP2pService service = items.get(position);
            if (service != null) {
                TextView nameText = (TextView) v.findViewById(android.R.id.text1);

                if (nameText != null) {
                    nameText.setText(service.device.deviceName); // + service.instanceName);
                }
                TextView statusText = (TextView) v.findViewById(android.R.id.text2);

                if (statusText != null) {
                    statusText.setText(getDeviceStatus(service.device.status));
                }
//                if(statusText != null ) {
//                    if(service.isConnected) {
//                        statusText.setText("Connected");
//                    } else {
//                        statusText.setText(getDeviceStatus(service.device.status));
//                    }
//                }
            }
            return v;
        }

    }

    public int getNumberOfConnectedDevices() {
        int count = 0;

        for (int j = 0; j < peers.size(); j++) {
            if (peers.get(j).device.status == WifiP2pDevice.CONNECTED) {
                count++;
            }
        }
        return count;
    }

    public void updatePeerDetails() {//Not sure if working
        Log.d(WiFiDirectActivity.TAG, "Trying to update peer details");

        for (int i = 0; i < allNearbyPeers.size(); i++) {
            for (int j = 0; j < peers.size(); j++) {
                Log.d(WiFiDirectActivity.TAG, peers.get(j).device.deviceName + ", " + peers.get(j).device.deviceAddress + " --- " + allNearbyPeers.get(i).deviceName + ", " + allNearbyPeers.get(i).deviceAddress);

                if (allNearbyPeers.get(i).deviceAddress.equalsIgnoreCase(peers.get(j).device.deviceAddress)) {
                    Log.d(WiFiDirectActivity.TAG, "Found a match: " + peers.get(j).device.deviceName + " - " + allNearbyPeers.get(i).deviceName);

                    peers.get(j).device = allNearbyPeers.get(i);
                }
            }
        }
        ((WiFiDevicesAdapter) getListAdapter()).notifyDataSetChanged();
    }

    public int checkIfDeviceAlreadyAdded(String deviceAddress, String deviceName) {//Not sure if working
        Log.d(WiFiDirectActivity.TAG, "Checking if device has already been added to list");

        for (int j = 0; j < peers.size(); j++) {
            Log.d(WiFiDirectActivity.TAG, peers.get(j).device.deviceName + ", " + peers.get(j).device.deviceAddress);

            if (peers.get(j).device.deviceAddress.equalsIgnoreCase(deviceAddress) && peers.get(j).device.deviceName.equals(deviceName)) {
                Log.d(WiFiDirectActivity.TAG, "Found a match: " + peers.get(j).device.deviceName);
                return j;
            }
        }
        return -1;

        //((WiFiDevicesAdapter) getListAdapter()).notifyDataSetChanged();
    }

    public void replaceExistingService(int positionToReplace, WiFiP2pService newService) {
        peers.remove(positionToReplace);
        peers.add(positionToReplace, newService);
        ((WiFiDevicesAdapter) getListAdapter()).notifyDataSetChanged();
    }


    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        Log.d(WiFiDirectActivity.TAG, "onPeersAvailable called...");

        // Out with the old, in with the new.
        allNearbyPeers.clear();
        allNearbyPeers.addAll(peerList.getDeviceList());

        // If an AdapterView is backed by this data, notify it
        // of the change.  For instance, if you have a ListView of available
        // peers, trigger an update.
        //((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
        if (allNearbyPeers.size() == 0) {
            Log.d(WiFiDirectActivity.TAG, "No devices found");
            return;
        }
        //updatePeerDetails();//............................................................
        Log.d(WiFiDirectActivity.TAG, "Found devices: " + allNearbyPeers.size());
    }


//    @Override
//    public void onPeersAvailable(WifiP2pDeviceList peerList) {//called by manager.requestPeers(...) in broadcast receiver
////        if (progressDialog != null && progressDialog.isShowing()) {
////            progressDialog.dismiss();
////        }
//
//        peers.clear();
//        peers.addAll(peerList.getDeviceList());
//
//        ((WiFiDevicesAdapter) getListAdapter()).notifyDataSetChanged();
//        if (peers.size() == 0) {
//            Log.d(WiFiDirectActivity.TAG, "No devices found");
//            ApplicationContext.showToast("No devices found!!");
//        }
//
//    }

    public void clearPeers() {
        peers.clear();
        ((WiFiDevicesAdapter) getListAdapter()).notifyDataSetChanged();
    }

    public void clearNonConnectedPeers() {
        //peers.clear();

        for (int i = 0; i < peers.size(); i++) {
            WiFiP2pService p = peers.get(i);
            if (p.device.status != WifiP2pDevice.CONNECTED || myDevice.status != WifiP2pDevice.CONNECTED) {
                Log.d(WiFiDirectActivity.TAG, "Device is not connected. Removing from list." + p.device.deviceName);
                peers.remove(i);
            }
        }

        ((WiFiDevicesAdapter) getListAdapter()).notifyDataSetChanged();
    }

    public void refreshList() {
        ((WiFiDevicesAdapter) getListAdapter()).notifyDataSetChanged();
    }

    public WifiP2pDevice getThisDevice() {
        return myDevice;
    }


    public void updateThisDevice(WifiP2pDevice device) {  //Test this.....................
        TextView friendlyNameView = (TextView) mContentView.findViewById(R.id.my_device_friendly_name);
        TextView nameView = (TextView) mContentView.findViewById(R.id.my_device_name);
        TextView statusView = (TextView) mContentView.findViewById(R.id.my_device_status);

        if (device != null) {
            Log.d(WiFiDirectActivity.TAG, "updateThisDevice: " + device.deviceName + " = " + getDeviceStatus(device.status));
            this.myDevice = device;
            friendlyNameView.setText(SharedPrefManager.getStringDefaults("USERNAME", ApplicationContext.getContext()));
            nameView.setText(device.deviceName);

            //TODO seprate device status into 2, host and player. move to functions and call from reciever.
            if (SharedPrefManager.getBooleanDefaults("HOST", ApplicationContext.getContext())) {
                TextView label_status = (TextView) mContentView.findViewById(R.id.textview_label_status);
                label_status.setText("Players Connected:");
                statusView.setText("" + getNumberOfConnectedDevices());
            } else if (device.status == WifiP2pDevice.CONNECTED) {
                statusView.setText("Connected to Host");// - " + SharedPrefManager.getStringDefaults("HOST_NAME", ApplicationContext.getContext()));

            } else {
                statusView.setText(getDeviceStatus(device.status));
            }

        } else if (this.myDevice != null) {
            friendlyNameView.setText(SharedPrefManager.getStringDefaults("USERNAME", ApplicationContext.getContext()));
            nameView.setText(this.myDevice.deviceName);
            statusView.setText("WiFi Direct Disabled! Re-enable.");
        }

    }

    public void setDeviceStatusText(String text) {
        TextView view = (TextView) mContentView.findViewById(R.id.my_device_status);
        view.setText(text);
    }

    public void setFindingTextView(String text) {
        TextView view = (TextView) mContentView.findViewById(R.id.finding_text);
        view.setText(text);
    }

    public void dismissProgressBar() {
        mContentView.findViewById(R.id.discoveryProgressBar).setVisibility(View.GONE);
        //(R.id.discoveryProgressBar).setVisibility(View.GONE);
    }

    public void showProgressBar() {
        mContentView.findViewById(R.id.discoveryProgressBar).setVisibility(View.VISIBLE);
        //(R.id.discoveryProgressBar).setVisibility(View.GONE);
    }


    public static String getDeviceStatus(int statusCode) {//Move...
        switch (statusCode) {
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown - " + statusCode;
        }
    }
}

