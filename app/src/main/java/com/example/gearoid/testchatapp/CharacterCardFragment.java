package com.example.gearoid.testchatapp;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

        ImageView image = (ImageView) rootView.findViewById(R.id.imageView_characterCard);
        image.setBackgroundColor(Color.BLACK);

        if (characterName.equalsIgnoreCase("Lady of Lake")){
            image.setImageDrawable(DrawableFactory.getDrawable(getResources(), characterName));
            TextView description = (TextView) rootView.findViewById(R.id.textView_characterDescription);
            description.setVisibility(View.VISIBLE);

            rootView.setMinimumHeight(825);
        } else {
            image.setImageDrawable(DrawableFactory.getDrawable(getResources(), characterName));
        }
        return rootView;
    }

    public Drawable getCharacterImage(String characterName) {

        switch (characterName) {
            case ConstantsChara.MERLIN:
                return getResources().getDrawable(R.drawable.good_merlin);
            case ConstantsChara.PERCIVAL:
                return getResources().getDrawable(R.drawable.good_percival);
            case ConstantsChara.ASSASSIN:
                return getResources().getDrawable(R.drawable.evil_assassin);
            case ConstantsChara.MORDRED:
                return getResources().getDrawable(R.drawable.evil_mordred);
            case ConstantsChara.OBERON:
                return getResources().getDrawable(R.drawable.evil_oberon);
            case ConstantsChara.MORGANA:
                return getResources().getDrawable(R.drawable.evil_morgana);
            case "Knight 1":
                return getResources().getDrawable(R.drawable.good_knight1);
            case "Knight 2":
                return getResources().getDrawable(R.drawable.good_knight2);
            case "Knight 3":
                return getResources().getDrawable(R.drawable.good_knight3);
            case "Knight 4":
                return getResources().getDrawable(R.drawable.good_knight4);
            case "Knight 5":
                return getResources().getDrawable(R.drawable.good_knight5);
            case "Minion 1":
                return getResources().getDrawable(R.drawable.evil_minion1);
            case "Minion 2":
                return getResources().getDrawable(R.drawable.evil_minion2);
            case "Minion 3":
                return getResources().getDrawable(R.drawable.evil_minion3);
            default:
                return getResources().getDrawable(R.drawable.token_approve);
        }
    }
}