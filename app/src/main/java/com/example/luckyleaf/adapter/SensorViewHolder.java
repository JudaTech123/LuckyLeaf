package com.example.luckyleaf.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.example.luckyleaf.databinding.ItemSensorBinding;
import com.example.luckyleaf.dataholders.LeafSensor;

public class SensorViewHolder extends SensorListItem{
    final ItemSensorBinding dataBinding;
    public SensorViewHolder(@NonNull ItemSensorBinding dataBinding) {
        super(dataBinding);
        this.dataBinding = dataBinding;
    }

    @Override
    public ViewDataBinding getMyDataBinding()
    {
        return dataBinding;
    }


}
