package com.example.blue2;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PairedDevicesAdapter extends RecyclerView.Adapter<PairedDevicesAdapter.ViewHolder> {

    private List<BluetoothDevice> mPairedDevices;

    public PairedDevicesAdapter(List<BluetoothDevice> pairedDevices) {
        this.mPairedDevices = pairedDevices;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paired_device, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindItem(mPairedDevices.get(position));
    }

    @Override
    public int getItemCount() {
        return mPairedDevices.size();
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
            shortcutTextView.setText(String.valueOf(device.getName().charAt(0)));
        }
    }
}
