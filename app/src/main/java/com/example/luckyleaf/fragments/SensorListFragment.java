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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.luckyleaf.R;
import com.example.luckyleaf.adapter.SensorAdapter;
import com.example.luckyleaf.adapter.SensorViewHolder;
import com.example.luckyleaf.databinding.FragmentSensorListBinding;
import com.example.luckyleaf.dataholders.LeafSensor;
import com.example.luckyleaf.repo.SensorRepo;

import java.util.ArrayList;
import java.util.List;

public class SensorListFragment extends Fragment implements View.OnClickListener {
    private SensorAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentSensorListBinding dataBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_sensor_list, container, false);
        dataBinding.setClickListener(this);
        SensorViewHolder.SensorPressedCallback openFullScreen = (sensor, index) -> {
            if (!sensor.hasStatus()) return;
            NavController navController = Navigation.findNavController(getActivity(), R.id.host_fragment);
            Bundle args = new Bundle();
            args.putString("LeafSensor", sensor.getMqttTopic());
            navController.navigate(R.id.action_startFragment_to_sensorScreen, args, null);
        };
        SensorViewHolder.SensorPressedSwitchCallback switchCallback = (sensor, index) -> {
            sensor.toggleActive();
            adapter.updateItem(sensor,index);
            SensorRepo.getInstane().updateSensor(sensor);
        };
        SensorViewHolder.SensorPressedEditCallback  editCallback = new SensorViewHolder.SensorPressedEditCallback() {
            @Override
            public void sensorClicked(LeafSensor sensor, int index) {

            }
        };
        adapter = new SensorAdapter(requireContext(), SensorRepo.getInstane().getSensors(), openFullScreen, switchCallback, editCallback);
        dataBinding.setSensorAdapter(adapter);
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
        if (view.getId() == R.id.imgMenu)
        {
            NavController navController = Navigation.findNavController(getActivity(), R.id.host_fragment);
            Bundle args = new Bundle();
            navController.navigate(R.id.action_startFragment_to_settingsScreen, args, null);
        }
    }
}
