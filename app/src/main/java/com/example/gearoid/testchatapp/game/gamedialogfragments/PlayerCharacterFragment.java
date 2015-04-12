package com.example.gearoid.testchatapp.game.gamedialogfragments;

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

import com.example.gearoid.testchatapp.PlayerListViewAdapter;
import com.example.gearoid.testchatapp.R;
import com.example.gearoid.testchatapp.character.evil.EvilCharacter;
import com.example.gearoid.testchatapp.character.good.Merlin;
import com.example.gearoid.testchatapp.character.good.Percival;
import com.example.gearoid.testchatapp.game.DrawableFactory;
import com.example.gearoid.testchatapp.game.GameLogicFunctions;
import com.example.gearoid.testchatapp.multiplayer.Player;
import com.example.gearoid.testchatapp.multiplayer.Session;

import java.util.ArrayList;

/**
 * Created by gearoid on 30/03/15.
 */
public class PlayerCharacterFragment extends DialogFragment {

    //Constants
    public static final String TAG = "PlayerCharacterFragment";
    public static final String TOOLBAR_TITLE = "Your Character";

    View mContentView = null;
    ListView visiblePlayersView;
    PlayerListViewAdapter adapterVisiblePlayers;
    ArrayList<Player> visiblePlayersArray;
    String characterName;
    Player userPlayer;

    public static PlayerCharacterFragment newInstance() {
        Log.d(TAG, "Creating instance of a PlayerCharacterFragment fragment");

        return new PlayerCharacterFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");

        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);

        userPlayer = GameLogicFunctions.getUserPlayer();
        Log.d(TAG, "Trying to get Character name: " + userPlayer.character.getCharacterName());
        characterName = userPlayer.character.getCharacterName();

        visiblePlayersArray = new ArrayList<>();

        for (int i = 0; i < Session.allPlayers.size(); i++) {

            Player otherPlayer = Session.allPlayers.get(i);

            if (otherPlayer.character.isVisibleTo(userPlayer.character) && otherPlayer.playerID != userPlayer.playerID ) {
                Log.d(TAG, otherPlayer.character.getCharacterName() + " is visible to userPlayer (" + userPlayer.character.getCharacterName() + ")");

                visiblePlayersArray.add(otherPlayer);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.player_character, container, false);
        Log.d(TAG, "onCreateView called");

        mContentView = rootView;

        Toolbar mActionBarToolbar = (Toolbar) rootView.findViewById(R.id.frag_playerCharacter_toolbar);
        mActionBarToolbar.setTitle(TOOLBAR_TITLE);
        mActionBarToolbar.setLogo(getResources().getDrawable(R.drawable.icon_characterback));

        visiblePlayersView = (ListView) rootView.findViewById(R.id.listview_playersVisable);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated called");

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

                    ImageView playerCharacter = (ImageView) mContentView.findViewById(R.id.imageView_playerCharacterCard);
                    playerCharacter.setImageDrawable(DrawableFactory.getDrawable(getResources(), characterName));

                    TextView noVisiblePlayers = (TextView) mContentView.findViewById(R.id.textView_noPlayersVisable);

                    if (adapterVisiblePlayers.getCount() > 0) {
                        noVisiblePlayers.setVisibility(View.GONE);
                        visiblePlayersView.setVisibility(View.VISIBLE);

                    } else {
                        noVisiblePlayers.setVisibility(View.VISIBLE);
                    }

                    TextView visiblePlayersLabel = (TextView) mContentView.findViewById(R.id.textView_playersVisableLabel);

                    if(userPlayer.character instanceof Percival) {
                        visiblePlayersLabel.setText("Merlin's Identity");
                    } else if(userPlayer.character instanceof EvilCharacter || userPlayer.character instanceof Merlin){
                        visiblePlayersLabel.setText("Evil Players");
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

                    TextView visiblePlayersLabel = (TextView) mContentView.findViewById(R.id.textView_playersVisableLabel);

                    if(userPlayer.character instanceof Percival || userPlayer.character instanceof EvilCharacter || userPlayer.character instanceof Merlin) {
                        visiblePlayersLabel.setText(getActivity().getString(R.string.players_visible_text));
                    }

                }
                return false;
            }
        });
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