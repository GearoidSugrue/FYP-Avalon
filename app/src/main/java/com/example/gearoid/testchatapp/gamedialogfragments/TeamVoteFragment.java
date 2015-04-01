package com.example.gearoid.testchatapp.gamedialogfragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.gearoid.testchatapp.GameLogicFunctions;
import com.example.gearoid.testchatapp.PlayerListViewAdapter;
import com.example.gearoid.testchatapp.R;
import com.example.gearoid.testchatapp.multiplayer.PlayerBasic;
import com.example.gearoid.testchatapp.multiplayer.Session;
import com.example.gearoid.testchatapp.singletons.Player;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by gearoid on 24/03/15.
 */
public class TeamVoteFragment extends DialogFragment {

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
    ArrayList<PlayerBasic> proposedPlayersArray;
    ArrayList<PlayerBasic> currentLeaderArray;
    ArrayList<PlayerBasic> nextLeaderArray;
    int questNumber;
    int voteCount;





    public static TeamVoteFragment newInstance(int[] playerPositions, int questNumber, int voteNumber) {
        TeamVoteFragment frag = new TeamVoteFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putIntArray("PLAYER_POS", playerPositions);
        args.putInt("QUEST_NUM", questNumber);
        args.putInt("VOTE_NUM", voteNumber);
        frag.setArguments(args);
        Log.d("TeamVoteFrag", "Creating instance of a teamvote fragment");
        return frag;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //characterName = getArguments().getString("character");
        Log.d("TeamVoteFrag", "onCreate called");
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);

        Bundle extras = getArguments();
        playerPos = extras.getIntArray("PLAYER_POS");
        questNumber = extras.getInt("QUEST_NUM");
        voteCount = extras.getInt("VOTE_NUM");

        proposedPlayersArray = new ArrayList<>();
        currentLeaderArray = new ArrayList<>();
        nextLeaderArray = new ArrayList<>();

        for(int i=0; i < playerPos.length; i++){
            Log.d("TeamVoteFrag", "adding player to adapterVictoriousPlayers. int[" + i + "] = " + playerPos[i]);
            proposedPlayersArray.add(Session.allPlayersBasic.get(playerPos[i]));
        }

        currentLeaderArray.add(Session.allPlayersBasic.get(GameLogicFunctions.getCurrentLeaderIndexInAllPlayerList()));
        nextLeaderArray.add(Session.allPlayersBasic.get(GameLogicFunctions.getNextLeaderIndexInAllPlayerList()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.team_vote, container, false);
        Log.d("TeamVoteFrag", "onCreateView called");

        Toolbar mActionBarToolbar = (Toolbar) rootView.findViewById(R.id.frag_teamVote_toolbar);
        mActionBarToolbar.setTitle("Quest " + questNumber + " - Vote " + voteCount);
        mActionBarToolbar.setLogo(getResources().getDrawable(R.drawable.icon_votetoken));

//        mActionBarToolbar.
//        rootView.setSupportActionBar(mActionBarToolbar);
//        getSupportActionBar().setTitle("My title");

        //getDialog().setTitle("Team Vote");
        image1 = (ImageView) rootView.findViewById(R.id.imageView_teamVote1);
        image2 = (ImageView) rootView.findViewById(R.id.imageView_teamVote2);

        randomiseTokenOrder();
        setOnClicklisteners();
        //image1.setBackground(getResources().getDrawable(R.drawable.token_approve));
        //image1.setBackgroundColor(Color.BLACK);

        proposedPlayerView = (ListView) rootView.findViewById(R.id.listview_proposedTeam);
        currentLeaderView = (ListView) rootView.findViewById(R.id.listview_currentLeader);
        nextLeaderView = (ListView) rootView.findViewById(R.id.listview_nextLeader);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        Log.d("TeamVoteFrag", "onCreateView called");
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
        Log.d("TeamVoteFrag", "randomiseCardOrder called");

        Random randomGenerator = new Random();
        int randNum = randomGenerator.nextInt(2);
        Log.d("TeamVoteFrag", "randomiseCardOrder: random num = " + randNum);

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
        Log.d("TeamVoteFrag", "setOnClicklisteners called");

        image1.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TeamVoteFrag", "image1 clicked. Result: "+ image1Approve);
                returnVoteResultAndClose(image1Approve);
            }
        });

        image2.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TeamVoteFrag", "image2 clicked. Result: "+ image1Approve);
                returnVoteResultAndClose(image2Approve);
            }
        });

    }

    public void returnVoteResultAndClose(boolean voteResult){
        Log.d("TeamVoteFrag", "returnVoteResultAndClose called. Vote Result: " + voteResult);


        TeamVoteDialogListener activity = (TeamVoteDialogListener) getActivity();
        activity.onVoteSelected(voteResult); //Returns result of the vote to the GameActivity

        this.dismiss();
    }

    public interface TeamVoteDialogListener {
        void onVoteSelected(boolean voteResult);
    }
}
