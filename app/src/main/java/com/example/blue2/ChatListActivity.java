package com.example.blue2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
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

public class ChatListActivity extends AppCompatActivity {

    private View mEnableBluetoothView;
    private Button mStartChatButton;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        mEnableBluetoothView = findViewById(R.id.ll_enable_bluetooth);
        mStartChatButton = findViewById(R.id.btn_start_new_chat);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        enableChat(bluetoothAdapter.isEnabled());
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        bindActions();
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
                    ////
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
                startNewChat();
                break;
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
                startNewChat();
                break;
            default:
                break;
        }
    }

    private void bindActions() {
        findViewById(R.id.btn_turnon_bluetooth).setOnClickListener(this::onClick);
        findViewById(R.id.btn_start_new_chat).setOnClickListener(this::onClick);
    }

    private void enableChat(boolean enable) {
        if (enable) {
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