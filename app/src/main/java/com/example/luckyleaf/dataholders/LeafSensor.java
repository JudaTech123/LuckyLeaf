package com.example.luckyleaf.dataholders;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.luckyleaf.Consts;
import com.example.luckyleaf.R;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
//data saved on sensor in mobile for local usage
//status : open / locked / unlocked / alarm
//mqttTopic : to what we do subscribe / publish
//sensorName : the sensor name we show in the list
//active : if mobile should respond to mqtt messages now
//updateDate : last time we got status change
//**settings that tell mobile how to handle status changes
//timeAllowedUnlockInMin : how much time sensor stays in unlocked before starting sound
//timeInDayToCheckHour : what hour to check is sensor is unlocked
//timeInDayToCheckMin : what min to check is sensor is unlocked
//timeAllowedUnlockActive : should check "timeAllowedUnlockInMin"
//timeInDayToChecActive : should check "timeAllowedUnlockInMin"
//**settings that tell sensor how to handle status changes. if 0x02 || 0x08 || 0x020 are not raised sensor should not send data to server
//timeUntilUnlockBecomesAlarm : time until sensor changes status from unlock to alarm in sec
//sensorSettings :
//                  1  0x01 - sensor should
//                  2  0x02 - sensor should send unlock to mobile
//                  4  0x04 - sensor should
//                  8  0x08 - sensor should send to mobile...
//                  16 0x10 - sensor should
//                  32 0x20 - sensor should send to mobile...
//                  64 0x40 - monitor
//sensorSettingsJson : {"timeAllowedUnlockInMin":5,"timeInDayToCheckHour":10,"timeInDayToCheckMin":10,"timeAllowedUnlockActive":0,"timeInDayToChecActive":0,"timeUntilUnlockBecomesAlarm":30,"sensorSettings":17}
@Entity
public class LeafSensor {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    long dbID;
    @ColumnInfo(name = "status")
    LeafStatus status = LeafStatus.unknown;
    @ColumnInfo(name = "mqttTopic")
    String     mqttTopic;
    @ColumnInfo(name = "sensorName")
    String     sensorName;
    @ColumnInfo(name = "sensorSN")
    String     sensorSN;
    @ColumnInfo(name = "active")
    boolean     active;
    @ColumnInfo(name = "updateDate")
    long     updateDate;
    @ColumnInfo(name = "state_event_group")
    long      state_event_group;
    @ColumnInfo(name = "time_based_alarm_time_amount")
    long      time_based_alarm_time_amount;
    @ColumnInfo(name = "time_based_alarm_mobile_enable")
    boolean      time_based_alarm_mobile_enable;
    @ColumnInfo(name = "time_based_alarm_buzzer_enable")
    boolean      time_based_alarm_buzzer_enable;

    @ColumnInfo(name = "hourly_based_alarm_hour_min_time")
    long      hourly_based_alarm_hour_min_time;
    @ColumnInfo(name = "hourly_based_alarm_mobile_enable")
    boolean      hourly_based_alarm_mobile_enable;
    @ColumnInfo(name = "hourly_based_alarm_buzzer_enable")
    boolean      hourly_based_alarm_buzzer_enable;
    @ColumnInfo(name = "wifi_ssid")
    String      wifi_ssid;
    @ColumnInfo(name = "wifi_pswd")
    String      wifi_pswd;
    @Ignore
    boolean     editMode;
    @Ignore
    boolean     notifyMobile;
    @Ignore
    boolean     notifySensor;

    public void setSensorSN(String sensorSN) {
        this.sensorSN = sensorSN;
    }

    public String getSensorSN() {
        return sensorSN;
    }

    public void setWifi_pswd(String wifi_pswd) {
        this.wifi_pswd = wifi_pswd;
    }

    public String getWifi_pswd() {
        return wifi_pswd;
    }

    public void setWifi_ssid(String wifi_ssid) {
        this.wifi_ssid = wifi_ssid;
    }

    public String getWifi_ssid() {
        return wifi_ssid;
    }

    public void setState_event_group(long event_group) {
        this.state_event_group = event_group;
    }

    public long getState_event_group() {
        return state_event_group;
    }

    public boolean isNotifyMobile() {
        return notifyMobile;
    }

    public boolean isNotifySensor() {
        return notifySensor;
    }

    public void setNotifyMobile(boolean notifyMobile) {
        this.notifyMobile = notifyMobile;
    }

    public void setNotifySensor(boolean notifySensor) {
        this.notifySensor = notifySensor;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public void setTime_based_alarm_time_amount(long time_based_alarm_time_amount) {
        this.time_based_alarm_time_amount = time_based_alarm_time_amount;
    }

    public void setTime_based_alarm_buzzer_enable(boolean time_based_alarm_buzzer_enable) {
        this.time_based_alarm_buzzer_enable = time_based_alarm_buzzer_enable;
    }
    public boolean getTime_based_alarm_buzzer_enable() {
        return time_based_alarm_buzzer_enable;
    }

    public void setTime_based_alarm_mobile_enable(boolean time_based_alarm_mobile_enable) {
        this.time_based_alarm_mobile_enable = time_based_alarm_mobile_enable;
    }
    public boolean getTime_based_alarm_mobile_enable() {
        return time_based_alarm_mobile_enable;
    }

    public long getTime_based_alarm_time_amount() {
        return time_based_alarm_time_amount;
    }

    public void setHourly_based_alarm_hour_min_time(long hourly_based_alarm_hour_min_time) {
        this.hourly_based_alarm_hour_min_time = hourly_based_alarm_hour_min_time;
    }
    //hour*60+min. this will tell us what min and hour to check if sensor is not locked
    public long getHourly_based_alarm_hour_min_time() {
        return hourly_based_alarm_hour_min_time;
    }

    public void setHourly_based_alarm_buzzer_enable(boolean hourly_based_alarm_buzzer_enable) {
        this.hourly_based_alarm_buzzer_enable = hourly_based_alarm_buzzer_enable;
    }
    public boolean getHourly_based_alarm_buzzer_enable()
    {
        return hourly_based_alarm_buzzer_enable;
    }

    public void setHourly_based_alarm_mobile_enable(boolean hourly_based_alarm_mobile_enable) {
        this.hourly_based_alarm_mobile_enable = hourly_based_alarm_mobile_enable;
    }
    public boolean getHourly_based_alarm_mobile_enable()
    {
        return hourly_based_alarm_mobile_enable;
    }

    @Ignore
    String     strStatus = "";

    public long getDbID() {
        return dbID;
    }

    public void setDbID(long dbID) {
        this.dbID = dbID;
    }
    public static final String FULL_DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm";
    public static final String SHORT_DATE_TIME_FORMAT = "HH:mm:ss";
    private boolean isCurrentDay(Calendar statusDate)
    {
        Calendar now = Calendar.getInstance();
        if (now.get(Calendar.YEAR) != statusDate.get(Calendar.YEAR)) return false;
        if (now.get(Calendar.MONTH) != statusDate.get(Calendar.MONTH)) return false;
        if (now.get(Calendar.DAY_OF_MONTH) != statusDate.get(Calendar.DAY_OF_MONTH)) return false;
        return true;
    }

    public String getTimeStampAsString()
    {
        Calendar now = Calendar.getInstance();
        long timePassed = (now.getTimeInMillis() - updateDate)/1000;
        if (timePassed<60)
        {
            return timePassed + " sec";
        }
        timePassed/=60;//mintues
        if (timePassed<60)
        {
            return timePassed + " min";
        }
        timePassed/=60;//hours
        if (timePassed<24)
        {
            return timePassed + " hours";
        }
        timePassed/=24;//hours
        return timePassed + " days";
    }
    public long getUpdateDate() {
        return updateDate;
    }

    public boolean isActive() {
        return active;
    }
    public void toggleActive()
    {
        active = !active;
    }
    public boolean hasStatus()
    {
        return status != LeafStatus.unknown && active;
    }
    public void updateItem(LeafSensor copyFrom)
    {
        status = copyFrom.status;
        mqttTopic = copyFrom.mqttTopic;
        strStatus = copyFrom.strStatus;
        sensorName = copyFrom.sensorName;
        active = copyFrom.active;
        updateDate = copyFrom.updateDate;
        time_based_alarm_buzzer_enable = copyFrom.time_based_alarm_buzzer_enable;
        time_based_alarm_mobile_enable = copyFrom.time_based_alarm_mobile_enable;
        time_based_alarm_time_amount = copyFrom.time_based_alarm_time_amount;
        hourly_based_alarm_buzzer_enable = copyFrom.hourly_based_alarm_buzzer_enable;
        hourly_based_alarm_hour_min_time = copyFrom.hourly_based_alarm_hour_min_time;
        hourly_based_alarm_mobile_enable = copyFrom.hourly_based_alarm_mobile_enable;
    }

    public LeafStatus getStatus() {
        return status;
    }

    public String getMqttTopic() {
        return mqttTopic;
    }

    @Ignore
    public LeafSensor()
    {

    }

    public int getStatusAsColor()
    {
        if (!active)
            return Consts.getInstance().getUndefined_color();
        switch (status)
        {
            case open:
                return Consts.getInstance().getOpen_color();
            case locked:
                return Consts.getInstance().getLocked_color();
            case unlocked:
            case alarm:
                return Consts.getInstance().getUnlocked_color();
            default:
                return Consts.getInstance().getUndefined_color();
        }
    }

    public int getStatusAsImage()
    {
        if (!active)
            return R.drawable.sensor_mode_undefined;
        switch (status)
        {
            case open:
                return R.drawable.sensor_mode_open;
            case locked:
                return R.drawable.sensor_mode_locked;
            case unlocked:
            case alarm:
                return R.drawable.sensor_mode_unlocked;
            default:
                return R.drawable.sensor_mode_undefined;
        }
    }

    public int getLockStatusAsImage()
    {
        if (!active)
            return R.drawable.sensor_mode_undefined;
        switch (status)
        {
            case locked:
                return R.drawable.door_locked;
            case unlocked:
            case alarm:
            case open:
                return R.drawable.door_unlocked;
            default:
                return R.drawable.sensor_mode_undefined;
        }
    }
    public String getStatusAsString()
    {
        if (!active)
            return "";
        switch (status)
        {
            case open:
                return "opened";
            case locked:
                return "locked";
            case unlocked:
                return "closed";
            case alarm:
                return "alarm";
            default:
                return strStatus;
        }
    }
    public boolean isSameStatus(String strStatusData)
    {
        LeafStatus tmpStatus = LeafStatus.unknown;
        MqqtMessageResponseModel mqqtMessageData = new Gson().fromJson(strStatusData,MqqtMessageResponseModel.class);
        this.strStatus = mqqtMessageData.getState();
        if (strStatus.equalsIgnoreCase("opened"))
            tmpStatus = LeafStatus.open;
        else if (strStatus.equalsIgnoreCase("locked"))
            tmpStatus = LeafStatus.locked;
        else if (strStatus.equalsIgnoreCase("closed"))
            tmpStatus = LeafStatus.unlocked;
        else if (strStatus.equalsIgnoreCase("alarm"))
            tmpStatus = LeafStatus.alarm;
        return tmpStatus.equals(status);
    }
    public void processStatus(String strStatusData)
    {
        MqqtMessageResponseModel mqqtMessageData = new Gson().fromJson(strStatusData,MqqtMessageResponseModel.class);
        this.strStatus = mqqtMessageData.getState();
        if (strStatus.equalsIgnoreCase("opened"))
            status = LeafStatus.open;
        else if (strStatus.equalsIgnoreCase("locked"))
            status = LeafStatus.locked;
        else if (strStatus.equalsIgnoreCase("closed"))
            status = LeafStatus.unlocked;
        else if (strStatus.equalsIgnoreCase("alarm"))
            status = LeafStatus.alarm;
        else
            status = LeafStatus.unknown;
    }

    public String getSensorName() {
        return sensorName;
    }

    @Ignore
    public LeafSensor(String mqttTopic, String sensorName,boolean active)
    {
        this.mqttTopic = mqttTopic;
        this.sensorName = sensorName;
        this.active = active;
    }
    public void updateWifiSettings(String ssid,String wifi_pswd,boolean sensor_active)
    {
        wifi_ssid = ssid;
        this.wifi_pswd = wifi_pswd;
        this.active = sensor_active;
    }
    public LeafSensor(String mqttTopic, String sensorName,LeafStatus status,long updateDate,boolean active,long time_based_alarm_time_amount,
                      boolean time_based_alarm_mobile_enable,boolean time_based_alarm_buzzer_enable, long hourly_based_alarm_hour_min_time,
                      boolean hourly_based_alarm_mobile_enable,boolean hourly_based_alarm_buzzer_enable,String wifi_ssid,String wifi_pswd)
    {
        this.mqttTopic = mqttTopic;
        this.sensorName = sensorName;
        this.active = active;
        this.status = status;
        this.updateDate = updateDate;
        this.time_based_alarm_time_amount = time_based_alarm_time_amount;
        this.time_based_alarm_mobile_enable = time_based_alarm_mobile_enable;
        this.time_based_alarm_buzzer_enable = time_based_alarm_buzzer_enable;
        this.hourly_based_alarm_hour_min_time = hourly_based_alarm_hour_min_time;
        this.hourly_based_alarm_mobile_enable = hourly_based_alarm_mobile_enable;
        this.hourly_based_alarm_buzzer_enable = hourly_based_alarm_buzzer_enable;
        this.wifi_pswd = wifi_pswd;
        this.wifi_ssid = wifi_ssid;
        switch (status)
        {
            case open:
                strStatus = "opened";
            case locked:
                strStatus = "locked";
            case unlocked:
            case alarm:
                strStatus =  "closed";
            default:
                strStatus = "";
        }
    }

    public LeafSensor(String SN, String sensorName)
    {
        this.sensorSN = SN;
        this.sensorName = sensorName;
        this.mqttTopic = "event";
        this.active = true;
    }

    public String getSettingsAsJson(boolean forHTTP)
    {
        StringBuilder settings = new StringBuilder();
        if (forHTTP)
            settings.append("{\"notification_configuration\":");
        settings.append("{\"state_event_group\":");
        settings.append("" + state_event_group + ",");
        settings.append("\"timer_based\": {");
        settings.append("\"value_second\": " + time_based_alarm_time_amount + ",");
        settings.append("\"sound_enable\": " + (time_based_alarm_buzzer_enable ? "1" : "0") + ",");
        settings.append("\"mobap_enable\": " + (time_based_alarm_mobile_enable ? "1" : "0") + "},");
        settings.append("\"hour_based\": {");
        settings.append("\"value_second\": " + hourly_based_alarm_hour_min_time + ",");
        settings.append("\"sound_enable\": " + (hourly_based_alarm_buzzer_enable ? "1" : "0") + ",");
        settings.append("\"mobap_enable\": " + (hourly_based_alarm_mobile_enable ? "1" : "0") + "}}");
        if (forHTTP)
            settings.append("}");
        return settings.toString();
    }

    public String getWifiSettingsAsJson()
    {
        StringBuilder settings = new StringBuilder();
        settings.append("{\"online_mode_configuration\": {");
        settings.append("\"ssid\": \"" + wifi_ssid + "\",");
        settings.append("\"pswd\": \"" + wifi_pswd + "\",");
        settings.append("\"enable\": " + (active ? "1" : "0") + "}}");
        return settings.toString();
    }

}
