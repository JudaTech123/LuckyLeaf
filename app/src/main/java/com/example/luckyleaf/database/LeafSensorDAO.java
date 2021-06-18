package com.example.luckyleaf.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.luckyleaf.dataholders.LeafSensor;
import com.example.luckyleaf.dataholders.LeafStatus;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface LeafSensorDAO {
    @Insert(onConflict = REPLACE)
    long[] insert(LeafSensor... Accounts);

    @Query("SELECT * FROM LeafSensor ")
    LiveData<List<LeafSensor>> monitorSensorData();

    @Query("SELECT * FROM LeafSensor ")
    List<LeafSensor> getAllSensors();

    @Query("DELETE FROM LeafSensor WHERE sensorName = :sensorName AND mqttTopic = :sensorMqqt")
    void removeSensor(String sensorName,String sensorMqqt);

    @Query("UPDATE LeafSensor SET status = :status , updateDate =:updateTime , active = :active , " +
            "time_based_alarm_time_amount = :time_based_alarm_time_amount, state_event_group = :event_group , " +
            "time_based_alarm_mobile_enable = :time_based_alarm_mobile_enable , time_based_alarm_buzzer_enable = :time_based_alarm_buzzer_enable," +
            "hourly_based_alarm_hour_min_time =:hourly_based_alarm_hour_min_time , hourly_based_alarm_mobile_enable=:hourly_based_alarm_mobile_enable," +
            "hourly_based_alarm_buzzer_enable =:hourly_based_alarm_buzzer_enable, wifi_ssid=:wifi_ssid, wifi_pswd =:wifi_pwd " +
            "WHERE sensorName = :sensorName AND mqttTopic = :sensorMqqt")
    void updateSensorData(LeafStatus status,long updateTime,String sensorName,boolean active,long event_group,
                          long time_based_alarm_time_amount,boolean time_based_alarm_mobile_enable, boolean time_based_alarm_buzzer_enable,
                          long hourly_based_alarm_hour_min_time,boolean hourly_based_alarm_mobile_enable,boolean hourly_based_alarm_buzzer_enable,String sensorMqqt,
                          String wifi_ssid,String wifi_pwd);

    @Query("UPDATE LeafSensor SET wifi_ssid = :wifi_ssid , wifi_pswd =:wifi_pwd , active = :sensor_active WHERE sensorName = :sensorName AND mqttTopic = :sensorMqqt")
    void updateSensorWifiData(String wifi_ssid,String wifi_pwd,boolean sensor_active,String sensorName,String sensorMqqt);
}
