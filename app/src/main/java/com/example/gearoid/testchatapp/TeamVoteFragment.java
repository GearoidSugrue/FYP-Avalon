package com.example.gearoid.testchatapp;

import android.graphics.Color;
import android.os.Bundle;
import android.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

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


    static TeamVoteFragment newInstance(String name) {
        TeamVoteFragment frag = new TeamVoteFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        //args.putString("character", name);
        //frag.setArguments(args);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.team_vote, container, false);
        Log.d("TeamVoteFrag", "onCreateView called");

        //getDialog().setTitle("Team Vote");
        image1 = (ImageView) rootView.findViewById(R.id.imageView_teamVote1);
        image2 = (ImageView) rootView.findViewById(R.id.imageView_teamVote2);

        randomiseTokenOrder();
        setOnClicklisteners();
        //image1.setBackground(getResources().getDrawable(R.drawable.token_approve));
        //image2.setBackground(getResources().getDrawable(R.drawable.token_reject));
        //image1.setBackgroundColor(Color.BLACK);
        //image2.setBackgroundColor(Color.DKGRAY);


        ListView proposedTeam = (ListView) rootView.findViewById(R.id.listview_proposedTeam);

        //TODO add player info here

        //ListAdapter listAdapter
        //proposedTeam.setAdapter();

        //image.setBackgroundColor(Color.BLACK);
        //mContentView = container;
        return rootView;
    }

    public void randomiseTokenOrder(){
        Log.d("TeamVoteFrag", "randomiseTokenOrder called");

        Random randomGenerator = new Random();
        int randNum = randomGenerator.nextInt(2);
        Log.d("TeamVoteFrag", "randomiseTokenOrder: random num = " + randNum);

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
