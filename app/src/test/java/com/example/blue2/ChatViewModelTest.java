package com.example.blue2;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.test.core.app.ApplicationProvider;

import com.example.blue2.database.AppDatabase;
import com.example.blue2.database.AppDatabase_Impl;
import com.example.blue2.database.Conversation;
import com.example.blue2.database.ConversationDao;
import com.example.blue2.mvvm.view.ChatActivity;
import com.example.blue2.mvvm.view.ChatListActivity;
import com.example.blue2.mvvm.viewmodel.ChatViewModel;
import com.example.blue2.mvvm.viewmodel.ChatViewModelInterface;

import static android.content.Context.LAUNCHER_APPS_SERVICE;
import static android.content.Context.MODE_ENABLE_WRITE_AHEAD_LOGGING;
import static android.os.ParcelFileDescriptor.MODE_WORLD_WRITEABLE;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class ChatViewModelTest {


    //  ChatViewModelInterface chatViewModel = new ChatViewModel(new Application());


    ChatViewModelInterface chatViewModel;
    ChatActivity chatActivity;
    ChatListActivity chatListActivity;

    Application application;

    AndroidViewModel androidViewModel;
    BluetoothAdapter bluetoothAdapter;

    @Before
    public void Erzeugung() {
/*
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        application = new Application();
        chatViewModel = new ChatViewModel(application);
        chatViewModel.setmDao(new ConversationDao_Impl(new AppDatabase_Impl()));


        AndroidViewModel androidViewModel = (AndroidViewModel) chatViewModel;

             //  application = new Application();

        //  chatViewModel = new ChatViewModel(application);
*/
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    @Test
    public void insertMessageTest() {




        }













/*
    @Before
    public void Erzeugung() {




        AppDatabase appDatabase = new AppDatabase_Impl();


        appDatabase.setINSTANCE(new AppDatabase_Impl());
        application = new Application();

        chatViewModel = new ChatViewModel(application);


        application = new Application();
        chatViewModel = mock(ChatViewModel.class);
        when(chatViewModel.getmDao()).thenReturn(mock(ConversationDao.class));




    }

    @Test
    public void Test() {


        Conversation conversation = mock(Conversation.class);

        conversation.opponentName = "Amro";
        conversation.conversationId = 10L;
        conversation.opponentAddress = "123";

        BluetoothDevice bluetoothDevice = mock(BluetoothDevice.class);
        when(bluetoothDevice.getAddress()).thenReturn("123");

        chatViewModel.setmDevice(bluetoothDevice);

        when(chatViewModel.getmConversation()).thenReturn(conversation);
//        when(chatViewModel.getmDao().getConversation("123")).thenReturn(conversation);



        chatViewModel.setmDevice(bluetoothDevice);


        chatViewModel.createOrGetConversation();


        assertTrue(chatViewModel.getmDao() != null);
        assertTrue(chatViewModel.getmConversation() != null);



    }

    @Test
    public void insertMessageTest() {

        chatViewModel.insertMessage("hey", "Amro");

        assertTrue(chatViewModel.getmDao() != null);

    }
*/


    }

