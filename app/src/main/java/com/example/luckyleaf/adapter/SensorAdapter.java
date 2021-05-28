package com.example.luckyleaf.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.example.luckyleaf.R;
import com.example.luckyleaf.databinding.ItemSensorBinding;
import com.example.luckyleaf.databinding.ItemSensorOpenBinding;
import com.example.luckyleaf.dataholders.LeafSensor;

import java.util.ArrayList;
import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorListItem>{
    private List<LeafSensor> sensorList;
    private final Context context;
    private final SensorPressedCallback callback;

    public void updateSensorList(List<LeafSensor> sensorList) {
        if (this.sensorList==null || sensorList==null)
            this.sensorList = sensorList;
        else
        {
            for (int storedSensorIndex=0;storedSensorIndex<this.sensorList.size();storedSensorIndex++)
            {
                for (int newSensorIndex=0;newSensorIndex<sensorList.size();newSensorIndex++)
                {
                    LeafSensor storedSensor = this.sensorList.get(storedSensorIndex);
                    LeafSensor newSensor = sensorList.get(newSensorIndex);
                    if (!storedSensor.getSensorName().equals(newSensor.getSensorName())) continue;
                    storedSensor.updateItem(newSensor);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateItem(LeafSensor sensor,int index) {
        if (index==-1 || index>sensorList.size()) return;
        sensorList.get(index).updateItem(sensor);
        notifyItemChanged(index);
    }

    public SensorAdapter(Context context, ArrayList<LeafSensor> sensorList, SensorPressedCallback callback)
    {
        this.sensorList = sensorList;
        this.context = context;
        this.callback = callback;
    }

    @Override
    public int getItemViewType(int position) {
        if (sensorList==null || position < 0 || position >= sensorList.size()) super.getItemViewType(position);
        LeafSensor sensor = sensorList.get(position);
        if (sensor.isEditMode())
            return 1;
        else
            return 0;
    }

    @NonNull
    @Override
    public SensorListItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==0)
        {
            ItemSensorBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_sensor, parent, false);
            dataBinding.setSensorIndex(-1);
            dataBinding.setItemClick(callback);
            return new SensorViewHolder(dataBinding);
        }
        else
        {
            ItemSensorOpenBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_sensor_open, parent, false);
            dataBinding.setSensorIndex(-1);
            dataBinding.setItemClicked(callback);
            return new SensorEditViewHolder(dataBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SensorListItem holder, int position) {
        if (position < 0 || position >= sensorList.size()) return;
        LeafSensor sensor = sensorList.get(position);
        if (sensor == null) return;


        if (getItemViewType(position)==0)
        {
            ViewDataBinding dataBinding = holder.getMyDataBinding();
            ItemSensorBinding dataBind = (ItemSensorBinding) dataBinding;
            dataBind.setSensorIndex(position);
            dataBind.setSensorData(sensor);
        }
        if (getItemViewType(position)==1)
        {
            ViewDataBinding dataBinding = holder.getMyDataBinding();
            ItemSensorOpenBinding dataBind = (ItemSensorOpenBinding) dataBinding;
            dataBind.setSensorIndex(position);
            dataBind.setSensorData(sensor);
        }
    }

    @Override
    public int getItemCount() {
        return (sensorList == null) ? 0 : sensorList.size();
    }

    public interface SensorPressedCallback{
        void sensorClicked(LeafSensor sensor, int index,int clickType);
    }
}
