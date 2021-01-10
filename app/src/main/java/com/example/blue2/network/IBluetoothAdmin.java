package com.example.blue2.network;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import androidx.fragment.app.Fragment;

public interface IBluetoothAdmin {

    boolean isBluetoothAvailable();
    boolean isEnabled();
    void enableBluetooth(Activity activity, int requestCode);
    ArrayList<BluetoothDevice> getPairedDevices();
    void makeMyDeviceDiscoverable(Activity activity, int requestCode);
    <T extends Activity & DiscoveryObserver> void startDiscovery(T observer);
    <T extends Activity & DiscoveryObserver> void cancelDiscovery(T observer);
    <T extends Fragment & DiscoveryObserver> void startDiscovery(T observer);
    <T extends Fragment & DiscoveryObserver> void cancelDiscovery(T observer);
    <T extends Activity & BluetoothStateObserver> void observeBluetoothState(T observer);
    <T extends Activity & BluetoothStateObserver> void unRegisterBluetoothStateObserver(T observer);
    BluetoothDevice getRemoteDevice(String address);
    BluetoothServerSocket listenUsingRfcommWithServiceRecord(String name, UUID uuid) throws IOException;

    interface DiscoveryObserver {
        void onDiscoveryStarted();
        void onDiscoveryFinished();
        void onDeviceFound(BluetoothDevice device);
    }

    interface BluetoothStateObserver {
        void onStateChanged(BluetoothState state);
    }

    enum BluetoothState {
        ON, OFF
    }
}