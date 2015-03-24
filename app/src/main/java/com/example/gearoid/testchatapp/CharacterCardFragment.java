package com.example.gearoid.testchatapp;

import android.graphics.Color;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.gearoid.testchatapp.character.ConstantsChara;
import com.example.gearoid.testchatapp.character.ICharacter;

/**
 * Created by gearoid on 19/03/15.
 */
public class CharacterCardFragment extends DialogFragment {

    String characterName;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    static CharacterCardFragment newInstance(String name) {
        CharacterCardFragment frag = new CharacterCardFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("character", name);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        characterName = getArguments().getString("character");

        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;

        setStyle(style, theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.character_card, container,
                false);

        //rootView.setBackgroundColor(Color.TRANSPARENT);
        ImageView image = (ImageView) rootView.findViewById(R.id.imageView_characterCard);
        image.setBackgroundColor(Color.BLACK);
        //image.setBackgroundColor(Color.TRANSPARENT);

        switch (characterName) {
            case ConstantsChara.MERLIN:
                image.setImageDrawable(getResources().getDrawable(R.drawable.good_merlin));
                break;
            case ConstantsChara.PERCIVAL:
                image.setImageDrawable(getResources().getDrawable(R.drawable.good_percival));
                break;
            case ConstantsChara.ASSASSIN:
                image.setImageDrawable(getResources().getDrawable(R.drawable.evil_assassin));
                break;
            case ConstantsChara.MORDRED:
                image.setImageDrawable(getResources().getDrawable(R.drawable.evil_mordred));
                break;
            case ConstantsChara.OBERON:
                image.setImageDrawable(getResources().getDrawable(R.drawable.evil_oberon));
                break;
            case ConstantsChara.MORGANA:
                image.setImageDrawable(getResources().getDrawable(R.drawable.evil_morgana));
                break;
            case "Knight 1":
                image.setImageDrawable(getResources().getDrawable(R.drawable.good_knight1));
                break;
            case "Knight 2":
                image.setImageDrawable(getResources().getDrawable(R.drawable.good_knight2));
                break;
            case "Knight 3":
                image.setImageDrawable(getResources().getDrawable(R.drawable.good_knight3));
                break;
            case "Knight 4":
                image.setImageDrawable(getResources().getDrawable(R.drawable.good_knight4));
                break;
            case "Knight 5":
                image.setImageDrawable(getResources().getDrawable(R.drawable.good_knight5));
                break;
            case "Minion 1":
                image.setImageDrawable(getResources().getDrawable(R.drawable.evil_minion1));
                break;
            case "Minion 2":
                image.setImageDrawable(getResources().getDrawable(R.drawable.evil_minion2));
                break;
            case "Minion 3":
                image.setImageDrawable(getResources().getDrawable(R.drawable.evil_minion3));
                break;
            case "Lady of Lake":
                image.setImageDrawable(getResources().getDrawable(R.drawable.token_ladyoflake));
                break;
            default:
                image.setImageDrawable(getResources().getDrawable(R.drawable.token_approve));
                //throw new IllegalArgumentException("Invalid character name: " + characterName );
        }
        return rootView;
    }
}