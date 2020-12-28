package com.example.blue2;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

<<<<<<< Updated upstream
import com.example.blue2.network.BluetoothService;

import androidx.appcompat.app.AppCompatActivity;

public class OnBoardingActivity extends AppCompatActivity {
=======
import androidx.appcompat.app.AppCompatActivity;

import com.example.blue2.mvvm.view.ChatListActivity;

public class OnBoardingActivity extends AppCompatActivity implements View.OnClickListener {
>>>>>>> Stashed changes

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
<<<<<<< Updated upstream
            findViewById(R.id.btn_start_chat).setOnClickListener(this::onClick);
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_chat:
                Intent intent = new Intent(this, BluetoothService.class);
                intent.setAction(BluetoothService.ACTION_START);
                startService(intent);
=======
            findViewById(R.id.btn_start_chat).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_chat:
>>>>>>> Stashed changes

                startChat();
            default:
                break;
        }
    }

    private void startChat() {
<<<<<<< Updated upstream
        startActivity(new Intent(this, ChatListActivity.class));
=======
        Intent i = new Intent(this, ChatListActivity.class);
        startActivity(i);
>>>>>>> Stashed changes
    }
}