package com.example.luckyleaf.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import com.example.luckyleaf.R;
import com.example.luckyleaf.databinding.FragmentSensorScreenBinding;
import com.example.luckyleaf.dataholders.LeafSensor;
import com.example.luckyleaf.repo.SensorRepo;

import java.util.ArrayList;
import java.util.List;

public class SensorFragment extends Fragment {
    LeafSensor sensor = null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentSensorScreenBinding dataBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_sensor_screen, container, false);

        Bundle args = getArguments();
        if (args!=null)
        {
            if (args.containsKey("LeafSensor"))
            {
                String sensorID = args.getString("LeafSensor");
                sensor = SensorRepo.getInstane().getSensor(sensorID);
                dataBinding.setSensorData(sensor);
            }
        }
        if (sensor!=null)
        {
            LiveData<List<LeafSensor>> data =  SensorRepo.getInstane().askUpdates();
            data.observe(getViewLifecycleOwner(), leafSensors -> {
                for (LeafSensor _sensor : leafSensors)
                {
                    if (_sensor.getMqttTopic().equals(sensor.getMqttTopic()))
                    {
                        dataBinding.setSensorData(_sensor);
                        break;
                    }
                }
            });
        }


        return dataBinding.getRoot();
    }

}
