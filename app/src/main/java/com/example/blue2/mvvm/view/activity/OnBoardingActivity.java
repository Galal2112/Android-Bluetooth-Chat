package com.example.blue2.mvvm.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.blue2.R;
import com.example.blue2.network.BluetoothAdmin;
import com.example.blue2.network.IBluetoothAdmin;

import androidx.appcompat.app.AppCompatActivity;

/**
 * welcome screen, Main Launcher, the first screen in the app.
 */
public class OnBoardingActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * @param savedInstanceState
     *
     * there are here 2 views
     * the first one to make sure if the device has a Bluetooth Adapter, if not, a view with error msg will be shown
     * the second one, if the usr have a bluetooth adapter, he can continue to click on start chat button.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);
        IBluetoothAdmin manager = BluetoothAdmin.sharedAdmin;
        if (!manager.isBluetoothAvailable()) {
            // no bluetooth, then show error view
            findViewById(R.id.ll_start_chat_view).setVisibility(View.GONE);
            findViewById(R.id.ll_error_view).setVisibility(View.VISIBLE);
        } else {
            // bluetooth adapter found, then show start chat view
            findViewById(R.id.ll_start_chat_view).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_error_view).setVisibility(View.GONE);
            findViewById(R.id.btn_start_chat).setOnClickListener(this);
        }
    }

    /**
     * usr can click on start chat button to start chat, he will go to the next
     * screen, chatList through the startChat() method.
     * @param v: clicked view
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_chat:
                startChat();
            default:
                break;
        }
    }

    /**
     * create an Intent to start a new Activity
     * usr can go to the next screen "ChatListActivity"
     */
    private void startChat() {
        Intent i = new Intent(this, ChatListActivity.class);
        startActivity(i);
    }
}