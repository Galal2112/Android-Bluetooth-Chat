package com.example.blue2.mvvm.view.activity;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.blue2.R;
import com.example.blue2.database.Message;
import com.example.blue2.mvvm.view.adapter.MessagesAdapter;
import com.example.blue2.mvvm.viewmodel.ChatViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Chat screen in which the usr can send and receive messages.
 */
public class ChatActivity extends AppCompatActivity {
    private static final String EXTRA_BLUETOOTH_DEVICE = "EXTRA_BLUETOOTH_DEVICE";

    private EditText mChatMsgEditText;
    private MessagesAdapter mAdapter;
    private RecyclerView mMessagesRecyclerView;
    private ChatViewModel mViewModel;

    /**
     * Helper method that starts the Activity and adds the data inside the intent.
     * @param context from device list activity or chat list activity to open the device
     * @param bluetoothDevice the selected Bluetooth device to start chat with.
     */
    public static void startActivity(Context context, BluetoothDevice bluetoothDevice) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EXTRA_BLUETOOTH_DEVICE, bluetoothDevice);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // get the bluetooth device the user started with
        BluetoothDevice bluetoothDevice = getIntent().getParcelableExtra(EXTRA_BLUETOOTH_DEVICE);

        // create view model
        mViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        mViewModel.start(bluetoothDevice);

        // set the views from XML
        mChatMsgEditText = findViewById(R.id.edit_message);
        findViewById(R.id.btn_send).setOnClickListener(this::send);
        mMessagesRecyclerView = findViewById(R.id.rv_messages);
        mMessagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MessagesAdapter(new ArrayList<>());
        mMessagesRecyclerView.setAdapter(mAdapter);

        observerMessages();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.onDestroy();
    }

    private void send(View view) {
        String text = mChatMsgEditText.getText().toString();
        mViewModel.sendMessage(text);
        mChatMsgEditText.setText("");
    }

    private void observerMessages() {
        // Observe conversation live data to refresh the new on updates
        mViewModel.getMessages().observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                mAdapter.setMessages(messages);
                mAdapter.notifyDataSetChanged();
                mMessagesRecyclerView.smoothScrollToPosition(messages.size());
            }
        });
    }
}