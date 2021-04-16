package com.example.luckyleaf.dataholders;

public class MqttMessage {
    String topic;
    String message;
    public MqttMessage(String topic,String message)
    {
        this.topic = topic;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getTopic() {
        return topic;
    }
}
