package com.example.gearoid.testchatapp.game;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.gearoid.testchatapp.R;
import com.example.gearoid.testchatapp.multiplayer.Session;

/**
 * Created by gearoid on 22/03/15.
 */
public class GameBoardFragment extends Fragment {

    private View mContentView = null;
    private GameLogicFunctions.Board gameBoard;

    ImageView token1;
    ImageView token2;
    ImageView token3;
    ImageView token4;
    ImageView token5;

    Drawable goodToken;
    Drawable evilToken;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("gameBoardFrag", "onActivityCreated called");

        setRetainInstance(true);

//        gameBoard = (GameLogicFunctions.Board) getActivity().getIntent().getSerializableExtra("BOARD");
//        Log.d("gameBoardFrag", "Got activity intent" + gameBoard);

        gameBoard = Session.currentBoard;

        ImageView boardImage = (ImageView) mContentView.findViewById(R.id.imageView_gameBoardLayer);
        Drawable image = getBoardImage(gameBoard);
        boardImage.setImageDrawable(image);

        goodToken = getResources().getDrawable(R.drawable.token_goodrounded);
        evilToken = getResources().getDrawable(R.drawable.token_evilrounded);

        //token1.setImageDrawable(goodToken);

        //boardImage.setBackground(getBoardImage(gameBoard));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.boardlayers, null);
        Log.d("gameBoardFrag", "onCreateView called");

        token1 = (ImageView) mContentView.findViewById(R.id.ImageView_Token01);
        token2 = (ImageView) mContentView.findViewById(R.id.ImageView_Token02);
        token3 = (ImageView) mContentView.findViewById(R.id.ImageView_Token03);
        token4 = (ImageView) mContentView.findViewById(R.id.ImageView_Token04);
        token5 = (ImageView) mContentView.findViewById(R.id.ImageView_Token05);

        setQuestResultTokensVisible();

        return mContentView;
    }

    public void setQuestResultTokensVisible() {
        Log.d("gameBoardFrag", "setQuestResultTokensVisible called");

        if (Session.clientQuestResults != null) {

            for (int i = 0; i < Session.clientQuestResults.size(); i++) {
                updateBoardQuestResult(Session.clientQuestResults.get(i));
            }
        }
    }

    public void updateBoardQuestResult(GameLogicFunctions.Quest quest) {

        switch (quest) {
            case FIRST:
                setQuestResult(token1, quest.getResult());
                break;
            case SECOND:
                setQuestResult(token2, quest.getResult());
                break;
            case THIRD:
                setQuestResult(token3, quest.getResult());
                break;
            case FOURTH:
                setQuestResult(token4, quest.getResult());
                break;
            case FIFTH:
                setQuestResult(token5, quest.getResult());
                break;
        }
    }

    public void setQuestResult(final ImageView token, final boolean result) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("GameBoardFragment", "runOnUiThread: GameBoardFragment.setQuestResult(...) ");

                if (result) {
                    token.setImageDrawable(goodToken);
                } else {
                    token.setImageDrawable(evilToken);
                }
                token.setVisibility(View.VISIBLE);
            }
        });
    }

    public Drawable getBoardImage(GameLogicFunctions.Board board) {

        switch (board) {
            case FIVE:
                return getResources().getDrawable(R.drawable.board_5);
            case SIX:
                return getResources().getDrawable(R.drawable.board_6);
            case SEVEN:
                return getResources().getDrawable(R.drawable.board_7);
            case EIGHT:
                return getResources().getDrawable(R.drawable.board_8);
            case NINE:
                return getResources().getDrawable(R.drawable.board_9);
            case TEN:
                return getResources().getDrawable(R.drawable.board_10);
        }
        return getResources().getDrawable(R.drawable.board_5);
    }

}
