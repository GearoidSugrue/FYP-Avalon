package com.example.gearoid.testchatapp.game;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gearoid.testchatapp.utils.ApplicationContext;
import com.example.gearoid.testchatapp.R;
import com.example.gearoid.testchatapp.character.evil.Assassin;
import com.example.gearoid.testchatapp.game.gamedialogfragments.AssassinateFragment;
import com.example.gearoid.testchatapp.game.gamedialogfragments.GameFinishedFragment;
import com.example.gearoid.testchatapp.game.gamedialogfragments.LadyOfLakeFragment;
import com.example.gearoid.testchatapp.game.gamedialogfragments.PlayerCharacterFragment;
import com.example.gearoid.testchatapp.game.gamedialogfragments.QuestResultFragment;
import com.example.gearoid.testchatapp.game.gamedialogfragments.QuestVoteFragment;
import com.example.gearoid.testchatapp.game.gamedialogfragments.SelectTeamFragment;
import com.example.gearoid.testchatapp.game.gamedialogfragments.TeamVoteFragment;
import com.example.gearoid.testchatapp.game.gamedialogfragments.TeamVoteResultFragment;
import com.example.gearoid.testchatapp.kryopackage.client.ListenerClient;
import com.example.gearoid.testchatapp.kryopackage.server.ListenerServer;
import com.example.gearoid.testchatapp.kryopackage.Packet;
import com.example.gearoid.testchatapp.kryopackage.PacketFactory;
import com.example.gearoid.testchatapp.multiplayer.Session;
import com.example.gearoid.testchatapp.singletons.PlayerConnection;
import com.example.gearoid.testchatapp.singletons.ServerInstance;
import com.example.gearoid.testchatapp.utils.ScreenReceiver;

import java.util.ArrayList;

import static com.example.gearoid.testchatapp.game.GameLogicFunctions.*;
import static com.example.gearoid.testchatapp.multiplayer.Session.*;


public class GameActivity extends ActionBarActivity implements TeamVoteFragment.TeamVoteDialogListener, QuestVoteFragment.QuestVoteDialogListener, SelectTeamFragment.TeamSelectDialogListener,
        AssassinateFragment.AssassinateDialogListener, LadyOfLakeFragment.LadyOfLakeDialogListener, GameFinishedFragment.GameFinishedDialogListener, QuestResultFragment.QuestResultDialogListener,
        ListenerServer.IGameActivityServerListener, ListenerClient.IActivityClientListener {


//    public Board currentBoard;
//    public Quest currentQuest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Log.d("GameActivity", "onCreate");

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.game_toolbar);
            setSupportActionBar(toolbar);

            ActionBar actionBar = getSupportActionBar();
            actionBar.hide();
        } else {
            mainToolbar = (Toolbar) findViewById(R.id.game_toolbar);
            setSupportActionBar(mainToolbar);
            //getSupportActionBar().setTitle("Quest " + currentQuest.getValue());
        }
        //todo change standard color...

        //mainToolbar.setBackgroundColor(getResources().getColor(R.color.RedWine));

        initialiseFragments();
        initialiseButtons();

        if (serverListener != null) {
            serverListener.attachGameActivityToServerListener(this);
        }
        if (clientListener != null) {
            clientListener.attachActivityToClientListener(this);
        }

        if (!isGameIntialised) {
            Log.d("GameActivity", "onCreate, 1st time initialisation");

            standardTitleColor = getResources().getColor(R.color.HazelGreen);
            standardStatusColor = getResources().getColor(R.color.HazelGreenLight);

            teamVoteTitleColor = getResources().getColor(R.color.MediumPurple);
            teamVoteStatusColor = getResources().getColor(R.color.MediumPurpleLight);
            questVoteTitleColor = getResources().getColor(R.color.SlateGray);
            questVoteStatusColor = getResources().getColor(R.color.LightSlateGray);
            teamSelectTitleColor = getResources().getColor(R.color.MuddyBrown);
            teamSelectStatusColor = getResources().getColor(R.color.MuddyBrownLight);
            assassinateTitleColor = getResources().getColor(R.color.RedWine);
            assassinateStatusColor = getResources().getColor(R.color.WineRedLight);
            ladyOfLakeTitleColor = getResources().getColor(R.color.TiffanyBlueDark);
            ladyOfLakeStatusColor = getResources().getColor(R.color.TiffanyBlue);
            viewCharacterTitleColor = getResources().getColor(R.color.HazelGreen);
            viewCharacterStatusColor = getResources().getColor(R.color.HazelGreenLight);

            gameStatusView = (TextView) findViewById(R.id.textView_gameStateStatus);
            gameActionsView = (TextView) findViewById(R.id.textView_gameActionLabel);

            checkGameStateAndUpdateScreen();
            registerBroadcastReceiverForScreenChange();

//            gameToolbarText = "Quest " + currentQuest.getValue();
//            gameStatusText = "View Character";
        }
        isGameIntialised = true;

        updateTitleAndStatusColor(standardTitleColor, standardStatusColor);
        updateGameStatusText(gameStatusText);
        updateTitleText(gameToolbarText);
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

    @Override
    public void onResume() {
        Log.d("GameActivity", "onResume called");

        if (!ScreenReceiver.wasScreenOn) {
            // this is when onResume() is called due to a screen state change
            Log.d("GameActivity", "onResume SCREEN TURNED ON");

        } else {
            // this is when onResume() is called when the screen state has not changed
            Log.d("GameActivity", "onResume not screen related");

            ApplicationContext myApp = (ApplicationContext) this.getApplication();
            if (myApp.wasInBackground) {
                Log.d("GameActivity", "onResume User has returned to the app!");

                Packet.Packet_PlayerHasReturnedToApp packet = (Packet.Packet_PlayerHasReturnedToApp) PacketFactory.createPack(PacketFactory.PacketType.PLAYER_RETURNED);
                packet.playerName = getUserPlayer().userName;

                client_sendPacketToServer(packet);
            }

            myApp.stopActivityTransitionTimer();
        }
        super.onResume();


        //ApplicationContext.showToast("onStart");
    }

    @Override
    public void onPause() {
        Log.d("GameActivity", "onPause called");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            if (!powerManager.isInteractive()) { //isInteractive has a min API level of 20.
                // this is the case when onPause() is called by the system due to the screen turning off
                Log.d("GameActivity", "onPause SCREEN TURNED OFF");
            } else {
                // this is when onPause() is called when the screen has not turned off
                Log.d("GameActivity", "onPause SCREEN NOT TURNED OFF");
                ((ApplicationContext) this.getApplication()).startActivityTransitionTimer();
            }

        } else {
            if (!powerManager.isScreenOn()) { //isScreenOn has been depreciated from API 20 onwards. My min API level is 16 so this is still needed.
                // this is the case when onPause() is called by the system due to the screen turning off
                Log.d("GameActivity", "onPause SCREEN TURNED OFF");
            } else {
                Log.d("GameActivity", "onPause SCREEN NOT TURNED OFF");
                // This is when onPause() is called when the screen has not turned off
                // Starts a timer task that if not stopped will inform all players this user has left the app
                ((ApplicationContext) this.getApplication()).startActivityTransitionTimer();
            }
        }
        super.onPause();
    }

    public void registerBroadcastReceiverForScreenChange() {
        Log.d("GameActivity", "registerBroadcastReceiverForScreenChange");

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);

        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
    }

    private void initialiseFragments() {
        gameBoardFrag = (GameBoardFragment) getFragmentManager()
                .findFragmentById(R.id.layout_gameBoardFragment);
    }

    public void initialiseButtons() {
//        teamVoteFrag = (Button) findViewById(R.id.button_teamVoteFrag);
//        questVoteFrag = (Button) findViewById(R.id.button_questVoteFrag);
//        teamSelectFrag = (Button) findViewById(R.id.button_teamSelectFrag);
//        assassinateFrag = (Button) findViewById(R.id.button_assassinateFrag);
//        ladyOfLakeFrag = (Button) findViewById(R.id.button_ladyOfLakeFrag);
//        teamVoteResultFrag = (Button) findViewById(R.id.button_teamVoteResultFrag);
//        gameFinishedFrag = (Button) findViewById(R.id.button_gameFinishedFrag);
//        playerCharacterFrag = (Button) findViewById(R.id.button_playerCharacterFrag);
//        questResultFrag = (Button) findViewById(R.id.button_questResultFrag);

        playerCharacterFrag = (LinearLayout) findViewById(R.id.layout_button_playerCharacterFrag);
        teamVoteFrag = (LinearLayout) findViewById(R.id.layout_button_teamVoteFrag);
        questVoteFrag = (LinearLayout) findViewById(R.id.layout_button_questVoteFrag);
        teamSelectFrag = (LinearLayout) findViewById(R.id.layout_button_teamSelectFrag);
        assassinateFrag = (LinearLayout) findViewById(R.id.layout_button_assassinateFrag);
        ladyOfLakeFrag = (LinearLayout) findViewById(R.id.layout_button_ladyOfLakeFrag);
        teamVoteResultFrag = (LinearLayout) findViewById(R.id.layout_button_teamVoteResultFrag);
        gameFinishedFrag = (LinearLayout) findViewById(R.id.layout_button_gameFinishedFrag);
        questResultFrag = (LinearLayout) findViewById(R.id.layout_button_questResultFrag);


        teamSelectFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment newFragment = SelectTeamFragment.newInstance(GameLogicFunctions.calculatePlayersRequiredForQuest(currentBoard, currentQuest), currentQuest.getValue());
                newFragment.show(getFragmentManager(), "teamselectdialog");
            }
        });

        assassinateFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment newFragment = AssassinateFragment.newInstance();
                newFragment.show(getFragmentManager(), "assassinatedialog");
            }
        });

        ladyOfLakeFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentGameState == GameState.LADY_OF_LAKE) {
                    DialogFragment newFragment = LadyOfLakeFragment.newInstance();
                    newFragment.show(getFragmentManager(), "ladyoflakedialog");
                } else {
                    ApplicationContext.showToast("Must be used at the beginning of a Quest!");
                }
            }
        });

        playerCharacterFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment newFragment = PlayerCharacterFragment.newInstance();
                newFragment.show(getFragmentManager(), "playercharacterdialog");
            }
        });

    }

    public void setAllButtonsGone() {
//        final Button teamVoteFrag = (Button) findViewById(R.id.button_teamVoteFrag);
//        final Button questVoteFrag = (Button) findViewById(R.id.button_questVoteFrag);
//        final Button teamSelectFrag = (Button) findViewById(R.id.button_teamSelectFrag);
//        final Button assassinateFrag = (Button) findViewById(R.id.button_assassinateFrag);
//        final Button ladyOfLakeFrag = (Button) findViewById(R.id.button_ladyOfLakeFrag);
//        final Button teamVoteResultFrag = (Button) findViewById(R.id.button_teamVoteResultFrag);
//        final Button gameFinishedFrag = (Button) findViewById(R.id.button_gameFinishedFrag);
//        final Button questResultFrag = (Button) findViewById(R.id.button_questResultFrag);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("GameActivity", "runOnUiThread: setting all buttons(layouts) invisible (not View Character)");

                teamSelectFrag.setVisibility(View.GONE);
                teamVoteFrag.setVisibility(View.GONE);
                teamVoteResultFrag.setVisibility(View.GONE);
                questVoteFrag.setVisibility(View.GONE);
                questResultFrag.setVisibility(View.GONE);
                ladyOfLakeFrag.setVisibility(View.GONE);
                assassinateFrag.setVisibility(View.GONE);
                gameFinishedFrag.setVisibility(View.GONE);
                //stuff that updates ui

            }
        });
    }

    public void setButtonLayoutGone(final LinearLayout buttonLayout) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("GameActivity", "runOnUiThread: setting button(layout) invisible");

                buttonLayout.setVisibility(View.GONE);
            }
        });
    }

    public void setButtonLayoutVisible(final LinearLayout buttonLayout) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("GameActivity", "runOnUiThread: setting button(layout) visible");

                buttonLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    public void checkGameStateAndUpdateScreen() {

        setAllButtonsGone();

        switch (currentGameState) {
            case TEAM_SELECT:
                setTeamSelectState();
                break;
            case TEAM_VOTE:
                setTeamVoteState();
                break;
            case TEAM_VOTE_RESULT:
                setTeamVoteResultState();
                break;
            case QUEST_VOTE:
                setQuestVoteState();
                break;
            case QUEST_VOTE_RESULT:
                setQuestVoteResultState();
                break;
            case LADY_OF_LAKE:
                setLadyOfLakeState();
                break;
            case ASSASSIN:
                setAssassinState();
                break;
            case FINISHED:
                setFinishedState();
                break;

            default:
                Log.d("GameActivity", "Error checking game states!");
        }
    }

    public void setTeamSelectState() {
        Log.d("GameActivity", "setTeamSelectState");

        voteCount++;
        setNextLeader();

        updateTitleText("Quest " + currentQuest.getValue() + " - Vote " + voteCount);

        //TODO fix leader and quest

        if (getCurrentLeaderID() == getUserPlayer().playerID )  { //getUserPlayer().isLeader) {
            updateGameStatusText("Your the Leader (Select a team)");
            setButtonLayoutVisible(teamSelectFrag);
            updateTitleAndStatusColor(teamSelectTitleColor, teamSelectStatusColor);
            updateStandardTitleAndStatusColor(teamSelectTitleColor, teamSelectStatusColor);

        } else {
            updateGameStatusText("Waiting for " + Session.allPlayers.get(getCurrentLeaderID()).userName + " (The Leader) to select a team");
            setButtonLayoutGone(teamSelectFrag);
            updateTitleAndStatusColor(viewCharacterTitleColor, viewCharacterStatusColor);
            updateStandardTitleAndStatusColor(viewCharacterTitleColor, viewCharacterStatusColor);
        }

    }

    public void setTeamVoteState() {
        Log.d("GameActivity", "setTeamVoteState");

        updateGameStatusText("Vote on " + Session.allPlayers.get(getCurrentLeaderID()).userName + "'s selected team");
        updateTitleText("Quest " + currentQuest.getValue() + " - Vote " + voteCount);
        updateTitleAndStatusColor(teamVoteTitleColor, teamVoteStatusColor);
        updateStandardTitleAndStatusColor(teamVoteTitleColor, teamVoteStatusColor);
    }

    public void setTeamVoteResultState() {
        Log.d("GameActivity", "setTeamVoteResultState");

        updateGameStatusText("View Team Vote result");
        updateTitleAndStatusColor(teamVoteTitleColor, teamVoteStatusColor);
        updateStandardTitleAndStatusColor(teamVoteTitleColor, teamVoteStatusColor);
        updateTitleText("Quest " + currentQuest.getValue() + " - Vote " + voteCount);

    }

    public void setQuestVoteState() {
        Log.d("GameActivity", "setQuestVoteState");

        voteCount = 0;
        updateTitleText("Quest " + currentQuest.getValue());

        if (getUserPlayer().isOnQuest) {
            updateGameStatusText("Your on a Quest, please vote");
            updateTitleAndStatusColor(questVoteTitleColor, questVoteStatusColor);
            updateStandardTitleAndStatusColor(questVoteTitleColor, questVoteStatusColor);

        } else {
            updateGameStatusText("Waiting for the Quest Team to finish");
            setButtonLayoutGone(questVoteFrag);
            updateTitleAndStatusColor(viewCharacterTitleColor, viewCharacterStatusColor);
            updateStandardTitleAndStatusColor(viewCharacterTitleColor, viewCharacterStatusColor);
        }
    }

    public void setQuestVoteResultState() {
        Log.d("GameActivity", "setQuestVoteResultState");

        updateGameStatusText("View Quest " + currentQuest.getValue() + " Result");
        setButtonLayoutVisible(questResultFrag);

        updateTitleAndStatusColor(questVoteTitleColor, questVoteStatusColor);
        updateStandardTitleAndStatusColor(questVoteTitleColor, questVoteStatusColor);
    }

    public void setLadyOfLakeState() {
        Log.d("GameActivity", "setLadyOfLakeState");

        updateTitleAndStatusColor(ladyOfLakeTitleColor, ladyOfLakeStatusColor);
        updateStandardTitleAndStatusColor(ladyOfLakeTitleColor, ladyOfLakeStatusColor);
        updateTitleText("Quest " + currentQuest.getValue() + " - End");

        if (getUserPlayer().hasLadyOfLake) {
            updateGameStatusText("Please use the Lady of The Lake token");
            setButtonLayoutVisible(ladyOfLakeFrag);

            DialogFragment newFragment = LadyOfLakeFragment.newInstance(); //TODO change to questNum...
            newFragment.show(getFragmentManager(), "ladyoflakedialog");

        } else {
            updateGameStatusText("Waiting for " + Session.allPlayers.get(getLadyOfLakeHolderID()).userName + " to use the Lady of the Lake token");
//            updateTitleAndStatusColor(viewCharacterTitleColor, viewCharacterStatusColor);
        }
    }

    public void setAssassinState() {
        Log.d("GameActivity", "setAssassinState");

        updateTitleAndStatusColor(assassinateTitleColor, assassinateStatusColor);
        updateStandardTitleAndStatusColor(assassinateTitleColor, assassinateStatusColor);
        updateTitleText("Game End");

        if (getUserPlayer().character instanceof Assassin) {
            updateGameStatusText("Select a player to Assassinate");
            setButtonLayoutVisible(assassinateFrag);
        } else {
            updateGameStatusText("Waiting For The Assassin");
//            updateTitleAndStatusColor(viewCharacterTitleColor, viewCharacterStatusColor);
        }
    }

    public void setFinishedState() {
        Log.d("GameActivity", "setFinishedState");

        updateTitleAndStatusColor(standardTitleColor, standardStatusColor);

        updateGameStatusText("Game Finished");
    }

    public void updateStandardTitleAndStatusColor(int newTitleColor, int newStatusColor) {

        standardTitleColor = newTitleColor;
        standardStatusColor = newStatusColor;
    }

    public static void updateGameStatusText(final String text) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d("GameActivity", "Attempting to update gameStatusText: " + text);

                if (!text.equalsIgnoreCase("")) {
                    gameStatusText = text;
                    gameStatusView.setText(text);
                }
            }
        });
    }

    public void updateTitleText(final String text) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d("GameActivity", "Attempting to update TitleText: " + text);

                if (!text.equalsIgnoreCase("")) {
                    gameToolbarText = text;
                    mainToolbar.setTitle(text);
                }

            }
        });
    }

    public void updateTitleAndStatusColor(final int titleColor, final int statusColor) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d("GameActivity", "Attempting to update Title Color");

                mainToolbar.setBackgroundColor(titleColor);
                gameStatusView.setBackgroundColor(statusColor);
                gameActionsView.setBackgroundColor(statusColor);
            }
        });
    }


    @Override
    public void onVoteSelected(boolean voteResult) {
        Log.d("GameActivity", "Vote Result received from team vote dialog: " + voteResult);

        final Packet.Packet_TeamVoteReply packet = (Packet.Packet_TeamVoteReply) PacketFactory.createPack(PacketFactory.PacketType.TEAM_VOTE_REPLY);
        packet.vote = voteResult;
        packet.playerID = PlayerConnection.getInstance().playerID;

        Log.d("GameActivity", "Sending Packet_TeamVoteReply to Server");
        Session.client_sendPacketToServer(packet);
    }

    @Override
    public void onQuestVoteSelected(boolean voteResult) {
        Log.d("GameActivity", "Vote Result received from quest vote dialog: " + voteResult);

        setButtonLayoutGone(questVoteFrag);

        final Packet.Packet_QuestVoteReply packet = (Packet.Packet_QuestVoteReply) PacketFactory.createPack(PacketFactory.PacketType.QUEST_VOTE_REPLY);
        packet.vote = voteResult;
        packet.playerID = PlayerConnection.getInstance().playerID;

        Log.d("GameActivity", "Sending Packet_QuestVoteReply to Server");
        Session.client_sendPacketToServer(packet);
    }

    @Override
    public void onTeamSelected(int[] teamIndexes) {
        Log.d("GameActivity", "Team selected received from TeamSelect dialog.");

        setButtonLayoutGone(teamSelectFrag);

        final Packet.Packet_SelectTeamReply packet = (Packet.Packet_SelectTeamReply) PacketFactory.createPack(PacketFactory.PacketType.SELECT_TEAM_REPLY);
        packet.teamPos = teamIndexes;
        packet.playerID = PlayerConnection.getInstance().playerID;

        Log.d("GameActivity", "Sending Packet_SelectTeamReply to Server");
        Session.client_sendPacketToServer(packet);
    }

    @Override
    public void onAssassination(boolean isSuccess) {
        Log.d("GameActivity", "OnAssassinate received from Assassinate dialog. Result: " + isSuccess);

        setButtonLayoutGone(assassinateFrag);

        final Packet.Packet_AssassinateReply packet = (Packet.Packet_AssassinateReply) PacketFactory.createPack(PacketFactory.PacketType.ASSASSINATE_REPLY);
        packet.isSuccess = isSuccess;
        packet.playerID = PlayerConnection.getInstance().playerID;

        Log.d("GameActivity", "Sending Packet_AssassinateReply to Server");
        Session.client_sendPacketToServer(packet);
    }

    @Override
    public void ladyOfLakeActivated(int selectedPlayerIndex) {
        Log.d("GameActivity", "LadyOfLakeActivated received from LadyOfLake dialog. Player ID " + selectedPlayerIndex);

        setButtonLayoutGone(ladyOfLakeFrag);

        final Packet.Packet_LadyOfLakeReply packet = (Packet.Packet_LadyOfLakeReply) PacketFactory.createPack(PacketFactory.PacketType.LADYOFLAKE_REPLY);
        packet.selectedPlayerIndex = selectedPlayerIndex;
        packet.playerID = getUserPlayer().playerID;

        Log.d("GameActivity", "Sending Packet_LadyOfLakeReply to Server");
        Session.client_sendPacketToServer(packet);
    }

    @Override
    public void onGameFinishedSelection(boolean playAgain) {
        Log.d("GameActivity", "GameFinished option received from GameFinished dialog. PlayAgain = " + playAgain);

        final Packet.Packet_GameFinishedReply packet = (Packet.Packet_GameFinishedReply) PacketFactory.createPack(PacketFactory.PacketType.GAME_FINISHED_REPLY);
        packet.playAgain = playAgain;
        packet.playerID = PlayerConnection.getInstance().playerID;

        Log.d("GameActivity", "Sending Packet_GameFinishedReply to Server");
        Session.client_sendPacketToServer(packet);
    }

    @Override
    public void onQuestVoteResultRevealed(int voteNumber, boolean voteResult) {
        Log.d("GameActivity", "QuestVoteResultRevealed from QuestResultDialog. Vote Number: " + voteNumber + ", Vote Result: " + voteResult);

        final Packet.Packet_QuestVoteResultRevealed packet = (Packet.Packet_QuestVoteResultRevealed) PacketFactory.createPack(PacketFactory.PacketType.QUESTVOTE_REVEALED);
        packet.voteNumber = voteNumber;
        packet.voteResult = voteResult;
        packet.playerID = PlayerConnection.getInstance().playerID;

        Log.d("GameActivity", "Sending Packet_QuestVoteResultRevealed to Server");
        Session.client_sendPacketToServer(packet);
    }

    @Override
    public void onQuestVoteResultsFinished(boolean result) {
        Log.d("GameActivity", "onQuestVoteResultsFinished from QuestResultDialog. Quest Result: " + result);

        setButtonLayoutGone(questResultFrag);

        if (getCurrentLeaderID() == getUserPlayer().playerID) {

            final Packet.Packet_QuestVoteResultFinished packet = (Packet.Packet_QuestVoteResultFinished) PacketFactory.createPack(PacketFactory.PacketType.QUESTVOTE_FINISHED);
            packet.result = result;
            packet.playerID = PlayerConnection.getInstance().playerID;

            Log.d("GameActivity", "Sending Packet_QuestVoteResultFinished to Server");
            Session.client_sendPacketToServer(packet);
        }
    }

    @Override
    public void server_OnMessagePacketReceived(String message) {
        Log.d("GameActivity", "server_OnMessagePacketReceived from ListenerServer. Message: " + message);

    }

    @Override
    public void server_OnTeamVoteReplyReceived(Packet.Packet_TeamVoteReply voteInfo) {
        Log.d("GameActivity", "server_OnTeamVoteReplyReceived from ListenerServer. Player ID: " + voteInfo.playerID);

        server_teamVoteReplies.add(voteInfo);

        if (server_teamVoteReplies.size() == Session.allPlayers.size()) {

            Packet.Packet_TeamVoteResult packetTeamVoteResult = (Packet.Packet_TeamVoteResult) PacketFactory.createPack(PacketFactory.PacketType.TEAM_VOTE_RESULT);
            packetTeamVoteResult.quest = currentQuest;
            packetTeamVoteResult.voteNumber = voteCount;
            packetTeamVoteResult.playerApprovedPos = getApprovedPlayerPos(server_teamVoteReplies);
            packetTeamVoteResult.playerRejectedPos = getRejectedPlayerPos(server_teamVoteReplies);

            server_teamVoteReplies.clear();

            if (packetTeamVoteResult.playerApprovedPos.length > packetTeamVoteResult.playerRejectedPos.length) {
                packetTeamVoteResult.isApproved = true;

                sendPlayersOnQuest(currentQuest, server_currentProposedTeam);

                Session.server_sendToEveryone(packetTeamVoteResult);

            } else {

                if (voteCount > 4) {

                    endGame(false);//Game Finished, Evil Wins.
                } else {
                    packetTeamVoteResult.isApproved = false;

                    startNewTeamSelectPhase(currentQuest); //needs to be done by clients, send packet

                    Session.server_sendToEveryone(packetTeamVoteResult);
                }
            }

        }
    }

    @Override
    public void server_OnQuestVoteReplyReceived(Packet.Packet_QuestVoteReply voteInfo) {
        Log.d("GameActivity", "server_OnQuestVoteReplyReceived from ListenerServer. Player ID: " + voteInfo.playerID + ", server_questVoteReplies size: " + (server_questVoteReplies.size() + 1));

        server_questVoteReplies.add(voteInfo);

        if (server_questVoteReplies.size() == calculatePlayersRequiredForQuest(currentBoard, currentQuest)) {
            Packet.Packet_QuestVoteResult packet_questVoteResult = (Packet.Packet_QuestVoteResult) PacketFactory.createPack(PacketFactory.PacketType.QUEST_VOTE_RESULT);
            packet_questVoteResult.quest = currentQuest;
            packet_questVoteResult.teamMemberPos = server_currentProposedTeam;
            packet_questVoteResult.votes = getQuestVotesFromPackets(server_questVoteReplies);

            updateAllPlayersGameState(GameState.QUEST_VOTE_RESULT);
            server_sendToEveryone(packet_questVoteResult);
        }

    }

    @Override
    public void server_OnSelectTeamReplyReceived(Packet.Packet_SelectTeamReply teamInfo) {
        Log.d("GameActivity", "server_OnSelectTeamReplyReceived from ListenerServer");

        Packet.Packet_TeamVote packet = (Packet.Packet_TeamVote) PacketFactory.createPack(PacketFactory.PacketType.TEAM_VOTE);
        server_currentProposedTeam = teamInfo.teamPos;
        packet.proposedTeam = server_currentProposedTeam;
        packet.quest = currentQuest;
        packet.voteCount = voteCount;

        server_teamVoteReplies = new ArrayList<>();

        updateAllPlayersGameState(GameState.TEAM_VOTE);
        Session.server_sendToEveryone(packet);
    }

    @Override
    public void server_OnAssassinateReplyReceived(Packet.Packet_AssassinateReply assassinateInfo) {
        Log.d("GameActivity", "server_OnAssassinateReplyReceived from ListenerServer");

        Packet.Packet_GameFinished packet = (Packet.Packet_GameFinished) PacketFactory.createPack(PacketFactory.PacketType.GAME_FINISHED);

        if (assassinateInfo.isSuccess) {
            packet.gameResult = false;
        } else {
            packet.gameResult = true;
        }

        Session.server_sendToEveryone(packet);
    }

    @Override
    public void server_OnLadyOfLakeReplyReceived(Packet.Packet_LadyOfLakeReply ladyOfLakeInfo) {
        Log.d("GameActivity", "server_OnLadyOfLakeReplyReceived from ListenerServer");

        Session.serverAllPlayers.get(ladyOfLakeInfo.playerID).hasUsedLadyOfLake = true;
        Session.serverAllPlayers.get(ladyOfLakeInfo.playerID).hasLadyOfLake = false;
        Session.serverAllPlayers.get(ladyOfLakeInfo.selectedPlayerIndex).hasLadyOfLake = true;

        Packet.Packet_LadyOfLakeUpdate packetLadyOfLakeUpdate = (Packet.Packet_LadyOfLakeUpdate) PacketFactory.createPack(PacketFactory.PacketType.LADYOFLAKE_UPDATE);
        packetLadyOfLakeUpdate.newTokenHolderID = ladyOfLakeInfo.selectedPlayerIndex;
        packetLadyOfLakeUpdate.previousTokenHolderID = ladyOfLakeInfo.playerID;

        server_sendToEveryone(packetLadyOfLakeUpdate);

        startNewQuest(currentQuest.getResult());
        //ServerInstance.server.getServer().sendToAllTCP(packet);
    }

    @Override
    public void server_OnGameFinishedReplyReceived(Packet.Packet_GameFinishedReply gameInfo) {
        Log.d("GameActivity", "server_OnGameFinishedReplyReceived from ListenerServer");

    }

    @Override
    public void server_OnQuestVoteResultRevealedReceived(Packet.Packet_QuestVoteResultRevealed voteInfo) {
        Log.d("GameActivity", "server_OnQuestVoteResultRevealedReceived from ListenerServer");

        ServerInstance.server.getServer().sendToAllExceptTCP(Session.serverAllPlayerConnections.get(voteInfo.playerID).playerConnection.getID(), voteInfo);
    }

    @Override
    public void server_OnQuestVoteResultFinishedReceived(Packet.Packet_QuestVoteResultFinished resultInfo) {
        Log.d("GameActivity", "server_OnQuestVoteResultFinishedReceived from ListenerServer. Result: " + resultInfo.result + ", playerID: " + resultInfo.playerID);

//        if(resultInfo.playerID == getCurrentLeaderID()){
        Log.d("GameActivity", "server_OnQuestVoteResultFinishedReceived from The Leader");

        currentQuest.setResult(resultInfo.result);
        serverQuestResults.add(currentQuest);

        if (checkIfGoodHaveWon(serverQuestResults)) {
            endGame(true);
        } else if (checkIfEvilHaveWon(serverQuestResults)) {
            endGame(false);
        }

        if (serverIsLadyOfLakeOn && currentQuest != Quest.FIRST && currentQuest != Quest.FIFTH) {
            updateAllPlayersGameState(GameState.LADY_OF_LAKE);
        } else {
            startNewQuest(resultInfo.result);
        }

        //Save result + check if game is finished + if not start new quest...etc...

    }

    @Override
    public void client_OnMessagePacketReceived(String message) {
        Log.d("GameActivity", "client_OnMessagePacketReceived from ListenerClient");


    }

    @Override
    public void client_OnUpdateGameStateReceived(Packet.Packet_UpdateGameState nextGameStateInfo) {
        Log.d("GameActivity", "client_OnUpdateGameStateReceived from ListenerClient");
        currentGameState = nextGameStateInfo.nextGameState;
        checkGameStateAndUpdateScreen();
    }

    @Override
    public void client_OnTeamVoteReceived(final Packet.Packet_TeamVote voteInfo) {
        Log.d("GameActivity", "client_OnTeamVoteReceived from ListenerClient");

        setButtonLayoutVisible(teamVoteFrag);

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

        setButtonLayoutVisible(teamVoteResultFrag);

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

        setButtonLayoutVisible(questVoteFrag);

        updateGameStatusText("Your on a Quest, please vote");
        updateTitleAndStatusColor(questVoteTitleColor, questVoteStatusColor);
        updateStandardTitleAndStatusColor(questVoteTitleColor, questVoteStatusColor);

        final DialogFragment newFragment = QuestVoteFragment.newInstance(questInfo.teamMemberPos, GameLogicFunctions.getUserPlayer().character.getAllegiance(), questInfo.quest.getValue());

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
    public void client_OnQuestVoteResultReceived(Packet.Packet_QuestVoteResult questResultInfo) {
        Log.d("GameActivity", "client_OnQuestVoteResultReceived from ListenerClient");

        setButtonLayoutVisible(questResultFrag);

        final DialogFragment newFragment = QuestResultFragment.newInstance(questResultInfo.teamMemberPos, questResultInfo.votes, questResultInfo.quest, GameLogicFunctions.calculateFailRequiredForQuest(currentBoard, questResultInfo.quest));

        questResultFrag.setOnClickListener(null);
        questResultFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("GameActivity", "questVoteFrag clicked, Starting questresultdialog.");

                newFragment.show(getFragmentManager(), "questresultdialog");
            }
        });

        newFragment.show(getFragmentManager(), "questresultdialog");
    }

    @Override
    public void client_OnQuestVoteResultRevealed(final Packet.Packet_QuestVoteResultRevealed voteInfo) {
        Log.d("GameActivity", "client_OnQuestVoteResultRevealed from ListenerClient. VoteNumber: " + voteInfo.voteNumber + " voteResult: " + voteInfo.voteResult);

        final QuestResultFragment fragment = (QuestResultFragment) getFragmentManager().findFragmentByTag("questresultdialog");

        if (fragment != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("GameActivity", "runOnUiThread: QuestResultFragment.showVote(...) ");

                    fragment.showVote(voteInfo.voteNumber, voteInfo.voteResult);
                }
            });
        }
    }

    @Override
    public void client_OnLadyOfLakeUpdateReceived(Packet.Packet_LadyOfLakeUpdate updateInfo) {
        Log.d("GameActivity", "client_OnLadyOfLakeUpdateReceived from ListenerClient. New Holder ID: " + updateInfo.newTokenHolderID + ", Old holder ID: " + updateInfo.previousTokenHolderID);

        Session.allPlayers.get(updateInfo.newTokenHolderID).hasLadyOfLake = true;
        Session.allPlayers.get(updateInfo.previousTokenHolderID).hasLadyOfLake = false;
        Session.allPlayers.get(updateInfo.previousTokenHolderID).hasUsedLadyOfLake = true;

        if (updateInfo.newTokenHolderID == getUserPlayer().playerID) {
            ApplicationContext.showToast(Session.allPlayers.get(updateInfo.previousTokenHolderID).userName + " has used the Lady of The Lake Token on you");
            ApplicationContext.showToast(Session.allPlayers.get(updateInfo.previousTokenHolderID).userName + " now knows your allegiance");
        }
    }

    @Override
    public void client_OnSelectTeamReceived(Packet.Packet_SelectTeam questInfo) {
        Log.d("GameActivity", "client_OnSelectTeamReceived from ListenerClient");

        setButtonLayoutVisible(teamSelectFrag);

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

    @Override
    public void client_OnStartNextQuestReceived(Packet.Packet_StartNextQuest previousQuestResult) {
        Log.d("GameActivity", "client_OnStartNextQuestReceived from ListenerClient");

        currentQuest.setResult(previousQuestResult.previousQuestResult);
        clientQuestResults.add(currentQuest);

        currentQuest = getNextQuest(currentQuest);

        updateTitleText("Quest " + currentQuest.getValue() + " - Vote " + 1);

        // checkGameStateAndUpdateScreen(); //TODO check if this works

    }

    @Override
    public void client_OnGameFinishedReceived(final Packet.Packet_GameFinished gameResultInfo) {
        Log.d("GameActivity", "client_OnGameFinishedReceived from ListenerClient");

        setButtonLayoutVisible(gameFinishedFrag);

        final DialogFragment newFragment = GameFinishedFragment.newInstance(gameResultInfo.gameResult, getEvilAllegiancePositions(), getGoodAllegiancePositions());

        gameFinishedFrag.setOnClickListener(null);
        gameFinishedFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("GameActivity", "teamSelectFrag clicked, Starting gamefinisheddialog.");

                newFragment.show(getFragmentManager(), "gamefinisheddialog");
            }
        });

        newFragment.show(getFragmentManager(), "gamefinisheddialog");
    }
}
