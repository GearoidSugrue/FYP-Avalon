package com.example.gearoid.testchatapp.game.gamedialogfragments;

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
import com.example.gearoid.testchatapp.multiplayer.Player;
import com.example.gearoid.testchatapp.multiplayer.Session;

import java.util.ArrayList;

/**
 * Created by gearoid on 30/03/15.
 */
public class TeamVoteResultFragment extends DialogFragment {

    View mContentView = null;
    int[] playerApprovePos;
    int[] playerRejectPos;
    ListView approvedPlayersView;
    ListView rejectedPlayersView;
    PlayerListViewAdapter adapterApprovedPlayers;
    PlayerListViewAdapter adapterRejectedPlayers;
    ArrayList<Player> approvedPlayersArray;
    ArrayList<Player> rejectedPlayersArray;
    int questNumber;
    int voteNumber;
    boolean isAccepted;


    public static TeamVoteResultFragment newInstance(boolean isAccepted, int[] playerApprovePositions, int[] playerRejectPositions, int questNumber, int voteNumber) {
        TeamVoteResultFragment frag = new TeamVoteResultFragment();

        Bundle args = new Bundle();
        args.putBoolean("IS_ACCEPTED", isAccepted);
        args.putIntArray("PLAYER_APPROVE_POS", playerApprovePositions);
        args.putIntArray("PLAYER_REJECT_POS", playerRejectPositions);
        args.putInt("QUEST_NUM", questNumber);
        args.putInt("VOTE_NUM", voteNumber);
        frag.setArguments(args);
        Log.d("TeamVoteResultFragment", "Creating instance of a teamvoteResult fragment");
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("TeamVoteResultFragment", "onCreate called");
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);

        Bundle extras = getArguments();
        isAccepted = extras.getBoolean("IS_ACCEPTED");
        playerApprovePos = extras.getIntArray("PLAYER_APPROVE_POS");
        playerRejectPos = extras.getIntArray("PLAYER_REJECT_POS");
        questNumber = extras.getInt("QUEST_NUM");
        voteNumber = extras.getInt("VOTE_NUM");

        approvedPlayersArray = new ArrayList<>();
        rejectedPlayersArray = new ArrayList<>();

        for (int i = 0; i < playerApprovePos.length; i++) {
            Log.d("TeamVoteResultFragment", "adding player to adapterVictoriousPlayers. int[" + i + "] = " + playerApprovePos[i]);
            approvedPlayersArray.add(Session.allPlayers.get(playerApprovePos[i]));
        }

        for (int i = 0; i < playerRejectPos.length; i++) {
            Log.d("TeamVoteResultFragment", "adding player to adapterDefeatedPlayers. int[" + i + "] = " + playerRejectPos[i]);
            rejectedPlayersArray.add(Session.allPlayers.get(playerRejectPos[i]));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.team_vote_result, container, false);
        Log.d("TeamVoteResultFragment", "onCreateView called");

        mContentView = rootView;

        Toolbar mActionBarToolbar = (Toolbar) rootView.findViewById(R.id.frag_teamVoteResult_toolbar);
        mActionBarToolbar.setTitle("Quest " + questNumber + " - Vote " + voteNumber + " Result");
        mActionBarToolbar.setLogo(getResources().getDrawable(R.drawable.icon_votetoken));

        approvedPlayersView = (ListView) rootView.findViewById(R.id.listview_votedApprove);
        rejectedPlayersView = (ListView) rootView.findViewById(R.id.listview_votedReject);

        if (!isAccepted) {
            TextView voteResult = (TextView) rootView.findViewById(R.id.textView_voteResult);
            voteResult.setText("Team Rejected");
            voteResult.setTextColor(getResources().getColor(R.color.WineRedLight));
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("TeamVoteResultFragment", "onCreateView called");

        initializeButtons();

        adapterApprovedPlayers = new PlayerListViewAdapter(getActivity(), R.layout.row_players, android.R.id.text1, approvedPlayersArray);
        approvedPlayersView.setAdapter(adapterApprovedPlayers);

        adapterRejectedPlayers = new PlayerListViewAdapter(getActivity(), R.layout.row_players, android.R.id.text1, rejectedPlayersArray);
        rejectedPlayersView.setAdapter(adapterRejectedPlayers);

        int approvedPlayerCount = adapterApprovedPlayers.getCount();
        int rejectPlayerCount = adapterRejectedPlayers.getCount();

        if (approvedPlayerCount > 3) {

            View item = adapterApprovedPlayers.getView(0, null, approvedPlayersView);
            item.measure(0, 0);
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (3.5 * item.getMeasuredHeight()));
            approvedPlayersView.setLayoutParams(params);
        } else if(approvedPlayerCount == 0){
            View item = adapterApprovedPlayers.getView(0, null, approvedPlayersView);
            item.measure(0, 0);
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (item.getMeasuredHeight()));
            approvedPlayersView.setLayoutParams(params);
        }
        if (rejectPlayerCount > 3) {

            View item = adapterRejectedPlayers.getView(0, null, rejectedPlayersView);
            item.measure(0, 0);
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (3.5 * item.getMeasuredHeight()));
            rejectedPlayersView.setLayoutParams(params);
        } else if(rejectPlayerCount == 0){
            View item = adapterRejectedPlayers.getView(0, null, rejectedPlayersView);
            item.measure(0, 0);
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (item.getMeasuredHeight()));
            rejectedPlayersView.setLayoutParams(params);
        }


    }

    public void initializeButtons() {

        Button dismiss = (Button) mContentView.findViewById(R.id.button_dismissTeamVoteResult);

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                closeDialog();
            }
        });

    }

    public void closeDialog() {
        this.dismiss();
    }
}
