package com.example.gearoid.testchatapp.wifidirect.servicediscovery;

import android.app.ListFragment;

import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gearoid.testchatapp.utils.ApplicationContext;
import com.example.gearoid.testchatapp.R;
import com.example.gearoid.testchatapp.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gearoid on 27/02/15.
 */
/**
 * A ListFragment that shows the available services as published by the
 * peers
 */
public class WiFiDirectServicesList extends ListFragment implements WifiP2pManager.PeerListListener {
    WiFiDevicesAdapter listAdapter = null;
    private List<WiFiP2pService> peers = new ArrayList<WiFiP2pService>();
    private View mContentView = null;
    private WifiP2pDevice myDevice;

    public TextView otherDeviceStatusText;

    private List<WifiP2pDevice> allNearbyPeers = new ArrayList<WifiP2pDevice>();


    interface DeviceClickListener {
        public void connectP2p(WiFiP2pService wifiP2pService);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.devices_list, null);

        return mContentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listAdapter = new WiFiDevicesAdapter(getActivity(), android.R.layout.simple_list_item_2, android.R.id.text1,
                peers);
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
                otherDeviceStatusText = ((TextView) v.findViewById(android.R.id.text2));
                otherDeviceStatusText.setText("Connecting");
            }
        } else {
            ((DeviceClickListener) getActivity()).connectP2p(selectedService);
            otherDeviceStatusText = ((TextView) v.findViewById(android.R.id.text2));
            otherDeviceStatusText.setText("Connecting");
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
                    nameText.setText(service.device.deviceName);
                }
                TextView statusText = (TextView) v.findViewById(android.R.id.text2);

                if (statusText != null) {
                    statusText.setText(getDeviceStatus(service.device.status));
                }

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

    public int checkIfDeviceAlreadyAdded(String deviceAddress, String deviceName) {//Not sure if working
        Log.d("WiFiDirectServiceList", "Checking if device has already been added to list");

        for (int j = 0; j < peers.size(); j++) {
            Log.d("WiFiDirectServiceList", peers.get(j).device.deviceName + ", " + peers.get(j).device.deviceAddress);

            if (peers.get(j).device.deviceAddress.equalsIgnoreCase(deviceAddress) && peers.get(j).device.deviceName.equals(deviceName)) {
                Log.d("WiFiDirectServiceList", "Found a match: " + peers.get(j).device.deviceName);
                return j;
            }
        }
        return -1;
    }

    public void replaceExistingService(int positionToReplace, WiFiP2pService newService) {
        peers.remove(positionToReplace);
        peers.add(positionToReplace, newService);
        ((WiFiDevicesAdapter) getListAdapter()).notifyDataSetChanged();
    }


    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        Log.d("WiFiDirectServiceList", "onPeersAvailable called...");

        allNearbyPeers.clear();
        allNearbyPeers.addAll(peerList.getDeviceList());

        if (allNearbyPeers.size() == 0) {
            Log.d("WiFiDirectServiceList", "No devices found");
            return;
        }
        //updatePeerDetails();
        Log.d("WiFiDirectServiceList", "Found devices: " + allNearbyPeers.size());
    }

    public void clearPeers() {
        peers.clear();
        ((WiFiDevicesAdapter) getListAdapter()).notifyDataSetChanged();
    }

    public void clearNonConnectedPeers() {

        for (int i = 0; i < peers.size(); i++) {
            WiFiP2pService p = peers.get(i);
            if (p.device.status != WifiP2pDevice.CONNECTED || myDevice.status != WifiP2pDevice.CONNECTED) {
                Log.d("WiFiDirectServiceList", "Device is not connected. Removing from list." + p.device.deviceName);
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
            Log.d("WiFiDirectServiceList", "updateThisDevice: " + device.deviceName + " = " + getDeviceStatus(device.status));
            this.myDevice = device;
            friendlyNameView.setText(SharedPrefManager.getStringDefaults("USERNAME", ApplicationContext.getContext()));
            nameView.setText(device.deviceName);

            if (SharedPrefManager.getBooleanDefaults("HOST", ApplicationContext.getContext())) {
                TextView label_status = (TextView) mContentView.findViewById(R.id.textview_label_status);
                label_status.setVisibility(View.GONE);
                statusView.setVisibility(View.GONE);

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

    public void setOtherDeviceStatusToConnected(){
        Log.d("WiFiDirectServiceList", "setOtherDeviceStatusToConnected: ");

        if(otherDeviceStatusText != null){
            otherDeviceStatusText.setText("Connected");
        }
    }


    public void setColorsToBlue(){
        TextView view = (TextView)  mContentView.findViewById(R.id.textview_label_myDevice);
        view.setBackgroundColor(getResources().getColor(R.color.SlateBlue));

        TextView view2 = (TextView)  mContentView.findViewById(R.id.finding_text);
        view2.setBackgroundColor(getResources().getColor(R.color.SlateBlue));

    }

    public void setFindingTextView(String text) {
        TextView view = (TextView) mContentView.findViewById(R.id.finding_text);
        view.setText(text);
    }

    public void dismissProgressBar() {
        mContentView.findViewById(R.id.discoveryProgressBar).setVisibility(View.GONE);
    }

    public void showProgressBar() {
        mContentView.findViewById(R.id.discoveryProgressBar).setVisibility(View.VISIBLE);
    }

    public static String getDeviceStatus(int statusCode) {
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

