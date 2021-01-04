package com.example.blue2.mvvm.viewmodel;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;

import androidx.lifecycle.LiveData;

import com.example.blue2.database.AppDatabase;
import com.example.blue2.database.Conversation;
import com.example.blue2.database.ConversationDao;
import com.example.blue2.database.Message;

import java.util.List;

public interface ChatViewModelInterface {

    void start(BluetoothDevice bluetoothDevice);

    LiveData<List<Message>> getMessages();

    void insertMessage(String textBody, String sender);

    void createOrGetConversation();

    void sendMessage(String textBody);

    void setmDevice(BluetoothDevice mDevice);

    Conversation getmConversation();

    void setmConversation(Conversation mConversation);

    ConversationDao getmDao();

    void setmDao(ConversationDao mDao);


}
