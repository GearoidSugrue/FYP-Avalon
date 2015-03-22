package com.example.gearoid.testchatapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.esotericsoftware.kryo.Kryo;
import com.example.gearoid.testchatapp.character.CharacterFactory;
import com.example.gearoid.testchatapp.character.ConstantsChara;
import com.example.gearoid.testchatapp.character.EvilCharacter;
import com.example.gearoid.testchatapp.character.GoodCharacter;
import com.example.gearoid.testchatapp.character.ICharacter;
import com.example.gearoid.testchatapp.kryopackage.ConstantsKryo;
import com.example.gearoid.testchatapp.kryopackage.ListenerClient;
import com.example.gearoid.testchatapp.kryopackage.Packet;
import com.example.gearoid.testchatapp.kryopackage.PacketFactory;
import com.example.gearoid.testchatapp.singletons.ClientInstance;
import com.example.gearoid.testchatapp.singletons.ServerInstance;

import java.util.ArrayList;

import static com.example.gearoid.testchatapp.R.drawable;


public class GameSetupActivity extends ActionBarActivity implements GameSetupCharacterListFragment.CharacterListFragListener {

    Board currentBoard;
    int playerCount, evilCount, goodCount;
    boolean ladyOfLake = false;
    ArrayList<ICharacter> allCharacters;
    GameSetupCharacterListFragment goodListFrag;
    GameSetupCharacterListFragment evilListFrag;
    GameSetupCharacterListFragment optionalListFrag;
    GameSetupCharacterListFragment.CharacterListAdapter goodListAdapter;
    GameSetupCharacterListFragment.CharacterListAdapter evilListAdapter;
    GameSetupCharacterListFragment.CharacterListAdapter optionalListAdapter;


    public enum Board {
        FIVE, SIX, SEVEN, EIGHT, NINE, TEN;
    }

    public enum Quest {
        FIRST(1), SECOND(2), THIRD(3), FOURTH(4), FIFTH(5);
        private int value;

        private Quest(int value) {
            this.value = value;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        Toolbar toolbar = (Toolbar) findViewById(R.id.game_setup_toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("Game Setup");

        Intent intent = getIntent();

        initializeButtons();
        initializeCheckbox();

        int numOfPlayers = intent.getIntExtra("PLAYER_COUNT", 5);
        calculateGlobalValues(numOfPlayers);

        getSupportActionBar().setTitle("Game Setup: " + playerCount + " Players");

        initializeFragments();
        setUpGoodCharacterList();
        setUpEvilCharacterList();
        setUpOptionalCharacterList();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setup, menu);
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
    public void characterSelected(int position, boolean isOptionalList, GameSetupCharacterListFragment.CharacterListAdapter listAdapter) {

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
                ApplicationContext.showToast("Side full. Remove a character first.");
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
        newFragment.show(getFragmentManager(), "dialog");
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
                Log.d("GameSetup", "Lady of Lake toggled: " + isChecked);
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
                Log.d("GameSetup", "Lady of Lake long pressed");
                displayCharacterCard("Lady of Lake");
                return true;
            }
        });
    }

    private void initializeFragments() {
        goodListFrag = (GameSetupCharacterListFragment) getFragmentManager()
                .findFragmentById(R.id.good_list);
        goodListFrag.setTitleText(goodCount + " Good Characters");

        evilListFrag = (GameSetupCharacterListFragment) getFragmentManager()
                .findFragmentById(R.id.evil_list);
        evilListFrag.setTitleText(evilCount + " Evil Characters");

        optionalListFrag = (GameSetupCharacterListFragment) getFragmentManager()
                .findFragmentById(R.id.optional_list);
        optionalListFrag.setTitleText("Select Optional Characters To Add");
        optionalListFrag.isOptionalCharacterList = true;

        goodListAdapter = (GameSetupCharacterListFragment.CharacterListAdapter) goodListFrag.getListAdapter();
        evilListAdapter = (GameSetupCharacterListFragment.CharacterListAdapter) evilListFrag.getListAdapter();
        optionalListAdapter = (GameSetupCharacterListFragment.CharacterListAdapter) optionalListFrag.getListAdapter();

        goodListFrag.getView().setBackground(getResources().getDrawable(R.drawable.misc_blueloyalty));
        goodListFrag.getView().getBackground().setAlpha(150);
        goodListFrag.getView().getBackground().setColorFilter(Color.argb(70, 0, 0, 255), PorterDuff.Mode.DARKEN);

        evilListFrag.getView().setBackground(getResources().getDrawable(drawable.misc_redloyalty));
        evilListFrag.getView().getBackground().setAlpha(150);
        evilListFrag.getView().getBackground().setColorFilter(Color.argb(70, 255, 0, 0), PorterDuff.Mode.DARKEN);
    }

    public void setUpGoodCharacterList() {
        ICharacter merlin = CharacterFactory.createPlayer(ConstantsChara.MERLIN);

        Log.d("GameSetup", "Attempting to add a ICharacter to list");
        goodListAdapter.add(merlin);
        fillRemainingGoodListPlaces();
    }

    public void setUpEvilCharacterList() {
        ICharacter assassin = CharacterFactory.createPlayer(ConstantsChara.ASSASSIN);
        evilListAdapter.add(assassin);
        fillRemainingEvilListPlaces();
    }

    public void setUpOptionalCharacterList() {
        ICharacter percival = CharacterFactory.createPlayer(ConstantsChara.PERCIVAL);
        ICharacter morgana = CharacterFactory.createPlayer(ConstantsChara.MORGANA);
        ICharacter mordred = CharacterFactory.createPlayer(ConstantsChara.MORDRED);
        ICharacter oberon = CharacterFactory.createPlayer(ConstantsChara.OBERON);
        optionalListAdapter.add(percival);
        optionalListAdapter.add(morgana);
        optionalListAdapter.add(mordred);
        optionalListAdapter.add(oberon);
    }

    public void fillRemainingGoodListPlaces() {
        int listCount = goodListAdapter.getCount();
        for (int i = 0; i < goodCount - listCount; i++) {
            ICharacter knight = CharacterFactory.createPlayer(ConstantsChara.GOOD);
            knight.setCharacterName("Knight " + (i + 1));
            goodListAdapter.add(knight);
        }
        goodListAdapter.notifyDataSetChanged();
    }

    public void fillRemainingEvilListPlaces() {
        int listCount = evilListAdapter.getCount();
        for (int i = 0; i < evilCount - listCount; i++) {
            ICharacter minion = CharacterFactory.createPlayer(ConstantsChara.EVIL);
            minion.setCharacterName("Minion " + (i + 1));
            evilListAdapter.add(minion);
        }
        evilListAdapter.notifyDataSetChanged();
    }

    public void calculateGlobalValues(int numberOfPlayers) {
        playerCount = numberOfPlayers;
        currentBoard = calculateBoard(playerCount);//Gets the board we'll be using
        evilCount = calculateNumberOfEvilPlayers(currentBoard);//Gets the number of evil players
        goodCount = playerCount - evilCount;//Gets the number of good players
    }

    public void startGameActivity() {
        //String userName = SharedPrefManager.getStringDefaults("USERNAME", this);

        //Intent intent = new Intent(this, ___.class);

        //startActivity(intent);
        if(goodListAdapter.getCount() < goodCount || evilListAdapter.getCount() < evilCount){
            ApplicationContext.showToast("Not enough characters!");
        } else {

            //TODO Start game activity...
        }


        /*
        Thread sendPacketThread = new Thread() {//Testing purposes...
            @Override
            public void run() {
                try {
                    //Packet.Packet00_ClientDetails testPacket = (Packet.Packet00_ClientDetails) PacketFactory.createPacket("Client Details");
                    //testPacket.playerName = SharedPrefManager.getStringDefaults("USERNAME", ApplicationContext.getContext());
                    ApplicationContext.showToast("[C] Sending Packet...");

                    Packet.Packet2_Message reply = (Packet.Packet2_Message) PacketFactory.createPacket(ConstantsKryo.MESSAGE);
                    reply.message = "Start";
                    //ClientInstance.getKryoClientInstance().getClient().sendTCP(testPacket);
                    ServerInstance.getServerInstance().sendToEveryone(reply);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        sendPacketThread.start();
        */
    }

    public Board calculateBoard(int numberOfPlayers) {
        switch (numberOfPlayers) {
            case 5: return Board.FIVE;
            case 6: return Board.SIX;
            case 7: return Board.SEVEN;
            case 8: return Board.EIGHT;
            case 9: return Board.NINE;
            case 10: return Board.TEN;
        }
        return Board.FIVE;
    }

    public int calculateNumberOfEvilPlayers(Board board) {

        switch (board) {
            case FIVE:
            case SIX:
                return 2;
            case SEVEN:
            case EIGHT:
            case NINE:
                return 3;
            case TEN:
                return 4;
        }
        return 2;
    }

    public static int[] getBoardConfiguration(Board board) {//Returns an int array that holds the number of players needed for each quest

        int[] arr;
        switch (board) {
            case FIVE:
                return arr = new int[]{2, 3, 2, 3, 3};
            case SIX:
                return arr = new int[]{2, 3, 4, 3, 4};
            case SEVEN:
                return arr = new int[]{2, 3, 3, 4, 4};
            case EIGHT:
            case NINE:
            case TEN:
                return arr = new int[]{3, 4, 4, 5, 5};
        }
        return arr = new int[]{0, 0, 0, 0, 0};
    }

    public static int calculatePlayersOnQuest(Board board, Quest questNumber) {//Move this???
        return getBoardConfiguration(board)[questNumber.value]; //Returns the number of players that go on a particular quest
    }

}
