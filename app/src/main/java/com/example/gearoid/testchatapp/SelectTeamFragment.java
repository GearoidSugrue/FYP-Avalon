package com.example.gearoid.testchatapp;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gearoid.testchatapp.multiplayer.Session;
import com.example.gearoid.testchatapp.singletons.Player;

import java.util.ArrayList;

/**
 * Created by gearoid on 29/03/15.
 */
public class SelectTeamFragment extends DialogFragment {

    View mContentView = null;
    ListView chosenTeamView;
    ListView candidatePlayersView;
    PlayerListViewAdapter adapterChosenTeam;
    PlayerListViewAdapter adapterCandidatePlayers;
    ArrayList<Player> chosenTeamArray;
    ArrayList<Player> candidatePlayersArray;
    int teamSize;
    int questNumber;

    static SelectTeamFragment newInstance(int teamSize, int questNumber) {

        Log.d("SelectTeamFragment", "Creating instance of a SelectTeamFragment fragment");

        SelectTeamFragment frag = new SelectTeamFragment();

        Bundle args = new Bundle();
        //args.putSerializable("BOARD", currentBoard);
        args.putInt("TEAM_SIZE", teamSize);
        args.putInt("QUEST_NUM", questNumber);
        frag.setArguments(args);

        return frag;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SelectTeamFragment", "onCreate called");

        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);

        Bundle extras = getArguments();
        teamSize = extras.getInt("TEAM_SIZE");
        questNumber = extras.getInt("QUEST_NUM");

        chosenTeamArray = new ArrayList<>();

        candidatePlayersArray = new ArrayList<>();
        candidatePlayersArray.addAll(Session.masterAllPlayers);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.select_team, container, false);
        Log.d("SelectTeamFragment", "onCreateView called");

        mContentView = rootView;

        Toolbar mActionBarToolbar = (Toolbar) rootView.findViewById(R.id.frag_selectTeam_toolbar);
        mActionBarToolbar.setTitle("Quest " + questNumber + " - Team Select");
        mActionBarToolbar.setLogo(getResources().getDrawable(R.drawable.icon_leader));

        TextView chosenTeamLabel = (TextView) rootView.findViewById(R.id.textView_chosenTeamLabel);
        chosenTeamLabel.setText("Your Chosen Team (" + teamSize + " Members)");

        chosenTeamView = (ListView) rootView.findViewById(R.id.listview_chosenTeam);
        candidatePlayersView = (ListView) rootView.findViewById(R.id.listview_teamCandidates);

        // randomiseCardOrder();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("SelectTeamFragment", "onCreateView called");

        initializeButtons();

        adapterChosenTeam = new PlayerListViewAdapter(getActivity(), R.layout.row_players, android.R.id.text1, chosenTeamArray);
        chosenTeamView.setAdapter(adapterChosenTeam);

        adapterCandidatePlayers = new PlayerListViewAdapter(getActivity(), R.layout.row_players, android.R.id.text1, candidatePlayersArray);
        candidatePlayersView.setAdapter(adapterCandidatePlayers);

        setListViewOnclickListeners();

        //int deviceConfig = getActivity().getResources().getConfiguration().orientation;

        if (teamSize < 4) {
            View item = adapterCandidatePlayers.getView(0, null, candidatePlayersView);
            item.measure(0, 0);
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (teamSize * item.getMeasuredHeight()));
            chosenTeamView.setLayoutParams(params);
        } else {
            View item = adapterCandidatePlayers.getView(0, null, candidatePlayersView);
            item.measure(0, 0);
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (3.5 * item.getMeasuredHeight()));
            chosenTeamView.setLayoutParams(params);
        }

    }

    public void setListViewOnclickListeners() {

        candidatePlayersView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Player player = (Player) parent.getItemAtPosition(position);
                Log.d("SelectTeam Fragment", "Player clicked:" + player.userName);

                if(adapterChosenTeam.getCount() < teamSize){
                    adapterChosenTeam.add(player);
                    adapterCandidatePlayers.remove(player);
                } else {
                    ApplicationContext.showToast("Team Full. Tap To Remove Members.");
                }
            }
        });

        chosenTeamView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Player player = (Player) parent.getItemAtPosition(position);
                Log.d("SelectTeam Fragment", "Player clicked:" + player.userName);

                adapterChosenTeam.remove(player);
                adapterCandidatePlayers.add(player);
            }
        });
    }

    public void initializeButtons() {

        Button dismiss = (Button) mContentView.findViewById(R.id.button_dismissTeamSelect);
        Button confirm = (Button) mContentView.findViewById(R.id.button_confirmTeamSelect);

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDialog();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int chosenTeamSize = adapterChosenTeam.getCount();

                if(chosenTeamSize == teamSize){
                    int[] chosenTeamArr = new int[chosenTeamSize];

                    for(int i=0; i < chosenTeamSize; i++){
                        chosenTeamArr[i] = adapterChosenTeam.getItem(i).playerID;
                        Log.d("SelectTeam Fragment", "Chosen Player " + i + ": " + chosenTeamArr[i]);
                    }

                    TeamSelectDialogListener activity = (TeamSelectDialogListener) getActivity();
                    activity.onTeamSelected(chosenTeamArr);

                    closeDialog();
                } else {
                    ApplicationContext.showToast("Team Not Full!");
                }
            }
        });
    }

    public void closeDialog() {
        this.dismiss();
    }

    public interface TeamSelectDialogListener {
        void onTeamSelected(int[] teamIndexes);
    }
}
