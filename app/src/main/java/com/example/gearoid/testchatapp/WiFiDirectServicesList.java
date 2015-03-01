package com.example.gearoid.testchatapp;

import android.app.ListFragment;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
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
public class WiFiDirectServicesList extends ListFragment {
    WiFiDevicesAdapter listAdapter = null;
    private View mContentView = null;

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
                new ArrayList<WiFiP2pService>()); //R.layout.row_devices
        this.setListAdapter(listAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ((DeviceClickListener) getActivity()).connectP2p((WiFiP2pService) l.getItemAtPosition(position));
        ((TextView) v.findViewById(android.R.id.text2)).setText("Connecting");
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
                    nameText.setText(service.device.deviceName + service.instanceName);
                }
                TextView statusText = (TextView) v.findViewById(android.R.id.text2);
                statusText.setText(getDeviceStatus(service.device.status));
            }
            return v;
        }

    }

    public void updateThisDevice(WifiP2pDevice device){

        TextView view = (TextView) mContentView.findViewById(R.id.my_device_name);
        view.setText(device.deviceName);
        view = (TextView) mContentView.findViewById(R.id.my_device_status);
        view.setText(getDeviceStatus(device.status));
    }

    public void setFindingTextView(String text){
        TextView view = (TextView) mContentView.findViewById(R.id.finding_text);
        view.setText(text);
    }

    public void dismissProgressBar(){
        mContentView.findViewById(R.id.discoveryProgressBar).setVisibility(View.GONE);
        //(R.id.discoveryProgressBar).setVisibility(View.GONE);
    }

    public void showProgressBar(){
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
                return "Unknown";
        }
    }
}

