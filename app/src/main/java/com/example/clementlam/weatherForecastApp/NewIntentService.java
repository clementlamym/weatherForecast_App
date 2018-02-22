package com.example.clementlam.weatherForecastApp;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationManagerCompat;

public class NewIntentService extends IntentService {
    private static final int NOTIFICATION_ID = 3;

    private SharedPreferences settingSave;
    private String selectedLocation;

    private String weather;

    public NewIntentService() {
        super("NewIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        loadUserData();
        getWeatherStatusFromXML();
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(getResources().getString(R.string.app_name));
        builder.setContentText("本日" + selectedLocation + "天氣預報: " + weather);
        builder.setSmallIcon(R.drawable.notify_icon);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setDefaults(Notification.DEFAULT_ALL);
        Intent notifyIntent = new Intent(this, MainActivity.class);
        int locationIndex = 0;
        String[] itemArray = getResources().getStringArray(R.array.locationList);
        for(int i = 0 ; i < itemArray.length ; i++) {
            if(itemArray[i].equals(selectedLocation))
                locationIndex = i;
        }
        notifyIntent.putExtra("OPEN_FROM_NOTIF", true);
        notifyIntent.putExtra("SELECTED_LOCATION", selectedLocation);
        notifyIntent.putExtra("LOCATION_INDEX", locationIndex);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notificationCompat = builder.build();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(NOTIFICATION_ID, notificationCompat);
    }

    private void loadUserData() {
        settingSave = getSharedPreferences("SETTING_SAVE", MODE_PRIVATE);
        selectedLocation = settingSave.getString("SELECTED_LOCATION", null);
    }

    private void getWeatherStatusFromXML() {
        int locationIndex = settingSave.getInt("ITEM_POSITION", 0);
        XMLParser xmlParser = new XMLParser(locationIndex);
        xmlParser.execute();
        while(!xmlParser.getWorkStatus()) {}
        weather = xmlParser.getWeather();
    }

}
