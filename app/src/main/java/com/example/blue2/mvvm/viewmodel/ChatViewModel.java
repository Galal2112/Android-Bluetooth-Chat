package com.example.blue2.mvvm.viewmodel;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.example.blue2.database.AppDatabase;
import com.example.blue2.database.Conversation;
import com.example.blue2.database.ConversationDao;
import com.example.blue2.database.Message;
import com.example.blue2.network.BluetoothService;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class ChatViewModel extends AndroidViewModel {
    // Live data for current conversation messages
    private LiveData<List<Message>> mMessages;
    // Bluetooth device that the user opened chat with
    private BluetoothDevice mDevice;
    // Current conversation from database
    private Conversation mConversation;
    private ConversationDao mDao = AppDatabase.getDatabase(getApplication()).conversationDao();

    public ChatViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * start bluetooth service
     * @param bluetoothDevice the bluetooth device from StartChatActivity
     *
     *
     */
    public void start(BluetoothDevice bluetoothDevice) {
        this.mDevice = bluetoothDevice;
        Intent intent = new Intent(getApplication(), BluetoothService.class);
        intent.setAction(BluetoothService.ACTION_START);
        intent.putExtra(BluetoothService.EXTRA_BLUETOOTH_DEVICE, bluetoothDevice);
        getApplication().startService(intent);
        IntentFilter receivedMsgFilter = new IntentFilter(BluetoothService.ACTION_MESSAGE_RECEIVED);
        getApplication().registerReceiver(mReceiver, receivedMsgFilter);
        createOrGetConversation();
    }

    // Get the live data object so the screen can observe the changes and update ui
    public LiveData<List<Message>> getMessages() {
        if (mMessages == null) {
            // get messages using current conversation id
            mMessages = mDao.getConversationMessages(mConversation.conversationId);
        }
        return mMessages;
    }

    // when the screen us destroyed notify the bluetooth service
    public void onDestroy() {
        getApplication().unregisterReceiver(mReceiver);
        Intent intent = new Intent(getApplication(), BluetoothService.class);
        intent.setAction(BluetoothService.ACTION_CANCEL);
        getApplication().startService(intent);
    }

    /**
     * this method creates an intent to send message to the service
     * put the extra message and than start service
     * @param textBody message body
     */

    public void sendMessage(String textBody) {
        if (textBody == null || textBody.isEmpty()) return;
        Intent intent = new Intent(getApplication(), BluetoothService.class);
        intent.setAction(BluetoothService.ACTION_SEND_MESSAGE);
        intent.putExtra(BluetoothService.EXTRA_MESSAGE, textBody);
        getApplication().startService(intent);
        insertMessage(textBody, null);
    }

    // Receiver to handle received messages from the other user
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        /**
         * if the message action received, get the massage from extra and insert this message
         * @param context
         * @param intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothService.ACTION_MESSAGE_RECEIVED)) {
                String text = intent.getStringExtra(BluetoothService.EXTRA_MESSAGE);
                insertMessage(text, mDevice.getAddress());
            }
        }
    };

    /**
     * to get the conversation from Database by the device address
     * if the conversation doesn't exist, then create a new conversation
     * and insert it in the database
     */
    private void createOrGetConversation() {
        Conversation conversation = mDao.getConversation(mDevice.getAddress());
        if (conversation == null) {
            conversation = new Conversation();
            conversation.opponentName = mDevice.getName();
            conversation.opponentAddress = mDevice.getAddress();
            conversation.conversationId = mDao.insert(conversation);
        }
        this.mConversation = conversation;
    }

    /**
     * insert the message in the database, live data of the messages list will update automatically
     */
    private void insertMessage(String textBody, String sender) {
        Message message = new Message();
        message.parentConversationId = mConversation.conversationId;
        message.textBody = textBody;
        message.senderAddress = sender;
        mDao.insert(message);
    }
}
