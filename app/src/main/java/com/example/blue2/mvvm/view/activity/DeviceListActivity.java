package com.example.blue2.mvvm.view.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.example.blue2.R;
import com.example.blue2.mvvm.view.fragment.ScanForDeviceFragment;
import com.example.blue2.mvvm.view.adapter.BluetoothDevicesAdapter;
import com.example.blue2.mvvm.view.listener.RecyclerItemClickListener;
import com.example.blue2.mvvm.view.listener.ScanForDeviceCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Paired devices screen in which the usr can select a device to start chat with.
 */
public class DeviceListActivity extends AppCompatActivity implements ScanForDeviceCallback {

    private BluetoothDevicesAdapter mAdapter;
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final List<BluetoothDevice> mPairedDevicesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        // get the paired devices and add them in paired devices list
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        mPairedDevicesList.addAll(pairedDevices);

        // get the list from xml and show the devices in it
        RecyclerView devicesRecyclerView = findViewById(R.id.rv_paired_devices);
        devicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new BluetoothDevicesAdapter(mPairedDevicesList);
        devicesRecyclerView.setAdapter(mAdapter);
        // add click listener for the list to start chat
        devicesRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, this::onItemClick));

        // scan button to show the user near by devices and the user can pair with this devices
        findViewById(R.id.btn_scan_bluetooth).setOnClickListener(this::onClick);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // refresh the list every time the user come back to this screen
        refreshList();
    }

    // Get recent list of paired devices and refresh view to show it
    private void refreshList() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        mPairedDevicesList.clear();
        mPairedDevicesList.addAll(pairedDevices);
        mAdapter.notifyDataSetChanged();
    }

    // Click event for buttons
    private void onClick(View v) {
        switch (v.getId()) {
            // when scan button clicked, start scan
            case R.id.btn_scan_bluetooth:
                startScan();
            default:
                break;
        }
    }

    /**
     * first keep the usr screen ON until the scan is finished, because the user will find some problem if the screen go to sleep mode
     *
     * open the scan fragment
     */
    private void startScan() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ScanForDeviceFragment scanFragment = new ScanForDeviceFragment();
        scanFragment.show(getSupportFragmentManager(), "Scan for device");
    }

    // when the user click on the list, get the device and start chat
    private void onItemClick(View view, int position) {
        onDeviceSelected(mPairedDevicesList.get(position));
    }

    /**
     *  if the usr choose a device, then the keep screen on flag will be cleared.
     *  and open the chatActivity
     */
    @Override
    public void onDeviceSelected(BluetoothDevice bluetoothDevice) {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ChatActivity.startActivity(this, bluetoothDevice);
    }
}