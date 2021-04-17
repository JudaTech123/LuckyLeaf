package com.example.luckyleaf.fragments;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import com.example.luckyleaf.R;
import com.example.luckyleaf.databinding.FragmentSensorSettingsBinding;
import com.example.luckyleaf.dataholders.LeafSensor;
import com.example.luckyleaf.repo.SensorRepo;

import java.util.List;

public class SensorSettingFragment extends Fragment implements View.OnClickListener {
    FragmentSensorSettingsBinding dataBinding;
    LeafSensor sensor = null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_sensor_settings, container, false);
        dataBinding.setClickListener(this);
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


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imgUnlockTimeStatus)
        {
            LeafSensor _sensor = dataBinding.getSensorData();
            _sensor.setTimeAllowedUnlockActive(!_sensor.isTimeAllowedUnlockActive());
            dataBinding.setSensorData(_sensor);
        }
        if (view.getId() == R.id.imgCheckTimerStatus)
        {
            LeafSensor _sensor = dataBinding.getSensorData();
            _sensor.setTimeInDayToChecActive(!_sensor.isTimeInDayToChecActive());
            dataBinding.setSensorData(_sensor);
        }
        if (view.getId() == R.id.btnPickTime)
        {
            LeafSensor _sensor = dataBinding.getSensorData();
            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {

                            LeafSensor _sensor = dataBinding.getSensorData();
                            _sensor.setTimeInDayToCheck(hourOfDay,minute);
                            dataBinding.setSensorData(_sensor);
                        }
                    }, (int)_sensor.getTimeInDayToCheckHour(), (int)_sensor.getTimeAllowedUnlockInMin(), true);
            timePickerDialog.show();
        }
        if (view.getId() == R.id.btnSaveData)
        {
            LeafSensor _sensor = dataBinding.getSensorData();
            if (dataBinding.edtUnlockSettings.getText()!=null) {
                try {
                    _sensor.setTimeAllowedUnlockInMin(Long.parseLong(dataBinding.edtUnlockSettings.getText().toString()));
                }catch (NumberFormatException ignore){
                    _sensor.setTimeAllowedUnlockInMin(0);
                }
            }
            SensorRepo.getInstane().updateSensor(_sensor);
        }
    }
}
