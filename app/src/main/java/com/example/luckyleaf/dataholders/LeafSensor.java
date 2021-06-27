package com.example.luckyleaf.dataholders;

import android.util.Log;

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
    @Ignore
    boolean     tmpActive;
    @Ignore
    String      tmpWifi_ssid;
    @Ignore
    String      tmpWifi_pswd;
    @Ignore
    boolean     tmpHourly_based_alarm_buzzer_enable;
    @Ignore
    boolean     tmpHourly_based_alarm_mobile_enable;
    @Ignore
    long        tmpHourly_based_alarm_hour_min_time;
    @Ignore
    boolean     tmpTime_based_alarm_buzzer_enable;
    @Ignore
    boolean     tmpTime_based_alarm_mobile_enable;
    @Ignore
    long        tmpState_event_group;
    @Ignore
    long        tmpTime_based_alarm_time_amount;


    public void setSensorSN(String sensorSN) {
        this.sensorSN = sensorSN;
    }

    public String getSensorSN() {
        return sensorSN;
    }

    public void setWifi_pswd(String wifi_pswd) {
        tmpWifi_pswd = wifi_pswd;
    }

    public String getWifi_pswd() {
        return tmpWifi_pswd;
    }

    public void setWifi_ssid(String wifi_ssid) {
        tmpWifi_ssid = wifi_ssid;
    }

    public String getWifi_ssid() {
        return tmpWifi_ssid;
    }

    public void setState_event_group(long event_group) {
        this.tmpState_event_group = event_group;
    }

    public long getState_event_group() {
        return tmpState_event_group;
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
        this.tmpTime_based_alarm_time_amount = time_based_alarm_time_amount;
    }

    public void setTime_based_alarm_buzzer_enable(boolean time_based_alarm_buzzer_enable) {
        this.tmpTime_based_alarm_buzzer_enable = time_based_alarm_buzzer_enable;
    }
    public boolean getTime_based_alarm_buzzer_enable() {
        return tmpTime_based_alarm_buzzer_enable;
    }

    public void setTime_based_alarm_mobile_enable(boolean time_based_alarm_mobile_enable) {
        this.tmpTime_based_alarm_mobile_enable = time_based_alarm_mobile_enable;
    }
    public boolean getTime_based_alarm_mobile_enable() {
        return tmpTime_based_alarm_mobile_enable;
    }

    public long getTime_based_alarm_time_amount() {
        return tmpTime_based_alarm_time_amount;
    }

    public void setHourly_based_alarm_hour_min_time(long hourly_based_alarm_hour_min_time) {
        this.tmpHourly_based_alarm_hour_min_time = hourly_based_alarm_hour_min_time;
    }
    //hour*60+min. this will tell us what min and hour to check if sensor is not locked
    public long getHourly_based_alarm_hour_min_time() {
        return tmpHourly_based_alarm_hour_min_time;
    }

    public void setHourly_based_alarm_buzzer_enable(boolean hourly_based_alarm_buzzer_enable) {
        this.tmpHourly_based_alarm_buzzer_enable = hourly_based_alarm_buzzer_enable;
    }
    public boolean getHourly_based_alarm_buzzer_enable()
    {
        return tmpHourly_based_alarm_buzzer_enable;
    }

    public void setHourly_based_alarm_mobile_enable(boolean hourly_based_alarm_mobile_enable) {
        this.tmpHourly_based_alarm_mobile_enable = hourly_based_alarm_mobile_enable;
    }
    public boolean getHourly_based_alarm_mobile_enable()
    {
        return tmpHourly_based_alarm_mobile_enable;
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
        return tmpActive;
    }
    public void toggleActive()
    {
        tmpActive = !tmpActive;
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

    /**
     * This function will check if the status is in the allowed event group:
     * open == 1
     * lock == 4
     * closed == 2
     * @param status what status we got
     * @return true is we should continue process status
     */
    public boolean isStatusAllowed(String status)
    {
        //open      == 1
        //lock      == 4
        //closed    == 2
        Log.d("isStatusAllowed","status = " + status + " getState_event_group = " + getState_event_group());
        if (status.equals("opened") && (getState_event_group() & 1)!=0)
            return true;
        if (status.equals("locked") && (getState_event_group() & 4)!=0)
            return true;
        if (status.equals("closed") && (getState_event_group() & 2)!=0)
            return true;
        return false;
    }
    public boolean isSameStatus(String strStatusData)
    {
        LeafStatus tmpStatus = LeafStatus.unknown;
        try {
            MqqtMessageResponseModel mqqtMessageData = new Gson().fromJson(strStatusData, MqqtMessageResponseModel.class);

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
        }catch (Exception ignore){return true;}
    }
    public void processStatus(MqqtMessageResponseModel mqqtMessageData)
    {
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

        tmpActive = active;
        tmpWifi_ssid = wifi_ssid;
        tmpWifi_pswd = wifi_pswd;
        tmpHourly_based_alarm_buzzer_enable = hourly_based_alarm_buzzer_enable;
        tmpHourly_based_alarm_mobile_enable = hourly_based_alarm_mobile_enable;
        tmpHourly_based_alarm_hour_min_time = hourly_based_alarm_hour_min_time;
        tmpTime_based_alarm_buzzer_enable = time_based_alarm_buzzer_enable;
        tmpTime_based_alarm_mobile_enable = time_based_alarm_mobile_enable;
        tmpState_event_group = state_event_group;
        tmpTime_based_alarm_time_amount = time_based_alarm_time_amount;
    }

    public LeafSensor(String SN, String sensorName)
    {
        this.sensorSN = SN;
        this.sensorName = sensorName;
        this.mqttTopic = "event";
        this.active = true;
    }

    public boolean isNotifactionSettingsChanged()
    {
        if (state_event_group!=tmpState_event_group) return true;
        if (time_based_alarm_time_amount!=tmpTime_based_alarm_time_amount) return true;
        if (time_based_alarm_buzzer_enable!=tmpTime_based_alarm_buzzer_enable) return true;
        if (time_based_alarm_mobile_enable!=tmpTime_based_alarm_mobile_enable) return true;
        if (hourly_based_alarm_hour_min_time!=tmpHourly_based_alarm_hour_min_time) return true;
        if (hourly_based_alarm_buzzer_enable!=tmpHourly_based_alarm_buzzer_enable) return true;
        if (hourly_based_alarm_mobile_enable!=tmpHourly_based_alarm_mobile_enable) return true;
        if (tmpActive!=active) return true;
        if (!tmpWifi_ssid.equals(wifi_ssid)) return true;
        if (!tmpWifi_pswd.equals(wifi_pswd)) return true;
        return false;
    }

    public String getSettingsAsJson()
    {
        state_event_group = tmpState_event_group;
        time_based_alarm_time_amount = tmpTime_based_alarm_time_amount;
        time_based_alarm_buzzer_enable = tmpTime_based_alarm_buzzer_enable;
        time_based_alarm_mobile_enable = tmpTime_based_alarm_mobile_enable;
        hourly_based_alarm_hour_min_time = tmpHourly_based_alarm_hour_min_time;
        hourly_based_alarm_buzzer_enable = tmpHourly_based_alarm_buzzer_enable;
        hourly_based_alarm_mobile_enable = tmpHourly_based_alarm_mobile_enable;

        StringBuilder settings = new StringBuilder();
//        if (forHTTP)
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
//        if (forHTTP)
            settings.append("}");
        return settings.toString();
    }

    public boolean isWifiSettingsChanged()
    {
        if (!wifi_ssid.equals(tmpWifi_ssid)) return true;
        if (!wifi_pswd.equals(tmpWifi_pswd)) return true;
        if (active!=tmpActive) return true;

        return false;
    }

    public String getWifiSettingsAsJson()
    {
        wifi_ssid = tmpWifi_ssid;
        wifi_pswd = tmpWifi_pswd;
        active = tmpActive;
        StringBuilder settings = new StringBuilder();
        settings.append("{\"online_mode_configuration\": {");
        settings.append("\"ssid\": \"" + wifi_ssid + "\",");
        settings.append("\"pswd\": \"" + wifi_pswd + "\",");
        settings.append("\"enable\": " + (active ? "1" : "0") + "}}");
        return settings.toString();
    }

}
