package com.example.gearoid.testchatapp;

import android.app.DialogFragment;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.gearoid.testchatapp.character.Assassin;
import com.example.gearoid.testchatapp.character.EvilCharacter;
import com.example.gearoid.testchatapp.gamedialogfragments.AssassinateFragment;
import com.example.gearoid.testchatapp.gamedialogfragments.GameFinishedFragment;
import com.example.gearoid.testchatapp.gamedialogfragments.LadyOfLakeFragment;
import com.example.gearoid.testchatapp.gamedialogfragments.PlayerCharacterFragment;
import com.example.gearoid.testchatapp.gamedialogfragments.QuestResultFragment;
import com.example.gearoid.testchatapp.gamedialogfragments.QuestVoteFragment;
import com.example.gearoid.testchatapp.gamedialogfragments.SelectTeamFragment;
import com.example.gearoid.testchatapp.gamedialogfragments.TeamVoteFragment;
import com.example.gearoid.testchatapp.gamedialogfragments.TeamVoteResultFragment;
import com.example.gearoid.testchatapp.kryopackage.ListenerClient;
import com.example.gearoid.testchatapp.kryopackage.ListenerServer;
import com.example.gearoid.testchatapp.kryopackage.Packet;
import com.example.gearoid.testchatapp.singletons.Player;

import static com.example.gearoid.testchatapp.GameLogicFunctions.*;
import static com.example.gearoid.testchatapp.multiplayer.Session.*;


public class GameActivity extends ActionBarActivity implements TeamVoteFragment.TeamVoteDialogListener, QuestVoteFragment.QuestVoteDialogListener, SelectTeamFragment.TeamSelectDialogListener,
        AssassinateFragment.AssassinateDialogListener, LadyOfLakeFragment.LadyOfLakeDialogListener, GameFinishedFragment.GameFinishedDialogListener, QuestResultFragment.QuestResultDialogListener, ListenerServer.IActivityServerListener, ListenerClient.IActivityClientListener {

    public GameBoardFragment gameBoardFrag;

    Button teamVoteFrag;
    Button questVoteFrag;
    Button teamSelectFrag;
    Button assassinateFrag;
    Button ladyOfLakeFrag;
    Button teamVoteResultFrag;
    Button gameFinishedFrag;
    Button playerCharacterFrag;
    Button questResultFrag;
//    public Board currentBoard;
//    public Quest currentQuest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.hide();
        } else {
            Toolbar toolbar = (Toolbar) findViewById(R.id.game_toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Quest " + currentQuest.getValue());
        }


        gameStatusView = (TextView) findViewById(R.id.textView_gameStateStatus);
        gameStatusView.setText(gameStatusText);

        initialiseFragments();
        initialiseButtons();

        if (serverListener != null) {
            serverListener.setActivityServerListener(this);
        }

        if (clientListener != null) {
            clientListener.setActivityClientListener(this);
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initialiseFragments() {
        gameBoardFrag = (GameBoardFragment) getFragmentManager()
                .findFragmentById(R.id.layout_gameBoardFragment);

//        Bundle args = new Bundle();
//        args.putSerializable("BOARD", currentBoard);
//        gameBoardFrag.setArguments(args);
    }

    public void initialiseButtons() {
        teamVoteFrag = (Button) findViewById(R.id.button_teamVoteFrag);
        questVoteFrag = (Button) findViewById(R.id.button_questVoteFrag);
        teamSelectFrag = (Button) findViewById(R.id.button_teamSelectFrag);
        assassinateFrag = (Button) findViewById(R.id.button_assassinateFrag);
        ladyOfLakeFrag = (Button) findViewById(R.id.button_ladyOfLakeFrag);
        teamVoteResultFrag = (Button) findViewById(R.id.button_teamVoteResultFrag);
        gameFinishedFrag = (Button) findViewById(R.id.button_gameFinishedFrag);
        playerCharacterFrag = (Button) findViewById(R.id.button_playerCharacterFrag);
        questResultFrag = (Button) findViewById(R.id.button_questResultFrag);

        teamVoteFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment newFragment = TeamVoteFragment.newInstance(new int[]{0, 1}, 1, 3); //TODO change to voteCount + QuestCount
                newFragment.show(getFragmentManager(), "teamdialog");
            }
        });

        questVoteFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment newFragment = QuestVoteFragment.newInstance(new int[]{0, 1}, Player.getInstance().character instanceof EvilCharacter, 1); //TODO change to questNum...
                newFragment.show(getFragmentManager(), "questdialog");
            }
        });

        teamSelectFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment newFragment = SelectTeamFragment.newInstance(3, 2); //TODO change to questNum...
                newFragment.show(getFragmentManager(), "teamselectdialog");
            }
        });

        assassinateFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment newFragment = AssassinateFragment.newInstance(); //TODO change to questNum...
                newFragment.show(getFragmentManager(), "assassinatedialog");
            }
        });

        ladyOfLakeFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(currentGameState == GameState.LADY_OF_LAKE){
                    DialogFragment newFragment = LadyOfLakeFragment.newInstance(); //TODO change to questNum...
                    newFragment.show(getFragmentManager(), "ladyoflakedialog");
                } else {
                    ApplicationContext.showToast("Must be used at the beginning of a Quest!");
                }


            }
        });

        teamVoteResultFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment newFragment = TeamVoteResultFragment.newInstance(false, new int[]{0, 1}, new int[]{0, 1}, 2, 3); //TODO change to questNum...
                newFragment.show(getFragmentManager(), "teamvoteresultdialog");
            }
        });

        gameFinishedFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment newFragment = GameFinishedFragment.newInstance(true, new int[]{0, 1}, new int[]{0, 1}); //TODO change to questNum...
                newFragment.show(getFragmentManager(), "teamvoteresultdialog");
            }
        });

        playerCharacterFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment newFragment = PlayerCharacterFragment.newInstance(); //TODO change to questNum...
                newFragment.show(getFragmentManager(), "playercharacterdialog");
            }
        });

        questResultFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment newFragment = QuestResultFragment.newInstance(new int[]{0, 1}, new boolean[]{true, true}, currentQuest, calculateFailRequiredForQuest(currentBoard, currentQuest)); //TODO change to questNum...
                newFragment.show(getFragmentManager(), "questresultdialog");
            }
        });

    }

    public void setAllButtonsGone(){
        teamSelectFrag.setVisibility(View.GONE);
        teamVoteFrag.setVisibility(View.GONE);
        teamVoteResultFrag.setVisibility(View.GONE);
        questVoteFrag.setVisibility(View.GONE);
        questResultFrag.setVisibility(View.GONE);
        ladyOfLakeFrag.setVisibility(View.GONE);
        assassinateFrag.setVisibility(View.GONE);
        gameFinishedFrag.setVisibility(View.GONE);
    }

    public void checkGameStateAndUpdateScreen() {

        setAllButtonsGone();

        switch (currentGameState) {
            case TEAM_SELECT:
                setTeamSelectState();
            case TEAM_VOTE:
                setTeamVoteState();
            case TEAM_VOTE_RESULT:
                setTeamVoteResultState();
            case QUEST_VOTE:
                setQuestVoteState();
            case QUEST_VOTE_RESULT:
                setQuestVoteResultState();
            case LADY_OF_LAKE:
                setLadyOfLakeState();
            case ASSASSIN:
                setAssassinState();
            case FINISHED:
                setFinishedState();
        }
    }

    public void setTeamSelectState() {

        if (getUserPlayer().isLeader) {
            updateGameStatusText("Your The Leader - Select A Team");
            teamSelectFrag.setVisibility(View.VISIBLE);
        } else {
            updateGameStatusText("Waiting For The Leader To Select Team");
            teamSelectFrag.setVisibility(View.GONE);
        }
    }

    public void setTeamVoteState() {
        teamVoteFrag.setVisibility(View.VISIBLE);
    }

    public void setTeamVoteResultState() {
        teamVoteResultFrag.setVisibility(View.VISIBLE);

        if(getUserPlayer().isLeader){
            teamSelectFrag.setVisibility(View.VISIBLE);
        }
    }

    public void setQuestVoteState() {
        if (getUserPlayer().isOnQuest) {
            updateGameStatusText("Your On A Quest");
            questVoteFrag.setVisibility(View.VISIBLE);
        } else {
            updateGameStatusText("Waiting For Quest Team");
            questVoteFrag.setVisibility(View.GONE);
        }
    }

    public void setQuestVoteResultState() {
        updateGameStatusText("View Quest " + currentQuest.getValue() + " Results");
        questResultFrag.setVisibility(View.VISIBLE);
    }

    public void setLadyOfLakeState() {
        if (getUserPlayer().hasLadyOfLake) {
            updateGameStatusText("Lady of The Lake Token");
            ladyOfLakeFrag.setVisibility(View.VISIBLE);
        } else{
            updateGameStatusText("Waiting For Lady of the Lake Holder");
        }
    }

    public void setAssassinState() {
        if (getUserPlayer().character instanceof Assassin) {
            updateGameStatusText("Select A Player To Assassinate");
            assassinateFrag.setVisibility(View.VISIBLE);
        } else{
            updateGameStatusText("Waiting For The Assassin");
        }
    }

    public void setFinishedState() {
        updateGameStatusText("Game Finished");
    }

    public static void updateGameStatusText(final String text) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d("GameActivity", "Attempting to update gameStatusText");
                gameStatusText = text;
                gameStatusView.setText(text);
                //Toast.makeText(getContext(), data, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onVoteSelected(boolean voteResult) {
        Log.d("GameActivity", "Vote Result received from team vote dialog: " + voteResult);

        //TODO Send vote result to server
    }

    @Override
    public void onQuestVoteSelected(boolean voteResult) {
        Log.d("GameActivity", "Vote Result received from quest vote dialog: " + voteResult);

        //TODO Send vote result to server

    }

    @Override
    public void onTeamSelected(int[] teamIndexes) {
        Log.d("GameActivity", "Team selected received from TeamSelect dialog.");
        //TODO Send player indexes to server result to server


    }

    @Override
    public void onAssassination(boolean isSuccess) {
        Log.d("GameActivity", "OnAssassinate received from Assassinate dialog. Result: " + isSuccess);
        //TODO Send info to server

    }

    @Override
    public void ladyOfLakeActivated(int playerIndex) {
        Log.d("GameActivity", "LadyOfLakeActivated received from LadyOfLake dialog. Player ID " + playerIndex);
        //TODO Send player info to server

    }

    @Override
    public void onGameFinishedSelection(boolean playAgain) {
        Log.d("GameActivity", "GameFinished option received from GameFinished dialog. PlayAgain = " + playAgain);
        //TODO Send info to server

    }

    @Override
    public void onQuestVoteResultRevealed(int voteNumber, boolean voteResult) {
        Log.d("GameActivity", "QuestVoteResultRevealed from QuestResultDialog. Vote Number: " + voteNumber + ", Vote Result: " + voteResult);
        //TODO Send info to server

    }

    @Override
    public void onQuestVoteResultsFinished(boolean result) {
        Log.d("GameActivity", "onQuestVoteResultsFinished from QuestResultDialog. Quest Result: " + result);
        //TODO Send info to server

    }

    @Override
    public void onPacketReceived(String message) {
        Log.d("GameActivity", "onPacketReceived from ListenerServer. Message: " + message);

        ApplicationContext.showToast("IActivityServerListener Works!!!");
    }

    @Override
    public void startTeamVoteDialog(int[] proposedTeam, int questNumber, int voteCount) {
        Log.d("GameActivity", "startTeamVoteDialog from ListenerServer");

    }

    @Override
    public void startTeamVoteResultDialog() {
        Log.d("GameActivity", "startTeamVoteResultDialog from ListenerServer");

    }

    @Override
    public void client_OnPacketReceived(String message) {
        Log.d("GameActivity", "client_OnPacketReceived from ListenerClient");

    }

    @Override
    public void client_UpdateGameState(GameState nextGameState) {
        Log.d("GameActivity", "client_UpdateGameState from ListenerClient");
        currentGameState = nextGameState;
        checkGameStateAndUpdateScreen();
    }

//    @Override
//    public void clientStartTeamVoteDialog(int[] proposedTeam, GameLogicFunctions.Quest quest, int voteCount) {
//        Log.d("GameActivity", "clientStartTeamVoteDialog from ListenerClient");
//        teamVoteFrag.setVisibility(View.VISIBLE);
//
//        DialogFragment newFragment = TeamVoteFragment.newInstance(proposedTeam, quest.getValue(), voteCount); //TODO change to voteCount + QuestCount
//        newFragment.show(getFragmentManager(), "teamdialog");
//    }
//
//    @Override
//    public void clientStartTeamVoteResultDialog(boolean isApproved, int[] playerApprovedPos, int[] playerRejectedPos, GameLogicFunctions.Quest quest, int voteNumber) {
//        Log.d("GameActivity", "clientStartTeamVoteResultDialog from ListenerClient");
//
//        teamVoteResultFrag.setVisibility(View.VISIBLE);
//
//        DialogFragment newFragment = TeamVoteResultFragment.newInstance(isApproved, playerApprovedPos, playerRejectedPos, quest.getValue(), voteNumber); //TODO change to voteCount + QuestCount
//        newFragment.show(getFragmentManager(), "teamvoteresultdialog");
//    }

    @Override
    public void client_OnTeamVoteReceived(final Packet.Packet_TeamVote voteInfo) {
        Log.d("GameActivity", "client_OnTeamVoteReceived from ListenerClient");

        teamVoteFrag.setVisibility(View.VISIBLE);
        final DialogFragment newFragment = TeamVoteFragment.newInstance(voteInfo.proposedTeam, voteInfo.quest.getValue(), voteInfo.voteCount); //TODO change to questNum...

        teamVoteFrag.setOnClickListener(null);
        teamVoteFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("GameActivity", "TeamVoteFrag clicked, Starting teamVoteDialog.");

                newFragment.show(getFragmentManager(), "teamvotedialog");
            }
        });

        newFragment.show(getFragmentManager(), "teamvotedialog");
    }

    @Override
    public void client_OnTeamVoteResultReceived(final Packet.Packet_TeamVoteResult voteResultInfo) {
        Log.d("GameActivity", "client_OnTeamVoteResultReceived from ListenerClient");

        teamVoteResultFrag.setVisibility(View.VISIBLE);
        final DialogFragment newFragment = TeamVoteResultFragment.newInstance(voteResultInfo.isApproved, voteResultInfo.playerApprovedPos, voteResultInfo.playerRejectedPos, voteResultInfo.quest.getValue(), voteResultInfo.voteNumber); //TODO change to questNum...

        teamVoteResultFrag.setOnClickListener(null);
        teamVoteResultFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("GameActivity", "TeamVoteResultFrag clicked, Starting teamVoteResultDialog.");

                newFragment.show(getFragmentManager(), "teamvoteresultdialog");
            }
        });

        newFragment.show(getFragmentManager(), "teamvoteresultdialog");
    }

    @Override
    public void client_OnQuestVoteReceived(Packet.Packet_QuestVote questInfo) {
        Log.d("GameActivity", "client_OnQuestVoteReceived from ListenerClient");

        questVoteFrag.setVisibility(View.VISIBLE);
        final DialogFragment newFragment = QuestVoteFragment.newInstance(questInfo.teamMemberPos, GameLogicFunctions.getUserPlayer().character instanceof EvilCharacter , questInfo.quest.getValue());

        questVoteFrag.setOnClickListener(null);
        questVoteFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("GameActivity", "questVoteFrag clicked, Starting questdialog.");

                newFragment.show(getFragmentManager(), "questdialog");
            }
        });

        newFragment.show(getFragmentManager(), "questdialog");
    }

    @Override
    public void client_OnSelectTeamReceived(Packet.Packet_SelectTeam questInfo) {
        Log.d("GameActivity", "client_OnSelectTeamReceived from ListenerClient");

        teamSelectFrag.setVisibility(View.VISIBLE);
        final DialogFragment newFragment = SelectTeamFragment.newInstance(questInfo.teamSize, questInfo.quest.getValue());

        teamSelectFrag.setOnClickListener(null);
        teamSelectFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("GameActivity", "teamSelectFrag clicked, Starting teamselectdialog.");

                newFragment.show(getFragmentManager(), "teamselectdialog");
            }
        });

        newFragment.show(getFragmentManager(), "teamselectdialog");
    }



}
