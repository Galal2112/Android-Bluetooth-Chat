package com.example.blue2;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.example.blue2.database.AppDatabase;
import com.example.blue2.database.AppDatabase_Impl;
import com.example.blue2.database.Conversation;
import com.example.blue2.database.ConversationDao;
import com.example.blue2.database.ConversationDao_Impl;
import com.example.blue2.database.Message;
import com.example.blue2.mvvm.view.ChatListActivity;
import com.example.blue2.mvvm.viewmodel.ChatViewModel;
import com.example.blue2.mvvm.viewmodel.ChatViewModelInterface;
import com.example.blue2.network.BluetoothService;
import com.example.blue2.network.NetworkInterface;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


public class ChatViewModelTest {


    ChatViewModelInterface chatViewModel;


    Application application;

    //Context context = mock(Conversation.class);


    @Before
    public void Erzeugung() {


        application = new Application();

        chatViewModel = new ChatViewModel(application);


    }

    @Test
    public void start() {

        AppDatabase db = mock(AppDatabase.class);


        //   ConversationDao conversationDao = new ConversationDao_Impl(db);


        Conversation conversation = new Conversation();

        conversation.opponentAddress = "";
        conversation.opponentName = "Amro";

        ConversationDao conversationDao = mock(ConversationDao_Impl.class);

        when(conversationDao.getConversation("")).thenReturn(null);
        when(conversationDao.insert(conversation)).thenReturn(10L);

        chatViewModel.setmDao(conversationDao);
        when(db.conversationDao()).thenReturn(conversationDao);
        BluetoothDevice bluetoothDevice = mock(BluetoothDevice.class);
        when(bluetoothDevice.getAddress()).thenReturn("");
        when(bluetoothDevice.getName()).thenReturn("Amro");
        chatViewModel.setmDevice(bluetoothDevice);

        //  chatViewModel.insertMessage("hello", "amro");

        //   Conversation conversation = chatViewModel.getmDao().getConversation(bluetoothDevice.getAddress());

        chatViewModel.start(bluetoothDevice);

        assertTrue(chatViewModel.getmConversation() != null);
        assertTrue(conversation.conversationId != 10L);


    }


    @Test
    public void createOrGetConversation() {

        AppDatabase db = new AppDatabase_Impl();

        ConversationDao conversationDao = new ConversationDao_Impl(db);

        chatViewModel.setmDao(conversationDao);

        BluetoothDevice bluetoothDevice = mock(BluetoothDevice.class);

        when(bluetoothDevice.getAddress()).thenReturn("123");

        Conversation conversation = chatViewModel.getmDao().getConversation(bluetoothDevice.getAddress());

        chatViewModel.insertMessage("hello", "amro");

        chatViewModel.setmConversation(conversation);

        chatViewModel.setmMessages(null);

        chatViewModel.createOrGetConversation();

        assertTrue(chatViewModel.getmConversation() != null);

    }

}
