package com.example.gearoid.testchatapp;

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Random;

/**
 * Created by gearoid on 26/03/15.
 */
public class QuestVoteFragment extends DialogFragment {

    View mContentView = null;
    ImageView image1;
    ImageView image2;
    boolean isImage1Sucess;
    boolean isImage2Sucess;
    boolean isEvil;
//        ListView proposedPlayerView;  //Delete???
//        PlayerListViewAdapter adapterProposedTeam;
//        ArrayList<Player> proposedPlayersArray;

    static QuestVoteFragment newInstance(boolean isEvil) {
        QuestVoteFragment frag = new QuestVoteFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putBoolean("IS_EVIL", isEvil);
        frag.setArguments(args);
        Log.d("QuestVoteFragment", "Creating instance of a QuestVoteFragment fragment");
        return frag;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //characterName = getArguments().getString("character");
        Log.d("QuestVoteFrag", "onCreate called");
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);

        Bundle extras = getArguments();
        isEvil = extras.getBoolean("IS_EVIL", false);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.quest_vote, container, false);
        Log.d("TeamVoteFrag", "onCreateView called");

        //getDialog().setTitle("Team Vote");
        image1 = (ImageView) rootView.findViewById(R.id.imageView_questVote1);
        image2 = (ImageView) rootView.findViewById(R.id.imageView_questVote2);

        randomiseCardOrder();
        setOnClicklisteners();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("QuestVoteFrag", "onCreateView called");
    }

    public void randomiseCardOrder() {//TODO add isEvil check
        Log.d("QuestVoteFrag", "randomiseCardOrder called");

        Random randomGenerator = new Random();
        int randNum = randomGenerator.nextInt(2);
        Log.d("QuestVoteFrag", "randomiseCardOrder: random num = " + randNum);

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
        Log.d("QuestVoteFrag", "setOnClicklisteners called");

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("QuestVoteFrag", "image1 clicked. Result: " + isImage1Sucess);

                if(isEvil){
                    returnVoteResultAndClose(isImage1Sucess);
                } else if(isImage1Sucess){
                    returnVoteResultAndClose(isImage1Sucess);
                } else {
                    ApplicationContext.showToast("Must Vote Success");
                }
            }
        });

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("QuestVoteFrag", "image2 clicked. Result: " + isImage1Sucess);
                if(isEvil){
                    returnVoteResultAndClose(isImage2Sucess);
                } else if(isImage2Sucess){
                    returnVoteResultAndClose(isImage2Sucess);
                } else {
                    ApplicationContext.showToast("Must Vote Success");
                }
            }
        });

    }

    public void returnVoteResultAndClose(boolean voteResult) {
        Log.d("QuestVoteFrag", "returnVoteResultAndClose called. Vote Result: " + voteResult);

        QuestVoteDialogListener activity = (QuestVoteDialogListener) getActivity();
        activity.onQuestVoteSelected(voteResult); //Returns result of the vote to the GameActivity

        this.dismiss();
    }

    public interface QuestVoteDialogListener {
        void onQuestVoteSelected(boolean voteResult);
    }


}
