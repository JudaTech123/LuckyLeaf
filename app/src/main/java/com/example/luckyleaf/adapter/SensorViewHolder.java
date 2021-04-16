package com.example.luckyleaf.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.luckyleaf.databinding.ItemSensorBinding;
import com.example.luckyleaf.dataholders.LeafSensor;

public class SensorViewHolder extends RecyclerView.ViewHolder{
    final ItemSensorBinding dataBinding;
    public SensorViewHolder(@NonNull ItemSensorBinding dataBinding) {
        super(dataBinding.getRoot());
        this.dataBinding = dataBinding;
    }
    public interface SensorPressedCallback{
        void sensorClicked(LeafSensor sensor,int index);
    }

    public interface SensorPressedSwitchCallback{
        void sensorClicked(LeafSensor sensor,int index);
    }

    public interface SensorPressedEditCallback{
        void sensorClicked(LeafSensor sensor,int index);
    }
}
