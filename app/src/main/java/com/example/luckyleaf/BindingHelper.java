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
        String status = "Door";
        if (statusText!=null && sensor!=null)
        {
            statusText.setText("is " + sensor.getStatusAsString());
        }
    }
}
