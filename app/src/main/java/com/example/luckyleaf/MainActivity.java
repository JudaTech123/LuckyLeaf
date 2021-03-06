package com.example.luckyleaf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.luckyleaf.database.DB;
import com.example.luckyleaf.databinding.ActivityMainBinding;
import com.example.luckyleaf.databinding.PushScreenBinding;
import com.example.luckyleaf.dataholders.LeafSensor;
import com.example.luckyleaf.dataholders.LeafStatus;
import com.example.luckyleaf.dataholders.MqttMessage;
import com.example.luckyleaf.network.MqqtApi;
import com.example.luckyleaf.repo.SensorRepo;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavController.OnDestinationChangedListener {

    private NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getIntent().getExtras();
        if (args!=null)
        {
            if (args.getBoolean("show_login_widnow",false)) {
                int lockStatusAsImage = args.getInt("lockStatusAsImage",0);
                String sensorName = args.getString("sensorName","");
                String sensorStatus = args.getString("sensorStatus","");
                showPushScreen(lockStatusAsImage,sensorName,sensorStatus);
                return;
            }
        }
        DataBindingUtil.setContentView(this, R.layout.activity_main);
        navController = Navigation.findNavController(this, R.id.host_fragment);
        navController.addOnDestinationChangedListener(this);
        DB.getDatabase(getApplicationContext());
        startService();
    }

    private void showPushScreen(int lockStatusAsImage,String sensorName,String sensorStatus)
    {
        PushScreenBinding dataBinding = DataBindingUtil.setContentView(this, R.layout.push_screen);
        dataBinding.sensorStatusImg.setImageResource(lockStatusAsImage);
        dataBinding.txtSensorName.setText(sensorName);
        dataBinding.txtSensorStatus.setText(sensorStatus);
    }
    private void startService()
    {
        Intent mqttService = new Intent(this, BackGroundService.class);
        mqttService.putExtra(BackGroundService.IN_FRONT, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(mqttService);
        }
        else
            startService(mqttService);
    }

    public void connectToMqtt()
    {
        Intent mqttService = new Intent(this, BackGroundService.class);
        mqttService.putExtra(BackGroundService.RECONNECT_MQTT, "");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(mqttService);
        }
        else
            startService(mqttService);
    }

    private void markAppForeGroundStatus(boolean infront)
    {
        Intent mqttService = new Intent(this, BackGroundService.class);
        mqttService.putExtra(BackGroundService.IN_FRONT, infront);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(mqttService);
        }
        else
            startService(mqttService);
    }
    @Override
    protected void onResume() {
        super.onResume();
        markAppForeGroundStatus(true);
    }



    @Override
    protected void onPause() {
        super.onPause();
        markAppForeGroundStatus(false);
    }

    @Override
    protected void onDestroy() {
        LiveData<Boolean> disConnectStatus = MqqtApi.getInstance().disConnect();
        disConnectStatus.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Log.d("juda","disConnectStatus = " + aBoolean);
            }
        });
        super.onDestroy();
    }

    @Override
    public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {

    }
}