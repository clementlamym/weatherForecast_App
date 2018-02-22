package com.example.clementlam.weatherForecastApp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import android.util.Log;


public class XMLParser extends AsyncTask {

    private static final int weatherList = 75;

    private String[] weatherStatus;

    private ProgressDialog dialog;
    private AlertDialog.Builder alertDialog;

    private String locationSelected;
    private int locationIndex;

    private MainActivity activity;

    private boolean workDone;
    private boolean downloadSucced;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        downloadSucced = false;
        if (activity != null) {
            dialog.setMessage(activity.getResources().getString(R.string.downloading));
            dialog.show();
        }
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            Log.v("XMLParser", "Downloading data");
            URL url = new URL("http://opendata.cwb.gov.tw/opendataapi?dataid=F-C0032-003&authorizationkey=CWB-61269E47-7EEA-401F-9F3F-196E0ACF15BC");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            InputStream stream = connection.getInputStream();

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(stream);

            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("weatherElement");
            for(int i = 0 ; i < nList.getLength() ; i++) {
                Node node = nList.item(i);
                if(node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element2 = (Element) node;
                    weatherStatus[i] = getValue("parameterName", element2);
                }
            }
            downloadSucced = true;
            Log.v("XMLParser", "Download completed");
        }
        catch (Exception e) {
            Log.v("XMLParser", "Error occured");
            downloadSucced = false;
            e.printStackTrace();
        }
        return weatherStatus;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        Log.v("XMLParser", "onPostExecute");
        if(activity != null) {
            if (dialog.isShowing()) {
                Log.v("XMLParser", "Dismissing dialog");
                dialog.dismiss();
            }
            if(downloadSucced) {
                Intent intent = new Intent(activity, ShowWeather.class);
                intent.putExtra("SELECTED_LOCATION", locationSelected);
                intent.putExtra("WEATHER", getWeather());
                intent.putExtra("MAX_TEMP", getMaxTemp());
                intent.putExtra("MIN_TEMP", getMinTemp());
                activity.startActivity(intent);
                activity.finish();
            }
            else {
                alertDialog.setMessage(R.string.downloadFailed);
                alertDialog.setNeutralButton(R.string.okText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                alertDialog.show();
            }
        }
        workDone = true;
    }

    public XMLParser(int _locationIndex) {
        workDone = false;
        locationIndex = _locationIndex * 3;
        weatherStatus = new String[weatherList];
    }

    public XMLParser(MainActivity _activty, String _locationSelected, int _locationIndex) {
        workDone = false;
        activity = _activty;
        locationSelected = _locationSelected;
        locationIndex = _locationIndex * 3;
        dialog = new ProgressDialog(activity);
        alertDialog = new AlertDialog.Builder(activity);
        weatherStatus = new String[weatherList];
    }

    public String getWeather() {
        return weatherStatus[locationIndex];
    }

    public String getMaxTemp() {
        return weatherStatus[locationIndex + 1];
    }

    public String getMinTemp() {
        return weatherStatus[locationIndex + 2];
    }

    public boolean getWorkStatus() {
        return workDone;
    }

    private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }
}
