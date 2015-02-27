package com.example.gearoid.testchatapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.gearoid.testchatapp.singletons.ServerInstance;


public class HostActivity extends ActionBarActivity {

    private static boolean showSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        //getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_host);


        Toolbar toolbar = (Toolbar) findViewById(R.id.host_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeButtons();

        WiFiDirectActivity.groupOwnerIntent = 15;//Wants to be group owner but value is ignored is the group was previously created


        //setSupportProgressBarIndeterminateVisibility(true);

        //ActionBar actionBar = getSupportActionBar();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_host, menu);
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

    @Override
    public void onResume() {
        super.onResume();
        if (showSpinner) {
            spinnerOn();
        } else {
            spinnerOff();
        }
    }

    private void initializeButtons() {
        Button button_setup_game = (Button) findViewById(R.id.button_setup_game);
        Button button_startWiFiDirect = (Button) findViewById(R.id.button_startWiFiDirect);


        button_setup_game.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startSetupGameActivity();
            }
        });
        button_startWiFiDirect.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            startWiFiDirectActivity();
        }
    });
}
    public void startWiFiDirectActivity(){
        //String userName = SharedPrefManager.getStringDefaults("USERNAME", this);
        Intent intent = new Intent(this, WiFiDirectActivity.class);
        //intent.putExtra("USER_LIST", userList);
        startActivity(intent);
    }

    public void startSetupGameActivity(){
        //String userName = SharedPrefManager.getStringDefaults("USERNAME", this);

        Intent intent = new Intent(this, SetupActivity.class);
        //intent.putExtra("USER_LIST", userList);
        startActivity(intent);
    }


    public void spinnerOn() {//needed????
        showSpinner = true;
        setSupportProgressBarIndeterminateVisibility(true);
    }

    public void spinnerOff() {
        showSpinner = false;
        setSupportProgressBarIndeterminateVisibility(false);
    }

}
