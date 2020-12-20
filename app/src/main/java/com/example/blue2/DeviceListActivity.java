package com.example.blue2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DeviceListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        List<BluetoothDevice> pairedDevicesList = new ArrayList<>();
        pairedDevicesList.addAll(pairedDevices);

        RecyclerView devicedRecyclerView = findViewById(R.id.rv_paired_devices);
        devicedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        devicedRecyclerView.setAdapter(new PairedDevicesAdapter(pairedDevicesList));

    }
}