package com.example.blue2.mvvm.view.adapter;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.blue2.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * the data source for the Bluetooth devices recycler View
 */
public class BluetoothDevicesAdapter extends RecyclerView.Adapter<BluetoothDevicesAdapter.ViewHolder> {

    // devices list
    private List<BluetoothDevice> mBlutoothDevices;

    public BluetoothDevicesAdapter(List<BluetoothDevice> bluetoothDevices) {
        this.mBlutoothDevices = bluetoothDevices;
    }

    // create cell view holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bluetooth_device, parent, false);
        return new ViewHolder(view);
    }

    // bind data for device appearing on the screen
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindItem(mBlutoothDevices.get(position));
    }

    @Override
    public int getItemCount() {
        return mBlutoothDevices.size();
    }

    /**
     * Bluetooth device ViewHolder.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView shortcutTextView;
        private final TextView nameTextView;

        public ViewHolder(View view) {
            super(view);
            nameTextView = view.findViewById(R.id.tv_name);
            shortcutTextView = view.findViewById(R.id.tv_shortcut);
        }

        void bindItem(BluetoothDevice device) {
            // display device name
            nameTextView.setText(device.getName());
            // display first character of device name as image
            if (device.getName() != null) {
                shortcutTextView.setText(String.valueOf(device.getName().charAt(0)));
            } else {
                shortcutTextView.setText("N");
            }
        }
    }
}
