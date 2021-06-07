package com.example.luckyleaf.fragments;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

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
    private Handler delayedHandler;
    private Handler dbUpdaterHandler;
    private void sendSettingsToSensor(LeafSensor sensor)
    {
        Intent mqttService = new Intent(requireContext(), BackGroundService.class);
        mqttService.putExtra(BackGroundService.UPDATE_SETTINGS, sensor.getSettingsAsJson());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(mqttService);
        }
        else
            requireContext().startService(mqttService);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentSensorListBinding dataBinding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_sensor_list, container, false);
        dataBinding.setClickListener(this);
        Runnable updateDB = () -> {
            SensorRepo.getInstane().updateAllSensorsToDB(adapter.getSensorList());
            for (LeafSensor sensor : adapter.getSensorList())
            {
                sendSettingsToSensor(sensor);
            }
        };
        dbUpdaterHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void dispatchMessage(@NonNull Message msg) {
                super.dispatchMessage(msg);
                if (msg.what==-1) return;
                int index = msg.what;
                LeafSensor sensor = (LeafSensor) msg.obj;
                SensorRepo.getInstane().updateSensor(sensor,index);
                sendSettingsToSensor(sensor);
            }
        };
        delayedHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void dispatchMessage(@NonNull Message msg) {
                super.dispatchMessage(msg);
                if (msg.what==-1) return;
                int index = msg.what;
                LeafSensor sensor = (LeafSensor) msg.obj;
                adapter.updateItem(sensor,index);
                dataBinding.getRoot().postDelayed(updateDB,500);

            }
        };
        SensorAdapter.SensorPressedCallback openFullScreen = (sensor, index,i) -> {
            if (i!=-1)
            {
                if (i==1)
                {
                    sensor.setEditMode(true);
                    adapter.notifyItemChanged(index);
                    return;
                }
                if (i==2)
                {
                    sensor.setEditMode(false);
                    adapter.notifyItemChanged(index);
                    return;
                }
                dbUpdaterHandler.removeMessages(index);
                if (i==3)//timer event buzzer enable
                {
                    sensor.setTime_based_alarm_buzzer_enable(!sensor.getTime_based_alarm_buzzer_enable());
                    adapter.notifyItemChanged(index);
                    Message msg = Message.obtain();
                    msg.what = index;
                    msg.obj = sensor;
                    dbUpdaterHandler.sendMessageDelayed(msg,1500);
                }
                if (i==4)//timer event mobile enable
                {
                    sensor.setTime_based_alarm_mobile_enable(!sensor.getTime_based_alarm_mobile_enable());
                    adapter.notifyItemChanged(index);
                    Message msg = Message.obtain();
                    msg.what = index;
                    msg.obj = sensor;
                    dbUpdaterHandler.sendMessageDelayed(msg,1500);
                }
                if (i==5)//timer event buzzer enable
                {
                    sensor.setHourly_based_alarm_buzzer_enable(!sensor.getHourly_based_alarm_buzzer_enable());
                    adapter.notifyItemChanged(index);
                    Message msg = Message.obtain();
                    msg.what = index;
                    msg.obj = sensor;
                    dbUpdaterHandler.sendMessageDelayed(msg,1500);
                }
                if (i==6)//timer event mobile enable
                {
                    sensor.setHourly_based_alarm_mobile_enable(!sensor.getHourly_based_alarm_mobile_enable());
                    adapter.notifyItemChanged(index);
                    Message msg = Message.obtain();
                    msg.what = index;
                    msg.obj = sensor;
                    dbUpdaterHandler.sendMessageDelayed(msg,1500);
                }
                if (i==7)//timer event mobile enable
                {

                    long currHourMin = sensor.getHourly_based_alarm_hour_min_time();
                    int hour = (int)currHourMin / 100;
                    int min = (int)currHourMin % 100;
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay,
                                                      int minute) {
                                    sensor.setHourly_based_alarm_hour_min_time(hourOfDay*100 + minute);
                                    adapter.updateItem(sensor,index);
                                    Message msg = Message.obtain();
                                    msg.what = index;
                                    msg.obj = sensor;
                                    dbUpdaterHandler.sendMessageDelayed(msg,1500);
                                }
                            }, hour, min, true);
                    timePickerDialog.show();
                }
                if (i==8)//event_group open
                {
                    sensor.setState_event_group(sensor.getState_event_group() ^ 1);
                    adapter.notifyItemChanged(index);
                    Message msg = Message.obtain();
                    msg.what = index;
                    msg.obj = sensor;
                    dbUpdaterHandler.sendMessageDelayed(msg,1500);
                }
                if (i==10)//event_group locked
                {
                    sensor.setState_event_group(sensor.getState_event_group() ^ 4);
                    adapter.notifyItemChanged(index);
                    Message msg = Message.obtain();
                    msg.what = index;
                    msg.obj = sensor;
                    dbUpdaterHandler.sendMessageDelayed(msg,1500);
                }
                if (i==9)//event_group unlocked
                {
                    sensor.setState_event_group(sensor.getState_event_group() ^ 2);
                    adapter.notifyItemChanged(index);
                    Message msg = Message.obtain();
                    msg.what = index;
                    msg.obj = sensor;
                    dbUpdaterHandler.sendMessageDelayed(msg,1500);
                }
                return;
            }
            if (!sensor.hasStatus()) return;
            NavController navController = Navigation.findNavController(getActivity(), R.id.host_fragment);
            Bundle args = new Bundle();
            args.putString("LeafSensor", sensor.getMqttTopic());
            navController.navigate(R.id.action_startFragment_to_sensorScreen, args, null);
        };
        SensorAdapter.SensorEditChangedCallback textChangedListener = new SensorAdapter.SensorEditChangedCallback() {
            @Override
            public void timeChanged(LeafSensor sensor, String text, int index) {
                if (index!=-1)
                {
                    dataBinding.getRoot().removeCallbacks(updateDB);
                    long timeAmount = 0;
                    try {
                        timeAmount = Long.parseLong(text);
                    }catch (NumberFormatException ignore){}//if we failed to get number treat as 0
                    sensor.setTime_based_alarm_time_amount(timeAmount);
                    delayedHandler.removeMessages(index);
                    Message msg = Message.obtain();
                    msg.what = index;
                    msg.obj = sensor;
                    delayedHandler.sendMessageDelayed(msg,1500);
                }
            }
        };
        adapter = new SensorAdapter(requireContext(), SensorRepo.getInstane().getSensors(), openFullScreen,textChangedListener);
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
