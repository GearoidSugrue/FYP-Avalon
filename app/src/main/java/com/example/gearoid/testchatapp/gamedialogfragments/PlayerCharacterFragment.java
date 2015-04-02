package com.example.gearoid.testchatapp.gamedialogfragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gearoid.testchatapp.DrawableFactory;
import com.example.gearoid.testchatapp.GameLogicFunctions;
import com.example.gearoid.testchatapp.PlayerListViewAdapter;
import com.example.gearoid.testchatapp.R;
import com.example.gearoid.testchatapp.character.ICharacter;
import com.example.gearoid.testchatapp.multiplayer.Player;
import com.example.gearoid.testchatapp.multiplayer.Session;

import java.util.ArrayList;

/**
 * Created by gearoid on 30/03/15.
 */
public class PlayerCharacterFragment extends DialogFragment {

    View mContentView = null;
    ListView visiblePlayersView;
    PlayerListViewAdapter adapterVisiblePlayers;
    ArrayList<Player> visiblePlayersArray;
    String characterName;

    public static PlayerCharacterFragment newInstance() {

        Log.d("PlayerCharacterFragment", "Creating instance of a PlayerCharacterFragment fragment");

        PlayerCharacterFragment frag = new PlayerCharacterFragment();

//        Player.getInstance().character = CharacterFactory.createPlayer(CharacterFactory.CharacterType.MERLIN); //TODO delete this...
//        Player.getInstance().character.setCharacterName("Merlin");

//        Bundle args = new Bundle();
        //args.putSerializable("BOARD", currentBoard);
//        args.putInt("TEAM_SIZE", teamSize);
//        args.putInt("QUEST_NUM", questNumber);
//        frag.setArguments(args);

        return frag;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("PlayerCharacterFragment", "onCreate called");

        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);

//        Bundle extras = getArguments();
//        teamSize = extras.getInt("TEAM_SIZE");
//        questNumber = extras.getInt("QUEST_NUM");

        ICharacter playerCharacter = GameLogicFunctions.getUserPlayer().character;
        Log.d("PlayerCharacterFragment", "Trying to get Characater name: " + playerCharacter.getShortDescription());
        characterName = playerCharacter.getCharacterName();

        visiblePlayersArray = new ArrayList<>();

        for (int i = 0; i < Session.allPlayers.size(); i++) {

            Player otherPlayer = Session.allPlayers.get(i);

            if (otherPlayer.character.isVisibleTo(playerCharacter)) {
                Log.d("PlayerCharacterFragment", otherPlayer.character + " is visible to player (" + playerCharacter + ")");
                visiblePlayersArray.add(otherPlayer);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.player_character, container, false);
        Log.d("PlayerCharacterFragment", "onCreateView called");

        mContentView = rootView;

        Toolbar mActionBarToolbar = (Toolbar) rootView.findViewById(R.id.frag_playerCharacter_toolbar);
        mActionBarToolbar.setTitle("Your Character");
        mActionBarToolbar.setLogo(getResources().getDrawable(R.drawable.icon_characterback));

        visiblePlayersView = (ListView) rootView.findViewById(R.id.listview_playersVisable);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("PlayerCharacterFragment", "onCreateView called");

        initializeButtons();

        adapterVisiblePlayers = new PlayerListViewAdapter(getActivity(), R.layout.row_players, android.R.id.text1, visiblePlayersArray);
        visiblePlayersView.setAdapter(adapterVisiblePlayers);

        setImageViewOnClickListeners();
        int adapterSize = adapterVisiblePlayers.getCount();

        TextView noVisiblePlayers = (TextView) mContentView.findViewById(R.id.textView_noPlayersVisable);


        View item = adapterVisiblePlayers.getView(0, null, visiblePlayersView);
        item.measure(0, 0);
        ViewGroup.LayoutParams params;

        if(adapterSize > 3){
            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (3.5 * item.getMeasuredHeight()));
            visiblePlayersView.setLayoutParams(params);
        } else if(adapterSize > 2){
            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (3 * item.getMeasuredHeight()));
            visiblePlayersView.setLayoutParams(params);
        }  else {
            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (2 * item.getMeasuredHeight()));
            visiblePlayersView.setLayoutParams(params);
        }

        noVisiblePlayers.setLayoutParams(params);
        noVisiblePlayers.setVisibility(View.INVISIBLE);

        visiblePlayersView.setVisibility(View.GONE);
    }


    public void setImageViewOnClickListeners() {
        final ImageView playerCharacter = (ImageView) mContentView.findViewById(R.id.imageView_playerCharacterCard);

        playerCharacter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionevent) {

                int action = motionevent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    Log.i("PlayerCharacter", "ImageView - MotionEvent.ACTION_DOWN");
                    //mHandler.removeCallbacks(mUpdateTaskup);
//                    mHandler.postAtTime(mUpdateTaskup,
//                            SystemClock.uptimeMillis() + 50);
                    ImageView playerCharacter = (ImageView) mContentView.findViewById(R.id.imageView_playerCharacterCard);
                    playerCharacter.setImageDrawable(DrawableFactory.getDrawable(getResources(), characterName));

                    TextView noVisiblePlayers = (TextView) mContentView.findViewById(R.id.textView_noPlayersVisable);

                    if (adapterVisiblePlayers.getCount() > 0) {
                        noVisiblePlayers.setVisibility(View.GONE);
                        visiblePlayersView.setVisibility(View.VISIBLE);

                    } else {
                        noVisiblePlayers.setVisibility(View.VISIBLE);
                    }
                } else if (action == MotionEvent.ACTION_UP) {
                    Log.i("PlayerCharacter", "ImageView - MotionEvent.ACTION_UP");

                    ImageView playerCharacter = (ImageView) mContentView.findViewById(R.id.imageView_playerCharacterCard);
                    playerCharacter.setImageDrawable(getResources().getDrawable(R.drawable.misc_characterback));

                    TextView noVisiblePlayers = (TextView) mContentView.findViewById(R.id.textView_noPlayersVisable);

                    if (adapterVisiblePlayers.getCount() > 0) {
                        visiblePlayersView.setVisibility(View.GONE);
                        noVisiblePlayers.setVisibility(View.INVISIBLE);

                    } else {
                        noVisiblePlayers.setVisibility(View.INVISIBLE);
                    }

                    // mHandler.removeCallbacks(mUpdateTaskup);
                }//end else
                return false;
            }
        });

//        chosenPlayer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//            }
//        });

    }

    public void initializeButtons() {

        Button dismiss = (Button) mContentView.findViewById(R.id.button_dismissPlayerCharacter);

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                closeDialog();
            }
        });

    }

    public void closeDialog() {
        this.dismiss();
    }
}