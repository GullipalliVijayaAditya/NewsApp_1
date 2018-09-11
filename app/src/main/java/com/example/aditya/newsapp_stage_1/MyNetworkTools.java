package com.example.aditya.newsapp_stage_1;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

@SuppressWarnings("ConstantConditions")
class MyNetworkTools {
    private final Context context;

    public MyNetworkTools(Context context) {
        this.context = context;
    }

    public boolean Offline() {
        ConnectivityManager connectivityManager;
        boolean connected = false;
        try {
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = (networkInfo != null) && networkInfo.isAvailable() && networkInfo.isConnected();
            return !connected;
        } catch (Exception e) {
        }
        return !connected;
    }

    public boolean Online() {
        return !Offline();
    }
}