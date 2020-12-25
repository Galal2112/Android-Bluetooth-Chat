package com.example.blue2;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BluetoothDevicesAdapter extends RecyclerView.Adapter<BluetoothDevicesAdapter.ViewHolder> {

    private List<BluetoothDevice> mBlutoothDevices;

    public BluetoothDevicesAdapter(List<BluetoothDevice> bluetoothDevices) {
        this.mBlutoothDevices = bluetoothDevices;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bluetooth_device, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindItem(mBlutoothDevices.get(position));
    }

    @Override
    public int getItemCount() {
        return mBlutoothDevices.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView shortcutTextView;
        private final TextView nameTextView;

        public ViewHolder(View view) {
            super(view);
            nameTextView = view.findViewById(R.id.tv_name);
            shortcutTextView = view.findViewById(R.id.tv_shortcut);
        }

        void bindItem(BluetoothDevice device) {
            nameTextView.setText(device.getName());
            if (device.getName() != null) {
                shortcutTextView.setText(String.valueOf(device.getName().charAt(0)));
            } else {
                shortcutTextView.setText("N");
            }
        }
    }
}
