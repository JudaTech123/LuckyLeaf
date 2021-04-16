package com.example.luckyleaf.database;

import androidx.room.TypeConverter;

import com.example.luckyleaf.dataholders.LeafSensor;
import com.example.luckyleaf.dataholders.LeafStatus;

public class SensorTypeConverterHelper {
    @TypeConverter
    public static LeafStatus statusFromString(String status) {
        if (status==null) return LeafStatus.unknown;
        if (status.equalsIgnoreCase("open"))
            return LeafStatus.open;
        else if (status.equalsIgnoreCase("locked"))
            return LeafStatus.locked;
        else if (status.equalsIgnoreCase("unlocked"))
            return LeafStatus.unlocked;
        else if (status.equalsIgnoreCase("alarm"))
            return LeafStatus.alarm;
        else
            return LeafStatus.unknown;
    }

    @TypeConverter
    public static String StatusToString(LeafStatus status) {
        switch (status)
        {
            case open:return "open";
            case locked:return "locked";
            case unlocked:return "unlocked";
            case alarm:return "alarm";
            default:return "";
        }
    }
}
