package com.example.blue2.mvvm.view.listener;

import android.bluetooth.BluetoothDevice;

/**
 * interface to notify the observer on device selected from discovery fragment
 */
public interface ScanForDeviceCallback {
    void onDeviceSelected(BluetoothDevice bluetoothDevice);
}
