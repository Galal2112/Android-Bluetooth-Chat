package com.example.blue2.network;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.blue2.database.AppDatabase;
import com.example.blue2.database.Conversation;
import com.example.blue2.database.ConversationDao;
import com.example.blue2.database.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BluetoothService extends Service implements NetworkInterface {

    // Available actions that the service handle
    public static final String ACTION_SEND_MESSAGE = "com.example.blue2.action.SEND_MESSAGE";
    public static final String ACTION_START = "com.example.blue2.action.START";
    public static final String ACTION_CANCEL = "com.example.blue2.action.CANCEL";

    // Actions that the service send as broadcast
    public static final String ACTION_MESSAGE_SENT = "com.example.blue2.action.MESSAGE_SENT";
    public static final String ACTION_MESSAGE_RECEIVED = "com.example.blue2.action.MESSAGE_RECEIVED";
    public static final String ACTION_MESSAGE_SEND_FAILED = "com.example.blue2.action.MESSAGE_SEND_FAILED";
    public static final String ACTION_CONNECTION_SUCCESS = "com.example.blue2.action.CONNECTION_SUCCESS";
    public static final String ACTION_CONNECTION_FAILURE = "com.example.blue2.action.CONNECTION_FAILURE";
    public static final String ACTION_CONNECTION_LOST = "com.example.blue2.action.CONNECTION_LOST";
    public static final String ACTION_DEVICE_NAME = "com.example.blue2.action.DEVICE_NAME";

    // Constants used for sending/receiving data in/from intents
    public static final String EXTRA_BLUETOOTH_DEVICE = "com.example.blue2.extra.BLUETOOTH_DEVICE";
    public static final String EXTRA_MESSAGE = "com.example.blue2.extra.EXTRA_MESSAGE";
    public static final String EXTRA_DEVICE_NAME = "com.example.blue2.extra.DEVICE_NAME";
    public static final String EXTRA_DEVICE_ADDRESS = "com.example.blue2.extra.DEVICE_ADDRESS";

    private boolean brodcast = false;

    // connection port one to one.
    private static final UUID MY_UUID_SECURE = UUID.fromString("b9d04dfb-3da4-4622-b253-63f8ace49d9a");

    // states of connection
    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    private static final String BLUE_CHAT_APP = "BluetoothService";

    /**
     * the main 3 Threads in the App
     * StartConnectionThread: start thread to pair device
     * ConnectedThread: connected thread to write and read from socket
     * AcceptConnectionThread: Accept thread waiting for connection
     */
    private StartConnectionThread mStartConnectionThread;
    private ConnectedThread mConnectedThread;
    private AcceptConnectionThread mAcceptConnectionThread;

    // Bluetooth adapter
    private IBluetoothAdmin mBluetoothAdmin;
    // Current device the user chats with
    private String mCurrentDeviceAddress = "";

    // Current state of connection
    private static int mState = STATE_NONE;

    /**
     * To run actions on main thread from background thread, in this case the only action is showing a toast
     */
    private final Handler mUIHanlder = new Handler(Looper.getMainLooper());

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private ConversationDao mDao;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(ACTION_START)) {
            mDao = AppDatabase.getDatabase(getApplication()).conversationDao();
            // on start action, get the device from intent
            BluetoothDevice bluetoothDevice = intent.getParcelableExtra(EXTRA_BLUETOOTH_DEVICE);
            // if there is no device or the device is not the current device in the service then start connection from beginning
            if (bluetoothDevice == null || mState != STATE_CONNECTED || !bluetoothDevice.getAddress().equals(mCurrentDeviceAddress)) {
                mBluetoothAdmin = BluetoothAdmin.sharedAdmin;
                mState = STATE_NONE;
                if (bluetoothDevice == null) {
                    startListenerThread();
                } else {
                    connectToDevice(bluetoothDevice);
                }
                if (bluetoothDevice == null) {
                    mCurrentDeviceAddress = "";
                } else {
                    mCurrentDeviceAddress = bluetoothDevice.getAddress();
                }
            }
        } else if (intent.getAction().equals(ACTION_SEND_MESSAGE)) {
            // On send message action, write the message to the socket
            String message = intent.getStringExtra(EXTRA_MESSAGE);
            insertMessage(message, null);
            sendData(message.getBytes());
        } else if (intent.getAction().equals(ACTION_CANCEL)) {
            // Close connection when user closes chat screen
           cancel();
        }
        return START_STICKY;
    }

    @Override
    public boolean stopService(Intent name) {
        cancel();
        Log.d("Galal Ahmed", "Service stopped");
        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        cancel();
        Log.d("Galal Ahmed", "Service destroyed");
        super.onDestroy();
    }

    // cancel connection if any and start the thread that accepts connections
    public synchronized void startListenerThread() {

        if (mStartConnectionThread != null) {
            mStartConnectionThread.cancel();
            mStartConnectionThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mAcceptConnectionThread == null) {
            mAcceptConnectionThread = new AcceptConnectionThread();
            mAcceptConnectionThread.start();
        }
    }

    // cancel current connection if any and try connecting to the provided device
    public synchronized void connectToDevice(BluetoothDevice bluetoothDevice) {

        if (mState == STATE_CONNECTING) {
            if (mStartConnectionThread != null) {
                mStartConnectionThread.cancel();
                mStartConnectionThread = null;
            }
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        mStartConnectionThread = new StartConnectionThread(bluetoothDevice);
        mStartConnectionThread.start();
    }

    // When receiving a connection from a device, open a socket with this device to start sending and receiving data
    public synchronized void onDeviceConnected(BluetoothSocket socket, BluetoothDevice device) {

        if (mStartConnectionThread != null) {
            mStartConnectionThread.cancel();
            mStartConnectionThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mAcceptConnectionThread != null) {
            mAcceptConnectionThread.cancel();
            mAcceptConnectionThread = null;
        }

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        Map<String, String> extras = new HashMap<>();
        extras.put(EXTRA_DEVICE_NAME, device.getName());
        extras.put(EXTRA_DEVICE_ADDRESS, device.getAddress());
        sendBroadcast(ACTION_DEVICE_NAME, extras);
        mCurrentDeviceAddress = device.getAddress();
    }

    // Cancel all threads
    public synchronized void cancel() {
        mState = STATE_NONE;

        if (mStartConnectionThread != null) {
            mStartConnectionThread.cancel();
            mStartConnectionThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mAcceptConnectionThread != null) {
            mAcceptConnectionThread.cancel();
            mAcceptConnectionThread = null;
        }
        mCurrentDeviceAddress = "";
        stopSelf();
    }

    // Send data in the current socket connected
    public void sendData(byte[] data) {
        if (mConnectedThread != null) {
            ConnectedThread r;
            synchronized (this) {
                if (mState != STATE_CONNECTED) return;
                r = mConnectedThread;
            }
            r.write(data);
        } else {
            if (mCurrentDeviceAddress != null) {
                BluetoothDevice bluetoothDevice = mBluetoothAdmin.getRemoteDevice(mCurrentDeviceAddress);
                if (bluetoothDevice != null) {
                    connectToDevice(bluetoothDevice);
                }
            }

            Log.d("Galal Ahmed", "send Data Error");
        }

    }

    // When connection fails, try to open the connection again
    public void onConnectionFailed() {
        sendBroadcast(ACTION_CONNECTION_FAILURE, null);
        mState = STATE_NONE;
        startListenerThread();
    }

    // When connection is lost, try to open the connection again
    public void onConnectionLost() {
        sendBroadcast(ACTION_CONNECTION_LOST, null);
        mState = STATE_NONE;
        startListenerThread();
    }

    // utility method to send broadcast with action and data
    public void sendBroadcast(String action, Map<String, String> extras) {
        Intent intent = new Intent(action);
        if (extras != null && !extras.isEmpty()) {
            for (String key : extras.keySet()) {
                intent.putExtra(key, extras.get(key));
            }
        }
        sendBroadcast(intent);
        brodcast = true;
    }

    @Override
    public void setmBluetoothAdmin(IBluetoothAdmin mBluetoothAdapter) {
        this.mBluetoothAdmin = mBluetoothAdapter;
    }

    @Override
    public boolean isBrodcast() {
        return brodcast;
    }

    @Override
    public StartConnectionThread getmStartConnectionThread() {
        return mStartConnectionThread;
    }

    @Override
    public void setmStartConnectionThread(StartConnectionThread mStartConnectionThread) {
        this.mStartConnectionThread = mStartConnectionThread;
    }

    @Override
    public ConnectedThread getmConnectedThread() {
        return mConnectedThread;
    }

    @Override
    public void setmConnectedThread(ConnectedThread mConnectedThread) {
        this.mConnectedThread = mConnectedThread;
    }

    @Override
    public AcceptConnectionThread getmAcceptConnectionThread() {
        return mAcceptConnectionThread;
    }

    @Override
    public void setmAcceptConnectionThread(AcceptConnectionThread mAcceptConnectionThread) {
        this.mAcceptConnectionThread = mAcceptConnectionThread;
    }

    @Override
    public String getmCurrentDeviceAddress() {
        return mCurrentDeviceAddress;
    }

    @Override
    public void setmCurrentDeviceAddress(String mCurrentDeviceAddress) {
        this.mCurrentDeviceAddress = mCurrentDeviceAddress;
    }

    /**
     * insert the message in the database, live data of the messages list will update automatically
     */
    private void insertMessage(String textBody, String sender) {
        if (mCurrentDeviceAddress == null || mCurrentDeviceAddress.isEmpty()) {
           return;
        }
        Conversation conversation = createOrGetConversation();
        Message message = new Message();
        message.parentConversationId = conversation.conversationId;
        message.textBody = textBody;
        message.senderAddress = sender;
        mDao.insert(message);
    }

    private Conversation createOrGetConversation() {
        Conversation conversation = mDao.getConversation(mCurrentDeviceAddress);
        BluetoothDevice device = mBluetoothAdmin.getRemoteDevice(mCurrentDeviceAddress);
        if (conversation == null) {
            conversation = new Conversation();
            conversation.opponentName = device.getName();
            conversation.opponentAddress = device.getAddress();
            conversation.conversationId = mDao.insert(conversation);
        }
        return conversation;
    }

     // Thread to pair device
    public class StartConnectionThread extends Thread {

        private final BluetoothSocket mSocket;
        private final BluetoothDevice mDevice;

        public StartConnectionThread(BluetoothDevice device) {
            mDevice = device;
            BluetoothSocket tmp = null;

            // get socket with the device
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
            } catch (IOException e) {
                Log.e("Galal Ahmed", "Socket create() failed", e);
            }
            mSocket = tmp;
            mState = STATE_CONNECTING;
        }

        public void run() {
            try {
                // try connecting with the device
                mSocket.connect();
                // notify of connection success
                sendBroadcast(ACTION_CONNECTION_SUCCESS, null);
                Log.d("Galal Ahmed", "Connected");
            } catch (IOException e) {
                mUIHanlder.post(() -> Toast.makeText(getApplicationContext(), "Connecting error " + e.getMessage(), Toast.LENGTH_LONG).show());
                try {
                    // in case of error close the socket
                    mSocket.close();
                } catch (IOException e2) {
                    Log.e("Galal Ahmed", "unable to close() socket during connection failure", e2);
                }
                onConnectionFailed();
                return;
            }

            synchronized (BluetoothService.this) {
                mStartConnectionThread = null;
            }

            mUIHanlder.post(() -> Toast.makeText(getApplicationContext(), "Connecting...", Toast.LENGTH_LONG).show());

            // do the next step by starting the read and write thread
            onDeviceConnected(mSocket, mDevice);
        }

        // close the socket on cancel
        public void cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e("CONNECTTHREAD", "Could not close connection:" + e.toString(), e);
            }
        }
    }

     // connected thread to write and read from socket
    public class ConnectedThread extends Thread {
        private BluetoothSocket mSocket;
        private InputStream mInStream;
        private OutputStream mOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d("Galal Ahmed", "create ConnectedThread");
            mSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // get input and output streams from the socket
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e("Galal Ahmed", "temp sockets not created", e);
                mUIHanlder.post(() -> Toast.makeText(getApplicationContext(), "Connect error :(", Toast.LENGTH_LONG).show());
                return;
            }

            mInStream = tmpIn;
            mOutStream = tmpOut;
            mState = STATE_CONNECTED;
            mUIHanlder.post(() -> Toast.makeText(getApplicationContext(), "Connected :)))", Toast.LENGTH_LONG).show());
        }

        public void run() {
            Log.d("Galal Ahmed", "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            while (mState == STATE_CONNECTED) {
                try {
                    // read received messages from the socket
                    bytes = mInStream.read(buffer);
                    String str = new String(buffer, 0, bytes);
                    mUIHanlder.post(() -> Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show());

                    Map<String, String> extras = new HashMap<>();
                    extras.put(EXTRA_MESSAGE, str);
                    sendBroadcast(ACTION_MESSAGE_RECEIVED, extras);
                    insertMessage(str, mCurrentDeviceAddress);
                } catch (IOException e) {
                    Log.e("Galal Ahmed", "disconnected", e);
                    onConnectionLost();
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                // write sent message in the socket
                mOutStream.write(buffer);
                mUIHanlder.post(() -> Toast.makeText(getApplicationContext(), "Sent", Toast.LENGTH_LONG).show());
                sendBroadcast(ACTION_MESSAGE_SENT, null);
            } catch (IOException e) {
                sendBroadcast(ACTION_MESSAGE_SEND_FAILED, null);
                Log.e("Galal Ahmed", "Exception during write", e);
            }
        }

        // on cancel, close the socket
        public void cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e("Galal Ahmed", "close() of connect socket failed", e);
            }
        }
    }

    // Accept thread waiting for connection
    public class AcceptConnectionThread extends Thread {
        private final BluetoothServerSocket mServerSocket;

        public AcceptConnectionThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = mBluetoothAdmin.listenUsingRfcommWithServiceRecord(BLUE_CHAT_APP,
                        MY_UUID_SECURE);
            } catch (IOException e) {
                Log.e("Galal Ahmed", "Error listen() failed", e);
            }
            mServerSocket = tmp;
            mState = STATE_LISTEN;
        }

        public void run() {

            BluetoothSocket socket = null;

            while (mState != STATE_CONNECTED) {
                try {
                    socket = mServerSocket.accept();
                } catch (Exception e) {
                    Log.e("Galal Ahmed", "Error accept() failed", e);
                    break;
                }

                if (socket != null) {
                    synchronized (BluetoothService.this) {
                        switch (mState) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                onDeviceConnected(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e("Galal Ahmed", "Could not close unwanted socket", e);
                                }
                                break;
                        }
                    }
                }
            }
            Log.d("Galal Ahmed", "END mAcceptThread");
        }

        // on cancel, close the socket
        public void cancel() {
            try {
                mServerSocket.close();
            } catch (IOException e) {
                Log.e("Galal Ahmed", "close() of server failed", e);
            }
        }
    }
}