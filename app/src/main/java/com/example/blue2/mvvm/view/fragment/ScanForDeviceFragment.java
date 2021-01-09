package com.example.blue2.mvvm.view.fragment;

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
import android.widget.Toast;

import com.example.blue2.R;
import com.example.blue2.mvvm.view.adapter.BluetoothDevicesAdapter;
import com.example.blue2.mvvm.view.listener.RecyclerItemClickListener;
import com.example.blue2.mvvm.view.listener.ScanForDeviceCallback;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * this fragment is for start bluetooth discovery.
 */
public class ScanForDeviceFragment extends DialogFragment {

    // request code for access location permission required for bluetooth discovery
    private static final int REQUEST_ACCESS_FINE_LOCATION = 2001;

    private BluetoothDevicesAdapter mAdapter;
    // adapter for the discovered devices recycler view
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    // List of discovered devices during the scan
    private final List<BluetoothDevice> mDiscoveredDevicesList = new ArrayList<>();

    private View mScanInprogressView;
    private View mNoDevicesFoundView;

    // Callback to notify device selected
    private ScanForDeviceCallback mCallback;

    // when fragment is attached, check if its parent activity confirms to scan for device callback
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ScanForDeviceCallback) {
            mCallback = (ScanForDeviceCallback) context;
        }
    }

    /**
     * 3 Intent filters to get the results of the discovery in the BroadcastReceiver
     * if a device was found or the discover was finished, or discovery was started
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter filterDicoveryFinished = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        IntentFilter filterDicoveryStarted = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);

        requireActivity().registerReceiver(mReceiver, foundFilter);
        requireActivity().registerReceiver(mReceiver, filterDicoveryFinished);
        requireActivity().registerReceiver(mReceiver, filterDicoveryStarted);
    }

    /**
     * get the xml, that has list of bluetooth devices and the scan progress bar
     * The xml also contains a view if no devices were found.
     * Here also the code tries to get the required permission
     * a request is needed for the location permission starting from api 23
     * if the usr uses am api version lower than api 23, then it doesn't need to ask for this permission.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return the view from xml
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragView = inflater.inflate(R.layout.fragment_scan_for_device, container, false);

        // get the list and set adapter
        RecyclerView devicesRecyclerView = fragView.findViewById(R.id.rv_scanned);
        devicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new BluetoothDevicesAdapter(mDiscoveredDevicesList);
        devicesRecyclerView.setAdapter(mAdapter);
        devicesRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), this::onItemClick));

        mScanInprogressView = fragView.findViewById(R.id.ll_scan_inprogress);
        mNoDevicesFoundView = fragView.findViewById(R.id.rl_no_devices_found);

        fragView.findViewById(R.id.btn_try_again).setOnClickListener(v -> startDiscovery());

        // request the permission if api level greater than or equal 23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switch (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                case PackageManager.PERMISSION_DENIED:
                    // request the permission if not available
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
                    break;
                case PackageManager.PERMISSION_GRANTED:
                    // start discovery of permission is granted
                    startDiscovery();
                    break;
            }
        } else {
            // start discovery of permission request is not needed
            startDiscovery();
        }

        return fragView;
    }

    public void onResume() {
        super.onResume();

        // resize the fragment dialog
        Window window = getDialog().getWindow();
        Point size = new Point();

        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);

        int width = size.x;
        int height = size.y;

        window.setLayout((int) (width * 0.80), (int) (height * 0.6));
        window.setGravity(Gravity.CENTER);
    }

    /**
     * @param requestCode check for request access location
     * @param permissions
     * @param grantResults check if the usr accept the location permission, if usr accepted, discovery will be started
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startDiscovery();
                } else {
                    // is user declined the permission, show a message that the permission is needed
                    Toast.makeText(requireActivity(), R.string.enable_location_permt_warning, LENGTH_SHORT).show();
                    break;
                }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // if fragmed is removed from screen, cancel discovery and unregister the receiver
    @Override
    public void onDetach() {
        super.onDetach();
        requireActivity().unregisterReceiver(mReceiver);
        mBluetoothAdapter.cancelDiscovery();
    }

    // start bluetooth discovery and if discovery is running cancel it
    private void startDiscovery() {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        mBluetoothAdapter.startDiscovery();
    }

    // Handel list item click by canceling discovery and notify observer
    private void onItemClick(View view, int position) {
        if (mCallback != null) mCallback.onDeviceSelected(mDiscoveredDevicesList.get(position));
        mBluetoothAdapter.cancelDiscovery();
        dismiss();
    }

    // Reciever for discovery actions
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // in case of device found, add it to the list if not added before
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
                // if discovery finished, hide progress bar
                mScanInprogressView.setVisibility(View.GONE);
                // if no devices found, show try again button
                if (mDiscoveredDevicesList.size() == 0) {
                    mNoDevicesFoundView.setVisibility(View.VISIBLE);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                // show progress bar on discovery started
                mScanInprogressView.setVisibility(View.VISIBLE);
                mNoDevicesFoundView.setVisibility(View.GONE);
            }
        }
    };
}
