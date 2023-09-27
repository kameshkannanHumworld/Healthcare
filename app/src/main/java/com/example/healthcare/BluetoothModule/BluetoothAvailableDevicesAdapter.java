package com.example.healthcare.BluetoothModule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.MedicationsModule.ViewMedications.ViewMedicationData;
import com.example.healthcare.R;

import java.util.List;

public class BluetoothAvailableDevicesAdapter extends RecyclerView.Adapter<BluetoothAvailableDevicesAdapter.ViewHolder>{
    private List<BluetoothDeviceModalClass> deviceList;
    Context context;

    public BluetoothAvailableDevicesAdapter(List<BluetoothDeviceModalClass> deviceList) {
        this.deviceList = deviceList;
    }


    @NonNull
    @Override
    public BluetoothAvailableDevicesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bluetooth_device, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BluetoothAvailableDevicesAdapter.ViewHolder holder, int position) {
        BluetoothDeviceModalClass device = deviceList.get(position);
        holder.deviceName.setText(device.name);
        holder.deviceAddress.setText(device.address);
    }

    @Override
    public int getItemCount() {
         return deviceList == null ? 0 : deviceList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView deviceName;
        TextView deviceAddress;

        public ViewHolder(View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.bluetoothDeviceName);
            deviceAddress = itemView.findViewById(R.id.bluetoothDeviceConnectionStatus);
        }
    }
}


