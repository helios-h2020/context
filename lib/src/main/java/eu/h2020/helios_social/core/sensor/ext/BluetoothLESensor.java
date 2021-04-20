package eu.h2020.helios_social.core.sensor.ext;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.ContextWrapper;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import eu.h2020.helios_social.core.sensor.Sensor;

/**
 *  This class provides an implementation of sensor API for scanning bluetooth LE devices.
 *  BluetoothLESensor is a subclass of the abstract base class Sensor.
 *  It uses Android BluetoothAdapter API.
 */
public class BluetoothLESensor extends Sensor {

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mScanner;
    private ContextWrapper appEnv;
    private ScanCallback mScanCallback;
    private List<ScanFilter> filters;
    private ScanSettings settings;
    private static final String TAG = "HeliosBluetoothLESensor";


    /**
     * Creates a BluetoothLESensor
     *
     * @param appEnv the application env
     */
    public BluetoothLESensor(ContextWrapper appEnv) {
        this(null, appEnv, null, new ScanSettings.Builder().build());
    }

    /**
     * Creates a BluetoothLESensor
     *
     * @param appEnv
     * @param filters
     * @param settings
     */
    public BluetoothLESensor(String id, ContextWrapper appEnv, List<ScanFilter> filters, ScanSettings settings) {
        super(id);
        this.appEnv = appEnv;
        this.filters = filters;
        this.settings = settings;
        mBluetoothAdapter = null;
        mScanner = null;
        createBluetoothAdapter();
    }

    @Override
    public void startUpdates() {
        if (isBluetoothEnabled()) {
            mScanner.startScan(filters, settings, mScanCallback);
            Log.d(TAG, "startUpdates");
        }
    }

    @Override
    public void stopUpdates() {
        if (isBluetoothEnabled()) {
            mScanner.stopScan(mScanCallback);
            Log.d(TAG, "stopUpdates");
        }
    }

    /**
     * Checks if Bluetooth LE enabled
     * @return boolean
     */
    public boolean isBluetoothEnabled() {
        return (mBluetoothAdapter != null && mScanner != null && mBluetoothAdapter.isEnabled());
    }

    private void createBluetoothAdapter() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Log.d(TAG, "Device doesn't support Bluetooth!");
            return;
        } else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (appEnv instanceof Activity) {
                int REQUEST_ENABLE_BT = 1;
                ((Activity) appEnv).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        mScanner = mBluetoothAdapter.getBluetoothLeScanner();

        // Device scan callback.
        mScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                if (callbackType == ScanSettings.CALLBACK_TYPE_ALL_MATCHES) {
                    BluetoothDevice device = result.getDevice();
                    String deviceName = device.getName();
                    int rssi = result.getRssi();
                    Log.d(TAG, "BT device found: " + deviceName + ",rssi:" + rssi);
                    receiveValue(new Object[]{device, new Integer(rssi)});
                }
            }
        };
    }
}