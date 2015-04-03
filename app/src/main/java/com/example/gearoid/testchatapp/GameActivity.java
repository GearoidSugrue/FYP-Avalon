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
import com.example.gearoid.testchatapp.kryopackage.PacketFactory;
import com.example.gearoid.testchatapp.multiplayer.Session;
import com.example.gearoid.testchatapp.singletons.ClientInstance;
import com.example.gearoid.testchatapp.singletons.PlayerConnection;
import com.example.gearoid.testchatapp.singletons.ServerInstance;

import java.util.ArrayList;

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
            Toolbar toolbar = (Toolbar) findViewById(R.id.game_toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Quest " + currentQuest.getValue());

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
            serverListener.attachActivityToServerListener(this);
        }

        if (clientListener != null) {
            clientListener.attachActivityToClientListener(this);
        }

        checkGameStateAndUpdateScreen();
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

//                DialogFragment newFragment = TeamVoteFragment.newInstance(new int[]{0, 1}, 1, 3); //TODO change to voteCount + QuestCount
//                newFragment.show(getFragmentManager(), "teamdialog");
            }
        });

        questVoteFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        teamSelectFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment newFragment = SelectTeamFragment.newInstance(GameLogicFunctions.calculatePlayersRequiredForQuest(currentBoard, currentQuest), currentQuest.getValue()); //TODO change to questNum...
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

                if (currentGameState == GameState.LADY_OF_LAKE) {
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

//                DialogFragment newFragment = TeamVoteResultFragment.newInstance(false, new int[]{0, 1}, new int[]{0, 1}, 2, 3); //TODO change to questNum...
//                newFragment.show(getFragmentManager(), "teamvoteresultdialog");
            }
        });

        gameFinishedFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                DialogFragment newFragment = GameFinishedFragment.newInstance(true, new int[]{0, 1}, new int[]{0, 1}); //TODO change to questNum...
//                newFragment.show(getFragmentManager(), "teamvoteresultdialog");
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

//                DialogFragment newFragment = QuestResultFragment.newInstance(new int[]{0, 1}, new boolean[]{true, true}, currentQuest, calculateFailRequiredForQuest(currentBoard, currentQuest)); //TODO change to questNum...
//                newFragment.show(getFragmentManager(), "questresultdialog");
            }
        });

    }

    public void setAllButtonsGone() {
        final Button teamVoteFrag = (Button) findViewById(R.id.button_teamVoteFrag);
        final Button questVoteFrag = (Button) findViewById(R.id.button_questVoteFrag);
        final Button teamSelectFrag = (Button) findViewById(R.id.button_teamSelectFrag);
        final Button assassinateFrag = (Button) findViewById(R.id.button_assassinateFrag);
        final Button ladyOfLakeFrag = (Button) findViewById(R.id.button_ladyOfLakeFrag);
        final Button teamVoteResultFrag = (Button) findViewById(R.id.button_teamVoteResultFrag);
        final Button gameFinishedFrag = (Button) findViewById(R.id.button_gameFinishedFrag);
        final Button questResultFrag = (Button) findViewById(R.id.button_questResultFrag);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("GameActivity", "runOnUiThread: setting all buttons invisible (not viewcharacter)");

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

    public void setButtonGone(final Button button) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("GameActivity", "runOnUiThread: setting button invisible: " + button.getText());

                button.setVisibility(View.GONE);
            }
        });
    }

    public void setButtonVisible(final Button button) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("GameActivity", "runOnUiThread: setting button visible: " + button.getText());

                button.setVisibility(View.VISIBLE);
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

        if (getUserPlayer().isLeader) {
            updateGameStatusText("Your The Leader - Select A Team");
//            teamSelectFrag.setVisibility(View.VISIBLE);
            setButtonVisible(teamSelectFrag);

        } else {
            updateGameStatusText("Waiting For The Leader To Select Team");
//            teamSelectFrag.setVisibility(View.GONE);
            setButtonGone(teamSelectFrag);

        }
    }

    public void setTeamVoteState() {
        Log.d("GameActivity", "setTeamVoteState");

        updateGameStatusText("Vote On Selected Team");

//        Button teamVoteBut = (Button) findViewById(R.id.button_teamVoteFrag);
//        teamVoteBut.setVisibility(View.VISIBLE);
        //setButtonVisible(teamVoteFrag);

    }

    public void setTeamVoteResultState() {
        Log.d("GameActivity", "setTeamVoteResultState");

        updateGameStatusText("View Team Vote Result");


//        Button teamVoteResultBut = (Button) findViewById(R.id.button_teamVoteResultFrag);
//        teamVoteResultFrag.setVisibility(View.VISIBLE);

//        if (getUserPlayer().isLeader) {
////            teamVoteResultBut.setVisibility(View.VISIBLE);
//            setButtonVisible(teamVoteResultFrag);
//
//        }
    }

    public void setQuestVoteState() {
        Log.d("GameActivity", "setQuestVoteState");

        voteCount = 0;

//        Button questVoteBut = (Button) findViewById(R.id.button_questVoteFrag);

        if (getUserPlayer().isOnQuest) {
            updateGameStatusText("Your On A Quest. Please Vote.");
//            questVoteBut.setVisibility(View.VISIBLE);
            //setButtonVisible(questVoteFrag);

        } else {
            updateGameStatusText("Waiting For Quest Team");
//            questVoteBut.setVisibility(View.GONE);
            setButtonGone(questVoteFrag);
        }
    }

    public void setQuestVoteResultState() {
        Log.d("GameActivity", "setQuestVoteResultState");

        updateGameStatusText("View Quest " + currentQuest.getValue() + " Results");
//        Button questResultBut = (Button) findViewById(R.id.button_questResultFrag);
//        questResultBut.setVisibility(View.VISIBLE);
        setButtonVisible(questResultFrag);

//        questResultFrag.setVisibility(View.VISIBLE);
    }

    public void setLadyOfLakeState() {
        Log.d("GameActivity", "setLadyOfLakeState");


        if (getUserPlayer().hasLadyOfLake) {
            updateGameStatusText("Lady of The Lake Token");
//            Button ladyOfLakeBut = (Button) findViewById(R.id.button_ladyOfLakeFrag);
//            ladyOfLakeBut.setVisibility(View.VISIBLE);
            setButtonVisible(ladyOfLakeFrag);

            DialogFragment newFragment = LadyOfLakeFragment.newInstance(); //TODO change to questNum...
            newFragment.show(getFragmentManager(), "ladyoflakedialog");
//            ladyOfLakeFrag.setVisibility(View.VISIBLE);
        } else {
            updateGameStatusText("Waiting For Lady of the Lake Holder");
        }
    }

    public void setAssassinState() {
        Log.d("GameActivity", "setAssassinState");

        if (getUserPlayer().character instanceof Assassin) {
            updateGameStatusText("Select A Player To Assassinate");
//            Button assassinateBut = (Button) findViewById(R.id.button_assassinateFrag);
//            assassinateBut.setVisibility(View.VISIBLE);
            setButtonVisible(assassinateFrag);

//            assassinateFrag.setVisibility(View.VISIBLE);
        } else {
            updateGameStatusText("Waiting For The Assassin");
        }
    }

    public void setFinishedState() {
        Log.d("GameActivity", "setFinishedState");

        updateGameStatusText("Game Finished");
    }

    public static void updateGameStatusText(final String text) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d("GameActivity", "Attempting to update gameStatusText: " + text);
                gameStatusText = text;
                gameStatusView.setText(text);
                //Toast.makeText(getContext(), data, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onVoteSelected(boolean voteResult) {
        Log.d("GameActivity", "Vote Result received from team vote dialog: " + voteResult);

        final Packet.Packet_TeamVoteReply packet = (Packet.Packet_TeamVoteReply) PacketFactory.createPack(PacketFactory.PacketType.TEAM_VOTE_REPLY);
        packet.vote = voteResult;
        packet.playerID = PlayerConnection.getInstance().playerID;

//        Session.client_sendPacketToServer(packet);
        Thread thread = new Thread() {//The host is also a player!!
            @Override
            public void run() {
                try {
                    Log.d("Session", "Sending Packet_TeamVoteReply to Server");
                    ClientInstance.getKryoClientInstance().getClient().sendTCP(packet);
                } catch (Exception e) {
                    Log.d("Session", "Error Sending Packet_TeamVoteReply to Server. Player Name: " + PlayerConnection.getInstance().playerID);
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    @Override
    public void onQuestVoteSelected(boolean voteResult) {
        Log.d("GameActivity", "Vote Result received from quest vote dialog: " + voteResult);

        final Packet.Packet_QuestVoteReply packet = (Packet.Packet_QuestVoteReply) PacketFactory.createPack(PacketFactory.PacketType.QUEST_VOTE_REPLY);
        packet.vote = voteResult;
        packet.playerID = PlayerConnection.getInstance().playerID;

//        Session.client_sendPacketToServer(packet);
        Thread thread = new Thread() {//The host is also a player!!
            @Override
            public void run() {
                try {
                    Log.d("Session", "Sending Packet_QuestVoteReply to Server");
                    ClientInstance.getKryoClientInstance().getClient().sendTCP(packet);
                } catch (Exception e) {
                    Log.d("Session", "Error Sending Packet_QuestVoteReply to Server. Player Name: " + PlayerConnection.getInstance().playerID);
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    @Override
    public void onTeamSelected(int[] teamIndexes) {
        Log.d("GameActivity", "Team selected received from TeamSelect dialog.");

        //server_currentProposedTeam = teamIndexes;

        final Packet.Packet_SelectTeamReply packet = (Packet.Packet_SelectTeamReply) PacketFactory.createPack(PacketFactory.PacketType.SELECT_TEAM_REPLY);
        packet.teamPos = teamIndexes;
        packet.playerID = PlayerConnection.getInstance().playerID;

        Session.client_sendPacketToServer(packet);
//        Thread thread = new Thread() {//The host is also a player!!
//            @Override
//            public void run() {
//                try {
//                    Log.d("Session", "Sending Packet_QuestVoteReply to Server");
//                    ClientInstance.getKryoClientInstance().getClient().sendTCP(packet);
//                } catch (Exception e) {
//                    Log.d("Session", "Error Sending Packet_QuestVoteReply to Server. Player Name: " + PlayerConnection.getInstance().playerID);
//                    e.printStackTrace();
//                }
//            }
//        };
//        thread.start();
    }

    @Override
    public void onAssassination(boolean isSuccess) {
        Log.d("GameActivity", "OnAssassinate received from Assassinate dialog. Result: " + isSuccess);

        final Packet.Packet_AssassinateReply packet = (Packet.Packet_AssassinateReply) PacketFactory.createPack(PacketFactory.PacketType.ASSASSINATE_REPLY);
        packet.isSuccess = isSuccess;
        packet.playerID = PlayerConnection.getInstance().playerID;

//        Session.client_sendPacketToServer(packet);
        Thread thread = new Thread() {//The host is also a player!!
            @Override
            public void run() {
                try {
                    Log.d("Session", "Sending Packet_AssassinateReply to Server");
                    ClientInstance.getKryoClientInstance().getClient().sendTCP(packet);
                } catch (Exception e) {
                    Log.d("Session", "Error Sending Packet_AssassinateReply to Server. Player Name: " + PlayerConnection.getInstance().playerID);
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    @Override
    public void ladyOfLakeActivated(int selectedPlayerIndex) {
        Log.d("GameActivity", "LadyOfLakeActivated received from LadyOfLake dialog. Player ID " + selectedPlayerIndex);

        final Packet.Packet_LadyOfLakeReply packet = (Packet.Packet_LadyOfLakeReply) PacketFactory.createPack(PacketFactory.PacketType.LADYOFLAKE_REPLY);
        packet.selectedPlayerIndex = selectedPlayerIndex;
        packet.playerID = PlayerConnection.getInstance().playerID;

//        Session.client_sendPacketToServer(packet);

        Thread thread = new Thread() {//The host is also a player!!
            @Override
            public void run() {
                try {
                    Log.d("Session", "Sending Packet_LadyOfLakeReply to Server");
                    ClientInstance.getKryoClientInstance().getClient().sendTCP(packet);
                } catch (Exception e) {
                    Log.d("Session", "Error Sending Packet_LadyOfLakeReply to Server. Player Name: " + PlayerConnection.getInstance().playerID);
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    @Override
    public void onGameFinishedSelection(boolean playAgain) {
        Log.d("GameActivity", "GameFinished option received from GameFinished dialog. PlayAgain = " + playAgain);

        final Packet.Packet_GameFinishedReply packet = (Packet.Packet_GameFinishedReply) PacketFactory.createPack(PacketFactory.PacketType.GAME_FINISHED_REPLY);
        packet.playAgain = playAgain;
        packet.playerID = PlayerConnection.getInstance().playerID;

//        Session.client_sendPacketToServer(packet);
        Thread thread = new Thread() {//The host is also a player!!
            @Override
            public void run() {
                try {
                    Log.d("Session", "Sending Packet_GameFinishedReply to Server");
                    ClientInstance.getKryoClientInstance().getClient().sendTCP(packet);
                } catch (Exception e) {
                    Log.d("Session", "Error Sending Packet_GameFinishedReply to Server. Player Name: " + PlayerConnection.getInstance().playerID);
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    @Override
    public void onQuestVoteResultRevealed(int voteNumber, boolean voteResult) {
        Log.d("GameActivity", "QuestVoteResultRevealed from QuestResultDialog. Vote Number: " + voteNumber + ", Vote Result: " + voteResult);

        final Packet.Packet_QuestVoteResultRevealed packet = (Packet.Packet_QuestVoteResultRevealed) PacketFactory.createPack(PacketFactory.PacketType.QUESTVOTE_REVEALED);
        packet.voteNumber = voteNumber;
        packet.voteResult = voteResult;
        packet.playerID = PlayerConnection.getInstance().playerID;

//        Session.client_sendPacketToServer(packet);
        Thread thread = new Thread() {//The host is also a player!!
            @Override
            public void run() {
                try {
                    Log.d("Session", "Sending Packet_QuestVoteResultRevealed to Server");
                    ClientInstance.getKryoClientInstance().getClient().sendTCP(packet);
                } catch (Exception e) {
                    Log.d("Session", "Error Sending Packet_QuestVoteResultRevealed to Server. Player Name: " + PlayerConnection.getInstance().playerID);
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    @Override
    public void onQuestVoteResultsFinished(boolean result) {
        Log.d("GameActivity", "onQuestVoteResultsFinished from QuestResultDialog. Quest Result: " + result);

        if (getUserPlayer().isLeader) {

            final Packet.Packet_QuestVoteResultFinished packet = (Packet.Packet_QuestVoteResultFinished) PacketFactory.createPack(PacketFactory.PacketType.QUESTVOTE_FINISHED);
            packet.result = result;
            packet.playerID = PlayerConnection.getInstance().playerID;

//        Session.client_sendPacketToServer(packet);
            Thread thread = new Thread() {//The host is also a player!!
                @Override
                public void run() {
                    try {
                        Log.d("Session", "Sending Packet_QuestVoteResultFinished to Server");
                        ClientInstance.getKryoClientInstance().getClient().sendTCP(packet);
                    } catch (Exception e) {
                        Log.d("Session", "Error Sending Packet_QuestVoteResultFinished to Server. Player Name: " + PlayerConnection.getInstance().playerID);
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
        }
    }

    @Override
    public void server_OnMessagePacketReceived(String message) {
        Log.d("GameActivity", "server_OnMessagePacketReceived from ListenerServer. Message: " + message);

        ApplicationContext.showToast("IActivityServerListener Works!!!");
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

        startNewQuest(currentQuest.getScore());
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

        currentQuest.setScore(resultInfo.result);
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

//        teamVoteFrag.setVisibility(View.VISIBLE);
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Log.d("GameActivity", "runOnUiThread: setting teamVoteFrag button visible");
//
//                teamVoteFrag.setVisibility(View.VISIBLE);
//            }
//        });

        setButtonVisible(teamVoteFrag);

//        Button teamVoteBut = (Button) findViewById(R.id.button_teamVoteFrag);
//        teamVoteBut.setVisibility(View.VISIBLE);
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

//        teamVoteResultFrag.setVisibility(View.VISIBLE);
        setButtonVisible(teamVoteResultFrag);

//        Button teamVoteResultBut = (Button) findViewById(R.id.button_teamVoteResultFrag);
//        teamVoteResultBut.setVisibility(View.VISIBLE);

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

//        questVoteFrag.setVisibility(View.VISIBLE);
        setButtonVisible(questVoteFrag);

//        Button questVoteBut = (Button) findViewById(R.id.button_questVoteFrag);
//        questVoteBut.setVisibility(View.VISIBLE);
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

//        questResultFrag.setVisibility(View.VISIBLE);
        setButtonVisible(questResultFrag);

//        Button questResultBut = (Button) findViewById(R.id.button_questResultFrag);
//        questResultBut.setVisibility(View.VISIBLE);

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
        Log.d("GameActivity", "client_OnLadyOfLakeUpdateReceived from ListenerClient.");

        Session.allPlayers.get(updateInfo.newTokenHolderID).hasLadyOfLake = true;
        Session.allPlayers.get(updateInfo.previousTokenHolderID).hasLadyOfLake = false;
        Session.allPlayers.get(updateInfo.previousTokenHolderID).hasUsedLadyOfLake = true;

        if(updateInfo.newTokenHolderID == getUserPlayer().playerID){
            ApplicationContext.showToast(Session.allPlayers.get(updateInfo.previousTokenHolderID).userName + " has used the Lady of The Lake Token on you");
            ApplicationContext.showToast(Session.allPlayers.get(updateInfo.previousTokenHolderID).userName + " now knows your allegiance");
        }

    }

    @Override
    public void client_OnSelectTeamReceived(Packet.Packet_SelectTeam questInfo) {
        Log.d("GameActivity", "client_OnSelectTeamReceived from ListenerClient");

//        teamSelectFrag.setVisibility(View.VISIBLE);
        setButtonVisible(teamSelectFrag);

//        Button teamSelectBut = (Button) findViewById(R.id.button_teamSelectFrag);
//        teamSelectBut.setVisibility(View.VISIBLE);


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

        currentQuest.setScore(previousQuestResult.previousQuestResult);
        clientQuestResults.add(currentQuest);

        currentQuest = getNextQuest(currentQuest);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("GameActivity", "runOnUiThread: updating Toolbar text: Quest " + currentQuest.getValue());

                Toolbar toolbar = (Toolbar) findViewById(R.id.game_toolbar);
                setSupportActionBar(toolbar);
                getSupportActionBar().setTitle("Quest " + currentQuest.getValue());
            }
        });
    }

    @Override
    public void client_OnGameFinishedReceived(final Packet.Packet_GameFinished gameResultInfo) {
        Log.d("GameActivity", "client_OnGameFinishedReceived from ListenerClient");

//        Button gameFinishedBut = (Button) findViewById(R.id.button_gameFinishedFrag);
//        gameFinishedBut.setVisibility(View.VISIBLE);

        setButtonVisible(gameFinishedFrag);

//        gameFinishedFrag.setVisibility(View.VISIBLE);//TODO fix this. Can't change views from different thread...use handler???
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
