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
    static CharacterCardFragment newInstance(ICharacter character) {
        CharacterCardFragment frag = new CharacterCardFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("character", character.getCharacterName());
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

                break;
            case ConstantsChara.ASSASSIN:

                break;
            case ConstantsChara.MORDRED:

                break;
            case ConstantsChara.OBERON:

                break;
            case ConstantsChara.MORGANA:

                break;
            //Add Knight 1, 2, 3 etc
            default:
                image.setImageDrawable(getResources().getDrawable(R.drawable.approve_token));
                //throw new IllegalArgumentException("Invalid character name: " + characterName );
        }


        // Do something else
        return rootView;
    }
}