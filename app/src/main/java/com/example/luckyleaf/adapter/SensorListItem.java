package com.example.luckyleaf.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.example.luckyleaf.databinding.ItemSensorBinding;

public class SensorListItem extends RecyclerView.ViewHolder{
    public SensorListItem(@NonNull ViewDataBinding dataBinding) {
        super(dataBinding.getRoot());
    }
    public ViewDataBinding getMyDataBinding()
    {
        return null;
    }
}
