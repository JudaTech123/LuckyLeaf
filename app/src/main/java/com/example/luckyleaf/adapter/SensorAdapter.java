package com.example.luckyleaf.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.example.luckyleaf.R;
import com.example.luckyleaf.databinding.ItemSensorBinding;
import com.example.luckyleaf.databinding.ItemSensorOpenBinding;
import com.example.luckyleaf.dataholders.LeafSensor;

import java.util.ArrayList;
import java.util.List;

public class SensorAdapter extends RecyclerView.Adapter<SensorListItem>{
    private List<LeafSensor> sensorList;
    private final Context context;
    private final SensorPressedCallback callback;
    private final SensorEditChangedCallback textChangedCallBack;
    private final SensorEditChangedCallback ssidChangedCallBack;
    private final SensorEditChangedCallback wifi_pwdChangedCallBack;

    public List<LeafSensor> getSensorList() {
        return sensorList;
    }

    public void updateSensorList(List<LeafSensor> sensorList) {
        if (this.sensorList==null || sensorList==null)
            this.sensorList = sensorList;
        else
        {
            for (int storedSensorIndex=0;storedSensorIndex<this.sensorList.size();storedSensorIndex++)
            {
                for (int newSensorIndex=0;newSensorIndex<sensorList.size();newSensorIndex++)
                {
                    LeafSensor storedSensor = this.sensorList.get(storedSensorIndex);
                    LeafSensor newSensor = sensorList.get(newSensorIndex);
                    if (!storedSensor.getSensorName().equals(newSensor.getSensorName())) continue;
                    storedSensor.updateItem(newSensor);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateItem(LeafSensor sensor,int index) {
        if (index==-1 || index>sensorList.size()) return;
        sensorList.get(index).updateItem(sensor);
        notifyItemChanged(index);
    }

    public SensorAdapter(Context context, ArrayList<LeafSensor> sensorList, SensorPressedCallback callback,SensorEditChangedCallback textChangedCallBack,
                         SensorEditChangedCallback ssidChangedCallBack,SensorEditChangedCallback wifi_pwdChangedCallBack)
    {
        this.sensorList = sensorList;
        this.context = context;
        this.callback = callback;
        this.textChangedCallBack = textChangedCallBack;
        this.ssidChangedCallBack = ssidChangedCallBack;
        this.wifi_pwdChangedCallBack = wifi_pwdChangedCallBack;
    }

    @Override
    public int getItemViewType(int position) {
        if (sensorList==null || position < 0 || position >= sensorList.size()) super.getItemViewType(position);
        LeafSensor sensor = sensorList.get(position);
        if (sensor.isEditMode())
            return 1;
        else
            return 0;
    }

    @NonNull
    @Override
    public SensorListItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==0)
        {
            ItemSensorBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_sensor, parent, false);
            dataBinding.setSensorIndex(-1);
            dataBinding.setItemClick(callback);
            return new SensorViewHolder(dataBinding);
        }
        else
        {
            ItemSensorOpenBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_sensor_open, parent, false);
            dataBinding.setSensorIndex(-1);
            dataBinding.setItemClicked(callback);
            return new SensorEditViewHolder(dataBinding);
        }
    }
    private void setIconColor(ImageView imgIcon,int index)
    {
        if (index==0)
            imgIcon.setImageResource(R.drawable.icon_1);
        if (index==1)
            imgIcon.setImageResource(R.drawable.icon_2);
        if (index==2)
            imgIcon.setImageResource(R.drawable.icon_3);
        if (index==3)
            imgIcon.setImageResource(R.drawable.icon_4);
        if (index==4)
            imgIcon.setImageResource(R.drawable.icon_5);
        if (index==5)
            imgIcon.setImageResource(R.drawable.icon_6);
    }
    TextWatcher timeChanged = null;
    TextWatcher ssidChanged = null;
    TextWatcher pwdChanged = null;
    @Override
    public void onBindViewHolder(@NonNull SensorListItem holder, int position) {
        if (position < 0 || position >= sensorList.size()) return;
        LeafSensor sensor = sensorList.get(position);
        if (sensor == null) return;


        if (getItemViewType(position)==0)
        {
            ViewDataBinding dataBinding = holder.getMyDataBinding();
            ItemSensorBinding dataBind = (ItemSensorBinding) dataBinding;
            dataBind.setSensorIndex(position);
            dataBind.setSensorData(sensor);
            setIconColor(dataBind.imgShortLogo,position % 6);
            if (position==sensorList.size()-1)
                dataBind.getRoot().setBackgroundResource(R.drawable.list_end_background);
            else
                dataBind.getRoot().setBackgroundResource(R.drawable.list_background);
        }
        if (getItemViewType(position)==1)
        {
            ViewDataBinding dataBinding = holder.getMyDataBinding();
            ItemSensorOpenBinding dataBind = (ItemSensorOpenBinding) dataBinding;
            dataBind.setSensorIndex(position);
            dataBind.setSensorData(sensor);
            setIconColor(dataBind.imgShortLogo,position % 6);
            if (position==sensorList.size()-1)
                dataBind.getRoot().setBackgroundResource(R.drawable.list_end_background);
            else
                dataBind.getRoot().setBackgroundResource(R.drawable.list_background);
            if (timeChanged!=null)
                dataBind.edtTimeToBuzz.removeTextChangedListener(timeChanged);
            dataBind.edtTimeToBuzz.setText(sensor.getTime_based_alarm_time_amount() + "");
            long currHourMin = sensor.getHourly_based_alarm_hour_min_time();
            if (currHourMin!=0) {
                int hour = (int) currHourMin / 60;
                int min = (int) currHourMin - (hour * 60);
                dataBind.txtTimeInDay.setText(hour + ":" + min);
            }
            else
            {
                dataBind.txtTimeInDay.setText("Pick Time");
            }
            dataBind.txtTimeInDay.setPaintFlags(dataBind.txtTimeInDay.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
            if (timeChanged==null)
                timeChanged = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (textChangedCallBack!=null)
                        {
                            textChangedCallBack.timeChanged(sensor,editable.toString(),position);
                        }
                    }
                };
            if (ssidChanged==null)
                ssidChanged = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (ssidChangedCallBack!=null)
                        {
                            ssidChangedCallBack.timeChanged(sensor,editable.toString(),position);
                        }
                    }
                };
            if (pwdChanged==null)
                pwdChanged = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (wifi_pwdChangedCallBack!=null)
                        {
                            wifi_pwdChangedCallBack.timeChanged(sensor,editable.toString(),position);
                        }
                    }
                };
            dataBind.edtTimeToBuzz.addTextChangedListener(timeChanged);
            dataBind.edtWifiSSID.addTextChangedListener(ssidChanged);
            dataBind.edtWifiPWD.addTextChangedListener(pwdChanged);
        }

    }

    @Override
    public int getItemCount() {
        return (sensorList == null) ? 0 : sensorList.size();
    }

    public interface SensorPressedCallback{
        void sensorClicked(LeafSensor sensor, int index,int clickType);
    }
    public interface SensorEditChangedCallback{
        void timeChanged(LeafSensor sensor,String text, int index);
    }
}
