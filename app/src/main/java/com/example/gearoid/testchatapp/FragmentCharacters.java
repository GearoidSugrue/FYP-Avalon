package com.example.gearoid.testchatapp;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gearoid.testchatapp.character.ICharacter;
import com.example.gearoid.testchatapp.wifidirect.peerdiscovery.WiFiDirectActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gearoid on 17/03/15.
 */
public class FragmentCharacters extends ListFragment {

    View mContentView = null;
    private  List<ICharacter> characterList =  new ArrayList<ICharacter>();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.setListAdapter(new CharacterListAdapter(getActivity(), android.R.layout.simple_list_item_2, characterList));

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int position, long id) {
                //TODO Do stuff...
                ICharacter character = (ICharacter) getListAdapter().getItem(position);
                //((DeviceActionListener) getActivity()).showDetails(device);
                Log.d("Fragment Characters", "Character long clicked:" + character.getCharacterName());

                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.character_list, null);
        return mContentView;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ICharacter character = (ICharacter) getListAdapter().getItem(position);
        //((DeviceActionListener) getActivity()).showDetails(device);
        Log.d("Fragment Characters", "Character clicked:" + character.getCharacterName());

    }

    /**
     * Array adapter for ListFragment that maintains Character list.
     */
    private class CharacterListAdapter extends ArrayAdapter<ICharacter> {

        private List<ICharacter> items;

        /**
         * @param context
         * @param textViewResourceId
         * @param characters
         */
        public CharacterListAdapter(Context context, int textViewResourceId,
                                   List<ICharacter> characters) {
            super(context, textViewResourceId, characters);
            this.items = characters;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(android.R.layout.simple_list_item_2, null);
            }
            ICharacter character = items.get(position);
            if (character != null) {
                TextView top = (TextView) v.findViewById(android.R.id.text1);
                TextView bottom = (TextView) v.findViewById(android.R.id.text2);

                if(top != null){
                    top.setText(character.getCharacterName());
                }
                if(bottom != null){
                    //TODO fix this. Also add a character description in ICharacter
                    bottom.setText(character.getCharacterName());
                }
            }
            return v;
        }
    }


}
