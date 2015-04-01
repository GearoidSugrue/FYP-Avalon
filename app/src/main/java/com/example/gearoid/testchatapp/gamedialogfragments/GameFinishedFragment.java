package com.example.gearoid.testchatapp.gamedialogfragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gearoid.testchatapp.PlayerListViewAdapter;
import com.example.gearoid.testchatapp.R;
import com.example.gearoid.testchatapp.multiplayer.PlayerBasic;
import com.example.gearoid.testchatapp.multiplayer.Session;
import com.example.gearoid.testchatapp.singletons.Player;

import java.util.ArrayList;

/**
 * Created by gearoid on 30/03/15.
 */
public class GameFinishedFragment extends DialogFragment {


    View mContentView = null;
    int[] victoriousPlayerPos;
    int[] defeatedPlayerPos;
    ListView victoriousPlayersView;
    ListView defeatedPlayersView;
    PlayerListViewAdapter adapterVictoriousPlayers;
    PlayerListViewAdapter adapterDefeatedPlayers;
    ArrayList<PlayerBasic> victoriousPlayersArray;
    ArrayList<PlayerBasic> defeatedPlayersArray;
    boolean gameResult;


    public static GameFinishedFragment newInstance(boolean gameResult, int[] victoriousPlayersPositions, int[] defeatedPlayersPositions) {
        Log.d("GameFinishedFragment", "Creating instance of a GameFinishedFragment");

        GameFinishedFragment frag = new GameFinishedFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putBoolean("GAME_RESULT", gameResult);
        args.putIntArray("VICTORIOUS_PLAYER_POS", victoriousPlayersPositions);
        args.putIntArray("DEFEATED_PLAYER_POS", defeatedPlayersPositions);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("GameFinishedFragment", "onCreate called");

        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);

        Bundle extras = getArguments();
        gameResult = extras.getBoolean("GAME_RESULT");
        victoriousPlayerPos = extras.getIntArray("VICTORIOUS_PLAYER_POS");
        defeatedPlayerPos = extras.getIntArray("DEFEATED_PLAYER_POS");

        victoriousPlayersArray = new ArrayList<>();
        defeatedPlayersArray = new ArrayList<>();

        for (int i = 0; i < victoriousPlayerPos.length; i++) {
            Log.d("GameFinishedFragment", "adding player to adapterVictoriousPlayers. int[" + i + "] = " + victoriousPlayerPos[i]);
            victoriousPlayersArray.add(Session.allPlayersBasic.get(victoriousPlayerPos[i]));
        }

        for (int i = 0; i < defeatedPlayerPos.length; i++) {
            Log.d("GameFinishedFragment", "adding player to adapterDefeatedPlayers. int[" + i + "] = " + defeatedPlayerPos[i]);
            defeatedPlayersArray.add(Session.allPlayersBasic.get(defeatedPlayerPos[i]));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.game_finished, container, false);
        Log.d("GameFinishedFragment", "onCreateView called");

        mContentView = rootView;

        Toolbar mActionBarToolbar = (Toolbar) rootView.findViewById(R.id.frag_gameFinished_toolbar);
        mActionBarToolbar.setTitle("Game Finished");

        if(gameResult){
            mActionBarToolbar.setLogo(getResources().getDrawable(R.drawable.icon_blueloyaltycard));
        } else {
            mActionBarToolbar.setLogo(getResources().getDrawable(R.drawable.icon_redloyaltycard));
        }

        victoriousPlayersView = (ListView) rootView.findViewById(R.id.listview_victoriousTeam);
        defeatedPlayersView = (ListView) rootView.findViewById(R.id.listview_defeatedTeam);

        if (!gameResult) {
            TextView voteResult = (TextView) rootView.findViewById(R.id.textView_gameResult);
            voteResult.setText("Minion's of Mordred Win");
            voteResult.setTextColor(getResources().getColor(R.color.WineRedLight));
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("GameFinishedFragment", "onCreateView called");

        initializeButtons();

        adapterVictoriousPlayers = new PlayerListViewAdapter(getActivity(), R.layout.row_players, android.R.id.text1, victoriousPlayersArray);
        victoriousPlayersView.setAdapter(adapterVictoriousPlayers);

        adapterDefeatedPlayers = new PlayerListViewAdapter(getActivity(), R.layout.row_players, android.R.id.text1, defeatedPlayersArray);
        defeatedPlayersView.setAdapter(adapterDefeatedPlayers);

        int approvedPlayerCount = adapterVictoriousPlayers.getCount();
        int rejectPlayerCount = adapterDefeatedPlayers.getCount();

        if (approvedPlayerCount > 3) {

            View item = adapterVictoriousPlayers.getView(0, null, victoriousPlayersView);
            item.measure(0, 0);
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (3.5 * item.getMeasuredHeight()));
            victoriousPlayersView.setLayoutParams(params);
        } else if (rejectPlayerCount > 3) {

            View item = adapterDefeatedPlayers.getView(0, null, defeatedPlayersView);
            item.measure(0, 0);
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (3.5 * item.getMeasuredHeight()));
            defeatedPlayersView.setLayoutParams(params);
        }
    }

    public void initializeButtons() {

        Button quit = (Button) mContentView.findViewById(R.id.button_quitGame);
        Button playAgain = (Button) mContentView.findViewById(R.id.button_playAgain);

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GameFinishedDialogListener activity = (GameFinishedDialogListener) getActivity();
                activity.onGameFinishedSelection(false);
                closeDialog();
            }
        });

        playAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GameFinishedDialogListener activity = (GameFinishedDialogListener) getActivity();
                activity.onGameFinishedSelection(true);
                closeDialog();
            }
        });
    }

    public void closeDialog() {
        this.dismiss();
    }

    public interface GameFinishedDialogListener {
        void onGameFinishedSelection(boolean playAgain);
    }
}

