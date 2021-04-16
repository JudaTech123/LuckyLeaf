package com.example.luckyleaf.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.luckyleaf.R;
import com.example.luckyleaf.databinding.ItemSensorBinding;
import com.example.luckyleaf.dataholders.LeafSensor;

import java.util.ArrayList;
import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorViewHolder>{
    private List<LeafSensor> sensorList;
    private final Context context;
    private final SensorViewHolder.SensorPressedCallback callback;
    private final SensorViewHolder.SensorPressedSwitchCallback switchCallback;
    private final SensorViewHolder.SensorPressedEditCallback editCallback;

    public void updateSensorList(List<LeafSensor> sensorList) {
        this.sensorList = sensorList;
        notifyDataSetChanged();
    }

    public void updateItem(LeafSensor sensor,int index) {
        if (index==-1 || index>sensorList.size()) return;
        sensorList.get(index).updateItem(sensor);
        notifyItemChanged(index);
    }

    public SensorAdapter(Context context, ArrayList<LeafSensor> sensorList, SensorViewHolder.SensorPressedCallback callback,SensorViewHolder.SensorPressedSwitchCallback switchCallback,SensorViewHolder.SensorPressedEditCallback editCallback)
    {
        this.sensorList = sensorList;
        this.context = context;
        this.callback = callback;
        this.switchCallback = switchCallback;
        this.editCallback = editCallback;
//        Log.d("juda","SensorAdapter size = " + sensorList.size());
    }
    @NonNull
    @Override
    public SensorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSensorBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_sensor, parent, false);
        dataBinding.setSensorIndex(-1);
        dataBinding.setOpenZoomClick(callback);
        dataBinding.setSwitchClick(switchCallback);
        dataBinding.setEditCallback(editCallback);
        return new SensorViewHolder(dataBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorViewHolder holder, int position) {
        if (position < 0 || position >= sensorList.size()) return;
        LeafSensor sensor = sensorList.get(position);
        if (sensor == null) return;
        holder.dataBinding.setSensorIndex(position);
        holder.dataBinding.setSensorData(sensor);
    }

    @Override
    public int getItemCount() {
        return (sensorList == null) ? 0 : sensorList.size();
    }
}
