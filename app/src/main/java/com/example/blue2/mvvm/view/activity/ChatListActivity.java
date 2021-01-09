package com.example.blue2.mvvm.view.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.example.blue2.network.BluetoothService;

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
public class ChatListActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BLUETOOTH = 1001;

    private View mEnableBluetoothView;
    private View mEmptyView;
    private Button mStartChatButton;
    private ChatListViewModel mViewModel;
    private ConversationAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        // create view model
        mViewModel = new ViewModelProvider(this).get(ChatListViewModel.class);

        // set the views from XML
        mEnableBluetoothView = findViewById(R.id.ll_enable_bluetooth);
        mEmptyView = findViewById(R.id.ll_empty_chat_list);
        mStartChatButton = findViewById(R.id.btn_start_new_chat);
        RecyclerView conversationsRecyclerView = findViewById(R.id.rv_conversations);
        conversationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ConversationAdapter(new ArrayList<>());
        conversationsRecyclerView.setAdapter(mAdapter);
        conversationsRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, this::onItemClick));

        // Start bluetooth service if bluetooth is enabled.
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter.isEnabled()){
            startBluetoothService();
        }
        // if bluetooth is enabled, show chat view
        enableChat(bluetoothAdapter.isEnabled());

        // Intent filter, to check if the usr changed the state of bluetooth
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);

        bindActions();
        observeConversations();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
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
                if(BluetoothAdapter.getDefaultAdapter().isEnabled()){
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
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
                break;
            case R.id.btn_start_new_chat:
                // In case user clicked start chat, he needs Bluetooth to be ON
                if (BluetoothAdapter.getDefaultAdapter().isEnabled()){
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
        BluetoothDevice bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(conversation.opponentAddress);
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
     * Check the action of the received intent
     * in case of state changed, get the state from extra
     *
     * and check the bluetooth adapter state if the bluetooth is turned off then enable chat will be false
     *  if the bluetooth is turned on, then enable chat will be true.
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        enableChat(false);
                        break;
                    case BluetoothAdapter.STATE_ON:
                    case BluetoothAdapter.STATE_TURNING_ON:
                        enableChat(true);
                        break;
                }
            }
        }
    };

}