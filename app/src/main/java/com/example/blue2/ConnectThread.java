package com.example.blue2;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class ConnectThread extends Thread{
    private final BluetoothSocket mSocket;
    private final BluetoothDevice mDevice;
    private OnConnectListener mListener;
    private static final UUID MY_UUID_SECURE =  UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    interface OnConnectListener {
        void onConnectionSuccess();
        void onConnectionFailure();
    }
    public ConnectThread(BluetoothDevice device, OnConnectListener listener) {
        mDevice = device;
        mListener = listener;
        BluetoothSocket tmp = null;

        // Get a BluetoothSocket for a connection with the
        // given BluetoothDevice
        try {
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
        } catch (IOException e) {

            mListener.onConnectionFailure();
            Log.e("Galal Ahmed", "Socket create() failed", e);
        }
        mSocket = tmp;
    }

    public void run() {

        // Make a connection to the BluetoothSocket
        try {
            // This is a blocking call and will only return on a
            // successful connection or an exception
            mSocket.connect();
            mListener.onConnectionSuccess();
            Log.d("Galal Ahmed", "Connected");
        } catch (IOException e) {
            // Close the socket
            mListener.onConnectionFailure();
            try {
                mSocket.close();
            } catch (IOException e2) {
                Log.e("Galal Ahmed", "unable to close() socket during connection failure", e2);
            }
            return;
        }
    }

    public boolean cancel() {
        try {
            mSocket.close();
        } catch(IOException e) {
            Log.d("CONNECTTHREAD","Could not close connection:" + e.toString());
            return false;
        }
        return true;
    }
}
