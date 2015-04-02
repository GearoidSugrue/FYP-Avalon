package com.example.gearoid.testchatapp;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.gearoid.testchatapp.singletons.PlayerConnection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gearoid on 24/03/15.
 */
public class PlayerListFragment extends ListFragment {

    PlayerListAdapter listAdapter = null;
    View mContentView = null;
    private List<PlayerConnection> playerConnectionList = new ArrayList<PlayerConnection>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listAdapter = new PlayerListAdapter(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, playerConnectionList);
        setListAdapter(listAdapter);



    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {//Called after GameSetupActivity onCreate is called
        super.onActivityCreated(savedInstanceState);

//            TextView tv = (TextView) mContentView.findViewById(R.id.textview_characterListTitle);
//            tv.setTypeface(null, Typeface.BOLD);
//            tv.setTextColor(Color.BLACK);


        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v,
                                           int position, long id) {
                PlayerConnection playerConnection = (PlayerConnection) getListAdapter().getItem(position);
                Log.d("Fragment PlayerList", "Player clicked: " + playerConnection.userName);
                ((PlayerListFragListener) getActivity()).playerClicked(playerConnection);
            }
        });

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int position, long id) {
                PlayerConnection playerConnection = (PlayerConnection) getListAdapter().getItem(position);
                Log.d("Fragment PlayerList", "Player Long clicked: " + playerConnection.userName);
                ((PlayerListFragListener) getActivity()).playerLongClicked(playerConnection);

                return true;
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.player_list, null);
        //mContentView.setBackground(getResources().getDrawable(R.drawable.misc_blueloyalty));
        //mContentView.getBackground().setAlpha(120);

        return mContentView;
    }

    public class PlayerListAdapter extends ArrayAdapter<PlayerConnection> {

        private List<PlayerConnection> items;

        /**
         * @param context
         * @param textViewResourceId
         * @param playerConnections
         */
        public PlayerListAdapter(Context context, int resource, int textViewResourceId,
                                    List<PlayerConnection> playerConnections) {
            super(context, resource, textViewResourceId, playerConnections);
            this.items = playerConnections;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(android.R.layout.simple_list_item_1, null);
            }
            PlayerConnection playerConnection = items.get(position);
            if (playerConnection != null) {
                TextView top = (TextView) v.findViewById(android.R.id.text1);

                if (top != null) {
                    top.setText(playerConnection.userName);
                    //top.setTextColor(Color.BLACK);
                }
//                        v.setBackground(getResources().getDrawable(R.drawable.misc_redloyaltystrip));
//                        v.getBackground().setAlpha(150);
//                        v.getBackground().setColorFilter(Color.argb(70, 255, 0, 0), PorterDuff.Mode.DARKEN);
//

            }
            //v.setBackgroundColor(Color.argb(255, 255, 192, 192));
            return v;
        }
    }

    public interface PlayerListFragListener {

        void playerClicked(PlayerConnection playerConnectionClicked);
        void playerLongClicked(PlayerConnection playerConnectionClicked);
    }

}
