package com.example.clementlam.weatherForecastApp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class Settings extends AppCompatActivity {

    private CheckBox notifyChecked;

    private LinearLayout notifySettingBlock;

    private TextView nowNotifyTV;
    private TextView nowLocationTV;
    private TextView statusTextTV;

    private SharedPreferences settingSave;

    private int hour;
    private int min;
    private int itemPosition;
    private String selectedLocation;
    private boolean validate;
    private boolean notifyCheckBoxChecked;
    private boolean timeFlag;
    private boolean locationFlag;

    private CheckBox.OnCheckedChangeListener notifyCheckedListender = new CheckBox.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            notifyCheckBoxChecked = isChecked;
            if (notifyChecked.isChecked()) {
                notifySettingBlock.setVisibility(View.VISIBLE);
                setNowNotifyText();
                setNowLocationText();
                setStatusText();
            } else {
                notifySettingBlock.setVisibility(View.INVISIBLE);
                cancelNotification();
            }
            saveUserData();
        }
    };

    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour = hourOfDay;
            min = minute;
            timeFlag = true;
            validate = timeFlag && locationFlag;
            saveUserData();
            setNowNotifyText();
            setStatusText();
            setNotification();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setupComponent();
        setCheckBoxListener();

        loadUserData();

        restoreUIState();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        saveUserData();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        loadUserData();
        restoreUIState();
    }

    private void setupComponent() {
        notifyChecked = (CheckBox) findViewById(R.id.notifyCheck);
        notifySettingBlock = (LinearLayout) findViewById(R.id.notifySettingBlock);
        nowNotifyTV = (TextView) findViewById(R.id.nowNotify);
        nowLocationTV = (TextView) findViewById(R.id.nowLocation);
        statusTextTV = (TextView) findViewById(R.id.statusText);

        settingSave = Settings.this.getSharedPreferences("SETTING_SAVE", MODE_PRIVATE);
    }

    private void setCheckBoxListener() {
        notifyChecked.setOnCheckedChangeListener(notifyCheckedListender);
    }

    private void saveUserData() {
        settingSave.edit()
                .putBoolean("VALIDATE", timeFlag && locationFlag)
                .putInt("HOUR", hour)
                .putInt("MIN", min)
                .putString("SELECTED_LOCATION", selectedLocation)
                .putInt("ITEM_POSITION", itemPosition)
                .putBoolean("NOTIFY_CHECKED", notifyCheckBoxChecked)
                .apply();
    }

    private void loadUserData() {
        timeFlag = settingSave.getBoolean("VALIDATE", false);
        locationFlag = settingSave.getBoolean("VALIDATE", false);
        validate = settingSave.getBoolean("VALIDATE", false);
        hour = settingSave.getInt("HOUR", 0);
        min = settingSave.getInt("MIN", 0);
        selectedLocation = settingSave.getString("SELECTED_LOCATION", null);
        notifyCheckBoxChecked = settingSave.getBoolean("NOTIFY_CHECKED", false);
    }

    private void restoreUIState() {
            notifyChecked.setChecked(notifyCheckBoxChecked);
    }

    private void setNotification() {
        if(validate) {
            cancelNotification();

            Intent notifyIntent = new Intent(Settings.this, Receiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, min);

            AlarmManager alarmManager = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60 * 60 * 24, pendingIntent);
        }
    }

    private void cancelNotification() {
        Intent notifyIntent = new Intent(Settings.this, Receiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManger = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManger.cancel(pendingIntent);
    }

    private void setNowNotifyText() {
        if(timeFlag) {
            if(DateFormat.is24HourFormat(this)) {
                String time = generate24();
                String string = getResources().getString(R.string.nowNotify1) + " " + time + " " + getResources().getString(R.string.nowNotify2);
                nowNotifyTV.setText(string);
            }
            else {
                String time = convert24to12();
                String string = getResources().getString(R.string.nowNotify1) + " " + time + " " + getResources().getString(R.string.nowNotify2);
                nowNotifyTV.setText(string);
            }
        }
    }

    private void setNowLocationText() {
        if(locationFlag) {
            String nowLocationText = getNowLocationString();
            nowLocationTV.setText(nowLocationText);
        }
    }

    private void setStatusText() {
        if(!validate) {
            statusTextTV.setText(R.string.warningText);
            statusTextTV.setTextColor(getResources().getColor(R.color.red));
        }
        else {
            statusTextTV.setText(R.string.validateText);
            statusTextTV.setTextColor(getResources().getColor(R.color.black));
        }
    }

    private String generate24() {
        String time = String.format("%02d", hour) + ":" + String.format("%02d", min);
        return time;
    }

    private String convert24to12() {
        String time = "";
        if(hour > 12) {
            time += getResources().getString(R.string.pm) + " " + String.valueOf(hour - 12);
        }
        else if(hour == 12) {
            time += getResources().getString(R.string.pm) + " " + String.valueOf(hour);
        }
        else if(hour == 0) {
            time += getResources().getString(R.string.am) + " " + String.valueOf(hour + 12);
        }
        else {
            time += getResources().getString(R.string.am) + " " + String.valueOf(hour);
        }
        time += ":" + String.format("%02d", min);
        return time;
    }

    private String getNowLocationString() {
        String text = getResources().getString(R.string.nowLocation1) + " " + selectedLocation + " " + getResources().getString(R.string.nowLocation2);
        return text;
    }

    public void setTimeBtnOnClick(View view) {
        Calendar c = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, timeSetListener, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    public void setLocationBtnOnClick(View view) {
        final String[] locationList = getResources().getStringArray(R.array.locationList);
        AlertDialog.Builder dialog_list = new AlertDialog.Builder(Settings.this);
        dialog_list.setTitle(R.string.mainText);
        dialog_list.setItems(locationList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                itemPosition = which;
                selectedLocation = locationList[which];
                locationFlag = true;
                validate = timeFlag && locationFlag;
                saveUserData();
                setNowLocationText();
                setStatusText();
                setNotification();
            }
        });
        dialog_list.show();
    }
}
