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
    @ColumnInfo(name = "active")
    boolean     active;
    @ColumnInfo(name = "updateDate")
    long     updateDate;
    @ColumnInfo(name = "timeAllowedUnlockInMin")
    long     timeAllowedUnlockInMin;
    @ColumnInfo(name = "timeInDayToCheckHour")
    long     timeInDayToCheckHour;
    @ColumnInfo(name = "timeInDayToCheckMin")
    long     timeInDayToCheckMin;
    @ColumnInfo(name = "timeAllowedUnlockActive")
    boolean     timeAllowedUnlockActive;
    @ColumnInfo(name = "timeInDayToChecActive")
    boolean     timeInDayToChecActive;
    @Ignore
    boolean     editMode;
    @Ignore
    boolean     notifyMobile;
    @Ignore
    boolean     notifySensor;

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

    public void setTimeAllowedUnlockActive(boolean timeAllowedUnlockActive) {
        this.timeAllowedUnlockActive = timeAllowedUnlockActive;
    }

    public void setTimeInDayToChecActive(boolean timeInDayToChecActive) {
        this.timeInDayToChecActive = timeInDayToChecActive;
    }

    public boolean isTimeAllowedUnlockActive() {
        return timeAllowedUnlockActive;
    }

    public boolean isTimeInDayToChecActive() {
        return timeInDayToChecActive;
    }

    public void setTimeInDayToCheckMin(long timeInDayToCheckMin) {
        this.timeInDayToCheckMin = timeInDayToCheckMin;
    }

    public long getTimeAllowedUnlockInMin() {
        return timeAllowedUnlockInMin;
    }

    public String getTimeAllowedUnlockInMinToStr() {
        if (timeAllowedUnlockInMin<=0) return "";
        return String.valueOf(timeAllowedUnlockInMin);
    }

    public String getTimeCheckToStr() {
        return String.format("%02d:%02d",timeInDayToCheckHour,timeInDayToCheckMin);
    }


    public long getTimeInDayToCheckHour() {
        return timeInDayToCheckHour;
    }

    public long getTimeInDayToCheckMin() {
        return timeInDayToCheckMin;
    }

    public void setTimeInDayToCheckHour(long timeInDayToCheckHour) {
        this.timeInDayToCheckHour = timeInDayToCheckHour;
    }

    public void setTimeInDayToCheck(int timeInDayToCheckHour, int timeInDayToCheckHourMin) {
        this.timeInDayToCheckHour = timeInDayToCheckHour;
        this.timeInDayToCheckMin = timeInDayToCheckHourMin;
    }

    public void setTimeAllowedUnlockInMin(long timeAllowedUnlockInMin) {
        this.timeAllowedUnlockInMin = timeAllowedUnlockInMin;
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

    public LeafSensor(String mqttTopic, String sensorName,LeafStatus status,long updateDate,boolean active,long timeAllowedUnlockInMin,long timeInDayToCheckHour,long timeInDayToCheckMin,boolean timeAllowedUnlockActive,boolean timeInDayToChecActive)
    {
        this.mqttTopic = mqttTopic;
        this.sensorName = sensorName;
        this.active = active;
        this.status = status;
        this.updateDate = updateDate;
        this.timeInDayToCheckMin = timeInDayToCheckMin;
        this.timeInDayToCheckHour = timeInDayToCheckHour;
        this.timeAllowedUnlockInMin = timeAllowedUnlockInMin;
        this.timeAllowedUnlockActive = timeAllowedUnlockActive;
        this.timeInDayToChecActive = timeInDayToChecActive;
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
}
