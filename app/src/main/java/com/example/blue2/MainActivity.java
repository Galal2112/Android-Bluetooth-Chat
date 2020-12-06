package com.example.blue2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int Request_connect_devices_sequre = 1;
    private static final int Request_enable_bluetooth = 3;

    private BluetoothAdapter bluetoothAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        if (bluetoothAdapter == null) {

            Toast.makeText(this, "Sie haben kein Bluetooth !!", Toast.LENGTH_SHORT).show();

            finish();

        }
    }


    @Override
    protected void onStart() {

        if (bluetoothAdapter.isEnabled()) {


            Intent enable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);


            startActivityForResult(enable, Request_enable_bluetooth);
        }


        super.onStart();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {

            case Request_enable_bluetooth:

                if (requestCode == Activity.RESULT_OK) {


                } else

                    Toast.makeText(this, "Bluetooth nicht an ", Toast.LENGTH_SHORT).show();


        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.blue_chat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {


            case R.id.secure_connect_scan: {


                Intent intent = new Intent(this, DeviceListActivity.class);

                startActivityForResult(intent, Request_connect_devices_sequre);

                return true;

            }


        }

        return false;

    }
}