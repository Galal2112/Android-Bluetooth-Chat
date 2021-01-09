package com.example.blue2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.example.blue2.network.BluetoothService;
import com.example.blue2.network.NetworkInterface;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;



public class BluetoothServiceTest {


    NetworkInterface blueToothService;


    @Before
    public void Erzeugung() {


        blueToothService = new BluetoothService();

    }

    @Test
    public void startListenerThreadTest() {
        BluetoothService.AcceptConnectionThread thread = mock(BluetoothService.AcceptConnectionThread.class);
        blueToothService.setmAcceptConnectionThread(thread);

        BluetoothService.ConnectedThread thread1 = mock(BluetoothService.ConnectedThread.class);
        blueToothService.setmConnectedThread(thread1);

        BluetoothService.StartConnectionThread thread2 = mock(BluetoothService.StartConnectionThread.class);
        blueToothService.setmStartConnectionThread(thread2);

        blueToothService.startListenerThread();
        assertTrue(blueToothService.getmStartConnectionThread() == null);
        assertTrue(blueToothService.getmConnectedThread() == null);
        assertTrue(blueToothService.getmAcceptConnectionThread() != null);


    }

    @Test
    public void connectToDeviceTest() {

        BluetoothService.ConnectedThread thread1 = mock(BluetoothService.ConnectedThread.class);
        blueToothService.setmConnectedThread(thread1);

        BluetoothService.StartConnectionThread thread2 = mock(BluetoothService.StartConnectionThread.class);
        blueToothService.setmStartConnectionThread(thread2);


        BluetoothDevice bluetoothDevice = mock(BluetoothDevice.class);
        blueToothService.connectToDevice(bluetoothDevice);

        assertTrue(blueToothService.getmStartConnectionThread() != null);
        assertTrue(blueToothService.getmConnectedThread() == null);


    }

    @Test
    public void onDeviceConnectedTest() {
        BluetoothService.AcceptConnectionThread thread = mock(BluetoothService.AcceptConnectionThread.class);
        blueToothService.setmAcceptConnectionThread(thread);

        BluetoothService.ConnectedThread thread1 = mock(BluetoothService.ConnectedThread.class);
        blueToothService.setmConnectedThread(thread1);

        BluetoothService.StartConnectionThread thread2 = mock(BluetoothService.StartConnectionThread.class);
        blueToothService.setmStartConnectionThread(thread2);

        BluetoothSocket bluetoothSocket = mock(BluetoothSocket.class);
        BluetoothDevice bluetoothDevice = mock(BluetoothDevice.class);

        blueToothService.onDeviceConnected(bluetoothSocket, bluetoothDevice);
        assertTrue(blueToothService.getmStartConnectionThread() == null);
        assertTrue(blueToothService.getmConnectedThread() != null);
        assertTrue(blueToothService.getmAcceptConnectionThread() == null);

    }

    @Test
    public void sendDataTest() {

        blueToothService.setmConnectedThread(null);

        blueToothService.setmCurrentDeviceAddress("123456");

        BluetoothService.StartConnectionThread thread2 = mock(BluetoothService.StartConnectionThread.class);
        blueToothService.setmStartConnectionThread(thread2);

        BluetoothAdapter bluetoothAdapter = mock(BluetoothAdapter.class);


        blueToothService.setmCurrentDeviceAddress("123456");
        BluetoothDevice bluetoothDevice1 = bluetoothAdapter.getRemoteDevice(blueToothService.getmCurrentDeviceAddress());
        blueToothService.setmBluetoothAdapter(bluetoothAdapter);
        byte[] bytes = {1, 2, 3};

        blueToothService.sendData(bytes);

        assertTrue(blueToothService.getmStartConnectionThread() != null);


    }

    @Test
    public void sendBroadcast() {

        Map<String, String> extras = mock(Map.class);

        String action = "action";


        assertTrue(blueToothService.isBrodcast() == false);


        blueToothService.sendBroadcast(action, extras);


        assertTrue(blueToothService.isBrodcast() == true);


    }

    @Test
    public void onConnectionFailedTest() {

        BluetoothService.AcceptConnectionThread thread = mock(BluetoothService.AcceptConnectionThread.class);
        blueToothService.setmAcceptConnectionThread(thread);

        BluetoothService.ConnectedThread thread1 = mock(BluetoothService.ConnectedThread.class);
        blueToothService.setmConnectedThread(thread1);

        BluetoothService.StartConnectionThread thread2 = mock(BluetoothService.StartConnectionThread.class);
        blueToothService.setmStartConnectionThread(thread2);

        blueToothService.onConnectionFailed();


        assertTrue(blueToothService.getmStartConnectionThread() == null);
        assertTrue(blueToothService.getmConnectedThread() == null);
        assertTrue(blueToothService.getmAcceptConnectionThread() != null);

    }

    @Test
    public void onConnectionLostTest() {

        BluetoothService.AcceptConnectionThread thread = mock(BluetoothService.AcceptConnectionThread.class);
        blueToothService.setmAcceptConnectionThread(thread);

        BluetoothService.ConnectedThread thread1 = mock(BluetoothService.ConnectedThread.class);
        blueToothService.setmConnectedThread(thread1);

        BluetoothService.StartConnectionThread thread2 = mock(BluetoothService.StartConnectionThread.class);
        blueToothService.setmStartConnectionThread(thread2);

        blueToothService.onConnectionLost();


        assertTrue(blueToothService.getmStartConnectionThread() == null);
        assertTrue(blueToothService.getmConnectedThread() == null);
        assertTrue(blueToothService.getmAcceptConnectionThread() != null);

    }

    @Test
    public void cancelTest() {

        BluetoothService.AcceptConnectionThread thread = mock(BluetoothService.AcceptConnectionThread.class);
        blueToothService.setmAcceptConnectionThread(thread);

        BluetoothService.ConnectedThread thread1 = mock(BluetoothService.ConnectedThread.class);
        blueToothService.setmConnectedThread(thread1);

        BluetoothService.StartConnectionThread thread2 = mock(BluetoothService.StartConnectionThread.class);
        blueToothService.setmStartConnectionThread(thread2);

        blueToothService.cancel();
        assertTrue(blueToothService.getmStartConnectionThread() == null);
        assertTrue(blueToothService.getmConnectedThread() == null);
        assertTrue(blueToothService.getmAcceptConnectionThread() == null);


    }


}