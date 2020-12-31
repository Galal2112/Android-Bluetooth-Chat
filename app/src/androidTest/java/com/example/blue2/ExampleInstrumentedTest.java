package com.example.blue2;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.res.Configuration;
import android.service.autofill.FieldClassification;

import androidx.lifecycle.AndroidViewModel;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.blue2.database.AppDatabase_Impl;
import com.example.blue2.database.Conversation;
import com.example.blue2.database.ConversationDao_Impl;
import com.example.blue2.mvvm.view.ChatActivity;
import com.example.blue2.mvvm.view.ChatListActivity;
import com.example.blue2.mvvm.viewmodel.ChatListViewModel;
import com.example.blue2.mvvm.viewmodel.ChatViewModel;
import com.example.blue2.mvvm.viewmodel.ChatViewModelInterface;
import com.example.blue2.network.BluetoothService;
import com.example.blue2.network.NetworkInterface;

import org.hamcrest.integration.JMock1Adapter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.blue2", appContext.getPackageName());
    }


    ChatViewModelInterface chatViewModel;
    ChatActivity chatActivity;
    ChatListActivity chatListActivity;

    Application application;

    AndroidViewModel androidViewModel;


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


        //   chatActivity = new ChatActivity();
        //   chatViewModel = chatActivity.getmViewModel();
        //   chatListActivity = new ChatListActivity();

        NetworkInterface networkInterface = new BluetoothService();

    }

    @Test
    public void insertMessageTest() {

        Conversation conversation = new Conversation();
        conversation.conversationId = 10L;
        conversation.opponentAddress = "123";
        conversation.opponentName = "Amro";


        chatViewModel.insertMessage("Hello","Amro");
        assertTrue(chatViewModel.getmDao() != null);


    }
    /*
    @Test
    public void testUser() {
        UserViewModel model = new UserViewModel(RuntimeEnvironment.application);
        // how do I test that startGetUserService() is sending
        // the Intent to MyIntentService and check the extras?
    }

 */

}