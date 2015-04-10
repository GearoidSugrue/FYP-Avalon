package com.example.gearoid.testchatapp.game.gamedialogfragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.example.gearoid.testchatapp.game.DrawableFactory;
import com.example.gearoid.testchatapp.PlayerListViewAdapter;
import com.example.gearoid.testchatapp.R;
import com.example.gearoid.testchatapp.character.good.Merlin;
import com.example.gearoid.testchatapp.multiplayer.Player;
import com.example.gearoid.testchatapp.multiplayer.Session;

import java.util.ArrayList;

/**
 * Created by gearoid on 29/03/15.
 */
public class AssassinateFragment extends DialogFragment {

    //Constants
    public static final String TAG = "AssassinateFragment";
    public static final String TOOLBAR_TITLE = "Assassinate Merlin";

    View mContentView = null;
    ListView chosenPlayerView;
    ListView candidatePlayersView;
    PlayerListViewAdapter adapterChosenPlayer;
    PlayerListViewAdapter adapterCandidatePlayers;
    ArrayList<Player> chosenPlayerArray;
    ArrayList<Player> candidatePlayersArray;
    boolean isFinished = false;
    boolean isSuccess = false;

    public static AssassinateFragment newInstance() {
        Log.d(TAG, "Creating instance of a AssassinateFragment");

        return new AssassinateFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");

        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);

        chosenPlayerArray = new ArrayList<>();

        candidatePlayersArray = new ArrayList<>();
        candidatePlayersArray.addAll(Session.allPlayers);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.assassin_endgame, container, false);
        Log.d(TAG, "onCreateView called");

        mContentView = rootView;

        Toolbar mActionBarToolbar = (Toolbar) rootView.findViewById(R.id.frag_assassin_toolbar);
        mActionBarToolbar.setTitle(TOOLBAR_TITLE);
        mActionBarToolbar.setLogo(getResources().getDrawable(R.drawable.icon_assassin));

        chosenPlayerView = (ListView) rootView.findViewById(R.id.listview_chosenPlayerToAssassinate);
        candidatePlayersView = (ListView) rootView.findViewById(R.id.listview_assassinCandidates);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onCreateView called");

        initializeButtons();

        adapterChosenPlayer = new PlayerListViewAdapter(getActivity(), R.layout.row_players, android.R.id.text1, chosenPlayerArray);
        chosenPlayerView.setAdapter(adapterChosenPlayer);

        adapterCandidatePlayers = new PlayerListViewAdapter(getActivity(), R.layout.row_players, android.R.id.text1, candidatePlayersArray);
        candidatePlayersView.setAdapter(adapterCandidatePlayers);

        setListViewOnclickListeners();
        setImageViewOnClickListeners();

        if (adapterCandidatePlayers.getCount() > 3) {
            View item = adapterCandidatePlayers.getView(0, null, candidatePlayersView);
            item.measure(0, 0);
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (3.5 * item.getMeasuredHeight()));
            candidatePlayersView.setLayoutParams(params);
        }

    }

    public void setListViewOnclickListeners() {

        candidatePlayersView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Player player = (Player) parent.getItemAtPosition(position);
                Log.d(TAG, "Player clicked:" + player.userName);

                if(!isFinished) {
                    adapterChosenPlayer.clear();
                    adapterChosenPlayer.add(player);
                    final ImageView chosenPlayer = (ImageView) mContentView.findViewById(R.id.imageView_assassinChosenPlayer);
                    chosenPlayer.setVisibility(View.VISIBLE);
                }
            }
        });

        chosenPlayerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Player player = (Player) parent.getItemAtPosition(position);
                Log.d(TAG, "Player clicked:" + player.userName);

                if(!isFinished) {
                    adapterChosenPlayer.remove(player);
                    final ImageView chosenPlayer = (ImageView) mContentView.findViewById(R.id.imageView_assassinChosenPlayer);
                    chosenPlayer.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void setImageViewOnClickListeners() {
        final ImageView chosenPlayer = (ImageView) mContentView.findViewById(R.id.imageView_assassinChosenPlayer);

        chosenPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (adapterChosenPlayer.getCount() == 1) {

                    assassinatePlayer();
                }
            }
        });

    }

    public void initializeButtons() {

        Button finishGame = (Button) mContentView.findViewById(R.id.button_assassinFinishGame);

        finishGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isFinished) {
                    AssassinateDialogListener activity = (AssassinateDialogListener) getActivity();
                    activity.onAssassination(isSuccess);
                    closeDialog();
                }
            }
        });

    }

    public void assassinatePlayer() {

        isFinished = true;
        TextView result = (TextView) mContentView.findViewById(R.id.textView_assassinResult);
        Player player = adapterChosenPlayer.getItem(0);

        if (player.character instanceof Merlin) {
            isSuccess = true;
            result.setText(getActivity().getString(R.string.assassinationSuccessful));
        } else {
            isSuccess = false;
        }

        result.setVisibility(View.VISIBLE);

        final ImageView chosenPlayer = (ImageView) mContentView.findViewById(R.id.imageView_assassinChosenPlayer);
        Log.d(TAG, "Trying to get drawable for player: " +  player.userName);
        Log.d(TAG, "Trying to get drawable for character: " +  player.character.getCharacterName());

        chosenPlayer.setImageDrawable(DrawableFactory.getDrawable(getResources(), player.character.getCharacterName()));

        Button finishGame = (Button) mContentView.findViewById(R.id.button_assassinFinishGame);
        finishGame.setVisibility(View.VISIBLE);


        View item = adapterCandidatePlayers.getView(0, null, candidatePlayersView);
        item.measure(0, 0);

        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (3.5 * item.getMeasuredHeight()));
        result.setLayoutParams(params);

        candidatePlayersView.setVisibility(View.GONE);

    }


    public void closeDialog() {
        this.dismiss();
    }

    public interface AssassinateDialogListener {
        void onAssassination(boolean isSuccess);
    }
}



