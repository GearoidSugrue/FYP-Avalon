package com.example.gearoid.testchatapp.game.gamedialogfragments;

import android.app.DialogFragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.gearoid.testchatapp.PlayerListViewAdapter;
import com.example.gearoid.testchatapp.R;
import com.example.gearoid.testchatapp.game.GameLogicFunctions;
import com.example.gearoid.testchatapp.multiplayer.Player;
import com.example.gearoid.testchatapp.multiplayer.Session;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by gearoid on 24/03/15.
 */
public class TeamVoteFragment extends DialogFragment {

    //Constants
    public static final String TAG = "TeamVoteFrag";
    public static final String PLAYER_POS = "PLAYER_POS";
    public static final String QUEST = "QUEST";
    public static final String VOTE_NUM = "VOTE_NUM";

    View mContentView = null;
    ImageView image1;
    ImageView image2;
    boolean image1Approve;
    boolean image2Approve;
    int[] playerPos;
    ListView proposedPlayerView;
    ListView currentLeaderView;
    ListView nextLeaderView;
    PlayerListViewAdapter adapterProposedTeam;
    PlayerListViewAdapter adapterCurrentLeader;
    PlayerListViewAdapter adapterNextLeader;
    ArrayList<Player> proposedPlayersArray;
    ArrayList<Player> currentLeaderArray;
    ArrayList<Player> nextLeaderArray;
    int questNumber;
    int voteCount;


    public static TeamVoteFragment newInstance(int[] playerPositions, int questNumber, int voteNumber) {
        Log.d(TAG, "Creating instance of a teamvote fragment");

        TeamVoteFragment frag = new TeamVoteFragment();

        Bundle args = new Bundle();
        args.putIntArray(PLAYER_POS, playerPositions);
        args.putInt(QUEST, questNumber);
        args.putInt(VOTE_NUM, voteNumber);
        frag.setArguments(args);
        return frag;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");

        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);

        Bundle extras = getArguments();
        playerPos = extras.getIntArray(PLAYER_POS);
        questNumber = extras.getInt(QUEST);
        voteCount = extras.getInt(VOTE_NUM);

        proposedPlayersArray = new ArrayList<>();
        currentLeaderArray = new ArrayList<>();
        nextLeaderArray = new ArrayList<>();

        for(int i=0; i < playerPos.length; i++){
            Log.d(TAG, "adding player to adapterVictoriousPlayers. int[" + i + "] = " + playerPos[i]);
            proposedPlayersArray.add(Session.allPlayers.get(playerPos[i]));
        }

        currentLeaderArray.add(Session.allPlayers.get(GameLogicFunctions.getCurrentLeaderID()));
        nextLeaderArray.add(Session.allPlayers.get(GameLogicFunctions.getNextLeaderID()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.team_vote, container, false);
        Log.d(TAG, "onCreateView called");

        Toolbar mActionBarToolbar = (Toolbar) rootView.findViewById(R.id.frag_teamVote_toolbar);
        String lastVote = "";

        if (voteCount == 5){
            lastVote = getActivity().getString(R.string.finalInBrackets);
        }

        mActionBarToolbar.setTitle("Quest " + questNumber + " - Vote " + voteCount + lastVote);
        mActionBarToolbar.setLogo(getResources().getDrawable(R.drawable.icon_votetoken));

        image1 = (ImageView) rootView.findViewById(R.id.imageView_teamVote1);
        image2 = (ImageView) rootView.findViewById(R.id.imageView_teamVote2);

        randomiseTokenOrder();
        setOnClicklisteners();

        proposedPlayerView = (ListView) rootView.findViewById(R.id.listview_proposedTeam);
        currentLeaderView = (ListView) rootView.findViewById(R.id.listview_currentLeader);
        nextLeaderView = (ListView) rootView.findViewById(R.id.listview_nextLeader);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated called");

        adapterProposedTeam = new PlayerListViewAdapter(getActivity(), R.layout.row_players, android.R.id.text1, proposedPlayersArray);
        proposedPlayerView.setAdapter(adapterProposedTeam);

        adapterCurrentLeader = new PlayerListViewAdapter(getActivity(), R.layout.row_players, android.R.id.text1, currentLeaderArray);
        currentLeaderView.setAdapter(adapterCurrentLeader);
        adapterNextLeader = new PlayerListViewAdapter(getActivity(), R.layout.row_players, android.R.id.text1, nextLeaderArray);
        nextLeaderView.setAdapter(adapterNextLeader);

        int proposedTeamCount = adapterProposedTeam.getCount();
        int deviceConfig = getActivity().getResources().getConfiguration().orientation;

        //Limits listview to display a certain amount of rows depending what the orientation of the screen is. This helps ensure parts of the fragment doesn't get pushed off screen.
        if(deviceConfig == Configuration.ORIENTATION_LANDSCAPE && proposedTeamCount > 2 ){

            View item = adapterProposedTeam.getView(0, null, proposedPlayerView);
            item.measure(0, 0);
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (1.5 * item.getMeasuredHeight()));
            proposedPlayerView.setLayoutParams(params);
        } else if(deviceConfig == Configuration.ORIENTATION_PORTRAIT && proposedTeamCount > 3) {

            View item = adapterProposedTeam.getView(0, null, proposedPlayerView);
            item.measure(0, 0);
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (3.6 * item.getMeasuredHeight()));
            proposedPlayerView.setLayoutParams(params);
        }
    }

    public void randomiseTokenOrder(){
        Log.d(TAG, "randomiseCardOrder called");

        Random randomGenerator = new Random();
        int randNum = randomGenerator.nextInt(2);
        Log.d(TAG, "randomiseCardOrder: random num = " + randNum);

        if(Math.random() > 0.5){
            image1Approve = true;
            image2Approve = false;
            image1.setImageDrawable(getResources().getDrawable(R.drawable.token_approve));
            image2.setImageDrawable(getResources().getDrawable(R.drawable.token_reject));
        } else {
            image1Approve = false;
            image2Approve = true;
            image1.setImageDrawable(getResources().getDrawable(R.drawable.token_reject));
            image2.setImageDrawable(getResources().getDrawable(R.drawable.token_approve));
        }

    }

    public void setOnClicklisteners(){
        Log.d(TAG, "setOnClickListeners called");

        image1.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "image1 clicked. Result: "+ image1Approve);
                returnVoteResultAndClose(image1Approve);
            }
        });

        image2.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "image2 clicked. Result: "+ image1Approve);
                returnVoteResultAndClose(image2Approve);
            }
        });

    }

    public void returnVoteResultAndClose(boolean voteResult){
        Log.d(TAG, "returnVoteResultAndClose called. Vote Result: " + voteResult);


        TeamVoteDialogListener activity = (TeamVoteDialogListener) getActivity();
        activity.onVoteSelected(voteResult); //Returns result of the vote to the GameActivity

        this.dismiss();
    }

    public interface TeamVoteDialogListener {
        void onVoteSelected(boolean voteResult);
    }
}
