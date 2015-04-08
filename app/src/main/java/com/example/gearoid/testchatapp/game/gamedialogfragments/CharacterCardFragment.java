package com.example.gearoid.testchatapp.game.gamedialogfragments;

import android.graphics.Color;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gearoid.testchatapp.game.DrawableFactory;
import com.example.gearoid.testchatapp.R;

/**
 * Created by gearoid on 19/03/15.
 */
public class CharacterCardFragment extends DialogFragment {

    String characterName;

    public static CharacterCardFragment newInstance(String name) {
        CharacterCardFragment frag = new CharacterCardFragment();

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
}