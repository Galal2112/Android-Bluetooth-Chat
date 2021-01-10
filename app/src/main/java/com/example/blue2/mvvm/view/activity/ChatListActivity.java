package com.example.blue2.mvvm.view.activity;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.blue2.R;
import com.example.blue2.database.ConversationResult;
import com.example.blue2.mvvm.view.adapter.ConversationAdapter;
import com.example.blue2.mvvm.view.listener.RecyclerItemClickListener;
import com.example.blue2.mvvm.viewmodel.ChatListViewModel;
import com.example.blue2.network.BluetoothAdmin;
import com.example.blue2.network.BluetoothService;
import com.example.blue2.network.IBluetoothAdmin;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * **************************** Short overview ***************************
 * This is the second screen in BlueChatApp
 * it's a for chat history and if usr doesn't have a previous chats
 * he will be able to click on Start new Chat button.
 *
 * usr can't start chat, without having bluetooth connection On.
 * if the usr has a previous chat history it will be listed with the device name and the last massage
 * if the usr need to start a new conversation, he can click on new chat button from action bar.
 * if user clicked on a previous chat it will open and he can read the previous messages and
 * the device will try atomically to connect with the other device
 */
public class ChatListActivity extends AppCompatActivity implements IBluetoothAdmin.BluetoothStateObserver {

    private static final int REQUEST_ENABLE_BLUETOOTH = 1001;

    private View mEnableBluetoothView;
    private View mEmptyView;
    private Button mStartChatButton;
    private ChatListViewModel mViewModel;
    private ConversationAdapter mAdapter;
    private final IBluetoothAdmin mBluetoothAdmin = BluetoothAdmin.sharedAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        // create view model
        mViewModel = new ViewModelProvider(this).get(ChatListViewModel.class);

        // set the views from XML
        mEnableBluetoothView = findViewById(R.id.ll_enable_bluetooth);
        mEmptyView = findViewById(R.id.rl_empty_chat_list);
        mStartChatButton = findViewById(R.id.btn_start_new_chat);
        RecyclerView conversationsRecyclerView = findViewById(R.id.rv_conversations);
        conversationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ConversationAdapter(new ArrayList<>());
        conversationsRecyclerView.setAdapter(mAdapter);
        conversationsRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, this::onItemClick));

        // Start bluetooth service if bluetooth is enabled.
        if(mBluetoothAdmin.isEnabled()){
            startBluetoothService();
        }
        // if bluetooth is enabled, show chat view
        enableChat(mBluetoothAdmin.isEnabled());

        // observe Bluetooth state change, to check if the usr changed the state of bluetooth
        mBluetoothAdmin.observeBluetoothState(this);

        bindActions();
        observeConversations();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBluetoothAdmin.unRegisterBluetoothStateObserver(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BLUETOOTH:
                if (resultCode == Activity.RESULT_OK) {
                    // if the usr turned on the bluetooth, then start the bluetooth service
                   startBluetoothService();
                } else {
                    // if usr choose denied, he will see the toast that App needs Bluetooth to be ON
                    Toast.makeText(ChatListActivity.this, R.string.enable_bluetooth_error, Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // menu with one option, start chat
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_start_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_start_chat:
                // when usr click to start chat from menu, check if bluetooth is enabled
                if(mBluetoothAdmin.isEnabled()){
                    startNewChat();
                } else {
                    // show toast when bluetooth is not enabled
                    Toast.makeText(this, R.string.enable_bluetooth_error,Toast.LENGTH_LONG).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Click action of buttons
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_turnon_bluetooth:
                // Start enable Bluetooth and receive the result in onActivityResult
                mBluetoothAdmin.enableBluetooth(this, REQUEST_ENABLE_BLUETOOTH);
            case R.id.btn_start_new_chat:
                // In case user clicked start chat, he needs Bluetooth to be ON
                if (mBluetoothAdmin.isEnabled()){
                    startNewChat();
                } else {
                    Toast.makeText(this, R.string.enable_bluetooth_error,Toast.LENGTH_LONG).show();
                }
            default:
                break;
        }
    }

    // Set click action of buttons
    private void bindActions() {
        findViewById(R.id.btn_turnon_bluetooth).setOnClickListener(this::onClick);
        findViewById(R.id.btn_start_new_chat).setOnClickListener(this::onClick);
    }

    /**
     *
     * @param enabled I check if bluetooth is turned off or on.
     *
     * if the bluetooth is enabled the usr will able to see the start chat button, if there is no previous.
     * chat history.
     *
     * if the bluetooth is turned off, user will see a view with the msg bluetooth is turned of, Turn on
     *
     */
    private void enableChat(boolean enabled) {
        if (enabled) {
            mEnableBluetoothView.setVisibility(View.GONE);
            mStartChatButton.setVisibility(View.VISIBLE);
        } else {
            mEnableBluetoothView.setVisibility(View.VISIBLE);
            mStartChatButton.setVisibility(View.GONE);
        }
    }

    /**
     * start new chat by opening paired device list so the user can select a device to start chat with
     */
    private void startNewChat() {
        startActivity(new Intent(this, DeviceListActivity.class));
    }

    /**
     * observe the conversations live data
     * and it has anonymous inner class observer
     * onchange get the life conversations, and set the adapter
     * then notify the chang to refresh data in the Conversation adapter
     */
    private void observeConversations() {
        mViewModel.getConversations().observe(this, new Observer<List<ConversationResult>>() {
            @Override
            public void onChanged(List<ConversationResult> conversations) {
                mEmptyView.setVisibility(conversations.size() > 0 ? View.GONE : View.VISIBLE);
                mAdapter.setConversations(conversations);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    // Handle clicking one of the history conversations
    private void onItemClick(View view, int position) {
        ConversationResult conversation = mAdapter.getConversations().get(position);
        BluetoothDevice bluetoothDevice = mBluetoothAdmin.getRemoteDevice(conversation.opponentAddress);
        if (bluetoothDevice != null) {
            ChatActivity.startActivity(this, bluetoothDevice);
        }
    }

    // Start bluetooth service to start listening for connections
    private void startBluetoothService() {
        Intent intent = new Intent(this, BluetoothService.class);
        intent.setAction(BluetoothService.ACTION_START);
        startService(intent);
    }


    /**
     * Check the state bluetooth.
     * in case of state on, enable chat.
     * in case of state off, disable chat.
     */

    @Override
    public void onStateChanged(IBluetoothAdmin.BluetoothState state) {
        switch (state){
            case ON:
                enableChat(true);
                break;
            case OFF:
                enableChat(false);
                break;
        }

    }
}