package com.example.luckyleaf.repo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.luckyleaf.PrefsHelper;
import com.example.luckyleaf.database.DB;
import com.example.luckyleaf.dataholders.LeafSensor;
import com.example.luckyleaf.dataholders.LeafStatus;
import com.example.luckyleaf.dataholders.MqqtMessageResponseModel;
import com.example.luckyleaf.dataholders.MqttMessage;
import com.example.luckyleaf.myApp;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SensorRepo {
    static SensorRepo instane;
    private DB db;
    private ArrayList<LeafSensor> sensors = new ArrayList<>();

    public static SensorRepo getInstane() {
        if (instane==null)
            instane = new SensorRepo();
        return instane;
    }

    SensorRepo()
    {
        loadSensors();
    }

    public ArrayList<LeafSensor> getSensors() {
        return sensors;
    }

    public void addSensor(LeafSensor sensor)
    {
        if (sensors==null)
            sensors = new ArrayList<>();
        if (getSensor(sensor.getMqttTopic())==null) {
            sensors.add(sensor);
            myApp.getSelf().getDbHandler().post(new Runnable() {
                @Override
                public void run() {
                    DB.getDatabase(myApp.getSelf()).leafSensorDAO().insert(sensor);
                }
            });
        }
    }

    public void removeSensor(LeafSensor sensor)
    {
        if (sensors==null)
            sensors = new ArrayList<>();
        LeafSensor sensorFound = getSensor(sensor.getMqttTopic());
        if (sensorFound!=null) {
            sensors.remove(sensorFound);
            myApp.getSelf().getDbHandler().post(new Runnable() {
                @Override
                public void run() {
                    DB.getDatabase(myApp.getSelf()).leafSensorDAO().removeSensor(sensor.getSensorName(),sensor.getMqttTopic());
                }
            });
        }
    }

    public LeafSensor getSensor(int index)
    {
        if (index==-1 || sensors==null || index>sensors.size())
            return null;
        return sensors.get(index);
    }

    public LeafSensor getSensor(String sensorId)
    {
        if (sensorId==null || sensors==null)
            return null;
        for(LeafSensor sensor : sensors)
        {
            if (sensorId.equals(sensor.getMqttTopic()))
                return sensor;
        }
        return null;
    }

    /**
     * this function will try to update the sensor with new message
     * @param msg       -   msg to parse
     * @param _sensor   -   sensor effected
     * @return          -   if sensor was updated
     */
    public boolean updateSensor(MqttMessage msg,LeafSensor _sensor)
    {
        try {
            LeafSensor sensor = getSensor(msg.getTopic());
            if (_sensor != null)
                sensor = _sensor;
            if (sensor == null)
                return false;
            final LeafSensor sensorToUpdate = sensor;
            String statusStr = msg.getMessage();
            if (sensor.isSameStatus(statusStr))
                return false;
            MqqtMessageResponseModel mqqtMessageData = new Gson().fromJson(statusStr, MqqtMessageResponseModel.class);
            sensorToUpdate.processStatus(mqqtMessageData);
            myApp.getSelf().getDbHandler().post(new Runnable() {
                @Override
                public void run() {
                    DB.getDatabase(myApp.getSelf()).leafSensorDAO().updateSensorData(sensorToUpdate.getStatus(), System.currentTimeMillis(), sensorToUpdate.getSensorName(), sensorToUpdate.isActive(),
                            sensorToUpdate.getState_event_group(), sensorToUpdate.getTime_based_alarm_time_amount(), sensorToUpdate.getTime_based_alarm_mobile_enable(), sensorToUpdate.getTime_based_alarm_buzzer_enable(),
                            sensorToUpdate.getHourly_based_alarm_hour_min_time(), sensorToUpdate.getHourly_based_alarm_mobile_enable(), sensorToUpdate.getHourly_based_alarm_buzzer_enable(), sensorToUpdate.getMqttTopic(),
                            sensorToUpdate.getWifi_ssid(), sensorToUpdate.getWifi_pswd());
                }
            });
            return true;
        }catch (Exception ignore){
            return false;
        }
    }

    public boolean updateSensor(LeafSensor sensor)
    {
        myApp.getSelf().getDbHandler().post(new Runnable() {
            @Override
            public void run() {
                DB.getDatabase(myApp.getSelf()).leafSensorDAO().updateSensorData(sensor.getStatus(),sensor.getUpdateDate(),sensor.getSensorName(),sensor.isActive(),
                        sensor.getState_event_group(),sensor.getTime_based_alarm_time_amount(),sensor.getTime_based_alarm_mobile_enable(),sensor.getTime_based_alarm_buzzer_enable(),
                        sensor.getHourly_based_alarm_hour_min_time(),sensor.getHourly_based_alarm_mobile_enable(),sensor.getHourly_based_alarm_buzzer_enable(),sensor.getMqttTopic(),
                        sensor.getWifi_ssid(),sensor.getWifi_pswd());
            }
        });
        return sensor.getStatus() == LeafStatus.alarm;
    }
    public boolean updateSensorWifi(LeafSensor sensor)
    {
        myApp.getSelf().getDbHandler().post(new Runnable() {
            @Override
            public void run() {
                DB.getDatabase(myApp.getSelf()).leafSensorDAO().updateSensorWifiData(sensor.getWifi_ssid(),sensor.getWifi_pswd(),sensor.isActive(),sensor.getSensorName(),sensor.getMqttTopic());
            }
        });
        return true;
    }
    public void updateAllSensorsToDB(final List<LeafSensor> sensorsList)
    {
        myApp.getSelf().getDbHandler().post(() -> DB.getDatabase(myApp.getSelf()).runInTransaction(() -> {
            for (LeafSensor sensorToUpdate : sensorsList)
            {
                DB.getDatabase(myApp.getSelf()).leafSensorDAO().updateSensorData(sensorToUpdate.getStatus(),sensorToUpdate.getUpdateDate(),sensorToUpdate.getSensorName(),sensorToUpdate.isActive(),
                        sensorToUpdate.getState_event_group(),sensorToUpdate.getTime_based_alarm_time_amount(),sensorToUpdate.getTime_based_alarm_mobile_enable(),sensorToUpdate.getTime_based_alarm_buzzer_enable(),
                        sensorToUpdate.getHourly_based_alarm_hour_min_time(),sensorToUpdate.getHourly_based_alarm_mobile_enable(),sensorToUpdate.getHourly_based_alarm_buzzer_enable(),sensorToUpdate.getMqttTopic(),
                        sensorToUpdate.getWifi_ssid(),sensorToUpdate.getWifi_pswd());
            }
            sensors = new ArrayList<>(sensorsList);
        }));
    }
    public void updateSensor(final LeafSensor sensorToUpdate,int index)
    {
        myApp.getSelf().getDbHandler().post(() -> DB.getDatabase(myApp.getSelf()).runInTransaction(() -> {
            DB.getDatabase(myApp.getSelf()).leafSensorDAO().updateSensorData(sensorToUpdate.getStatus(),sensorToUpdate.getUpdateDate(),sensorToUpdate.getSensorName(),sensorToUpdate.isActive(),
                    sensorToUpdate.getState_event_group(),sensorToUpdate.getTime_based_alarm_time_amount(),sensorToUpdate.getTime_based_alarm_mobile_enable(),sensorToUpdate.getTime_based_alarm_buzzer_enable(),
                    sensorToUpdate.getHourly_based_alarm_hour_min_time(),sensorToUpdate.getHourly_based_alarm_mobile_enable(),sensorToUpdate.getHourly_based_alarm_buzzer_enable(),sensorToUpdate.getMqttTopic(),
                    sensorToUpdate.getWifi_ssid(),sensorToUpdate.getWifi_pswd());
        }));
        sensors.get(index).updateItem(sensorToUpdate);
    }
    public void updateSensorList(ArrayList<LeafSensor> sensorList)
    {
        sensors = sensorList;
    }
    public LiveData<List<LeafSensor>> askUpdates()
    {
        return DB.getDatabase(myApp.getSelf()).leafSensorDAO().monitorSensorData();
    }

    private void loadSensors()
    {
        myApp.getSelf().getDbHandler().post(new Runnable() {
            @Override
            public void run() {
                List<LeafSensor> _sensors = DB.getDatabase(myApp.getSelf()).leafSensorDAO().getAllSensors();
                if (_sensors!=null)
                    sensors.addAll(_sensors);
            }
        });
    }

}
