package eu.h2020.helios_social.core.sensor.ext;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import eu.h2020.helios_social.core.sensor.Sensor;

/**
 *  This class provides an implementation of sensor API for detecting currentlu active Wifi network.
 *  WifiSensor is a subclass of the abstract base class Sensor.
 *  It uses Android WifiManager API to detect the current Wifi and changes in network state.
 *  It detects the ssid of the current, connected Wifi (if any).
 */
public class WifiSensor extends Sensor {

    private WifiManager mWifiManager;
    private WifiSensorReceiver mWifiReceiver;
    private ContextWrapper appEnv;
    private String ssid;
    private boolean registered;

    private static final String TAG = "HeliosWifiSensor";

    /**
     * Creates a WifiSensor
     * @param appEnv the application env
     */
    public WifiSensor(String id, ContextWrapper appEnv) {
        super(id);
        this.appEnv = appEnv;
        mWifiManager = (WifiManager) appEnv.getSystemService(Context.WIFI_SERVICE);
        // The receiver listens for the changed network state
        mWifiReceiver = new WifiSensorReceiver();
        ssid = null;
        registered = false;
    }

    /**
     * Creates a WifiSensor
     * @param appEnv the application env
     */
    public WifiSensor(ContextWrapper appEnv) {
        this(null, appEnv);
    }

    @Override
    public void startUpdates() {
        getWifiSSID();
        appEnv.registerReceiver(
                mWifiReceiver,
                new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        );
        registered = true;
        Log.d(TAG, "startUpdates");
    }

    @Override
    public void stopUpdates() {
        if(registered) {
            appEnv.unregisterReceiver(mWifiReceiver);
            ssid = null;
            Log.d(TAG, "stopUpdates");
        }
    }

    /**
     * Handles Wifi network state change actions
     */
    public class WifiSensorReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"WifiReceiver; onReceive()");
            final String action = intent.getAction();
            if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                // Sensor receive SSID value, and notifies Sensor value listeners about the value
                receiveValue(getWifiSSID());
            }
        }
    }

    /**
     * Gets current Wifi SSID value
     */
    public String getWifiSSID() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        ssid = wifiInfo.getSSID();
        Log.d(TAG,"Current ssid: " + ssid);
        if(ssid != null && ssid.length() > 2) {  // remove double quotes
            if(ssid.charAt(0) == '"' && ssid.charAt(ssid.length()-1) == '"') {
                ssid = ssid.substring(1, ssid.length() - 1);
            }
        }
        return ssid;
    }
}