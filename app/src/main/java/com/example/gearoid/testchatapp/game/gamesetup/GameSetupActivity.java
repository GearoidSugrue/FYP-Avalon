package com.example.gearoid.testchatapp.game.gamesetup;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gearoid.testchatapp.R;
import com.example.gearoid.testchatapp.character.CharacterFactory;
import com.example.gearoid.testchatapp.character.ConstantsChara;
import com.example.gearoid.testchatapp.character.ICharacter;
import com.example.gearoid.testchatapp.character.evil.EvilCharacter;
import com.example.gearoid.testchatapp.character.good.GoodCharacter;
import com.example.gearoid.testchatapp.game.GameLogicFunctions;
import com.example.gearoid.testchatapp.game.gamedialogfragments.CharacterCardFragment;
import com.example.gearoid.testchatapp.kryopackage.Packet;
import com.example.gearoid.testchatapp.kryopackage.PacketFactory;
import com.example.gearoid.testchatapp.multiplayer.Player;
import com.example.gearoid.testchatapp.multiplayer.Session;
import com.example.gearoid.testchatapp.utils.ApplicationContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class GameSetupActivity extends ActionBarActivity implements CharacterListFragment.CharacterListFragListener {

    //Constants
    public static final String TAG = "GameSetup";
    public static final String PLAYER_COUNT = "PLAYER_COUNT";
    public static final String DIALOG = "dialog";



    GameLogicFunctions.Board currentBoard;
    int playerCount = 5, evilCount = 2, goodCount = 3;
    boolean ladyOfLake = false;
    ArrayList<ICharacter> allCharacters;
    CharacterListFragment goodListFrag;
    CharacterListFragment evilListFrag;
    CharacterListFragment optionalListFrag;
    CharacterListFragment.CharacterListAdapter goodListAdapter;
    CharacterListFragment.CharacterListAdapter evilListAdapter;
    CharacterListFragment.CharacterListAdapter optionalListAdapter;

    int numOfPlayersTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Toolbar toolbar = (Toolbar) findViewById(R.id.game_setup_toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();

        initializeButtons();
        initializeCheckbox();

        int numOfPlayers = intent.getIntExtra(PLAYER_COUNT, 5);

        numOfPlayersTest = numOfPlayers; //Testing purposes

        if(numOfPlayers < 5){
            numOfPlayers = 5;
        }

        calculateGlobalValues(numOfPlayers);

        getSupportActionBar().setTitle("Game Setup: " + playerCount + " Players");

        initializeFragments();
        setFragTitles();
        setUpGoodCharacterList();
        setUpEvilCharacterList();
        setUpOptionalCharacterList();

    }

    @Override
    public void characterSelected(int position, boolean isOptionalList, CharacterListFragment.CharacterListAdapter listAdapter) {

        ICharacter character = listAdapter.getItem(position);
        if (isOptionalList) {

            if (character instanceof GoodCharacter && goodListAdapter.getCount() < goodCount) {
                listAdapter.remove(character);
                goodListAdapter.add(character);
                listAdapter.notifyDataSetChanged();
                goodListAdapter.notifyDataSetChanged();

            } else if (character instanceof EvilCharacter && evilListAdapter.getCount() < evilCount) {
                listAdapter.remove(character);
                evilListAdapter.add(character);
                listAdapter.notifyDataSetChanged();
                evilListAdapter.notifyDataSetChanged();

            } else {
                ApplicationContext.showToast(getString(R.string.sideFullRemoveCharacter));
            }
        } else {
            listAdapter.remove(character);
            optionalListAdapter.add(character);
            listAdapter.notifyDataSetChanged();
            optionalListAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void displayCharacterCard(String characterName) {//from GameSetupCharacterListFragment.CharacterListFragListener

        DialogFragment newFragment = CharacterCardFragment.newInstance(characterName);
        newFragment.show(getFragmentManager(), DIALOG);
    }


    private void initializeButtons() {
        Button button_startGame = (Button) findViewById(R.id.button_start_game);

        button_startGame.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startGameActivity();
            }
        });
    }

    private void initializeCheckbox() {
        CheckBox checkBox_ladyOfLake = (CheckBox) findViewById(R.id.checkBox_LadyOfLake);
        checkBox_ladyOfLake.setChecked(false);

        checkBox_ladyOfLake.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "Lady of Lake toggled: " + isChecked);
                if (isChecked) {
                    ladyOfLake = true;
                } else {
                    ladyOfLake = false;
                }
            }
        });

        checkBox_ladyOfLake.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG, "Lady of Lake long pressed");
                displayCharacterCard(ConstantsChara.LADY_OF_LAKE);
                return true;
            }
        });
    }

    public void setFragTitles(){
        TextView goodFragTitle = (TextView) findViewById(R.id.textView_GoodCharactersLabel);
        goodFragTitle.setText(goodCount + " " + getString(R.string._goodCharacters));
        TextView evilFragTitle = (TextView) findViewById(R.id.textView_EvilCharactersLabel);
        evilFragTitle.setText(evilCount + " " + getString(R.string._evilCharacters));
    }

    private void initializeFragments() {
        goodListFrag = (CharacterListFragment) getFragmentManager()
                .findFragmentById(R.id.good_list);

        evilListFrag = (CharacterListFragment) getFragmentManager()
                .findFragmentById(R.id.evil_list);

        optionalListFrag = (CharacterListFragment) getFragmentManager()
                .findFragmentById(R.id.optional_list);
        optionalListFrag.isOptionalCharacterList = true;

        goodListAdapter = (CharacterListFragment.CharacterListAdapter) goodListFrag.getListAdapter();
        evilListAdapter = (CharacterListFragment.CharacterListAdapter) evilListFrag.getListAdapter();
        optionalListAdapter = (CharacterListFragment.CharacterListAdapter) optionalListFrag.getListAdapter();

        LinearLayout goodLayout = (LinearLayout) findViewById(R.id.layout_goodFrag);
        goodLayout.setBackground(getResources().getDrawable(R.drawable.misc_blueloyalty));
        goodLayout.getBackground().setAlpha(150);
        goodLayout.getBackground().setColorFilter(Color.argb(70, 0, 0, 255), PorterDuff.Mode.DARKEN);

        LinearLayout evilLayout = (LinearLayout) findViewById(R.id.layout_evilFrag);
        evilLayout.setBackground(getResources().getDrawable(R.drawable.misc_redloyalty));
        evilLayout.getBackground().setAlpha(150);
        evilLayout.getBackground().setColorFilter(Color.argb(70, 255, 0, 0), PorterDuff.Mode.DARKEN);
    }

    public void setUpGoodCharacterList() {
        ICharacter merlin = CharacterFactory.createPlayer(CharacterFactory.CharacterType.MERLIN);

        Log.d(TAG, "Attempting to add a ICharacter to list");
        goodListAdapter.add(merlin);
        fillRemainingGoodListPlaces();
    }

    public void setUpEvilCharacterList() {
        ICharacter assassin = CharacterFactory.createPlayer(CharacterFactory.CharacterType.ASSASSIN);
        evilListAdapter.add(assassin);
        fillRemainingEvilListPlaces();
    }

    public void setUpOptionalCharacterList() {
        ICharacter percival = CharacterFactory.createPlayer(CharacterFactory.CharacterType.PERCIVAL);
        ICharacter morgana = CharacterFactory.createPlayer(CharacterFactory.CharacterType.MORGANA);
        ICharacter mordred = CharacterFactory.createPlayer(CharacterFactory.CharacterType.MORDRED);
        ICharacter oberon = CharacterFactory.createPlayer(CharacterFactory.CharacterType.OBERON);
        optionalListAdapter.add(percival);
        optionalListAdapter.add(morgana);
        optionalListAdapter.add(mordred);
        optionalListAdapter.add(oberon);
    }

    public void fillRemainingGoodListPlaces() {
        int listCount = goodListAdapter.getCount();
        for (int i = 0; i < goodCount - listCount; i++) {
            ICharacter knight = CharacterFactory.createPlayer(CharacterFactory.CharacterType.KNIGHT);
            knight.setCharacterName(ConstantsChara.KNIGHT_OF_ARTHUR + " " + (i + 1));
            goodListAdapter.add(knight);
        }
        goodListAdapter.notifyDataSetChanged();
    }

    public void fillRemainingEvilListPlaces() {
        int listCount = evilListAdapter.getCount();
        for (int i = 0; i < evilCount - listCount; i++) {
            ICharacter minion = CharacterFactory.createPlayer(CharacterFactory.CharacterType.MINION);
            minion.setCharacterName(ConstantsChara.MINION_OF_MORDRED + " " + (i + 1));
            evilListAdapter.add(minion);
        }
        evilListAdapter.notifyDataSetChanged();
    }

    public void calculateGlobalValues(int numberOfPlayers) {
        playerCount = numberOfPlayers;
        currentBoard = GameLogicFunctions.calculateBoard(playerCount);//Gets the board we'll be using
        evilCount = GameLogicFunctions.calculateNumberOfEvilPlayers(currentBoard);//Gets the number of evil players
        goodCount = playerCount - evilCount;//Gets the number of good players
    }

    public void startGameActivity() {

        if (goodListAdapter.getCount() < goodCount || evilListAdapter.getCount() < evilCount) {
            ApplicationContext.showToast(getString(R.string.notEnoughCharacters));
        } else {

            if(numOfPlayersTest < 5){//Testing purposes.
                Player testPlayer1 = new Player();
                testPlayer1.userName = "Chris";
                Session.serverAllPlayers.add(testPlayer1);
            }

            checkAndAssignLadyOfLake();
            assignAllPlayersCharacters(getCombinedGoodAndEvilList());
            initialiseLeaderOrderedAllPlayers();

            final Packet.Packet_StartGame packet = (Packet.Packet_StartGame) PacketFactory.createPack(PacketFactory.PacketType.START_GAME);

            packet.allPlayers = new ArrayList<>();
            packet.leaderOrderList = new ArrayList<>();
            packet.allPlayers.addAll(Session.serverAllPlayers);
            packet.leaderOrderList.addAll(Session.leaderOrderList);
            packet.currentBoard = currentBoard;

            Session.server_sendToEveryone(packet);
        }
    }

    public void checkAndAssignLadyOfLake(){
        Log.d(TAG, "Checking if lady of lake has been checked");

        if(ladyOfLake){
            Log.d(TAG, "Assigning lady of lake");
            Session.serverIsLadyOfLakeOn = true;

            Random ran = new Random();
            final int randomIndex = ran.nextInt(Session.serverAllPlayers.size());

            Session.serverAllPlayers.get(randomIndex).hasLadyOfLake = true;

        } else {
            Session.serverIsLadyOfLakeOn = false;
        }
    }

    public ArrayList<ICharacter> getCombinedGoodAndEvilList() {
        ArrayList<ICharacter> aCharacters = new ArrayList<>();

        for (int i = 0; i < goodListAdapter.getCount(); i++) {
            aCharacters.add(goodListAdapter.getItem(i));
        }
        for (int i = 0; i < evilListAdapter.getCount(); i++) {
            aCharacters.add(evilListAdapter.getItem(i));
        }

        return aCharacters;
    }

    public static void assignAllPlayersCharacters(ArrayList<ICharacter> chosenCharacters) {
        Log.d(TAG, "Assigning players characters");

        Collections.shuffle(chosenCharacters);

        if (chosenCharacters.size() == Session.serverAllPlayers.size()) {

            for (int i = 0; i < Session.serverAllPlayers.size(); i++) {
                Log.d(TAG, "Character: " + chosenCharacters.get(i).getCharacterName());
                Session.serverAllPlayers.get(i).character = chosenCharacters.get(i);
            }
        }
    }

    public static void initialiseLeaderOrderedAllPlayers() {
        Session.leaderOrderList = new ArrayList<Integer>();
        Session.leaderOrderIterator = 0;

        for(int i=0; i < Session.serverAllPlayers.size(); i++){
            Session.leaderOrderList.add(i);
            Log.d(TAG, "leaderOrderList - " +  Session.leaderOrderList.get(i));
        }

        Collections.shuffle(Session.leaderOrderList);

//        if(!Session.serverAllPlayers.isEmpty()){
//
//            for(int i=0; i < Session.serverAllPlayers.size(); i++){
//
//                Log.d(TAG, "leaderOrderList - " +  Session.leaderOrderList.get(i));
//            }
//        }

//        Session.leaderOrderAllPlayers = new ArrayList<Player>();
//        Session.leaderOrderAllPlayers.addAll(Session.serverAllPlayers);
//        Collections.shuffle(Session.leaderOrderAllPlayers);
//        if(!Session.leaderOrderAllPlayers.isEmpty()){
//            Session.leaderOrderAllPlayers.get(0).isLeader = true;
//        }
    }
   
}
