package com.example.gearoid.testchatapp;

import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.TextView;

import com.example.gearoid.testchatapp.wifidirect.peerdiscovery.WiFiDirectActivity;
import com.example.gearoid.testchatapp.wifidirect.servicediscovery.WiFiDirectServiceActivity;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.activity_main);


        initializeButtons();

        String userName = SharedPrefManager.getStringDefaults("USERNAME", this);
        if (userName.equals("")) {
            showDialogEditName();
        }
        setTextView_PlayerName();

        //run();
    }

    private void run() {//????
        Class activity;
        try {
            activity = Class.forName("com.example.gearoid.testchatapp." + "ChatClientAndServer");
            Intent openActivity = new Intent(MainActivity.this, activity);
            startActivity(openActivity);
            finish();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initializeButtons() {
        Button button_editName = (Button) findViewById(R.id.button_edit_name);
        Button button_hostGame = (Button) findViewById(R.id.button_Host);
        Button button_joinGame = (Button) findViewById(R.id.button_Join);

        Button button_hostService = (Button) findViewById(R.id.button_host_service);
        Button button_joinService = (Button) findViewById(R.id.button_join_service);

        button_editName.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialogEditName();
            }
        });
        button_hostGame.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //startHostActivity();
                startHostWiFiActivity();
            }
        });
        button_joinGame.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //startJoinActivity();
                startJoinWiFiActivity();
            }
        });
        button_joinService.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startJoinServiceActivity();
            }
        });
        button_hostService.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startHostServiceActivity();
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startJoinWiFiActivity() {
        WiFiDirectActivity.groupOwnerIntent = 0; //doesn't want to be group owner
        Intent intent = new Intent(this, WiFiDirectActivity.class);
        //intent.putExtra("USER_LIST", userList);
        startActivity(intent);
    }

    public void startHostWiFiActivity() {
        WiFiDirectActivity.groupOwnerIntent = 15; //wants to be group owner
        Intent intent = new Intent(this, WiFiDirectActivity.class);
        //intent.putExtra("USER_LIST", userList);
        startActivity(intent);
    }

    public void startJoinServiceActivity() {
        SharedPrefManager.setDefaults("HOST", false, getApplicationContext());

        WiFiDirectActivity.groupOwnerIntent = 0; //doesn't want to be group owner
        Intent intent = new Intent(this, WiFiDirectServiceActivity.class);
        intent.putExtra("isHost", false);
        startActivity(intent);
    }

    public void startHostServiceActivity() {
        SharedPrefManager.setDefaults("HOST", true, getApplicationContext());

        WiFiDirectActivity.groupOwnerIntent = 15; //wants to be group owner
        Intent intent = new Intent(this, WiFiDirectServiceActivity.class);
        intent.putExtra("isHost", true);
        startActivity(intent);
    }

    private void showDialogEditName() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.enter_name);
        //alert.setMessage(R.string.name_when_playing);
        alert.setCancelable(false);
        alert.setPositiveButton(android.R.string.ok, null);
        alert.setNegativeButton(android.R.string.cancel, null);

        final LayoutInflater inflater = this.getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        alert.setView(inflater.inflate(R.layout.dialog_edit_name, null));

        AlertDialog dialog = alert.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(final DialogInterface dialog) {
                Button buttonPos = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                Button buttonNeg = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                final EditText userInput = (EditText) ((AlertDialog) dialog).findViewById(R.id.username);
                final String userName = SharedPrefManager.getStringDefaults("USERNAME", getApplicationContext());

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
                        final String userName = SharedPrefManager.getStringDefaults("USERNAME", getApplicationContext());

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
        SharedPrefManager.setDefaults("USERNAME", newName, getApplicationContext());
        setTextView_PlayerName();
        ApplicationContext.showToast(getString(R.string.name_saved));
    }

    public void setTextView_PlayerName() {
        TextView textView = (TextView) findViewById(R.id.textView_username);
        final String userName = SharedPrefManager.getStringDefaults("USERNAME", this);
        textView.setText(userName);
    }


}
