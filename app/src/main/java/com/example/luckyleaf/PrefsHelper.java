package com.example.luckyleaf;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.luckyleaf.dataholders.LeafSensor;

import java.util.ArrayList;

public class PrefsHelper {
    static PrefsHelper instance;
    SharedPreferences pref = null;
    private final String MQTT_URL_KEY = "MQTT";
    private final String MQTT_SENSOR_KEY = "SENSORS";
    public static PrefsHelper getInstance() {
        if (instance==null) instance = new PrefsHelper();
        return instance;
    }

    public void initPrefs(Context context)
    {
        pref = context.getSharedPreferences("MyPref", 0);
    }

    public void setMqttUrl(String mqttUrl)
    {
        if (pref==null) return;
        pref.edit().putString(MQTT_URL_KEY,mqttUrl).apply();
    }

    public String getMqttUrl()
    {
        if (pref==null) return "";
        return pref.getString(MQTT_URL_KEY,"");
    }

    public ArrayList<LeafSensor> getSensorList()
    {
        if (pref==null) return new ArrayList<>();
        String sensorList = pref.getString(MQTT_SENSOR_KEY,"");
        ArrayList<LeafSensor> leafSensors = new ArrayList<>();
        if (!sensorList.isEmpty())
        {
            String [] sensors = sensorList.split("\n");
            if (sensors.length>0)
            {
                for (String sensor : sensors)
                {
                    String [] sensorParts = sensor.split("\t");
                    String name = sensorParts[0];
                    String id = sensorParts[1];
                    leafSensors.add(new LeafSensor(id,name,true));
                }
            }
        }
        return leafSensors;
    }

    public void removeSensor(LeafSensor sensorToRemove)
    {
        if(pref==null) return;
        ArrayList<LeafSensor> sensors = getSensorList();
        StringBuilder sensorString = new StringBuilder();
        for (LeafSensor sensor : sensors)
        {
            if (sensor.getMqttTopic().equals(sensorToRemove.getMqttTopic())) continue;
            if (sensorString.length()>0) sensorString.append("\n");
            sensorString.append(sensor.getSensorName()).append("\t").append(sensor.getMqttTopic());
        }
        pref.edit().putString(MQTT_SENSOR_KEY,sensorString.toString()).apply();
    }

    public void addSensor(LeafSensor newSensor)
    {
        if(pref==null) return;
        ArrayList<LeafSensor> sensors = getSensorList();
        sensors.add(newSensor);
        StringBuilder sensorString = new StringBuilder();
        for (LeafSensor sensor : sensors)
        {
            if (sensorString.length()>0) sensorString.append("\n");
            sensorString.append(sensor.getSensorName()).append("\t").append(sensor.getMqttTopic());
        }
        pref.edit().putString(MQTT_SENSOR_KEY,sensorString.toString()).apply();
    }


}
