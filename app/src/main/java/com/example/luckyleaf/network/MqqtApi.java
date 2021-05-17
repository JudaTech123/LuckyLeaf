package com.example.luckyleaf.network;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqqtApi {
    String clientID;
    MqttAndroidClient mqttClient;
    static MqqtApi instance;

    public static MqqtApi getInstance() {
        if (instance==null)
            instance = new MqqtApi();
        return instance;
    }

    public boolean askConnectionStatud()
    {
        return mqttClient!=null && mqttClient.isConnected();
    }
    public LiveData<Boolean> connectInitMqqtApi(Application app, String serverUrl,String _clientID)
    {
        MutableLiveData<Boolean> mqttStatus = new MutableLiveData<>();
        if (_clientID==null)
            clientID = MqttClient.generateClientId();
        else
            clientID = _clientID;
        mqttClient = new MqttAndroidClient(app,serverUrl,clientID);
        if (mqttClient!=null)
        {
            try {
                mqttClient.connect().setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        _Connected = true;
                        connectedStatus.postValue(_Connected);
                        mqttStatus.postValue(true);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        _Connected = false;
                        connectedStatus.postValue(_Connected);
                        mqttStatus.postValue(false);
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
                _Connected = false;
                connectedStatus.postValue(_Connected);
                mqttStatus.postValue(false);
            }
        }
        return mqttStatus;
    }
    MutableLiveData<Boolean> connectedStatus = new MutableLiveData<>();
    public LiveData<Boolean> askConnectedStatus()
    {
        connectedStatus.postValue(_Connected);
        return connectedStatus;
    }
    Boolean _Connected = null;
    public LiveData<Boolean> disConnect()
    {
        MutableLiveData<Boolean> mqttStatus = new MutableLiveData<>();
        try {
            mqttClient.disconnect().setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    _Connected = false;
                    connectedStatus.postValue(_Connected);
                    mqttStatus.postValue(true);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    connectedStatus.postValue(_Connected);
                    mqttStatus.postValue(false);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            connectedStatus.postValue(_Connected);
            mqttStatus.postValue(false);
        }
        return mqttStatus;
    }
    public LiveData<com.example.luckyleaf.dataholders.MqttMessage> subscribe(String topic)
    {
        MutableLiveData<com.example.luckyleaf.dataholders.MqttMessage> mqttStatus = new MutableLiveData<>();
        try {
            mqttClient.subscribe(topic, 0, (topic1, message) -> {
                com.example.luckyleaf.dataholders.MqttMessage msg = new com.example.luckyleaf.dataholders.MqttMessage(topic1,message.toString());
                mqttStatus.postValue(msg);
            });

        } catch (MqttException e) {
            e.printStackTrace();
            mqttStatus.postValue(null);
        }
        return mqttStatus;
    }

    public LiveData<Boolean> publish(String topic,String message,boolean retained)
    {
        MutableLiveData<Boolean> mqttStatus = new MutableLiveData<>();
        MqttMessage msg = new MqttMessage();
        msg.setPayload(message.getBytes());
        msg.setRetained(retained);
        try {
            mqttClient.publish(topic,msg).setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    mqttStatus.postValue(true);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    mqttStatus.postValue(false);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            mqttStatus.postValue(null);
        }
        return mqttStatus;
    }

    public LiveData<Boolean> unsubscribe(String topic)
    {
        MutableLiveData<Boolean> unsubscribeAnswer = new MutableLiveData<>();
        try {
            mqttClient.unsubscribe(topic).setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    unsubscribeAnswer.postValue(true);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    unsubscribeAnswer.postValue(false);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            unsubscribeAnswer.postValue(false);
        }
        return unsubscribeAnswer;
    }


}
