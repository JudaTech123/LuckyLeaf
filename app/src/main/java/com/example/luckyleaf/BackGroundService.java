package com.example.luckyleaf;

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
import android.os.IBinder;
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
import com.example.luckyleaf.repo.SensorRepo;

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
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }

    private void notifyAlarm(@NonNull LeafSensor sensor, String Notifymessage)
    {
        if (notificationManager!=null)
        {
            String message = sensor.getSensorName() + " " + Notifymessage;
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
                    .setAutoCancel(true)
                    .setChannelId(channelID)
                    .setContentIntent(PendingIntent.getActivity(BackGroundService.this,0,resultIntent,0))
                    .build();
            notificationManager.notify((int)System.currentTimeMillis() - 1000000,notification);
            mp.start();
        }
    }
    ArrayList<LeafSensor> sensors;
    class TimerTracker extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            synchronized (sensorUpdate) {
                Calendar now = Calendar.getInstance();
                long currTime = now.getTimeInMillis();
                int currHour = now.get(Calendar.HOUR_OF_DAY);
                int currMinute = now.get(Calendar.MINUTE);

                //check
                for (LeafSensor sensor : sensors) {
                    if (!sensor.isActive()) continue;
                    if (sensor.isTimeAllowedUnlockActive() && sensor.getTimeAllowedUnlockInMin() > 0 && sensor.getStatus() == LeafStatus.unlocked) {
                        long TimeInMin = sensor.getTimeAllowedUnlockInMin() * 60_000;//convert to mili
                        if (currTime - TimeInMin >= sensor.getUpdateDate()) {
                            notifyAlarm(sensor, "Unlock too long");
                            continue;
                        }
                    }
                    if (sensor.isTimeInDayToChecActive()) {
                        if (sensor.getTimeInDayToCheckHour() == currHour && sensor.getTimeInDayToCheckMin() == currMinute)
                            notifyAlarm(sensor, "Unlock after set hour");
                    }
                }
            }
        }
    }
    Object sensorUpdate = new Object();
    TimerTracker myTimer = new TimerTracker();
    @Override
    public void onCreate() {
        super.onCreate();
        Consts.getInstance().initColors(this);
        createSerivceAsForGround();
        mp = MediaPlayer.create(this,R.raw.silentping);
        if (PrefsHelper.getInstance().getMqttUrl().length()>0)
            connectToMqtt();
        IntentFilter mTime = new IntentFilter(Intent.ACTION_TIME_TICK);
        registerReceiver(myTimer, mTime);
        SensorRepo.getInstane().askUpdates().observe(this, new Observer<List<LeafSensor>>() {
            @Override
            public void onChanged(List<LeafSensor> sensorList) {
                synchronized (sensorUpdate)
                {
                    sensors = new ArrayList<>(sensorList);
                }
            }
        });
    }

    public static final String RECONNECT_MQTT = "CONNECT";
    public static final String DISONNECT_MQTT = "DISCONNECT";
    public static final String IN_FRONT = "IN_FRONT";
    private boolean inFront = false;

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent!=null && intent.getExtras()!=null)
        {
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
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void createSerivceAsForGround()
    {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager==null) return;
        if (channelID.equals(""))
            channelID = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelID);
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
    public void connectToMqtt()
    {
        LiveData<Boolean> connectStatus = MqqtApi.getInstance().connectInitMqqtApi(getApplication(),"tcp://" + PrefsHelper.getInstance().getMqttUrl() + ":1883",null);
        connectStatus.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                connectStatus.removeObserver(this);
                synchronized (sensorUpdate) {
                    Log.d("juda", "connectToMqtt = " + aBoolean);
                    if (aBoolean) {
                        sensors = SensorRepo.getInstane().getSensors();
                        if (sensors != null) {
                            for (LeafSensor sensor : sensors) {
                                LiveData<MqttMessage> data = MqqtApi.getInstance().subscribe(sensor.getMqttTopic());
                                data.observe(BackGroundService.this, new Observer<MqttMessage>() {
                                    @Override
                                    public void onChanged(MqttMessage s) {
                                        boolean soundAlarm = SensorRepo.getInstane().updateSensor(s);
                                        if (!inFront) {
                                            Log.d("juda", "topic = " + s.getTopic() + " message = " + s.getMessage());
                                            if (soundAlarm) {
                                                LeafSensor leafSensor = SensorRepo.getInstane().getSensor(s.getTopic());
                                                if (leafSensor != null)
                                                    notifyAlarm(leafSensor, leafSensor.getStatusAsString());
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }
        });
    }
}
