package com.example.luckyleaf.network.responsemodels;

import com.google.gson.annotations.SerializedName;

public class SettingsResponsemodel {
    @SerializedName("notification_configuration")
    notification_configuration_holder notification_configuration;

    public int getState_event_group() {
        return notification_configuration!=null ? notification_configuration.state_event_group : 0;
    }

    public value_holder getHour_based() {
        return notification_configuration!=null ? notification_configuration.getHour_based() : new value_holder();
    }

    public value_holder getTimer_based() {
        return notification_configuration!=null ? notification_configuration.getTimer_based() : new value_holder();
    }

    class notification_configuration_holder
    {
        @SerializedName("state_event_group")
        int state_event_group;
        @SerializedName("timer_based")
        value_holder timer_based;
        @SerializedName("hour_based")
        value_holder hour_based;

        public int getState_event_group() {
            return state_event_group;
        }

        public value_holder getHour_based() {
            return hour_based;
        }

        public value_holder getTimer_based() {
            return timer_based;
        }
    }
    public class  value_holder
    {
        @SerializedName("value_second")
        int value_second;
        @SerializedName("sound_enable")
        int sound_enable;
        @SerializedName("mobap_enable")
        int mobap_enable;

        public int getMobap_enable() {
            return mobap_enable;
        }

        public int getSound_enable() {
            return sound_enable;
        }

        public int getValue_second() {
            return value_second;
        }
    }
}
