package com.example.gearoid.testchatapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.gearoid.testchatapp.singletons.Player;

import java.util.List;

/**
 * Created by gearoid on 25/03/15.
 */
public class PlayerListViewAdapter extends ArrayAdapter<Player> {

    int resource;
    private List<Player> items;
    Context context;

    /**
     * @param context
     * @param textViewResourceId
     * @param players
     */
    public PlayerListViewAdapter(Context context, int resource, int textViewResourceId,
                             List<Player> players) {
        super(context, resource, textViewResourceId, players);
        this.items = players;
        this.context = context;
        this.resource = resource;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            v = inflater.inflate(resource, parent, false);
        }
        Player player = items.get(position);
        if (player != null) {
            TextView top = (TextView) v.findViewById(android.R.id.text1);

            if (top != null) {
                top.setText((position + 1) + " - " + player.userName );
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

