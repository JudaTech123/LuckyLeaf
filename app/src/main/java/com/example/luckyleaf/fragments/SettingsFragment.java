package com.example.luckyleaf.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.example.luckyleaf.network.Api;
import com.example.luckyleaf.network.MqqtApi;
import com.example.luckyleaf.network.responsemodels.SettingsWifiResponsemodel;
import com.example.luckyleaf.repo.SensorRepo;

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

    //this function will take the settings from the local sensor and send it to the connected sensor
    private void sendWifiSettings(LeafSensor sensor)
    {
        final ProgressDialog pd = new ProgressDialog(requireActivity());
        pd.setMessage("loading");
        pd.setCancelable(false);
        pd.show();

        Api.instance().sendSensorWifiSettings(sensor.getWifiSettingsAsJson()).observe(getViewLifecycleOwner(), new Observer<SettingsWifiResponsemodel>() {
            @Override
            public void onChanged(SettingsWifiResponsemodel settingsWifiResponsemodel) {
                pd.dismiss();
            }
        });
    }
    private void showWifiSettingsDialog(LeafSensor markedItem)
    {
        final View input = getLayoutInflater().inflate(R.layout.dialog_router_settings, null);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireActivity());
        alertDialog.setTitle("Router settings");
        alertDialog.setMessage("Senseor name is : " + markedItem.getSensorName());
        alertDialog.setView(input);
        final EditText edtWifiName = ((EditText)input.findViewById(R.id.edtWifiName));
        final EditText edtWifiPassword = ((EditText)input.findViewById(R.id.edtWifiPassword));
        alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                markedItem.setWifi_ssid(edtWifiName.getText().toString());
                markedItem.setWifi_pswd(edtWifiPassword.getText().toString());
                SensorRepo.getInstane().updateSensorWifi(markedItem);
                sendWifiSettings(markedItem);
            }
        });

        alertDialog.setNegativeButton("Refresh", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
                refreshSensorWifiSettings(markedItem);
            }
        });
        edtWifiName.setText(markedItem.getWifi_ssid());
        edtWifiPassword.setText(markedItem.getWifi_pswd());
        SensorRepo.getInstane().updateSensorWifi(markedItem);
        alertDialog.show();
    }
    private void refreshSensorWifiSettings(LeafSensor markedItem)
    {

        final ProgressDialog pd = new ProgressDialog(requireActivity());
        pd.setMessage("loading");
        pd.setCancelable(false);
        pd.show();
        Api.instance().askSensorWifiSettings().observe(getViewLifecycleOwner(), new Observer<SettingsWifiResponsemodel>() {
            @Override
            public void onChanged(SettingsWifiResponsemodel settingsWifiResponsemodel) {
                pd.dismiss();
                if (settingsWifiResponsemodel==null) return;
                markedItem.setWifi_pswd(settingsWifiResponsemodel.getPswd());
                markedItem.setWifi_ssid(settingsWifiResponsemodel.getSsid());
                SensorRepo.getInstane().updateSensorWifi(markedItem);
                showWifiSettingsDialog(markedItem);
            }
        });
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnGetSettingsFromSensor)
        {
            LeafSensor markedItem = dataBinding.getAdapter().getMarkedItem();
            if (markedItem!=null)
            {
                Api.instance().askSettingsToSensor().observe(getViewLifecycleOwner(), settingsResponsemodel -> {
                    if (settingsResponsemodel==null) return;
                    markedItem.setState_event_group(settingsResponsemodel.getState_event_group());

                    markedItem.setTime_based_alarm_buzzer_enable(settingsResponsemodel.getTimer_based().getSound_enable() == 1);
                    markedItem.setTime_based_alarm_mobile_enable(settingsResponsemodel.getTimer_based().getMobap_enable() == 1);
                    markedItem.setTime_based_alarm_time_amount(settingsResponsemodel.getTimer_based().getValue_second());

                    markedItem.setHourly_based_alarm_hour_min_time(settingsResponsemodel.getHour_based().getValue_second());
                    markedItem.setHourly_based_alarm_mobile_enable(settingsResponsemodel.getHour_based().getMobap_enable() == 1);
                    markedItem.setHourly_based_alarm_buzzer_enable(settingsResponsemodel.getHour_based().getSound_enable()==1);
                    SensorRepo.getInstane().updateSensor(markedItem);
                });
            }
        }
        if (view.getId() == R.id.btnViewRouterSettings)
        {
            LeafSensor markedItem = dataBinding.getAdapter().getMarkedItem();
            if (markedItem!=null)
            {
                if (markedItem.getWifi_ssid()==null || markedItem.getWifi_ssid().isEmpty() || markedItem.getWifi_pswd()==null || markedItem.getWifi_pswd().isEmpty())
                {
                    refreshSensorWifiSettings(markedItem);
                }
                else
                {
                    showWifiSettingsDialog(markedItem);
                }
            }
        }
        if (view.getId() == R.id.btnSendSettingsFromSensor)
        {
            LeafSensor markedItem = dataBinding.getAdapter().getMarkedItem();
            if (markedItem!=null)
            {
                Api.instance().sendSettingsToSensor(markedItem.getSettingsAsJson()).observe(getViewLifecycleOwner(), settingsResponsemodel -> {
                    if (settingsResponsemodel==null) return;
                    markedItem.setState_event_group(settingsResponsemodel.getState_event_group());

                    markedItem.setTime_based_alarm_buzzer_enable(settingsResponsemodel.getTimer_based().getSound_enable() == 1);
                    markedItem.setTime_based_alarm_mobile_enable(settingsResponsemodel.getTimer_based().getMobap_enable() == 1);
                    markedItem.setTime_based_alarm_time_amount(settingsResponsemodel.getTimer_based().getValue_second());

                    markedItem.setHourly_based_alarm_hour_min_time(settingsResponsemodel.getHour_based().getValue_second());
                    markedItem.setHourly_based_alarm_mobile_enable(settingsResponsemodel.getHour_based().getMobap_enable() == 1);
                    markedItem.setHourly_based_alarm_buzzer_enable(settingsResponsemodel.getHour_based().getSound_enable()==1);
                    SensorRepo.getInstane().updateSensor(markedItem);
                });
            }
        }
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
