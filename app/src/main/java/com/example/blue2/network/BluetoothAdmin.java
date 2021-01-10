package com.example.blue2.network;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import androidx.fragment.app.Fragment;

public class BluetoothAdmin implements IBluetoothAdmin {

    public static final BluetoothAdmin sharedAdmin = new BluetoothAdmin();

    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final ArrayList<DiscoveryObserver> discoveryObservers = new ArrayList<>();

    private final ArrayList<BluetoothStateObserver> bluetoothStateObservers = new ArrayList<>();

    private BluetoothAdmin() {}

    @Override
    public boolean isBluetoothAvailable() {
        return mBluetoothAdapter != null;
    }

    @Override
    public boolean isEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    @Override
    public void enableBluetooth(Activity activity, int requestCode) {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableIntent, requestCode);
    }

    @Override
    public ArrayList<BluetoothDevice> getPairedDevices() {
        return new ArrayList<>(mBluetoothAdapter.getBondedDevices());
    }

    @Override
    public <T extends Activity & DiscoveryObserver> void startDiscovery(T observer) {
        startDiscovery(observer, observer);
    }

    @Override
    public <T extends Activity & DiscoveryObserver> void cancelDiscovery(T observer) {
        cancelDiscovery(observer, observer);
    }

    @Override
    public <T extends Fragment & DiscoveryObserver> void startDiscovery(T observer) {
        startDiscovery(observer.requireActivity(), observer);
    }

    @Override
    public <T extends Fragment & DiscoveryObserver> void cancelDiscovery(T observer) {
        cancelDiscovery(observer.requireActivity(), observer);
    }

    @Override
    public <T extends Activity & BluetoothStateObserver> void observeBluetoothState(T observer) {

        // Intent filter, to check if the usr changed the state of bluetooth
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        bluetoothStateObservers.add(observer);
        observer.registerReceiver(mStateChangedReceiver, filter);
    }

    @Override
    public <T extends Activity & BluetoothStateObserver> void unRegisterBluetoothStateObserver(T observer) {
        if (bluetoothStateObservers.contains(observer)) {
            bluetoothStateObservers.remove(observer);
            observer.unregisterReceiver(mStateChangedReceiver);
        }
    }

    @Override
    public BluetoothDevice getRemoteDevice(String address) {
        return mBluetoothAdapter.getRemoteDevice(address);
    }

    @Override
    public BluetoothServerSocket listenUsingRfcommWithServiceRecord(String name, UUID uuid) throws IOException {
        return mBluetoothAdapter.listenUsingRfcommWithServiceRecord(name,
                uuid);
    }

    /**
     * 3 Intent filters to get the results of the discovery in the BroadcastReceiver
     * if a device was found or the discover was finished, or discovery was started
     *
     * @param activity: activity that starts discovery
     * @param observer: observer for discovery events
     */
    private void startDiscovery(Activity activity, DiscoveryObserver observer) {
        IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter discoveryFinishedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        IntentFilter discoveryStartedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);

        activity.registerReceiver(mDiscoveryReceiver, foundFilter);
        activity.registerReceiver(mDiscoveryReceiver, discoveryFinishedFilter);
        activity.registerReceiver(mDiscoveryReceiver, discoveryStartedFilter);

        discoveryObservers.add(observer);
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();
    }

    private void cancelDiscovery(Activity activity, DiscoveryObserver observer) {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        if (discoveryObservers.contains(observer)) {
            discoveryObservers.remove(observer);
            activity.unregisterReceiver(mDiscoveryReceiver);
        }
    }

    private final BroadcastReceiver mStateChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state){
                    case BluetoothAdapter.STATE_ON:
                    case BluetoothAdapter.STATE_TURNING_ON:
                        for (BluetoothStateObserver observer : bluetoothStateObservers){
                            observer.onStateChanged(BluetoothState.ON);
                        }
                        break;

                    case BluetoothAdapter.STATE_OFF:
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        for (BluetoothStateObserver observer : bluetoothStateObservers){
                            observer.onStateChanged(BluetoothState.OFF);
                        }
                        break;
                }
            }
        }
    };

    // Receiver for discovery actions
    private final BroadcastReceiver mDiscoveryReceiver = new BroadcastReceiver() {
        public synchronized void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // in case of device found, add it to the list if not added before
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null && device.getName() != null) {
                    for (DiscoveryObserver observer : discoveryObservers) {
                        observer.onDeviceFound(device);
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                for (DiscoveryObserver observer : discoveryObservers) {
                    observer.onDiscoveryFinished();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                for (DiscoveryObserver observer : discoveryObservers) {
                    observer.onDiscoveryStarted();
                }
            }
        }
    };
}
