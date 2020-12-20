package com.example.blue2;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ScanForDeviceFragment extends DialogFragment {

    private static final int REQUEST_ACCESS_FINE_LOCATION = 2001;

    interface OnDeviceSelectedListener {
        void onDeviceSelected(BluetoothDevice bluetoothDevice);
    }
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothDevicesAdapter mAdapter;
    private final List<BluetoothDevice> mDiscoveredDevicesList = new ArrayList<>();
    private View mScanInprogressView;
    private OnDeviceSelectedListener mListener;
    private View mNoDevicesFoundView;

    public ScanForDeviceFragment(OnDeviceSelectedListener listener) {
        this.mListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragView = inflater.inflate(R.layout.fragment_scan_for_device, container, false);

        IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        requireActivity().registerReceiver(mReceiver, foundFilter);
        IntentFilter filterDicoveryFinished = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        requireActivity().registerReceiver(mReceiver, filterDicoveryFinished);
        IntentFilter filterDicoveryStarted = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        requireActivity().registerReceiver(mReceiver, filterDicoveryStarted);

        RecyclerView devicesRecyclerView = fragView.findViewById(R.id.rv_scanned);
        devicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new BluetoothDevicesAdapter(mDiscoveredDevicesList);
        devicesRecyclerView.setAdapter(mAdapter);
        devicesRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), this::onItemClick));
        mScanInprogressView = fragView.findViewById(R.id.ll_scan_inprogress);
        mNoDevicesFoundView = fragView.findViewById(R.id.rl_no_devices_found);
        fragView.findViewById(R.id.btn_try_again).setOnClickListener(v -> startDiscovery());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switch (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                case PackageManager.PERMISSION_DENIED:
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
                    break;
                case PackageManager.PERMISSION_GRANTED:
                    startDiscovery();
                    break;
            }
        } else {
            startDiscovery();
        }

        return fragView;
    }

    public void onResume() {
        super.onResume();

        Window window = getDialog().getWindow();
        Point size = new Point();

        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);

        int width = size.x;
        int height = size.y;

        window.setLayout((int) (width * 0.80), (int) (height * 0.6));
        window.setGravity(Gravity.CENTER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startDiscovery();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        requireActivity().unregisterReceiver(mReceiver);
        mBluetoothAdapter.cancelDiscovery();
    }

    private void startDiscovery() {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();
    }

    private void onItemClick(View view, int position) {
        mListener.onDeviceSelected(mDiscoveredDevicesList.get(position));
        dismiss();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null && device.getName() != null) {
                    boolean found = false;
                    for (BluetoothDevice d : mDiscoveredDevicesList) {
                        if (d.getAddress().equals(device.getAddress())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        mDiscoveredDevicesList.add(device);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mScanInprogressView.setVisibility(View.GONE);
                if (mDiscoveredDevicesList.size() == 0) {
                    mNoDevicesFoundView.setVisibility(View.VISIBLE);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                mScanInprogressView.setVisibility(View.VISIBLE);
                mNoDevicesFoundView.setVisibility(View.GONE);
            }
        }
    };
}
