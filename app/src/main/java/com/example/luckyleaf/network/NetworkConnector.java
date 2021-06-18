package com.example.luckyleaf.network;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.luckyleaf.dataholders.LeafSensor;

import java.util.List;

public class NetworkConnector {
    private String sensorWifiName = "Test_config_ap";

    void disconnectFromSensorWifi(Context context, LeafSensor sensor)
    {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + sensorWifiName + "\"")) {
                wifiManager.enableNetwork(i.networkId, false);
                wifiManager.disconnect();
                wifiManager.removeNetwork(i.networkId);
                break;
            }
        }
    }
    public boolean connectToSensorWifi(Context context, LeafSensor sensor) {

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + sensorWifiName + "\"";   // Please note the quotes. String should contain ssid in quotes

        conf.wepKeys[0] = "\"" + sensor.getSensorSN() + "\"";
        conf.wepTxKeyIndex = 0;
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        conf.preSharedKey = "\"" + sensor.getSensorSN() + "\"";

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(conf);

        if (wifiManager.getConnectionInfo().getSSID().equals("\"" + sensorWifiName + "\""))
            return true;
        else
        {
            context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            return false;
        }
    }
}
