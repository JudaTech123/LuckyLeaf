package com.example.luckyleaf.network;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.luckyleaf.dataholders.LeafSensor;
import com.example.luckyleaf.fragments.SettingsFragment;
import com.example.luckyleaf.network.responsemodels.SettingsResponsemodel;
import com.example.luckyleaf.network.responsemodels.SettingsWifiResponsemodel;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Api {
    private String baseURL = "http://192.168.4.1/";
    private final OkHttpClient okHttpClient;
    private static Api ourInstance = null;
    private NetworkConnector networkConnector;
    private boolean connectedToSensor = false;
    public static Api instance() {
        if (ourInstance==null)
            ourInstance = new Api();
        return ourInstance;
    }
    private Api() {
        final int TIME_OUT = 60;//Timeout in seconds
        okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS).build();
    }

    public void disconnectFromSensorWifi(Context context, LeafSensor sensor)
    {
        if (networkConnector==null)
            networkConnector = new NetworkConnector();
        connectedToSensor = false;
        networkConnector.disconnectFromSensorWifi(context,sensor);
    }

    public boolean connectToSensorWifi(Context context, LeafSensor sensor)
    {
        if (networkConnector==null)
            networkConnector = new NetworkConnector();

        if (networkConnector.connectToSensorWifi(context,sensor))
        {
            connectedToSensor = true;
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean isConnectedToSensor() {
        return connectedToSensor;
    }

    public LiveData<SettingsResponsemodel> askSettingsToSensor()
    {
        MutableLiveData <SettingsResponsemodel> settingData = new MutableLiveData<>();
        MediaType mediaType = MediaType.parse("application/json");
        Request.Builder reqBodyBuilder = new Request.Builder();
        reqBodyBuilder.url(baseURL + "notification_configuration");
        reqBodyBuilder.addHeader("content-type", "application/json");
        okHttpClient.newCall(reqBodyBuilder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                settingData.postValue(null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code()==200)
                {
                    String body = response.body().string();
                    try {
                        SettingsResponsemodel data = new Gson().fromJson(body, SettingsResponsemodel.class);
                        settingData.postValue(data);
                    }catch (Exception e)
                    {
                        settingData.postValue(null);
                    }
                }
            }
        });
        return settingData;
    }

    public LiveData<SettingsWifiResponsemodel> sendSensorWifiSettings(String sensorDataJson)
    {
        MutableLiveData <SettingsWifiResponsemodel> settingData = new MutableLiveData<>();
        MediaType mediaType = MediaType.parse("application/json");
        Request.Builder reqBodyBuilder = new Request.Builder();
        RequestBody reqBody = RequestBody.create(mediaType, sensorDataJson);
        reqBodyBuilder.url(baseURL + "online_mode_configuration");
        reqBodyBuilder.post(reqBody);
        reqBodyBuilder.addHeader("content-type", "application/json");
        okHttpClient.newCall(reqBodyBuilder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                settingData.postValue(null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code()==200)
                {
                    String body = response.body().string();
                    try {
                        SettingsWifiResponsemodel data = new Gson().fromJson(body, SettingsWifiResponsemodel.class);
                        settingData.postValue(data);
                    }catch (Exception e)
                    {
                        settingData.postValue(null);
                    }
                }
            }
        });
        return settingData;
    }

    public LiveData<SettingsWifiResponsemodel> askSensorWifiSettings()
    {
        MutableLiveData <SettingsWifiResponsemodel> settingData = new MutableLiveData<>();
        MediaType mediaType = MediaType.parse("application/json");
        Request.Builder reqBodyBuilder = new Request.Builder();
        reqBodyBuilder.url(baseURL + "online_mode_configuration");
        reqBodyBuilder.addHeader("content-type", "application/json");
        okHttpClient.newCall(reqBodyBuilder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                settingData.postValue(null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code()==200)
                {
                    String body = response.body().string();
                    try {
                        SettingsWifiResponsemodel data = new Gson().fromJson(body, SettingsWifiResponsemodel.class);
                        settingData.postValue(data);
                    }catch (Exception e)
                    {
                        settingData.postValue(null);
                    }
                }
            }
        });
        return settingData;
    }

    public LiveData<SettingsResponsemodel> sendSettingsToSensor(String sensorDataJson)
    {
        MutableLiveData <SettingsResponsemodel> settingData = new MutableLiveData<>();
        MediaType mediaType = MediaType.parse("application/json");
        Request.Builder reqBodyBuilder = new Request.Builder();
        RequestBody reqBody = RequestBody.create(mediaType, sensorDataJson);
        reqBodyBuilder.url(baseURL + "notification_configuration");
        reqBodyBuilder.post(reqBody);
        reqBodyBuilder.addHeader("content-type", "application/json");
        okHttpClient.newCall(reqBodyBuilder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                settingData.postValue(null);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code()==200)
                {
                    String body = response.body().string();
                    try {
                        SettingsResponsemodel data = new Gson().fromJson(body, SettingsResponsemodel.class);
                        settingData.postValue(data);
                    }catch (Exception e)
                    {
                        settingData.postValue(null);
                    }
                }
            }
        });
        return settingData;
    }
}
