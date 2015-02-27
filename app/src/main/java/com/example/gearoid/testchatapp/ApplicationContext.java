package com.example.gearoid.testchatapp;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

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

//    public static void showToast(String data) {
//        Toast.makeText(getContext(), data,
//                Toast.LENGTH_SHORT).show();
//    }

    public static void showToast(final String data){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), data, Toast.LENGTH_SHORT).show();
            }
            });
    }
}
