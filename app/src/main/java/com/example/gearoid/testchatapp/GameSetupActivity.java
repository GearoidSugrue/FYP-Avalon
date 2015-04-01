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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gearoid.testchatapp.character.CharacterFactory;
import com.example.gearoid.testchatapp.character.EvilCharacter;
import com.example.gearoid.testchatapp.character.GoodCharacter;
import com.example.gearoid.testchatapp.character.ICharacter;
import com.example.gearoid.testchatapp.gamedialogfragments.CharacterCardFragment;
import com.example.gearoid.testchatapp.kryopackage.Packet;
import com.example.gearoid.testchatapp.kryopackage.PacketFactory;
import com.example.gearoid.testchatapp.multiplayer.Session;
import com.example.gearoid.testchatapp.singletons.ServerInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class GameSetupActivity extends ActionBarActivity implements CharacterListFragment.CharacterListFragListener {

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
        calculateGlobalValues(5);

        getSupportActionBar().setTitle("Game Setup: " + playerCount + " Players");

        initializeFragments();
        setFragTitles();
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

    public void setFragTitles(){
        TextView goodFragTitle = (TextView) findViewById(R.id.textView_GoodCharactersLabel);
        goodFragTitle.setText(goodCount + " Good Characters");
        TextView evilFragTitle = (TextView) findViewById(R.id.textView_EvilCharactersLabel);
        evilFragTitle.setText(evilCount + " Evil Characters");
    }

    private void initializeFragments() {
        goodListFrag = (CharacterListFragment) getFragmentManager()
                .findFragmentById(R.id.good_list);
        //goodListFrag.setTitleText(goodCount + " Good Characters");

        evilListFrag = (CharacterListFragment) getFragmentManager()
                .findFragmentById(R.id.evil_list);
        //evilListFrag.setTitleText(evilCount + " Evil Characters");

        optionalListFrag = (CharacterListFragment) getFragmentManager()
                .findFragmentById(R.id.optional_list);
        //optionalListFrag.setTitleText("Select Optional Characters To Add");
        optionalListFrag.isOptionalCharacterList = true;

        goodListAdapter = (CharacterListFragment.CharacterListAdapter) goodListFrag.getListAdapter();
        evilListAdapter = (CharacterListFragment.CharacterListAdapter) evilListFrag.getListAdapter();
        optionalListAdapter = (CharacterListFragment.CharacterListAdapter) optionalListFrag.getListAdapter();

//        goodListFrag.getView().setBackground(getResources().getDrawable(R.drawable.misc_blueloyalty));
//        goodListFrag.getView().getBackground().setAlpha(150);
//        goodListFrag.getView().getBackground().setColorFilter(Color.argb(70, 0, 0, 255), PorterDuff.Mode.DARKEN);

        LinearLayout goodLayout = (LinearLayout) findViewById(R.id.layout_goodFrag);
        goodLayout.setBackground(getResources().getDrawable(R.drawable.misc_blueloyalty));
        goodLayout.getBackground().setAlpha(150);
        goodLayout.getBackground().setColorFilter(Color.argb(70, 0, 0, 255), PorterDuff.Mode.DARKEN);

        LinearLayout evilLayout = (LinearLayout) findViewById(R.id.layout_evilFrag);
        evilLayout.setBackground(getResources().getDrawable(R.drawable.misc_redloyalty));
        evilLayout.getBackground().setAlpha(150);
        evilLayout.getBackground().setColorFilter(Color.argb(70, 255, 0, 0), PorterDuff.Mode.DARKEN);

//
//        evilListFrag.getView().setBackground(getResources().getDrawable(drawable.misc_redloyalty));
//        evilListFrag.getView().getBackground().setAlpha(150);
//        evilListFrag.getView().getBackground().setColorFilter(Color.argb(70, 255, 0, 0), PorterDuff.Mode.DARKEN);
    }

    public void setUpGoodCharacterList() {
        ICharacter merlin = CharacterFactory.createPlayer(CharacterFactory.CharacterType.MERLIN);

        Log.d("GameSetup", "Attempting to add a ICharacter to list");
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
            knight.setCharacterName("Knight " + (i + 1));
            goodListAdapter.add(knight);
        }
        goodListAdapter.notifyDataSetChanged();
    }

    public void fillRemainingEvilListPlaces() {
        int listCount = evilListAdapter.getCount();
        for (int i = 0; i < evilCount - listCount; i++) {
            ICharacter minion = CharacterFactory.createPlayer(CharacterFactory.CharacterType.MINION);
            minion.setCharacterName("Minion " + (i + 1));
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
        //String userName = SharedPrefManager.getStringDefaults("USERNAME", this);

        //Intent intent = new Intent(this, ___.class);

        //startActivity(intent);
        if (goodListAdapter.getCount() < goodCount || evilListAdapter.getCount() < evilCount) {
            ApplicationContext.showToast("Not enough characters!");
        } else {
//            Player p1 = new Player(); //TODO Testing purpose. Delete when done.
//            Player p2 = new Player();
//            Player p3 = new Player();
//            Player p4 = new Player();
//            Player p5 = new Player();
//            p1.userName = "Gearoid";
//            p2.userName = "Emily";
//            p3.userName = "Lemon";
//            p4.userName = "Kabuki";
//            p4.hasLadyOfLake = true;
//            p5.userName = "Mopsy";
//            Session.masterAllPlayers.add(p1);
//            Session.masterAllPlayers.add(p2);
//            Session.masterAllPlayers.add(p3);
//            Session.masterAllPlayers.add(p4);
//            Session.masterAllPlayers.add(p5);

            checkAndAssignLadyOfLake();
            assignAllPlayersCharacters(getCombinedGoodAndEvilList());
            initialiseLeaderOrderedAllPlayers();

            final Packet.Packet_StartGame packet = (Packet.Packet_StartGame) PacketFactory.createPack(PacketFactory.PacketType.START_GAME);

            packet.allPlayersBasic = new ArrayList<>();
            packet.leaderOrderList = new ArrayList<>();
            packet.allPlayersBasic.addAll(Session.allPlayersBasic);
            packet.leaderOrderList.addAll(Session.leaderOrderList);
            packet.currentBoard = currentBoard;

            Thread thread = new Thread() {
                @Override
                public void run() {
                    ServerInstance.server.getServer().sendToAllTCP(packet);
                }
            };
            thread.start();

//            Intent intent = new Intent(this, GameActivity.class);
//            intent.putExtra("BOARD", currentBoard);
//
//            startActivity(intent);
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

    public void checkAndAssignLadyOfLake(){
        Log.d("GameSetup", "Checking if lady of lake has been checked");

        if(ladyOfLake){
            Log.d("GameSetup", "Assigning lady of lake");

            Random ran = new Random();
            final int randomIndex = ran.nextInt(Session.allPlayersBasic.size());

            Session.allPlayersBasic.get(randomIndex).hasLadyOfLake = true;
            final Packet.Packet_IsLadyOfLake packet = (Packet.Packet_IsLadyOfLake) PacketFactory.createPack(PacketFactory.PacketType.IS_LADYOFLAKE);
            packet.isLadyOfLake = true;

            Thread thread = new Thread() {
                @Override
                public void run() {
                    Session.masterAllPlayers.get(randomIndex).playerConnection.sendTCP(packet);
                }
            };
            thread.start();

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
        Collections.shuffle(chosenCharacters);
        Session.allCharacters = new ArrayList<>();
        Session.allCharacters.addAll(chosenCharacters);// TODO is allCharacters necessary???

        Log.d("GameSetup", "Assigning players characters");

        if (chosenCharacters.size() == Session.allPlayersBasic.size()) {

            for (int i = 0; i < Session.allPlayersBasic.size(); i++) {
                Log.d("GameSetup", "Character: " + chosenCharacters.get(i).getCharacterName());
                Session.allPlayersBasic.get(i).character = chosenCharacters.get(i);
            }
        }
    }

    public static void initialiseLeaderOrderedAllPlayers() {
        Session.leaderOrderList = new ArrayList<Integer>();
        Session.leaderOrderIterator = 0;

        for(int i=0; i < Session.allPlayersBasic.size(); i++){
            Session.leaderOrderList.add(i);
            Log.d("GameSetup", "leaderOrderList - " +  Session.leaderOrderList.get(i));
        }

        Collections.shuffle(Session.leaderOrderList);

        if(!Session.allPlayersBasic.isEmpty()){
            Session.allPlayersBasic.get(Session.leaderOrderList.get(Session.leaderOrderIterator)).isLeader = true;
        }

//        Session.leaderOrderAllPlayers = new ArrayList<Player>();
//        Session.leaderOrderAllPlayers.addAll(Session.masterAllPlayers);
//        Collections.shuffle(Session.leaderOrderAllPlayers);
//        if(!Session.leaderOrderAllPlayers.isEmpty()){
//            Session.leaderOrderAllPlayers.get(0).isLeader = true;
//        }
    }
   
}
