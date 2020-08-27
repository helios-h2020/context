package eu.h2020.helios_social.core.sensor.ext;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.ArrayList;

import eu.h2020.helios_social.core.sensor.Sensor;

/**
 *  This class provides an implementation of sensor API for discovering bluetooth devices.
 *  BluetoothSensor is a subclass of the abstract base class Sensor.
 *  It uses Android BluetoothAdapter API.
 */
public class BluetoothSensor extends Sensor {

    private BluetoothAdapter mBluetoothAdapter;
    private ContextWrapper appEnv;
    private BroadcastReceiver mReceiver;
    private static final String TAG = "HeliosBluetoothSensor";

    /**
     * Creates a WifiSensor
     *
     * @param appEnv the application env
     */
    public BluetoothSensor(ContextWrapper appEnv) {
        this.appEnv = appEnv;
        createBluetoothAdapter();
    }

    @Override
    public void startUpdates() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        appEnv.registerReceiver(mReceiver, filter);
        Log.d(TAG, "startUpdates");
    }

    @Override
    public void stopUpdates() {
        appEnv.unregisterReceiver(mReceiver);
        Log.d(TAG, "stopUpdates");
    }

    /**
     * Starts discovering bluetooth devices
     */
    public void startDiscovery() {
        if (isBluetoothEnabled()) {
            mBluetoothAdapter.startDiscovery();
        }
    }

    /**
     * Checks if Bluetooth LE enabled
     * @return boolean
     */
    public boolean isBluetoothEnabled() {
        return (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled());
    }

    private void createBluetoothAdapter() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Log.d(TAG, "Device doesn't support Bluetooth!");
        } else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (appEnv instanceof Activity) {
                int REQUEST_ENABLE_BT = 1;
                ((Activity) appEnv).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        mReceiver = new BroadcastReceiver() {
            ArrayList<Object[]> devices = null;
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                //Finding devices
                if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                    devices = new ArrayList<Object[]>();
                    Log.d(TAG, "start discovery");
                } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String hwaddr = device.getAddress();
                    int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                    devices.add(new Object[]{device, new Integer(rssi)});
                    Log.d(TAG, "found device:" + hwaddr + "rssi:" + rssi);
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    receiveValue(devices);
                    Log.d(TAG, "discovery finished");
                }
            }
        };
    }
}