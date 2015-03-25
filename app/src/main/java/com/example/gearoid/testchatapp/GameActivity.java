package com.example.gearoid.testchatapp;

import android.app.DialogFragment;
import android.content.res.Configuration;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;


public class GameActivity extends ActionBarActivity implements TeamVoteFragment.TeamVoteDialogListener {

    public GameBoardFragment gameBoardFrag;
    public GameSetupActivity.Board currentBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            ActionBar actionBar = getSupportActionBar();
            actionBar.hide();
        }

        setContentView(R.layout.activity_game);

        currentBoard = (GameSetupActivity.Board) getIntent().getSerializableExtra("BOARD");
        initialiseFragments();
        intialiseButtons();
    }

    private void initialiseFragments() {
        gameBoardFrag = (GameBoardFragment) getFragmentManager()
                .findFragmentById(R.id.layout_gameBoardFragment);

//        Bundle args = new Bundle();
//        args.putSerializable("BOARD", currentBoard);
//        gameBoardFrag.setArguments(args);
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

    public void intialiseButtons(){
        Button voteFragTest = (Button) findViewById(R.id.button_voteFragTest);

        voteFragTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment newFragment = TeamVoteFragment.newInstance(new int[] {1,2,3});
                newFragment.show(getFragmentManager(), "dialog");
            }
        });
    }


    @Override
    public void onVoteSelected(boolean voteResult) {
        Log.d("GameActivity", "Vote Result received from team vote dialog: " + voteResult);

        //Send vote result to server
    }
}
