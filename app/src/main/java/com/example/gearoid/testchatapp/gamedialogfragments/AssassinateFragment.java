package com.example.gearoid.testchatapp.gamedialogfragments;

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

import com.example.gearoid.testchatapp.DrawableFactory;
import com.example.gearoid.testchatapp.PlayerListViewAdapter;
import com.example.gearoid.testchatapp.R;
import com.example.gearoid.testchatapp.character.Merlin;
import com.example.gearoid.testchatapp.multiplayer.Session;
import com.example.gearoid.testchatapp.singletons.Player;

import java.util.ArrayList;

/**
 * Created by gearoid on 29/03/15.
 */
public class AssassinateFragment extends DialogFragment {


    View mContentView = null;
    ListView chosenPlayerView;
    ListView candidatePlayersView;
    PlayerListViewAdapter adapterChosenPlayer;
    PlayerListViewAdapter adapterCandidatePlayers;
    ArrayList<Player> chosenPlayerArray;
    ArrayList<Player> candidatePlayersArray;
    boolean isFinished = false;
    boolean isSucess = false;

    public static AssassinateFragment newInstance() {

        Log.d("AssassinateFragment", "Creating instance of a SelectTeamFragment fragment");

        AssassinateFragment frag = new AssassinateFragment();

//        Bundle args = new Bundle();
        //args.putSerializable("BOARD", currentBoard);
//        args.putInt("TEAM_SIZE", teamSize);
//        args.putInt("QUEST_NUM", questNumber);
//        frag.setArguments(args);

        return frag;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("AssassinateFragment", "onCreate called");

        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);

//        Bundle extras = getArguments();
//        teamSize = extras.getInt("TEAM_SIZE");
//        questNumber = extras.getInt("QUEST_NUM");

        chosenPlayerArray = new ArrayList<>();

        candidatePlayersArray = new ArrayList<>();
        candidatePlayersArray.addAll(Session.masterAllPlayers);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.assassin_endgame, container, false);
        Log.d("AssassinateFragment", "onCreateView called");

        mContentView = rootView;

        Toolbar mActionBarToolbar = (Toolbar) rootView.findViewById(R.id.frag_assassin_toolbar);
        mActionBarToolbar.setTitle("Assassinate Merlin");
        mActionBarToolbar.setLogo(getResources().getDrawable(R.drawable.icon_assassin));

        chosenPlayerView = (ListView) rootView.findViewById(R.id.listview_chosenPlayerToAssassinate);
        candidatePlayersView = (ListView) rootView.findViewById(R.id.listview_assassinCandidates);

        // randomiseCardOrder();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("AssassinateFragment", "onCreateView called");

        initializeButtons();

        adapterChosenPlayer = new PlayerListViewAdapter(getActivity(), R.layout.row_players, android.R.id.text1, chosenPlayerArray);
        chosenPlayerView.setAdapter(adapterChosenPlayer);

        adapterCandidatePlayers = new PlayerListViewAdapter(getActivity(), R.layout.row_players, android.R.id.text1, candidatePlayersArray);
        candidatePlayersView.setAdapter(adapterCandidatePlayers);

        setListViewOnclickListeners();
        setImageViewOnClickListeners();

        //int deviceConfig = getActivity().getResources().getConfiguration().orientation;

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
                Log.d("AssassinateFragment", "Player clicked:" + player.userName);

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
                Log.d("AssassinateFragment", "Player clicked:" + player.userName);

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
                    activity.onAssassination(isSucess);
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
            isSucess = true;
            result.setText("Assassination Successful");
        } else {
            isSucess = false;
        }

        result.setVisibility(View.VISIBLE);

        final ImageView chosenPlayer = (ImageView) mContentView.findViewById(R.id.imageView_assassinChosenPlayer);
        chosenPlayer.setImageDrawable(DrawableFactory.getDrawable(getResources(), player.character.getCharacterName()));

        Button finishGame = (Button) mContentView.findViewById(R.id.button_assassinFinishGame);
        finishGame.setVisibility(View.VISIBLE);


        View item = adapterCandidatePlayers.getView(0, null, candidatePlayersView);
        item.measure(0, 0);

        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (3.5 * item.getMeasuredHeight()));
        result.setLayoutParams(params);

        candidatePlayersView.setVisibility(View.GONE);
//
//        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (3.5 * item.getMeasuredHeight()));
//        visiblePlayersView.setLayoutParams(params);
    }


    public void closeDialog() {
        this.dismiss();
    }

    public interface AssassinateDialogListener {
        void onAssassination(boolean isSuccess);
    }
}



