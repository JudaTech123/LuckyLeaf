package com.example.luckyleaf.fragments;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.example.luckyleaf.BackGroundService;
import com.example.luckyleaf.R;
import com.example.luckyleaf.adapter.DividerItemDecorator;
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
        SensorAdapter.SensorPressedCallback openFullScreen = (sensor, index,i) -> {
            if (i!=-1)
            {
                if (i==1)
                {
                    sensor.setEditMode(true);
                }
                if (i==2)
                {
                    sensor.setEditMode(false);
                }
                if (i==3)
                {
                    sensor.setNotifyMobile(!sensor.isNotifyMobile());
                }
                if (i==4)
                {
                    sensor.setNotifySensor(!sensor.isNotifySensor());
                }
//                if (i==5)
//                {
//                    sensor.setTimeAllowedUnlockActive(!sensor.isTimeAllowedUnlockActive());
//                }
                adapter.notifyItemChanged(index);
                return;
            }
            if (!sensor.hasStatus()) return;
            NavController navController = Navigation.findNavController(getActivity(), R.id.host_fragment);
            Bundle args = new Bundle();
            args.putString("LeafSensor", sensor.getMqttTopic());
            navController.navigate(R.id.action_startFragment_to_sensorScreen, args, null);
        };
        adapter = new SensorAdapter(requireContext(), SensorRepo.getInstane().getSensors(), openFullScreen);
        dataBinding.setSensorAdapter(adapter);
        LiveData<List<LeafSensor>> data =  SensorRepo.getInstane().askUpdates();
        data.observe(getViewLifecycleOwner(), new Observer<List<LeafSensor>>() {
            @Override
            public void onChanged(List<LeafSensor> leafSensors) {
                Log.d("juda","onChanged");
                adapter.updateSensorList(leafSensors);
            }
        });
        DividerItemDecorator simpleDividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(requireContext(),R.drawable.list_didiver));
        dataBinding.sensorList.addItemDecoration(simpleDividerItemDecoration);
        return dataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
