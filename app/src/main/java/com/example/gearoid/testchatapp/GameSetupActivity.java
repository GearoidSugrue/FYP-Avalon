package com.example.gearoid.testchatapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.gearoid.testchatapp.character.ICharacter;

import java.util.ArrayList;


public class GameSetupActivity extends ActionBarActivity {

    Board currentBoard;
    int playerCount, evilCount, goodCount;
    ArrayList<ICharacter> allCharacters;

    public enum Board {
        FIVE, SIX, SEVEN, EIGHT, NINE, TEN;
    }

    public enum Quest {
        FIRST(1), SECOND(2), THIRD(3), FOURTH(4), FIFTH(5) ;
        private int value;

        private Quest(int value) {
            this.value = value;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        initializeButtons();

        //setup + calculate...

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

    private void initializeButtons() {
        Button button_startGame = (Button) findViewById(R.id.button_start_game);

        button_startGame.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startGameActivity();
            }
        });
    }

    public void startGameActivity(){
        //String userName = SharedPrefManager.getStringDefaults("USERNAME", this);

        //Intent intent = new Intent(this, ___.class);
        //intent.putExtra("USER_LIST", userList);
        //startActivity(intent);

    }
    public Board calculateBoard(int numberOfPlayers){
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

    public int calculateNumberOfEvilPlayers(Board board){

        switch (board) {
            case FIVE:
            case SIX: return 2;
            case SEVEN:
            case EIGHT:
            case NINE: return 3;
            case TEN: return 4;
        }
        return 2;
    }

    public static int[] getBoardConfiguration(Board board){

        int[] arr;
        switch (board) {
            case FIVE: return arr = new int[]{2,3,2,3,3};
            case SIX: return arr = new int[]{2,3,4,3,4};
            case SEVEN: return arr = new int[]{2,3,3,4,4};
            case EIGHT:
            case NINE:
            case TEN: return arr = new int[]{3,4,4,5,5};
        }
        return arr = new int[]{0,0,0,0,0};
    }

    public static int calculatePlayersOnQuest(Board board, Quest questNumber){//Move this???
        return getBoardConfiguration(board)[questNumber.value];
    }

}
