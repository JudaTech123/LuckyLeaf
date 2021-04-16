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

    public LeafSensor(String mqttTopic, String sensorName,LeafStatus status,long updateDate,boolean active)
    {
        this.mqttTopic = mqttTopic;
        this.sensorName = sensorName;
        this.active = active;
        this.status = status;
        this.updateDate = updateDate;
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
