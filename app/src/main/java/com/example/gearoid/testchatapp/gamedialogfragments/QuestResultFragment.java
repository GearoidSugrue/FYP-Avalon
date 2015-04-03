package com.example.gearoid.testchatapp.gamedialogfragments;

import android.app.DialogFragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gearoid.testchatapp.ApplicationContext;
import com.example.gearoid.testchatapp.GameLogicFunctions;
import com.example.gearoid.testchatapp.PlayerListViewAdapter;
import com.example.gearoid.testchatapp.R;
import com.example.gearoid.testchatapp.multiplayer.Player;
import com.example.gearoid.testchatapp.multiplayer.Session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by gearoid on 30/03/15.
 */
public class QuestResultFragment extends DialogFragment {

    View mContentView = null;
    ImageView image1;
    ImageView image2;
    ImageView image3;
    ImageView image4;
    ImageView image5;
    Drawable success;
    Drawable fail;
    ListView teamMembersView;
    PlayerListViewAdapter adapterTeamMembers;
    ArrayList<Player> teamMembersArray;
    Button finishGame;
    TextView resultTextview;
    int[] playerPos;
    boolean[] votes;
    int failsRequired, votesRevealedCount = 0;
    boolean isFinished, isLeader, questResult;
    boolean vote1Revealed, vote2Revealed, vote3Revealed, vote4Revealed, vote5Revealed;
    GameLogicFunctions.Quest quest;

    public static QuestResultFragment newInstance(int[] teamMembersPos, boolean[] votes, GameLogicFunctions.Quest quest, int failsRequired) {
        Log.d("QuestResultFragment", "Creating instance of a QuestResultFragment");

        QuestResultFragment frag = new QuestResultFragment();

        Bundle args = new Bundle();
        args.putIntArray("TEAM_MEMBERS", teamMembersPos);
        args.putBooleanArray("VOTES", votes);
        args.putSerializable("QUEST", quest);
        args.putInt("FAILS_REQUIRED", failsRequired);

        frag.setArguments(args);

        return frag;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //characterName = getArguments().getString("character");
        Log.d("QuestResultFragment", "onCreate called");
        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);

        isLeader = GameLogicFunctions.getUserPlayer().isLeader;

        Bundle extras = getArguments();
        playerPos = extras.getIntArray("TEAM_MEMBERS");
        votes = extras.getBooleanArray("VOTES");
        quest = (GameLogicFunctions.Quest) extras.getSerializable("QUEST");
        failsRequired = extras.getInt("FAILS_REQUIRED");

        Collections.shuffle(Arrays.asList(votes)); //TODO fix shuffle

        teamMembersArray = new ArrayList<>();

        for (int i = 0; i < playerPos.length; i++) {
            Log.d("QuestResultFrag", "adding player to adapterTeamMembers. int[" + i + "] = " + playerPos[i]);
            teamMembersArray.add(Session.allPlayers.get(playerPos[i]));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.quest_result, container, false);
        Log.d("QuestResultFragment", "onCreateView called");

        mContentView = rootView;

        String leaderText = "";
        if(isLeader){
            leaderText = " - Leader";
        }

        Toolbar mActionBarToolbar = (Toolbar) rootView.findViewById(R.id.frag_questVoteResult_toolbar);
        mActionBarToolbar.setTitle("Quest " + quest.getValue() + " Result" + leaderText);
        mActionBarToolbar.setLogo(getResources().getDrawable(R.drawable.icon_questvote));

        success = getResources().getDrawable(R.drawable.misc_success);
        fail = getResources().getDrawable(R.drawable.misc_fail);

        image1 = (ImageView) rootView.findViewById(R.id.imageView_questVote1);
        image2 = (ImageView) rootView.findViewById(R.id.imageView_questVote2);
        image3 = (ImageView) rootView.findViewById(R.id.imageView_questVote3);
        image4 = (ImageView) rootView.findViewById(R.id.imageView_questVote4);
        image5 = (ImageView) rootView.findViewById(R.id.imageView_questVote5);
        //image6 = (ImageView) rootView.findViewById(R.id.imageView_questVote6);

        int voteCount = votes.length;

        LinearLayout row2 = (LinearLayout) rootView.findViewById(R.id.layout_questVoteResultsRow2);

        if (voteCount > 2) {
            image3.setVisibility(View.VISIBLE);

        }
        if (votes.length > 3) {
            row2.setVisibility(View.VISIBLE);
            image4.setVisibility(View.VISIBLE);

        }
        if (votes.length > 4) {
            row2.setVisibility(View.VISIBLE);
            image4.setVisibility(View.VISIBLE);
            image5.setVisibility(View.VISIBLE);
        }

        teamMembersView = (ListView) rootView.findViewById(R.id.listview_questTeam);

        // randomiseCardOrder();

        //TODO add team member adapter...

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("QuestResultFragment", "onCreateView called");

        initializeButtons();
        setOnClicklisteners();

        adapterTeamMembers = new PlayerListViewAdapter(getActivity(), R.layout.row_players, android.R.id.text1, teamMembersArray);
        teamMembersView.setAdapter(adapterTeamMembers);

        resultTextview = (TextView) mContentView.findViewById(R.id.textView_questResult);

        int adapterCount = adapterTeamMembers.getCount();
        //int deviceConfig = getActivity().getResources().getConfiguration().orientation;

        View item = adapterTeamMembers.getView(0, null, teamMembersView);
        item.measure(0, 0);

        if (adapterCount > 3) {

            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (1.5 * item.getMeasuredHeight()));
            teamMembersView.setLayoutParams(params);
            resultTextview.setLayoutParams(params);
        } else if (adapterCount < 3) {
            View margin = (View) mContentView.findViewById(R.id.rightBlankSpaceRow1);
            margin.setVisibility(View.GONE);

            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (2 * item.getMeasuredHeight()));
            resultTextview.setLayoutParams(params);
        } else {
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (3 * item.getMeasuredHeight()));
            resultTextview.setLayoutParams(params);
        }

    }

    public void initializeButtons() {

        finishGame = (Button) mContentView.findViewById(R.id.button_dismissQuestResult);

        finishGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isFinished) {
                    if (isLeader) {
                        QuestResultDialogListener activity = (QuestResultDialogListener) getActivity();
                        activity.onQuestVoteResultsFinished(questResult);
                    }

                    closeDialog();
                }
            }
        });

    }

    public void setOnClicklisteners() {
        Log.d("QuestResultFragment", "setOnClicklisteners called");

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("QuestResultFragment", "image1 clicked. Result: " + votes[0]);

                if (!vote1Revealed) {
                    if (isLeader) {

                        if (votes[0]) {
                            image1.setImageDrawable(success);
                        } else {
                            image1.setImageDrawable(fail);
                        }
                        vote1Revealed = true;
                        votesRevealedCount++;
                        checkAllVotesRevealed();

                        QuestResultDialogListener activity = (QuestResultDialogListener) getActivity();
                        activity.onQuestVoteResultRevealed(1, votes[0]);
                    } else {
                        ApplicationContext.showToast("Only The Leader Can Reveal Quest Result");
                    }
                }
            }
        });

        image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("QuestResultFragment", "image2 clicked. Result: " + votes[1]);

                if (!vote2Revealed) {
                    if (isLeader) {
                        if (votes[1]) {
                            image2.setImageDrawable(success);
                        } else {
                            image2.setImageDrawable(fail);
                        }
                        vote2Revealed = true;
                        votesRevealedCount++;
                        checkAllVotesRevealed();

                        QuestResultDialogListener activity = (QuestResultDialogListener) getActivity();
                        activity.onQuestVoteResultRevealed(2, votes[1]);
                    } else {
                        ApplicationContext.showToast("Only The Leader Can Reveal Quest Result");
                    }
                }
            }
        });

        image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("QuestResultFragment", "image3 clicked. Result: " + votes[2]);

                if (!vote3Revealed) {
                    if (isLeader) {
                        if (votes[2]) {
                            image3.setImageDrawable(success);
                        } else {
                            image3.setImageDrawable(fail);
                        }
                        vote3Revealed = true;
                        votesRevealedCount++;
                        checkAllVotesRevealed();

                        QuestResultDialogListener activity = (QuestResultDialogListener) getActivity();
                        activity.onQuestVoteResultRevealed(3, votes[2]);
                    } else {
                        ApplicationContext.showToast("Only The Leader Can Reveal Quest Result");
                    }
                }

            }
        });

        image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("QuestResultFragment", "image4 clicked. Result: " + votes[3]);

                if (!vote4Revealed) {
                    if (isLeader) {
                        if (votes[3]) {
                            image4.setImageDrawable(success);
                        } else {
                            image4.setImageDrawable(fail);
                        }
                        vote4Revealed = true;
                        votesRevealedCount++;
                        checkAllVotesRevealed();

                        QuestResultDialogListener activity = (QuestResultDialogListener) getActivity();
                        activity.onQuestVoteResultRevealed(4, votes[3]);
                    } else {
                        ApplicationContext.showToast("Only The Leader Can Reveal Quest Result");
                    }
                }

            }
        });

        image5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("QuestResultFragment", "image5 clicked. Result: " + votes[4]);

                if (!vote5Revealed) {
                    if (isLeader) {
                        if (votes[4]) {
                            image5.setImageDrawable(success);
                        } else {
                            image5.setImageDrawable(fail);
                        }
                        vote5Revealed = true;
                        votesRevealedCount++;
                        checkAllVotesRevealed();

                        QuestResultDialogListener activity = (QuestResultDialogListener) getActivity();
                        activity.onQuestVoteResultRevealed(5, votes[4]);
                    } else {
                        ApplicationContext.showToast("Only The Leader Can Reveal Quest Result");
                    }
                }
            }
        });

    }

    public void checkAllVotesRevealed() {

        if (votesRevealedCount == votes.length) {
            finishGame.setVisibility(View.VISIBLE);
            teamMembersView.setVisibility(View.GONE);
            isFinished = true;

            int fails = 0;
            for (boolean vote : votes) {
                if (!vote) {
                    fails++;
                }
            }

            if (fails >= failsRequired) {
                questResult = false;
                resultTextview.setText("Quest Failed");
                resultTextview.setTextColor(getResources().getColor(R.color.RedWine));
            } else {
                questResult = true;
            }

            resultTextview.setVisibility(View.VISIBLE);

        }
    }

    public void showVote(int voteNumber, boolean voteResult) {

        votesRevealedCount++;
        checkAllVotesRevealed();
        Drawable result;

        if (voteResult) {
            result = getResources().getDrawable(R.drawable.misc_success);
        } else {
            result = getResources().getDrawable(R.drawable.misc_fail);
        }

        if (voteNumber == 1) {
            image1.setImageDrawable(result);

        } else if (voteNumber == 2) {
            image2.setImageDrawable(result);

        } else if (voteNumber == 3) {
            image3.setImageDrawable(result);

        } else if (voteNumber == 4) {
            image4.setImageDrawable(result);

        } else if (voteNumber == 5) {
            image5.setImageDrawable(result);
        }
    }

    public void closeDialog() {
        this.dismiss();
    }

    public interface QuestResultDialogListener {
        void onQuestVoteResultRevealed(int voteNumber, boolean voteResult);

        void onQuestVoteResultsFinished(boolean result);
    }
}