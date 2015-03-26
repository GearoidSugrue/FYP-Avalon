package com.example.gearoid.testchatapp;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by gearoid on 22/03/15.
 */
public class GameBoardFragment extends Fragment {

    private View mContentView = null;
    private GameLogicFunctions.Board gameBoard;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        gameBoard = (GameLogicFunctions.Board) getActivity().getIntent().getSerializableExtra("BOARD");
        Log.d("gameBoardFrag", "Got activity intent" + gameBoard);

        ImageView boardImage = (ImageView) mContentView.findViewById(R.id.imageView_gameBoard);

        Drawable image = getBoardImage(gameBoard);

        boardImage.setImageDrawable(image);
        //boardImage.setBackground(getBoardImage(gameBoard));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.game_board, null);


        //boardImage.setImageDrawable(getResources().getDrawable(R.drawable.misc_swordback));

        return mContentView;
    }


    public Drawable getBoardImage(GameLogicFunctions.Board board){

        Drawable image;

        switch (board) {
            case FIVE: return image = getResources().getDrawable(R.drawable.board_5);
            case SIX: return image = getResources().getDrawable(R.drawable.board_6);
            case SEVEN: return image = getResources().getDrawable(R.drawable.board_7);
            case EIGHT: return image = getResources().getDrawable(R.drawable.board_8);
            case NINE: return image = getResources().getDrawable(R.drawable.board_9);
            case TEN: return image = getResources().getDrawable(R.drawable.board_10);
        }
        return image = getResources().getDrawable(R.drawable.board_5);
    }

}
