package com.example.gearoid.testchatapp.game.gamedialogfragments;

import android.app.DialogFragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gearoid.testchatapp.R;
import com.example.gearoid.testchatapp.utils.ApplicationContext;

/**
 * Created by gearoid on 07/04/15.
 */
public class InstructionsFragment extends DialogFragment {

    //Constants
    public static final String TAG = "InstructionsFragment";
    public static final String TOOLBAR_TITLE = "Game Guide";

    View mContentView;
    ImageView pageImage;
    TextView instructionsLabel;
    int pageNumber = 1;

    public static InstructionsFragment newInstance() {
        Log.d(TAG, "Creating instance of a InstructionsFragment");

        return new InstructionsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int style = DialogFragment.STYLE_NO_TITLE, theme = 0;

        setStyle(style, theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.instructions, container, false);
        Log.d(TAG, "onCreateView called");

        setRetainInstance(true);

        mContentView = rootView;

        initializeToolbar();

        pageImage = (ImageView) mContentView.findViewById(R.id.imageView_instructions);
        instructionsLabel = (TextView) mContentView.findViewById(R.id.textView_instructionsLabel);
        updatePage(pageNumber);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated called");

    }

    public void initializeToolbar() {
        Log.d(TAG, "initializeToolbar called");

        Toolbar mActionBarToolbar = (Toolbar) mContentView.findViewById(R.id.frag_instructions_toolbar);
        mActionBarToolbar.setTitle(TOOLBAR_TITLE);
        mActionBarToolbar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Handle the menu item
                        int id = item.getItemId();

                        //noinspection SimplifiableIfStatement
                        if (id == R.id.previous_page) {

                            if(pageNumber > 1){
                                pageNumber--;
                                updatePage(pageNumber);
                            } else {
                                ApplicationContext.showToast(getActivity().getString(R.string.alreadyAtBegining));
                            }

                            return true;
                        } else if (id == R.id.next_page) {
                            if(pageNumber < 8){
                                pageNumber++;
                                updatePage(pageNumber);
                            } else {
                                ApplicationContext.showToast(getActivity().getString(R.string.endOfGuide));
                            }
                        }

                        return true;
                    }
                });

        mActionBarToolbar.inflateMenu(R.menu.menu_guidetoolbar);

    }


    public void updatePage(int pageNumber){
        instructionsLabel.setText(getActivity().getString(R.string.page_) + pageNumber);

        pageImage.setImageDrawable(getInstructionPageDrawable(pageNumber));
    }

    public Drawable getInstructionPageDrawable(int pageNumber){

        switch (pageNumber){
            case 1: return getResources().getDrawable(R.drawable.instructions_page1);
            case 2: return getResources().getDrawable(R.drawable.instructions_page2);
            case 3: return getResources().getDrawable(R.drawable.instructions_page3);
            case 4: return getResources().getDrawable(R.drawable.instructions_page4);
            case 5: return getResources().getDrawable(R.drawable.instructions_page5);
            case 6: return getResources().getDrawable(R.drawable.instructions_page6);
            case 7: return getResources().getDrawable(R.drawable.instructions_page7);
            case 8: return getResources().getDrawable(R.drawable.instructions_page8);
        }
        return getResources().getDrawable(R.drawable.instructions_page1);
    }
}