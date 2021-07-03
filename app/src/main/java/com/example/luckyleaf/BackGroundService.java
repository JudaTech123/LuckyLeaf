package com.example.luckyleaf;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.luckyleaf.dataholders.LeafSensor;
import com.example.luckyleaf.dataholders.LeafStatus;
import com.example.luckyleaf.dataholders.MqttMessage;
import com.example.luckyleaf.network.MqqtApi;
import com.example.luckyleaf.network.responsemodels.SettingsResponsemodel;
import com.example.luckyleaf.repo.SensorRepo;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static androidx.core.app.NotificationCompat.PRIORITY_MAX;

public class BackGroundService extends LifecycleService {
    MediaPlayer mp;
    NotificationManager notificationManager;
    String channelID = "";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return null;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager notificationManager){
        String channelId = "Luckyleaf";
        String channelName = "Sensor monitor app";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        // omitted the LED color
        channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannelNoLockScreen(NotificationManager notificationManager){
        String channelId = "LuckyleafNoLock";
        String channelName = "Sensor monitor app no lock";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        // omitted the LED color
        channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }

    private void notifyAlarm(String message)
    {
        if (notificationManager!=null)
        {
            if (channelID.equals(""))
                channelID = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
            // Create an Intent for the activity you want to start
            Intent resultIntent = new Intent(BackGroundService.this, MainActivity.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(BackGroundService.this, channelID);
            Notification notification = notificationBuilder.setOngoing(false)
                    .setSmallIcon(R.drawable.logo_img)
                    .setContentTitle(message)
                    .setWhen(System.currentTimeMillis())
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .setAutoCancel(false)
                    .setChannelId(channelID)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentIntent(PendingIntent.getActivity(BackGroundService.this,0,resultIntent,0))
                    .build();
            notificationManager.notify((int)System.currentTimeMillis() - 1000000,notification);
            mp.start();
        }
    }
    private void notifyAlarm(@NonNull LeafSensor sensor, String Notifymessage)
    {
        String message = sensor.getSensorName() + " " + Notifymessage;
        notifyAlarm(message);
    }
    final String unlockAlarmKey = "unlockKey";
    ArrayList<LeafSensor> sensors;
    Object sensorUpdate = new Object();
    @Override
    public void onCreate() {
        super.onCreate();
        Consts.getInstance().initColors(this);
        createSerivceAsForGround();
        mp = MediaPlayer.create(this,R.raw.silentping);
        if (PrefsHelper.getInstance().getMqttUrl().length()>0)
            connectToMqtt();
        //IntentFilter mTime = new IntentFilter(Intent.ACTION_TIME_TICK);
        //registerReceiver(myTimer, mTime);
        SensorRepo.getInstane().askUpdates().observe(this, new Observer<List<LeafSensor>>() {
            @Override
            public void onChanged(List<LeafSensor> sensorList) {
                synchronized (sensorUpdate)
                {
                    sensors = new ArrayList<>(sensorList);
                    SensorRepo.getInstane().updateSensorList(sensors);
                }
            }
        });
    }

    public static final String RECONNECT_MQTT = "CONNECT";
    public static final String DISONNECT_MQTT = "DISCONNECT";
    public static final String IN_FRONT = "IN_FRONT";
    public static final String UPDATE_SETTINGS = "UPDATE_SETTINGS";
    public static final String SHOW_NOTIFACTION = "SHOW_NOTIFACTION";

    private final String SETTING_SUFFIX = "_settings";
    private boolean inFront = false;

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent!=null && intent.getExtras()!=null)
        {
            if (intent.getExtras().containsKey(SHOW_NOTIFACTION))
            {
                if (intent!=null && intent.getExtras()!=null && intent.getExtras().containsKey(unlockAlarmKey))
                {
                    String title = intent.getExtras().getString(unlockAlarmKey);
                    notifyAlarm(title);
                }
            }
            if (intent.getExtras().containsKey(RECONNECT_MQTT))
            {
                connectToMqtt();
            }
            if (intent.getExtras().containsKey(DISONNECT_MQTT))
            {
                disConnectToMqtt();
            }
            if (intent.getExtras().containsKey(IN_FRONT))
            {
                inFront = intent.getExtras().getBoolean(IN_FRONT);
                if (!MqqtApi.getInstance().askConnectionStatud())
                    connectToMqtt();
            }
            if (intent.getExtras().containsKey(UPDATE_SETTINGS))
            {
                String settings = intent.getExtras().getString(UPDATE_SETTINGS);
                sendSettingsToSensor(settings);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void createSerivceAsForGround()
    {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager==null) return;
        String channelIDNoLock = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannelNoLockScreen(notificationManager) : "";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelIDNoLock);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.logo_img)
                .setPriority(PRIORITY_MAX)
                .setContentTitle("Luckyleaf")
                .setContentText("Sensor monitor app")
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build();

        startForeground(1, notification);
    }
    public void disConnectToMqtt()
    {
        LiveData<Boolean> connectStatus = MqqtApi.getInstance().disConnect();
        connectStatus.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                connectStatus.removeObserver(this);
                Log.d("juda","disConnectToMqtt = " + aBoolean);
            }
        });
    }
    private void sendSettingsToSensor(String settingsJson)
    {
        Log.d("juda","sendSettingsToSensor : " + settingsJson);
        MqqtApi.getInstance().publish("response",settingsJson,true).observe(BackGroundService.this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean!=null && aBoolean)
                {
                    Log.d("juda", "send settings to sensor");
                }
            }
        });
    }

    private void processMqttStatusMessage(MqttMessage mqttMessage)
    {
        LeafSensor leafSensor = null;
        Log.d("juda", "topic = " + mqttMessage.getTopic() + " message = " + mqttMessage.getMessage());
        synchronized (sensorUpdate)
        {
            for (LeafSensor sensorItem : sensors)
            {
                if (mqttMessage.getTopic().equals(sensorItem.getMqttTopic())) {
                    leafSensor = sensorItem;
                    break;
                }
            }
            if (leafSensor==null) return;
        }
        boolean isStatusChanged = SensorRepo.getInstane().updateSensor(mqttMessage,leafSensor);
        if (leafSensor.isSingleStateConfigured(mqttMessage))//if only one status than make sound for each status
        {
            isStatusChanged = true;
        }
        if (!isStatusChanged) return;
        notifyAlarm(leafSensor, leafSensor.getStatusAsString());
//        switch (leafSensor.getStatus())
//        {
//            case alarm:
//            case unlocked:
//            case open:
//                notifyAlarm(leafSensor, leafSensor.getStatusAsString());
//                triggerSensorAlramBasedOnTime(leafSensor);
//                break;
//            case locked:
//                removeTimers(leafSensor);
//                break;
//        }
    }

    /**
     * This function will go over the sensor settings and set a timer based on them
     * @param leafSensor
     */
    private void triggerSensorAlramBasedOnTime(LeafSensor leafSensor)
    {
        AlarmManager alarmMgr;
//        if (leafSensor.getTime_based_alarm_mobile_enable() && leafSensor.getTime_based_alarm_time_amount()>0)
//        {
//            PendingIntent alarmIntent;
//
//            alarmMgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//            Intent intent = new Intent(getApplicationContext(), TimedBroadcast.class);
//            Bundle args = new Bundle();
//            intent.putExtra(unlockAlarmKey,leafSensor.getSensorName() +  " Open too long");
//            alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), (int)leafSensor.getDbID(), intent, 0);
//
//            alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                    SystemClock.elapsedRealtime() +
//                            leafSensor.getTime_based_alarm_time_amount()*1000, alarmIntent);
//        }
        if (leafSensor.getHourly_based_alarm_mobile_enable())
        {
            long rawTime = leafSensor.getHourly_based_alarm_hour_min_time();
            if (rawTime==0) return;

            long alarmHour = rawTime/100;
            long alarmMinute = rawTime % 100;
            Calendar now = Calendar.getInstance();
            Calendar alarmTimer = Calendar.getInstance();
            alarmTimer.set(Calendar.HOUR_OF_DAY, (int)alarmHour);
            alarmTimer.set(Calendar.MINUTE, (int)alarmMinute);
            if (now.after(alarmTimer))
                alarmTimer.add(Calendar.DAY_OF_MONTH,1);

            PendingIntent alarmIntent;
            alarmMgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getApplicationContext(), TimedBroadcast.class);
            alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 100 + (int)leafSensor.getDbID(), intent, 0);

            alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    alarmTimer.getTimeInMillis(), alarmIntent);
        }
    }

    /**
     * This function will clear any timers associted withs this sensor
     * @param leafSensor
     */
    private void removeTimers(LeafSensor leafSensor)
    {
        AlarmManager alarmMgr;
        PendingIntent alarmIntent;

        alarmMgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        //cancel unlock delay timer
        Intent intent = new Intent(getApplicationContext(), TimedBroadcast.class);
        alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), (int)leafSensor.getDbID(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmIntent.cancel();
        alarmMgr.cancel(alarmIntent);
        //cancel hour : min timer
        intent = new Intent(getApplicationContext(), TimedBroadcast.class);
        alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 100 + (int)leafSensor.getDbID(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmIntent.cancel();
        alarmMgr.cancel(alarmIntent);
    }
    private void processMqttSettingsMessage(MqttMessage mqttMessage)
    {
        LeafSensor leafSensor = null;
        synchronized (sensorUpdate)
        {
            for (LeafSensor sensorItem : sensors)
            {
                if (mqttMessage.getTopic().equals("response")) {
                    Log.d("juda", "settings Json = " + mqttMessage.getMessage());
                    SettingsResponsemodel.notification_configuration_holder data = new Gson().fromJson(mqttMessage.getMessage(), SettingsResponsemodel.notification_configuration_holder.class);
                    if (data.getTimer_based()==null)
                        return;
                    sensorItem.setState_event_group(data.getState_event_group());

                    sensorItem.setTime_based_alarm_buzzer_enable(data.getTimer_based().getSound_enable() == 1);
                    sensorItem.setTime_based_alarm_mobile_enable(data.getTimer_based().getMobap_enable() == 1);
                    sensorItem.setTime_based_alarm_time_amount(data.getTimer_based().getValue_second());

                    sensorItem.setHourly_based_alarm_hour_min_time(data.getHour_based().getValue_second());
                    sensorItem.setHourly_based_alarm_mobile_enable(data.getHour_based().getMobap_enable() == 1);
                    sensorItem.setHourly_based_alarm_buzzer_enable(data.getHour_based().getSound_enable()==1);
                    SensorRepo.getInstane().updateSensor(sensorItem);
                    break;
                }
            }
            if (leafSensor==null) return;
        }
    }
    public void connectToMqtt()
    {
        LiveData<Boolean> connectStatus = MqqtApi.getInstance().connectInitMqqtApi(getApplication(),"tcp://" + PrefsHelper.getInstance().getMqttUrl() + ":1883",null);
        connectStatus.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                connectStatus.removeObserver(this);
                synchronized (sensorUpdate) {
                    Log.d("juda", "connectToMqtt = " + aBoolean);
                    LiveData<MqttMessage> settingsData = MqqtApi.getInstance().subscribe("response");
                    settingsData.observe(BackGroundService.this, mqttMessage -> processMqttSettingsMessage(mqttMessage));
                    if (aBoolean) {
                        sensors = SensorRepo.getInstane().getSensors();
                        if (sensors != null) {
                            for (LeafSensor sensor : sensors) {
                                LiveData<MqttMessage> data = MqqtApi.getInstance().subscribe(sensor.getMqttTopic());
                                data.observe(BackGroundService.this, mqttMessage -> processMqttStatusMessage(mqttMessage));
                            }
                        }
                    }
                }
            }
        });
    }
}
