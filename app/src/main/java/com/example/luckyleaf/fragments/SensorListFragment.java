package com.example.luckyleaf.fragments;
import android.Manifest;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
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
import com.example.luckyleaf.myApp;
import com.example.luckyleaf.network.Api;
import com.example.luckyleaf.network.NetworkConnector;
import com.example.luckyleaf.network.responsemodels.SettingsResponsemodel;
import com.example.luckyleaf.network.responsemodels.SettingsWifiResponsemodel;
import com.example.luckyleaf.repo.SensorRepo;
import com.example.luckyleaf.repo.SharedPrefs;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SensorListFragment extends Fragment implements View.OnClickListener {
    private SensorAdapter adapter;
    private Handler delayedHandler;
    private Handler dbUpdaterHandler;
    private void sendSettingsToSensor(LeafSensor sensor)
    {
        if (!sensor.isNotifactionSettingsChanged())return;
        if (NetworkConnector.getInstance().checkIfConnected(requireContext()))
            sendSettingsHTTP(sensor);
        else
        {
            sendSettingsMQTT(sensor);
        }
    }
    private void sendSettingsMQTT(LeafSensor sensor)
    {
        if (sensor==null) return;
        Intent mqttService = new Intent(requireContext(), BackGroundService.class);
        mqttService.putExtra(BackGroundService.UPDATE_SETTINGS, sensor.getSettingsAsJson());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireContext().startForegroundService(mqttService);
        }
        else
            requireContext().startService(mqttService);
    }
    private void sendWifiSettingHTTPLeafSensorsensor(LeafSensor sensor)
    {
        if (sensor.isWifiSettingsChanged())
        {
            Api.instance().sendSensorWifiSettings(sensor.getWifiSettingsAsJson()).observe(getViewLifecycleOwner(), settingsWifiResponsemodel -> {
                if (settingsWifiResponsemodel==null)
                {
                    pd.dismiss();
                    return;
                }
                sendNotifactionSettingHTTP(sensor);
            });
        }
        else
            sendNotifactionSettingHTTP(sensor);

    }
    private void sendNotifactionSettingHTTP(LeafSensor sensor)
    {
        if (sensor.isNotifactionSettingsChanged())
        {
            Api.instance().sendSettingsToSensor(sensor.getSettingsAsJson()).observe(getViewLifecycleOwner(), settingsResponsemodel -> pd.dismiss());
        }
        else
            pd.dismiss();
    }
    private void sendSettingsHTTP(LeafSensor sensor)
    {
        pd = new ProgressDialog(requireActivity());
        pd.setMessage("sending data...");
        pd.setCancelable(false);
        pd.show();
        sendWifiSettingHTTPLeafSensorsensor(sensor);
    }
    private int delayTimeUntilDataSend = 3000;
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
                    dbUpdaterHandler.sendMessageDelayed(msg,delayTimeUntilDataSend);
                }
                if (i==4)//timer event mobile enable
                {
                    sensor.setTime_based_alarm_mobile_enable(!sensor.getTime_based_alarm_mobile_enable());
                    adapter.notifyItemChanged(index);
                    Message msg = Message.obtain();
                    msg.what = index;
                    msg.obj = sensor;
                    dbUpdaterHandler.sendMessageDelayed(msg,delayTimeUntilDataSend);
                }
                if (i==5)//timer event buzzer enable
                {
                    sensor.setHourly_based_alarm_buzzer_enable(!sensor.getHourly_based_alarm_buzzer_enable());
                    adapter.notifyItemChanged(index);
                    Message msg = Message.obtain();
                    msg.what = index;
                    msg.obj = sensor;
                    dbUpdaterHandler.sendMessageDelayed(msg,delayTimeUntilDataSend);
                }
                if (i==6)//timer event mobile enable
                {
                    sensor.setHourly_based_alarm_mobile_enable(!sensor.getHourly_based_alarm_mobile_enable());
                    adapter.notifyItemChanged(index);
                    Message msg = Message.obtain();
                    msg.what = index;
                    msg.obj = sensor;
                    dbUpdaterHandler.sendMessageDelayed(msg,delayTimeUntilDataSend);
                }
                if (i==7)//timer event mobile enable
                {

                    long currHourMin = sensor.getHourly_based_alarm_hour_min_time();
                    int hour = (int)currHourMin / 60;
                    int min = (int)(currHourMin - hour * 60);
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay,
                                                      int minute) {
                                    sensor.setHourly_based_alarm_hour_min_time(hourOfDay*60 + minute);
                                    adapter.updateItem(sensor,index);
                                    Message msg = Message.obtain();
                                    msg.what = index;
                                    msg.obj = sensor;
                                    dbUpdaterHandler.sendMessageDelayed(msg,delayTimeUntilDataSend);
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
                    dbUpdaterHandler.sendMessageDelayed(msg,delayTimeUntilDataSend);
                }
                if (i==10)//event_group locked
                {
                    sensor.setState_event_group(sensor.getState_event_group() ^ 4);
                    adapter.notifyItemChanged(index);
                    Message msg = Message.obtain();
                    msg.what = index;
                    msg.obj = sensor;
                    dbUpdaterHandler.sendMessageDelayed(msg,delayTimeUntilDataSend);
                }
                if (i==9)//event_group unlocked
                {
                    sensor.setState_event_group(sensor.getState_event_group() ^ 2);
                    adapter.notifyItemChanged(index);
                    Message msg = Message.obtain();
                    msg.what = index;
                    msg.obj = sensor;
                    dbUpdaterHandler.sendMessageDelayed(msg,delayTimeUntilDataSend);
                }
                if (i==11)//network active
                {
                    sensor.toggleActive();
                    adapter.notifyItemChanged(index);
                    Message msg = Message.obtain();
                    msg.what = index;
                    msg.obj = sensor;
                    dbUpdaterHandler.sendMessageDelayed(msg,delayTimeUntilDataSend);
                }
                if (i==12)//connect to sensor hot spot
                {
                    if (NetworkConnector.getInstance().checkIfConnected(requireContext()))
                        Api.instance().disconnectFromSensorWifi(requireContext(),sensor);
                    else {
                        SharedPrefs.instance(myApp.getSelf()).putInt(SharedPrefs.sensorIndex,index);
                        if (Api.instance().connectToSensorWifi(requireContext(), sensor))
                        {
                            pd = new ProgressDialog(requireActivity());
                            pd.setMessage("loading");
                            pd.setCancelable(false);
                            pd.show();
                            connectToSensor(sensor,index);
                        }
                    }
                }
                if (i==13)//remove sensor
                {
                    SensorRepo.getInstane().removeSensor(sensor);
                    adapter.removeSensor(index,sensor);
                }
                return;
            }
            if (!sensor.hasStatus()) return;
            NavController navController = Navigation.findNavController(getActivity(), R.id.host_fragment);
            Bundle args = new Bundle();
            args.putString("LeafSensor", sensor.getMqttTopic());
            navController.navigate(R.id.action_startFragment_to_sensorScreen, args, null);
        };
        SensorAdapter.SensorEditChangedCallback textChangedListener = (sensor, text, index) -> {
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
                delayedHandler.sendMessageDelayed(msg,delayTimeUntilDataSend);
            }
        };
        SensorAdapter.SensorEditChangedCallback ssidChangedListener = (sensor, text, index) -> {
            if (index!=-1)
            {
                dataBinding.getRoot().removeCallbacks(updateDB);
                sensor.setWifi_ssid(text);
                delayedHandler.removeMessages(index);
                Message msg = Message.obtain();
                msg.what = index;
                msg.obj = sensor;
                delayedHandler.sendMessageDelayed(msg,delayTimeUntilDataSend);
            }
        };
        SensorAdapter.SensorEditChangedCallback pwdChangedListener = (sensor, text, index) -> {
            if (index!=-1)
            {
                dataBinding.getRoot().removeCallbacks(updateDB);
                sensor.setWifi_pswd(text);
                delayedHandler.removeMessages(index);
                Message msg = Message.obtain();
                msg.what = index;
                msg.obj = sensor;
                delayedHandler.sendMessageDelayed(msg,delayTimeUntilDataSend);
            }
        };
        adapter = new SensorAdapter(requireContext(), SensorRepo.getInstane().getSensors(), openFullScreen,textChangedListener,ssidChangedListener,pwdChangedListener);
        dataBinding.setSensorAdapter(adapter);
        LiveData<List<LeafSensor>> data =  SensorRepo.getInstane().askUpdates();
        data.observe(getViewLifecycleOwner(), leafSensors -> {
            Log.d("juda_123","onChanged");
            adapter.updateSensorList(leafSensors);
            askDataInGet();
        });
        DividerItemDecorator simpleDividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(requireContext(),R.drawable.list_didiver));
        dataBinding.sensorList.addItemDecoration(simpleDividerItemDecoration);
        return dataBinding.getRoot();
    }

    private void askDataInGet()
    {
        Log.d("juda_123","askDataInGet");
        int selectedIndex = SharedPrefs.instance(myApp.getSelf()).getInt(SharedPrefs.sensorIndex,-1);
        SharedPrefs.instance(myApp.getSelf()).putInt(SharedPrefs.sensorIndex,-1);
        LeafSensor selectedSensor = adapter.getSensorByIndex(selectedIndex);
        //if connected to sensor WIFI do GET command to get sensor data
        if (Api.instance().connectToSensorWifi(requireContext(), selectedSensor))
        {
            pd = new ProgressDialog(requireActivity());
            pd.setMessage("loading");
            pd.setCancelable(false);
            pd.show();
            connectToSensor(selectedSensor,selectedIndex);
        }
        else//update MQTT data of sensor settings
        {
            int itemCount = adapter.getItemCount();
            for (int i=0;i<itemCount;i++)
            {
                selectedSensor = adapter.getSensorByIndex(selectedIndex);
                sendSettingsMQTT(selectedSensor);
            }
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();
        Log.d("juda_123","onStart");
        if (adapter.getItemCount()>0)
            askDataInGet();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!checkIfAlreadyhavePermission()) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }
    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (!checkIfAlreadyhavePermission()) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }

    private void checkPermission()
    {
        if (!checkIfAlreadyhavePermission()) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }
    ProgressDialog pd;
    private void connectToSensor(LeafSensor sensor,int index)
    {
        Api.instance().askSensorWifiSettings().observe(getViewLifecycleOwner(), new Observer<SettingsWifiResponsemodel>() {
            @Override
            public void onChanged(SettingsWifiResponsemodel settingsWifiResponsemodel) {
                if (settingsWifiResponsemodel==null)
                {
                    pd.dismiss();
                    return;
                }
                sensor.setWifi_pswd(settingsWifiResponsemodel.getPswd());
                sensor.setWifi_ssid(settingsWifiResponsemodel.getSsid());
                sensor.setActive(settingsWifiResponsemodel.getEnable());
                Api.instance().askSettingsToSensor().observe(getViewLifecycleOwner(), new Observer<SettingsResponsemodel>() {
                    @Override
                    public void onChanged(SettingsResponsemodel settingsResponsemodel) {
                        if (settingsResponsemodel==null)
                        {
                            pd.dismiss();
                            return;
                        }
                        sensor.setState_event_group(settingsResponsemodel.getState_event_group());

                        sensor.setTime_based_alarm_buzzer_enable(settingsResponsemodel.getTimer_based().getSound_enable() == 1);
                        sensor.setTime_based_alarm_mobile_enable(settingsResponsemodel.getTimer_based().getMobap_enable() == 1);
                        sensor.setTime_based_alarm_time_amount(settingsResponsemodel.getTimer_based().getValue_second());

                        sensor.setHourly_based_alarm_hour_min_time(settingsResponsemodel.getHour_based().getValue_second());
                        sensor.setHourly_based_alarm_mobile_enable(settingsResponsemodel.getHour_based().getMobap_enable() == 1);
                        sensor.setHourly_based_alarm_buzzer_enable(settingsResponsemodel.getHour_based().getSound_enable()==1);
                        SensorRepo.getInstane().updateSensor(sensor);
                        adapter.notifyItemChanged(index);
                        pd.dismiss();
                    }
                });
            }
        });
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imgMenu)
        {
            NavController navController = Navigation.findNavController(getActivity(), R.id.host_fragment);
            Bundle args = new Bundle();
            navController.navigate(R.id.action_startFragment_to_settingsScreen, args, null);
        }
        if (view.getId() == R.id.btnAddSensor)
        {
            final View input = getLayoutInflater().inflate(R.layout.dialog_add_sensor, null);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireActivity());
            alertDialog.setTitle("Add sensor");
            alertDialog.setMessage("Please enter sensor name and S.N");
            alertDialog.setView(input);
            final EditText edtSensorSN = ((EditText)input.findViewById(R.id.edtSensorSN));
            final EditText edtSensorName = ((EditText)input.findViewById(R.id.edtSensorName));
            alertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    LeafSensor sensor = new LeafSensor(edtSensorSN.getText().toString(),edtSensorName.getText().toString());
                    SensorRepo.getInstane().addSensor(sensor);
                    adapter.updateSensorList(SensorRepo.getInstane().getSensors());
                    adapter.notifyItemInserted(0);
                }
            });
            alertDialog.show();
        }
    }
}
