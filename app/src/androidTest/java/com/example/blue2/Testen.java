package com.example.blue2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;


import com.example.blue2.database.AppDatabase;
import com.example.blue2.database.Conversation;
import com.example.blue2.database.ConversationDao;
import com.example.blue2.database.ConversationDao_Impl;
import com.example.blue2.database.ConversationResult;
import com.example.blue2.database.Message;
import com.example.blue2.mvvm.viewmodel.ChatViewModel;
import com.example.blue2.mvvm.viewmodel.ChatViewModelInterface;
import com.example.blue2.network.BluetoothService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.configuration.AnnotationEngine;

import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
public class Testen {


    private AppDatabase appDatabase;
    private ConversationDao conversationDao;
    ChatViewModelInterface chatViewModel;
    private BluetoothAdapter mAdapter;
    private BluetoothDevice mTestDevice;


    @Before

    public void Erzeugen() {

        MockitoAnnotations.initMocks(this);
        Context context = ApplicationProvider.getApplicationContext();
        appDatabase = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        conversationDao = (ConversationDao_Impl) appDatabase.conversationDao();
        chatViewModel = new ChatViewModel(ApplicationProvider.getApplicationContext());
        chatViewModel.setmDao(conversationDao);

        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mTestDevice = mAdapter.getRemoteDevice("00:01:02:03:04:05");

    }

    @After
    public void closeDb() throws IOException {
        appDatabase.close();
    }

    @Test
    public void grundlegenderTest() throws Exception {


        assertTrue(chatViewModel.getmDao() == conversationDao);


    }

    @Test
    public void StartTest() {


        assertTrue(chatViewModel.getmConversation() == null);


        chatViewModel.start(mTestDevice);


        chatViewModel.createOrGetConversation();

        assertTrue(chatViewModel.getmConversation() != null);


    }

    @Test
    public void createOrGetConversationTest() {

        chatViewModel.start(mTestDevice);
        chatViewModel.setmDevice(mTestDevice);
        chatViewModel.setmConversation(null);

        assertTrue(chatViewModel.getmConversation() == null);

        chatViewModel.createOrGetConversation();

        assertTrue(chatViewModel.getmConversation() != null);


    }

    @Test
    public void AppDatabaseTest() {

        chatViewModel.start(mTestDevice);
        chatViewModel.setmDevice(mTestDevice);
        Message message = new Message();
        message.textBody = "Hello";
        message.parentConversationId = 10L;
        message.senderAddress = "123";
        message.messageId = 1;

        chatViewModel.createOrGetConversation();

        Conversation conversation = chatViewModel.getmConversation();

        assertNotEquals(chatViewModel.getmDao().getConversationMessages(chatViewModel.getmConversation().conversationId), null);


    }

    @Test
    public void InsertMessageTest() {

        Conversation conversation = new Conversation();
        conversation.conversationId = 10L;
        conversation.opponentAddress = "123";
        conversation.opponentName = "Amro";

        chatViewModel.setmDevice(mTestDevice);

        chatViewModel.start(mTestDevice);

        assertTrue(conversationDao.getConversation("123") == null);

        conversationDao.insert(conversation);

        assertTrue(conversationDao.getConversation("123") != null);


    }












  /*

   Conversation conversation = new Conversation();
        conversation.conversationId = 10L;
        conversation.opponentAddress = "123";
        conversation.opponentName = "Amro";


    Object[] messages =messageList.toArray();


        Object[] messages2 =  chatViewModel.getmDao().getConversationMessages(x).getValue().toArray();


        for (Object e : messages) {

            Message u = (Message) e;

            for (Object c : messages2) {

                Message q = (Message) c;

                if (u.textBody.equals(q.textBody)) {

                    y = true;


                }

            }

        }

*/

/*

    Conversation conversation = new Conversation();
    conversation.conversationId = 10L;
    conversation.opponentAddress = "123";
    conversation.opponentName = "Amro";

        chatViewModel.setmDevice(mTestDevice);

        chatViewModel.setmConversation(conversation);

    assertTrue(conversationDao.getConversation("123")==null);

        conversationDao.insert(conversation);

        conversationDao.getConversation("123");


    assertTrue(conversationDao.getConversation("123")!=null);
*/
}
