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

    @Query("UPDATE LeafSensor SET status = :status , updateDate =:updateTime , active = :active WHERE sensorName = :sensorName AND mqttTopic = :sensorMqqt")
    void updateSensorData(LeafStatus status,long updateTime,String sensorName,boolean active,String sensorMqqt);
}
