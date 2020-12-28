package com.example.blue2.mvvm.view;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blue2.DeviceListActivity;
import com.example.blue2.R;
import com.example.blue2.database.ConversationResult;
import com.example.blue2.mvvm.viewmodel.ChatListViewModel;
import com.example.blue2.network.BluetoothService;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {

    private View mEnableBluetoothView;
    private View mEmptyView;
    private Button mStartChatButton;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1001;
    private ChatListViewModel mViewModel;
    private ConversationAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        mViewModel = new ViewModelProvider(this).get(ChatListViewModel.class);

        mEnableBluetoothView = findViewById(R.id.ll_enable_bluetooth);
        mEmptyView = findViewById(R.id.ll_empty_chat_list);
        mStartChatButton = findViewById(R.id.btn_start_new_chat);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        enableChat(bluetoothAdapter.isEnabled());
        if(bluetoothAdapter.isEnabled()){
            startBluetoothService();
        }

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);

        bindActions();

        RecyclerView conversationsRecyclerView = findViewById(R.id.rv_conversations);
        conversationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ConversationAdapter(new ArrayList<>());
        conversationsRecyclerView.setAdapter(mAdapter);
        conversationsRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, this::onItemClick));
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
                   startBluetoothService();
                } else {
                    Toast.makeText(ChatListActivity.this, R.string.enable_bluetooth_error, Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_start_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_start_chat:
                if(BluetoothAdapter.getDefaultAdapter().isEnabled()){
                    startNewChat();
                    break;
                }else {
                    Toast.makeText(this, R.string.enable_bluetooth_error,Toast.LENGTH_LONG).show();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_turnon_bluetooth:
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
                break;
            case R.id.btn_start_new_chat:

                if (BluetoothAdapter.getDefaultAdapter().isEnabled()){
                    startNewChat();
                    break;
                } else {
                    Toast.makeText(this, R.string.enable_bluetooth_error,Toast.LENGTH_LONG).show();
                }

            default:
                break;
        }
    }

    private void bindActions() {
        findViewById(R.id.btn_turnon_bluetooth).setOnClickListener(this::onClick);
        findViewById(R.id.btn_start_new_chat).setOnClickListener(this::onClick);
    }

    private void enableChat(boolean enabled) {
        if (enabled) {
            mEnableBluetoothView.setVisibility(View.GONE);
            mStartChatButton.setVisibility(View.VISIBLE);
        } else {
            mEnableBluetoothView.setVisibility(View.VISIBLE);
            mStartChatButton.setVisibility(View.GONE);
        }
    }

    private void startNewChat() {
        startActivity(new Intent(this, DeviceListActivity.class));
    }

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

    private void onItemClick(View view, int position) {
        ConversationResult conversation = mAdapter.getConversations().get(position);
        BluetoothDevice bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(conversation.opponentAddress);
        if (bluetoothDevice != null) {
            ChatActivity.startActivity(this, bluetoothDevice);
        }
    }

    private void startBluetoothService (){
        Intent intent = new Intent(this, BluetoothService.class);
        intent.setAction(BluetoothService.ACTION_START);
        startService(intent);
    }

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