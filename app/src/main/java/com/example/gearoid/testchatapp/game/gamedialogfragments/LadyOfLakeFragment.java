package com.example.gearoid.testchatapp.game.gamedialogfragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gearoid.testchatapp.PlayerListViewAdapter;
import com.example.gearoid.testchatapp.R;
import com.example.gearoid.testchatapp.character.ConstantsChara;
import com.example.gearoid.testchatapp.character.evil.EvilCharacter;
import com.example.gearoid.testchatapp.game.DrawableFactory;
import com.example.gearoid.testchatapp.multiplayer.Player;
import com.example.gearoid.testchatapp.multiplayer.Session;
import com.example.gearoid.testchatapp.utils.ApplicationContext;

import java.util.ArrayList;

/**
 * Created by gearoid on 30/03/15.
 */
public class LadyOfLakeFragment extends DialogFragment {


    //Constants
    public static final String TAG = "LadyOfLakeFragment";

    View mContentView = null;
    ListView chosenPlayerView;
    ListView candidatePlayersView;
    PlayerListViewAdapter adapterChosenPlayer;
    PlayerListViewAdapter adapterCandidatePlayers;
    ArrayList<Player> chosenPlayerArray;
    ArrayList<Player> candidatePlayersArray;
    boolean isFinished = false;
    int playerIndex = 0;

    public static LadyOfLakeFragment newInstance() {
        Log.d(TAG, "Creating instance of a LadyOfLakeFragment fragment");

        return new LadyOfLakeFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");

        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;
        setStyle(style, theme);

        chosenPlayerArray = new ArrayList<>();

        candidatePlayersArray = new ArrayList<>();
        candidatePlayersArray.addAll(Session.allPlayers);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.ladyoflake, container, false);
        Log.d(TAG, "onCreateView called");

        mContentView = rootView;

        Toolbar mActionBarToolbar = (Toolbar) rootView.findViewById(R.id.frag_ladyOfLake_toolbar);
        mActionBarToolbar.setTitle(ConstantsChara.LADY_OF_LAKE);
        mActionBarToolbar.setLogo(getResources().getDrawable(R.drawable.icon_ladyoflake));

        chosenPlayerView = (ListView) rootView.findViewById(R.id.listview_chosenLadyOfLakePlayer);
        candidatePlayersView = (ListView) rootView.findViewById(R.id.listview_ladyOfLakeCandidates);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated called");

        initializeButtons();

        adapterChosenPlayer = new PlayerListViewAdapter(getActivity(), R.layout.row_players, android.R.id.text1, chosenPlayerArray);
        chosenPlayerView.setAdapter(adapterChosenPlayer);

        adapterCandidatePlayers = new PlayerListViewAdapter(getActivity(), R.layout.row_players, android.R.id.text1, candidatePlayersArray);
        candidatePlayersView.setAdapter(adapterCandidatePlayers);

        setListViewOnclickListeners();
        setImageViewOnClickListeners();

        if (adapterCandidatePlayers.getCount() > 3) {
            View item = adapterCandidatePlayers.getView(0, null, candidatePlayersView);
            item.measure(0, 0);
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (3.5 * item.getMeasuredHeight()));
            candidatePlayersView.setLayoutParams(params);
        }

    }

    public void setListViewOnclickListeners() {

        candidatePlayersView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Player player = (Player) parent.getItemAtPosition(position);
                Log.d(TAG, "Player clicked:" + player.userName);

                if (!isFinished) {
                    if(!player.hasUsedLadyOfLake) {
                        adapterChosenPlayer.clear();
                        adapterChosenPlayer.add(player);
                        final ImageView chosenPlayer = (ImageView) mContentView.findViewById(R.id.imageView_ladyOfLakeloyaltyCard);
                        chosenPlayer.setVisibility(View.VISIBLE);
                    } else {
                        ApplicationContext.showToast(getActivity().getString(R.string.alreadyUsedLadyOfLake));
                    }
                }
            }
        });

        chosenPlayerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Player player = (Player) parent.getItemAtPosition(position);
                Log.d(TAG, "Player clicked:" + player.userName);

                if (!isFinished) {
                    adapterChosenPlayer.remove(player);
                    final ImageView chosenPlayer = (ImageView) mContentView.findViewById(R.id.imageView_ladyOfLakeloyaltyCard);
                    chosenPlayer.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void setImageViewOnClickListeners() {
        final ImageView chosenPlayer = (ImageView) mContentView.findViewById(R.id.imageView_ladyOfLakeloyaltyCard);

        chosenPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (adapterChosenPlayer.getCount() == 1) {

                    useLadyOfLake();
                }
            }
        });

    }

    public void initializeButtons() {

        Button finishGame = (Button) mContentView.findViewById(R.id.button_cancelLadyOfLake);

        finishGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isFinished) {
                    LadyOfLakeDialogListener activity = (LadyOfLakeDialogListener) getActivity();
                    activity.ladyOfLakeActivated(playerIndex);
                    closeDialog();
                } else {
                    closeDialog();
                }
            }
        });

    }

    public void useLadyOfLake() {

        isFinished = true;
        Player player = adapterChosenPlayer.getItem(0);
        playerIndex = player.playerID;
        final ImageView chosenPlayer = (ImageView) mContentView.findViewById(R.id.imageView_ladyOfLakeloyaltyCard);
        TextView result = (TextView) mContentView.findViewById(R.id.textView_ladyOfLakeResult);

        if (player.character instanceof EvilCharacter) {
            chosenPlayer.setImageDrawable(DrawableFactory.getDrawable(getResources(), getActivity().getString(R.string.redLoyaltyCard)));
        } else {
            chosenPlayer.setImageDrawable(DrawableFactory.getDrawable(getResources(), getActivity().getString(R.string.blueLoyaltyCard)));
            result.setText(getActivity().getString(R.string.playerIsGood));
            result.setTextColor(getResources().getColor(R.color.SlateBlue));
        }

        Button finishGame = (Button) mContentView.findViewById(R.id.button_cancelLadyOfLake);
        finishGame.setText(getActivity().getString(R.string.close));

        result.setVisibility(View.VISIBLE);

        View item = adapterCandidatePlayers.getView(0, null, candidatePlayersView);
        item.measure(0, 0);

        ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (3.5 * item.getMeasuredHeight()));
        result.setLayoutParams(params);

        candidatePlayersView.setVisibility(View.GONE);

    }


    public void closeDialog() {
        this.dismiss();
    }

    public interface LadyOfLakeDialogListener {
        void ladyOfLakeActivated(int playerIndex);
    }
}






