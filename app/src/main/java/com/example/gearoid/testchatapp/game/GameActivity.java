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

    //Constants
    public static final String TAG = "GameActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Log.d(TAG, "onCreate");

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.game_toolbar);
            setSupportActionBar(toolbar);

            ActionBar actionBar = getSupportActionBar();
            actionBar.hide();
        } else {
            mainToolbar = (Toolbar) findViewById(R.id.game_toolbar);
            setSupportActionBar(mainToolbar);
        }

        initialiseFragments();
        initialiseButtons();

        if (serverListener != null) {
            serverListener.attachGameActivityToServerListener(this);
        }
        if (clientListener != null) {
            clientListener.attachActivityToClientListener(this);
        }

        gameStatusView = (TextView) findViewById(R.id.textView_gameStateStatus);
        gameActionsView = (TextView) findViewById(R.id.textView_gameActionLabel);

        if (!isGameIntialised) {
            Log.d(TAG, "onCreate, 1st time initialisation");

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

            checkGameStateAndUpdateScreen();
            registerBroadcastReceiverForScreenChange();

        }
        isGameIntialised = true;

        updateTitleAndStatusColor(standardTitleColor, standardStatusColor);
        updateGameStatusText(gameStatusText);
        updateTitleText(gameToolbarText);
    }


    @Override
    public void onResume() {
        Log.d(TAG, "onResume called");

        if (!ScreenReceiver.wasScreenOn) {
            // this is when onResume() is called due to a screen state change
            Log.d(TAG, "onResume SCREEN TURNED ON");

        } else {
            // this is when onResume() is called when the screen state has not changed
            Log.d(TAG, "onResume not screen related");

            ApplicationContext myApp = (ApplicationContext) this.getApplication();
            if (myApp.wasInBackground) {
                Log.d(TAG, "onResume User has returned to the app!");

                Packet.Packet_PlayerHasReturnedToApp packet = (Packet.Packet_PlayerHasReturnedToApp) PacketFactory.createPack(PacketFactory.PacketType.PLAYER_RETURNED);
                packet.playerName = getUserPlayer().userName;

                client_sendPacketToServer(packet);
            }

            myApp.stopActivityTransitionTimer();
        }

        registerBroadcastReceiverForScreenChange();
        super.onResume();


    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause called");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            if (!powerManager.isInteractive()) { //isInteractive has a min API level of 20.
                // this is the case when onPause() is called by the system due to the screen turning off
                Log.d(TAG, "onPause SCREEN TURNED OFF");
            } else {
                // this is when onPause() is called when the screen has not turned off
                Log.d(TAG, "onPause SCREEN NOT TURNED OFF");
                ((ApplicationContext) this.getApplication()).startActivityTransitionTimer();
            }

        } else {
            if (!powerManager.isScreenOn()) { //isScreenOn has been depreciated from API 20 onwards. My min API level is 16 so this is still needed.
                // this is the case when onPause() is called by the system due to the screen turning off
                Log.d(TAG, "onPause SCREEN TURNED OFF");
            } else {
                Log.d(TAG, "onPause SCREEN NOT TURNED OFF");
                // This is when onPause() is called when the screen has not turned off
                // Starts a timer task that if not stopped will inform all players this user has left the app
                ((ApplicationContext) this.getApplication()).startActivityTransitionTimer();
            }
        }
        unregisterReceiver(Session.mReceiver);
        super.onPause();
    }

    public void registerBroadcastReceiverForScreenChange() {
        Log.d(TAG, "registerBroadcastReceiverForScreenChange");

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter); //TODO implement unregister() in on pause or on destroy....

        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
    }

    private void initialiseFragments() {
        gameBoardFrag = (GameBoardFragment) getFragmentManager()
                .findFragmentById(R.id.layout_gameBoardFragment);
    }

    public void initialiseButtons() {

        playerCharacterFrag = (LinearLayout) findViewById(R.id.layout_button_playerCharacterFrag);
        teamVoteFrag = (LinearLayout) findViewById(R.id.layout_button_teamVoteFrag);
        questVoteFrag = (LinearLayout) findViewById(R.id.layout_button_questVoteFrag);
        teamSelectFrag = (LinearLayout) findViewById(R.id.layout_button_teamSelectFrag);
        assassinateFrag = (LinearLayout) findViewById(R.id.layout_button_assassinateFrag);
        ladyOfLakeFrag = (LinearLayout) findViewById(R.id.layout_button_ladyOfLakeFrag);
        teamVoteResultFrag = (LinearLayout) findViewById(R.id.layout_button_teamVoteResultFrag);
        gameFinishedFrag = (LinearLayout) findViewById(R.id.layout_button_gameFinishedFrag);
        questResultFrag = (LinearLayout) findViewById(R.id.layout_button_questResultFrag);

        if (isTeamSelectFragVisible) {
            teamSelectFrag.setVisibility(View.VISIBLE);
        }
        if (isTeamVoteFragVisible) {
            teamVoteFrag.setVisibility(View.VISIBLE);
        }
        if (isTeamVoteResultFragVisible) {
            teamVoteResultFrag.setVisibility(View.VISIBLE);
        }
        if (isQuestVoteFragVisible) {
            questVoteFrag.setVisibility(View.VISIBLE);
        }
        if (isQuestResultFragVisible) {
            questResultFrag.setVisibility(View.VISIBLE);
        }
        if (isLadyOfLakeFragVisible) {
            ladyOfLakeFrag.setVisibility(View.VISIBLE);
        }
        if (isAssassinateFragVisible) {
            assassinateFrag.setVisibility(View.VISIBLE);
        }
        if (isGameFinishedFragVisible) {
            gameFinishedFrag.setVisibility(View.VISIBLE);
        }

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

        isTeamVoteFragVisible = false;
        isQuestVoteFragVisible = false;
        isTeamSelectFragVisible = false;
        isAssassinateFragVisible = false;
        isLadyOfLakeFragVisible = false;
        isTeamVoteResultFragVisible = false;
        isGameFinishedFragVisible = false;
        isQuestResultFragVisible = false;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "runOnUiThread: setting all buttons(layouts) invisible (not View Character)");

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
                Log.d(TAG, "runOnUiThread: setting button(layout) invisible");

                buttonLayout.setVisibility(View.GONE);
            }
        });
    }

    public void setButtonLayoutVisible(final LinearLayout buttonLayout) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "runOnUiThread: setting button(layout) visible");

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

            case QUEST_VOTE:
                setQuestVoteState();
                break;

            case LADY_OF_LAKE:
                setLadyOfLakeState();
                break;
            case ASSASSIN:
                setAssassinState();
                break;

            default:
                Log.d(TAG, "Error checking game states!");
        }
    }

    public void setTeamSelectState() {
        Log.d(TAG, "setTeamSelectState");

        voteCount++;
        setNextLeader();

        updateTitleText("Quest " + currentQuest.getValue() + " - Vote " + voteCount);


        if (getCurrentLeaderID() == getUserPlayer().playerID) { //getUserPlayer().isLeader) {
            updateGameStatusText("Your the Leader (Select a team)");
            setButtonLayoutVisible(teamSelectFrag);
            isTeamSelectFragVisible = true;
            updateTitleAndStatusColor(teamSelectTitleColor, teamSelectStatusColor);
            updateStandardTitleAndStatusColor(teamSelectTitleColor, teamSelectStatusColor);

        } else {
            updateGameStatusText("Waiting for " + Session.allPlayers.get(getCurrentLeaderID()).userName + " (The Leader) to select a team");

            updateTitleAndStatusColor(viewCharacterTitleColor, viewCharacterStatusColor);
            updateStandardTitleAndStatusColor(viewCharacterTitleColor, viewCharacterStatusColor);
        }

    }

//    public void setTeamVoteState() {
//        Log.d(TAG, "setTeamVoteState");
//
////        updateGameStatusText("Vote on " + Session.allPlayers.get(getCurrentLeaderID()).userName + "'s selected team");
////        updateTitleText("Quest " + currentQuest.getValue() + " - Vote " + voteCount);
////        updateTitleAndStatusColor(teamVoteTitleColor, teamVoteStatusColor);
////        updateStandardTitleAndStatusColor(teamVoteTitleColor, teamVoteStatusColor);
//    }

//    public void setTeamVoteResultState() {
//        Log.d(TAG, "setTeamVoteResultState");
//
////        updateGameStatusText("View Team Vote result");
////        updateTitleAndStatusColor(teamVoteTitleColor, teamVoteStatusColor);
////        updateStandardTitleAndStatusColor(teamVoteTitleColor, teamVoteStatusColor);
////        updateTitleText("Quest " + currentQuest.getValue() + " - Vote " + voteCount);
//
//    }

    public void setQuestVoteState() {
        Log.d(TAG, "setQuestVoteState");

//        voteCount = 0;
//        updateTitleText("Quest " + currentQuest.getValue());
//
//        if (getUserPlayer().isOnQuest) {
//            updateGameStatusText("You have been selected to go on a Quest");
//            updateTitleAndStatusColor(questVoteTitleColor, questVoteStatusColor);
//            updateStandardTitleAndStatusColor(questVoteTitleColor, questVoteStatusColor);
//
//        } else {
//            updateGameStatusText("Waiting for the Quest Team to finish");
//            //setButtonLayoutGone(questVoteFrag);
//            updateTitleAndStatusColor(viewCharacterTitleColor, viewCharacterStatusColor);
//            updateStandardTitleAndStatusColor(viewCharacterTitleColor, viewCharacterStatusColor);
//        }
    }

//    public void setQuestVoteResultState() {
//        Log.d(TAG, "setQuestVoteResultState");
//
////        updateGameStatusText("View Quest " + currentQuest.getValue() + " Result");
////        setButtonLayoutVisible(questResultFrag);
////
////        updateTitleAndStatusColor(questVoteTitleColor, questVoteStatusColor);
////        updateStandardTitleAndStatusColor(questVoteTitleColor, questVoteStatusColor);
//    }

    public void setLadyOfLakeState() {
        Log.d(TAG, "setLadyOfLakeState");

        updateTitleAndStatusColor(ladyOfLakeTitleColor, ladyOfLakeStatusColor);
        updateStandardTitleAndStatusColor(ladyOfLakeTitleColor, ladyOfLakeStatusColor);
        updateTitleText("Quest " + currentQuest.getValue() + " - End");

        if (getUserPlayer().hasLadyOfLake) {
            updateGameStatusText("Please use the Lady of The Lake token");
            setButtonLayoutVisible(ladyOfLakeFrag);
            isLadyOfLakeFragVisible = true;

            DialogFragment newFragment = LadyOfLakeFragment.newInstance();
            newFragment.show(getFragmentManager(), "ladyoflakedialog");

        } else {
            updateGameStatusText("Waiting for " + Session.allPlayers.get(getLadyOfLakeHolderID()).userName + " to use the Lady of the Lake token");
//            updateTitleAndStatusColor(viewCharacterTitleColor, viewCharacterStatusColor);
        }
    }

    public void setAssassinState() {
        Log.d(TAG, "setAssassinState");

        updateTitleAndStatusColor(assassinateTitleColor, assassinateStatusColor);
        updateStandardTitleAndStatusColor(assassinateTitleColor, assassinateStatusColor);
        updateTitleText("Game End");

        if (getUserPlayer().character instanceof Assassin) {
            updateGameStatusText("Select a player to Assassinate");
            setButtonLayoutVisible(assassinateFrag);
            isAssassinateFragVisible = true;
        } else {
            updateGameStatusText("Waiting For The Assassin");
//            updateTitleAndStatusColor(viewCharacterTitleColor, viewCharacterStatusColor);
        }
    }

//    public void setFinishedState() {
//        Log.d(TAG, "setFinishedState");
//
//        updateTitleAndStatusColor(standardTitleColor, standardStatusColor);
//
//        updateGameStatusText("Game Finished");
//    }

    public void updateStandardTitleAndStatusColor(int newTitleColor, int newStatusColor) {

        standardTitleColor = newTitleColor;
        standardStatusColor = newStatusColor;
    }

    public static void updateGameStatusText(final String text) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Attempting to update gameStatusText: " + text);

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
                Log.d(TAG, "Attempting to update TitleText: " + text);

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
                Log.d(TAG, "Attempting to update Title Color");

                mainToolbar.setBackgroundColor(titleColor);
                gameStatusView.setBackgroundColor(statusColor);
                gameActionsView.setBackgroundColor(statusColor);
            }
        });
    }


    @Override
    public void onVoteSelected(boolean voteResult) {
        Log.d(TAG, "Vote Result received from team vote dialog: " + voteResult);

        final Packet.Packet_TeamVoteReply packet = (Packet.Packet_TeamVoteReply) PacketFactory.createPack(PacketFactory.PacketType.TEAM_VOTE_REPLY);
        packet.vote = voteResult;
        packet.playerID = PlayerConnection.getInstance().playerID;

        Log.d(TAG, "Sending Packet_TeamVoteReply to Server");
        Session.client_sendPacketToServer(packet);
    }

    @Override
    public void onQuestVoteSelected(boolean voteResult) {
        Log.d(TAG, "Vote Result received from quest vote dialog: " + voteResult);

        setButtonLayoutGone(questVoteFrag);
        isQuestVoteFragVisible = false;

        updateGameStatusText("Waiting for team members to finish Quest");

        final Packet.Packet_QuestVoteReply packet = (Packet.Packet_QuestVoteReply) PacketFactory.createPack(PacketFactory.PacketType.QUEST_VOTE_REPLY);
        packet.vote = voteResult;
        packet.playerID = PlayerConnection.getInstance().playerID;

        Log.d(TAG, "Sending Packet_QuestVoteReply to Server");
        Session.client_sendPacketToServer(packet);
    }

    @Override
    public void onTeamSelected(int[] teamIndexes) {
        Log.d(TAG, "Team selected received from TeamSelect dialog.");

        setButtonLayoutGone(teamSelectFrag);
        isTeamSelectFragVisible = false;

        final Packet.Packet_SelectTeamReply packet = (Packet.Packet_SelectTeamReply) PacketFactory.createPack(PacketFactory.PacketType.SELECT_TEAM_REPLY);
        packet.teamPos = teamIndexes;
        packet.playerID = PlayerConnection.getInstance().playerID;

        Log.d(TAG, "Sending Packet_SelectTeamReply to Server");
        Session.client_sendPacketToServer(packet);
    }

    @Override
    public void onAssassination(boolean isSuccess) {
        Log.d(TAG, "OnAssassinate received from Assassinate dialog. Result: " + isSuccess);

        setButtonLayoutGone(assassinateFrag);
        isAssassinateFragVisible = false;

        final Packet.Packet_AssassinateReply packet = (Packet.Packet_AssassinateReply) PacketFactory.createPack(PacketFactory.PacketType.ASSASSINATE_REPLY);
        packet.isSuccess = isSuccess;
        packet.playerID = PlayerConnection.getInstance().playerID;

        Log.d(TAG, "Sending Packet_AssassinateReply to Server");
        Session.client_sendPacketToServer(packet);
    }

    @Override
    public void ladyOfLakeActivated(int selectedPlayerIndex) {
        Log.d(TAG, "LadyOfLakeActivated received from LadyOfLake dialog. Player ID " + selectedPlayerIndex);

        setButtonLayoutGone(ladyOfLakeFrag);
        isLadyOfLakeFragVisible = false;

        final Packet.Packet_LadyOfLakeReply packet = (Packet.Packet_LadyOfLakeReply) PacketFactory.createPack(PacketFactory.PacketType.LADYOFLAKE_REPLY);
        packet.selectedPlayerIndex = selectedPlayerIndex;
        packet.playerID = getUserPlayer().playerID;

        Log.d(TAG, "Sending Packet_LadyOfLakeReply to Server");
        Session.client_sendPacketToServer(packet);
    }

    @Override
    public void onGameFinishedSelection(boolean playAgain) {
        Log.d(TAG, "GameFinished option received from GameFinished dialog. PlayAgain = " + playAgain);

        final Packet.Packet_GameFinishedReply packet = (Packet.Packet_GameFinishedReply) PacketFactory.createPack(PacketFactory.PacketType.GAME_FINISHED_REPLY);
        packet.playAgain = playAgain;
        packet.playerID = PlayerConnection.getInstance().playerID;

        Log.d(TAG, "Sending Packet_GameFinishedReply to Server");
        Session.client_sendPacketToServer(packet);
    }

    @Override
    public void onQuestVoteResultRevealed(int voteNumber, boolean voteResult) {
        Log.d(TAG, "QuestVoteResultRevealed from QuestResultDialog. Vote Number: " + voteNumber + ", Vote Result: " + voteResult);

        final Packet.Packet_QuestVoteResultRevealed packet = (Packet.Packet_QuestVoteResultRevealed) PacketFactory.createPack(PacketFactory.PacketType.QUESTVOTE_REVEALED);
        packet.voteNumber = voteNumber;
        packet.voteResult = voteResult;
        packet.playerID = PlayerConnection.getInstance().playerID;

        Log.d(TAG, "Sending Packet_QuestVoteResultRevealed to Server");
        Session.client_sendPacketToServer(packet);
    }

    @Override
    public void onQuestVoteResultsFinished(boolean result) {
        Log.d(TAG, "onQuestVoteResultsFinished from QuestResultDialog. Quest Result: " + result);

        setButtonLayoutGone(questResultFrag);
        isQuestResultFragVisible = false;

        if (getCurrentLeaderID() == getUserPlayer().playerID) {

            final Packet.Packet_QuestVoteResultFinished packet = (Packet.Packet_QuestVoteResultFinished) PacketFactory.createPack(PacketFactory.PacketType.QUESTVOTE_FINISHED);
            packet.result = result;
            packet.playerID = PlayerConnection.getInstance().playerID;

            Log.d(TAG, "Sending Packet_QuestVoteResultFinished to Server");
            Session.client_sendPacketToServer(packet);
        }
    }

    @Override
    public void server_OnMessagePacketReceived(String message) {
        Log.d(TAG, "server_OnMessagePacketReceived from ListenerServer. Message: " + message);

    }

    @Override
    public void server_OnTeamVoteReplyReceived(Packet.Packet_TeamVoteReply voteInfo) {
        Log.d(TAG, "server_OnTeamVoteReplyReceived from ListenerServer. Player ID: " + voteInfo.playerID);

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

                //updateAllPlayersGameState(GameState.QUEST_VOTE);
                sendPlayersOnQuest(currentQuest, server_currentProposedTeam);

                //Session.server_sendToEveryone();
                Session.server_sendToEveryone(packetTeamVoteResult);

            } else {

                if (voteCount > 4) {

                    endGame(false);//Game Finished, Evil Wins.
                } else {
                    packetTeamVoteResult.isApproved = false;

                    startNewTeamSelectPhase(currentQuest);

                    Session.server_sendToEveryone(packetTeamVoteResult);
                }
            }

        }
    }

    @Override
    public void server_OnQuestVoteReplyReceived(Packet.Packet_QuestVoteReply voteInfo) {
        Log.d(TAG, "server_OnQuestVoteReplyReceived from ListenerServer. Player ID: " + voteInfo.playerID + ", server_questVoteReplies size: " + (server_questVoteReplies.size() + 1));

        server_questVoteReplies.add(voteInfo);

        if (server_questVoteReplies.size() == calculatePlayersRequiredForQuest(currentBoard, currentQuest)) {
            Packet.Packet_QuestVoteResult packet_questVoteResult = (Packet.Packet_QuestVoteResult) PacketFactory.createPack(PacketFactory.PacketType.QUEST_VOTE_RESULT);
            packet_questVoteResult.quest = currentQuest;
            packet_questVoteResult.teamMemberPos = server_currentProposedTeam;
            packet_questVoteResult.votes = getQuestVotesFromPackets(server_questVoteReplies);

            // updateAllPlayersGameState(GameState.QUEST_VOTE_RESULT);
            server_sendToEveryone(packet_questVoteResult);
        }

    }

    @Override
    public void server_OnSelectTeamReplyReceived(Packet.Packet_SelectTeamReply teamInfo) {
        Log.d(TAG, "server_OnSelectTeamReplyReceived from ListenerServer");

        Packet.Packet_TeamVote packet = (Packet.Packet_TeamVote) PacketFactory.createPack(PacketFactory.PacketType.TEAM_VOTE);
        server_currentProposedTeam = teamInfo.teamPos;
        packet.proposedTeam = server_currentProposedTeam;
        packet.quest = currentQuest;
        packet.voteCount = voteCount;

        server_teamVoteReplies = new ArrayList<>();

        // updateAllPlayersGameState(GameState.TEAM_VOTE);
        Session.server_sendToEveryone(packet);
    }

    @Override
    public void server_OnAssassinateReplyReceived(Packet.Packet_AssassinateReply assassinateInfo) {
        Log.d(TAG, "server_OnAssassinateReplyReceived from ListenerServer");

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
        Log.d(TAG, "server_OnLadyOfLakeReplyReceived from ListenerServer. New holder Name: " + Session.allPlayers.get(ladyOfLakeInfo.selectedPlayerIndex).userName + ", Old player Name: " + Session.allPlayers.get(ladyOfLakeInfo.playerID).userName);

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
        Log.d(TAG, "server_OnGameFinishedReplyReceived from ListenerServer");


    }

    @Override
    public void server_OnQuestVoteResultRevealedReceived(Packet.Packet_QuestVoteResultRevealed voteInfo) {
        Log.d(TAG, "server_OnQuestVoteResultRevealedReceived from ListenerServer");

        ServerInstance.server.getServer().sendToAllExceptTCP(Session.serverAllPlayerConnections.get(voteInfo.playerID).playerConnection.getID(), voteInfo);
    }

    @Override
    public void server_OnQuestVoteResultFinishedReceived(Packet.Packet_QuestVoteResultFinished resultInfo) {
        Log.d(TAG, "server_OnQuestVoteResultFinishedReceived from ListenerServer. Result: " + resultInfo.result + ", playerID: " + resultInfo.playerID);

//        if(resultInfo.playerID == getCurrentLeaderID()){
        Log.d(TAG, "server_OnQuestVoteResultFinishedReceived from The Leader");

        currentQuest.setResult(resultInfo.result);
        serverQuestResults.add(currentQuest);

        if (checkIfGoodHaveWon(serverQuestResults)) {
            endGame(true);
        } else if (checkIfEvilHaveWon(serverQuestResults)) {
            endGame(false);
        } else {
            if (serverIsLadyOfLakeOn && currentQuest != Quest.FIRST && currentQuest != Quest.FIFTH) {
                updateAllPlayersGameState(GameState.LADY_OF_LAKE);
            } else {
                startNewQuest(resultInfo.result);
            }
        }

    }

    @Override
    public void client_OnMessagePacketReceived(String message) {//For test purposes
        Log.d(TAG, "client_OnMessagePacketReceived from ListenerClient: Message: " + message);


    }

    @Override
    public void client_OnUpdateGameStateReceived(Packet.Packet_UpdateGameState nextGameStateInfo) {
        Log.d(TAG, "client_OnUpdateGameStateReceived from ListenerClient");
        currentGameState = nextGameStateInfo.nextGameState;
        checkGameStateAndUpdateScreen();
    }

    @Override
    public void client_OnTeamVoteReceived(final Packet.Packet_TeamVote voteInfo) {
        Log.d(TAG, "client_OnTeamVoteReceived from ListenerClient");

        updateGameStatusText("Vote on " + Session.allPlayers.get(getCurrentLeaderID()).userName + "'s selected team");
        updateTitleText("Quest " + voteInfo.quest.getValue() + " - Vote " + voteInfo.voteCount);
        updateTitleAndStatusColor(teamVoteTitleColor, teamVoteStatusColor);
        updateStandardTitleAndStatusColor(teamVoteTitleColor, teamVoteStatusColor);

        setButtonLayoutVisible(teamVoteFrag);
        isTeamVoteFragVisible = true;

        final DialogFragment newFragment = TeamVoteFragment.newInstance(voteInfo.proposedTeam, voteInfo.quest.getValue(), voteInfo.voteCount); //TODO change to questNum...

        teamVoteFrag.setOnClickListener(null);
        teamVoteFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "TeamVoteFrag clicked, Starting teamVoteDialog.");

                newFragment.show(getFragmentManager(), "teamvotedialog");
            }
        });

        newFragment.show(getFragmentManager(), "teamvotedialog");
    }

    @Override
    public void client_OnTeamVoteResultReceived(final Packet.Packet_TeamVoteResult voteResultInfo) {
        Log.d(TAG, "client_OnTeamVoteResultReceived from ListenerClient");


        if (currentGameState != GameState.QUEST_VOTE && currentGameState != GameState.TEAM_SELECT) {
            updateGameStatusText("View Team Vote result");
            updateTitleAndStatusColor(teamVoteTitleColor, teamVoteStatusColor);
            updateStandardTitleAndStatusColor(teamVoteTitleColor, teamVoteStatusColor);
            updateTitleText("Quest " + voteResultInfo.quest.getValue() + " - Vote " + voteResultInfo.voteNumber);
        }

        setButtonLayoutVisible(teamVoteResultFrag);
        isTeamVoteResultFragVisible = true;

        final DialogFragment newFragment = TeamVoteResultFragment.newInstance(voteResultInfo.isApproved, voteResultInfo.playerApprovedPos, voteResultInfo.playerRejectedPos, voteResultInfo.quest.getValue(), voteResultInfo.voteNumber); //TODO change to questNum...

        teamVoteResultFrag.setOnClickListener(null);
        teamVoteResultFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "TeamVoteResultFrag clicked, Starting teamVoteResultDialog.");

                newFragment.show(getFragmentManager(), "teamvoteresultdialog");
            }
        });

        newFragment.show(getFragmentManager(), "teamvoteresultdialog");
    }

    @Override
    public void client_OnQuestVoteReceived(Packet.Packet_QuestVote questInfo) {
        Log.d(TAG, "client_OnQuestVoteReceived from ListenerClient");

        voteCount = 0;
        updateTitleText("Quest " + questInfo.quest.getValue());

        if(isUserInTeam(questInfo.teamMemberPos)){
            setButtonLayoutVisible(questVoteFrag);
            isQuestVoteFragVisible = true;

            updateGameStatusText("You have been selected to go on a Quest");
            updateTitleAndStatusColor(questVoteTitleColor, questVoteStatusColor);
            updateStandardTitleAndStatusColor(questVoteTitleColor, questVoteStatusColor);

            final DialogFragment newFragment = QuestVoteFragment.newInstance(questInfo.teamMemberPos, GameLogicFunctions.getUserPlayer().character.getAllegiance(), questInfo.quest.getValue());

            questVoteFrag.setOnClickListener(null);
            questVoteFrag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "questVoteFrag clicked, Starting questdialog.");

                    newFragment.show(getFragmentManager(), "questdialog");
                }
            });

            newFragment.show(getFragmentManager(), "questdialog");
        } else {
            isQuestVoteFragVisible = false;
            updateGameStatusText("Waiting for the Quest Team to finish");
            updateTitleAndStatusColor(viewCharacterTitleColor, viewCharacterStatusColor);
            updateStandardTitleAndStatusColor(viewCharacterTitleColor, viewCharacterStatusColor);

        }
    }

    public boolean isUserInTeam(int[] teamMemberPos){
        Log.d(TAG, "isUserInTeam");

        boolean isMember = false;

        for(int i=0; i < teamMemberPos.length; i++){
            Log.d(TAG, "isUserInTeam. teamMemberPos: " + teamMemberPos[i] + " playerID: " + getUserPlayer().playerID);

            if(teamMemberPos[i] == getUserPlayer().playerID){
                isMember = true;
            }
        }

        return isMember;
    }

    @Override
    public void client_OnQuestVoteResultReceived(Packet.Packet_QuestVoteResult questResultInfo) {
        Log.d(TAG, "client_OnQuestVoteResultReceived from ListenerClient");

        updateGameStatusText("View Quest " + questResultInfo.quest.getValue() + " Result");
        setButtonLayoutVisible(questResultFrag);
        isQuestResultFragVisible = true;

        updateTitleAndStatusColor(questVoteTitleColor, questVoteStatusColor);
        updateStandardTitleAndStatusColor(questVoteTitleColor, questVoteStatusColor);

        final DialogFragment newFragment = QuestResultFragment.newInstance(questResultInfo.teamMemberPos, questResultInfo.votes, questResultInfo.quest, GameLogicFunctions.calculateFailRequiredForQuest(currentBoard, questResultInfo.quest));

        questResultFrag.setOnClickListener(null);
        questResultFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "questVoteFrag clicked, Starting questresultdialog.");

                newFragment.show(getFragmentManager(), "questresultdialog");
            }
        });

        newFragment.show(getFragmentManager(), "questresultdialog");
    }

    @Override
    public void client_OnQuestVoteResultRevealed(final Packet.Packet_QuestVoteResultRevealed voteInfo) {
        Log.d(TAG, "client_OnQuestVoteResultRevealed from ListenerClient. VoteNumber: " + voteInfo.voteNumber + " voteResult: " + voteInfo.voteResult);

        final QuestResultFragment fragment = (QuestResultFragment) getFragmentManager().findFragmentByTag("questresultdialog");

        if (fragment != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "runOnUiThread: QuestResultFragment.showVote(...) ");

                    fragment.showVote(voteInfo.voteNumber, voteInfo.voteResult);
                }
            });
        }
    }

    @Override
    public void client_OnLadyOfLakeUpdateReceived(Packet.Packet_LadyOfLakeUpdate updateInfo) {
        Log.d(TAG, "client_OnLadyOfLakeUpdateReceived from ListenerClient. New Holder ID: " + updateInfo.newTokenHolderID + ", Old holder ID: " + updateInfo.previousTokenHolderID);

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
        Log.d(TAG, "client_OnSelectTeamReceived from ListenerClient");

        setButtonLayoutVisible(teamSelectFrag);
        isTeamSelectFragVisible = true;

        final DialogFragment newFragment = SelectTeamFragment.newInstance(questInfo.teamSize, questInfo.quest.getValue());

        teamSelectFrag.setOnClickListener(null);
        teamSelectFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "teamSelectFrag clicked, Starting teamselectdialog.");

                newFragment.show(getFragmentManager(), "teamselectdialog");
            }
        });

        newFragment.show(getFragmentManager(), "teamselectdialog");
    }

    @Override
    public void client_OnStartNextQuestReceived(Packet.Packet_StartNextQuest previousQuestResult) {
        Log.d(TAG, "client_OnStartNextQuestReceived from ListenerClient");

        currentQuest.setResult(previousQuestResult.previousQuestResult);
        clientQuestResults.add(currentQuest);

        gameBoardFrag.updateBoardQuestResult(currentQuest);

        currentQuest = getNextQuest(currentQuest);

        updateTitleText("Quest " + currentQuest.getValue() + " - Vote " + 1);

        // checkGameStateAndUpdateScreen();

    }

    @Override
    public void client_OnGameFinishedReceived(final Packet.Packet_GameFinished gameResultInfo) {
        Log.d(TAG, "client_OnGameFinishedReceived from ListenerClient");

        setButtonLayoutVisible(gameFinishedFrag);
        isGameFinishedFragVisible = true;
        updateTitleAndStatusColor(standardTitleColor, standardStatusColor);
        updateGameStatusText(getString(R.string.gameFinished));

        final DialogFragment newFragment = GameFinishedFragment.newInstance(gameResultInfo.gameResult, getEvilAllegiancePositions(), getGoodAllegiancePositions());

        gameFinishedFrag.setOnClickListener(null);
        gameFinishedFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "teamSelectFrag clicked, Starting gamefinisheddialog.");

                newFragment.show(getFragmentManager(), "gamefinisheddialog");
            }
        });

        newFragment.show(getFragmentManager(), "gamefinisheddialog");
    }
}
