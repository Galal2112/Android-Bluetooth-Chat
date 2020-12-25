package com.example.blue2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.blue2.database.Message;
import com.example.blue2.network.BluetoothService;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private static final String EXTRA_BLUETOOTH_DEVICE = "EXTRA_BLUETOOTH_DEVICE";

    private EditText mChatMsgEditText;
    private List<Message> mMessages = new ArrayList<>();
    private MessagesAdapter mAdapter;
    private RecyclerView mRevicesRecyclerView;

    public static void startActivity(Context context, BluetoothDevice bluetoothDevice) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EXTRA_BLUETOOTH_DEVICE, bluetoothDevice);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mChatMsgEditText = findViewById(R.id.edit_message);
        ImageButton sendButton = findViewById(R.id.btn_send);
        sendButton.setOnClickListener(this::send);
        BluetoothDevice bluetoothDevice = (BluetoothDevice) getIntent().getParcelableExtra(EXTRA_BLUETOOTH_DEVICE);
        Intent intent = new Intent(this, BluetoothService.class);
        intent.setAction(BluetoothService.ACTION_START);
        intent.putExtra(BluetoothService.EXTRA_BLUETOOTH_DEVICE, bluetoothDevice);
        startService(intent);

        mRevicesRecyclerView = findViewById(R.id.rv_messages);
        mRevicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MessagesAdapter(mMessages);
        mRevicesRecyclerView.setAdapter(mAdapter);

        IntentFilter receivedMsgFilter = new IntentFilter(BluetoothService.ACTION_MESSAGE_RECEIVED);
        registerReceiver(mReceiver, receivedMsgFilter);
        mRevicesRecyclerView.scrollToPosition(mMessages.size());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private void send(View view) {
        String text = mChatMsgEditText.getText().toString();
        if (text == null || text.isEmpty()) return;
        Intent intent = new Intent(this, BluetoothService.class);
        intent.setAction(BluetoothService.ACTION_SEND_MESSAGE);
        intent.putExtra(BluetoothService.EXTRA_MESSAGE, text);
        startService(intent);
        Message message = new Message();
        message.textBody = text;
        addMessage(message);
        mChatMsgEditText.setText("");
        mRevicesRecyclerView.smoothScrollToPosition(mMessages.size());
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothService.ACTION_MESSAGE_RECEIVED)) {
                String text = intent.getStringExtra(BluetoothService.EXTRA_MESSAGE);
                Message message = new Message();
                message.textBody = text;
                message.senderAddress = "SET ME";
                addMessage(message);
            }
        }
    };

    private void addMessage(Message message) {
        mMessages.add(message);
        mAdapter.notifyDataSetChanged();
        mRevicesRecyclerView.smoothScrollToPosition(mMessages.size());
    }
}