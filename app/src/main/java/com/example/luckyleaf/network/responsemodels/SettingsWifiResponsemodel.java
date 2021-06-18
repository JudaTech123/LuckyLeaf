package com.example.luckyleaf.network.responsemodels;

import com.google.gson.annotations.SerializedName;

public class SettingsWifiResponsemodel {
    @SerializedName("online_mode_configuration")
    Online_mode_configuration_holder online_mode_configuration;

    public boolean getEnable() {
        return online_mode_configuration!=null ? online_mode_configuration.getEnable() : false;
    }

    public String getPswd() {
        return online_mode_configuration!=null ? online_mode_configuration.getPswd() : "";
    }

    public String getSsid() {
        return online_mode_configuration!=null ? online_mode_configuration.getSsid() : "";
    }

    class Online_mode_configuration_holder
    {
        @SerializedName("ssid")
        String ssid;
        @SerializedName("pswd")
        String pswd;
        @SerializedName("enable")
        int enable;

        public boolean getEnable() {
            return enable==1;
        }

        public String getPswd() {
            return pswd;
        }

        public String getSsid() {
            return ssid;
        }
    }
}
