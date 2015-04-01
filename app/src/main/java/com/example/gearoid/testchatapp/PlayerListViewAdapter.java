package com.example.gearoid.testchatapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gearoid.testchatapp.multiplayer.PlayerBasic;
import com.example.gearoid.testchatapp.singletons.Player;

import java.util.List;

/**
 * Created by gearoid on 25/03/15.
 */
public class PlayerListViewAdapter extends ArrayAdapter<PlayerBasic> {

    int resource;
    private List<PlayerBasic> items;
    Context context;

    /**
     * @param context
     * @param textViewResourceId
     * @param players
     */
    public PlayerListViewAdapter(Context context, int resource, int textViewResourceId,
                                 List<PlayerBasic> players) {
        super(context, resource, textViewResourceId, players);
        this.items = players;
        this.context = context;
        this.resource = resource;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            v = inflater.inflate(R.layout.row_players, null);

//            LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(
//                    Context.LAYOUT_INFLATER_SERVICE);
//            v = vi.inflate(R.layout.row_devices, null);
        }
        if (items.size() > 0) {
            PlayerBasic player = items.get(position);
            if (player != null) {
                TextView top = (TextView) v.findViewById(R.id.textview_playerName);

                if (player.isLeader) { //TODO check if else statement is needed to change view back
                    ImageView leader = (ImageView) v.findViewById(R.id.icon_leader);
                    leader.setVisibility(View.VISIBLE);
                } else {
                    ImageView leader = (ImageView) v.findViewById(R.id.icon_leader);
                    leader.setVisibility(View.INVISIBLE);
                }
                if (player.hasLadyOfLake) {
                    ImageView lady = (ImageView) v.findViewById(R.id.icon_ladyOfLake);
                    lady.setVisibility(View.VISIBLE);
                } else {
                    ImageView lady = (ImageView) v.findViewById(R.id.icon_ladyOfLake);
                    lady.setVisibility(View.GONE);
                }
                if (player.isOnQuest) {
                    ImageView questMember = (ImageView) v.findViewById(R.id.icon_questMember);
                    questMember.setVisibility(View.VISIBLE);
                } else {
                    ImageView questMember = (ImageView) v.findViewById(R.id.icon_questMember);
                    questMember.setVisibility(View.GONE);
                }
                if (top != null) {
                    top.setText(player.userName);
                    //top.setTextColor(Color.BLACK);
                }
            }
//                        v.setBackground(getResources().getDrawable(R.drawable.misc_redloyaltystrip));
//                        v.getBackground().setAlpha(150);
//                        v.getBackground().setColorFilter(Color.argb(70, 255, 0, 0), PorterDuff.Mode.DARKEN);
//

        }
        //v.setBackgroundColor(Color.argb(255, 255, 192, 192));
        return v;
    }

//    public interface PlayerListListener {
//
//        void playerSelected(int position, boolean isOptionalList, PlayerListViewAdapter listAdapter);
//    }
}

