package com.example.gearoid.testchatapp;

import android.app.ListFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gearoid.testchatapp.character.EvilCharacter;
import com.example.gearoid.testchatapp.character.ICharacter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gearoid on 17/03/15.
 */
public class GameSetupCharacterListFragment extends ListFragment {

    CharacterListAdapter listAdapter = null;
    View mContentView = null;
    private  List<ICharacter> characterList =  new ArrayList<ICharacter>();
    public boolean isOptionalCharacterList = false;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        listAdapter = new CharacterListAdapter(getActivity(), android.R.layout.simple_list_item_2, android.R.id.text1, characterList);
        setListAdapter(listAdapter);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {//Called after GameSetupActivity onCreate is called
        super.onActivityCreated(savedInstanceState);

        if(!isOptionalCharacterList) {
            TextView tv = (TextView) mContentView.findViewById(R.id.textview_characterListTitle);
            tv.setTypeface(null, Typeface.BOLD);
            tv.setTextColor(Color.BLACK);
        }

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int position, long id) {
                //TODO Do stuff...
                ICharacter character = (ICharacter) getListAdapter().getItem(position);
                //((DeviceActionListener) getActivity()).showDetails(device);
                Log.d("Fragment Characters", "Character long clicked:" + character.getCharacterName());
                ((CharacterListFragListener) getActivity()).displayCharacterCard(character.getCharacterName());


                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.character_list, null);
        //mContentView.setBackground(getResources().getDrawable(R.drawable.misc_blueloyalty));
        //mContentView.getBackground().setAlpha(120);

        return mContentView;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ICharacter character = (ICharacter) getListAdapter().getItem(position);
        //((DeviceActionListener) getActivity()).showDetails(device);
        Log.d("Fragment Characters", "Character clicked:" + character.getCharacterName());

        ((CharacterListFragListener) getActivity()).characterSelected(position, isOptionalCharacterList, listAdapter);

    }

    /**
     * Array adapter for ListFragment that maintains Character list.
     */
    public class CharacterListAdapter extends ArrayAdapter<ICharacter> {

        private List<ICharacter> items;

        /**
         * @param context
         * @param textViewResourceId
         * @param characters
         */
        public CharacterListAdapter(Context context, int resource, int textViewResourceId,
                                   List<ICharacter> characters) {
            super(context, resource, textViewResourceId, characters);
            this.items = characters;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(android.R.layout.simple_list_item_2, null);
            }
            ICharacter character = items.get(position);
            if (character != null) {
                TextView top = (TextView) v.findViewById(android.R.id.text1);
                TextView bottom = (TextView) v.findViewById(android.R.id.text2);


                if(top != null){
                    top.setText(character.getCharacterName());
                    top.setTextColor(Color.BLACK);
                }
                if(bottom != null){
                    bottom.setText(character.getShortDescription());
                    bottom.setTextColor(Color.rgb(55, 55, 55));
                }
                if(isOptionalCharacterList) {
                    if (character instanceof EvilCharacter) {//TODO ensure that this is only done on optional list
                        v.setBackground(getResources().getDrawable(R.drawable.misc_redloyaltystrip));
                        v.getBackground().setAlpha(150);
                        v.getBackground().setColorFilter(Color.argb(70, 255, 0, 0), PorterDuff.Mode.DARKEN);
                    } else {
                        v.setBackground(getResources().getDrawable(R.drawable.misc_blueloyaltystrip));
                        v.getBackground().setAlpha(150);
                        v.getBackground().setColorFilter(Color.argb(70, 0, 0, 255), PorterDuff.Mode.DARKEN);
                    }
                }

            }
            //v.setBackgroundColor(Color.argb(255, 255, 192, 192));
            return v;
        }
    }

    public void setTitleText(String text){
        TextView view = (TextView) mContentView.findViewById(R.id.textview_characterListTitle);
        view.setText(text);
    }

    public interface CharacterListFragListener {

        void characterSelected(int position, boolean isOptionalList, CharacterListAdapter listAdapter);
        void displayCharacterCard(String characterName);


    }

}
