package com.example.gearoid.testchatapp;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gearoid.testchatapp.game.gamedialogfragments.InstructionsFragment;
import com.example.gearoid.testchatapp.utils.ApplicationContext;
import com.example.gearoid.testchatapp.utils.SharedPrefManager;
import com.example.gearoid.testchatapp.wifidirect.servicediscovery.WiFiDirectServiceActivity;


public class MainActivity extends ActionBarActivity {

    //Constants
    public static final String GAME_GUIDE_DIALOG = "gameguidedialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.activity_main);

        initializeButtons();

        String userName = SharedPrefManager.getStringDefaults(SharedPrefManager.USERNAME, this);
        if (userName.equals("")) {
            showDialogEditName();
        }
        setTextView_PlayerName();

    }

    private void initializeButtons() {

        LinearLayout editNameButtonLayout = (LinearLayout) findViewById(R.id.linearLayout_editNameButton);
        editNameButtonLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialogEditName();
            }
        });

        LinearLayout hostButtonLayout = (LinearLayout) findViewById(R.id.linearLayout_hostButton);
        hostButtonLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startHostServiceActivity();
            }
        });

        LinearLayout joinButtonLayout = (LinearLayout) findViewById(R.id.linearLayout_joinButton);
        joinButtonLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startJoinServiceActivity();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.game_guide) {
            displayGameGuideFragment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void displayGameGuideFragment(){
        final DialogFragment newFragment = InstructionsFragment.newInstance();
        newFragment.show(getFragmentManager(), GAME_GUIDE_DIALOG);
    }

    public void startJoinServiceActivity() {
        SharedPrefManager.setDefaults(SharedPrefManager.HOST, false, getApplicationContext());

        Intent intent = new Intent(this, WiFiDirectServiceActivity.class);
        intent.putExtra(WiFiDirectServiceActivity.IS_HOST, false);
        startActivity(intent);
    }

    public void startHostServiceActivity() {
        SharedPrefManager.setDefaults(SharedPrefManager.HOST, true, getApplicationContext());

        Intent intent = new Intent(this, WiFiDirectServiceActivity.class);
        intent.putExtra(WiFiDirectServiceActivity.IS_HOST, true);
        startActivity(intent);
    }

    private void showDialogEditName() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.enter_name);
        alert.setCancelable(false);
        alert.setPositiveButton(android.R.string.ok, null);
        alert.setNegativeButton(android.R.string.cancel, null);

        final LayoutInflater inflater = this.getLayoutInflater();

        alert.setView(inflater.inflate(R.layout.dialog_edit_name, null));

        AlertDialog dialog = alert.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {
                Button buttonPos = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                Button buttonNeg = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                final EditText userInput = (EditText) ((AlertDialog) dialog).findViewById(R.id.username);
                final String userName = SharedPrefManager.getStringDefaults(SharedPrefManager.USERNAME, getApplicationContext());

                if (userName.equals("")) {
                    userInput.setHint(R.string.name_here);
                } else {
                    userInput.setText(userName);
                }

                buttonPos.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {//OK Button
                        String inputName = userInput.getText().toString();

                        if (isNameValid(inputName)) {
                            updateName(inputName);
                            dialog.dismiss();
                        }
                    }
                });
                buttonNeg.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) { //Cancel Button
                        final String userName = SharedPrefManager.getStringDefaults(SharedPrefManager.USERNAME, getApplicationContext());

                        if (isNameValid(userName)) {
                            dialog.dismiss();
                        }
                    }
                });
                //Keyboard 'Done' listener.
                userInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        boolean handled = false;
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            String inputName = userInput.getText().toString();

                            if (isNameValid(inputName)) {
                                updateName(inputName);
                                dialog.dismiss();
                            }
                            handled = true;
                        }
                        return handled;
                    }
                });

            }
        });
        dialog.show();
    }

    public boolean isNameValid(String newName) {
        if (newName.length() > 0) {
            return true;
        } else {
            ApplicationContext.showToast(getString(R.string.please_enter_name));
            return false;
        }
    }

    public void updateName(String newName) {
        SharedPrefManager.setDefaults(SharedPrefManager.USERNAME, newName, getApplicationContext());
        setTextView_PlayerName();
        ApplicationContext.showToast(getString(R.string.name_saved));
    }

    public void setTextView_PlayerName() {
        TextView textView = (TextView) findViewById(R.id.textView_username);
        final String userName = SharedPrefManager.getStringDefaults(SharedPrefManager.USERNAME, this);
        textView.setText(userName);
    }
}
