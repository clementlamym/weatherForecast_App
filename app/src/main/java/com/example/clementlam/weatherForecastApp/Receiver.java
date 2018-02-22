package com.example.clementlam.weatherForecastApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Receiver extends BroadcastReceiver{
    public Receiver() {}

    @Override
    public void onReceive(Context context, Intent _intent) {
        Intent intent = new Intent(context, NewIntentService.class);
        context.startService(intent);
    }
}