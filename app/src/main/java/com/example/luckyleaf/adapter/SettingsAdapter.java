package com.example.luckyleaf.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.luckyleaf.Consts;
import com.example.luckyleaf.R;
import com.example.luckyleaf.databinding.ItemSensorBinding;
import com.example.luckyleaf.databinding.ItemSettingsBinding;
import com.example.luckyleaf.dataholders.LeafSensor;

import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsViewHolder>{
    private List<LeafSensor> sensorList;
    private final Context context;
    private int markedItem = -1;

    public void updateSensorList(List<LeafSensor> sensorList) {
        this.sensorList = sensorList;
        notifyDataSetChanged();
    }

    public SettingsAdapter(Context context,List<LeafSensor> sensorList)
    {
        this.context = context;
        this.sensorList = sensorList;
    }
    @NonNull
    @Override
    public SettingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSettingsBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_settings, parent, false);
        dataBinding.setItemClick(new SettingsViewHolder.SettingsItemClickCallback() {
            @Override
            public void sensorClicked(LeafSensor sensor, int index) {
                markedItem = index;
                notifyDataSetChanged();
            }
        });
        return new SettingsViewHolder(dataBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsViewHolder holder, int position) {
        if (position < 0 || position >= sensorList.size()) return;
        LeafSensor sensor = sensorList.get(position);
        if (sensor == null) return;
        holder.dataBinding.setMarked(position == markedItem);
        holder.dataBinding.setSensorIndex(position);
        holder.dataBinding.setSensorData(sensor);
    }

    public LeafSensor getMarkedItem() {
        if (markedItem < 0 || markedItem >= sensorList.size()) return null;
        return sensorList.get(markedItem);
    }

    @Override
    public int getItemCount() {
        return sensorList==null ? 0 : sensorList.size();
    }
}
