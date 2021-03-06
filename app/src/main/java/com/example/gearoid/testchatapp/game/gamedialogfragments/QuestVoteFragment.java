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
import com.example.gearoid.testchatapp.multiplayer.Player;
import com.example.gearoid.testchatapp.multiplayer.Session;
import com.example.gearoid.testchatapp.utils.ApplicationContext;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by gearoid on 26/03/15.
 */
public class QuestVoteFragment extends DialogFragment {

    //Constants
    public static final String TAG = "QuestVoteFragment";
    public static final String TEAM_MEMBERS = "TEAM_MEMBERS";
    public static final String IS_GOOD = "IS_GOOD";
    public static final String QUEST = "QUEST";

    View mContentView = null;
    ImageView image1;
    ImageView image2;
    boolean isImage1Sucess;
    boolean isImage2Sucess;
    ListView teamMembersView;
    PlayerListViewAdapter adapterTeamMembers;
    ArrayList<Player> teamMembersArray;
    int[] playerPos;
    boolean isGood;
    int questNumber;

    public static QuestVoteFragment newInstance(int[] teamMembersPos, boolean isGood, int questNumber) {
        Log.d(TAG, "Creating instance of a QuestVoteFragment fragment");

        QuestVoteFragment frag = new QuestVoteFragment();

        Bundle args = new Bundle();
        args.putIntArray(TEAM_MEMBERS, teamMembersPos);
        args.putBoolean(IS_GOOD, isGood);
        args.putInt(QUEST, questNumber);
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
        playerPos = extras.getIntArray(TEAM_MEMBERS);
        isGood = extras.getBoolean(IS_GOOD, false);
        questNumber = extras.getInt(QUEST);

        teamMembersArray = new ArrayList<>();

        for(int i=0; i < playerPos.length; i++){
            Log.d(TAG, "adding player to adapterTeamMembers. int[" + i + "] = " + playerPos[i]);
            teamMembersArray.add(Session.allPlayers.get(playerPos[i]));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.quest_vote, container, false);
        Log.d("TeamVoteFrag", "onCreateView called");

        Toolbar mActionBarToolbar = (Toolbar) rootView.findViewById(R.id.frag_questVote_toolbar);
        mActionBarToolbar.setTitle("Quest " + questNumber);
        mActionBarToolbar.setLogo(getResources().getDrawable(R.drawable.icon_questmember));

        image1 = (ImageView) rootView.findViewById(R.id.imageView_questVote1);
        image2 = (ImageView) rootView.findViewById(R.id.imageView_questVote2);

        teamMembersView = (ListView) rootView.findViewById(R.id.listview_teamMembers);

        randomiseCardOrder();
        setOnClicklisteners();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated called");

        adapterTeamMembers = new PlayerListViewAdapter(getActivity(), R.layout.row_players, android.R.id.text1, teamMembersArray);
        teamMembersView.setAdapter(adapterTeamMembers);

        int adapterCount = adapterTeamMembers.getCount();
        int deviceConfig = getActivity().getResources().getConfiguration().orientation;

        if(deviceConfig == Configuration.ORIENTATION_LANDSCAPE && adapterCount > 2 ){
            View item = adapterTeamMembers.getView(0, null, teamMembersView);
            item.measure(0, 0);
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (1.5 * item.getMeasuredHeight()));
            teamMembersView.setLayoutParams(params);
        } else if(deviceConfig == Configuration.ORIENTATION_PORTRAIT && adapterCount > 4) {
            View item = adapterTeamMembers.getView(0, null, teamMembersView);
            item.measure(0, 0);
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (3.6 * item.getMeasuredHeight()));
            teamMembersView.setLayoutParams(params);
        }

    }

    public void randomiseCardOrder() {//TODO add isGood check
        Log.d(TAG, "randomiseCardOrder called");

        Random randomGenerator = new Random();
        int randNum = randomGenerator.nextInt(2);
        Log.d(TAG, "randomiseCardOrder: random num = " + randNum);

        if (Math.random() > 0.5) {
            isImage1Sucess = true;
            isImage2Sucess = false;
            image1.setImageDrawable(getResources().getDrawable(R.drawable.misc_success));
            image2.setImageDrawable(getResources().getDrawable(R.drawable.misc_fail));
        } else {
            isImage1Sucess = false;
            isImage2Sucess = true;
            image1.setImageDrawable(getResources().getDrawable(R.drawable.misc_fail));
            image2.setImageDrawable(getResources().getDrawable(R.drawable.misc_success));
        }

    }

    public void setOnClicklisteners() {
        Log.d(TAG, "setOnClickListeners called");

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "image1 clicked. Result: " + isImage1Sucess);

                if(!isGood){
                    returnVoteResultAndClose(isImage1Sucess);
                } else if(isImage1Sucess){
                    returnVoteResultAndClose(isImage1Sucess);
                } else {
                    ApplicationContext.showToast(getActivity().getString(R.string.mustVoteSuccess));
                }
            }
        });

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "image2 clicked. Result: " + isImage1Sucess);
                if(!isGood){
                    returnVoteResultAndClose(isImage2Sucess);
                } else if(isImage2Sucess){
                    returnVoteResultAndClose(isImage2Sucess);
                } else {
                    ApplicationContext.showToast(getActivity().getString(R.string.mustVoteSuccess));
                }
            }
        });

    }

    public void returnVoteResultAndClose(boolean voteResult) {
        Log.d(TAG, "returnVoteResultAndClose called. Vote Result: " + voteResult);

        QuestVoteDialogListener activity = (QuestVoteDialogListener) getActivity();
        activity.onQuestVoteSelected(voteResult); //Returns result of the vote to the GameActivity

        this.dismiss();
    }

    public interface QuestVoteDialogListener {
        void onQuestVoteSelected(boolean voteResult);
    }


}
