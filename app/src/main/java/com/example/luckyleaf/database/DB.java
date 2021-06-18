package com.example.luckyleaf.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.luckyleaf.dataholders.LeafSensor;

@Database(entities = {LeafSensor.class}, version = 5, exportSchema = false)
@TypeConverters({SensorTypeConverterHelper.class})
public abstract class DB extends RoomDatabase {
    private static DB INSTANCE;

    public static DB getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), DB.class, "LuckyLeaf_db")
                    .fallbackToDestructiveMigration() // this will recreate db if no migration method found
                    .build();
        }
        return INSTANCE;
    }
    public abstract LeafSensorDAO leafSensorDAO();
}
