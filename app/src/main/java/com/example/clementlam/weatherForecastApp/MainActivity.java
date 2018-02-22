package com.example.clementlam.weatherForecastApp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(this.getIntent().getBooleanExtra("OPEN_FROM_NOTIF", false)) {
            startShowWeather(this.getIntent().getIntExtra("LOCATION_INDEX", 0), this.getIntent().getStringExtra("SELECTED_LOCATION"));
        }
    }

    public void locationSelectBtnOnClick(View view) {
        final String[] locationList = getResources().getStringArray(R.array.locationList);
        AlertDialog.Builder dialog_list = new AlertDialog.Builder(MainActivity.this);
        dialog_list.setTitle(R.string.mainText);
        dialog_list.setItems(locationList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startShowWeather(which, locationList[which]);
            }
        });
        dialog_list.show();
    }

    public void main_settingsBtnOnClick(View view) {
        Intent intent = new Intent(MainActivity.this, Settings.class);
        startActivity(intent);
    }

    private void startShowWeather(int locationIndex, String locationSelected) {
        XMLParser xmlParser = new XMLParser(this, locationSelected, locationIndex);
        xmlParser.execute();
    }
}
