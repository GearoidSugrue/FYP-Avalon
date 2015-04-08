package com.example.gearoid.testchatapp.game.gamedialogfragments;

import android.app.DialogFragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gearoid.testchatapp.R;
import com.example.gearoid.testchatapp.utils.ApplicationContext;

/**
 * Created by gearoid on 07/04/15.
 */
public class InstructionsFragment extends DialogFragment {

    View mContentView;
    ImageView pageImage;
    TextView instructionsLabel;
    int pageNumber = 1;

    public static InstructionsFragment newInstance() {
        Log.d("InstructionsFragment", "Creating instance of a InstructionsFragment");

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
        Log.d("InstructionsFragment", "onCreateView called");

        setRetainInstance(true);

        mContentView = rootView;

        Toolbar mActionBarToolbar = (Toolbar) rootView.findViewById(R.id.frag_instructions_toolbar);
        mActionBarToolbar.setTitle("Game Guide");

        pageImage = (ImageView) mContentView.findViewById(R.id.imageView_instructions);
        instructionsLabel = (TextView) mContentView.findViewById(R.id.textView_instructionsLabel);
        updatePage(pageNumber);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("InstructionsFragment", "onCreateView called");

        initializeButtons();
    }



    public void initializeButtons() {

        Button previousPage = (Button) mContentView.findViewById(R.id.button_previousPage);
        Button nextPage = (Button) mContentView.findViewById(R.id.button_nextPage);

        previousPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(pageNumber > 1){
                    pageNumber--;
                    updatePage(pageNumber);
                } else {
                    ApplicationContext.showToast("Already at the beginning");
                }
            }
        });

        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(pageNumber < 8){
                    pageNumber++;
                    updatePage(pageNumber);
                } else {
                    ApplicationContext.showToast("End of Guide");
                }

            }
        });

    }

    public void updatePage(int pageNumber){
        instructionsLabel.setText("Page " + pageNumber);

        pageImage.setImageDrawable(getInstructionPageDrawable(pageNumber));
    }

    public Drawable getInstructionPageDrawable(int pageNumber){

        switch (pageNumber){
            case 1: return getResources().getDrawable(R.drawable.instructions_page1);
            case 2: return getResources().getDrawable(R.drawable.instructions_page2);
            case 3: return getResources().getDrawable(R.drawable.instructions_page3);
            case 4: return getResources().getDrawable(R.drawable.instructions_page1);
            case 5: return getResources().getDrawable(R.drawable.instructions_page1);
            case 6: return getResources().getDrawable(R.drawable.instructions_page1);
            case 7: return getResources().getDrawable(R.drawable.instructions_page1);
            case 8: return getResources().getDrawable(R.drawable.instructions_page1);
        }
        return getResources().getDrawable(R.drawable.instructions_page1);
    }
}