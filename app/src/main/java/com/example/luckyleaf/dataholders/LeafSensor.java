package com.example.luckyleaf.dataholders;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.luckyleaf.Consts;
import com.example.luckyleaf.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
        Calendar statusTime = Calendar.getInstance();
        statusTime.setTimeInMillis(updateDate);
        SimpleDateFormat formatter = new SimpleDateFormat(isCurrentDay(statusTime) ? SHORT_DATE_TIME_FORMAT : FULL_DATE_TIME_FORMAT, Locale.ENGLISH);
        return formatter.format(statusTime.getTime());

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
    public String getStatusAsString()
    {
        if (!active)
            return "";
        switch (status)
        {
            case open:
                return "open";
            case locked:
                return "locked";
            case unlocked:
                return "unlocked";
            case alarm:
                return "alarm";
            default:
                return strStatus;
        }
    }
    public boolean isSameStatus(String strStatus)
    {
        LeafStatus tmpStatus = LeafStatus.unknown;
        if (strStatus.equalsIgnoreCase("open"))
            tmpStatus = LeafStatus.open;
        else if (strStatus.equalsIgnoreCase("locked"))
            tmpStatus = LeafStatus.locked;
        else if (strStatus.equalsIgnoreCase("unlocked"))
            tmpStatus = LeafStatus.unlocked;
        else if (strStatus.equalsIgnoreCase("alarm"))
            tmpStatus = LeafStatus.alarm;
        return tmpStatus.equals(status);
    }
    public void processStatus(String strStatus)
    {
        this.strStatus = strStatus;
        if (strStatus.equalsIgnoreCase("open"))
            status = LeafStatus.open;
        else if (strStatus.equalsIgnoreCase("locked"))
            status = LeafStatus.locked;
        else if (strStatus.equalsIgnoreCase("unlocked"))
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
                strStatus = "open";
            case locked:
                strStatus = "locked";
            case unlocked:
            case alarm:
                strStatus =  "unlocked";
            default:
                strStatus = "";
        }
    }
}
