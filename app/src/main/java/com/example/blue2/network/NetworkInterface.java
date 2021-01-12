package com.example.blue2.network;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.util.Map;

public interface NetworkInterface {

    /**
     * cancel connection if any and start the thread that accepts connections
     */
    void startListenerThread();
    /**
     *  cancel current connection if any and try connecting to the provided device
     * @param bluetoothDevice : the device to be connected to
     */
    void connectToDevice(BluetoothDevice bluetoothDevice);
    /**
     * When receiving a connection from a device, open a socket with this device to start sending and receiving data
     * @param socket : the current connected Socket
     * @param device : the current connected device
     */
    void onDeviceConnected(BluetoothSocket socket, BluetoothDevice device);
    /**
     * Send data in the current socket connected
     * @param data : data to be send
     */
    void sendData(byte[] data);
    /**
     * utility method to send broadcast with action and data
     *
     * @param action : the Massege
     * @param extras : extras for the brodcast
     */
    void sendBroadcast(String action, Map<String, String> extras);

    /**
     * Cancel all threads (all Threads == null)
     */
    void cancel();
    /**
     * When connection fails, try to open the connection again
     */
    void onConnectionFailed();
    /**
     *When connection is lost, try to open the connection again
     */
    void onConnectionLost();
    /**
     * Methode , um den aktuellen ConnectionThread abzuholen
     * @return : der  aktuelle ConnectionThread  .
     */

    BluetoothService.StartConnectionThread getmStartConnectionThread();
    /**
     * Methode , um den aktuellen ConnectedThread abzuholen
     * @return : der  aktuelle ConnectedThread  .
     */
    BluetoothService.ConnectedThread getmConnectedThread();
    /**
     * Methode , um den aktuellen AcceptConnectionThread abzuholen
     *
     * @return : der  aktuelle AcceptConnectionThread  .
     */
    BluetoothService.AcceptConnectionThread getmAcceptConnectionThread();

    /**
     * Methode , um den aktuellen ConnectionThread zu setzen .
     *
     * @param mStartConnectionThread : der  aktuelle ConnectionThread
     */
    void setmStartConnectionThread(BluetoothService.StartConnectionThread mStartConnectionThread);
    /**
     * Methode , um den aktuellen ConnectedThread zu setzen .
     *
     * @param mConnectedThread : der  aktuelle ConnectedThread  .
     */
    void setmConnectedThread(BluetoothService.ConnectedThread mConnectedThread);
    /**
     * Methode , um den aktuellen AcceptConnectionThread zu setzen .
     * @param mAcceptConnectionThread :der  aktuelle AcceptConnectionThread
     */
    void setmAcceptConnectionThread(BluetoothService.AcceptConnectionThread mAcceptConnectionThread);
    /**
     * Methode , um die Adresse des aktuellen verbundenen Geraet abzuholen
     * @return : die Adresse des aktuellen verbundenen Geraet  .
     */
    String getmCurrentDeviceAddress();
    /**
     * Methode , um die Adresse des aktuellen verbundenen Geraet zu setzen .
     * @param mCurrentDeviceAddress :die Adresse des aktuellen verbundenen Geraet
     */
    void setmCurrentDeviceAddress(String mCurrentDeviceAddress);


    /**
     * Methode , um den BluetoothAdapter zu setzen .
     *
     * @param mBluetoothAdmin :der BluetoothAdapter
     */

    void setmBluetoothAdmin(IBluetoothAdmin mBluetoothAdmin);

    /**
     * Methode , um den Zustand der gesendeten Brodcast abzuholen
     *
     * @return : der Zustand der gesendeten Brodcast
     */

    boolean isBrodcast();


}
