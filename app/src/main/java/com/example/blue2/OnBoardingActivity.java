package com.example.blue2;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.blue2.mvvm.view.ChatListActivity;

public class OnBoardingActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            findViewById(R.id.ll_start_chat_view).setVisibility(View.GONE);
            findViewById(R.id.ll_error_view).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.ll_start_chat_view).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_error_view).setVisibility(View.GONE);
            findViewById(R.id.btn_start_chat).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_chat:

                startChat();
            default:
                break;
        }
    }

    private void startChat() {
        Intent i = new Intent(this, ChatListActivity.class);
        startActivity(i);
    }
}