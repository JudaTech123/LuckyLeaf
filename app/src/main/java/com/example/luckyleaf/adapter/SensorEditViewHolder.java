package com.example.luckyleaf.adapter;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.example.luckyleaf.databinding.ItemSensorBinding;
import com.example.luckyleaf.databinding.ItemSensorOpenBinding;
import com.example.luckyleaf.dataholders.LeafSensor;

public class SensorEditViewHolder extends SensorListItem{
    final ItemSensorOpenBinding dataBinding;
    public SensorEditViewHolder(@NonNull ItemSensorOpenBinding dataBinding) {
        super(dataBinding);
        this.dataBinding = dataBinding;
    }

    @Override
    public ViewDataBinding getMyDataBinding()
    {
        return dataBinding;
    }
}
