package com.example.blue2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DeviceListActivity extends AppCompatActivity {

    private BluetoothDevicesAdapter mAdapter;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final List<BluetoothDevice> mPairedDevicesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        mPairedDevicesList.addAll(pairedDevices);

        RecyclerView devicesRecyclerView = findViewById(R.id.rv_paired_devices);
        devicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BluetoothDevicesAdapter(mPairedDevicesList);
        devicesRecyclerView.setAdapter(mAdapter);
        findViewById(R.id.btn_scan_bluetooth).setOnClickListener(this::onClick);
        devicesRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, this::onItemClick));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        mPairedDevicesList.clear();
        mPairedDevicesList.addAll(pairedDevices);
        mAdapter.notifyDataSetChanged();
    }

    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan_bluetooth:
                startScan();
            default:
                break;
        }
    }

    private void startScan() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ScanForDeviceFragment scanFragment = new ScanForDeviceFragment(this::onDeviceSelected);
        scanFragment.show(getSupportFragmentManager(), "Scan for device");
    }

    private void onItemClick(View view, int position) {
        onDeviceSelected(mPairedDevicesList.get(position));
    }
    private void onDeviceSelected(BluetoothDevice bluetoothDevice) {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ChatActivity.startActivity(this,bluetoothDevice);
    }
}