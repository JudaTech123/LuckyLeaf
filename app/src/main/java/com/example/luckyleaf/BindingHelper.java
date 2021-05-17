package com.example.luckyleaf;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.example.luckyleaf.dataholders.LeafSensor;

public class BindingHelper {
    @BindingAdapter("showIfHasStatus")
    public static void showIfHasStatus(View view, LeafSensor sensor)
    {
        if (view==null || sensor==null) return;
        view.setVisibility(sensor.hasStatus() ? View.VISIBLE : View.GONE);
    }
    @BindingAdapter("sensorStatus")
    public static void sensorStatus(ImageView statusImg, LeafSensor sensor)
    {
        if (statusImg!=null && sensor!=null)
        {
            statusImg.setImageResource(sensor.getStatusAsImage());
        }
    }
    @BindingAdapter("sensorLockStatus")
    public static void sensorLockStatus(ImageView statusImg, LeafSensor sensor)
    {
        if (statusImg!=null && sensor!=null)
        {
            int imageID = sensor.getLockStatusAsImage();
            if (imageID==R.drawable.sensor_mode_undefined)
                statusImg.setVisibility(View.INVISIBLE);
            else {
                statusImg.setVisibility(View.VISIBLE);
                statusImg.setImageResource(sensor.getLockStatusAsImage());
            }
        }
    }

    @BindingAdapter("sensorActive")
    public static void sensorActive(ImageView statusImg, LeafSensor sensor)
    {
        if (statusImg!=null && sensor!=null)
        {
            if (sensor.isActive())
                statusImg.setImageResource(R.drawable.track_active);
            else
                statusImg.setImageResource(R.drawable.track_notactive);
        }
    }

    @BindingAdapter("statusActive")
    public static void statusActive(ImageView statusImg, boolean active)
    {
        if (statusImg!=null)
        {
            if (active)
                statusImg.setImageResource(R.drawable.track_active);
            else
                statusImg.setImageResource(R.drawable.track_notactive);
        }
    }

    @BindingAdapter("isSoundActive")
    public static void markSoundStatus(ImageView statusImg, LeafSensor sensor)
    {
        if (statusImg!=null && sensor!=null)
        {
            if (sensor.isActive())
                statusImg.setImageResource(R.drawable.speaker_on);
            else
                statusImg.setImageResource(R.drawable.speaker_off);
        }
    }

    @BindingAdapter("sensorStatus")
    public static void sensorStatus(TextView statusText, LeafSensor sensor)
    {
        if (statusText!=null && sensor!=null)
        {
            statusText.setText(sensor.getStatusAsString());
        }
    }
}
