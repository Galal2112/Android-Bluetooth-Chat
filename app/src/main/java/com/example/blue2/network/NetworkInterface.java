package com.example.blue2.network;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.util.Map;

public interface NetworkInterface {


    void startListenerThread();

    void connectToDevice(BluetoothDevice bluetoothDevice);

    void onDeviceConnected(BluetoothSocket socket, BluetoothDevice device);

    void sendData(byte[] data);

    void sendBroadcast(String action, Map<String, String> extras);

    void cancel();

    void onConnectionFailed();

    void onConnectionLost();

    BluetoothService.StartConnectionThread getmStartConnectionThread();

    BluetoothService.ConnectedThread getmConnectedThread();

    BluetoothService.AcceptConnectionThread getmAcceptConnectionThread();

    void setmStartConnectionThread(BluetoothService.StartConnectionThread mStartConnectionThread);

    void setmConnectedThread(BluetoothService.ConnectedThread mConnectedThread);

    void setmAcceptConnectionThread(BluetoothService.AcceptConnectionThread mAcceptConnectionThread);

    String getmCurrentDeviceAddress();

    void setmCurrentDeviceAddress(String mCurrentDeviceAddress);

    void setmBluetoothAdapter(BluetoothAdapter mBluetoothAdapter);

    boolean isBrodcast();


}
