package com.example.clementlam.weatherForecastApp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShowWeather extends AppCompatActivity {

    private LinearLayout recommendationBlock;

    private TextView locationTV;
    private TextView weatherTV;
    private TextView maxTempTV;
    private TextView minTempTV;
    private TextView recommendationTV;

    private ImageView weatherIconIV;

    private String weather;
    private String maxTemp;
    private String minTemp;

    private String selectedLocation;

    private boolean sunnyFlag;
    private boolean cloudyFlag;
    private boolean rainyFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_weather);

        selectedLocation = this.getIntent().getStringExtra("SELECTED_LOCATION");

        setupComponents();

        getWeatherStatusFromXML();

        setWeatherStatus();
        setWeatherIcon();
        setRecommendation();
    }

    private void setupComponents() {
        recommendationBlock = (LinearLayout) findViewById(R.id.recommendationBlock);
        locationTV = (TextView) findViewById(R.id.location);
        weatherTV = (TextView) findViewById(R.id.weather);
        maxTempTV = (TextView) findViewById(R.id.maxTemp);
        minTempTV = (TextView) findViewById(R.id.minTemp);
        recommendationTV = (TextView) findViewById(R.id.recommendation);
        weatherIconIV = (ImageView) findViewById(R.id.weatherIcon);
    }

    private void getWeatherStatusFromXML() {
        weather = this.getIntent().getStringExtra("WEATHER");
        maxTemp = this.getIntent().getStringExtra("MAX_TEMP");
        minTemp = this.getIntent().getStringExtra("MIN_TEMP");
    }

    private void setWeatherStatus() {
        locationTV.setText(selectedLocation);
        weatherTV.setText(weather);
        maxTempTV.setText(maxTemp + "°C");
        minTempTV.setText(minTemp + "°C");
    }

    private void setWeatherIcon() {
        sunnyFlag = false;
        cloudyFlag = false;
        rainyFlag = false;
        for(int i = 0 ; i < weather.length() ; i++) {
            if(weather.charAt(i) == '晴') {
                sunnyFlag = true;
            }
            else if(weather.charAt(i) == '雨') {
                rainyFlag = true;
            }
            else if(weather.charAt(i) == '雲') {
                cloudyFlag = true;
            }
        }
        if(rainyFlag) {
            weatherIconIV.setImageResource(R.drawable.rainy);
        }
        else if (sunnyFlag && cloudyFlag) {
            weatherIconIV.setImageResource(R.drawable.cloudy_1);
        }
        else if (cloudyFlag) {
            weatherIconIV.setImageResource(R.drawable.cloudy);
        }
        else {
            weatherIconIV.setImageResource(R.drawable.sunny);
        }
    }

    private void setRecommendation() {
        String recommendationText = "";
        if(rainyFlag && Integer.parseInt(minTemp) < 15) {
            recommendationBlock.setVisibility(View.VISIBLE);
            recommendationText += getResources().getString(R.string.lowTempAlert) + "\n" + getResources().getString(R.string.rainAlert);
            recommendationTV.setText(recommendationText);
        }
        else if(rainyFlag) {
            recommendationBlock.setVisibility(View.VISIBLE);
            recommendationText += getResources().getString(R.string.rainAlert);
            recommendationTV.setText(recommendationText);
        }
        else if(Integer.parseInt(minTemp) < 15) {
            recommendationBlock.setVisibility(View.VISIBLE);
            recommendationText += getResources().getString(R.string.lowTempAlert);
            recommendationTV.setText(recommendationText);
        }
        else {
            recommendationBlock.setVisibility(View.INVISIBLE);
            recommendationTV.setText("");
        }
    }

    public void backBtnOnClick(View view) {
        Intent intent = new Intent(ShowWeather.this, MainActivity.class);
        this.finish();
        startActivity(intent);
    }

    public void sw_settingsBtnOnClick(View view) {
        Intent intent = new Intent(ShowWeather.this, Settings.class);
        startActivity(intent);
    }
}
