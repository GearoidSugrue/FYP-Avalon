package com.example.gearoid.testchatapp.utils;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.gearoid.testchatapp.kryopackage.Packet;
import com.example.gearoid.testchatapp.kryopackage.PacketFactory;
import com.example.gearoid.testchatapp.singletons.PlayerConnection;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.gearoid.testchatapp.game.GameLogicFunctions.getUserPlayer;
import static com.example.gearoid.testchatapp.multiplayer.Session.client_sendPacketToServer;

/**
 * Created by gearoid on 19/01/15.
 */
public class ApplicationContext extends Application {

    //Instance of the current application.
    private static ApplicationContext instance;

    public ApplicationContext() {
        instance = this;
    }

    public static Context getContext() {
        if (instance == null) {
            instance = new ApplicationContext();
        }
        return instance;
    }

    public static void showToast(final String text){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
            }
            });
    }

    public static void showLongToast(final String text){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
            }
        });
    }

    private Timer mActivityTransitionTimer;
    private TimerTask mActivityTransitionTimerTask;
    public boolean wasInBackground;
    private final long MAX_ACTIVITY_TRANSITION_TIME_MS = 1500;


    public void startActivityTransitionTimer() {
        this.mActivityTransitionTimer = new Timer();
        this.mActivityTransitionTimerTask = new TimerTask() {
            public void run() {
                ApplicationContext.this.wasInBackground = true;
                Log.d("ApplicationContext", "User has left app");

                if(PlayerConnection.getInstance().playerID >= 0){
                    Packet.Packet_PlayerHasLeftApp packet = (Packet.Packet_PlayerHasLeftApp) PacketFactory.createPack(PacketFactory.PacketType.PLAYER_LEFT);
                    packet.playerName = SharedPrefManager.getStringDefaults("USERNAME", getApplicationContext());

                    client_sendPacketToServer(packet);
                }

            }
        };

        this.mActivityTransitionTimer.schedule(mActivityTransitionTimerTask,
                MAX_ACTIVITY_TRANSITION_TIME_MS);
    }

    public void stopActivityTransitionTimer() {
        if (this.mActivityTransitionTimerTask != null) {
            this.mActivityTransitionTimerTask.cancel();
        }

        if (this.mActivityTransitionTimer != null) {
            this.mActivityTransitionTimer.cancel();
        }

        this.wasInBackground = false;
    }
}
