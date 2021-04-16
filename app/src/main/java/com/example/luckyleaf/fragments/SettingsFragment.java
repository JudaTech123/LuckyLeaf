package com.example.luckyleaf.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.luckyleaf.MainActivity;
import com.example.luckyleaf.PrefsHelper;
import com.example.luckyleaf.R;
import com.example.luckyleaf.adapter.SettingsAdapter;
import com.example.luckyleaf.databinding.FragmentSettingsBinding;
import com.example.luckyleaf.dataholders.LeafSensor;
import com.example.luckyleaf.network.MqqtApi;
import com.example.luckyleaf.repo.SensorRepo;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment implements View.OnClickListener {
    FragmentSettingsBinding dataBinding;
    private SettingsAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        dataBinding.setClickListener(this);
        dataBinding.edtMqqtUrl.setText(PrefsHelper.getInstance().getMqttUrl());
        MqqtApi.getInstance().askConnectedStatus().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean==null)
                dataBinding.imgMqqtStatus.setImageResource(R.drawable.sensor_mode_undefined);
            else
                dataBinding.imgMqqtStatus.setImageResource(aBoolean ? R.drawable.sensor_mode_locked : R.drawable.sensor_mode_unlocked);
        });

        adapter = new SettingsAdapter(requireContext(), SensorRepo.getInstane().getSensors());
        dataBinding.setAdapter(adapter);
        LiveData<List<LeafSensor>> data =  SensorRepo.getInstane().askUpdates();
        data.observe(getViewLifecycleOwner(), new Observer<List<LeafSensor>>() {
            @Override
            public void onChanged(List<LeafSensor> leafSensors) {
                Log.d("juda","onChanged");
                adapter.updateSensorList(leafSensors);
            }
        });
        return dataBinding.getRoot();
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnConnect)
        {
            PrefsHelper.getInstance().setMqttUrl(dataBinding.edtMqqtUrl.getText().toString());
            ((MainActivity)getActivity()).connectToMqtt();
        }
        if (view.getId() == R.id.btnRemoveSensor)
        {
            LeafSensor markedItem = dataBinding.getAdapter().getMarkedItem();
            if (markedItem!=null)
            {
                SensorRepo.getInstane().removeSensor(markedItem);
            }
        }
        if (view.getId() == R.id.btnAddSensor)
        {
            String sensorName = dataBinding.edtSensorName.getText().toString();
            String sensorTopic = dataBinding.edtSensorID.getText().toString();
            if (sensorName==null || sensorName.isEmpty()) return;
            if (sensorTopic==null || sensorTopic.isEmpty()) return;
            LeafSensor item = new LeafSensor(sensorTopic,sensorName,true);
            if (item!=null)
            {
                SensorRepo.getInstane().addSensor(item);
            }
        }
    }
}
