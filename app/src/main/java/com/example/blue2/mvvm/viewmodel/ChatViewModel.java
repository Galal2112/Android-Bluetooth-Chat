package com.example.blue2.mvvm.viewmodel;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.blue2.database.AppDatabase;
import com.example.blue2.database.Conversation;
import com.example.blue2.database.ConversationDao;
import com.example.blue2.database.Message;
import com.example.blue2.network.BluetoothService;

import java.util.List;

public class ChatViewModel extends AndroidViewModel implements ChatViewModelInterface {
    private LiveData<List<Message>> mMessages;
    private BluetoothDevice mDevice;
    private Conversation mConversation;
    private ConversationDao mDao = AppDatabase.getDatabase(getApplication()).conversationDao();

    public ChatViewModel(@NonNull Application application) {
        super(application);
    }

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

    public LiveData<List<Message>> getMessages() {
        if (mMessages == null) {
            mMessages = mDao.getConversationMessages(mConversation.conversationId);
        }
        return mMessages;
    }

    public void onDestroy() {
        getApplication().unregisterReceiver(mReceiver);
        Intent intent = new Intent(getApplication(), BluetoothService.class);
        intent.setAction(BluetoothService.ACTION_CANCEL);
        getApplication().startService(intent);
    }

    public void sendMessage(String textBody) {
        if (textBody == null || textBody.isEmpty()) return;
        Intent intent = new Intent(getApplication(), BluetoothService.class);
        intent.setAction(BluetoothService.ACTION_SEND_MESSAGE);
        intent.putExtra(BluetoothService.EXTRA_MESSAGE, textBody);
        getApplication().startService(intent);
        insertMessage(textBody, null);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothService.ACTION_MESSAGE_RECEIVED)) {
                String text = intent.getStringExtra(BluetoothService.EXTRA_MESSAGE);
                insertMessage(text, mDevice.getAddress());
            }
        }
    };

    public void createOrGetConversation() {
        Conversation conversation = mDao.getConversation(mDevice.getAddress());
        if (conversation == null) {
            conversation = new Conversation();
            conversation.opponentName = mDevice.getName();
            conversation.opponentAddress = mDevice.getAddress();
            conversation.conversationId = mDao.insert(conversation);
        }
        this.mConversation = conversation;
    }

    public void insertMessage(String textBody, String sender) {
        Message message = new Message();
        message.parentConversationId = mConversation.conversationId;
        message.textBody = textBody;
        message.senderAddress = sender;
        mDao.insert(message);
    }

    @Override
    public void setmDevice(BluetoothDevice mDevice) {
        this.mDevice = mDevice;
    }

    @Override
    public Conversation getmConversation() {
        return mConversation;
    }

    @Override
    public void setmConversation(Conversation mConversation) {
        this.mConversation = mConversation;
    }

    @Override
    public ConversationDao getmDao() {
        return mDao;
    }

    @Override
    public void setmDao(ConversationDao mDao) {
        this.mDao = mDao;
    }
}
