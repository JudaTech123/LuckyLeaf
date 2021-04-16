package com.example.luckyleaf.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.luckyleaf.databinding.ItemSensorBinding;
import com.example.luckyleaf.databinding.ItemSettingsBinding;
import com.example.luckyleaf.dataholders.LeafSensor;

public class SettingsViewHolder extends RecyclerView.ViewHolder{
    final ItemSettingsBinding dataBinding;
    public SettingsViewHolder(@NonNull ItemSettingsBinding dataBinding) {
        super(dataBinding.getRoot());
        this.dataBinding = dataBinding;
    }

    public interface SettingsItemClickCallback{
        void sensorClicked(LeafSensor sensor,int index);
    }
}
