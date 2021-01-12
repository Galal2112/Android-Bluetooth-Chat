package com.example.blue2.mvvm.viewmodel;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

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
    }

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
}
